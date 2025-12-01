/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * DAO (Data Access Object) para manejar todas las operaciones relacionadas con la tabla `carrito`
 * y sus relaciones (detallecarrito, producto, cliente).
 */

/*
 * Aquí se implementan métodos CRUD y de lógica de negocio como:
 *  - Agregar productos al carrito
 *  - Obtener carrito activo o cerrado
 *  - Confirmar compra
 */

/*
 *  - Actualizar cantidades, eliminar productos
 *  - Cambiar estado de entrega (PROCESO / ENTREGADO)
 */

package ModeloDAO;

import Modelo.Carrito;
import Modelo.Cliente;
import Modelo.DetalleCarrito;
import Modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kristhor
 */
public class CarritoDAO {
    private Connection con;

    // Constructor que recibe la conexión activa con la base de datos
    public CarritoDAO(Connection con) {
        this.con = con;
    }

    // Verifica si el cliente ya tiene un carrito abierto
    private int obtenerCarritoActivo(int idCliente) throws SQLException {
        String sql = "SELECT idCarrito FROM carrito WHERE idCliente=? AND estado='ABIERTO'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idCarrito");
            }
        }
        return -1;
    }

    // 🔹 Agregar un producto al carrito de un cliente
    public boolean agregarProducto(int idCliente, int idProducto, int cantidad) {
        try {
            int idCarrito = obtenerCarritoActivo(idCliente);

            if (idCarrito == -1) {
                // crear nuevo carrito
                String sqlCarrito = "INSERT INTO carrito(idCliente, total, estado, fecha) VALUES (?,0,'ABIERTO',NOW())";
                try (PreparedStatement ps = con.prepareStatement(sqlCarrito, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        idCarrito = rs.getInt(1);
                    }
                }   
            }

            // Agregar detalle
            String sqlDetalle = "INSERT INTO detallecarrito(idCarrito, idProducto, cantidadProducto, fechaAgregado) VALUES (?,?,?,NOW())";
            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setInt(1, idCarrito);
                ps.setInt(2, idProducto);
                ps.setInt(3, cantidad);
                ps.executeUpdate();
            }

            // Se recalcula el total del carrito sumando los precios
            String sqlTotal = "UPDATE carrito c SET c.total = (SELECT SUM(dc.cantidadProducto * p.precio) " +
                    "FROM detallecarrito dc INNER JOIN producto p ON dc.idProducto=p.idProducto WHERE dc.idCarrito=?) " +
                    "WHERE c.idCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sqlTotal)) {
                ps.setInt(1, idCarrito);
                ps.setInt(2, idCarrito);
                ps.executeUpdate();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    // 🔹 Obtener carrito actual del cliente
    public Carrito obtenerCarrito(int idCliente) {
        Carrito carrito = null;
        try {
            String sql = "SELECT * FROM carrito WHERE idCliente=? AND estado='ABIERTO'";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    carrito = new Carrito();
                    carrito.setIdCarrito(rs.getInt("idCarrito"));
                    carrito.setIdCliente(rs.getInt("idCliente"));
                    carrito.setTotal(rs.getBigDecimal("total"));
                    carrito.setEstado(rs.getString("estado"));
                    carrito.setFecha(rs.getTimestamp("fecha"));
                    carrito.setIdPago(rs.getInt("idPago"));

                    // Cargar detalles
                    carrito.setDetalles(obtenerDetalles(carrito.getIdCarrito()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carrito;
    }


    // 🔹 Obtener detalles del carrito
    private List<DetalleCarrito> obtenerDetalles(int idCarrito) {
        List<DetalleCarrito> detalles = new ArrayList<>();
        try {
            String sql = "SELECT dc.*, p.nombreProducto, p.precio, p.descripcion, p.imagen " +
                         "FROM detallecarrito dc INNER JOIN producto p ON dc.idProducto=p.idProducto " +
                         "WHERE dc.idCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCarrito);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    DetalleCarrito d = new DetalleCarrito();
                    d.setIdDetalleCarrito(rs.getInt("idDetalleCarrito"));
                    d.setIdCarrito(rs.getInt("idCarrito"));
                    d.setIdProducto(rs.getInt("idProducto"));
                    d.setCantidadProducto(rs.getInt("cantidadProducto"));
                    d.setFechaAgregado(rs.getTimestamp("fechaAgregado"));

                    // Producto relacionado
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setNombreProducto(rs.getString("nombreProducto")); // 🔹 corregido
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setImagen(rs.getString("imagen"));
                    d.setProducto(p);

                    detalles.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detalles;
    }
    

    // Obtener historial de compras del cliente (ABIERTO)
    public List<Carrito> obtenerHistorial(int idCliente) {
        List<Carrito> historial = new ArrayList<>();
        try  {
            String sql = "SELECT * FROM carrito WHERE idCliente=? AND estado='ABIERTO' ORDER BY fecha DESC";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCliente);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Carrito carrito = new Carrito();
                    carrito.setIdCarrito(rs.getInt("idCarrito"));
                    carrito.setIdCliente(rs.getInt("idCliente"));
                    carrito.setTotal(rs.getBigDecimal("total"));
                    carrito.setEstado(rs.getString("estado"));
                    carrito.setFecha(rs.getTimestamp("fecha"));
                    carrito.setIdPago(rs.getInt("idPago"));

                    // 🔹 cargar los detalles del carrito
                    carrito.setDetalles(obtenerDetalles(carrito.getIdCarrito()));

                    historial.add(carrito);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historial;
    }
    
    public boolean confirmarCompra(int idCarrito, int idPago) {
        try {
            String sql = "UPDATE carrito SET estado='CERRADO', idPago=? WHERE idCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idPago);
                ps.setInt(2, idCarrito);
                ps.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Eliminar producto del carrito por idDetalleCarrito
    public boolean eliminarProducto(int idDetalleCarrito, int idCarrito) {
        try {
            // eliminar detalle
            String sql = "DELETE FROM detallecarrito WHERE idDetalleCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idDetalleCarrito);
                ps.executeUpdate();
            }

            // actualizar total
            String sqlTotal = "UPDATE carrito c SET c.total = (SELECT IFNULL(SUM(dc.cantidadProducto * p.precio),0) " +
                              "FROM detallecarrito dc INNER JOIN producto p ON dc.idProducto=p.idProducto WHERE dc.idCarrito=?) " +
                              "WHERE c.idCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sqlTotal)) {
                ps.setInt(1, idCarrito);
                ps.setInt(2, idCarrito);
                ps.executeUpdate();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean actualizarCantidad(int idDetalleCarrito, int idCarrito, String accion) {
        try {
            // Sumar o restar según la acción
            String sqlUpdate = "";
            if ("sumar".equals(accion)) {
                sqlUpdate = "UPDATE detallecarrito SET cantidadProducto = cantidadProducto + 1 WHERE idDetalleCarrito=?";
            } else if ("restar".equals(accion)) {
                sqlUpdate = "UPDATE detallecarrito SET cantidadProducto = GREATEST(cantidadProducto - 1, 1) WHERE idDetalleCarrito=?";
            }

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, idDetalleCarrito);
                ps.executeUpdate();
            }

            // 🔹 Recalcular total
            String sqlTotal = "UPDATE carrito c SET c.total = (SELECT IFNULL(SUM(dc.cantidadProducto * p.precio),0) " +
                              "FROM detallecarrito dc INNER JOIN producto p ON dc.idProducto=p.idProducto WHERE dc.idCarrito=?) " +
                              "WHERE c.idCarrito=?";
            try (PreparedStatement ps = con.prepareStatement(sqlTotal)) {
                ps.setInt(1, idCarrito);
                ps.setInt(2, idCarrito);
                ps.executeUpdate();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Obtener lista de carritos CERRADOS (para el recepcionista)
    public List<Carrito> obtenerCarritosEnProceso() {
        List<Carrito> lista = new ArrayList<>();
        try {
            String sql = "SELECT c.*, cl.nombre, cl.apellido " +
                         "FROM carrito c " +
                         "INNER JOIN cliente cl ON c.idCliente = cl.idCliente " +
                         "WHERE c.estado = 'CERRADO' AND c.estadoEntrega = 'EN PROCESO' " +
                         "ORDER BY c.fecha DESC";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Carrito c = new Carrito();
                    c.setIdCarrito(rs.getInt("idCarrito"));
                    c.setIdCliente(rs.getInt("idCliente"));
                    c.setTotal(rs.getBigDecimal("total"));
                    c.setEstado(rs.getString("estado"));
                    c.setFecha(rs.getTimestamp("fecha"));
                    c.setEstadoEntrega(rs.getString("estadoEntrega"));

                    // Cliente
                    Cliente cliente = new Cliente();
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    c.setCliente(cliente);

                    // Detalles
                    c.setDetalles(obtenerDetalles(c.getIdCarrito()));

                    lista.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // Actualizar estado de entrega (PROCESO / ENTREGADO)
    public boolean actualizarEstadoEntrega(int idCarrito, String nuevoEstado) {
        String sql = "UPDATE Carrito SET estadoEntrega = ? WHERE idCarrito = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCarrito);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 🔹 Obtener solo los carritos con estadoEntrega = 'ENTREGADO'
    public List<Carrito> obtenerCarritosEntregados() {
        List<Carrito> lista = new ArrayList<>();
        try {
            String sql = "SELECT c.*, cl.nombre, cl.apellido " +
                         "FROM carrito c " +
                         "INNER JOIN cliente cl ON c.idCliente = cl.idCliente " +
                         "WHERE c.estado = 'CERRADO' AND c.estadoEntrega = 'ENTREGADO' " +
                         "ORDER BY c.fecha DESC";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Carrito c = new Carrito();
                    c.setIdCarrito(rs.getInt("idCarrito"));
                    c.setIdCliente(rs.getInt("idCliente"));
                    c.setTotal(rs.getBigDecimal("total"));
                    c.setEstado(rs.getString("estado"));
                    c.setEstadoEntrega(rs.getString("estadoEntrega"));
                    c.setFecha(rs.getTimestamp("fecha"));

                    // === Datos del cliente ===
                    Cliente cliente = new Cliente();
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    c.setCliente(cliente);

                    lista.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // ... (Tus métodos existentes: obtenerCarritosEntregados, etc.)

    // =========================================================
    // ⬇️ FUNCIONES PARA ADMINISTRACIÓN (Reporte de Ventas) ⬇️
    // =========================================================

    /**
     * Obtiene la lista de carritos (ventas) con estado CERRADO y ENTREGADO,
     * filtrados por mes y año, incluyendo los datos del Cliente.
     * * @param mes El mes para filtrar (1-12).
     * @param anio El año para filtrar (YYYY).
     * @return Lista de objetos Carrito (ventas completadas).
     */
    public List<Carrito> adminObtenerVentasPorMesAnio(int mes, int anio) {
        List<Carrito> ventas = new ArrayList<>();
        
        // 💡 Consulta SQL: Filtra por estado CERRADO/ENTREGADO y por Mes/Año. 
        // Se une con Cliente para obtener el nombre completo.
        String sql = "SELECT c.*, cl.nombre, cl.apellido, cl.dni " +
                     "FROM carrito c " +
                     "INNER JOIN cliente cl ON c.idCliente = cl.idCliente " +
                     "WHERE c.estado = 'CERRADO' AND c.estadoEntrega = 'ENTREGADO' " +
                     // Filtros de fecha (asumiendo MySQL/PostgreSQL/SQL Server compatible con YEAR/MONTH o similar)
                     "AND EXTRACT(YEAR FROM c.fecha) = ? AND EXTRACT(MONTH FROM c.fecha) = ? " +
                     "ORDER BY c.fecha DESC";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            
            // 🔹 Viculación de los parámetros de fecha
            ps.setInt(1, anio);
            ps.setInt(2, mes);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Carrito c = new Carrito();
                    
                    // Mapeo de Carrito (venta)
                    c.setIdCarrito(rs.getInt("idCarrito"));
                    c.setIdCliente(rs.getInt("idCliente"));
                    c.setTotal(rs.getBigDecimal("total"));
                    c.setEstado(rs.getString("estado"));
                    c.setEstadoEntrega(rs.getString("estadoEntrega"));
                    c.setFecha(rs.getTimestamp("fecha"));
                    c.setIdPago(rs.getInt("idPago"));
                    
                    // Mapeo de Cliente (datos requeridos para el reporte)
                    Cliente cliente = new Cliente();
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    // Asumiendo que la clase Cliente tiene el método setDni(String)
                    // Puedes obtener el DNI si lo necesitas, si no, omite esta línea.
                    // cliente.setDni(rs.getString("dni")); 
                    c.setCliente(cliente);
                    
                    // Nota: No cargamos los detalles (List<DetalleCarrito>) aquí
                    // ya que para un reporte tabular sencillo, solo se necesita el resumen (total y cliente).
                    
                    ventas.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en adminObtenerVentasPorMesAnio: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }
}

