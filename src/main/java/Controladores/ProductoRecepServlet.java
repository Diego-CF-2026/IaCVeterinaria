/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;
import Modelo.Carrito;
import ModeloDAO.CarritoDAO;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import Modelo.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Este servlet maneja las solicitudes (GET y POST) para gestionar los carritos cerrados
 * desde la vista del recepcionista.
 */
@WebServlet(name = "ProductoRecepServlet", urlPatterns = {"/ProductoRecepServlet"})
public class ProductoRecepServlet extends HttpServlet {

     /**
     * Método GET: se encarga de listar los carritos cerrados (pedidos finalizados)
     * y mostrarlos en la vista GestionProductosR.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Se obtiene el parámetro "accion" enviado desde la vista
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        // Se usa un bloque try-with-resources para abrir y cerrar automáticamente la conexión
        try (Connection con = Conexion.getConnection()) {
            CarritoDAO carritoDAO = new CarritoDAO(con);

            switch (accion) {
                case "listar":
                    // Obtiene la lista de carritos con estado "CERRADO"
                    List<Carrito> listaCarritos = carritoDAO.obtenerCarritosCerrados();
                    request.setAttribute("listaCarritos", listaCarritos);
                    // Redirige a la vista del recepcionista
                    request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionProductosR.jsp").forward(request, response);
                    break;

                default:
                    // Si la acción no coincide con ninguna, redirige a la página principal
                    response.sendRedirect("VistasRecep/GestionProductosR.jsp");
                    break;
            }

        } catch (Exception e) {
            // Si ocurre un error, se imprime en consola y redirige a una página de error
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
    
    /**
     * Método POST: se usa para actualizar el estado de entrega de un pedido.
     * Cambia el estadoEntrega a "PROCESO" o "ENTREGADO".
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se obtiene la acción enviada desde el formulario
        String accion = request.getParameter("accion");

        try (Connection con = Conexion.getConnection()) {
            CarritoDAO carritoDAO = new CarritoDAO(con);

            // Si la acción es actualizar el estado de entrega
            if ("actualizarEntrega".equals(accion)) {
                int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));
                String nuevoEstado = request.getParameter("estadoEntrega");

                // Llama al método del DAO para actualizar en la base de datos
                boolean actualizado = carritoDAO.actualizarEstadoEntrega(idCarrito, nuevoEstado);

                if (actualizado) {
                    // Si se actualizó correctamente, recarga la lista
                    response.sendRedirect("ProductoRecepServlet?accion=listar");
                } else {
                    // Si falló la actualización, redirige a la página de error
                    response.sendRedirect("error.jsp");
                }
            } else {
                // Si la acción no es reconocida, vuelve a listar
                response.sendRedirect("ProductoRecepServlet?accion=listar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
