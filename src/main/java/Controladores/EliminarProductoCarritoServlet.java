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
 *
 * @author kristhor
 */
@WebServlet(name = "EliminarProductoCarritoServlet", urlPatterns = {"/EliminarProductoCarritoServlet"})
public class EliminarProductoCarritoServlet extends HttpServlet {
    private CarritoDAO carritoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        carritoDAO = new CarritoDAO(con);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idDetalleCarrito = Integer.parseInt(request.getParameter("idDetalleCarrito"));
        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));

        carritoDAO.eliminarProducto(idDetalleCarrito, idCarrito);

        // Redirigir al historial de compras
        response.sendRedirect(request.getContextPath() + "/HistorialComprasServlet");
    }
}
