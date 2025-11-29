package ModeloDAO;

import Modelo.Conexion;
import Modelo.Cita;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object (DAO) para la gestión de Citas.
 * Maneja la interacción con la tabla 'citas' y sus JOINs.
 */
public class CitaDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CitaDAO.class.getName());
    // Asumiendo que el ID 3 corresponde a "Cancelada" en la tabla 'estado'
    private static final int ID_ESTADO_CANCELADA = 3;

    private static final String SQL_CANCELAR_CITA =
            // Solo permite cancelar si el estado actual es Pendiente (Asumiendo que 1 es 'Pendiente')
            "UPDATE citas SET idEstado = ? WHERE idCita = ? AND idEstado = 1";

    /**
     * Consulta base para el LISTADO GENERAL (ListarCitas, ListarCitasPendientes, etc.)
     * Incluye todos los campos de JOIN necesarios.
     */
    private static final String SQL_BASE_SELECT =
            "SELECT c.idCita, c.idCliente, c.idVeterinario, c.fecha, c.hora, c.motivo, c.idEstado, c.precio, e.tipoEstado, " +
            "cl.nombre AS nombreCliente, cl.apellido AS apellidoCliente, cl.dni AS dniCliente, " +
            "v.nombreVeterinario, v.apellidoVeterinario " +
            "FROM citas c " +
            "JOIN cliente cl ON c.idCliente = cl.idCliente " +
            "JOIN veterinario v ON c.idVeterinario = v.idVeterinario " +
            "JOIN estado e ON c.idEstado = e.idEstado "; // ✅ ESPACIO AL FINAL PARA CONCATENAR WHERE

    /**
     * Consulta base SIMPLIFICADA para el REPORTE DE GANANCIAS.
     * Solo incluye las columnas pedidas: Cliente, Veterinario, Precio, Estado y Fechas/Horas.
     */
    // En CitaDAO.java

    private static final String SQL_SELECT_REPORT_BASE
            = "SELECT c.idCita, c.fecha, c.hora, c.precio, "
            + "cl.nombre AS nombreCliente, cl.apellido AS apellidoCliente, cl.dni AS dniCliente, "
            // ⚠️ CORRECCIÓN AQUÍ: Usar los nombres de columna reales de la tabla veterinario
            + "v.nombreVeterinario, v.apellidoVeterinario, e.tipoEstado AS estadoNombre "
            + "FROM citas c "
            + "INNER JOIN cliente cl ON c.idCliente = cl.idCliente "
            + "INNER JOIN veterinario v ON c.idVeterinario = v.idVeterinario "
            + "INNER JOIN estado e ON c.idEstado = e.idEstado "; // ✅ ESPACIO FINAL INCLUIDO


    // --- MÉTODOS AUXILIARES Y DE VALIDACIÓN ---

    /**
     * ✅ Implementación para obtener el ID del estado a partir de su nombre.
     */
    private int obtenerIdEstadoPorNombre(String estadoNombre) throws SQLException {
        int idEstado = -1;
        // Consulta la columna 'tipoEstado' en la tabla 'estado'
        String sql = "SELECT idEstado FROM estado WHERE tipoEstado = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estadoNombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idEstado = rs.getInt("idEstado");
                }
            }
        }
        if (idEstado == -1) {
            LOGGER.log(Level.SEVERE, "El nombre de estado '{0}' no fue encontrado en la columna tipoEstado.", estadoNombre);
            throw new SQLException("El nombre de estado '" + estadoNombre + "' no fue encontrado.");
        }
        return idEstado;
    }

    /**
     * Valida reglas de negocio (pasado, horario, domingo).
     * @return String con mensaje de error (❌) o null si es válida.
     */
    public String validarFechaYHora(Cita cita) {
        if (cita.getFecha() == null || cita.getHora() == null) {
            return "❌ Error de validación: La fecha o la hora están nulas.";
        }

        java.time.LocalDate citaLocalDate = cita.getFecha().toLocalDate();
        java.time.LocalTime citaLocalTime = cita.getHora().toLocalTime();
        java.time.LocalDateTime citaDateTime = java.time.LocalDateTime.of(citaLocalDate, citaLocalTime);
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

        // A. La cita NO puede ser en el pasado o muy cercana (margen de 5 minutos)
        if (citaDateTime.isBefore(ahora.plusMinutes(5))) {
            return "❌ La fecha y hora de la cita no pueden ser en el pasado o muy cercanas a la hora actual.";
        }

        // B. Horario laboral (Ej: 6:00 a 22:00)
        java.time.LocalTime horaInicio = java.time.LocalTime.of(6, 0);
        java.time.LocalTime horaFin = java.time.LocalTime.of(22, 0);

        // Si es antes del inicio O después del final
        if (citaLocalTime.isBefore(horaInicio) || citaLocalTime.isAfter(horaFin)) {
            return "❌ La hora de la cita debe estar entre las 06:00 y las 22:00.";
        }

        // C. No puede ser en domingo
        if (citaLocalDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return "❌ No se pueden programar citas en día Domingo.";
        }

        return null;
    }

    /**
     * Método auxiliar para mapear un ResultSet a un objeto Cita.
     * **NOTA**: Este método espera todos los campos definidos en SQL_BASE_SELECT.
     */
    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();

        try {
            c.setIdCita(rs.getInt("idCita"));
            c.setIdCliente(rs.getInt("idCliente"));
            c.setIdVeterinario(rs.getInt("idVeterinario"));
            c.setFecha(rs.getDate("fecha"));
            c.setHora(rs.getTime("hora"));
            c.setMotivo(rs.getString("motivo"));
            c.setIdEstado(rs.getInt("idEstado"));

            // Datos del JOIN
            c.setEstadoNombre(rs.getString("tipoEstado"));
            c.setPrecio(rs.getDouble("precio"));
            c.setNombreVeterinario(rs.getString("nombreVeterinario"));
            c.setApellidoVeterinario(rs.getString("apellidoVeterinario"));

            // Campos opcionales (datos de cliente)
            try {
                c.setNombreCliente(rs.getString("nombreCliente"));
                c.setApellidoCliente(rs.getString("apellidoCliente"));
                c.setDniCliente(rs.getString("dniCliente"));
            } catch (SQLException ex) {
                // Se ignora si las columnas de cliente no están en este ResultSet (aunque deberían estarlo para SQL_BASE_SELECT)
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR FATAL en mapearCita. Revise si las columnas SQL coinciden con el mapeo.", e);
            throw e;
        }
        return c;
    }

    // --- MÉTODOS CRUD PRINCIPALES (No modificados) ---

    /**
     * Agrega una nueva cita con validaciones.
     * @return String con mensaje de éxito (✅) o error (❌).
     */
    public String agregarCita(Cita cita) {

        // 1. LLAMAR A LA VALIDACIÓN E INTERRUMPIR SI FALLA
        String validacionError = validarFechaYHora(cita);
        if (validacionError != null) {
            LOGGER.log(Level.WARNING, "❌ Validación de cita falló: {0}", validacionError);
            return validacionError;
        }

        int idEstadoPendiente;
        try {
            // Asume que la cita del cliente o la nueva siempre inician como "Pendiente"
            idEstadoPendiente = obtenerIdEstadoPorNombre("Pendiente");
        } catch (SQLException e) {
              LOGGER.log(Level.SEVERE, "❌ ERROR: No se pudo obtener el ID para el estado 'Pendiente'.", e);
              return "❌ Error: No se pudo verificar el estado de la cita. Contacte al administrador.";
        }

        // 4. SQL de Inserción
        String sql = "INSERT INTO citas (idCliente, idVeterinario, fecha, hora, motivo, precio, idEstado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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
            ps.setDouble(6, cita.getPrecio());
            ps.setInt(7, idEstadoPendiente); // idEstado

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita agregada. ID Cliente: {0}, Fecha: {1}", new Object[]{cita.getIdCliente(), cita.getFecha()});
                return "✅ Cita registrada con éxito. Recibirás una confirmación pronto.";
            } else {
                LOGGER.log(Level.WARNING, "❌ Fallo: La inserción devolvió 0 filas afectadas.");
                return "❌ Fallo al registrar: La base de datos no insertó el registro.";
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al agregar cita.", e);
            return "❌ Error en la base de datos: La cita no pudo ser registrada (Verifique IDs o formato).";
        }
    }

    /**
     * Actualiza una cita existente (usada desde Recepción/Admin).
     * @return true si la actualización fue exitosa.
     */
    public boolean actualizarCita(Cita cita) {
        String sql = "UPDATE citas SET idCliente=?, idVeterinario=?, fecha=?, hora=?, motivo=?, idEstado=?, precio=? WHERE idCita=?";

        try {
            // 🟢 PASO CLAVE 1: Obtener el ID del estado a partir del nombre
            int idEstado = obtenerIdEstadoPorNombre(cita.getEstadoNombre());
            cita.setIdEstado(idEstado); // Asigna el ID para consistencia interna si es necesario

            // ⚠️ VALIDACIÓN MEJORADA: Solo validar fecha/hora si el estado es Pendiente.
            // Asumiendo que el estado 1 es "Pendiente" (o si quieres validar todos menos Cancelado/Completado)
            // Usaremos el ID en lugar del nombre para mayor precisión si el método lo usa

            // Asumimos: 1=Pendiente, 2=Completado, 3=Cancelado
            if (idEstado == 1) { 
                String validacionError = validarFechaYHora(cita);
                if (validacionError != null) {
                    LOGGER.log(Level.WARNING, "❌ No se puede actualizar cita ID {0} debido a: {1}", new Object[]{cita.getIdCita(), validacionError});
                    // Podrías lanzar una excepción o retornar false. Retornar false es más simple.
                    return false; 
                }
            } 
            // Nota: Si cambias de Completado/Cancelado a Pendiente, la validación se aplica.


            try (Connection con = Conexion.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                // Asignación de parámetros
                ps.setInt(1, cita.getIdCliente());
                ps.setInt(2, cita.getIdVeterinario());
                ps.setDate(3, cita.getFecha());
                ps.setTime(4, cita.getHora());
                ps.setString(5, cita.getMotivo());

                ps.setInt(6, idEstado); // ID ya convertido
                ps.setDouble(7, cita.getPrecio());
                ps.setInt(8, cita.getIdCita()); // Condición WHERE

                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error al actualizar cita. Causa: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Elimina físicamente una cita.
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM citas WHERE idCita = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la cita ID: " + id, e);
            return false;
        }
    }

    // --- MÉTODOS DE BÚSQUEDA Y LISTADO (Usan SQL_BASE_SELECT) ---

    /**
     * Lista todas las citas, incluyendo todos los datos de cliente y veterinario.
     */
    public List<Cita> listarCitas() { // <--- MÉTODO PARA LISTAR TODAS LAS CITAS (Global)
        List<Cita> lista = new ArrayList<>();

        String sql = SQL_BASE_SELECT + "ORDER BY c.fecha DESC, c.hora DESC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL o JDBC al listar TODAS las citas para Recepción.", e);
        }
        return lista;
    }

    /**
     * ✅ NUEVO: Lista solo las citas con estado "Pendiente" (Global).
     * Requiere que el ID del estado "Pendiente" sea 1.
     */
    public List<Cita> listarCitasPendientes() {
        List<Cita> lista = new ArrayList<>();

        // Asumiendo que idEstado = 1 es "Pendiente"
        String sql = SQL_BASE_SELECT + "WHERE c.idEstado = 1 ORDER BY c.fecha ASC, c.hora ASC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al listar citas PENDIENTES.", e);
        }
        return lista;
    }

    /**
     * Lista todas las citas de un cliente específico (usado en Recepción/Admin).
     */
    public List<Cita> listarCitasPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();

        String sql = SQL_BASE_SELECT
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
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL o JDBC al listar citas por cliente.", e);
        }
        return lista;
    }

    /**
     * ✅ NUEVO: Lista solo las citas con estado "Pendiente" para un cliente específico (usado en MisCitas.jsp).
     * Requiere que el ID del estado "Pendiente" sea 1.
     */
    public List<Cita> listarCitasPendientesPorCliente(int idCliente) {
        List<Cita> lista = new ArrayList<>();

        String sql = SQL_BASE_SELECT
                     + "WHERE c.idCliente = ? AND c.idEstado = 1 " // ⚠️ Condición de filtro por Pendiente
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
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al listar citas PENDIENTES por cliente.", e);
        }
        return lista;
    }


    /**
     * Obtiene una cita por su ID.
     */
    public Cita obtenerCitaPorId(int id) {
        Cita cita = null;

        String sql = SQL_BASE_SELECT + "WHERE c.idCita = ?";

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
     * Busca citas por cliente, veterinario o motivo.
     */
    public List<Cita> buscarCitas(String termino) {
        List<Cita> lista = new ArrayList<>();

        String param = "%" + termino.toLowerCase() + "%";

        String sql = SQL_BASE_SELECT
                     + "WHERE LOWER(cl.nombre) LIKE ? OR LOWER(cl.apellido) LIKE ? "
                     + "OR LOWER(v.nombreVeterinario) LIKE ? OR LOWER(v.apellidoVeterinario) LIKE ? "
                     + "OR LOWER(c.motivo) LIKE ? "
                     + "ORDER BY c.fecha DESC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Asignar el parámetro a TODAS las condiciones LIKE
            ps.setString(1, param); // cl.nombre
            ps.setString(2, param); // cl.apellido
            ps.setString(3, param); // v.nombreVeterinario
            ps.setString(4, param); // v.apellidoVeterinario
            ps.setString(5, param); // c.motivo

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Error SQL al buscar citas.", e);
        }
        return lista;
    }

    // --- MÉTODOS OBSOLETOS/DEPRECADOS (No modificados) ---

    /**
     * ⚠️ ELIMINADO en el código final. Usar 'eliminar(int id)' o 'cancelarCita(int id)'.
     */
    public int eliminarCita(int id) {
        String sql = "{CALL sp_eliminar_cita(?, ?)}";
        int resultado = -2; // Valor de error

        try (Connection con = Conexion.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(2);

        } catch (SQLException e) {
             LOGGER.log(Level.SEVERE, "Error al eliminar cita mediante SP.", e);
        }
        return resultado;
    }

    // --- MÉTODOS DE ESTADO (No modificados) ---

    /**
     * Cambia el estado de una cita de 'Pendiente' (1) a 'Cancelada' (3).
     */
    public boolean cancelarCita(int idCita) {
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CANCELAR_CITA)) {

            ps.setInt(1, ID_ESTADO_CANCELADA);
            ps.setInt(2, idCita);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                LOGGER.log(Level.INFO, "✅ Éxito: Cita ID {0} cancelada.", idCita);
            } else {
                // Puede ser 0 si la cita ya estaba en otro estado que no fuera 'Pendiente' (1)
                LOGGER.log(Level.WARNING, "⚠️ Advertencia: No se pudo cancelar la cita ID {0}. Posiblemente ya no estaba Pendiente.", idCita);
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al intentar cancelar cita ID " + idCita, e);
            return false;
        }
    }
    
    // --- visualizacion en vista administrador---
    
    // --- IMPLEMENTACIÓN DEL REPORTE DE GANANCIAS ---

    /**
     * ✅ CORREGIDO: Lista las citas con estado "Completado" (2) para un mes y año específicos.
     * @param mes El mes a filtrar (1-12).
     * @param anio El año a filtrar.
     * @return Lista de objetos Cita con los datos simplificados.
     */
    public List<Cita> listarCitasCompletadasPorMesYAnio(int mes, int anio) throws SQLException{
        List<Cita> listaCitas = new ArrayList<>();

        // ⚠️ CORRECCIÓN CLAVE EN EL SQL: Usamos MONTH() y YEAR() con dos placeholders (?)
        String sql = SQL_SELECT_REPORT_BASE
                + " WHERE c.idEstado = 2 AND MONTH(c.fecha) = ? AND YEAR(c.fecha) = ? "
                + " ORDER BY c.fecha ASC, c.hora ASC";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // ⚠️ CORRECCIÓN DE PARÁMETROS:
            // 1. Asignamos el MES al primer '?' (MONTH(c.fecha) = ?)
            ps.setInt(1, mes);
            // 2. Asignamos el AÑO al segundo '?' (YEAR(c.fecha) = ?)
            ps.setInt(2, anio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapeo específico para el reporte
                    Cita cita = new Cita();
                    
                    // Campos de Cita (Precio, Fecha, Hora)
                    cita.setIdCita(rs.getInt("idCita"));
                    cita.setFecha(rs.getDate("fecha"));
                    cita.setHora(rs.getTime("hora"));
                    cita.setPrecio(rs.getDouble("precio"));
                    
                    // ⚠️ NOTA: El campo 'idEstado' NO se selecciona en SQL_SELECT_REPORT_BASE, 
                    // por lo que esta línea puede causar un error de columna no encontrada o devolver 0.
                    // La he comentado, ya que el reporte solo busca citas con idEstado=2.
                    // cita.setIdEstado(rs.getInt("idEstado")); 
                    
                    // Datos del JOIN
                    cita.setEstadoNombre(rs.getString("estadoNombre")); 
                    
                    // Cliente
                    cita.setNombreCliente(rs.getString("nombreCliente"));
                    cita.setApellidoCliente(rs.getString("apellidoCliente"));
                    cita.setDniCliente(rs.getString("dniCliente"));
                    
                    // Veterinario
                    cita.setNombreVeterinario(rs.getString("nombreVeterinario"));
                    cita.setApellidoVeterinario(rs.getString("apellidoVeterinario"));
                    
                    listaCitas.add(cita);
                }
            }

        } catch (SQLException e) {
            // Es buena práctica lanzar una excepción para que el Servlet sepa que falló el DAO.
            LOGGER.log(Level.SEVERE, "❌ ERROR SQL al listar citas completadas por mes y año. Mes: " + mes + ", Año: " + anio, e);
            throw new RuntimeException("Error en la BD al generar el reporte de ganancias.", e);
        }
        return listaCitas;
    }
}
