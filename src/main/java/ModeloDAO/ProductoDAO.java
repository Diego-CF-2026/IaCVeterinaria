package ModeloDAO;

import Modelo.Producto;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private final Connection con;

    public ProductoDAO(Connection con) {
        this.con = con;
    }

    // Lista todos los productos activos (estado = 1)
    public List<Producto> listarTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, estado, idProveedor, imagen, fechaRegistro FROM producto WHERE estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    // Lista los productos inactivos (estado = 0)
    public List<Producto> listarInactivos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, estado, idProveedor, imagen, fechaRegistro FROM producto WHERE estado = 0";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    // Busca productos por nombre y según su estado (activo o inactivo)
    public List<Producto> buscarPorNombre(String nombre, boolean inactivos) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, estado, idProveedor, imagen, fechaRegistro FROM producto WHERE nombreProducto LIKE ? AND estado = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ps.setInt(2, inactivos ? 0 : 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    // Lista productos por proveedor (solo los activos)
    public List<Producto> listarPorProveedor(int idProveedor) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, estado, idProveedor, imagen, fechaRegistro FROM producto WHERE idProveedor = ? AND estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productos;
    }

    // Obtiene un producto por su ID
    public Producto obtenerPorId(int id) {
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, estado, idProveedor, imagen, fechaRegistro FROM producto WHERE idProducto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Agrega un nuevo producto con validaciones básicas
    public boolean agregar(Producto producto) {       
        // Validar precio (no nulo ni negativo)
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            System.err.println("Error: El precio no puede ser nulo o negativo.");
            return false;
        }

        // Validar unidad de medida (solo letras)
        if (producto.getUnidadMedida() == null || !producto.getUnidadMedida().matches("^[a-zA-Z]+$")) {
            System.err.println("Error: La unidad de medida debe contener solo letras.");
            return false;
        }

        // Validar nombre del producto (no vacío)
        if (producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()) {
            System.err.println("Error: El nombre del producto no puede estar vacío.");
            return false;
        }

        String sql = "INSERT INTO producto (nombreProducto, descripcion, precio, stock, unidadMedida, estado, fechaRegistro, idProveedor, imagen) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getUnidadMedida());
            ps.setInt(6, producto.getEstado());
            ps.setInt(7, producto.getIdProveedor());
            ps.setString(8, producto.getImagen());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al agregar producto en la base de datos.");
            e.printStackTrace();
        }

        return false;
    }

    // Actualiza los datos de un producto existente
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE producto SET nombreProducto=?, descripcion=?, precio=?, stock=?, unidadMedida=?, estado=?, idProveedor=?, imagen=? WHERE idProducto=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setBigDecimal(3, producto.getPrecio());
            ps.setInt(4, producto.getStock());
            ps.setString(5, producto.getUnidadMedida());
            ps.setInt(6, producto.getEstado());
            ps.setInt(7, producto.getIdProveedor());
            ps.setString(8, producto.getImagen());
            ps.setInt(9, producto.getIdProducto());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminación lógica (cambia el estado a 0)
    public boolean eliminar(int id) {
        String sql = "UPDATE producto SET estado = 0 WHERE idProducto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminación física (borra el registro definitivamente)
    public boolean eliminarDefinitivo(int id) {
        String sql = "DELETE FROM producto WHERE idProducto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Mapea los datos del ResultSet a un objeto Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("idProducto"));
        p.setNombreProducto(rs.getString("nombreProducto"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setStock(rs.getInt("stock"));
        p.setUnidadMedida(rs.getString("unidadMedida"));
        p.setEstado(rs.getInt("estado"));
        p.setIdProveedor(rs.getInt("idProveedor"));
        p.setImagen(rs.getString("imagen"));
        p.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
        return p;
    }
}
