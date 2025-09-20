package ModeloDAO;

import Modelo.Conexion;  
import Modelo.Cliente;
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

    // Método: Buscar cliente por ID
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
        String sql = "INSERT INTO Cliente (idUsuario, nombre, dni, telefono, fechaRegistro) VALUES (?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setDate(5, c.getFechaRegistro());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método: Editar cliente
    public boolean editarCliente(Cliente c) {
        String sql = "UPDATE Cliente SET idUsuario=?, nombre=?, dni=?, telefono=?, fechaRegistro=? WHERE idCliente=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, c.getIdUsuario());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setDate(5, c.getFechaRegistro());
            ps.setInt(6, c.getIdCliente());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método: Eliminar cliente
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
                c.setDni(rs.getString("dni"));
                c.setTelefono(rs.getString("telefono"));
                c.setFechaRegistro(rs.getDate("fechaRegistro"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
}
