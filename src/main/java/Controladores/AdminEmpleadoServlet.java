package Controladores;

import Modelo.Veterinario;
import Modelo.Recepcionista;
import Modelo.Conexion; // Asegúrate de que tu clase Conexion maneje las conexiones de forma segura.
import ModeloDAO.VeterinarioDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importar HttpSession
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import Modelo.Especialidad;
import ModeloDAO.EspecialidadDAO;
import java.util.Map;
import java.util.HashMap;

@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminEmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminEmpleadoServlet.class.getName());

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty() || "listar".equals(accion)) {
            listarEmpleados(request, response);
        } else {
            switch (accion) {
                case "obtener":
                    obtenerDatosEmpleadoJson(request, response);
                    break;
                case "eliminar": // Se mantiene aquí para el caso de eliminación directa por GET (ej. enlace)
                    eliminarEmpleado(request, response);
                    break;
                case "nuevo":
                    mostrarFormularioNuevo(request, response);
                    break;
                case "obtenerEspecialidad":
                    obtenerEspecialidadJson(request, response);
                    break;

                default:
                    listarEmpleados(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        String tipoEmpleado = request.getParameter("tipoEmpleado"); // Asegúrate de que este parámetro se envíe desde el formulario/modal

        if (accion != null) {
            switch (accion) {
                case "agregar":
                    agregarEmpleado(request, response, tipoEmpleado);
                    break;
                case "actualizar":
                    actualizarEmpleado(request, response, tipoEmpleado);
                    break;

                // ===== NUEVO: especialidades =====
                case "agregarEspecialidad":
                    agregarEspecialidad(request, response);
                    return; // IMPORTANTE: return para no caer al default
                case "actualizarEspecialidad":
                    actualizarEspecialidad(request, response);
                    return;
                case "eliminarEspecialidad":
                    eliminarEspecialidad(request, response);
                    return;

                // Si decides manejar la eliminación por POST (ej. un modal de confirmación con submit POST),
                // la podrías habilitar aquí y eliminar el case "eliminar" de doGet.
                // case "eliminar":
                //     eliminarEmpleado(request, response);
                //     break;
                default:
                    response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
        }
    }

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("query");
        String activeTab = request.getParameter("currentTab");

        // Flash
        HttpSession session = request.getSession();
        String msg = (String) session.getAttribute("mensajeFlash");
        String err = (String) session.getAttribute("errorFlash");
        if (msg != null) {
            request.setAttribute("mensaje", msg);
            session.removeAttribute("mensajeFlash");
        }
        if (err != null) {
            request.setAttribute("error", err);
            session.removeAttribute("errorFlash");
        }

        if (activeTab == null || activeTab.isEmpty()) {
            activeTab = "veterinarios";
        }
        request.setAttribute("activeTab", activeTab);

        // Veterinarios
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        List<Veterinario> all = veterinarioDAO.listarVeterinarios();
        List<Veterinario> filtered = new ArrayList<>();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = searchQuery.trim().toLowerCase();
            for (Veterinario v : all) {
                if ((v.getNombreVeterianrio() != null && v.getNombreVeterianrio().toLowerCase().contains(q))
                        || (v.getApellidoVeterinario() != null && v.getApellidoVeterinario().toLowerCase().contains(q))
                        || (v.getCorreoVeterinario() != null && v.getCorreoVeterinario().toLowerCase().contains(q))
                        || (v.getTelefonoVeterinario() != null && v.getTelefonoVeterinario().toLowerCase().contains(q))) {
                    filtered.add(v);
                }
            }
            request.setAttribute("searchQuery", searchQuery);
        } else {
            filtered = all;
        }
        request.setAttribute("listaVeterinarios", filtered);

        // === ESPECIALIDADES para el <select> y nombre en tabla ===
        EspecialidadDAO espDAO = new EspecialidadDAO();
        List<Especialidad> listaEspecialidades = espDAO.listar();

        LOGGER.info("DEBUG - listaEspecialidades size = " + (listaEspecialidades != null ? listaEspecialidades.size() : -1));

        Map<Integer, String> mapaEspecialidad = new HashMap<>();
        for (Especialidad e : listaEspecialidades) {
            mapaEspecialidad.put(e.getIdEspecialidad(), e.getNombreEspecialidad());
        }
        request.setAttribute("listaEspecialidades", listaEspecialidades);
        request.setAttribute("mapaEspecialidad", mapaEspecialidad);

        // ======================
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
                .forward(request, response);
    }

    private String escapeJsonString(String text) {
        if (text == null) {
            return ""; // Changed from "null" to "" for cleaner JSON in case of null values
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    // Basic ASCII and Latin-1 characters
                    if (c >= ' ' && c <= '~' || (c >= '\u00A0' && c <= '\u00FF')) {
                        sb.append(c);
                    } else {
                        // For other Unicode characters, escape them
                        sb.append(String.format("\\u%04x", (int) c));
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private void obtenerDatosEmpleadoJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String tipo = request.getParameter("tipoEmpleado");
        if (tipo == null || tipo.isEmpty()) {
            tipo = "veterinario";
        }

        int id;
        try {
            String idStr = request.getParameter("idEmpleado");
            if (idStr == null || idStr.isEmpty()) {
                idStr = request.getParameter("idVeterinario");
            }
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID de empleado inválido.\"}");
            return;
        }

        try {
            if ("veterinario".equals(tipo)) {
                VeterinarioDAO dao = new VeterinarioDAO();
                Veterinario vet = dao.obtenerVeterinarioPorId(id);
                if (vet == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Veterinario no encontrado.\"}");
                    return;
                }

                // opcional: nombre de especialidad
                String nomEsp = "";
                try {
                    Especialidad esp = new EspecialidadDAO().obtenerPorId(vet.getIdEspecialidad());
                    if (esp != null) {
                        nomEsp = esp.getNombreEspecialidad();
                    }
                } catch (Exception ignore) {
                }

                String json = new StringBuilder()
                        .append("{")
                        .append("\"idVeterinario\":").append(vet.getIdVeterinario()).append(",")
                        .append("\"nombreVeterinario\":\"").append(escapeJsonString(vet.getNombreVeterianrio())).append("\",")
                        .append("\"apellidoVeterinario\":\"").append(escapeJsonString(vet.getApellidoVeterinario())).append("\",")
                        .append("\"telefonoVeterinario\":\"").append(escapeJsonString(vet.getTelefonoVeterinario())).append("\",")
                        .append("\"correoVeterinario\":\"").append(escapeJsonString(vet.getCorreoVeterinario())).append("\",")
                        .append("\"idEspecialidad\":").append(vet.getIdEspecialidad()).append(",")
                        .append("\"nombreEspecialidad\":\"").append(escapeJsonString(nomEsp)).append("\"")
                        .append("}")
                        .toString();

                response.getWriter().write(json);
                return;
            }

            response.getWriter().write("{}");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener datos JSON", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno del servidor.\"}");
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("accion", "agregar");
        request.setAttribute("tipoEmpleado", request.getParameter("tipo")); // Recibe 'veterinario' o 'recepcionista'
        // Forward a la JSP que contiene el formulario o el modal para agregar
        // Si tu modal está en ListadoEmpleados.jsp y se activa por JS, este forward no es estrictamente necesario,
        // pero es útil si tienes una JSP dedicada para el formulario.
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/FormularioEmpleado.jsp").forward(request, response);
    }

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        String mensaje = "";
        String error = "";
        String activeTab = request.getParameter("currentTab"); // Para redirigir a la pestaña correcta

        try {
            if ("veterinario".equals(tipoEmpleado)) {
                Veterinario vet = new Veterinario();
                vet.setNombreVeterianrio(request.getParameter("nombreVeterinario"));
                vet.setApellidoVeterinario(request.getParameter("apellidoVeterinario"));
                vet.setTelefonoVeterinario(request.getParameter("telefonoVeterinario"));
                vet.setCorreoVeterinario(request.getParameter("correoVeterinario"));
                vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
                try {
                    vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
                } catch (Exception ex) {
                    error = "Seleccione una especialidad válida.";
                    LOGGER.log(Level.WARNING, "idEspecialidad inválido al agregar: " + request.getParameter("idEspecialidad"));
                }

                if (error.isEmpty()) {
                    VeterinarioDAO dao = new VeterinarioDAO();
                    boolean exito = dao.agregarVeterinario(vet);
                    if (exito) {
                        mensaje = "Veterinario agregado exitosamente.";
                    } else {
                        error = "No se pudo agregar el veterinario.";
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al agregar empleado (" + tipoEmpleado + "): " + e.getMessage(), e);
            error = "Error interno del servidor al agregar empleado: " + e.getMessage();
        }

        // Usar patrón Post-Redirect-Get (PRG) para evitar doble envío y problemas de recarga
        HttpSession session = request.getSession();
        if (!mensaje.isEmpty()) {
            session.setAttribute("mensajeFlash", mensaje);
        }
        if (!error.isEmpty()) {
            session.setAttribute("errorFlash", error);
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response, String tipoEmpleado)
            throws ServletException, IOException {

        String mensaje = "";
        String error = "";
        int id = 0;
        String activeTab = request.getParameter("currentTab");

        try {
            String idStr = request.getParameter("idEmpleado");
            if (idStr == null || idStr.isEmpty()) {
                idStr = request.getParameter("idVeterinario");
            }
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID inválido para actualización: " + request.getParameter("idEmpleado"), e);
            error = "ID de empleado inválido para actualización. Recargue la página e intente de nuevo.";
            request.getSession().setAttribute("errorFlash", error);
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
            return;
        }

        try {
            Veterinario vet = new Veterinario();
            vet.setIdVeterinario(id);
            vet.setNombreVeterianrio(request.getParameter("nombreVeterinario"));
            vet.setApellidoVeterinario(request.getParameter("apellidoVeterinario"));
            vet.setTelefonoVeterinario(request.getParameter("telefonoVeterinario"));
            vet.setCorreoVeterinario(request.getParameter("correoVeterinario"));
            vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
            try {
                vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
            } catch (Exception ex) {
                error = "Seleccione una especialidad válida.";
            }

            if (error.isEmpty()) {
                VeterinarioDAO dao = new VeterinarioDAO();
                boolean exito = dao.actualizarVeterinario(vet);
                if (exito) {
                    mensaje = "Veterinario actualizado exitosamente.";
                } else {
                    error = "No se pudo actualizar el veterinario.";
                }

            } else {
                error = "Tipo de empleado no válido para actualizar.";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al actualizar empleado (" + tipoEmpleado + ", ID: " + id + "): " + e.getMessage(), e);
            error = "Error interno del servidor al actualizar empleado: " + e.getMessage();
        }

        // Usar patrón Post-Redirect-Get (PRG)
        HttpSession session = request.getSession();

        if (!mensaje.isEmpty()) {
            session.setAttribute("mensajeFlash", mensaje);
        }

        if (!error.isEmpty()) {
            session.setAttribute("errorFlash", error);
        }

        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void eliminarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String tipo = request.getParameter("tipoEmpleado");
        if (tipo == null || tipo.isEmpty()) {
            tipo = "veterinario";
        }

        String activeTab = request.getParameter("currentTab");
        if (activeTab == null || activeTab.isEmpty()) {
            activeTab = "veterinarios";
        }

        String mensajeRespuesta = "";
        String errorRespuesta = "";

        int id;
        try {
            String idStr = request.getParameter("idEmpleado");
            if (idStr == null || idStr.isEmpty()) {
                idStr = request.getParameter("idVeterinario");
            }
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            errorRespuesta = "ID de empleado inválido para eliminación.";
            request.getSession().setAttribute("errorFlash", errorRespuesta);
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
            return;
        }

        try {
            boolean exito = false;
            if ("veterinario".equals(tipo)) {
                exito = new VeterinarioDAO().eliminarVeterinario(id);
            } else {
                errorRespuesta = "Tipo de empleado no válido para eliminar.";
            }

            if (exito) {
                mensajeRespuesta = "Veterinario eliminado exitosamente.";
            }
            if (!exito && errorRespuesta.isEmpty()) {
                errorRespuesta = "No se pudo eliminar (posibles registros asociados).";
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al eliminar empleado (" + tipo + ", ID: " + id + "): " + e.getMessage(), e);
            errorRespuesta = "Error interno del servidor al eliminar empleado.";
        } finally {
            HttpSession session = request.getSession();
            if (!mensajeRespuesta.isEmpty()) {
                session.setAttribute("mensajeFlash", mensajeRespuesta);
            }
            if (!errorRespuesta.isEmpty()) {
                session.setAttribute("errorFlash", errorRespuesta);
            }
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
        }
    }

    private void agregarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        try {
            String nombre = request.getParameter("nombreEspecialidad");
            String precioStr = request.getParameter("precio");

            if (nombre == null || nombre.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                session.setAttribute("errorFlash", "Complete nombre y precio.");
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
                return;
            }

            double precio = Double.parseDouble(precioStr);
            Especialidad e = new Especialidad();
            e.setNombreEspecialidad(nombre.trim());
            e.setPrecio(precio);

            boolean ok = new EspecialidadDAO().agregar(e);
            session.setAttribute(ok ? "mensajeFlash" : "errorFlash", ok ? "Especialidad agregada." : "No se pudo agregar la especialidad.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "agregarEspecialidad", ex);
            session.setAttribute("errorFlash", "Error interno al agregar especialidad.");
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void actualizarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            String nombre = request.getParameter("nombreEspecialidad");
            String precioStr = request.getParameter("precio");
            if (nombre == null || nombre.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                session.setAttribute("errorFlash", "Complete nombre y precio.");
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
                return;
            }
            double precio = Double.parseDouble(precioStr);
            Especialidad e = new Especialidad();
            e.setIdEspecialidad(id);
            e.setNombreEspecialidad(nombre.trim());
            e.setPrecio(precio);
            boolean ok = new EspecialidadDAO().actualizar(e);
            session.setAttribute(ok ? "mensajeFlash" : "errorFlash", ok ? "Especialidad actualizada." : "No se pudo actualizar la especialidad.");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "actualizarEspecialidad", ex);
            session.setAttribute("errorFlash", "Error interno al actualizar especialidad.");
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void eliminarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            boolean ok = new EspecialidadDAO().eliminar(id);
            session.setAttribute(ok ? "mensajeFlash" : "errorFlash", ok ? "Especialidad eliminada." : "No se pudo eliminar (posibles referencias).");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "eliminarEspecialidad", ex);
            session.setAttribute("errorFlash", "Error interno al eliminar especialidad.");
        }
        response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

// helper (si aún no lo tienes)
    private static String paramOr(String v, String def) {
        return (v == null || v.isEmpty()) ? def : v;
    }

    private void obtenerEspecialidadJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            Especialidad e = new EspecialidadDAO().obtenerPorId(id);
            if (e == null) {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"No encontrada\"}");
                return;
            }
            String json = "{\"idEspecialidad\":" + e.getIdEspecialidad()
                    + ",\"nombreEspecialidad\":\"" + escapeJsonString(e.getNombreEspecialidad()) + "\""
                    + ",\"precio\":" + e.getPrecio() + "}";
            response.getWriter().write(json);
        } catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Solicitud inválida\"}");
        }

    }
}
