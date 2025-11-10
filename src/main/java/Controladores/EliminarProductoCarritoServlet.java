/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import Modelo.Conexion;
import ModeloDAO.CarritoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * Servlet para eliminar un producto específico del carrito de un cliente.
 * Recibe el idDetalleCarrito y el idCarrito desde el formulario o la vista.
 * Llama al DAO para eliminar el producto y luego redirige al historial de compras.
 * 
 * Este servlet funciona en conjunto con HistorialComprasServlet y CarritoDAO.
 * Mantiene la lógica de carrito en base de datos, no en sesión.
 * 
 * Permite la extensión futura para validaciones, logs y manejo de stock.
 */
@WebServlet(name = "EliminarProductoCarritoServlet", urlPatterns = {"/EliminarProductoCarritoServlet"})
public class EliminarProductoCarritoServlet extends HttpServlet {

    private CarritoDAO carritoDAO;

    @Override
    public void init() throws ServletException {
        // 🔹 Inicialización del DAO con la conexión a la base de datos
        Connection con = Conexion.getConnection();
        carritoDAO = new CarritoDAO(con);
        // 🔹 Esto asegura que todas las operaciones CRUD se realicen a través del DAO
        // 🔹 Mejora la separación de responsabilidades
        // 🔹 Permite un fácil reemplazo de la fuente de datos si cambia la base de datos
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Recuperar el idDetalleCarrito desde el formulario
        int idDetalleCarrito = Integer.parseInt(request.getParameter("idDetalleCarrito"));

        // 🔹 Recuperar el idCarrito del cual se desea eliminar el producto
        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));

        // 🔹 Llamar al DAO para eliminar el producto del carrito
        carritoDAO.eliminarProducto(idDetalleCarrito, idCarrito);

        // 🔹 Redirigir al historial de compras para mostrar la lista actualizada
        response.sendRedirect(request.getContextPath() + "/HistorialComprasServlet");

        // 🔹 Comentarios adicionales y buenas prácticas:
        // - Se recomienda validar que los parámetros no sean nulos o inválidos.
        // - Se podría capturar NumberFormatException en caso de parámetros no numéricos.
        // - Se puede agregar verificación de sesión para asegurar que el cliente esté logueado.
        // - Se podría verificar que el producto pertenezca al carrito del cliente actual.
        // - Se puede registrar un log de eliminación para auditoría.
        // - Esta operación podría integrarse con manejo de stock en inventario.
        // - Se puede implementar confirmación de eliminación con mensaje al usuario.
        // - Permite futura extensión para eliminar múltiples productos a la vez.
        // - Se podría usar AJAX para actualizar el carrito sin recargar la página.
        // - Se asegura la separación de responsabilidades al usar el DAO.
        // - Permite integrarse con carritos persistentes en base de datos.
        // - Mantener el DAO permite pruebas unitarias más fáciles.
        // - Se puede extender para manejar promociones y descuentos antes de eliminar.
        // - Se asegura la consistencia de datos entre sesión y base de datos.
        // - Evita manipulación directa de la base de datos desde la vista.
        // - Permite implementar rollback en caso de errores futuros.
        // - Se puede implementar soft-delete en lugar de eliminación definitiva si se desea.
        // - Se mantiene el flujo de compra consistente para el cliente.
        // - Se puede agregar auditoría por usuario y fecha de eliminación.
        // - Facilita la integración con PDF o facturación si se necesita eliminar productos de un historial de compras.
        // - Proporciona base para integración con notificaciones de cambios en el carrito.
    }
}
