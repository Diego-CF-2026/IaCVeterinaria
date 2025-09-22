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

/**
 *
 * @author kristhor
 */
@WebServlet("/ConfirmarCompraServlet")
public class ConfirmarCompraServlet extends HttpServlet {
    private CarritoDAO carritoDAO;

    @Override
    public void init() {
        carritoDAO = new CarritoDAO(Conexion.getConnection());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));
        int idPago = Integer.parseInt(request.getParameter("idPago"));

        boolean exito = carritoDAO.confirmarCompra(idCarrito, idPago);

        if (exito) {
            request.setAttribute("compraExitosa", true);
            request.getRequestDispatcher("VistasWeb/VistasCliente/historialdecompras.jsp")
                   .forward(request, response);
        } else {
            request.setAttribute("compraError", "No se pudo confirmar");
            request.getRequestDispatcher("VistasWeb/VistasCliente/historialdecompras.jsp")
                   .forward(request, response);
        }
    }
}
