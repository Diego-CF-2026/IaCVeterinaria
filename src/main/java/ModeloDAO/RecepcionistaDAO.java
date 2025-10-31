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
import org.mindrot.jbcrypt.BCrypt; // Necesario para hashear contraseñas

public class RecepcionistaDAO {

    // Constantes SQL y Rol
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Basado en tu tabla Usuario, el ID_ROL para Recepcionista es 2
    private static final int ID_ROL_RECEPCIONISTA = 2; 

    // =================================================================
    // LISTAR RECEPCIONISTAS
    // =================================================================

    public List<Recepcionista> listarRecepcionistas() {
        List<Recepcionista> lista = new ArrayList<>();
        // Query para listar recepcionistas y sus datos de usuario asociados
        String sql = "SELECT U.idUsuario, U.correo, U.Estado, "
                   + "R.idRecepcionista, R.nombreRecepcionista, R.apellidoRecepcionista, R.telefonoRecepcionista "
                   + "FROM Usuario U "
                   + "INNER JOIN recepcionista R ON U.idUsuario = R.idUsuario "
                   + "WHERE U.idRol = " + ID_ROL_RECEPCIONISTA + " "
                   + "ORDER BY R.apellidoRecepcionista ASC";

        try {
            con = Conexion.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                // Crear objeto Usuario anidado
                Usuario u = new Usuario(); 
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setCorreo(rs.getString("correo"));
                u.setEstado(rs.getBoolean("Estado"));

                // Crear objeto Recepcionista
                Recepcionista r = new Recepcionista();
                r.setIdRecepcionista(rs.getInt("idRecepcionista"));
                r.setIdUsuario(rs.getInt("idUsuario"));
                r.setNombreRecepcionista(rs.getString("nombreRecepcionista"));
                r.setApellidoRecepcionista(rs.getString("apellidoRecepcionista"));
                r.setTelefonoRecepcionista(rs.getString("telefonoRecepcionista"));
                
                // Asignar el objeto Usuario al Recepcionista (Relación de composición)
                r.setUsuario(u); 

                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar recepcionistas: " + e.getMessage());
        } finally {
            closeResources();
        }
        return lista;
    }

    // =================================================================
    // AGREGAR RECEPCIONISTA (Transaccional)
    // =================================================================

    public String agregarRecepcionista(Usuario usuario, Recepcionista recepcionista) {
        String resultado = "error";
        String sqlUser = "INSERT INTO Usuario (idRol, correo, contra, intentos, Estado) VALUES (?, ?, ?, 0, TRUE)";
        String sqlRecep = "INSERT INTO recepcionista (idUsuario, nombreRecepcionista, apellidoRecepcionista, telefonoRecepcionista) VALUES (LAST_INSERT_ID(), ?, ?, ?)";
        
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar Transacción

            // 1. Insertar Usuario
            String hashedPassword = BCrypt.hashpw(usuario.getContra(), BCrypt.gensalt());
            ps = con.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ID_ROL_RECEPCIONISTA);
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, hashedPassword);
            ps.executeUpdate();

            // 2. Insertar Recepcionista (usa LAST_INSERT_ID())
            if (ps != null) ps.close(); 
            ps = con.prepareStatement(sqlRecep);
            ps.setString(1, recepcionista.getNombreRecepcionista());
            ps.setString(2, recepcionista.getApellidoRecepcionista());
            ps.setString(3, recepcionista.getTelefonoRecepcionista());
            ps.executeUpdate();
            
            con.commit(); // Confirmar
            resultado = "ok";
            
        } catch (SQLException e) {
            try { 
                if (con != null) con.rollback(); 
            } catch (SQLException ex) { /* Ignorar */ }
            // Manejo específico para errores de unicidad
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

    // =================================================================
    // EDITAR RECEPCIONISTA (Transaccional)
    // =================================================================
    
    public String editarRecepcionista(Usuario usuario, Recepcionista recepcionista) {
        String resultado = "error";
        // Asumiendo que la contra se actualiza si se envía, si no, se usa otro método
        String sqlUpdateUser = "UPDATE Usuario SET correo = ?, contra = ? WHERE idUsuario = ?"; 
        String sqlUpdateRecep = "UPDATE recepcionista SET nombreRecepcionista = ?, apellidoRecepcionista = ?, telefonoRecepcionista = ? WHERE idUsuario = ?";
        
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar Transacción

            // 1. Actualizar Usuario
            String hashedPassword = BCrypt.hashpw(usuario.getContra(), BCrypt.gensalt());
            ps = con.prepareStatement(sqlUpdateUser);
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, hashedPassword);
            ps.setInt(3, recepcionista.getIdUsuario());
            ps.executeUpdate();

            // 2. Actualizar Recepcionista
            if (ps != null) ps.close(); 
            ps = con.prepareStatement(sqlUpdateRecep);
            ps.setString(1, recepcionista.getNombreRecepcionista());
            ps.setString(2, recepcionista.getApellidoRecepcionista());
            ps.setString(3, recepcionista.getTelefonoRecepcionista());
            ps.setInt(4, recepcionista.getIdUsuario());
            ps.executeUpdate();
            
            con.commit(); // Confirmar
            resultado = "ok";
            
        } catch (SQLException e) {
            try { 
                if (con != null) con.rollback(); 
            } catch (SQLException ex) { /* Ignorar */ }
            resultado = "error: " + e.getMessage();
        } finally {
            closeResourcesWithCommitControl();
        }
        return resultado;
    }


    // =================================================================
    // DESACTIVAR RECEPCIONISTA (Baja Lógica)
    // =================================================================

    public String desactivarRecepcionista(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        
        // SQL: Cambiar Estado a FALSE
        String sql = "UPDATE Usuario SET Estado = FALSE WHERE idUsuario = ?";
        
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
    
    // =================================================================
    // ACTIVAR RECEPCIONISTA (Alta Lógica)
    // =================================================================

    public String activarRecepcionista(int idUsuario) {
        Connection con = null;
        PreparedStatement ps = null;
        
        // SQL: Cambiar Estado a TRUE
        String sql = "UPDATE Usuario SET Estado = TRUE WHERE idUsuario = ?";
        
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
    
    // =================================================================
    // Cierre de Recursos
    // =================================================================
    
    // Cierra recursos básicos sin controlar el auto commit
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    
    // Cierra recursos y restablece auto commit a TRUE (necesario después de transacciones)
    private void closeResourcesWithCommitControl() {
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
}