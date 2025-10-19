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
                eliminarCita(request, response);
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
            request.setAttribute("listaCitas", misCitas); 
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

                // Se asume que redirigirás a una página JSP para ver/editar la cita, 
                // por lo que se debe usar forward.
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
        cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
        cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));

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
        // ❌ CORRECCIÓN CRÍTICA: setEstado se cambia por setEstadoNombre
        cita.setEstadoNombre(request.getParameter("estado")); 

        boolean operacionExitosa = citaDAO.agregarCita(cita);
        if (operacionExitosa) {
            request.getSession().setAttribute("mensaje", "✅ Cita agregada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al agregar la cita. Verifique IDs o conexión.");
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

        cita.setIdCita(Integer.parseInt(idCitaStr));
        cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
        cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));

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
        // ❌ CORRECCIÓN CRÍTICA: setEstado se cambia por setEstadoNombre
        cita.setEstadoNombre(request.getParameter("estado"));

        boolean operacionExitosa = citaDAO.actualizarCita(cita);
        if (operacionExitosa) {
            request.getSession().setAttribute("mensaje", "✅ Cita actualizada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al actualizar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/CitaServlet");
    }

    private void crearCitaDesdeCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
        cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));

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
            response.sendRedirect(request.getContextPath() + "/ClienteRServlet");  // Asumiendo que ClienteRServlet es la vista de gestión
            return;
        }
        cita.setMotivo(request.getParameter("motivo"));
        
        // ❌ CORRECCIÓN CRÍTICA: setEstado se cambia por setEstadoNombre
        // Se establece el estado inicial por defecto ("Pendiente").
        cita.setEstadoNombre("Pendiente"); 

        boolean agregada = citaDAO.agregarCita(cita);
        if (agregada) {
            request.getSession().setAttribute("mensaje", "✅ Cita creada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al crear la cita. Verifique IDs o conexión.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/ClienteRServlet"); 
    }

    private void eliminarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idCita = Integer.parseInt(request.getParameter("id"));
        int resultado = citaDAO.eliminarCita(idCita);

        switch (resultado) {
            case 1:
                request.getSession().setAttribute("mensaje", "✅ Cita eliminada con éxito.");
                request.getSession().setAttribute("tipoMensaje", "exito"); 
                break;
            case -1:
                request.getSession().setAttribute("mensaje", "⚠️ No se encontró la cita a eliminar.");
                request.getSession().setAttribute("tipoMensaje", "advertencia");
                break;
            case 0:
            case -2:
            default:
                request.getSession().setAttribute("mensaje", "❌ Error al eliminar la cita.");
                request.getSession().setAttribute("tipoMensaje", "error");
                break;
        }
        response.sendRedirect(request.getContextPath() + "/CitaServlet"); 
    }

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