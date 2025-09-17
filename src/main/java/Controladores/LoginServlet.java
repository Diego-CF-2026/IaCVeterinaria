package Controlador;

import Modelo.Usuario;
import ModeloDAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contra = request.getParameter("contrasena");

        // Busca al usuario en la BD
        Usuario usuario = usuarioDAO.login(correo, contra);

        if (usuario != null && usuario.isEstado()) {
            // Crear sesión
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuario);

           String contextPath = request.getContextPath();

            switch (usuario.getIdRol()) {
                case 1: // Administrador
                    response.sendRedirect(contextPath + "/VistasWeb/VistasAdmin/AdminDash.jsp");
                    break;
                case 2: // Recepcionista
                    response.sendRedirect(contextPath + "/CitaServlet");
                    break;
                case 3: // Cliente
                    response.sendRedirect(contextPath + "/VistasWeb/VistasCliente/Nosotros.jsp");
                    break;
                default:
                    request.setAttribute("errorLogin", "Rol no válido.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    break;
            }


        } else {
            // Error en login
            request.setAttribute("errorLogin", "Correo o contraseña incorrectos, o usuario inactivo.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
