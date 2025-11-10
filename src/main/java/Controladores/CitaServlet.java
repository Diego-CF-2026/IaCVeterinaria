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

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CitaServlet.class.getName());
    
    CitaDAO citaDAO = new CitaDAO();
    // Dependencias necesarias
    ClienteDAO clienteDAO = new ClienteDAO(); 
    VeterinarioDAO veterinarioDAO = new VeterinarioDAO(); 

    // Formateadores para parsear y formatear Date y Time
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm");

    // --- Métodos GET (Visualización y Cancelación/Eliminación) ---

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
    
    // --- Métodos POST (Creación y Actualización) ---

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

// -------------------------------------------------------------------

    /**
     * Muestra la lista completa de citas y las listas necesarias para los dropdowns.
     */
    private void verTodasCitasGlobal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Cargar listas necesarias para la tabla y el modal de edición
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
     * Busca citas por un término y muestra el resultado en la vista global.
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
            
            // Recargar datos de dropdowns
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
     * Elimina físicamente el registro (Admin).
     * Usa citaDAO.eliminar(idCita).
     */
    private void eliminarCitaFisica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idCitaStr = request.getParameter("id");
        
        try {
            int idCita = Integer.parseInt(idCitaStr);
            // CORRECCIÓN APLICADA: Usando el nombre de método 'eliminar'
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
        response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal");
    }

    /**
     * Procesa la creación de cita desde la vista de Recepción.
     */
    private void guardarNuevaCitaGlobal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Cita cita = new Cita();
        
        try {
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            cita.setMotivo(request.getParameter("motivo"));
            // 🟢 CORRECCIÓN: Capturar el nombre del estado
            cita.setEstadoNombre(request.getParameter("estado")); 

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
     * Muestra las citas de un cliente específico (desde la gestión de Recepción).
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
                
                // Recargar datos de dropdowns
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
    
// -------------------------------------------------------------------

    /**
     * Procesa el formulario del modal de edición (Universal).
     */
    private void actualizarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cita cita = new Cita();
        String idCitaStr = request.getParameter("idCita");
        // Determina el origen para la redirección final
        String origen = request.getParameter("origen") != null ? request.getParameter("origen") : "global"; 

        if (idCitaStr == null || idCitaStr.isEmpty()) {
            request.getSession().setAttribute("mensaje", "❌ ID de cita no proporcionado para la actualización.");
            request.getSession().setAttribute("tipoMensaje", "error");
            
            // Redirección por defecto si falta el ID
            if (origen.equals("cliente")) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal"); 
            }
            return;
        }

        try {
            // 1. Recepción de ID y claves foráneas
            cita.setIdCita(Integer.parseInt(idCitaStr));
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            
            // 2. Parseo de Fecha y Hora
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
            
            // Redirección si hay error de formato
             if (origen.equals("cliente")) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal"); 
            }
            return;
        }
        
        // 3. Recepción de campos de texto y Estado
        cita.setMotivo(request.getParameter("motivo"));
        // 🟢 CORRECCIÓN: Capturar el nombre del estado (ej: "Confirmada")
        cita.setEstadoNombre(request.getParameter("estado")); 

        // 4. Ejecutar la actualización en la base de datos
        // El CitaDAO se encargará de convertir el nombre del estado a idEstado.
        boolean operacionExitosa = citaDAO.actualizarCita(cita); 
        
        // 5. Manejo de la respuesta y redirección
        if (operacionExitosa) {
            request.getSession().setAttribute("mensaje", "✅ Cita actualizada con éxito!");
            request.getSession().setAttribute("tipoMensaje", "exito");
        } else {
            request.getSession().setAttribute("mensaje", "❌ Error al actualizar la cita.");
            request.getSession().setAttribute("tipoMensaje", "error");
        }
        
        // Redirigir al origen (MisCitas para cliente o Gestión Global para recepción/admin)
        if (origen.equals("cliente")) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
        } else {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal"); 
        }
    }

    /**
     * Cancela la cita (cambio de estado a "Cancelada").
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
        
        // Redirige a la vista del cliente o a la global, según sea necesario
        String origen = request.getParameter("origen") != null ? request.getParameter("origen") : "listar";
        if (origen.equals("global")) {
             response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verGlobal"); 
        } else {
             response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar"); 
        }
    }

    /**
     * Muestra la lista de citas para el cliente logueado (MisCitas.jsp).
     */
    private void listarCitasPorCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Integer idCliente = null;
        jakarta.servlet.http.HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            Object idSesion = sesion.getAttribute("idClienteSesion");
            if (idSesion != null) {
                try {
                    idCliente = (idSesion instanceof Integer) ? (Integer) idSesion : Integer.parseInt(idSesion.toString());
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.SEVERE, "ID de sesión inválido para listar citas. Valor: " + idSesion, e);
                }
            }
        }

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
            String urlVista = "/VistasWeb/VistasCliente/MisCitas.jsp";
            request.getRequestDispatcher(urlVista).forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fatal al cargar citas para ID: " + idCliente, e);
            request.setAttribute("errorMensaje", "Ocurrió un error en el servidor al cargar tus citas.");
            request.getRequestDispatcher("/VistasWeb/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Procesa la creación de cita desde el cliente (MisCitas.jsp).
     */
    private void crearCitaDesdeCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Cita cita = new Cita();
        
        try {
            cita.setIdCliente(Integer.parseInt(request.getParameter("idCliente")));
            cita.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario")));
            cita.setMotivo(request.getParameter("motivo"));
            cita.setEstadoNombre("Pendiente"); // Estado por defecto para citas agendadas por el cliente

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
     * Prepara los datos para la vista de edición (normalmente usada por Administrador/Recepción)
     */
    private void verCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int idCita = Integer.parseInt(idStr);
                // Asegúrate que obtenerCitaPorId carga el nombre del estado (estadoNombre)
                Cita citaSeleccionada = citaDAO.obtenerCitaPorId(idCita); 
                request.setAttribute("citaSeleccionada", citaSeleccionada);

                request.setAttribute("listaClientes", clienteDAO.listarClientesParaDropdown());
                request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());

                // 🟢 CORRECCIÓN: Si usas GestionCitas.jsp para el modal, ajusta la ruta.
                // Si usas una vista separada (editarCita.jsp), esta línea está bien:
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