package ModeloDAO;

import Modelo.Conexion;
import Modelo.Cita;
import Modelo.Cliente;
import Modelo.Veterinario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CitaDAO {

    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());

    // NOTA IMPORTANTE: Se eliminaron los atributos de clase (con, ps, rs) 
    // y el método closeResources para asegurar la seguridad en entornos multihilo.

    /**
     * Registra una nueva cita en la base de datos.
     * Utiliza EstadoDAO para obtener el idEstado "Pendiente".
     */
    public boolean agregarCita(Cita cita) {
        // 1. OBTENER EL ID DEL ESTADO PREDETERMINADO ("Pendiente")
        EstadoDAO estadoDAO = new EstadoDAO();
        int idEstadoPendiente = estadoDAO.obtenerIdEstadoPorNombre("Pendiente"); 
        
        // 2. Validación de Estado
        if (idEstadoPendiente <= 0) {
            LOGGER.log(Level.SEVERE, "❌ ERROR: No se pudo encontrar el ID para el estado 'Pendiente'.");
            return false;
        }

        // 3. SQL de Inserción: Usa idEstado (INT)
        String sql = "INSERT INTO citas (idCliente, idVeterinario, fecha, hora, motivo, idEstado) VALUES (?, ?, ?, ?, ?, ?)";

        // Uso de try-with-resources para asegurar el cierre automático
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Validación básica de datos
            if (cita.getFecha() == null || cita.getHora() == null || cita.getMotivo() == null || cita.getIdCliente() <= 0 || cita.getIdVeterinario() <= 0) {
                 LOGGER.log(Level.WARNING, "❌ ERROR de datos: Falta un campo obligatorio.");
                 return false;
            }

            // Asignación de parámetros
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setInt(6, idEstadoPendiente); // Inserta el ID (INT) del estado

            int filasAfectadas = ps.executeUpdate();

            // Logging y retorno
            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita agregada. ID Cliente: {0}, Fecha: {1}", new Object[]{cita.getIdCliente(), cita.getFecha()});
            } else {
                 LOGGER.log(Level.WARNING, "❌ Fallo: La inserción devolvió 0 filas afectadas.");
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al agregar cita. CAUSA PROBABLE: Clave Foránea (FK) o campo obligatorio nulo.", e);
            return false;
        }
    }

    /**
     * Lista todas las citas de un cliente específico, incluyendo nombre del veterinario y estado.
     */
    public List<Cita> listarCitasPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();
        
        // El SELECT requiere alias y JOINs para obtener los detalles
        String sql = "SELECT c.*, v.nombreVeterinario, v.apellidoVeterinario, e.tipoEstado "
                   + "FROM citas c " 
                   + "JOIN veterinario v ON c.idVeterinario = v.idVeterinario "
                   + "JOIN estado e ON c.idEstado = e.idEstado " // Une para obtener el nombre del estado
                   + "WHERE c.idCliente = ? "
                   + "ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);    
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL o JDBC al listar citas.", e);
        }
        return lista;
    }

    /**
     * Método auxiliar para mapear un ResultSet a un objeto Cita.
     * Mapea el nombre del estado a 'estadoNombre'.
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();

        try {
            // Mapeo de columnas de la tabla Citas
            c.setIdCita(rs.getInt("idCita"));
            c.setIdCliente(rs.getInt("idCliente"));
            c.setIdVeterinario(rs.getInt("idVeterinario"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setMotivo(rs.getString("motivo"));
            
            // CORREGIDO: Mapea el idEstado y el nombre del estado
            c.setIdEstado(rs.getInt("idEstado")); // Se asume que el SELECT tiene c.*
            c.setEstadoNombre(rs.getString("tipoEstado")); // CORREGIDO: Usa el campo 'tipoEstado' y el setter correcto

            // Mapeo de las columnas del JOIN Veterinario
            c.setNombreVeterinario(rs.getString("nombreVeterinario"));    
            c.setApellidoVeterinario(rs.getString("apellidoVeterinario"));    

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR FATAL en mapearCita. Revise si las columnas SQL coinciden con el mapeo.", e);
            throw e; 
        }

        return c;
    }

    /**
     * Obtiene una cita por su ID.
     */
    public Cita obtenerCitaPorId(int id) {
        Cita cita = null;
        String sql = "SELECT c.idCita, c.idCliente, c.idVeterinario, c.fecha, c.hora, c.motivo, c.idEstado, e.tipoEstado, " +
                     "cl.nombre AS nombreCliente, cl.apellido AS apellidoCliente, cl.dni AS dniCliente, " +
                     "v.nombreVeterinario, v.apellidoVeterinario, v.idEspecialidad " +
                     "FROM citas c " +
                     "JOIN cliente cl ON c.idCliente = cl.idCliente " +
                     "JOIN veterinario v ON c.idVeterinario = v.idVeterinario " +
                     "JOIN estado e ON c.idEstado = e.idEstado "
                     + "WHERE c.idCita = ?";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cita = mapearCita(rs);
                }
            }
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al obtener cita por ID.", e);
        }
        return cita;
    }

    /**
     * Actualiza una cita existente.
     * Obtiene el idEstado a partir del nombre del estado en el objeto Cita.
     */
    public boolean actualizarCita(Cita cita) {
        
        EstadoDAO estadoDAO = new EstadoDAO();
        // CORREGIDO: Usamos el getter correcto getEstadoNombre()
        int idEstado = estadoDAO.obtenerIdEstadoPorNombre(cita.getEstadoNombre()); 
        
        if (idEstado <= 0) {
            LOGGER.log(Level.WARNING, "❌ No se pudo encontrar el ID para el estado: " + cita.getEstadoNombre());
            return false;
        }
        
        // SQL: Actualiza idEstado con el ID (INT)
        String sql = "UPDATE citas SET idCliente=?, idVeterinario=?, fecha=?, hora=?, motivo=?, idEstado=? WHERE idCita=?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setInt(6, idEstado);    
            ps.setInt(7, cita.getIdCita());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al actualizar cita.", e);
            return false;
        }
    }

    /**
     * Elimina una cita mediante un Stored Procedure.
     */
    public int eliminarCita(int id) {
        String sql = "{CALL sp_eliminar_cita(?, ?)}";    
        int resultado = -2;

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(2);

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al eliminar cita mediante SP.", e);
        }
        return resultado;
    }

    /**
     * Lista los clientes para su uso en dropdowns/selects.
     */
    public List<Cliente> listarClientesParaDropdown() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT idCliente, nombre, apellido, dni FROM cliente ORDER BY apellido, nombre";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setDni(rs.getString("dni"));
                lista.add(cliente);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar clientes para dropdown.", e);
        }
        return lista;
    }

    /**
     * Lista los veterinarios para su uso en dropdowns/selects.
     */
    public List<Veterinario> listarVeterinariosParaDropdown() {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT idVeterinario, nombreVeterinario, apellidoVeterinario, idEspecialidad FROM veterinario ORDER BY apellidoVeterinario, nombreVeterinario";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                // CORREGIDO: Se usa el setter correcto 'setNombreVeterianrio' (con 'a')
                // aunque el nombre del campo en la BD es 'nombreVeterinario'
                vet.setNombreVeterianrio(rs.getString("nombreVeterinario")); 
                vet.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                vet.setIdEspecialidad(rs.getInt("idEspecialidad"));    
                lista.add(vet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar veterinarios para dropdown.", e);
        }
        return lista;
    }

    /**
     * Busca citas mediante un Stored Procedure.
     */
    public List<Cita> buscarCitas(String busqueda) {
        List<Cita> citas = new ArrayList<>();
        String sql = "{CALL sp_buscar_citas(?)}";    

        try (Connection con = Conexion.getConnection();    
             CallableStatement stmt = con.prepareCall(sql)) {

            stmt.setString(1, busqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(mapearCita(rs));    
                }
            }
        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al buscar citas con stored procedure", e);
        }
        return citas;
    }
}