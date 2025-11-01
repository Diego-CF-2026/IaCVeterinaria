package Controladores;

import Modelo.Veterinario;
import Modelo.Especialidad;
import ModeloDAO.VeterinarioDAO;
import ModeloDAO.EspecialidadDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminVeterinarioServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminVeterinarioServlet.class.getName());

    // =========================== HTTP ===========================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = paramOr(request.getParameter("accion"), "listar");

        switch (accion) {
            case "listar":
                listarEmpleados(request, response);
                break;

            // NUEVO: endpoints que devuelven JSON para los modales de edición
            case "obtener":
                obtenerVeterinarioJson(request, response);
                break;

            case "obtenerEspecialidad":
                obtenerEspecialidadJson(request, response);
                break;

            case "editar": // si alguna vista vieja lo usa
                prepararEdicionVeterinario(request, response);
                break;

            case "eliminar":
                eliminarEmpleado(request, response);
                break;

            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;

            default:
                listarEmpleados(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = paramOr(request.getParameter("accion"), "listar");

        switch (accion) {
            case "agregar":
                agregarEmpleado(request, response);
                break;

            case "actualizar":
                actualizarEmpleado(request, response);
                break;

            case "agregarEspecialidad":
                agregarEspecialidad(request, response);
                break;

            case "actualizarEspecialidad":
                actualizarEspecialidad(request, response);
                break;

            case "eliminarEspecialidad":
                eliminarEspecialidad(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
        }
    }

    // ======================= LISTAR / EDITAR (vista) =======================

    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("query");
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");

        moveFlash(request.getSession(), request);

        // Veterinarios
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        List<Veterinario> all = veterinarioDAO.listarVeterinarios();
        List<Veterinario> filtered;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = searchQuery.trim().toLowerCase();
            filtered = new ArrayList<>();
            for (Veterinario v : all) {
                if ((v.getNombreVeterinario() != null && v.getNombreVeterinario().toLowerCase().contains(q))
                 || (v.getApellidoVeterinario() != null && v.getApellidoVeterinario().toLowerCase().contains(q))
                 || (v.getTelefonoVeterinario() != null && v.getTelefonoVeterinario().toLowerCase().contains(q))) {
                    filtered.add(v);
                }
            }
            request.setAttribute("searchQuery", searchQuery);
        } else {
            filtered = all;
        }
        request.setAttribute("listaVeterinarios", filtered);

        // Especialidades
        EspecialidadDAO espDAO = new EspecialidadDAO();
        List<Especialidad> listaEspecialidades = espDAO.listar();
        Map<Integer, String> mapaEspecialidad = new HashMap<>();
        for (Especialidad e : listaEspecialidades) {
            mapaEspecialidad.put(e.getIdEspecialidad(), e.getNombreEspecialidad());
        }
        request.setAttribute("listaEspecialidades", listaEspecialidades);
        request.setAttribute("mapaEspecialidad", mapaEspecialidad);

        request.setAttribute("activeTab", activeTab);

        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
               .forward(request, response);
    }

    private void prepararEdicionVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        request.setAttribute("activeTab", activeTab);

        try {
            int idVet = Integer.parseInt(paramOr(request.getParameter("idVeterinario"),
                                                 request.getParameter("idEmpleado")));
            VeterinarioDAO dao = new VeterinarioDAO();
            Veterinario vet = dao.obtenerVeterinarioPorId(idVet);
            if (vet == null) {
                request.getSession().setAttribute("errorFlash", "Veterinario no encontrado.");
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
                return;
            }
            request.setAttribute("veterinarioEditar", vet);
            request.setAttribute("abrirModalVeterinario", "editar");
            listarEmpleados(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al preparar edición", e);
            request.getSession().setAttribute("errorFlash", "Error al preparar la edición.");
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("abrirModalVeterinario", "nuevo");
        listarEmpleados(request, response);
    }

    // ======================= JSON: OBTENER PARA MODALES =======================

    private void obtenerVeterinarioJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");

        try {
            int id = Integer.parseInt(paramOr(request.getParameter("idEmpleado"),
                                              request.getParameter("idVeterinario")));
            Veterinario v = new VeterinarioDAO().obtenerVeterinarioPorId(id);
            if (v == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Veterinario no encontrado\"}");
                return;
            }
            // Construir JSON manualmente (sin libs)
            String json = new StringBuilder()
                .append("{")
                .append("\"idVeterinario\":").append(v.getIdVeterinario()).append(",")
                .append("\"idUsuario\":").append(v.getIdUsuario()).append(",")
                .append("\"nombreVeterinario\":\"").append(esc(v.getNombreVeterinario())).append("\",")
                .append("\"apellidoVeterinario\":\"").append(esc(v.getApellidoVeterinario())).append("\",")
                .append("\"telefonoVeterinario\":\"").append(esc(v.getTelefonoVeterinario())).append("\",")
                .append("\"correoVeterinario\":\"").append(esc(nullToEmpty(v.getCorreoVeterinario()))).append("\",")
                .append("\"idEspecialidad\":").append(v.getIdEspecialidad())
                .append("}")
                .toString();

            response.getWriter().write(json);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "obtenerVeterinarioJson", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Solicitud inválida\"}");
        }
    }

    private void obtenerEspecialidadJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");

        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            Especialidad e = new EspecialidadDAO().obtenerPorId(id);
            if (e == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Especialidad no encontrada\"}");
                return;
            }
            String json = "{\"idEspecialidad\":"+e.getIdEspecialidad()
                        +",\"nombreEspecialidad\":\""+esc(e.getNombreEspecialidad())+"\""
                        +",\"precio\":"+e.getPrecio()+"}";
            response.getWriter().write(json);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "obtenerEspecialidadJson", ex);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Solicitud inválida\"}");
        }
    }

    // ======================= CRUD VETERINARIO =======================

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";

        try {
            Veterinario vet = new Veterinario();
            vet.setNombreVeterinario(request.getParameter("nombreVeterinario"));
            vet.setApellidoVeterinario(request.getParameter("apellidoVeterinario"));
            vet.setTelefonoVeterinario(request.getParameter("telefonoVeterinario"));
            vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));

            String correo = request.getParameter("correoVeterinario");
            String pass = request.getParameter("contrasenaVeterinario");
            if (pass == null || pass.isBlank()) {
                String tel = vet.getTelefonoVeterinario();
                String ult4 = (tel != null && tel.length() >= 4) ? tel.substring(tel.length() - 4) : "Temp";
                pass = "Vet" + ult4 + "!";
            }

            boolean exito = new VeterinarioDAO().agregarVeterinario(vet, correo, pass);
            ok  = exito ? "Veterinario agregado exitosamente." : "";
            err = exito ? "" : "No se pudo agregar el veterinario.";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al agregar veterinario", e);
            err = "Error interno del servidor al agregar.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";

        try {
            int idVet = Integer.parseInt(paramOr(request.getParameter("idVeterinario"),
                                                 request.getParameter("idEmpleado")));

            Veterinario vet = new Veterinario();
            vet.setIdVeterinario(idVet);
            vet.setNombreVeterinario(request.getParameter("nombreVeterinario"));
            vet.setApellidoVeterinario(request.getParameter("apellidoVeterinario"));
            vet.setTelefonoVeterinario(request.getParameter("telefonoVeterinario"));
            vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));

            String nuevoCorreo = request.getParameter("correoVeterinario");
            String nuevaContra = request.getParameter("contrasenaVeterinario");

            boolean exito = new VeterinarioDAO().actualizarVeterinario(vet, nuevoCorreo, nuevaContra);
            ok  = exito ? "Veterinario actualizado exitosamente." : "";
            err = exito ? "" : "No se pudo actualizar el veterinario.";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar veterinario", e);
            err = "Error interno del servidor al actualizar.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void eliminarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";

        try {
            int idVet = Integer.parseInt(paramOr(request.getParameter("idVeterinario"),
                                                 request.getParameter("idEmpleado")));
            boolean exito = new VeterinarioDAO().eliminarVeterinario(idVet);
            ok  = exito ? "Veterinario eliminado exitosamente." : "";
            err = exito ? "" : "No se pudo eliminar (posibles registros asociados).";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar veterinario", e);
            err = "Error interno al eliminar.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    // ======================= CRUD ESPECIALIDAD =======================

    private void agregarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";
        try {
            String nombre = request.getParameter("nombreEspecialidad");
            String precioStr = request.getParameter("precio");
            if (nombre == null || nombre.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                err = "Complete nombre y precio.";
            } else {
                double precio = Double.parseDouble(precioStr);
                Especialidad e = new Especialidad();
                e.setNombreEspecialidad(nombre.trim());
                e.setPrecio(precio);
                boolean exito = new EspecialidadDAO().agregar(e);
                ok  = exito ? "Especialidad agregada." : "";
                err = exito ? "" : "No se pudo agregar la especialidad.";
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "agregarEspecialidad", ex);
            err = "Error interno al agregar especialidad.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void actualizarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";
        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            String nombre = request.getParameter("nombreEspecialidad");
            String precioStr = request.getParameter("precio");
            if (nombre == null || nombre.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                err = "Complete nombre y precio.";
            } else {
                double precio = Double.parseDouble(precioStr);
                Especialidad e = new Especialidad();
                e.setIdEspecialidad(id);
                e.setNombreEspecialidad(nombre.trim());
                e.setPrecio(precio);
                boolean exito = new EspecialidadDAO().actualizar(e);
                ok  = exito ? "Especialidad actualizada." : "";
                err = exito ? "" : "No se pudo actualizar la especialidad.";
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "actualizarEspecialidad", ex);
            err = "Error interno al actualizar especialidad.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    private void eliminarEspecialidad(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";
        try {
            int id = Integer.parseInt(request.getParameter("idEspecialidad"));
            boolean exito = new EspecialidadDAO().eliminar(id);
            ok  = exito ? "Especialidad eliminada." : "";
            err = exito ? "" : "No se pudo eliminar (posibles referencias).";
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "eliminarEspecialidad", ex);
            err = "Error interno al eliminar especialidad.";
        }
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    // ======================= Utils =======================

    private static String paramOr(String v, String def) {
        return (v == null || v.isEmpty()) ? def : v;
    }

    private static void moveFlash(HttpSession session, HttpServletRequest request) {
        String msg = (String) session.getAttribute("mensajeFlash");
        String err = (String) session.getAttribute("errorFlash");
        if (msg != null) { request.setAttribute("mensaje", msg); session.removeAttribute("mensajeFlash"); }
        if (err != null) { request.setAttribute("error", err);   session.removeAttribute("errorFlash"); }
    }

    private static void flashAndRedirect(HttpSession session, String ok, String err,
                                         HttpServletResponse response, String to) throws IOException {
        if (ok != null && !ok.isEmpty())  session.setAttribute("mensajeFlash", ok);
        if (err != null && !err.isEmpty()) session.setAttribute("errorFlash", err);
        response.sendRedirect(to);
    }

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 32 || c > 126) out.append(String.format("\\u%04x", (int)c));
                    else out.append(c);
            }
        }
        return out.toString();
    }
    private static String nullToEmpty(String s){ return s==null? "": s; }
}
