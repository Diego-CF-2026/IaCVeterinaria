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

@WebServlet("/UsuarioMisCitasServlet")
public class UsuarioMisCitasServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioMisCitasServlet.class.getName());
    private final CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer idCliente = null;
        
        // 1. Obtener el ID de la sesión (Ahora con el nombre 'idClienteSesion')
        Object idSesion = session != null ? session.getAttribute("idClienteSesion") : null;
        
        if (idSesion instanceof Integer) {
            idCliente = (Integer) idSesion;
        } 
        
        // 2. Mecanismo de prueba con parámetro URL (Opcional, pero útil)
        if (idCliente == null) {
             String idTestStr = request.getParameter("id_test");
             if (idTestStr != null && !idTestStr.isEmpty()) {
                 try {
                     idCliente = Integer.parseInt(idTestStr); 
                     LOGGER.log(Level.INFO, "Usando ID de prueba desde parámetro URL: " + idCliente);
                 } catch (NumberFormatException e) {
                     LOGGER.log(Level.WARNING, "El parámetro 'id_test' no es un número válido.");
                 }
             }
        }

        if (idCliente == null) {
            // Si no está logueado, lo enviamos a iniciar sesión
            LOGGER.log(Level.WARNING, "Acceso a citas denegado: ID de cliente no disponible. Redirigiendo a Login.");
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/indexCliente.jsp"); 
            return;
        }

        try {
            // 3. Llamar al DAO con el ID obtenido
            List<Cita> misCitas = citaDAO.listarCitasPorCliente(idCliente);
            
            request.setAttribute("misCitas", misCitas);
            request.setAttribute("idCliente", idCliente);
            
            String urlVista = "/VistasWeb/VistasCliente/MisCitas.jsp"; 
            request.getRequestDispatcher(urlVista).forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error crítico al cargar citas para ID: " + idCliente + ". Revisar conexión y consulta SQL del DAO.", e);
            request.setAttribute("errorMensaje", "Ocurrió un error en el servidor al cargar tus citas. ID: " + idCliente);
            request.getRequestDispatcher("/VistasWeb/VistasCliente/MisCitas.jsp").forward(request, response);
        }
    }
}