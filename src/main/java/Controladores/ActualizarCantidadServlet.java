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
@WebServlet(name = "ActualizarCantidadServlet", urlPatterns = {"/ActualizarCantidadServlet"})
public class ActualizarCantidadServlet extends HttpServlet {
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
        String accion = request.getParameter("accion"); // sumar o restar

        carritoDAO.actualizarCantidad(idDetalleCarrito, idCarrito, accion);

        // redirigir de nuevo al historial
        response.sendRedirect(request.getContextPath() + "/HistorialComprasServlet");
    }
}
