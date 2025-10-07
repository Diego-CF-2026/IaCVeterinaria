package ModeloDAO;

import Modelo.Conexion;
import Modelo.Usuario;
import Modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UsuarioDAO {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Métodos existentes (insertarCliente, actualizarUsuario, eliminarUsuario, actualizarCorreo)

    public String insertarCliente(Usuario usuario, Cliente cliente) {
        if (usuario == null || cliente == null) {
            throw new IllegalArgumentException("Usuario y Cliente no pueden ser null");
        }

        // Validación de correo
        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty() || !usuario.getCorreo().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Correo no válido");
        }

        // Validación de contraseña
        if (usuario.getContra() == null || usuario.getContra().isEmpty() || usuario.getContra().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Validación DNI (ejemplo: 8 dígitos)
        if (cliente.getDni() == null || cliente.getDni().isEmpty() || !cliente.getDni().matches("^\\d{8}$")) {
            throw new IllegalArgumentException("DNI no válido, debe tener 8 dígitos");
        }

        // Validación teléfono (ejemplo: 9 dígitos)
        if (cliente.getTelefono() == null || cliente.getTelefono().isEmpty() || !cliente.getTelefono().matches("^9\\d{8}$")) {
            throw new IllegalArgumentException("Teléfono no válido, debe empezar con 9 y tener 9 dígitos");
        }
        
        // Se añade 'tiempo_bloqueo' con NULL por defecto en la inserción
        String sqlUsuario = "INSERT INTO Usuario(idRol, correo, contra, intentos, Estado, tiempo_bloqueo) VALUES (?, ?, ?, ?, ?, NULL)";
        String sqlCliente = "INSERT INTO Cliente(idUsuario, nombre, apellido, dni, telefono) VALUES (?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // Insertar usuario
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 3); // Rol cliente
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getContra());
            ps.setInt(4, usuario.getIntentos()); // Por defecto debe ser 0
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
    
    // -------------------------------------------------------------------------
    // MÉTODOS DE LOGIN Y CONTROL DE INTENTOS
    // -------------------------------------------------------------------------

    /**
     * Obtiene solo los campos necesarios para la lógica de bloqueo antes del login.
     */
    public Usuario obtenerIntentosYBloqueo(String correo) {
        String sql = "SELECT intentos, tiempo_bloqueo FROM usuario WHERE correo = ?";
        Usuario usuario = null;
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setIntentos(rs.getInt("intentos"));
                
                // Lectura del Timestamp
                usuario.setTiempoBloqueo(rs.getTimestamp("tiempo_bloqueo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return usuario;
    }

    /**
     * Implementa la lógica de autenticación y ahora lee tiempo_bloqueo.
     */
    public Usuario login(String correo, String contra) {
        // Validación de correo y contraseña (ajustada para el servlet)
        if (correo == null || correo.isEmpty() || contra == null || contra.length() < 8) {
            return null;
        }
        
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
                
                // Lectura del Timestamp
                u.setTiempoBloqueo(rs.getTimestamp("tiempo_bloqueo")); 
                
                return u;
            }
        } catch (Exception e) {
            System.out.println("Error login: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return null;
    }
    
    /**
     * Incrementa el contador de intentos fallidos.
     * Retorna el nuevo contador para que el Servlet pueda decidir si bloquear.
     */
    public int incrementarIntentos(String correo) {
        String sql = "UPDATE usuario SET intentos = intentos + 1 WHERE correo = ?";
        String sqlSelect = "SELECT intentos FROM usuario WHERE correo = ?";
        int nuevosIntentos = 0;
        
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);
            
            // 1. Ejecutar el UPDATE para incrementar
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.executeUpdate();
            ps.close();
            
            // 2. Obtener el nuevo valor de intentos
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) {
                nuevosIntentos = rs.getInt("intentos");
            }
            
            con.commit();
        } catch (SQLException e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
        return nuevosIntentos;
    }

    /**
     * Bloquea el usuario estableciendo el tiempo_bloqueo a una hora futura.
     */
    public void bloquearUsuario(String correo, int minutosBloqueo) {
        // Cálculo del tiempo futuro: Ahora + minutosBloqueo
        Instant tiempoFuturo = Instant.now().plus(minutosBloqueo, ChronoUnit.MINUTES);
        Timestamp timestampBloqueo = Timestamp.from(tiempoFuturo);
        
        // Actualiza el tiempo de bloqueo
        String sql = "UPDATE usuario SET tiempo_bloqueo = ? WHERE correo = ?"; 
        
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setTimestamp(1, timestampBloqueo); // Guardar el Timestamp
            ps.setString(2, correo);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
    
    /**
     * Reinicia el contador de intentos y el tiempo de bloqueo (tras login exitoso).
     */
    public void reiniciarIntentos(String correo) {
        // Establece intentos en 0 y tiempo_bloqueo en NULL
        String sql = "UPDATE usuario SET intentos = 0, tiempo_bloqueo = NULL WHERE correo = ?";
        
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
    
    // -------------------------------------------------------------------------
    // OTROS MÉTODOS EXISTENTES
    // -------------------------------------------------------------------------
    
    // 🔹 Actualizar perfil (usuario + cliente)
    public boolean actualizarUsuario(Usuario usuario, Cliente cliente) {
        String sqlUsuario = "UPDATE Usuario SET correo=? WHERE idUsuario=?";
        String sqlCliente = "UPDATE Cliente SET nombre=?, apellido=?, dni=?, telefono=? WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // Actualizar usuario
            ps = con.prepareStatement(sqlUsuario);
            ps.setString(1, usuario.getCorreo());
            ps.setInt(2, usuario.getIdUsuario());
            ps.executeUpdate();
            ps.close();

            // Actualizar cliente
            ps = con.prepareStatement(sqlCliente);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getTelefono());
            ps.setInt(5, usuario.getIdUsuario());
            ps.executeUpdate();
            ps.close();

            con.commit();
            return true;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            System.out.println("Error actualizarUsuario: " + e.getMessage());
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    // Eliminar usuario
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM Usuario WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ex) {}
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
        return false;
    }
    
    // Actualizar correo de usuario
    public boolean actualizarCorreo(Usuario u) {
        String sql = "UPDATE Usuario SET correo=? WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getCorreo());
            ps.setInt(2, u.getIdUsuario());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ex) {}
            try { if (con != null) con.close(); } catch (Exception ex) {}
        }
        return false;
    }
}