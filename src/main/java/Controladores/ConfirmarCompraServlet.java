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
            // 🔹 Posibles mejoras:
            // - Guardar fecha y hora de confirmación
            // - Registrar transacción para auditoría
            // - Enviar correo de confirmación al cliente
            // - Manejar concurrencia si varios clientes confirman simultáneamente
            // - Validar que el carrito aún tenga productos antes de confirmar
            // - Controlar inventario para evitar sobreventa
            // - Validar que el método de pago exista
            // - Manejar posibles errores de base de datos
            // - Integración con sistema de pagos externo
            // - Control de sesión para verificar cliente logueado
            // - Registrar usuario que realizó la acción para logs
            // - Notificar al administrador si hay problemas
            // - Posibilidad de revertir la compra si falla la transacción
            // - Limpiar carrito temporal después de confirmar
            // - Registrar monto total de la compra
            // - Manejo de impuestos y descuentos si aplica
            // - Validar que idCarrito y idPago sean positivos
            // - Preparar para soporte multimoneda en el futuro
            // - Documentar pasos en manual de operaciones
            // - Manejar seguridad en los parámetros recibidos
        } else {
            // 🔹 Compra no pudo ser confirmada
            request.setAttribute("compraError", "No se pudo confirmar la compra. Intenta nuevamente.");
            // 🔹 Redirigir al historial de compras con mensaje de error
            request.getRequestDispatcher("VistasWeb/VistasCliente/historialdecompras.jsp")
                   .forward(request, response);
            // 🔹 Posibles mejoras:
            // - Diferenciar tipos de error (inventario, pago, conexión)
            // - Registrar intento fallido en logs
            // - Notificar al cliente del error vía correo
            // - Sugerir pasos para corregir problema
            // - Preparar reintento automático si es temporal
            // - Validar integridad de datos del carrito antes de intentar
            // - Mantener consistencia de estado del carrito
            // - Implementar rollback si se actualizó parcialmente
            // - Controlar sesión caducada o expirada
            // - Evitar duplicación de confirmación
            // - Notificar al administrador si falla repetidamente
            // - Preparar mensajes amigables para UI
        }
    }
}
