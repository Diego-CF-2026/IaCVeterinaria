package Controladores;

// =================================================================
// 🚀 Importaciones de Jakarta EE (Servlets)
// =================================================================
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// =================================================================
// 🛠️ Importaciones de Java Utilities y Excepciones
// =================================================================
import java.io.IOException;
import java.io.UnsupportedEncodingException; // 🛑 CRÍTICA para el método URLEncoder
import java.text.SimpleDateFormat;
import java.text.ParseException; // Para manejo de errores de formato de fecha
import java.util.List;
import java.util.ArrayList; // Para inicializar listas vacías en caso de error
import java.util.logging.Level;
import java.util.logging.Logger;

// =================================================================
// 📅 Importaciones de Java SQL y Time
// =================================================================
import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeParseException; // Para manejo de errores de formato de tiempo (aunque SimpleDateFormat maneja ParseException)

// =================================================================
// 🧬 Importaciones de Modelos (Entidades)
// =================================================================
import Modelo.Cita;
import Modelo.Usuario; // Entidad de sesión
import Modelo.Cliente; // Entidad de datos de cliente
import Modelo.Especialidad; // Entidad para el formulario

// =================================================================
// 🗄️ Importaciones de DAOs (Acceso a Datos)
// =================================================================
import ModeloDAO.CitaDAO;
import ModeloDAO.VeterinarioDAO; // Aunque no se usa directamente en GET/POST, es bueno tenerlo
import ModeloDAO.ClienteDAO;
import ModeloDAO.EspecialidadDAO;

/**
 * Servlet principal para la gestión de citas por parte del cliente.
 * * 1. GET: Carga el formulario de solicitud de citas (listas de Especialidades).
 * 2. POST: Procesa el registro de una nueva cita.
 * * Mapeo: /UsuarioCitasServlet
 */
@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasServlet.class.getName());
    
    // Instancias de los DAOs para interactuar con la base de datos
    private final CitaDAO citaDAO = new CitaDAO();
    private final VeterinarioDAO vetDAO = new VeterinarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    // Objetos SimpleDateFormat para la conversión segura y estricta de fechas/horas
    private static final SimpleDateFormat SDF_FECHA = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_HORA = new SimpleDateFormat("HH:mm");

    // =================================================================
    // 🌍 MÉTODO GET: Cargar el Formulario de Citas
    // =================================================================
    /**
     * Prepara y envía los datos necesarios para mostrar la vista del formulario de reserva de citas.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        // 1. Obtener usuario logueado desde la sesión
        HttpSession session = request.getSession();
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        // 🛑 **SEGURIDAD:** Verificar que el usuario esté logueado y que sea un Cliente (ID Rol = 3)
        if (usuarioSesion == null || usuarioSesion.getIdRol() != 3) { 
            // Si falla la verificación, redirigir al index con un mensaje de error de seguridad.
            LOGGER.log(Level.WARNING, "Acceso no autorizado al formulario de citas. Rol: {0}", 
                       (usuarioSesion != null ? usuarioSesion.getIdRol() : "NULO"));
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=acceso_no_autorizado");
            return;
        }

        // 2. Buscar datos del cliente asociado al usuario (Necesario para obtener el idCliente real)
        Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());

        if (cliente == null) {
            // Si el Usuario existe pero no está vinculado a una entidad Cliente (error de datos).
            LOGGER.log(Level.SEVERE, "Usuario logueado ID {0} no tiene entidad Cliente vinculada.", usuarioSesion.getIdUsuario());
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=cliente_no_vinculado");
            return;
        }

        // Adjuntar el objeto Cliente al request (útil para campos ocultos o para mostrar datos del cliente).
        request.setAttribute("cliente", cliente);

        // 3. Obtener lista de ESPECIALIDADES (datos dinámicos para el formulario)
        try {
            List<Especialidad> listaEspecialidades = especialidadDAO.vistaClienteListarEspecialidades();
            request.setAttribute("listaEspecialidades", listaEspecialidades);
        } catch (RuntimeException e) {
            // Manejo de errores específicos del DAO (ej. problemas con la conexión JDBC)
            LOGGER.log(Level.SEVERE, "❌ Error al listar especialidades desde el DAO.", e);
            request.setAttribute("listaEspecialidades", new ArrayList<Especialidad>()); // Enviar lista vacía para no romper el JSP
        } catch (Exception e) {
            // Manejo de cualquier otra excepción inesperada
            LOGGER.log(Level.SEVERE, "❌ Error inesperado al cargar listas.", e);
            request.setAttribute("listaEspecialidades", new ArrayList<Especialidad>()); // Enviar lista vacía
        }

        // 4. Leer mensajes de estado de la URL
        // Este parámetro es usado después de un POST (patrón PRG - Post/Redirect/Get)
        String mensaje = request.getParameter("mensaje");
        request.setAttribute("mensaje", mensaje);

        // 5. Redirigir al JSP (solo formulario, no hay lista de citas aquí)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp"); 
        dispatcher.forward(request, response);
    }

    // =================================================================
    // 💾 MÉTODO POST: Procesar Registro de Nueva Cita
    // =================================================================
    /**
     * Maneja el envío del formulario para crear una nueva cita en la base de datos.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            // 🛑 CRÍTICO: Se añade UnsupportedEncodingException al throws para usar URLEncoder.encode
            throws ServletException, IOException, UnsupportedEncodingException { 
        
        // Obtener el parámetro 'accion' para saber qué proceso ejecutar (solo hay 'registrar' por ahora)
        String accion = request.getParameter("accion");

        if ("registrar".equalsIgnoreCase(accion)) {
            
            HttpSession session = request.getSession();
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

            // 🛑 **SEGURIDAD (Repetición):** Doble verificación de sesión y rol antes de procesar datos
            if (usuarioSesion == null || usuarioSesion.getIdRol() != 3) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=no_sesion_cliente");
                return;
            }
            
            String mensaje = "❌ Error general del sistema."; // Mensaje por defecto en caso de fallo no capturado

            try {
                // 1. Obtener el idCliente de forma segura desde la BD
                Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());

                if (cliente == null || cliente.getIdCliente() == 0) {
                    // Falla de integridad de datos
                    LOGGER.log(Level.WARNING, "❌ No se encontró el cliente asociado al usuario ID: {0}", usuarioSesion.getIdUsuario());
                    mensaje = "❌ Error: Cliente no encontrado o no vinculado.";
                    response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(mensaje, "UTF-8"));
                    return;
                }
                
                int idClienteSeguro = cliente.getIdCliente();

                // 2. Obtención y validación de parámetros del formulario
                String idVeterinarioStr = request.getParameter("idVeterinario");
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String motivo = request.getParameter("motivo");
                String precioStr = request.getParameter("precio");
                
                // Validación básica de campos obligatorios
                if (idVeterinarioStr == null || fechaStr == null || horaStr == null || motivo == null || motivo.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                    mensaje = "❌ Error: Todos los campos son obligatorios.";
                    response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(mensaje, "UTF-8"));
                    return;
                }
                
                // 3. Parseo de datos (puede lanzar NumberFormatException o ParseException)
                int idVeterinario = Integer.parseInt(idVeterinarioStr);
                double precio = Double.parseDouble(precioStr);

                // Conversión de String a java.util.Date/Time
                java.util.Date fechaUtil = SDF_FECHA.parse(fechaStr);
                java.util.Date horaUtil = SDF_HORA.parse(horaStr);

                // Conversión final a los tipos requeridos por SQL
                Date fechaSQL = new Date(fechaUtil.getTime());
                Time horaSQL = new Time(horaUtil.getTime());

                // 4. Creación del objeto Cita
                Cita cita = new Cita();
                cita.setIdCliente(idClienteSeguro); // Usar el ID seguro de la BD
                cita.setIdVeterinario(idVeterinario);
                cita.setFecha(fechaSQL);
                cita.setHora(horaSQL);
                cita.setMotivo(motivo);
                cita.setPrecio(precio);

                // 5. Inserción y CAPTURA DEL MENSAJE DEL DAO
                // El método agregarCita en el DAO debe devolver un String con el mensaje de éxito/error/conflicto (ej. "Cita registrada" o "Error: Hora no disponible").
                String resultadoDAO = citaDAO.agregarCita(cita); 

                // Redirigir con el mensaje exacto del DAO (Patrón PRG)
                response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(resultadoDAO, "UTF-8"));
                return;
                
            } catch (DateTimeParseException | ParseException e) {
                // Captura errores de conversión de fecha/hora (p. ej., formato incorrecto)
                LOGGER.log(Level.SEVERE, "❌ Error al registrar cita (Error de formato de Fecha/Hora).", e);
                mensaje = "❌ Error de formato: Asegúrate de que la fecha y hora sean válidas (yyyy-MM-dd / HH:mm).";
            } catch (NumberFormatException e) {
                // Captura errores de conversión de números (idVeterinario o precio no válidos)
                LOGGER.log(Level.SEVERE, "❌ Error al registrar cita (ID Veterinario o Precio no válido).", e);
                mensaje = "❌ Error de formato: El veterinario o el precio tienen un formato inválido.";
            } catch (Exception e) {
                // Captura cualquier otro error (ej. error de DB, error de I/O)
                LOGGER.log(Level.SEVERE, "❌ Error general al registrar cita.", e);
            }
            
            // Si el flujo de ejecución llega aquí (después de un catch), redirige con el mensaje de error capturado
            response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(mensaje, "UTF-8"));
        } else {
            // Si la acción no es "registrar" o es inválida, redirige al formulario principal.
            response.sendRedirect("UsuarioCitasServlet");
        }
    }
}