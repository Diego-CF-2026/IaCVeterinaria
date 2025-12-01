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
            e.printStackTrace();   // 🔹 Manejo de excepción general por seguridad
        }

        if (idProveedor != null && idProveedor.matches("\\d+")) {                            // 🔹 Redireccionar siempre al detalle del proveedor
            response.sendRedirect("ProveedorServlet?accion=ver&id=" + idProveedor);           // 🔹 Mantener modal abierto en la vista del proveedor
        } else {
            response.sendRedirect("ProveedorServlet?accion=listar");        // 🔹 Redirigir a lista general de proveedores si no hay id válido
        }
    }

    private void agregarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {       // 🔹 Crear objeto contacto y asignar datos del formulario
        try {
            ContactoProveedor contacto = new ContactoProveedor();
            contacto.setIdProveedor(Integer.parseInt(request.getParameter("idProveedor")));
            contacto.setNombreContacto(request.getParameter("nombreContacto"));
            contacto.setCargo(request.getParameter("cargo"));
            contacto.setTelefono(request.getParameter("telefono"));
            contacto.setCorreoContacto(request.getParameter("correoContacto"));

            dao.agregarContacto(contacto);   // 🔹 Llamar al DAO para agregar el contacto en la base de datos
          
        } catch (Exception e) {
            e.printStackTrace();  // 🔹 Manejo de excepciones por seguridad y logging
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

            dao.actualizarContacto(contacto);  // 🔹 Llamar al DAO para actualizar contacto en base de datos

        } catch (Exception e) {
            // 🔹 Manejo de excepciones para seguridad y logging
            e.printStackTrace();
        }
    }

    private void eliminarContacto(HttpServletRequest request, ContactoProveedorDAO dao) {
        try {
            int idContacto = Integer.parseInt(request.getParameter("idContacto"));  // 🔹 Obtener id del contacto a eliminar

            dao.eliminarContacto(idContacto);  // 🔹 Llamar al DAO para eliminar el contacto de la base de datos

        } catch (Exception e) {
            
            e.printStackTrace();  // 🔹 Manejo de errores de forma segura
        }
    }
}
