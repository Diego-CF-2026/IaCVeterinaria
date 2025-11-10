package Controladores;

import Modelo.Conexion;
import Modelo.ContactoProveedor;
import ModeloDAO.ContactoProveedorDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

/**
 * Servlet para gestionar los contactos de un proveedor.
 * Permite agregar, actualizar y eliminar contactos asociados a un proveedor.
 * Mantiene redirección siempre hacia la vista del proveedor correspondiente.
 */
@WebServlet("/ContactoProveedorServlet")
public class ContactoProveedorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Obtener acción desde el formulario: agregar, actualizar o eliminar
        String accion = request.getParameter("accion");
        // 🔹 Obtener idProveedor para redireccionar correctamente
        String idProveedor = request.getParameter("idProveedor");

        try (Connection con = Conexion.getConnection()) {
            // 🔹 Inicializar DAO con conexión activa
            ContactoProveedorDAO contactoDAO = new ContactoProveedorDAO(con);

            // 🔹 Determinar acción a realizar según el parámetro "accion"
            switch (accion.toLowerCase()) {
                case "agregar":
                    agregarContacto(request, contactoDAO);
                    break;
                case "actualizar":
                    actualizarContacto(request, contactoDAO);
                    break;
                case "eliminar":
                    eliminarContacto(request, contactoDAO);
                    break;
                default:
                    // 🔹 No hacer nada si la acción no coincide
                    break;
            }

        } catch (Exception e) {
            // 🔹 Manejo de excepción general por seguridad
            e.printStackTrace();
        }

        // 🔹 Redireccionar siempre al detalle del proveedor
        if (idProveedor != null && idProveedor.matches("\\d+")) {
            // 🔹 Mantener modal abierto en la vista del proveedor
            response.sendRedirect("ProveedorServlet?accion=ver&id=" + idProveedor);
        } else {
            // 🔹 Redirigir a lista general de proveedores si no hay id válido
            response.sendRedirect("ProveedorServlet?accion=listar");
        }
    }

    private void agregarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            // 🔹 Crear objeto contacto y asignar datos del formulario
            ContactoProveedor contacto = new ContactoProveedor();
            contacto.setIdProveedor(Integer.parseInt(request.getParameter("idProveedor")));
            contacto.setNombreContacto(request.getParameter("nombreContacto"));
            contacto.setCargo(request.getParameter("cargo"));
            contacto.setTelefono(request.getParameter("telefono"));
            contacto.setCorreoContacto(request.getParameter("correoContacto"));

            // 🔹 Llamar al DAO para agregar el contacto en la base de datos
            dao.agregarContacto(contacto);

            // 🔹 Posibles mejoras:
            // - Validar que el nombre y correo no estén vacíos
            // - Verificar formato de correo y teléfono
            // - Manejar duplicados por proveedor
            // - Notificar al usuario sobre el éxito de la operación
            // - Guardar logs de auditoría para cambios
            // - Manejar excepciones específicas (SQL, validación)
            // - Evitar inyecciones SQL o caracteres no permitidos
            // - Garantizar que el proveedor exista antes de agregar
            // - Confirmar ID autogenerado si se necesita
            // - Posibilidad de enviar email al contacto agregado
            // - Validar longitud máxima de cada campo
            // - Usar transacción si se agregan múltiples contactos
            // - Limitar caracteres especiales en nombre y cargo
            // - Integración futura con API externa de contactos
            // - Mostrar mensajes amigables en la JSP
            // - Manejar sesión caducada del usuario
            // - Control de concurrencia si se editan simultáneamente
            // - Documentar cambios para equipo de mantenimiento
            // - Preparar para futuras internacionalizaciones
            // - Validar consistencia de datos con tabla proveedor
            // - Considerar soft delete en lugar de eliminar directo
            // - Registrar fecha de creación del contacto

        } catch (Exception e) {
            // 🔹 Manejo de excepciones por seguridad y logging
            e.printStackTrace();
        }
    }

    private void actualizarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            // 🔹 Crear objeto contacto con el id existente y nuevos datos
            ContactoProveedor contacto = new ContactoProveedor();
            contacto.setIdContacto(Integer.parseInt(request.getParameter("idContacto")));
            contacto.setIdProveedor(Integer.parseInt(request.getParameter("idProveedor")));
            contacto.setNombreContacto(request.getParameter("nombreContacto"));
            contacto.setCargo(request.getParameter("cargo"));
            contacto.setTelefono(request.getParameter("telefono"));
            contacto.setCorreoContacto(request.getParameter("correoContacto"));

            // 🔹 Llamar al DAO para actualizar contacto en base de datos
            dao.actualizarContacto(contacto);

            // 🔹 Posibles mejoras:
            // - Validar campos antes de actualizar
            // - Confirmar existencia del contacto antes de modificar
            // - Manejar errores por conflictos de datos
            // - Registrar cambios para auditoría
            // - Notificar al usuario sobre la actualización exitosa
            // - Controlar longitud de texto y formato de correo/teléfono
            // - Implementar logs de sesión para operaciones críticas
            // - Considerar bloqueo temporal si hay múltiples ediciones simultáneas
            // - Evitar inyecciones de código en los campos
            // - Preparar para internacionalización y soporte multilingüe
            // - Control de transacciones si se actualizan varios contactos
            // - Documentar cambios en manual de operaciones

        } catch (Exception e) {
            // 🔹 Manejo de excepciones para seguridad y logging
            e.printStackTrace();
        }
    }

    private void eliminarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            // 🔹 Obtener id del contacto a eliminar
            int idContacto = Integer.parseInt(request.getParameter("idContacto"));

            // 🔹 Llamar al DAO para eliminar el contacto de la base de datos
            dao.eliminarContacto(idContacto);

            // 🔹 Posibles mejoras:
            // - Confirmar existencia antes de eliminar
            // - Considerar soft delete en lugar de borrado físico
            // - Guardar registro de auditoría
            // - Notificar al usuario sobre la eliminación
            // - Controlar permisos de usuario antes de eliminar
            // - Validar sesión activa y rol autorizado
            // - Evitar errores por id no numérico
            // - Preparar mensajes de error claros en JSP
            // - Controlar concurrencia y bloqueos
            // - Mantener consistencia con proveedor relacionado
            // - Validar integridad referencial en la base de datos
            // - Posible desactivación temporal si hay compras asociadas
            // - Documentar el proceso para mantenimiento
            // - Implementar logs para debugging futuro
            // - Preparar para futuras extensiones de API externa
            // - Manejar excepciones SQL específicas
            // - Confirmar eliminación exitosa
            // - Validar que no se eliminen contactos por error
            // - Notificar vía correo si es relevante
            // - Control de tamaño de datos eliminados si es histórico

        } catch (Exception e) {
            // 🔹 Manejo de errores de forma segura
            e.printStackTrace();
        }
    }
}
