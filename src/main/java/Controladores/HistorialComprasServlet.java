/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;

import Modelo.Carrito;
import Modelo.Conexion;
import Modelo.TipoDePago; 
import ModeloDAO.CarritoDAO;
import ModeloDAO.TipoDePagoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author kristhor
 */
@WebServlet(name = "HistorialComprasServlet", urlPatterns = {"/HistorialComprasServlet"})
public class HistorialComprasServlet extends HttpServlet {

    private CarritoDAO carritoDAO;
    private TipoDePagoDAO tipoPagoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        carritoDAO = new CarritoDAO(con);
        tipoPagoDAO = new TipoDePagoDAO(con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idClienteSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int idCliente = (int) session.getAttribute("idClienteSesion");

        // 🔹 obtener historial del cliente
        List<Carrito> historial = carritoDAO.obtenerHistorial(idCliente);
        
        // 🔹 obtener tipos de pago
        List<TipoDePago> tiposPago = tipoPagoDAO.listar();

        // 🔹 pasar a la JSP
        request.setAttribute("historialCompras", historial);
        request.setAttribute("tiposPago", tiposPago);

        // 🔹 redirigir
        request.getRequestDispatcher("/VistasWeb/VistasCliente/historialdecompras.jsp")
               .forward(request, response);
    }
}
