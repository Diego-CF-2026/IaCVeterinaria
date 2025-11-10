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
 * Servlet responsable de actualizar la cantidad de un ítem dentro de un carrito
 * (detalle de carrito) a través de una acción de sumar o restar.
 *
 * Flujo:
 *  - init(): obtiene una conexión y crea la instancia de CarritoDAO.
 *  - doPost(): recibe idDetalleCarrito, idCarrito y acción ("sumar" | "restar"),
 *              delega a la DAO la actualización y redirige al historial.
 *
 * Notas:
 *  - Se asume que la acción y los IDs son válidos y provienen de un formulario/JS
 *    (no hay validación adicional aquí; se delega a la capa DAO/bd).
 *  - Tras la operación, se aplica PRG (Post/Redirect/Get) redirigiendo al servlet
 *    HistorialComprasServlet para evitar reenvío de formularios.
 *  - La conexión es recuperada desde Modelo.Conexion (patrón centralizado).
 *  - Este servlet está mapeado a /ActualizarCantidadServlet.
 *
 * Autor original (según plantilla): kristhor
 */
@WebServlet(name = "ActualizarCantidadServlet", urlPatterns = {"/ActualizarCantidadServlet"})
public class ActualizarCantidadServlet extends HttpServlet {

    // DAO para operaciones sobre el carrito (detalle, cantidades, etc.)
    private CarritoDAO carritoDAO;

    /**
     * Inicializa el servlet creando la instancia de DAO con una conexión activa.
     * Se ejecuta una sola vez por ciclo de vida del servlet.
     */
    @Override
    public void init() throws ServletException {
        // Obtiene una conexión compartida desde la clase de utilería Conexion
        Connection con = Conexion.getConnection();
        // Inyecta la conexión en el DAO (responsable de las consultas/actualizaciones)
        carritoDAO = new CarritoDAO(con);
    }

    /**
     * Procesa las solicitudes POST para actualizar la cantidad de un detalle de carrito.
     * Parámetros esperados:
     *  - idDetalleCarrito: ID del detalle a modificar.
     *  - idCarrito: ID del carrito al que pertenece el detalle.
     *  - accion: "sumar" o "restar" para incrementar/decrementar la cantidad.
     *
     * Comportamiento:
     *  - Llama a carritoDAO.actualizarCantidad(...) para efectuar el cambio.
     *  - Redirige a /HistorialComprasServlet (patrón PRG).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Extrae parámetros del formulario/cliente (se asume formato correcto)
        int idDetalleCarrito = Integer.parseInt(request.getParameter("idDetalleCarrito"));
        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));
        String accion = request.getParameter("accion"); // valores esperados: "sumar" o "restar"

        // Delegación de la lógica de negocio/acceso a datos a la capa DAO
        carritoDAO.actualizarCantidad(idDetalleCarrito, idCarrito, accion);

        // PRG: evita reenvío del formulario al refrescar; vuelve a la vista del historial
        response.sendRedirect(request.getContextPath() + "/HistorialComprasServlet");
    }
}
