package ModeloDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Proveedor;
import Modelo.ContactoProveedor;
import Modelo.Producto;

public class ProveedorDAO {

    private Connection con;

    public ProveedorDAO(Connection con) {
        this.con = con;
    }

    // Lista los proveedores activos con sus contactos y productos
    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro FROM proveedor WHERE estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = mapearProveedor(rs);
                p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Busca proveedores activos por nombre, dirección, RUC o correo
    public List<Proveedor> buscarPorNombreODireccion(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro "
                   + "FROM proveedor WHERE estado = 1 AND (razonSocial LIKE ? OR direccion LIKE ? OR ruc LIKE ? OR correo LIKE ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String criterio = "%" + filtro + "%";
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ps.setString(3, criterio);
            ps.setString(4, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Busca proveedores inactivos por nombre, dirección, RUC o correo
    public List<Proveedor> buscarPorNombreODireccionInactivos(String filtro) {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro "
                   + "FROM proveedor WHERE estado = 0 AND (razonSocial LIKE ? OR direccion LIKE ? OR ruc LIKE ? OR correo LIKE ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String criterio = "%" + filtro + "%";
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ps.setString(3, criterio);
            ps.setString(4, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Lista proveedores inactivos
    public List<Proveedor> listarInactivos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro FROM proveedor WHERE estado = 0";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Proveedor p = mapearProveedor(rs);
                p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Reactiva un proveedor (estado = 1)
    public boolean reactivar(int id) {
        String sql = "UPDATE proveedor SET estado = 1 WHERE idProveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtiene un proveedor por su ID
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro FROM proveedor WHERE idProveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Obtiene un proveedor por su RUC
    public Proveedor obtenerPorRuc(String ruc) {
        String sql = "SELECT idProveedor, razonSocial, ruc, direccion, correo, estado, fechaRegistro FROM proveedor WHERE ruc = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ruc);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proveedor p = mapearProveedor(rs);
                    p.setContactos(obtenerContactosProveedor(p.getIdProveedor()));
                    p.setProductos(obtenerProductosProveedor(p.getIdProveedor()));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Agrega un nuevo proveedor con validaciones de RUC y correo
    public boolean agregar(Proveedor p) {
        if (p == null) {
            throw new IllegalArgumentException("El proveedor no puede ser null");
        }

        // Validación del RUC (11 dígitos, empieza con 10, 15 o 20)
        if (p.getRuc() == null || !p.getRuc().matches("^(10|15|20)\\d{9}$")) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos y empezar con 10, 15 o 20.");
        }

        // Validación del correo
        if (p.getCorreo() == null || !p.getCorreo().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El correo no es válido");
        }

        String sql = "INSERT INTO proveedor (razonSocial, ruc, direccion, correo, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getRazonSocial());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getDireccion());
            ps.setString(4, p.getCorreo());
            ps.setInt(5, p.getEstado());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualiza los datos de un proveedor
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedor SET razonSocial=?, ruc=?, direccion=?, correo=?, estado=? WHERE idProveedor=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getRazonSocial());
            ps.setString(2, p.getRuc());
            ps.setString(3, p.getDireccion());
            ps.setString(4, p.getCorreo());
            ps.setInt(5, p.getEstado());
            ps.setInt(6, p.getIdProveedor());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Eliminación lógica (estado = 0)
    public boolean eliminar(int id) {
        String sql = "UPDATE proveedor SET estado = 0 WHERE idProveedor = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Mapea un registro de ResultSet a un objeto Proveedor
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setIdProveedor(rs.getInt("idProveedor"));
        p.setRazonSocial(rs.getString("razonSocial"));
        p.setRuc(rs.getString("ruc"));
        p.setDireccion(rs.getString("direccion"));
        p.setCorreo(rs.getString("correo"));
        p.setEstado(rs.getInt("estado"));

        Timestamp timestamp = rs.getTimestamp("fechaRegistro");
        if (timestamp != null) {
            p.setFechaRegistro(timestamp.toLocalDateTime());
        }
        return p;
    }

    // Obtiene los contactos asociados a un proveedor
    private List<ContactoProveedor> obtenerContactosProveedor(int idProveedor) {
        List<ContactoProveedor> contactos = new ArrayList<>();
        String sql = "SELECT idContacto, idProveedor, nombreContacto, cargo, telefono, correoContacto FROM contactoproveedor WHERE idProveedor = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContactoProveedor c = new ContactoProveedor();
                    c.setIdContacto(rs.getInt("idContacto"));
                    c.setIdProveedor(rs.getInt("idProveedor"));
                    c.setNombreContacto(rs.getString("nombreContacto"));
                    c.setCargo(rs.getString("cargo"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreoContacto(rs.getString("correoContacto"));
                    contactos.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contactos;
    }

    // Obtiene los productos activos asociados a un proveedor
    private List<Producto> obtenerProductosProveedor(int idProveedor) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT idProducto, nombreProducto, descripcion, precio, stock, unidadMedida, imagen, estado, idProveedor, fechaRegistro FROM Producto WHERE idProveedor = ? AND estado = 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto prod = new Producto();
                    prod.setIdProducto(rs.getInt("idProducto"));
                    prod.setNombreProducto(rs.getString("nombreProducto"));
                    prod.setDescripcion(rs.getString("descripcion"));
                    prod.setPrecio(rs.getBigDecimal("precio"));
                    prod.setStock(rs.getInt("stock"));
                    prod.setUnidadMedida(rs.getString("unidadMedida"));
                    prod.setImagen(rs.getString("imagen"));
                    prod.setEstado(rs.getInt("estado"));
                    prod.setIdProveedor(rs.getInt("idProveedor"));

                    Timestamp fecha = rs.getTimestamp("fechaRegistro");
                    if (fecha != null) {
                        prod.setFechaRegistro(fecha);
                    }

                    productos.add(prod);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }
}
