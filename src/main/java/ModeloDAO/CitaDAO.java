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

// NOTA: Asume que la clase ModeloDAO.EstadoDAO existe y tiene el método obtenerIdEstadoPorNombre()

public class CitaDAO {

    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());
    private static final int ID_ESTADO_CANCELADA = 3;
    private static final String SQL_CANCELAR_CITA =
            "UPDATE citas SET idEstado = ? WHERE idCita = ? AND idEstado = 1";

    /**
     * Valida reglas de negocio (pasado, horario, domingo).
     * @return String con mensaje de error (❌) o null si es válida.
     */
    private String validarFechaYHora(Cita cita) {
        // Validación de datos nulos antes de intentar la conversión
        if (cita.getFecha() == null || cita.getHora() == null) {
            return "❌ Error de validación: La fecha o la hora están nulas.";
        }
        
        // 1. Convertir la fecha y hora de SQL a Java 8 Time (más fácil para comparaciones)
        java.time.LocalDate citaLocalDate = cita.getFecha().toLocalDate();
        java.time.LocalTime citaLocalTime = cita.getHora().toLocalTime();
        java.time.LocalDateTime citaDateTime = java.time.LocalDateTime.of(citaLocalDate, citaLocalTime);

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

        // --- REGLAS DE VALIDACIÓN ---

        // A. La cita NO puede ser en el futuro (si es para hoy, debe ser después de ahora)
        // Usamos isBefore() para asegurar que no sea en el pasado
        if (citaDateTime.isBefore(ahora.plusMinutes(5))) { // Le damos un margen de 5 minutos
            return "❌ La fecha y hora de la cita no pueden ser en el pasado o muy cercanas a la hora actual.";
        }

        // B. La hora de la cita debe estar dentro del horario laboral (Ej: 9:00 a 17:00)
        java.time.LocalTime horaInicio = java.time.LocalTime.of(6, 0); // las 6 am
        java.time.LocalTime horaFin = java.time.LocalTime.of(22, 0); // 1as 10

        // Si es antes del inicio O después del final
        if (citaLocalTime.isBefore(horaInicio) || citaLocalTime.isAfter(horaFin)) {
            return "❌ La hora de la cita debe estar entre las 06:00 y las 22:00.";
        }
        
        // C. La cita no puede ser en domingo (asumiendo que la veterinaria no abre)
        if (citaLocalDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return "❌ No se pueden programar citas en día Domingo.";
        }

        // Si todas las validaciones son correctas
        return null; 
    }
    
    // 🛑 CAMBIO CLAVE: Cambiar el tipo de retorno de 'boolean' a 'String'
    public String agregarCita(Cita cita) {
        
        // 1. LLAMAR A LA VALIDACIÓN E INTERRUMPIR SI FALLA
        String validacionError = validarFechaYHora(cita);
        if (validacionError != null) {
            LOGGER.log(Level.WARNING, "❌ Validación de cita falló: {0}", validacionError);
            return validacionError; // Retorna el error específico (domingo, horario, etc.)
        }
        
        // 2. OBTENER EL ID DEL ESTADO PREDETERMINADO ("Pendiente")
        EstadoDAO estadoDAO = new EstadoDAO();
        int idEstadoPendiente = estadoDAO.obtenerIdEstadoPorNombre("Pendiente");
        
        // 3. Validación de Estado
        if (idEstadoPendiente <= 0) {
            LOGGER.log(Level.SEVERE, "❌ ERROR: No se pudo encontrar el ID para el estado 'Pendiente'.");
            return "❌ Error: No se pudo verificar el estado de la cita. Contacte al administrador."; // Retorna error de estado
        }

        // 4. SQL de Inserción
        String sql = "INSERT INTO citas (idCliente, idVeterinario, fecha, hora, motivo, precio, idEstado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Validación de nulos/cero básicos (aunque el Servlet debería haberlo hecho)
            if (cita.getMotivo() == null || cita.getIdCliente() <= 0 || cita.getIdVeterinario() <= 0) {
                 LOGGER.log(Level.WARNING, "❌ ERROR de datos: Falta un campo obligatorio (Motivo, Cliente o Veterinario).");
                 return "❌ Error: Faltan datos obligatorios para registrar la cita.";
            }

            // Asignación de parámetros
            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdVeterinario());
            ps.setDate(3, cita.getFecha());
            ps.setTime(4, cita.getHora());
            ps.setString(5, cita.getMotivo());
            ps.setDouble(6, cita.getPrecio()); // Precio de la cita
            ps.setInt(7, idEstadoPendiente); // idEstado (INT)
            
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita agregada. ID Cliente: {0}, Fecha: {1}", new Object[]{cita.getIdCliente(), cita.getFecha()});
                return "✅ Cita registrada con éxito. Recibirás una confirmación pronto."; // 🛑 Retorna ÉXITO (String)
            } else {
                LOGGER.log(Level.WARNING, "❌ Fallo: La inserción devolvió 0 filas afectadas.");
                return "❌ Fallo al registrar: La base de datos no insertó el registro."; // 🛑 Retorna Fallo de Inserción
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al agregar cita.", e);
            // Error común de FK o columna:
            return "❌ Error en la base de datos: La cita no pudo ser registrada (Verifique IDs o formato)."; // 🛑 Retorna Error de DB
        }
    }

    /**
     * Cambia el estado de una cita de 'Pendiente' a 'Cancelada'.
     */
    public boolean cancelarCita(int idCita) {
        // ... (Tu código para cancelarCita permanece igual)
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CANCELAR_CITA)) {

            ps.setInt(1, ID_ESTADO_CANCELADA);
            ps.setInt(2, idCita);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita ID {0} cancelada (estado cambiado a {1}).", new Object[]{idCita, ID_ESTADO_CANCELADA});
            } else {
                LOGGER.log(Level.WARNING, "⚠️ Advertencia: No se pudo cancelar la cita ID {0}. 0 filas afectadas.", idCita);
            }
            
            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al intentar cancelar cita ID " + idCita, e);
            return false;
        }
    }

    /**
     * Lista todas las citas de un cliente específico, incluyendo nombre del veterinario, estado y precio.
     */
    public List<Cita> listarCitasPorCliente(int idCliente) {
        // ... (Tu código para listarCitasPorCliente permanece igual)
        List<Cita> lista = new ArrayList<>();
        
        // SQL: Lee c.* (incluyendo 'precio')
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
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        // ... (Tu código para mapearCita permanece igual)
        Cita c = new Cita();

        try {
            c.setIdCita(rs.getInt("idCita"));
            c.setIdCliente(rs.getInt("idCliente"));
            c.setIdVeterinario(rs.getInt("idVeterinario"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setMotivo(rs.getString("motivo"));
            c.setIdEstado(rs.getInt("idEstado"));
            c.setEstadoNombre(rs.getString("tipoEstado"));
            // Datos del JOIN
            c.setNombreVeterinario(rs.getString("nombreVeterinario"));
            c.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
            // Mapeo del Precio
            c.setPrecio(rs.getDouble("precio"));

            // Manejo de campos opcionales (datos de cliente)
            try {
                c.setNombreCliente(rs.getString("nombreCliente"));
                c.setApellidoCliente(rs.getString("apellidoCliente"));
                c.setDniCliente(rs.getString("dniCliente"));
            } catch (SQLException ex) {
                // Se ignora el error si las columnas de cliente no están en este ResultSet
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR FATAL en mapearCita. Revise si las columnas SQL coinciden con el mapeo.", e);
            throw e;
        }
        return c;
    }

    /**
     * Obtiene una cita por su ID, incluyendo todos los detalles de cliente y el precio.
     */
    public Cita obtenerCitaPorId(int id) {
        // ... (Tu código para obtenerCitaPorId permanece igual)
        Cita cita = null;
        
        // SQL: Se incluye c.precio
        String sql = "SELECT c.idCita, c.idCliente, c.idVeterinario, c.fecha, c.hora, c.motivo, c.idEstado, c.precio, e.tipoEstado, " +
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
     */
    public boolean actualizarCita(Cita cita) {
        // ... (Tu código para actualizarCita permanece igual)
        EstadoDAO estadoDAO = new EstadoDAO();
        int idEstado = estadoDAO.obtenerIdEstadoPorNombre(cita.getEstadoNombre());
        
        if (idEstado <= 0) {
            LOGGER.log(Level.WARNING, "❌ No se pudo encontrar el ID para el estado: " + cita.getEstadoNombre());
            return false;
        }
        
        // El precio no se actualiza aquí, ya que el precio es fijo al reservar.
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
        // ... (Tu código para eliminarCita permanece igual)
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
        // ... (Tu código para listarClientesParaDropdown permanece igual)
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
        // ... (Tu código para listarVeterinariosParaDropdown permanece igual)
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT idVeterinario, nombreVeterinario, apellidoVeterinario, idEspecialidad FROM veterinario ORDER BY apellidoVeterinario, nombreVeterinario";
        
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("idVeterinario"));
                vet.setNombreVeterinario(rs.getString("nombreVeterinario"));
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
        // ... (Tu código para buscarCitas permanece igual)
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