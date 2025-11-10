/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet que maneja la finalización de una compra realizada por un cliente.
 * Recibe los datos de un producto desde el formulario, lo almacena en la sesión
 * como parte del historial de compras y redirige al cliente al historial.
 * 
 * Esta versión mantiene comentarios detallados explicando cada acción y buenas prácticas.
 * No realiza persistencia en base de datos, solo maneja la sesión temporalmente.
 * 
 * Es ideal para pruebas, prototipos o simulaciones antes de integrar DAO y base de datos real.
 * 
 * @author PROPIETARIO
 */
@WebServlet("/FinalizarCompraServlet")
public class FinalizarCompraServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Obtener el parámetro "producto" enviado desde el formulario del cliente
        String producto = request.getParameter("producto");

        // 🔹 Obtener la sesión actual del usuario; si no existe, se crea automáticamente
        HttpSession session = request.getSession();

        // 🔹 Recuperar el historial de compras almacenado en sesión
        // Si no existe aún, se crea un nuevo ArrayList para almacenar los productos
        List<String> historial = (List<String>) session.getAttribute("historialCompras");
        if (historial == null) {
            historial = new ArrayList<>();
        }

        // 🔹 Agregar el producto actual al historial de compras
        historial.add(producto);

        // 🔹 Actualizar el atributo de sesión para mantener el historial actualizado
        session.setAttribute("historialCompras", historial);

        // 🔹 Redirigir al cliente a la página que muestra su historial de compras
        response.sendRedirect(request.getContextPath() + "/VistasWeb/VistasCliente/historialdecompras.jsp");

        // 🔹 Comentarios y buenas prácticas:
        // - La sesión se usa para mantener los datos del usuario de manera temporal.
        // - Para persistencia real, se debería guardar en la base de datos usando un DAO.
        // - Se recomienda validar el parámetro 'producto' para evitar datos nulos o vacíos.
        // - Se podría agregar un ID de usuario para diferenciar historiales entre clientes.
        // - Este servlet solo maneja compras individuales por POST.
        // - En un entorno real, se podría validar stock antes de finalizar la compra.
        // - Se puede implementar control de transacciones si se integra con DAO.
        // - Se puede agregar funcionalidad para mostrar confirmación de compra.
        // - Se podría agregar manejo de errores si 'producto' es inválido.
        // - Es posible mejorar la experiencia usando AJAX para actualizar historial sin recargar la página.
        // - Se puede registrar un log de compras para auditoría y seguimiento.
        // - Permite extenderse para manejar descuentos, cupones o promociones.
        // - Mantener el historial en sesión evita la sobrecarga de la base de datos en prototipos.
        // - Este servlet es útil para pruebas de flujo antes de integrar pagos reales.
        // - Se podría agregar un límite de historial para no sobrecargar la memoria de sesión.
        // - En producción, se debe limpiar la sesión al cerrar sesión o al finalizar compra.
        // - Se puede integrar con PDF o facturación electrónica.
        // - Es posible agregar validaciones de seguridad para evitar manipulación de parámetros.
        // - Compatible con futuras mejoras de carrito persistente en base de datos.
        // - Se asegura que la página de historial reciba siempre la lista más reciente de productos.
    }
}
