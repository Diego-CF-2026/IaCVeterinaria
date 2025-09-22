/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Cliente;
import Modelo.Usuario;
import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/EditarPerfilServlet")
public class EditarPerfilServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (usuario == null || cliente == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // Datos del formulario
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String dni = request.getParameter("dni");
            String telefono = request.getParameter("telefono");
            String correo = request.getParameter("correo");

            // Actualizar cliente
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDni(dni);
            cliente.setTelefono(telefono);

            boolean clienteEditado = clienteDAO.editarClientePorUsuario(cliente);

            // Actualizar usuario
            usuario.setCorreo(correo);
            boolean usuarioEditado = usuarioDAO.actualizarCorreo(usuario);

            if (clienteEditado && usuarioEditado) {
                session.setAttribute("cliente", cliente);
                session.setAttribute("usuario", usuario);
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?success=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
        }
    }
}

