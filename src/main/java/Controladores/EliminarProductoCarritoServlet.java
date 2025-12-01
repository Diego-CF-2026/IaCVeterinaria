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


@WebServlet(name = "EliminarProductoCarritoServlet", urlPatterns = {"/EliminarProductoCarritoServlet"})
public class EliminarProductoCarritoServlet extends HttpServlet {

    private CarritoDAO carritoDAO;

    @Override
    public void init() throws ServletException {
        // 🔹 Inicialización del DAO con la conexión a la base de datos
        Connection con = Conexion.getConnection();   // 🔹 Inicialización del DAO con la conexión a la base de datos
        carritoDAO = new CarritoDAO(con);         // 🔹 Permite un fácil reemplazo de la fuente de datos si cambia la base de datos
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idDetalleCarrito = Integer.parseInt(request.getParameter("idDetalleCarrito"));          // 🔹 Recuperar el idDetalleCarrito desde el formulario

        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));  // 🔹 Recuperar el idCarrito del cual se desea eliminar el producto

        carritoDAO.eliminarProducto(idDetalleCarrito, idCarrito);  // 🔹 Llamar al DAO para eliminar el producto del carrito

        response.sendRedirect(request.getContextPath() + "/HistorialComprasServlet");  // 🔹 Redirigir al historial de compras para mostrar la lista actualizada

    }
}
