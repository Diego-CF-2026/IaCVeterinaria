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

            // 🔹 Buenas prácticas:
            // - Se recomienda registrar en logs la eliminación de perfil para auditoría
            // - Se podría validar que el usuario realmente tiene permisos para eliminar su perfil
            // - Posible extensión: enviar correo de confirmación de eliminación
            // - Manejo de transacciones: asegurarse que si falla una eliminación, la base de datos quede consistente
            // - Se puede agregar un modal de confirmación antes de ejecutar la eliminación
            // - Se puede hacer soft-delete en lugar de borrado físico si se desea conservar historial
            // - Se podría capturar y mostrar errores específicos de DAO en la vista
            // - Validar que la sesión no haya expirado antes de intentar eliminar
            // - Permitir que el administrador pueda eliminar usuarios desde un panel de administración
            // - Integración con estadísticas o reportes que actualicen cuando se elimina un usuario
            // - Posibilidad de eliminar también datos asociados como historial de compras, carritos, etc.
            // - Se podría notificar al sistema de facturación si el usuario tenía compras pendientes
            // - Mantener consistencia entre tablas relacionadas al usuario
            // - Manejo de excepciones específico para SQLException para mayor detalle
            // - Validar concurrencia si varios usuarios intentan eliminar al mismo tiempo
            // - Agregar mensajes amigables en JSP según el resultado de la operación
            // - Controlar intentos de eliminación no autorizados desde la web
            // - Posible registro de auditoría para cumplir con normativas de datos
            // - Se puede usar un servlet filter para validar sesión antes de todos los servlets sensibles
            // - Permite futura extensión para eliminar perfiles de empleados o veterinarios siguiendo la misma lógica

        } catch (Exception e) {
            // 🔹 Captura cualquier excepción y redirige mostrando error
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
        }
    }
}
