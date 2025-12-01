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
 * Servlet que gestiona el historial de compras de los clientes.
 * Permite obtener todos los carritos/compras realizados por un cliente
 * y mostrar los tipos de pago disponibles para cada compra.
 * 
 * Se asegura de que el usuario esté autenticado antes de mostrar el historial.
 * 
 * @author kristhor
 */
@WebServlet(name = "HistorialComprasServlet", urlPatterns = {"/HistorialComprasServlet"})
public class HistorialComprasServlet extends HttpServlet {

    // DAO para interactuar con la tabla Carrito
    private CarritoDAO carritoDAO;
    // DAO para interactuar con la tabla TipoDePago
    private TipoDePagoDAO tipoPagoDAO;

    @Override
    public void init() throws ServletException {
        // Se obtiene la conexión a la base de datos desde la clase Conexion
        Connection con = Conexion.getConnection();
        // Se inicializan los DAOs con la conexión
        carritoDAO = new CarritoDAO(con);
        tipoPagoDAO = new TipoDePagoDAO(con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se obtiene la sesión actual, si existe; si no, devuelve null
        HttpSession session = request.getSession(false);
        // Si no hay sesión o no existe el idClienteSesion, redirigir al login
        if (session == null || session.getAttribute("idClienteSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int idCliente = (int) session.getAttribute("idClienteSesion");   // Obtener el ID del cliente de la sesión

        List<Carrito> historial = carritoDAO.obtenerHistorial(idCliente);  // 🔹 Obtener el historial de compras del cliente usando el DAO

        List<TipoDePago> tiposPago = tipoPagoDAO.listar();  // 🔹 Obtener la lista de tipos de pago disponibles
 
        request.setAttribute("historialCompras", historial);          // 🔹 Pasar el historial de compras a la JSP

        request.setAttribute("tiposPago", tiposPago);  // 🔹 Pasar los tipos de pago a la JSP para poder mostrarlos en cada compra

        request.getRequestDispatcher("/VistasWeb/VistasCliente/historialdecompras.jsp")   // 🔹 Redirigir a la página JSP que muestra el historial de compras del cliente
               .forward(request, response);

    }
}
