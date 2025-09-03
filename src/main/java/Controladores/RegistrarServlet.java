package Controladores; // Asegúrate de que el nombre del paquete sea el correcto

import Modelo.UsuarioCliente;
import ModeloDAO.UsuarioClienteDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/RegistrarServlet")
public class RegistrarServlet extends HttpServlet {

    /**
     * Maneja las peticiones POST. Procesa el registro y el inicio de sesión automático.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener los datos del formulario
        String nombre = request.getParameter("nombres");
        String apellido = request.getParameter("apellidos");
        String dni = request.getParameter("dni");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // 2. Crear el objeto del modelo 'UsuarioCliente' para el registro
        UsuarioCliente nuevoUsuario = new UsuarioCliente();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setDni(dni);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena);

        UsuarioClienteDAO usuarioDAO = new UsuarioClienteDAO();
        boolean registroExitoso = false;

        try {
            registroExitoso = usuarioDAO.registrar(nuevoUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            // Mapeo de errores específicos de la base de datos
            if (e.getMessage() != null && (e.getMessage().contains("DNI") || e.getMessage().contains("dni"))) {
                request.setAttribute("error", "dni");
            } else if (e.getMessage() != null && (e.getMessage().contains("telefono") || e.getMessage().contains("Telefono"))) {
                request.setAttribute("error", "telefono");
            } else {
                request.setAttribute("error", "general");
            }

            // Reenviar los datos del formulario para que no se borren
            request.setAttribute("valNombres", nombre);
            request.setAttribute("valApellidos", apellido);
            request.setAttribute("valDni", dni);
            request.setAttribute("valTelefono", telefono);
            request.setAttribute("valCorreo", correo);
            
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        // 3. Lógica de redirección e inicio de sesión automático
        if (registroExitoso) {
            UsuarioCliente usuarioLogueado = null;
            try {
                // Después de registrar, validamos al usuario para obtener el objeto completo
                usuarioLogueado = usuarioDAO.validarUsuario(correo, contrasena);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (usuarioLogueado != null) {
                // Si el usuario es encontrado, se crea la sesión y se redirige
                HttpSession session = request.getSession(true);
                session.setAttribute("usuario", usuarioLogueado);
                session.setAttribute("idUsuario", usuarioLogueado.getIdUsuario());
                // Redirige al usuario al index.jsp con la sesión iniciada
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            } else {
                // Si el registro fue exitoso pero la validación falló (caso raro)
                request.setAttribute("error", "login_fallido_post_registro");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } else {
            // Si la inserción en el DAO falló
            request.setAttribute("error", "general");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}