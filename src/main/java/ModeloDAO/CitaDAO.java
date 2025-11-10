
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//
// Clase: CitaDAO
// Descripción: Clase encargada de gestionar todas las operaciones relacionadas con
// la entidad 'Cita' en la base de datos. Implementa las operaciones CRUD y otras
// funciones auxiliares, como validaciones de negocio y búsquedas.
//

public class CitaDAO {

    // Logger para registrar información y errores
    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());
    
    // Constante que representa el ID del estado "Cancelada"
    private static final int ID_ESTADO_CANCELADA = 3;
    
    // Consulta SQL para actualizar el estado de una cita (de Pendiente a Cancelada)
    private static final String SQL_CANCELAR_CITA =
            "UPDATE citas SET idEstado = ? WHERE idCita = ? AND idEstado = 1";


    // Realiza la validación de reglas de negocio sobre fecha y hora de una cita.
    // Valida que no esté en el pasado, que sea dentro del horario laboral y que no sea domingo.
    //
    // @param cita Objeto Cita que contiene la información de fecha y hora.
    // @return Mensaje de error si no cumple las reglas, o null si es válida.

    private String validarFechaYHora(Cita cita) {
        // Validación de datos nulos (evita NullPointerException)
        if (cita.getFecha() == null || cita.getHora() == null) {
            return "❌ Error de validación: La fecha o la hora están nulas.";
        }
        
        // Conversión a tipos de fecha y hora modernos (Java Time)
        java.time.LocalDate citaLocalDate = cita.getFecha().toLocalDate();
        java.time.LocalTime citaLocalTime = cita.getHora().toLocalTime();
        java.time.LocalDateTime citaDateTime = java.time.LocalDateTime.of(citaLocalDate, citaLocalTime);

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

        // --- Reglas de negocio principales ---

        // No permitir citas en un tiempo pasado o demasiado cerca al tiempo actual
        if (citaDateTime.isBefore(ahora.plusMinutes(5))) {
            return "❌ La fecha y hora de la cita no pueden ser en el pasado o muy cercanas a la hora actual.";
        }

        // Rango horario permitido (6 AM a 10 PM)
        java.time.LocalTime horaInicio = java.time.LocalTime.of(6, 0);
        java.time.LocalTime horaFin = java.time.LocalTime.of(22, 0);

        if (citaLocalTime.isBefore(horaInicio) || citaLocalTime.isAfter(horaFin)) {
            return "❌ La hora de la cita debe estar entre las 06:00 y las 22:00.";
        }
        
        // No se permiten citas los domingos
        if (citaLocalDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return "❌ No se pueden programar citas en día Domingo.";
        }

        // Si pasa todas las validaciones
        return null; 
    }
    
    //
    // Inserta una nueva cita en la base de datos luego de validar los datos de entrada.
    // 
    // cita Objeto Cita con los datos del cliente, veterinario, fecha, etc.
    // @return String indicando el resultado de la operación (éxito o error).
    //
    
    public String agregarCita(Cita cita) {
        String validacionError = validarFechaYHora(cita);
        if (validacionError != null) {
            LOGGER.log(Level.WARNING, "❌ Validación de cita falló: {0}", validacionError);
            return validacionError;
        }
        
        // Obtener el ID del estado "Pendiente"
        EstadoDAO estadoDAO = new EstadoDAO();
        int idEstadoPendiente = estadoDAO.obtenerIdEstadoPorNombre("Pendiente");
        
        // Validar que el ID se haya obtenido correctamente
        if (idEstadoPendiente <= 0) {
            LOGGER.log(Level.SEVERE, "❌ No se pudo encontrar el ID para el estado 'Pendiente'.");
            return "❌ Error: No se pudo verificar el estado de la cita. Contacte al administrador.";
        }

        // Sentencia SQL para insertar cita
        String sql = "INSERT INTO citas (idCliente, idVeterinario, fecha, hora, motivo, precio, idEstado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Validación de campos obligatorios antes de ejecutar SQL
            if (cita.getMotivo() == null || cita.getIdCliente() <= 0 || cita.getIdVeterinario() <= 0) {
                 LOGGER.log(Level.WARNING, "❌ Datos faltantes (Motivo, Cliente o Veterinario).");
                 return "❌ Error: Faltan datos obligatorios para registrar la cita.";
            }

            // Asignación de parámetros a la consulta
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setDouble(6, cita.getPrecio());
            ps.setInt(7, idEstadoPendiente);
            
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Cita agregada correctamente (Cliente {0}, Fecha {1})", 
                           new Object[]{cita.getIdCliente(), cita.getFecha()});
                return "✅ Cita registrada con éxito. Recibirás una confirmación pronto.";
            } else {
                LOGGER.log(Level.WARNING, "❌ Inserción fallida (0 filas afectadas).");
                return "❌ Fallo al registrar: No se insertó el registro.";
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error SQL al agregar cita.", e);
            return "❌ Error en la base de datos: La cita no pudo ser registrada.";
        }
    }

    //
    // Cancela una cita existente cambiando su estado en la base de datos.
    // Solo se pueden cancelar citas en estado 'Pendiente'.
    //
    
    public boolean cancelarCita(int idCita) {
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CANCELAR_CITA)) {

            // Asignación de parámetros: nuevo estado y ID de cita
            ps.setInt(1, ID_ESTADO_CANCELADA);
            ps.setInt(2, idCita);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Cita ID {0} cancelada correctamente.", idCita);
            } else {
                LOGGER.log(Level.WARNING, "⚠️ No se pudo cancelar la cita ID {0}.", idCita);
            }
            
            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error SQL al intentar cancelar cita ID " + idCita, e);
            return false;
        }
    }

    //
    // Devuelve todas las citas de un cliente específico, ordenadas por fecha y hora.
    // Incluye información del veterinario y el estado de la cita.
    //
    
    public List<Cita> listarCitasPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();
        
        // SQL con JOINs para obtener datos relacionados (veterinario, estado)
        String sql = "SELECT c.*, v.nombreVeterinario, v.apellidoVeterinario, e.tipoEstado "
                    + "FROM citas c "
                    + "JOIN veterinario v ON c.idVeterinario = v.idVeterinario "
                    + "JOIN estado e ON c.idEstado = e.idEstado "
                    + "WHERE c.idCliente = ? "
                    + "ORDER BY c.fecha DESC, c.hora DESC"; 

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs)); // Mapea cada fila del resultado a un objeto Cita
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error al listar citas del cliente.", e);
        }
        return lista;
    }

    //
    // Convierte un registro de ResultSet en un objeto Cita completo.
    //
    
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        try {
            // Mapeo de los campos principales
            c.setIdCita(rs.getInt("idCita"));
            c.setIdCliente(rs.getInt("idCliente"));
            c.setIdVeterinario(rs.getInt("idVeterinario"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setMotivo(rs.getString("motivo"));
            c.setIdEstado(rs.getInt("idEstado"));
            
            try { c.setEstadoNombre(rs.getString("tipoEstado")); } catch (SQLException ex) {}
            try { c.setNombreVeterinario(rs.getString("nombreVeterinario")); } catch (SQLException ex) {}
            try { c.setApellidoVeterinario(rs.getString("apellidoVeterinario")); } catch (SQLException ex) {}
            try { c.setPrecio(rs.getDouble("precio")); } catch (SQLException ex) {}
            try {
                c.setNombreCliente(rs.getString("nombreCliente"));
                c.setApellidoCliente(rs.getString("apellidoCliente"));
                c.setDniCliente(rs.getString("dniCliente"));
            } catch (SQLException ex) {}
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error en el mapeo de ResultSet a Cita.", e);
            throw e;
        }
        return c;
    }
}
