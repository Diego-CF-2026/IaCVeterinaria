/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ModeloDAO;


import Modelo.Carrito;
import Modelo.DetalleCarrito;
import Modelo.Producto;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kristhor
 */
public class CarritoDAO {
    private Connection con;

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

            // Actualizar total
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
    
    
    
    // ==============================
    // 🔹 Obtener carrito actual del cliente
    // ==============================
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

    // ==============================
    // 🔹 Obtener detalles del carrito
    // ==============================
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
}
