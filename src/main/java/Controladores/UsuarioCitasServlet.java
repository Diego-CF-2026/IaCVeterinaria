package Controladores;

// Importaciones de Servlets (Jakarta EE)
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Importaciones de Java Util
import java.io.IOException;
import java.io.UnsupportedEncodingException; // 🛑 NECESARIA para URLEncoder
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList; 
import java.util.logging.Level;
import java.util.logging.Logger;

// Importaciones de Java SQL y Time
import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeParseException;

// Importaciones de tus Modelos
import Modelo.Cita;
import Modelo.Usuario;
import Modelo.Cliente;
import Modelo.Especialidad; 

// Importaciones de tus DAOs
import ModeloDAO.CitaDAO;
import ModeloDAO.VeterinarioDAO;
import ModeloDAO.ClienteDAO;
import ModeloDAO.EspecialidadDAO;

@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasServlet.class.getName());
    private final CitaDAO citaDAO = new CitaDAO();
    private final VeterinarioDAO vetDAO = new VeterinarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    private static final SimpleDateFormat SDF_FECHA = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_HORA = new SimpleDateFormat("HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        // 1. Obtener usuario logueado desde la sesión
        HttpSession session = request.getSession();
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion == null || usuarioSesion.getIdRol() != 3) { // Seguridad: debe ser Cliente (Rol 3)
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=acceso_no_autorizado");
            return;
        }

        // 2. Buscar datos del cliente asociado al usuario
        Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());

        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=cliente_no_vinculado");
            return;
        }

        request.setAttribute("cliente", cliente);

        // 3. Obtener lista de ESPECIALIDADES 
        try {
            List<Especialidad> listaEspecialidades = especialidadDAO.vistaClienteListarEspecialidades();
            request.setAttribute("listaEspecialidades", listaEspecialidades);
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "❌ Error al listar especialidades desde el DAO.", e);
            request.setAttribute("listaEspecialidades", new ArrayList<Especialidad>()); 
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ Error inesperado al cargar listas.", e);
            request.setAttribute("listaEspecialidades", new ArrayList<Especialidad>()); 
        }

        // 4. Mensajes
        String mensaje = request.getParameter("mensaje");
        request.setAttribute("mensaje", mensaje);

        // 5. Redirigir al JSP (solo formulario)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp"); 
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            // 🛑 CORRECCIÓN DEL ERROR DE COMPILACIÓN
            throws ServletException, IOException, UnsupportedEncodingException { 
        
        String accion = request.getParameter("accion");

        if ("registrar".equalsIgnoreCase(accion)) {
            
            HttpSession session = request.getSession();
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

            if (usuarioSesion == null || usuarioSesion.getIdRol() != 3) { // Seguridad
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=no_sesion_cliente");
                return;
            }
            
            String mensaje = "❌ Error general del sistema."; 

            try {
                // 1. Obtener el idCliente de forma segura desde la BD
                Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());

                if (cliente == null || cliente.getIdCliente() == 0) {
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
                
                if (idVeterinarioStr == null || fechaStr == null || horaStr == null || motivo == null || motivo.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                    mensaje = "❌ Error: Todos los campos son obligatorios.";
                    response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(mensaje, "UTF-8"));
                    return;
                }
                
                // 3. Parseo de datos
                int idVeterinario = Integer.parseInt(idVeterinarioStr);
                double precio = Double.parseDouble(precioStr);

                // Convertir a los tipos de SQL
                java.util.Date fechaUtil = SDF_FECHA.parse(fechaStr);
                java.util.Date horaUtil = SDF_HORA.parse(horaStr);

                Date fechaSQL = new Date(fechaUtil.getTime());
                Time horaSQL = new Time(horaUtil.getTime());

                // 4. Creación del objeto Cita
                Cita cita = new Cita();
                cita.setIdCliente(idClienteSeguro);
                cita.setIdVeterinario(idVeterinario);
                cita.setFecha(fechaSQL);
                cita.setHora(horaSQL);
                cita.setMotivo(motivo);
                cita.setPrecio(precio);

                // 5. Inserción y CAPTURA DEL MENSAJE DEL DAO 
                // 💡 EL DAO DEVUELVE EL MENSAJE DE ÉXITO O ERROR (String)
                String resultadoDAO = citaDAO.agregarCita(cita); 

                // Redirigir con el mensaje exacto del DAO
                response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(resultadoDAO, "UTF-8"));
                return;
                
            } catch (DateTimeParseException | ParseException e) {
                LOGGER.log(Level.SEVERE, "❌ Error al registrar cita (Error de formato de Fecha/Hora).", e);
                mensaje = "❌ Error de formato: Asegúrate de que la fecha y hora sean válidas (yyyy-MM-dd / HH:mm).";
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "❌ Error al registrar cita (ID Veterinario o Precio no válido).", e);
                mensaje = "❌ Error de formato: El veterinario o el precio tienen un formato inválido.";
            } catch (Exception e) {
                // Captura cualquier otro error (ej. error de DB)
                LOGGER.log(Level.SEVERE, "❌ Error general al registrar cita.", e);
            }
            
            // Si el flujo llega aquí (por un catch), redirige con el mensaje de error capturado
            response.sendRedirect("UsuarioCitasServlet?mensaje=" + java.net.URLEncoder.encode(mensaje, "UTF-8"));
        } else {
            // Si la acción no es "registrar" o es inválida
            response.sendRedirect("UsuarioCitasServlet");
        }
    }
}