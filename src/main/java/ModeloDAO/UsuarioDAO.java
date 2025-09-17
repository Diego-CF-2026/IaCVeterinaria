package ModeloDAO;

import Modelo.Conexion;
import Modelo.Usuario;
import Modelo.Cliente;
import Modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Retorna "ok" si todo salió bien o "dni"/"correo"/"telefono"/"error"
    public String insertarCliente(Usuario usuario, Cliente cliente) {
        String sqlUsuario = "INSERT INTO Usuario(idRol, correo, contra, intentos, Estado) VALUES (?, ?, ?, ?, ?)";
        String sqlCliente = "INSERT INTO Cliente(idUsuario, nombre, apellido, dni, telefono) VALUES (?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // Insertar usuario
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 3); // Rol cliente
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getContra());
            ps.setInt(4, usuario.getIntentos());
            ps.setBoolean(5, usuario.isEstado());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int idUsuario = 0;
            if (rs.next()) {
                idUsuario = rs.getInt(1);
            }
            rs.close();
            ps.close();

            // Insertar cliente
            ps = con.prepareStatement(sqlCliente);
            ps.setInt(1, idUsuario);
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellido());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.executeUpdate();
            ps.close();

            con.commit();
            return "ok";
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}

            if (e.getMessage() != null) {
                if (e.getMessage().contains("dni")) return "dni";
                if (e.getMessage().contains("correo")) return "correo";
                if (e.getMessage().contains("telefono")) return "telefono";
            }
            return "error";
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
    
   public Usuario login(String correo, String contra) {
    String sql = "SELECT u.*, r.nombreRol FROM Usuario u " +
                 "INNER JOIN Rol r ON u.idRol = r.idRol " +
                 "WHERE u.correo = ? AND u.contra = ?";
    try {
        con = Conexion.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, correo);
        ps.setString(2, contra);
        rs = ps.executeQuery();
        if (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setIdRol(rs.getInt("idRol"));
            u.setCorreo(rs.getString("correo"));
            u.setContra(rs.getString("contra"));
            u.setIntentos(rs.getInt("intentos"));
            u.setEstado(rs.getBoolean("estado"));
            u.setNombreRol(rs.getString("nombreRol"));
            return u;
        }
    } catch (Exception e) {
        System.out.println("Error login: " + e.getMessage());
    }
    return null;
}


}
