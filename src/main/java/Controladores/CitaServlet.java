package Controladores;

import Modelo.Cita;
import Modelo.Cliente;
import Modelo.Veterinario; 
import ModeloDAO.CitaDAO;
import ModeloDAO.ClienteDAO; 
import ModeloDAO.VeterinarioDAO; 
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

/**
 * Servlet principal para gestión de Citas.
 *
 * GET:
 *  - accion=listar            -> Lista citas del cliente logueado (MisCitas.jsp).
 *  - accion=verGlobal         -> Vista global (Recepción/Admin) con todas las citas.
 *  - accion=buscar            -> Búsqueda global por término.
 *  - accion=ver               -> Prepara datos para editar una cita específica.
 *  - accion=cancelar          -> Cambia estado de una cita a "Cancelada".
 *  - accion=eliminar          -> Elimina físicamente una cita (uso administrativo).
 *  - accion=verCitasCliente   -> Lista citas de un cliente específico (Recepción).
 *
 * POST:
 *  - accion=guardarNuevaCitaGlobal -> Crea una cita desde la vista global.
 *  - accion=actualizar             -> Actualiza una cita (modal universal).
 *  - accion=crearCita              -> Crea una cita desde MisCitas (cliente).
 *
 * Notas:
 *  - Usa DATE_FORMATTER/TIME_FORMATTER para parsear fecha/hora de formularios.
 *  - Los mensajes de feedback viajan en sesión (mensaje/tipoMensaje) tras redirects.
 */
@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CitaServlet.class.getName());
    
    // DAOs de trabajo
    CitaDAO citaDAO = new CitaDAO();
    ClienteDAO clienteDAO = new ClienteDAO(); 
    VeterinarioDAO veterinarioDAO = new VeterinarioDAO(); 

    // Formateadores de fecha/hora (coinciden con los inputs del front)
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

    // ========================== RUTAS GET ==========================

    /**
     * Enrutador GET según 'accion'. Por defecto muestra vista global.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "verGlobal"; 
        }

        switch (accion) {
            case "listar":
                listarCitasPorCliente(request, response);
                break;
            case "verGlobal":
                verTodasCitasGlobal(request, response);
                break;
            case "buscar":
                buscarCitasGlobal(request, response);
                break;
            case "ver":
                verCita(request, response); 
                break;
            case "cancelar":
                cancelarCita(request, response);
                break;
            case "eliminar":
                eliminarCitaFisica(request, response);
                break;
            case "verCitasCliente":
                verCitasPorClienteEspec(request, response);
                break;
            default:
                verTodasCitasGlobal(request, response);
                break;
        }
    }
    
    // ========================== RUTAS POST ==========================
    
    /**
     * Enrutador POST según 'accion'. Si no hay acción, redirige al índice del servlet.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
            return;
        }

        switch (accion) {
            case "guardarNuevaCitaGlobal":
                guardarNuevaCitaGlobal(request, response);
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

    // ===================================================================
    // ===================== VISTAS / LISTADOS (GET) =====================
    // ===================================================================

    /**
     * Vista global (Recepción/Admin): carga todas las citas y datos de combos.
     */
    private void verTodasCitasGlobal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Tabla principal + combos para modales (cliente/veterinario)
            request.setAttribute("listaCitas", citaDAO.listarCitas());
            request.setAttribute("listaClientes", clienteDAO.listarClientesParaDropdown());
            request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());
            
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al listar citas globales.", e);
            request.setAttribute("mensaje", "❌ Error de conexión o al cargar datos de citas.");
            request.setAttribute("tipoMensaje", "error");
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
        }
    }
    
    /**
     * Búsqueda global por término; recarga la misma vista con resultados/alertas.
     */
    private void buscarCitasGlobal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String termino = request.getParameter("busqueda");
        
        try {
            if (termino == null || termino.trim().isEmpty()) {
                request.setAttribute("mensaje", "Ingrese un término para buscar.");
                request.setAttribute("tipoMensaje", "advertencia");
                verTodasCitasGlobal(request, response);
                return;
            }
            
            List<Cita> citas = citaDAO.buscarCitas(termino.trim());
            request.setAttribute("listaCitas", citas);
            request.setAttribute("busqueda", termino);
            
            if (citas.isEmpty()) {
                request.setAttribute("mensaje", "No se encontraron citas para: '" + termino + "'.");
                request.setAttribute("tipoMensaje", "advertencia");
            }
            
            // Mantener combos disponibles
            request.setAttribute("listaClientes", clienteDAO.listarClientesParaDropdown());
            request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());
            
            request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al buscar citas globales.", e);
            request.setAttribute("mensaje", "❌ Error al realizar la búsqueda.");
            request.setAttribute("tipoMensaje", "error");
            verTodasCitasGlobal(request, response);
        }
    }
    
    /**
     * Eliminación física de cita (uso administrativo).
     */
    private void eliminarCitaFisica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idCitaStr = request.getParameter("id");
        
        try {
            int idCita = Integer.parseInt(idCitaStr);
            // Llama al método 'eliminar' del DAO
            if (citaDAO.eliminar(idCita)) { 
                request.getSession().setAttribute("mensaje", "✅ Cita eliminada (físicamente) correctamente.");
                request.getSession().setAttribute("tipoMensaje", "exito");
            } else {
                request.getSession().setAttribute("mensaje", "⚠️ No se pudo eliminar la cita.");
                request.getSession().setAttribute("tipoMensaje", "advertencia");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de cita inválido para eliminar: " + idCitaStr, e);
            request.getSession().setAttribute("mensaje", "❌ ID de cita inválido.");
            request.getSession().setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar la cita.", e);
            request.getSession().setAttribute("mensaje", "❌ Error al intentar eliminar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        // Siempre regresar a la vista global
        response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
    }

    /**
     * Carga citas de un cliente específico para Recepción (filtro por cliente).
     */
    private void verCitasPorClienteEspec(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idClienteStr = request.getParameter("idCliente");
        String urlVista = "/VistasWeb/VistasRecep/GestionCitas.jsp"; 
        
        try {
            if (idClienteStr != null && !idClienteStr.isEmpty()) {
                int idCliente = Integer.parseInt(idClienteStr.trim());
                
                Cliente cliente = clienteDAO.buscarIdCliente(idCliente); 
                if (cliente == null) {
                    request.setAttribute("mensaje", "Cliente no encontrado.");
                    request.setAttribute("tipoMensaje", "advertencia");
                    verTodasCitasGlobal(request, response);
                    return;
                }
                
                request.setAttribute("listaCitas", citaDAO.listarCitasPorCliente(idCliente));
                request.setAttribute("clienteActual", cliente); 
                
                // Mantener combos cargados
                request.setAttribute("listaClientes", clienteDAO.listarClientesParaDropdown());
                request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());
                
            } else {
                request.setAttribute("mensaje", "ID de Cliente no proporcionado.");
                request.setAttribute("tipoMensaje", "error");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("mensaje", "ID de Cliente inválido.");
            request.setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al ver citas de cliente específico.", e);
            request.setAttribute("mensaje", "❌ Error al cargar las citas del cliente.");
            request.setAttribute("tipoMensaje", "error");
        }
        
        request.getRequestDispatcher(urlVista).forward(request, response);
    }

    // ===================================================================
    // ===================== CREAR / EDITAR (POST) =======================
    // ===================================================================

    /**
     * Crea una cita desde la vista global (Recepción/Admin).
     * Usa estadoNombre desde el form para que DAO resuelva idEstado.
     */
    private void guardarNuevaCitaGlobal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Cita cita = new Cita();
        
        try {
            // IDs foráneos + motivo + estado (nombre)
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            cita.setMotivo(request.getParameter("motivo"));
            cita.setEstadoNombre(request.getParameter("estado")); // nombre de estado desde el form

            // Parseo de fecha y hora (según inputs)
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));
            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));

            // Inserción
            String resultadoDAO = citaDAO.agregarCita(cita); 
            if (resultadoDAO.startsWith("✅")) {
                request.getSession().setAttribute("mensaje", resultadoDAO);
                request.getSession().setAttribute("tipoMensaje", "exito");
            } else {
                request.getSession().setAttribute("mensaje", resultadoDAO);
                request.getSession().setAttribute("tipoMensaje", "error");
            }
            
        } catch (NumberFormatException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Error de formato/parseo al guardar nueva cita global.", e);
            request.getSession().setAttribute("mensaje", "❌ Error de formato en los datos de la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar nueva cita global.", e);
            request.getSession().setAttribute("mensaje", "❌ Error al intentar agendar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
    }
    
    /**
     * Crea una cita desde MisCitas (cliente). El estado por defecto es "Pendiente".
     */
    private void crearCitaDesdeCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Cita cita = new Cita();
        
        try {
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            cita.setMotivo(request.getParameter("motivo"));
            cita.setEstadoNombre("Pendiente"); // estado inicial para cliente

            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));
            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));
            
            String resultadoDAO = citaDAO.agregarCita(cita); 
            if (resultadoDAO.startsWith("✅")) {
                request.getSession().setAttribute("mensaje", resultadoDAO);
                request.getSession().setAttribute("tipoMensaje", "exito");
            } else {
                request.getSession().setAttribute("mensaje", resultadoDAO);
                request.getSession().setAttribute("tipoMensaje", "error");
            }
            
        } catch (NumberFormatException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Error de formato/parseo al crear cita desde cliente.", e);
            request.getSession().setAttribute("mensaje", "❌ Error de formato en los datos de la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar nueva cita desde cliente.", e);
            request.getSession().setAttribute("mensaje", "❌ Error al intentar agendar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        
        response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
    }

    /**
     * Actualiza una cita (modal universal). Redirige a origen (global/cliente).
     * El DAO se encarga de mapear estadoNombre -> idEstado.
     */
    private void actualizarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        String idCitaStr = request.getParameter("idCita");
        String origen = request.getParameter("origen") != null ? request.getParameter("origen") : "global"; 

        // Validación mínima: debe existir idCita
        if (idCitaStr == null || idCitaStr.isEmpty()) {
            request.getSession().setAttribute("mensaje", "❌ ID de cita no proporcionado para la actualización.");
            request.getSession().setAttribute("tipoMensaje", "error");
            if (origen.equals("cliente")) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
            }
            return;
        }

        try {
            // 1) IDs y FKs
            cita.setIdCita(Integer.parseInt(idCitaStr));
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            // 2) Fecha/Hora
            String fechaStr = request.getParameter("fecha");
            String horaStr = request.getParameter("hora");
            java.util.Date parsedDate = DATE_FORMATTER.parse(fechaStr);
            cita.setFecha(new Date(parsedDate.getTime()));
            java.util.Date parsedTime = TIME_FORMATTER.parse(horaStr);
            cita.setHora(new Time(parsedTime.getTime()));
        } catch (NumberFormatException | ParseException e) {
            LOGGER.log(Level.SEVERE, "Error de formato/parseo al actualizar la cita: " + e.getMessage(), e);
            request.getSession().setAttribute("mensaje", "❌ Error de formato en los datos de la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
            if (origen.equals("cliente")) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
            }
            return;
        }
        
        // 3) Campos texto/estado
        cita.setMotivo(request.getParameter("motivo"));
        cita.setEstadoNombre(request.getParameter("estado")); // nombre del estado

        // 4) Persistencia
        boolean operacionExitosa = citaDAO.actualizarCita(cita); 
        
        // 5) Feedback + redirect
        if (operacionExitosa) {
            request.getSession().setAttribute("mensaje", "✅ Cita actualizada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al actualizar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        if (origen.equals("cliente")) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
        } else {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
        }
    }

    /**
     * Cambia estado de una cita a "Cancelada". Redirige a origen (global/listar).
     */
    private void cancelarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idCitaStr = request.getParameter("id");
        int idCita;
        
        try {
            idCita = Integer.parseInt(idCitaStr);
            boolean exito = citaDAO.cancelarCita(idCita); 
            if (exito) {
                request.getSession().setAttribute("mensaje", "✅ La cita N° " + idCita + " ha sido cancelada correctamente.");
                request.getSession().setAttribute("tipoMensaje", "exito");
            } else {
                request.getSession().setAttribute("mensaje", "⚠️ No se pudo cancelar la cita N° " + idCita + ".");
                request.getSession().setAttribute("tipoMensaje", "advertencia");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de cita inválido para cancelar: " + idCitaStr, e);
            request.getSession().setAttribute("mensaje", "❌ Error: ID de cita no válido.");
            request.getSession().setAttribute("tipoMensaje", "error");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar la cancelación de cita ID: " + idCitaStr, e);
            request.getSession().setAttribute("mensaje", "❌ Error interno al procesar la cancelación.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        
        // Decide a dónde volver (vista global o lista del cliente)
        String origen = request.getParameter("origen") != null ? request.getParameter("origen") : "listar";
        if (origen.equals("global")) {
             response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal"); 
        } else {
             response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
        }
    }

    // ===================================================================
    // ===================== CLIENTE (GET MisCitas) ======================
    // ===================================================================

    /**
     * Lista citas del cliente autenticado (usa idClienteSesion de la sesión).
     */
    private void listarCitasPorCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer idCliente = null;
        jakarta.servlet.http.HttpSession sesion = request.getSession(false);

        // Extrae idClienteSesion con tolerancia a Integer/String
        if (sesion != null) {
            Object idSesion = sesion.getAttribute("idClienteSesion");
            if (idSesion != null) {
                try {
                    idCliente = (idSesion instanceof Integer) ? (Integer) idSesion
                               : Integer.parseInt(idSesion.toString());
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.SEVERE, "ID de sesión inválido para listar citas. Valor: " + idSesion, e);
                }
            }
        }

        // Si no hay sesión válida, enviar a login/index
        if (idCliente == null || idCliente <= 0) {
            request.getSession().setAttribute("mensaje", "⚠️ Debes iniciar sesión para ver tus citas.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            List<Cita> misCitas = citaDAO.listarCitasPorCliente(idCliente);
            if (misCitas.isEmpty()) {
                request.setAttribute("avisoCitas", "Aún no tienes citas agendadas.");
            }

            request.setAttribute("misCitas", misCitas);
            request.getRequestDispatcher("/VistasWeb/VistasCliente/MisCitas.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fatal al cargar citas para ID: " + idCliente, e);
            request.setAttribute("errorMensaje", "Ocurrió un error en el servidor al cargar tus citas.");
            request.getRequestDispatcher("/VistasWeb/error.jsp").forward(request, response);
        }
    }

    // ===================================================================
    // ===================== PREPARAR EDICIÓN (GET) ======================
    // ===================================================================

    /**
     * Prepara datos de una cita específica para edición (vista separada).
     * Carga también listas de clientes y veterinarios para los select.
     */
    private void verCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int idCita = Integer.parseInt(idStr);
                // obtenerCitaPorId debe cargar estadoNombre si el JSP lo usa
                Cita citaSeleccionada = citaDAO.obtenerCitaPorId(idCita); 
                request.setAttribute("citaSeleccionada", citaSeleccionada);

                request.setAttribute("listaClientes", clienteDAO.listarClientesParaDropdown());
                request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());

                // Nota: Ajusta esta ruta si usas modal dentro de la gestión
                request.getRequestDispatcher("/VistasWeb/VistasAdmin/editarCita.jsp").forward(request, response); 

            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID de cita inválido para ver: " + idStr, e);
                response.sendRedirect(request.getContextPath() + "/CitaServlet");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/CitaServlet");
        }
    }
}
