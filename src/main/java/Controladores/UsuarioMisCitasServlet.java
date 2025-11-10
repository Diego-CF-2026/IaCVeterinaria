package Controladores;

import Modelo.Cita;
import ModeloDAO.CitaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet encargado de listar todas las citas de un cliente específico.
 * * Funciones principales:
 * 1. Validar la autenticación del cliente a través de la sesión.
 * 2. Cargar las citas pendientes y completadas usando el CitaDAO.
 * 3. Enviar los datos a la vista MisCitas.jsp.
 * * Mapeo: /UsuarioMisCitasServlet
 */
@WebServlet("/UsuarioMisCitasServlet")
public class UsuarioMisCitasServlet extends HttpServlet {

    // Objeto Logger para el registro de eventos y errores a nivel de aplicación (importante para debugging).
    private static final Logger LOGGER = Logger.getLogger(UsuarioMisCitasServlet.class.getName());
    
    // Instancia del Data Access Object (DAO) para la gestión de las operaciones de Cita en la base de datos.
    private final CitaDAO citaDAO = new CitaDAO();

    // =========================================================================
    //                            MÉTODO GET
    // =========================================================================
    /**
     * Procesa las peticiones GET para mostrar la lista de citas del cliente logueado.
     * * @param request  Objeto HttpServletRequest que contiene la petición del cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Intentar recuperar la sesión existente. Si session=false, no crea una nueva si no existe.
        HttpSession session = request.getSession(false);
        Integer idCliente = null;
        
        // 1. **SEGURIDAD:** Obtener el ID del cliente autenticado desde la sesión
        Object idSesion = session != null ? session.getAttribute("idClienteSesion") : null;
        
        // Validar que el objeto recuperado de la sesión sea efectivamente un Integer.
        if (idSesion instanceof Integer) {
            idCliente = (Integer) idSesion;
        } 
        
        // 2. Mecanismo de prueba/debugging con parámetro URL (Opcional)
        // Permite a los desarrolladores probar la funcionalidad pasando el ID por URL.
        if (idCliente == null) {
            String idTestStr = request.getParameter("id_test");
            if (idTestStr != null && !idTestStr.isEmpty()) {
                try {
                    // Intenta parsear el ID de prueba.
                    idCliente = Integer.parseInt(idTestStr); 
                    LOGGER.log(Level.INFO, "Usando ID de prueba desde parámetro URL: " + idCliente);
                } catch (NumberFormatException e) {
                    // Si el ID de prueba no es un número, se registra una advertencia.
                    LOGGER.log(Level.WARNING, "El parámetro 'id_test' no es un número válido.");
                }
            }
        }

        if (idCliente == null) {
            // **Control de Acceso Denegado:** Si no se pudo obtener un ID válido (ni de sesión ni de prueba).
            LOGGER.log(Level.WARNING, "Acceso a citas denegado: ID de cliente no disponible. Redirigiendo a Login.");
            // Redirigir a la vista de login o index principal para forzar la autenticación.
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/indexCliente.jsp"); 
            return; // Detener la ejecución.
        }

        // 3. **Lógica Principal:** Cargar las citas del cliente
        try {
            // Llamar al DAO para obtener la lista de citas asociadas al idCliente.
            List<Cita> misCitas = citaDAO.listarCitasPorCliente(idCliente);
            
            // Adjuntar la lista de citas al objeto request. Esto será usado en el JSP.
            request.setAttribute("misCitas", misCitas);
            
            // Adjuntar el ID del cliente (útil para links o acciones específicas dentro de la vista).
            request.setAttribute("idCliente", idCliente);
            
            // Definir la ruta de la vista a la que se debe redirigir.
            String urlVista = "/VistasWeb/VistasCliente/MisCitas.jsp"; 
            
            // Despachar (reenviar) la petición y respuesta a la vista JSP.
            request.getRequestDispatcher(urlVista).forward(request, response);

        } catch (Exception e) {
            // 4. **Manejo de Errores Críticos**
            // Registrar el error grave (problemas de conexión a DB, error en SQL del DAO, etc.).
            LOGGER.log(Level.SEVERE, "Error crítico al cargar citas para ID: " + idCliente + ". Revisar conexión y consulta SQL del DAO.", e);
            
            // Configurar un mensaje de error para mostrar en la vista.
            request.setAttribute("errorMensaje", "Ocurrió un error en el servidor al cargar tus citas. ID: " + idCliente);
            
            // Redirigir a la misma vista, pero con el mensaje de error.
            request.getRequestDispatcher("/VistasWeb/VistasCliente/MisCitas.jsp").forward(request, response);
        }
    }
    
    // Nota: Los métodos POST, PUT, DELETE no están implementados en este Servlet.
    // Si se necesitara cancelar o modificar una cita, se implementarían aquí o en otro Servlet específico.
    // @Override
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) ...
}