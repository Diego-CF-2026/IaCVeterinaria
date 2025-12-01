package Controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    /**
     * Método GET para cerrar sesión.
     * Se invoca cuando el usuario hace clic en "Cerrar sesión".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // false evita crear una sesión nueva si no existe

        if (session != null) {
            // 3. Invalidar la sesión, eliminando todos los atributos
            session.invalidate(); // 4. Esto cierra efectivamente la sesión del usuario
        }

        response.sendRedirect(request.getContextPath() + "/index.jsp"); // 6. getContextPath asegura la ruta correcta

    }

    /**
     * Método POST para cerrar sesión.
     * Redirige al mismo proceso que doGet, permitiendo cerrar sesión desde formularios POST.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 9. Llamar a doGet() para centralizar la lógica de cierre de sesión
        doGet(request, response); // 10. Evita duplicar código
    }

    /**
     * Información descriptiva del servlet.
     */
    @Override
    public String getServletInfo() {
        // 11. Descripción corta del servlet
        return "Servlet para cerrar sesión del usuario"; // 12. Útil para documentación
    }
}
