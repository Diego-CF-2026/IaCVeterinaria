package Controladores;

import Modelo.Recepcionista;
import Modelo.Usuario;
import ModeloDAO.RecepcionistaDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet de administración para gestionar Recepcionistas.
 *
 * Rutas:
 *  - GET/POST ?accion=listar (o vacío)  -> Lista y muestra en JSP.
 *  - GET/POST ?accion=guardar           -> Crea recepcionista + usuario.
 *  - GET/POST ?accion=actualizar        -> Actualiza datos y usuario.
 *  - GET/POST ?accion=desactivar        -> Cambia estado a inactivo (FALSE).
 *  - GET/POST ?accion=activar           -> Cambia estado a activo (TRUE).
 *
 * Notas:
 *  - La vista objetivo es /VistasWeb/VistasAdmin/GestionRecep.jsp
 *  - Usa atributos request "mensaje" o "error" para feedback en el JSP.
 *  - La lógica de persistencia se delega a RecepcionistaDAO.
 */
@WebServlet(name = "AdminRecepServlet", urlPatterns = {"/AdminRecepServlet"})
public class AdminRecepServlet extends HttpServlet {

    // DAO central para operaciones de Recepcionista/Usuario
    private final RecepcionistaDAO dao = new RecepcionistaDAO();

    // Ruta de la vista asociada (JSP de administración)
    private final String LISTAR_VISTA = "/VistasWeb/VistasAdmin/GestionRecep.jsp"; 

    /**
     * Enrutador común para GET y POST. Lee "accion", ejecuta la operación,
     * carga la lista para renderizar en la vista y setea mensajes.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Por defecto, se despacha al JSP de gestión
        String acceso = LISTAR_VISTA; 
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty() || accion.equals("listar")) {
            // Acción por defecto: solo cargar la lista
            cargarLista(request);
            
        } else if (accion.equals("guardar")) {
            // 1) Guardar nuevo registro
            String resultado = agregarRecepcionista(request);
            // 2) Recargar lista para mostrar cambios
            cargarLista(request); 
            // 3) Exponer mensaje en request (éxito/error)
            mostrarResultado(request, resultado, "registro");
            
        } else if (accion.equals("actualizar")) {
            // 1) Actualizar registro existente
            String resultado = actualizarRecepcionista(request);
            // 2) Recargar lista
            cargarLista(request);
            // 3) Mensaje de feedback
            mostrarResultado(request, resultado, "actualización");
            
        } else if (accion.equals("desactivar")) { 
            // 🔴 Desactiva (Estado = FALSE) el usuario/recepcionista indicado
            String resultado = desactivarRecepcionista(request);
            // 2) Recargar lista
            cargarLista(request);
            // 3) Mensaje
            mostrarResultado(request, resultado, "desactivación");
            
        } else if (accion.equals("activar")) { 
            // 🟢 Activa (Estado = TRUE) el usuario/recepcionista indicado
            String resultado = activarRecepcionista(request);
            // 2) Recargar lista
            cargarLista(request);
            // 3) Mensaje
            mostrarResultado(request, resultado, "activación");
        }

        // Forward a la vista (sin redirect, conserva atributos del request)
        request.getRequestDispatcher(acceso).forward(request, response);
    }
    
    
    // --------------------------------------------------------------------------------
    // --- LÓGICA DE CONTROL ---
    // --------------------------------------------------------------------------------
    
    /**
     * Carga la lista de recepcionistas desde el DAO y la coloca en el request
     * bajo el atributo "recepcionistas" para consumo en el JSP.
     */
    private void cargarLista(HttpServletRequest request) {
        List<Recepcionista> lista = dao.listarRecepcionistas();
        request.setAttribute("recepcionistas", lista);
    }

    /**
     * Interpreta el resultado de una operación (cadena devuelta por DAO)
     * y setea en request un mensaje de éxito o error para el JSP.
     *
     * @param resultado  Cadena como "ok", "correo", "telefono", "no_encontrado", etc.
     * @param operacion  Texto amigable de la operación ("registro", "actualización", ...)
     */
    private void mostrarResultado(HttpServletRequest request, String resultado, String operacion) {
        if (resultado.equals("ok")) {
            request.setAttribute("mensaje", "Operación de " + operacion + " realizada con éxito.");
        } else if (resultado.contains("correo")) {
            request.setAttribute("error", "Error: El correo ya está registrado.");
        } else if (resultado.contains("telefono")) {
            request.setAttribute("error", "Error: El teléfono ya está registrado.");
        } else if (resultado.contains("no_encontrado")) {
            request.setAttribute("error", "Error: Registro no encontrado.");
        } else {
            request.setAttribute("error", "Error inesperado en el servidor al intentar " + operacion + ".");
        }
    }
    
    // --------------------------------------------------------------------------------
    // --- OPERACIONES CRUD (Métodos Privados) ---
    // --------------------------------------------------------------------------------

    /**
     * Crea un nuevo recepcionista junto con su usuario asociado.
     * Lee parámetros del formulario y delega en el DAO.
     *
     * @return "ok" si todo va bien, o un indicador de error reconocido por mostrarResultado().
     */
    private String agregarRecepcionista(HttpServletRequest request) {
        // Mapear datos para Usuario (credenciales)
        Usuario usuario = new Usuario();
        usuario.setCorreo(request.getParameter("txtCorreo"));
        usuario.setContra(request.getParameter("txtContrasena")); 
        
        // Mapear datos para Recepcionista (datos personales)
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setNombreRecepcionista(request.getParameter("txtNombre"));
        recepcionista.setApellidoRecepcionista(request.getParameter("txtApellido"));
        recepcionista.setTelefonoRecepcionista(request.getParameter("txtTelefono"));
        
        // Delegar creación a la capa DAO
        return dao.agregarRecepcionista(usuario, recepcionista);
    }
    
    /**
     * Actualiza datos de un recepcionista y su usuario asociado.
     * Requiere "txtIdUsuario" en el request.
     */
    private String actualizarRecepcionista(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("txtIdUsuario"));
            
            // Mapear datos de Usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(request.getParameter("txtCorreo"));
            usuario.setContra(request.getParameter("txtContrasena")); 
            
            // Mapear datos de Recepcionista
            Recepcionista recepcionista = new Recepcionista();
            recepcionista.setIdUsuario(id);
            recepcionista.setNombreRecepcionista(request.getParameter("txtNombre"));
            recepcionista.setApellidoRecepcionista(request.getParameter("txtApellido"));
            recepcionista.setTelefonoRecepcionista(request.getParameter("txtTelefono"));

            // Delegar actualización a la DAO
            return dao.editarRecepcionista(usuario, recepcionista);
        } catch (NumberFormatException e) {
            // Manejo simple de error de parseo
             return "ID de usuario inválido.";
        }
    }

    /**
     * Marca como inactivo (FALSE) el usuario/recepcionista indicado por "id".
     */
    private String desactivarRecepcionista(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            return dao.desactivarRecepcionista(id);
        } catch (NumberFormatException e) {
            return "ID inválido";
        }
    }
    
    /**
     * Marca como activo (TRUE) el usuario/recepcionista indicado por "id".
     */
    private String activarRecepcionista(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            return dao.activarRecepcionista(id);
        } catch (NumberFormatException e) {
            return "ID inválido";
        }
    }

    // --------------------------------------------------------------------------------
    // --- IMPLEMENTACIÓN DE HTTP SERVLET ---
    // --------------------------------------------------------------------------------
    
    /**
     * Redirige todas las peticiones GET al enrutador común processRequest().
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Redirige todas las peticiones POST al enrutador común processRequest().
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Breve descripción del servlet (aparece en algunas herramientas/controles).
     */
    @Override
    public String getServletInfo() {
        return "Servlet para la administración de Recepcionistas (CRUD con baja lógica)";
    }
}
