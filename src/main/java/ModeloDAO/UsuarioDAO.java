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
// AGREGAR ESTAS LÍNEAS
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioDAO {

    // ============================================================
    // ATRIBUTOS DE CONEXIÓN Y SENTENCIAS SQL
    // ============================================================
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // ============================================================
    // MÉTODO: INSERTAR CLIENTE Y USUARIO
    // ============================================================

    // Inserta un nuevo usuario con rol cliente y su respectivo registro en la tabla Cliente.
    // Incluye validaciones de correo, contraseña, DNI y teléfono.
    // Usa BCrypt para almacenar la contraseña de forma segura.
    public String insertarCliente(Usuario usuario, Cliente cliente) {
        if (usuario == null || cliente == null) {
            throw new IllegalArgumentException("Usuario y Cliente no pueden ser null");
        }

        // Validación de correo (expresión regular básica)
        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty() ||
            !usuario.getCorreo().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Correo no válido");
        }

        // Validación de contraseña (mínimo 8 caracteres)
        if (usuario.getContra() == null || usuario.getContra().isEmpty() || usuario.getContra().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Validación de DNI (8 dígitos)
        if (cliente.getDni() == null || cliente.getDni().isEmpty() || !cliente.getDni().matches("^\\d{8}$")) {
            throw new IllegalArgumentException("DNI no válido, debe tener 8 dígitos");
        }

        // Validación de teléfono (9 dígitos, empieza con 9)
        if (cliente.getTelefono() == null || cliente.getTelefono().isEmpty() || !cliente.getTelefono().matches("^9\\d{8}$")) {
            throw new IllegalArgumentException("Teléfono no válido, debe empezar con 9 y tener 9 dígitos");
        }

        // Línea ~73: Hashear contraseña con SHA-256
        String password_sin_hashear = usuario.getContra();
        String hashedPassword = hashearConSHA256(password_sin_hashear);

        // Sentencias SQL: primero Usuario, luego Cliente
        String sqlUsuario = "INSERT INTO usuario(idRol, correo, contra, intentos, Estado, tiempo_bloqueo) VALUES (?, ?, ?, ?, ?, NULL)";
        String sqlCliente = "INSERT INTO cliente(idUsuario, nombre, apellido, dni, telefono) VALUES (?, ?, ?, ?, ?)";

        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // Insertar Usuario (rol cliente)
            ps = con.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 3);
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, hashedPassword);
            ps.setInt(4, usuario.getIntentos());
            ps.setBoolean(5, usuario.isEstado());
            ps.executeUpdate();

            // Recuperar idUsuario generado
            rs = ps.getGeneratedKeys();
            int idUsuario = 0;
            if (rs.next()) {
                idUsuario = rs.getInt(1);
            }
            rs.close();
            ps.close();

            // Insertar Cliente asociado al usuario
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
            // En caso de error, revertir transacción
            try { if (con != null) con.rollback(); } catch (Exception ex) {}

            // Detectar errores comunes por campos únicos
            if (e.getMessage() != null) {
                if (e.getMessage().contains("dni")) return "dni";
                if (e.getMessage().contains("correo")) return "correo";
                if (e.getMessage().contains("telefono")) return "telefono";
            }
            return "error";

        } finally {
            // Cierre de recursos
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    // ============================================================
    // MÉTODOS DE LOGIN Y CONTROL DE INTENTOS
    // ============================================================

    // Obtiene los intentos y tiempo de bloqueo de un usuario específico por correo.
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

    // Realiza la autenticación del usuario verificando el hash con BCrypt.
    public Usuario login(String correo, String contra) {
        if (correo == null || correo.isEmpty() || contra == null || contra.length() != 64) {
            return null;
        }

        String sql = "SELECT u.*, r.nombreRol FROM usuario u " +
                     "INNER JOIN rol r ON u.idRol = r.idRol " +
                     "WHERE u.correo = ?";

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Línea ~226: Comparar contraseña ingresada con hash almacenado (SHA-256)
                String hash_almacenado = rs.getString("contra");

                if (contra.equals(hash_almacenado)) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setIdRol(rs.getInt("idRol"));
                    u.setCorreo(rs.getString("correo"));
                    u.setContra(rs.getString("contra"));
                    u.setIntentos(rs.getInt("intentos"));
                    u.setEstado(rs.getBoolean("estado"));
                    u.setNombreRol(rs.getString("nombreRol"));
                    u.setTiempoBloqueo(rs.getTimestamp("tiempo_bloqueo"));
                    return u;
                }
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

    // Incrementa el número de intentos fallidos y devuelve el nuevo valor.
    public int incrementarIntentos(String correo) {
        String sql = "UPDATE usuario SET intentos = intentos + 1 WHERE correo = ?";
        String sqlSelect = "SELECT intentos FROM usuario WHERE correo = ?";
        int nuevosIntentos = 0;

        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.executeUpdate();
            ps.close();

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

    // Bloquea al usuario durante un tiempo determinado en minutos.
    public void bloquearUsuario(String correo, int minutosBloqueo) {
        Instant tiempoFuturo = Instant.now().plus(minutosBloqueo, ChronoUnit.MINUTES);
        Timestamp timestampBloqueo = Timestamp.from(tiempoFuturo);

        String sql = "UPDATE usuario SET tiempo_bloqueo = ? WHERE correo = ?";

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setTimestamp(1, timestampBloqueo);
            ps.setString(2, correo);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try { if (ps != null) ps.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }

    // Restablece intentos y elimina el bloqueo tras un inicio de sesión exitoso.
    public void reiniciarIntentos(String correo) {
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

    // ============================================================
    // MÉTODOS ADICIONALES
    // ============================================================

    // Actualiza datos de usuario y cliente asociados.
    public boolean actualizarUsuario(Usuario usuario, Cliente cliente) {
        String sqlUsuario = "UPDATE usuario SET correo=? WHERE idUsuario=?";
        String sqlCliente = "UPDATE cliente SET nombre=?, apellido=?, dni=?, telefono=? WHERE idUsuario=?";
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            ps = con.prepareStatement(sqlUsuario);
            ps.setString(1, usuario.getCorreo());
            ps.setInt(2, usuario.getIdUsuario());
            ps.executeUpdate();
            ps.close();

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

    // Elimina un usuario por su ID.
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM usuario WHERE idUsuario=?";
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

    // Actualiza solo el correo de un usuario.
    public boolean actualizarCorreo(Usuario u) {
        String sql = "UPDATE usuario SET correo=? WHERE idUsuario=?";
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
    
    // ============================================================
    // FUNCIÓN AUXILIAR SHA-256
    // ============================================================
    private String hashearConSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            // Rellenar con ceros a la izquierda para asegurar 64 caracteres
            while (hashtext.length() < 64) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash SHA-256", e);
        }
    }
}
