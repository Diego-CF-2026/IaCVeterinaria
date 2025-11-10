package ModeloDAO;

import Modelo.Conexion;  
import Modelo.Cliente;
import Modelo.Tratamiento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Clase: ClienteDAO
// Descripción: Contiene los métodos necesarios para realizar operaciones CRUD 
// sobre la tabla 'Cliente' en la base de datos. 
// Implementa consultas, inserciones, actualizaciones y eliminaciones de clientes.
public class ClienteDAO {
    
    Connection con;           // Conexión a la base de datos
    PreparedStatement ps;     // Sentencia SQL preparada para ejecución segura
    ResultSet rs;             // Resultado de las consultas SQL

    // ------------------------------------------------------------
    // Método: Listar todos los clientes
    // Retorna una lista con todos los registros encontrados en la tabla Cliente.
    // ------------------------------------------------------------
    public List<Cliente> listarCliente() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try {
            con = Conexion.getConnection(); // Obtener conexión activa
            ps = con.prepareStatement(sql); // Preparar la consulta SQL
            rs = ps.executeQuery();         // Ejecutar la consulta

            // Recorrer los resultados y construir objetos Cliente
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("idCliente"));
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
                lista.add(c); // Agregar cliente a la lista
            }
        } catch (Exception e) {
            // Mostrar error si la consulta falla
            e.printStackTrace();
        }
        return lista;
    }

    // ------------------------------------------------------------
    // Método: Buscar cliente por su ID de cliente
    // Retorna un objeto Cliente si se encuentra el registro con el ID indicado.
    // ------------------------------------------------------------
    public Cliente buscarIdCliente(int id) {
        Cliente c = null;
        String sql = "SELECT * FROM Cliente WHERE idCliente = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = new Cliente();
                // Asignar los valores del registro encontrado al objeto Cliente
                c.setIdCliente(rs.getInt("idCliente"));
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    // ------------------------------------------------------------
    // Método: Agregar un nuevo cliente a la base de datos
    // Devuelve true si la inserción fue exitosa.
    // ------------------------------------------------------------
    public boolean agregarCliente(Cliente c) {
        String sql = "INSERT INTO Cliente (idUsuario, nombre, apellido, dni, telefono, fechaRegistro) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            // Asignar parámetros según los valores del objeto Cliente
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getDni());
            ps.setString(5, c.getTelefono());
            ps.setDate(6, c.getFechaRegistro());
            ps.executeUpdate(); // Ejecutar la inserción
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------
    // Método: Editar cliente a partir de su idCliente
    // Actualiza todos los campos del cliente existente.
    // ------------------------------------------------------------
    public boolean editarCliente(Cliente c) {
        String sql = "UPDATE Cliente SET idUsuario=?, nombre=?, apellido=?, dni=?, telefono=?, fechaRegistro=? WHERE idCliente=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getApellido());
            ps.setString(4, c.getDni());
            ps.setString(5, c.getTelefono());
            ps.setDate(6, c.getFechaRegistro());
            ps.setInt(7, c.getIdCliente());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------
    // Método: Editar cliente según su idUsuario
    // Se usa especialmente para actualizar datos personales desde MiPerfil.jsp
    // ------------------------------------------------------------
    public boolean editarClientePorUsuario(Cliente c) {
        String sql = "UPDATE Cliente SET nombre=?, apellido=?, dni=?, telefono=? WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setInt(5, c.getIdUsuario());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------
    // Método: Eliminar cliente por idCliente
    // Retorna true si la eliminación se realizó correctamente.
    // ------------------------------------------------------------
    public boolean eliminarCliente(int id) {
        String sql = "DELETE FROM Cliente WHERE idCliente=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ------------------------------------------------------------
    // Método: Eliminar cliente por idUsuario
    // Útil cuando se elimina una cuenta de usuario completa.
    // ------------------------------------------------------------
    public boolean eliminarClientePorUsuario(int idUsuario) {
        String sql = "DELETE FROM Cliente WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ------------------------------------------------------------
    // Método: Buscar cliente por su idUsuario
    // Retorna los datos personales de un cliente asociado a un usuario.
    // ------------------------------------------------------------
    public Cliente buscarPorIdUsuario(int idUsuario) {
        Cliente c = null;
        String sql = "SELECT * FROM Cliente WHERE idUsuario = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = new Cliente();
                c.setIdCliente(rs.getInt("idCliente"));
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
    
    // ------------------------------------------------------------
    // Método: Buscar cliente por DNI
    // Permite encontrar un cliente mediante su número de documento.
    // ------------------------------------------------------------
    public Cliente buscarClientePorDni(String dni) {
        Cliente c = null;
        String sql = "SELECT * FROM Cliente WHERE dni = ?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                c = new Cliente();
                // Asignar los datos obtenidos del registro
                c.setIdCliente(rs.getInt("idCliente")); 
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
            }
        } catch (Exception e) {
            System.err.println("Error en ClienteDAO.buscarClientePorDni: " + e.getMessage());
            e.printStackTrace();
        } finally {
        }
        return c;
    }
    
    // ------------------------------------------------------------
    // Método: Buscar clientes por coincidencia de nombre, apellido o DNI
    // Se usa para funciones de búsqueda general o filtrado dinámico.
    // ------------------------------------------------------------
    public List<Cliente> buscarClientes(String termino) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente WHERE nombre LIKE ? OR apellido LIKE ? OR dni LIKE ?";
        String patron = "%" + termino + "%"; // Patrón para búsqueda parcial
        
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("idCliente"));
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ------------------------------------------------------------
    // Método: Listar clientes para mostrar en un menú desplegable (Dropdown)
    // Solo devuelve los campos básicos: idCliente, nombre y apellido.
    // ------------------------------------------------------------
    public List<Modelo.Cliente> listarClientesParaDropdown() {
        List<Modelo.Cliente> lista = new ArrayList<>();
        String sql = "SELECT idCliente, nombre, apellido FROM cliente ORDER BY nombre"; 

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Modelo.Cliente cliente = new Modelo.Cliente();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                lista.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al listar clientes para dropdown: " + e.getMessage());
            e.printStackTrace(); 
        }
        
        // Mensaje de depuración para verificar cantidad de registros cargados
        System.out.println("DEBUG: Clientes cargados para Dropdown: " + lista.size() + " registros.");
        return lista;
    }
}
