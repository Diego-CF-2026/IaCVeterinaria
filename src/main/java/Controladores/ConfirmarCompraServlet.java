package Controladores;

import Modelo.Conexion;
import ModeloDAO.CarritoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet encargado de confirmar la compra del carrito de un cliente.
 * Recibe el id del carrito y el tipo de pago, y actualiza la base de datos
 * para marcar la compra como confirmada.
 * Redirige al historial de compras con mensaje de éxito o error.
 */
@WebServlet("/ConfirmarCompraServlet")
public class ConfirmarCompraServlet extends HttpServlet {
    
    private CarritoDAO carritoDAO;

    @Override
    public void init() {
        // 🔹 Inicializar el DAO con la conexión a la base de datos
        carritoDAO = new CarritoDAO(Conexion.getConnection());
        // 🔹 Posibles mejoras:
        // - Validar que la conexión no sea nula
        // - Manejar posibles excepciones de conexión
        // - Preparar pool de conexiones para eficiencia
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // 🔹 Obtener los parámetros enviados desde el formulario
        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));
        int idPago = Integer.parseInt(request.getParameter("idPago"));

        // 🔹 Llamar al DAO para confirmar la compra en la base de datos
        boolean exito = carritoDAO.confirmarCompra(idCarrito, idPago);

        // 🔹 Manejo de resultados y retroalimentación al usuario
        if (exito) {
            // 🔹 Compra confirmada exitosamente
            request.setAttribute("compraExitosa", true);
            // 🔹 Redirigir al historial de compras mostrando mensaje de éxito
            request.getRequestDispatcher("VistasWeb/VistasCliente/historialdecompras.jsp")
                   .forward(request, response);
            
        } else {
            // 🔹 Compra no pudo ser confirmada
            request.setAttribute("compraError", "No se pudo confirmar la compra. Intenta nuevamente.");
            // 🔹 Redirigir al historial de compras con mensaje de error
            request.getRequestDispatcher("VistasWeb/VistasCliente/historialdecompras.jsp")
                   .forward(request, response);
            
        }
    }
}
