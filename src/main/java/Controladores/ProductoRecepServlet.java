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

@WebServlet(name = "ProductoRecepServlet", urlPatterns = {"/ProductoRecepServlet"})
public class ProductoRecepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        try (Connection con = Conexion.getConnection()) {
            CarritoDAO carritoDAO = new CarritoDAO(con);

            switch (accion) {
                case "listar":
                    List<Carrito> listaCarritos = carritoDAO.obtenerCarritosCerrados();
                    request.setAttribute("listaCarritos", listaCarritos);
                    request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionProductosR.jsp").forward(request, response);
                    break;

                default:
                    response.sendRedirect("VistasRecep/GestionProductosR.jsp");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        try (Connection con = Conexion.getConnection()) {
            CarritoDAO carritoDAO = new CarritoDAO(con);

            if ("actualizarEntrega".equals(accion)) {
                int idCarrito = Integer.parseInt(request.getParameter("idCarrito"));
                String nuevoEstado = request.getParameter("estadoEntrega");

                boolean actualizado = carritoDAO.actualizarEstadoEntrega(idCarrito, nuevoEstado);

                if (actualizado) {
                    response.sendRedirect("ProductoRecepServlet?accion=listar");
                } else {
                    response.sendRedirect("error.jsp");
                }
            } else {
                response.sendRedirect("ProductoRecepServlet?accion=listar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
