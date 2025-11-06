package Controladores;

import Modelo.Cita;
import Modelo.Cliente;
import Modelo.Veterinario;
import ModeloDAO.CitaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CitaServlet.class.getName());
    // Se mantiene la instancia de DAO
    CitaDAO citaDAO = new CitaDAO();

    // Formateadores para parsear y formatear Date y Time
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarCitasPorCliente(request, response);
                break;
            case "ver":
                verCita(request, response);
                break;
            case "eliminar":
                // ⚠️ ESTE MÉTODO AHORA CANCELA LA CITA POR CAMBIO DE ESTADO
                cancelarCita(request, response); 
                break;
            case "verCitasCliente": // Usado por el rol Recepcionista
                verCitasCliente(request, response);
                break;
            default:
                listarCitasPorCliente(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
            return;
        }

        switch (accion) {
            case "guardar":
                guardarCita(request, response);
                break;
            case "actualizar":
                actualizarCita(request, response);
                break;
            case "crearCita":
                crearCitaDesdeCliente(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/CitaServlet");
                break;
        }
    }

    /**
     * Muestra la lista de citas para el cliente logueado.
     */
    private void listarCitasPorCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idCliente = null;
        jakarta.servlet.http.HttpSession sesion = request.getSession(false);

        // 1. OBTENER Y CASTELLAR ID DE SESIÓN
        if (sesion != null) {
            Object idSesion = sesion.getAttribute("idClienteSesion");

            if (idSesion != null) {
                if (idSesion instanceof Integer) {
                    idCliente = (Integer) idSesion;
                } else {
                    try {
                        idCliente = Integer.parseInt(idSesion.toString());
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.SEVERE, "ID de sesión inválido para listar citas. Valor: " + idSesion, e);
                    }
                }
            }
        }

        // 2. MANEJAR CASO SIN ID DE CLIENTE
        if (idCliente == null || idCliente <= 0) {
            LOGGER.log(Level.WARNING, "ID de Cliente no disponible o inválido en sesión.");
            request.getSession().setAttribute("mensaje", "⚠️ Debes iniciar sesión para ver tus citas.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // 3. LLAMADA AL DAO
            List<Cita> misCitas = citaDAO.listarCitasPorCliente(idCliente);
            
            if (misCitas.isEmpty()) {
                request.setAttribute("avisoCitas", "Aún no tienes citas agendadas.");
            } else {
                 LOGGER.log(Level.INFO, "Citas cargadas con éxito para el ID: " + idCliente + ". Total: " + misCitas.size());
            }

            // 4. COLOCAR DATOS en el request y hacer el forward
            request.setAttribute("misCitas", misCitas); // ⚠️ CAMBIO: Usamos 'misCitas' para coincidir con el JSP
            String urlVista = "/VistasWeb/VistasCliente/MisCitas.jsp";

            request.getRequestDispatcher(urlVista).forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fatal al cargar citas para ID: " + idCliente, e);
            request.setAttribute("errorMensaje", "Ocurrió un error en el servidor al cargar tus citas.");
            request.getRequestDispatcher("/VistasWeb/error.jsp").forward(request, response);
        }
    }
    
    // --- Métodos de CRUD y Auxiliares ---

    private void verCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int idCita = Integer.parseInt(idStr);
                Cita citaSeleccionada = citaDAO.obtenerCitaPorId(idCita);
                request.setAttribute("citaSeleccionada", citaSeleccionada);

                request.setAttribute("listaClientes", citaDAO.listarClientesParaDropdown());
                request.setAttribute("listaVeterinarios", citaDAO.listarVeterinariosParaDropdown());

                request.getRequestDispatcher("/VistasWeb/VistasAdmin/editarCita.jsp").forward(request, response);

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID de cita inválido para ver: " + idStr, e);
                response.sendRedirect(request.getContextPath() + "/CitaServlet");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
        }
    }

    private void guardarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        
        // Asume que los IDs ya vienen validados
        try {
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
        } catch (NumberFormatException e) {
             request.getSession().setAttribute("mensaje", "❌ Error de formato: ID de Cliente o Veterinario inválido.");
             request.getSession().setAttribute("tipoMensaje", "error");
             response.sendRedirect(request.getContextPath() + "/CitaServlet");
             return;
        }


        String fechaStr = request.getParameter("fecha");
        String horaStr = request.getParameter("hora");

        try {
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));

            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear fecha/hora al agregar la cita: " + e.getMessage(), e);
            request.getSession().setAttribute("mensaje", "❌ Error en el formato de fecha/hora al agregar.");
            request.getSession().setAttribute("tipoMensaje", "error");
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
            return;
        }

        cita.setMotivo(request.getParameter("motivo"));
        cita.setEstadoNombre(request.getParameter("estado"));
        
        // 🛑 CORRECCIÓN: Capturar el String del DAO
        String resultadoDAO = citaDAO.agregarCita(cita); 
        
        // 🛑 CORRECCIÓN: Evaluar el String
        if (resultadoDAO.startsWith("✅")) {
            request.getSession().setAttribute("mensaje", resultadoDAO);
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", resultadoDAO); // Mostrar el mensaje de error de validación (Domingo, horario, etc.)
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/CitaServlet");
    }

    private void actualizarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        String idCitaStr = request.getParameter("idCita");

        if (idCitaStr == null || idCitaStr.isEmpty()) {
            request.getSession().setAttribute("mensaje", "❌ ID de cita no proporcionado para la actualización.");
            request.getSession().setAttribute("tipoMensaje", "error");
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
            return;
        }

        try {
            cita.setIdCita(Integer.parseInt(idCitaStr));
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
        } catch (NumberFormatException e) {
             request.getSession().setAttribute("mensaje", "❌ Error de formato en los ID.");
             request.getSession().setAttribute("tipoMensaje", "error");
             response.sendRedirect(request.getContextPath() + "/CitaServlet");
             return;
        }


        String fechaStr = request.getParameter("fecha");
        String horaStr = request.getParameter("hora");

        try {
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));

            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear fecha/hora al actualizar la cita: " + e.getMessage(), e);
            request.getSession().setAttribute("mensaje", "❌ Error en el formato de fecha/hora al actualizar.");
            request.getSession().setAttribute("tipoMensaje", "error");
            response.sendRedirect(request.getContextPath() + "/CitaServlet"); // Redirigir a una vista que maneje el error
            return;
        }

        cita.setMotivo(request.getParameter("motivo"));
        cita.setEstadoNombre(request.getParameter("estado"));
        
        // NOTA: Se mantiene la lógica booleana aquí ya que actualizarCita() en el DAO
        // generalmente devuelve boolean, no tiene las validaciones complejas de agregarCita.
        boolean operacionExitosa = citaDAO.actualizarCita(cita); 
        
        if (operacionExitosa) {
            request.getSession().setAttribute("mensaje", "✅ Cita actualizada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al actualizar la cita. Verifique IDs, estado o conexión.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/CitaServlet");
    }

    private void crearCitaDesdeCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        
        try {
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
        } catch (NumberFormatException e) {
             request.getSession().setAttribute("mensaje", "❌ Error de formato en los ID de Cliente o Veterinario.");
             request.getSession().setAttribute("tipoMensaje", "error");
             response.sendRedirect(request.getContextPath() + "/ClienteRServlet");
             return;
        }


        String fechaStr = request.getParameter("fecha");
        String horaStr = request.getParameter("hora");

        try {
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));

            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));

        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error al parsear fecha/hora de la cita desde modal Cliente: " + e.getMessage(), e);
            request.getSession().setAttribute("mensaje", "❌ Error en el formato de fecha/hora de la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
            response.sendRedirect(request.getContextPath() + "/ClienteRServlet");
            return;
        }
        cita.setMotivo(request.getParameter("motivo"));
        
        // Se establece el estado inicial por defecto ("Pendiente").
        cita.setEstadoNombre("Pendiente");

        // 🛑 CORRECCIÓN: Capturar el String del DAO
        String resultadoDAO = citaDAO.agregarCita(cita); 
        
        // 🛑 CORRECCIÓN: Evaluar el String
        if (resultadoDAO.startsWith("✅")) {
            request.getSession().setAttribute("mensaje", resultadoDAO);
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", resultadoDAO); // Mostrar el mensaje de error de validación (Domingo, horario, etc.)
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteRServlet");
    }

    /**
     * MÉTODO DE CANCELACIÓN (Reemplaza al antiguo 'eliminarCita')
     * Actualiza el estado de la cita a 'Cancelada' en lugar de eliminar el registro.
     */
    private void cancelarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idCitaStr = request.getParameter("id");
        int idCita;

        try {
            idCita = Integer.parseInt(idCitaStr);
            
            // ⚠️ Llama al método del DAO que debe actualizar el idEstado a CANCELADA (ej. ID 3)
            boolean exito = citaDAO.cancelarCita(idCita); 
            
            // 2. Prepara el mensaje de respuesta
            if (exito) {
                request.getSession().setAttribute("mensaje", "✅ La cita N° " + idCita + " ha sido cancelada correctamente.");
                request.getSession().setAttribute("tipoMensaje", "exito");
            } else {
                // Esto puede ocurrir si la cita no existía, o si el DAO falló, o si ya no estaba "Pendiente".
                request.getSession().setAttribute("mensaje", "⚠️ No se pudo cancelar la cita N° " + idCita + ". Verifique su estado (solo se cancelan las citas 'Pendiente').");
                request.getSession().setAttribute("tipoMensaje", "advertencia");
            }
            
        } catch (NumberFormatException e) {
            // Manejo de error si el ID no es un número
            LOGGER.log(Level.WARNING, "ID de cita inválido para cancelar: " + idCitaStr, e);
            request.getSession().setAttribute("mensaje", "❌ Error: ID de cita no válido.");
            request.getSession().setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            // Manejo de errores generales del DAO o DB
            LOGGER.log(Level.SEVERE, "Error al procesar la cancelación de cita ID: " + idCitaStr, e);
            request.getSession().setAttribute("mensaje", "❌ Error interno al procesar la cancelación.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        
        // 3. Redireccionar de vuelta a la lista de citas del cliente
        // Podríamos redirigir a donde vino el usuario, pero volvemos al listado principal por defecto.
        response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
    }
    
    // El método 'verCitasCliente' se deja sin cambios.
    private void verCitasCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idClienteStr = request.getParameter("idCliente");
        String acceso = "/VistasWeb/VistasRecep/CitasCliente.jsp";

        try {
            if (idClienteStr != null && !idClienteStr.isEmpty()) {
                int idCliente = Integer.parseInt(idClienteStr.trim());
                
                List<Cita> citas = citaDAO.listarCitasPorCliente(idCliente);
                
                request.setAttribute("citasCliente", citas);
                request.setAttribute("idClienteSeleccionado", idCliente);
                
                // Muestra el nombre del cliente si hay citas
                if (!citas.isEmpty()) {
                    // El DAO ya carga el nombre y apellido en el modelo Cita
                    request.setAttribute("nombreCliente", citas.get(0).getNombreCliente() + " " + citas.get(0).getApellidoCliente());
                } else {
                    request.setAttribute("nombreCliente", "Cliente (ID: " + idCliente + ")");
                }
            } else {
                request.setAttribute("error", "ID de Cliente no proporcionado.");
                acceso = "/VistasWeb/VistasRecep/GestionCitas.jsp";
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de Cliente inválido.");
            acceso = "/VistasWeb/VistasRecep/GestionCitas.jsp";
        }
        
        request.getRequestDispatcher(acceso).forward(request, response);
    }
}