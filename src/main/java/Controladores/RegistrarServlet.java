package Controlador;

import ModeloDAO.UsuarioDAO;
import Modelo.Cliente;
import Modelo.Usuario;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegistrarServlet")
public class RegistrarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Capturar datos del formulario
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String dni = request.getParameter("dni");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // 2. Mantener valores en caso de error (para mostrarlos en el modal)
        request.setAttribute("valNombres", nombres);
        request.setAttribute("valApellidos", apellidos);
        request.setAttribute("valDni", dni);
        request.setAttribute("valTelefono", telefono);
        request.setAttribute("valCorreo", correo);

        // 3. Crear objetos modelo
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContra(contrasena);
        usuario.setIntentos(0);
        usuario.setEstado(true);

        Cliente cliente = new Cliente();
        cliente.setNombre(nombres);
        cliente.setApellido(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);

        // 4. Insertar con DAO
        UsuarioDAO dao = new UsuarioDAO();
        String resultado = dao.insertarCliente(usuario, cliente);

        // 5. Pasar resultado al JSP
        if ("ok".equals(resultado)) {
            request.setAttribute("exitoRegistro", "ok");
        } else {
            request.setAttribute("errorRegistro", resultado);
        }

        // 6. Redirigir a la vista principal (donde está tu modal)
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
