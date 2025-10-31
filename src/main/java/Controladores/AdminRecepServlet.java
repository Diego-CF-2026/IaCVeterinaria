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

@WebServlet(name = "AdminRecepServlet", urlPatterns = {"/AdminRecepServlet"})
public class AdminRecepServlet extends HttpServlet {

    private final RecepcionistaDAO dao = new RecepcionistaDAO();
    
    // Ruta absoluta basada en tu estructura de carpetas
    private final String LISTAR_VISTA = "/VistasWeb/VistasAdmin/GestionRecep.jsp"; 

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acceso = LISTAR_VISTA; 
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty() || accion.equals("listar")) {
            // Acción por defecto: Listar
            cargarLista(request);
            
        } else if (accion.equals("guardar")) {
            // 1. Ejecutar Guardar
            String resultado = agregarRecepcionista(request);
            // 2. Cargar lista actualizada
            cargarLista(request); 
            // 3. Mostrar mensaje de resultado
            mostrarResultado(request, resultado, "registro");
            
        } else if (accion.equals("actualizar")) {
            // 1. Ejecutar Actualizar
            String resultado = actualizarRecepcionista(request);
            // 2. Cargar lista actualizada
            cargarLista(request);
            // 3. Mostrar mensaje de resultado
            mostrarResultado(request, resultado, "actualización");
            
        } else if (accion.equals("desactivar")) { 
            // 🔴 MANEJA CAMBIO DE ESTADO A INACTIVO (Estado = FALSE)
            String resultado = desactivarRecepcionista(request);
            // 2. Cargar lista actualizada
            cargarLista(request);
            // 3. Mostrar mensaje de resultado
            mostrarResultado(request, resultado, "desactivación");
            
        } else if (accion.equals("activar")) { 
            // 🟢 MANEJA CAMBIO DE ESTADO A ACTIVO (Estado = TRUE)
            String resultado = activarRecepcionista(request);
            // 2. Cargar lista actualizada
            cargarLista(request);
            // 3. Mostrar mensaje de resultado
            mostrarResultado(request, resultado, "activación");
        }

        // Redirección final a la vista 
        request.getRequestDispatcher(acceso).forward(request, response);
    }
    
    
    // --------------------------------------------------------------------------------
    // --- LÓGICA DE CONTROL ---
    // --------------------------------------------------------------------------------
    
    /**
     * Carga la lista de recepcionistas desde el DAO y la coloca en el request.
     */
    private void cargarLista(HttpServletRequest request) {
        List<Recepcionista> lista = dao.listarRecepcionistas();
        request.setAttribute("recepcionistas", lista);
    }

    /**
     * Procesa el resultado de una operación y prepara un mensaje para el JSP.
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
     * Crea un nuevo recepcionista y su usuario asociado.
     */
    private String agregarRecepcionista(HttpServletRequest request) {
        // Mapear datos de Usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo(request.getParameter("txtCorreo"));
        usuario.setContra(request.getParameter("txtContrasena")); 
        
        // Mapear datos de Recepcionista
        Recepcionista recepcionista = new Recepcionista();
        recepcionista.setNombreRecepcionista(request.getParameter("txtNombre"));
        recepcionista.setApellidoRecepcionista(request.getParameter("txtApellido"));
        recepcionista.setTelefonoRecepcionista(request.getParameter("txtTelefono"));
        
        return dao.agregarRecepcionista(usuario, recepcionista);
    }
    
    /**
     * Actualiza los datos de un recepcionista y su usuario.
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

            return dao.editarRecepcionista(usuario, recepcionista);
        } catch (NumberFormatException e) {
             return "ID de usuario inválido.";
        }
    }

    /**
     * Cambia el estado del usuario a FALSE (Desactivar).
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
     * Cambia el estado del usuario a TRUE (Activar).
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
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para la administración de Recepcionistas (CRUD con baja lógica)";
    }
}