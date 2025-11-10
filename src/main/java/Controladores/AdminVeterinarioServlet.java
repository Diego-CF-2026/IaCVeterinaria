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

/**
 * Servlet de administración para gestionar Veterinarios y Especialidades.
 *
 * GET:
 *  - ?accion=listar               -> Carga vista principal con listas y pestañas.
 *  - ?accion=obtener              -> Devuelve JSON de un veterinario por ID (para modal).
 *  - ?accion=obtenerEspecialidad  -> Devuelve JSON de una especialidad por ID.
 *  - ?accion=editar               -> Prepara la vista con modal de edición abierto.
 *  - ?accion=eliminar             -> Elimina un veterinario y redirige a listar.
 *  - ?accion=nuevo                -> Muestra el modal de “nuevo” veterinario.
 *
 * POST:
 *  - accion=agregar               -> Inserta veterinario (y usuario si tu DAO lo maneja).
 *  - accion=actualizar            -> Actualiza veterinario (y usuario si aplica).
 *  - accion=agregarEspecialidad   -> Inserta especialidad.
 *  - accion=actualizarEspecialidad-> Actualiza especialidad.
 *  - accion=eliminarEspecialidad  -> Elimina especialidad.
 *
 * Notas:
 *  - Usa mensajes flash en sesión (mensajeFlash/errorFlash) para feedback tras redirects.
 *  - Construye JSON manual, escapando con esc() para seguridad.
 *  - currentTab mantiene la pestaña activa en la UI.
 */
@WebServlet(name = "AdminEmpleadoServlet", urlPatterns = {"/AdminEmpleadoServlet"})
public class AdminVeterinarioServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AdminVeterinarioServlet.class.getName());

    // =========================== HTTP ===========================

    /** Enrutador de peticiones GET basado en parámetro "accion". */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Valor por defecto: listar
        String accion = paramOr(request.getParameter("accion"), "listar");

        switch (accion) {
            case "listar":
                listarEmpleados(request, response);
                break;
            case "obtener": // JSON para modal de edición (AJAX)
                obtenerVeterinarioJson(request, response);
                break;
            case "obtenerEspecialidad": // JSON para modal de especialidad (AJAX)
                obtenerEspecialidadJson(request, response);
                break;
            case "editar": // compatibilidad con vistas antiguas
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

    /** Enrutador de peticiones POST para operaciones CRUD. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Asegura soporte de caracteres multibyte
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
                // Fallback: vuelve a la lista
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar");
        }
    }

    // ======================= LISTAR / EDITAR (vista) =======================

    /**
     * Carga datos para la vista principal:
     * - Lista filtrada de veterinarios (por nombre, apellido o teléfono).
     * - Lista y mapa de especialidades (id->nombre) para renderizar en tabla/form.
     * - Mantiene pestaña activa y mueve mensajes flash de sesión a request.
     */
    private void listarEmpleados(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchQuery = request.getParameter("query"); // término de búsqueda libre
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");

        // Pasa mensajes flash a request y los limpia de sesión
        moveFlash(request.getSession(), request);

        // --- Veterinarios: consulta completa y filtrado básico en memoria ---
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
            // Conserva el query en la vista para repoblar el input
            request.setAttribute("searchQuery", searchQuery);
        } else {
            filtered = all;
        }
        request.setAttribute("listaVeterinarios", filtered);

        // --- Especialidades: lista + mapa id->nombre para acceso rápido ---
        EspecialidadDAO espDAO = new EspecialidadDAO();
        List<Especialidad> listaEspecialidades = espDAO.listar();
        Map<Integer, String> mapaEspecialidad = new HashMap<>();
        for (Especialidad e : listaEspecialidades) {
            mapaEspecialidad.put(e.getIdEspecialidad(), e.getNombreEspecialidad());
        }
        request.setAttribute("listaEspecialidades", listaEspecialidades);
        request.setAttribute("mapaEspecialidad", mapaEspecialidad);

        // Mantiene la pestaña activa al volver del redirect/submit
        request.setAttribute("activeTab", activeTab);

        // Despacha a la JSP principal
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/ListadoEmpleados.jsp")
               .forward(request, response);
    }

    /**
     * Prepara la pantalla con el modal de edición de veterinario abierto.
     * Si el ID no existe, coloca error flash y redirige a listar.
     */
    private void prepararEdicionVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        request.setAttribute("activeTab", activeTab);

        try {
            // Admite idVeterinario o idEmpleado por compatibilidad
            int idVet = Integer.parseInt(paramOr(request.getParameter("idVeterinario"),
                                                 request.getParameter("idEmpleado")));
            VeterinarioDAO dao = new VeterinarioDAO();
            Veterinario vet = dao.obtenerVeterinarioPorId(idVet);
            if (vet == null) {
                request.getSession().setAttribute("errorFlash", "Veterinario no encontrado.");
                response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
                return;
            }
            // Señales a la vista para abrir el modal con datos
            request.setAttribute("veterinarioEditar", vet);
            request.setAttribute("abrirModalVeterinario", "editar");
            // Carga resto de datos de la vista
            listarEmpleados(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al preparar edición", e);
            request.getSession().setAttribute("errorFlash", "Error al preparar la edición.");
            response.sendRedirect(request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
        }
    }

    /** Muestra el modal de “nuevo” veterinario en la vista principal. */
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("abrirModalVeterinario", "nuevo");
        listarEmpleados(request, response);
    }

    // ======================= JSON: OBTENER PARA MODALES =======================

    /**
     * Devuelve JSON con los datos de un veterinario por ID.
     * 200: JSON; 404: no encontrado; 400: parámetros inválidos.
     */
    private void obtenerVeterinarioJson(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store"); // evita cachear respuestas sensibles

        try {
            // Acepta idEmpleado o idVeterinario
            int id = Integer.parseInt(paramOr(request.getParameter("idEmpleado"),
                                              request.getParameter("idVeterinario")));
            Veterinario v = new VeterinarioDAO().obtenerVeterinarioPorId(id);
            if (v == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Veterinario no encontrado\"}");
                return;
            }
            // Construcción manual del JSON (sin libs). Escapar siempre.
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

    /**
     * Devuelve JSON con los datos de una especialidad por ID.
     * 200: JSON; 404: no encontrada; 400: parámetros inválidos.
     */
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
            // JSON compacto con nombre escapado
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

    /**
     * Inserta un veterinario. Si no se envía contraseña, genera una por defecto
     * con patrón "Vet####!" usando los últimos 4 dígitos del teléfono (si existen).
     * Usa mensajes flash para feedback tras redirect.
     */
    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";

        try {
            // Construye el modelo desde parámetros del formulario
            Veterinario vet = new Veterinario();
            vet.setNombreVeterinario(request.getParameter("nombreVeterinario"));
            vet.setApellidoVeterinario(request.getParameter("apellidoVeterinario"));
            vet.setTelefonoVeterinario(request.getParameter("telefonoVeterinario"));
            vet.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));

            String correo = request.getParameter("correoVeterinario");
            String pass = request.getParameter("contrasenaVeterinario");

            // Generación de contraseña por defecto si no se proporcionó
            if (pass == null || pass.isBlank()) {
                String tel = vet.getTelefonoVeterinario();
                String ult4 = (tel != null && tel.length() >= 4) ? tel.substring(tel.length() - 4) : "Temp";
                pass = "Vet" + ult4 + "!";
            }

            // DAO ejecuta la inserción (y creación/relación de usuario si aplica)
            boolean exito = new VeterinarioDAO().agregarVeterinario(vet, correo, pass);
            ok  = exito ? "Veterinario agregado exitosamente." : "";
            err = exito ? "" : "No se pudo agregar el veterinario.";

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al agregar veterinario", e);
            err = "Error interno del servidor al agregar.";
        }

        // PRG: coloca flash y redirige manteniendo pestaña activa
        flashAndRedirect(session, ok, err,
                response, request.getContextPath() + "/AdminEmpleadoServlet?accion=listar&currentTab=" + activeTab);
    }

    /**
     * Actualiza un veterinario. Acepta idVeterinario o idEmpleado.
     * Si se envían nuevo correo/contraseña, la DAO puede sincronizar el usuario relacionado.
     */
    private void actualizarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String activeTab = paramOr(request.getParameter("currentTab"), "veterinarios");
        HttpSession session = request.getSession();
        String ok = "", err = "";

        try {
            // Compatibilidad con ambos nombres de parámetro
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

    /**
     * Elimina un veterinario por ID. La lógica de eliminación de usuario asociado
     * (en cascada/transacción) debe implementarse en la capa DAO/BD si corresponde.
     */
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

    /** Inserta una especialidad. Valida nombre y precio (no vacíos). */
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

    /** Actualiza una especialidad por ID. Valida nombre y precio. */
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

    /** Elimina una especialidad por ID. Si hay FK, reporta posible bloqueo por referencias. */
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

    /** Devuelve v si no es null/empty; de lo contrario def. */
    private static String paramOr(String v, String def) {
        return (v == null || v.isEmpty()) ? def : v;
    }

    /**
     * Mueve mensajes flash desde sesión a request (para mostrarlos una vez)
     * y los remueve de la sesión.
     */
    private static void moveFlash(HttpSession session, HttpServletRequest request) {
        String msg = (String) session.getAttribute("mensajeFlash");
        String err = (String) session.getAttribute("errorFlash");
        if (msg != null) { request.setAttribute("mensaje", msg); session.removeAttribute("mensajeFlash"); }
        if (err != null) { request.setAttribute("error", err);   session.removeAttribute("errorFlash"); }
    }

    /**
     * Coloca mensajes flash (si existen) y redirige. Implementa PRG (Post/Redirect/Get).
     */
    private static void flashAndRedirect(HttpSession session, String ok, String err,
                                         HttpServletResponse response, String to) throws IOException {
        if (ok != null && !ok.isEmpty())  session.setAttribute("mensajeFlash", ok);
        if (err != null && !err.isEmpty()) session.setAttribute("errorFlash", err);
        response.sendRedirect(to);
    }

    /**
     * Escapa cadena para incluirla de forma segura dentro de JSON manual:
     * comillas, backslash y caracteres de control -> secuencias de escape.
     */
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

    /** Convierte null a cadena vacía (útil antes de serializar). */
    private static String nullToEmpty(String s){ return s==null? "": s; }
}
