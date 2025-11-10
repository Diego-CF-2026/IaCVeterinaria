package ModeloDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.ContactoProveedor;

public class ContactoProveedorDAO {

    private Connection con;

    // Constructor que recibe la conexión activa con la base de datos
    public ContactoProveedorDAO(Connection con) {
        this.con = con;
    }

    // Método para listar todos los contactos asociados a un proveedor
    public List<ContactoProveedor> listarPorProveedor(int idProveedor) {
        List<ContactoProveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM contactoproveedor WHERE idProveedor = ?";

        // Se utiliza PreparedStatement para evitar inyección SQL
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);

            // Se ejecuta la consulta y se recorren los resultados
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContactoProveedor c = new ContactoProveedor();

                    // Se asignan los valores de cada columna al objeto ContactoProveedor
                    c.setIdContacto(rs.getInt("idContacto"));
                    c.setIdProveedor(rs.getInt("idProveedor"));
                    c.setNombreContacto(rs.getString("nombreContacto"));
                    c.setCargo(rs.getString("cargo"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreoContacto(rs.getString("correoContacto"));

                    lista.add(c); // Se agrega el contacto a la lista
                }
            }
        } catch (Exception e) {
            // En caso de error se muestra el mensaje en consola
            System.err.println("Error al listar contactos del proveedor: " + e.getMessage());
            e.printStackTrace();
        }

        // Se devuelve la lista con todos los contactos encontrados
        return lista;
    }

    // Método para agregar un nuevo contacto de proveedor
    public boolean agregarContacto(ContactoProveedor c) {
        String sql = "INSERT INTO contactoproveedor (idProveedor, nombreContacto, cargo, telefono, correoContacto) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Se establecen los valores que se insertarán
            ps.setInt(1, c.getIdProveedor());
            ps.setString(2, c.getNombreContacto());
            ps.setString(3, c.getCargo());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreoContacto());

            // Se ejecuta la sentencia y se devuelve true si fue exitosa
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            // Si ocurre algún error durante la inserción
            System.err.println("Error al agregar contacto: " + e.getMessage());
            e.printStackTrace();
        }

        // Se retorna false si no se pudo insertar
        return false;
    }

    // Método para actualizar los datos de un contacto existente
    public boolean actualizarContacto(ContactoProveedor c) {
        String sql = "UPDATE contactoproveedor SET nombreContacto = ?, cargo = ?, telefono = ?, correoContacto = ? WHERE idContacto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Se reemplazan los valores antiguos por los nuevos
            ps.setString(1, c.getNombreContacto());
            ps.setString(2, c.getCargo());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getCorreoContacto());
            ps.setInt(5, c.getIdContacto());

            // Si la actualización afectó 1 fila, se considera exitosa
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            // Manejo de error en caso de que falle la actualización
            System.err.println("Error al actualizar contacto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Método para eliminar un contacto según su ID
    public boolean eliminarContacto(int idContacto) {
        String sql = "DELETE FROM contactoproveedor WHERE idContacto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Se pasa el ID del contacto que se desea eliminar
            ps.setInt(1, idContacto);
            // Si la eliminación afectó 1 registro, fue exitosa
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            // Se muestra el error en consola si ocurre un problema
            System.err.println("Error al eliminar contacto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
