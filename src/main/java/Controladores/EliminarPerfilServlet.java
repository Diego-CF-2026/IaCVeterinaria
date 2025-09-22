/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

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

@WebServlet("/EliminarPerfilServlet")
public class EliminarPerfilServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idUsuario = usuario.getIdUsuario();

        try {
            // Primero eliminamos el cliente asociado
            boolean clienteEliminado = clienteDAO.eliminarClientePorUsuario(idUsuario);

            // Después eliminamos el usuario
            boolean usuarioEliminado = usuarioDAO.eliminarUsuario(idUsuario);

            if (clienteEliminado && usuarioEliminado) {
                session.invalidate(); // cerrar sesión
                response.sendRedirect(request.getContextPath() + "/index.jsp?deleted=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
        }
    }
}

