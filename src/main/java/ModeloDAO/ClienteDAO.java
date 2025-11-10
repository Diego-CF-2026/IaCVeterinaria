package ModeloDAO;

import Modelo.Conexion;  
import Modelo.Cliente;
import Modelo.Tratamiento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Método: Listar todos los clientes
    public List<Cliente> listarCliente() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Cliente";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
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

    // Método: Buscar cliente por ID de cliente
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

    // Método: Agregar cliente
    public boolean agregarCliente(Cliente c) {
        String sql = "INSERT INTO Cliente (idUsuario, nombre, apellido, dni, telefono, fechaRegistro) VALUES (?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3,c.getApellido());
            ps.setString(4, c.getDni());
            ps.setString(5, c.getTelefono());
            ps.setDate(6, c.getFechaRegistro());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método: Editar cliente por idCliente
    public boolean editarCliente(Cliente c) {
        String sql = "UPDATE Cliente SET idUsuario=?, nombre=?, apellido=?, dni=?, telefono=?, fechaRegistro=? WHERE idCliente=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3,c.getApellido());
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

    // 🔹 Nuevo: Editar cliente por idUsuario (más útil para MiPerfil.jsp)
    public boolean editarClientePorUsuario(Cliente c) {
        String sql = "UPDATE Cliente SET nombre=?, apellido=?, dni=?, telefono=? WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, c.getNombre());
            ps.setString(2,c.getApellido());
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

    // Método: Eliminar cliente por idCliente
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

    // 🔹 Nuevo: Eliminar cliente por idUsuario
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
    
    // Buscar cliente a partir de su idUsuario
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
    
    // ----------------------------------------------------
    // 🟢 NUEVO MÉTODO REQUERIDO: Buscar cliente por DNI
    // ----------------------------------------------------
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
                // Importante: Debemos obtener todos los datos, especialmente el idCliente.
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
             // Es buena práctica cerrar recursos aquí (rs, ps, con)
        }
        return c;
    }
    
        public List<Cliente> buscarClientes(String termino) {
        List<Cliente> lista = new ArrayList<>();
        // Consulta SQL con LIKE para búsqueda flexible
        String sql = "SELECT * FROM Cliente WHERE nombre LIKE ? OR apellido LIKE ? OR dni LIKE ?";
        String patron = "%" + termino + "%"; 
        
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

    public List<Modelo.Cliente> listarClientesParaDropdown() {
        List<Modelo.Cliente> lista = new ArrayList<>();
        // Las columnas son 'nombre' y 'apellido' según tu esquema
        String sql = "SELECT idCliente, nombre, apellido FROM cliente ORDER BY nombre"; 

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) { // <-- rs.next() devuelve un boolean, es correcto
                Modelo.Cliente cliente = new Modelo.Cliente();
                cliente.setIdCliente(rs.getInt("idCliente")); // <-- Esto es un int, pero se usa para setInt
                cliente.setNombre(rs.getString("nombre")); // <-- Esto es un String, se usa para setString
                cliente.setApellido(rs.getString("apellido")); // <-- Esto es un String, se usa para setString
                lista.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al listar clientes para dropdown: " + e.getMessage());
            e.printStackTrace(); 
        }
        System.out.println("DEBUG: Clientes cargados para Dropdown: " + lista.size() + " registros.");
        return lista;
    }
}
