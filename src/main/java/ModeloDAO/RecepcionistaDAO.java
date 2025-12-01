package ModeloDAO;

import Modelo.Conexion;
import Modelo.Recepcionista;
import Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// AGREGAR ESTAS LÍNEAS PARA SHA-256:
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RecepcionistaDAO {

    // Objetos de conexión y manejo SQL
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Rol fijo para los recepcionistas en la tabla Usuario
    private static final int ID_ROL_RECEPCIONISTA = 2;

    // ============================================================
    // LISTAR RECEPCIONISTAS ACTIVOS
    // ============================================================
    public List<Recepcionista> listarRecepcionistas() {
        List<Recepcionista> lista = new ArrayList<>();
        // Consulta para unir usuario con datos del recepcionista
        String sql = "SELECT U.idUsuario, U.correo, U.Estado, "
                   + "R.idRecepcionista, R.nombreRecepcionista, R.apellidoRecepcionista, R.telefonoRecepcionista "
                   + "FROM usuario U "
                   + "INNER JOIN recepcionista R ON U.idUsuario = R.idUsuario "
                   + "WHERE U.idRol = " + ID_ROL_RECEPCIONISTA + " "
                   + "ORDER BY R.apellidoRecepcionista ASC";

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // Recorremos resultados y mapeamos objetos
            while (rs.next()) {
                Usuario u = new Usuario(); 
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setCorreo(rs.getString("correo"));
                u.setEstado(rs.getBoolean("Estado"));

                Recepcionista r = new Recepcionista();
                r.setIdRecepcionista(rs.getInt("idRecepcionista"));
                r.setIdUsuario(rs.getInt("idUsuario"));
                r.setNombreRecepcionista(rs.getString("nombreRecepcionista"));
                r.setApellidoRecepcionista(rs.getString("apellidoRecepcionista"));
                r.setTelefonoRecepcionista(rs.getString("telefonoRecepcionista"));
                r.setUsuario(u); // Asociación con su usuario
                
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar recepcionistas: " + e.getMessage());
        } finally {
            closeResources();
        }
        return lista;
    }

    // ============================================================
    // AGREGAR NUEVO RECEPCIONISTA
    // ============================================================
    public String agregarRecepcionista(Usuario usuario, Recepcionista recepcionista) {
        String resultado = "error";
        // SQL para crear usuario y recepcionista (transacción)
        String sqlUser = "INSERT INTO usuario (idRol, correo, contra, intentos, Estado) VALUES (?, ?, ?, 0, TRUE)";
        String sqlRecep = "INSERT INTO recepcionista (idUsuario, nombreRecepcionista, apellidoRecepcionista, telefonoRecepcionista) VALUES (LAST_INSERT_ID(), ?, ?, ?)";
        
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Inicia transacción manual

            // Encriptar la contraseña antes de guardarla
            String hashedPassword = hashearConSHA256(usuario.getContra());
            ps = con.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ID_ROL_RECEPCIONISTA);
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, hashedPassword);
            ps.executeUpdate();

            // Insertar datos del recepcionista vinculando el usuario recién creado
            if (ps != null) ps.close(); 
            ps = con.prepareStatement(sqlRecep);
            ps.setString(1, recepcionista.getNombreRecepcionista());
            ps.setString(2, recepcionista.getApellidoRecepcionista());
            ps.setString(3, recepcionista.getTelefonoRecepcionista());
            ps.executeUpdate();
            
            con.commit(); // Confirmar cambios
            resultado = "ok";
            
        } catch (SQLException e) {
            // Si hay error se revierte la transacción
            try { 
                if (con != null) con.rollback(); 
            } catch (SQLException ex) {}
            // Verificación de errores de duplicado
            if (e.getSQLState().equals("23000")) { 
                if (e.getMessage().toLowerCase().contains("correo")) {
                    resultado = "error_correo_duplicado";
                } else if (e.getMessage().toLowerCase().contains("telefono")) {
                    resultado = "error_telefono_duplicado";
                } else {
                    resultado = "error_duplicado";
                }
            } else {
                resultado = "error: " + e.getMessage();
            }
        } finally {
            closeResourcesWithCommitControl();
        }
        return resultado;
    }

    // ============================================================
    // EDITAR DATOS DEL RECEPCIONISTA
    // ============================================================
    public String editarRecepcionista(Usuario usuario, Recepcionista recepcionista) {
        String resultado = "error";
        // Se actualiza tanto el usuario como la información del recepcionista
        String sqlUpdateUser = "UPDATE usuario SET correo = ?, contra = ? WHERE idUsuario = ?"; 
        String sqlUpdateRecep = "UPDATE recepcionista SET nombreRecepcionista = ?, apellidoRecepcionista = ?, telefonoRecepcionista = ? WHERE idUsuario = ?";
        
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            // Se vuelve a hashear la contraseña al actualizarla
            String hashedPassword = hashearConSHA256(usuario.getContra());
            ps = con.prepareStatement(sqlUpdateUser);
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, hashedPassword);
            ps.setInt(3, recepcionista.getIdUsuario());
            ps.executeUpdate();

            if (ps != null) ps.close(); 
            ps = con.prepareStatement(sqlUpdateRecep);
            ps.setString(1, recepcionista.getNombreRecepcionista());
            ps.setString(2, recepcionista.getApellidoRecepcionista());
            ps.setString(3, recepcionista.getTelefonoRecepcionista());
            ps.setInt(4, recepcionista.getIdUsuario());
            ps.executeUpdate();
            
            con.commit();
            resultado = "ok";
            
        } catch (SQLException e) {
            try { 
                if (con != null) con.rollback(); 
            } catch (SQLException ex) {}
            resultado = "error: " + e.getMessage();
        } finally {
            closeResourcesWithCommitControl();
        }
        return resultado;
    }

    // ============================================================
    // DESACTIVAR (BAJA LÓGICA)
    // ============================================================
    public String desactivarRecepcionista(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        // Solo cambia el estado del usuario a inactivo
        String sql = "UPDATE usuario SET estado = FALSE WHERE idUsuario = ?";
        
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();
            return (filasAfectadas > 0) ? "ok" : "no_encontrado";

        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        } finally {
            closeResources();
        }
    }
    
    // ============================================================
    // REACTIVAR RECEPCIONISTA
    // ============================================================
    public String activarRecepcionista(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "UPDATE usuario SET estado = TRUE WHERE idUsuario = ?";
        
        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();
            return (filasAfectadas > 0) ? "ok" : "no_encontrado";

        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        } finally {
            closeResources();
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES DE CIERRE
    // ============================================================
    private void closeResources() {
        // Cierra ResultSet, PreparedStatement y la conexión
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    private void closeResourcesWithCommitControl() {
        // Restablece el auto commit tras operaciones transaccionales
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos con control de commit: " + e.getMessage());
        }
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
