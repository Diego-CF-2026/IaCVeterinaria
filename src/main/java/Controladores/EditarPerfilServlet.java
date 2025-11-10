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

/**
 * Servlet para editar el perfil de un cliente.
 * Permite actualizar datos personales y el correo del usuario.
 * Mantiene la sesión actualizada con la información nueva.
 */
@WebServlet("/EditarPerfilServlet")
public class EditarPerfilServlet extends HttpServlet {

    private ClienteDAO clienteDAO = new ClienteDAO(); // DAO para operaciones sobre la tabla Cliente
    private UsuarioDAO usuarioDAO = new UsuarioDAO(); // DAO para operaciones sobre la tabla Usuario

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Obtener la sesión actual del usuario
        HttpSession session = request.getSession();

        // 🔹 Recuperar usuario y cliente de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        // 🔹 Validar que haya un usuario logueado y un cliente asociado
        if (usuario == null || cliente == null) {
            // 🔹 Si no hay sesión válida, redirigir a la página principal
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // 🔹 Obtener los datos enviados desde el formulario de edición
            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String dni = request.getParameter("dni");
            String telefono = request.getParameter("telefono");
            String correo = request.getParameter("correo");

            // 🔹 Actualizar los campos del cliente con los valores del formulario
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDni(dni);
            cliente.setTelefono(telefono);

            // 🔹 Llamar al DAO para actualizar la información del cliente en la base de datos
            boolean clienteEditado = clienteDAO.editarClientePorUsuario(cliente);

            // 🔹 Actualizar el correo del usuario
            usuario.setCorreo(correo);
            boolean usuarioEditado = usuarioDAO.actualizarCorreo(usuario);

            // 🔹 Verificar si ambas operaciones fueron exitosas
            if (clienteEditado && usuarioEditado) {
                // 🔹 Actualizar los objetos en la sesión para reflejar los cambios
                session.setAttribute("cliente", cliente);
                session.setAttribute("usuario", usuario);

                // 🔹 Redirigir a la vista de perfil con indicador de éxito
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?success=1");
            } else {
                // 🔹 Si falla alguna actualización, redirigir mostrando error
                response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
            }

            // 🔹 Buenas prácticas y consideraciones:
            // - Validar que los campos recibidos del formulario no estén vacíos
            // - Verificar que el correo no esté duplicado en la base de datos
            // - Posible registro de auditoría al editar perfil
            // - Manejo de excepciones más específico para capturar errores de DAO
            // - Validar formato de correo y teléfono
            // - Considerar reglas de negocio para cambios de DNI o datos críticos
            // - Confirmar cambios mediante un mensaje visual en la JSP
            // - Posibilidad de notificar por correo al usuario que se actualizó su perfil
            // - Controlar intentos de edición no autorizados desde fuera del perfil
            // - Garantizar consistencia de datos entre tablas relacionadas
            // - Mantener sesión activa segura después de actualizar datos
            // - Evitar problemas de concurrencia si el usuario tiene varias sesiones abiertas
            // - Posible extensión: permitir actualizar contraseña aquí
            // - Manejo de caracteres especiales para evitar inyección SQL
            // - Validar que el cliente pertenece al usuario logueado
            // - Posible soft-rollback si falla una de las dos operaciones (cliente/usuario)
            // - Evitar mostrar información sensible en la URL
            // - Considerar internacionalización si se desea soportar varios idiomas
            // - Documentar la funcionalidad en manual del sistema
            // - Facilitar futuras extensiones para roles distintos a cliente

        } catch (Exception e) {
            // 🔹 Captura cualquier excepción y redirige mostrando error
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/MiPerfil.jsp?error=1");
        }
    }
}
