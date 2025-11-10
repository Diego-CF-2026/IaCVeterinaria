
package ModeloDAO;

import Modelo.Administrador;
import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


// Clase AdministradorDAO
// Contiene métodos para realizar consultas a la tabla 'administrador'
// dentro de la base de datos, como la validación de un administrador al iniciar sesión.

public class AdministradorDAO {
    
    // Método para validar las credenciales de un administrador.
    // 
    // correo     Correo electrónico ingresado por el usuario.
    // contrasena Contraseña correspondiente al administrador.
    // @return Objeto Administrador si las credenciales son correctas, de lo contrario null.
    
    public Administrador validarAdministrador(String correo, String contrasena) {
        // Consulta SQL que busca un administrador con el correo y contraseña proporcionados.
        String sql = "SELECT * FROM administrador WHERE A_Correo = ? AND A_Contrasena = ?";
        
        // Variable para almacenar el resultado, se inicializa como null por si no se encuentra coincidencia.
        Administrador admin = null;
        
        // Uso de try-with-resources para garantizar el cierre automático de la conexión y el PreparedStatement.
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Asignación de los parámetros en la consulta preparada para evitar inyección SQL.
            ps.setString(1, correo);
            ps.setString(2, contrasena);
            
            // Ejecución de la consulta y obtención del resultado.
            try (ResultSet rs = ps.executeQuery()) {
                // Si existe un registro que coincide, se crea el objeto Administrador.
                if (rs.next()) {
                    admin = new Administrador();
                    
                    // Asignación de valores obtenidos del ResultSet a las propiedades del objeto.
                    admin.setIdAdmin(rs.getInt("idAdmin"));
                    admin.setNombre(rs.getString("A_Nombre"));
                    admin.setApellido(rs.getString("A_Apellido"));
                }
            }
        } catch (SQLException e) {
            // Manejo de errores de conexión o consulta.
            System.err.println("Error al validar administrador:");
            e.printStackTrace();
        }
        
        // Retorna el objeto Administrador si se encontró, o null en caso contrario.
        return admin;
    }
}