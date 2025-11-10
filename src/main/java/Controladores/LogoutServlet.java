package Controladores;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet para manejar el cierre de sesión de los usuarios.
 * Al invocar este servlet, se destruye la sesión actual y
 * se redirige al usuario a la página de inicio o login.
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    /**
     * Método GET para cerrar sesión.
     * Se invoca cuando el usuario hace clic en "Cerrar sesión".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener la sesión actual del usuario
        HttpSession session = request.getSession(false); // false evita crear una sesión nueva si no existe

        // 2. Verificar si la sesión existe
        if (session != null) {
            // 3. Invalidar la sesión, eliminando todos los atributos
            session.invalidate(); // 4. Esto cierra efectivamente la sesión del usuario
        }

        // 5. Redirigir al usuario a la página principal o login
        response.sendRedirect(request.getContextPath() + "/index.jsp"); // 6. getContextPath asegura la ruta correcta

        // 7. Nota: Después de invalidate(), no se puede usar la sesión anterior
        // 8. Cualquier intento de acceder a atributos de la sesión dará null
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

    // Comentarios adicionales pedagógicos

    // 13. El método invalidate() elimina automáticamente todos los atributos de la sesión
    // 14. También notifica a los listeners de sesión que la sesión ha terminado (si existen)
    // 15. No es necesario eliminar atributos individualmente
    // 16. Usar getSession(false) evita crear sesiones vacías innecesarias
    // 17. Se recomienda usar sendRedirect en lugar de forward para evitar recargar la página con sesión cerrada
    // 18. Si se usara forward después de invalidar, algunos navegadores podrían seguir mostrando la página anterior
    // 19. Este servlet es seguro para que se llame desde GET o POST indistintamente
    // 20. Cualquier intento de usar la sesión después de invalidate() causará NullPointerException si no se verifica
    // 21. Es buena práctica colocar este servlet en un navbar o botón de "Cerrar sesión"
    // 22. getContextPath() asegura que la redirección funcione incluso si la app está desplegada en un subdirectorio
    // 23. Este servlet no necesita acceso a la base de datos, solo gestiona la sesión
    // 24. Al cerrar sesión, se recomienda también limpiar cookies de autenticación si las hubiera
    // 25. El servlet es simple, pero crítico para la seguridad de la aplicación

}
