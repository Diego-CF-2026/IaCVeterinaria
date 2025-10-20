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
    private static final int ID_ESTADO_CANCELADA = 3;
    private static final String SQL_CANCELAR_CITA = 
            "UPDATE citas SET idEstado = ? WHERE idCita = ? AND idEstado = 1"; 
    
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
     * ️ MÉTODO NUEVO: Cambia el estado de una cita de 'Pendiente' a 'Cancelada'.
     */
    public boolean cancelarCita(int idCita) {
        
        // Uso de try-with-resources para asegurar el cierre automático
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CANCELAR_CITA)) {

            ps.setInt(1, ID_ESTADO_CANCELADA); // Nuevo estado: Cancelada
            ps.setInt(2, idCita);
            
            // executeUpdate devuelve el número de filas afectadas
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita ID {0} cancelada (estado cambiado a {1}).", new Object[]{idCita, ID_ESTADO_CANCELADA});
            } else {
                // Esto podría significar que la cita ya estaba Cancelada/Confirmada o que el ID no existe.
                LOGGER.log(Level.WARNING, "⚠️ Advertencia: No se pudo cancelar la cita ID {0}. 0 filas afectadas.", idCita);
            }
            
            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al intentar cancelar cita ID " + idCita, e);
            return false;
        }
    }

// ---------------------------------------------------------------------
//  SECCIÓN MODIFICADA: listarCitasPorCliente
// ---------------------------------------------------------------------

    /**
     * Lista todas las citas de un cliente específico, incluyendo nombre del veterinario, estado y precio.
     */
    public List<Cita> listarCitasPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();
        
        // El SELECT requiere alias y JOINs. Se usa LEFT JOIN para el precio y asegurar que las citas se muestren.
        String sql = "SELECT c.*, v.nombreVeterinario, v.apellidoVeterinario, e.tipoEstado, " 
                   + "esp.precio AS precioEspecialidad " 
                   + "FROM citas c " 
                   + "JOIN veterinario v ON c.idVeterinario = v.idVeterinario "
                   + "JOIN estado e ON c.idEstado = e.idEstado "
                   + "LEFT JOIN especialidad esp ON v.idEspecialidad = esp.idEspecialidad " 
                   + "WHERE c.idCliente = ? "
                   + "ORDER BY c.idCita ASC"; // ⬅️ ¡CAMBIO REALIZADO AQUÍ!

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

// ---------------------------------------------------------------------
//  SECCIÓN MODIFICADA: mapearCita
// ---------------------------------------------------------------------

    /**
     * Método auxiliar para mapear un ResultSet a un objeto Cita.
     * Ahora mapea el precio de la especialidad.
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();

        try {
            // ... (Mapeo de Citas, Veterinario y Estado existente)
            c.setIdCita(rs.getInt("idCita"));
            c.setIdCliente(rs.getInt("idCliente"));
            c.setIdVeterinario(rs.getInt("idVeterinario"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setMotivo(rs.getString("motivo"));

            c.setIdEstado(rs.getInt("idEstado"));
            c.setEstadoNombre(rs.getString("tipoEstado")); 

            c.setNombreVeterinario(rs.getString("nombreVeterinario"));    
            c.setApellidoVeterinario(rs.getString("apellidoVeterinario"));    

            // 💡 NUEVO: Mapeo del Precio de la Especialidad
            // Debe coincidir con el alias en el SELECT: 'precioEspecialidad'
            c.setPrecio(rs.getDouble("precioEspecialidad")); // ⬅️ Se usa el alias del SELECT

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR FATAL en mapearCita. Revise si las columnas SQL coinciden con el mapeo.", e);
            throw e; 
        }

        return c;
    }

// ---------------------------------------------------------------------
// ️ NO MODIFICADA: obtenerCitaPorId (Añade el precio si lo necesitas aquí también)
// ---------------------------------------------------------------------

    /**
     * Obtiene una cita por su ID.
     */
    public Cita obtenerCitaPorId(int id) {
        Cita cita = null;
        // ⚠️ Si necesitas el precio aquí, debes añadir el LEFT JOIN a especialidad y el campo precio al SELECT.
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
// ---------------------------------------------------------------------
// RESTO DE MÉTODOS SIN CAMBIOS
// ---------------------------------------------------------------------

    /**
     * Actualiza una cita existente.
     * Obtiene el idEstado a partir del nombre del estado en el objeto Cita.
     */
    public boolean actualizarCita(Cita cita) {
        
        EstadoDAO estadoDAO = new EstadoDAO();
        // Obtiene el ID del estado a partir del nombre
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
                // Asumiendo que el campo 'nombreVeterinario' de la BD coincide con el getter/setter del modelo
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