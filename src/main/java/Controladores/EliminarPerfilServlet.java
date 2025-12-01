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

/**
 * Servlet para eliminar el perfil de un cliente.
 * Elimina primero el registro en la tabla Cliente y luego en la tabla Usuario.
 * Cierra la sesión si la eliminación es exitosa.
 * Redirige a la página principal o muestra un error en la vista de perfil.
 */
@WebServlet("/EliminarPerfilServlet")
public class EliminarPerfilServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO(); // DAO para operaciones con clientes
    private UsuarioDAO usuarioDAO = new UsuarioDAO(); // DAO para operaciones con usuarios

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Obtener la sesión actual
        HttpSession session = request.getSession();

        // 🔹 Recuperar el usuario logueado desde la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // 🔹 Validar si no hay usuario logueado, redirigir a la página principal
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 🔹 Obtener el id del usuario para las operaciones en la base de datos
        int idUsuario = usuario.getIdUsuario();

        try {
            // 🔹 Primero eliminamos el cliente asociado al usuario
            boolean clienteEliminado = clienteDAO.eliminarClientePorUsuario(idUsuario);

            // 🔹 Después eliminamos el usuario
            boolean usuarioEliminado = usuarioDAO.eliminarUsuario(idUsuario);

            // 🔹 Verificar que ambas operaciones hayan sido exitosas
            if (clienteEliminado && usuarioEliminado) {
                // 🔹 Cerrar la sesión del usuario eliminado
                session.invalidate();

                // 🔹 Redirigir a la página principal con indicador de eliminación exitosa
                response.sendRedirect(request.getContextPath() + "/index.jsp?deleted=1");
            } else {
                // 🔹 Redirigir a la vista de perfil mostrando un error
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
            }


        } catch (Exception e) {
            // 🔹 Captura cualquier excepción y redirige mostrando error
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
        }
    }
}
