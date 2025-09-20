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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author kristhor
 */
@WebServlet(name = "CarritoServlet", urlPatterns = {"/CarritoServlet"})
public class CarritoServlet extends HttpServlet {
    private CarritoDAO carritoDAO;

    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        carritoDAO = new CarritoDAO(con);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("idCliente") == null) {
            response.setContentType("application/json");
            response.getWriter().write("{\"exito\": false, \"mensaje\":\"Usuario no logueado\"}");
            return;
        }
        int idCliente = (int) sesion.getAttribute("idCliente");

        switch (accion) {
            case "agregar":


              try {
                  
                    // DEBUG: imprimir TODO lo que llega
                    System.out.println("===== DEBUG CarritoServlet =====");
                    System.out.println("accion -> " + accion);
                    System.out.println("idCliente (session) -> " + idCliente);
                    System.out.println("param idProducto -> '" + request.getParameter("idProducto") + "'");
                    System.out.println("param cantidad   -> '" + request.getParameter("cantidad") + "'");
                    System.out.println("================================");

                    String idProductoParam = request.getParameter("idProducto");
                    String cantidadParam = request.getParameter("cantidad");

                    if (idProductoParam == null || idProductoParam.isBlank() ||
                        cantidadParam == null || cantidadParam.isBlank()) {
                        response.setContentType("application/json");
                        response.getWriter().write("{\"exito\": false, \"mensaje\":\"Faltan parámetros idProducto o cantidad\"}");
                        return;
                    }
                    
                int idProducto = Integer.parseInt(request.getParameter("idProducto"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                System.out.println("DEBUG >> idProducto=" + idProducto + ", cantidad=" + cantidad + ", idCliente=" + idCliente);

                boolean exito = carritoDAO.agregarProducto(idCliente, idProducto, cantidad);

                response.setContentType("application/json");
                response.getWriter().write("{\"exito\": " + exito + "}");
                } catch (Exception e) {
                    e.printStackTrace();

                    // Escapar caracteres problemáticos en el mensaje
                    String errorMsg = e.getMessage();
                    if (errorMsg != null) {
                        errorMsg = errorMsg.replace("\"", "\\\"").replace("\n", " ");
                    } else {
                        errorMsg = "Error desconocido";
                    }

                    response.setContentType("application/json");
                    response.getWriter().write("{\"exito\": false, \"mensaje\":\"Error en servidor: " + errorMsg + "\"}");
                }
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        }
    }
}