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
 * Servlet para operaciones del carrito desde el lado del cliente.
 * - init(): obtiene una conexión y prepara CarritoDAO.
 * - doPost(): recibe acciones (por ahora "agregar") y devuelve JSON.
 *
 * Seguridad:
 * - Requiere sesión válida con atributo "idClienteSesion".
 *
 * Respuestas:
 * - JSON con {exito: boolean, mensaje?: string} para facilitar manejo en JS.
 */
@WebServlet(name = "CarritoServlet", urlPatterns = {"/CarritoServlet"})
public class CarritoServlet extends HttpServlet {

    // Acceso a operaciones del carrito (agregar, actualizar, etc.)
    private CarritoDAO carritoDAO;

    /**
     * Inicializa el servlet y crea la instancia de DAO usando la conexión compartida.
     * Se ejecuta una vez en el ciclo de vida del servlet.
     */
    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection();
        carritoDAO = new CarritoDAO(con);
    }

    /**
     * Maneja solicitudes POST del cliente.
     * Parámetro requerido: "accion".
     * Acciones soportadas:
     *  - "agregar": agrega un producto al carrito del cliente autenticado.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        // Obtiene la sesión existente sin crear una nueva
        HttpSession sesion = request.getSession(false);

        // Validación de autenticación: debe existir "idClienteSesion"
        if (sesion == null || sesion.getAttribute("idClienteSesion") == null) {
            response.setContentType("application/json");
            response.getWriter().write("{\"exito\": false, \"mensaje\":\"Usuario no logueado\"}");
            return;
        }

        // ID del cliente autenticado (proporcionado al iniciar sesión)
        int idCliente = (int) sesion.getAttribute("idClienteSesion");

        switch (accion) {
            case "agregar":
                try {
                    // --- Bloque de depuración: imprime insumos recibidos ---
                    System.out.println("===== DEBUG CarritoServlet =====");
                    System.out.println("accion -> " + accion);
                    System.out.println("idCliente (session) -> " + idCliente);
                    System.out.println("param idProducto -> '" + request.getParameter("idProducto") + "'");
                    System.out.println("param cantidad   -> '" + request.getParameter("cantidad") + "'");
                    System.out.println("================================");

                    // Lee parámetros crudos para validar presencia (evita NumberFormatException temprana)
                    String idProductoParam = request.getParameter("idProducto");
                    String cantidadParam = request.getParameter("cantidad");

                    // Validación básica de parámetros requeridos
                    if (idProductoParam == null || idProductoParam.isBlank() ||
                        cantidadParam == null || cantidadParam.isBlank()) {
                        response.setContentType("application/json");
                        response.getWriter().write("{\"exito\": false, \"mensaje\":\"Faltan parámetros idProducto o cantidad\"}");
                        return;
                    }

                    // Parseo seguro tras validar no-vacío
                    int idProducto = Integer.parseInt(idProductoParam);
                    int cantidad = Integer.parseInt(cantidadParam);

                    // Log de depuración con valores parseados
                    System.out.println("DEBUG >> idProducto=" + idProducto + ", cantidad=" + cantidad + ", idCliente=" + idCliente);

                    // Delegar a la capa DAO la lógica de agregado
                    boolean exito = carritoDAO.agregarProducto(idCliente, idProducto, cantidad);

                    // Respuesta JSON estándar para consumo del front
                    response.setContentType("application/json");
                    response.getWriter().write("{\"exito\": " + exito + "}");
                } catch (Exception e) {
                    // Manejo genérico de errores (formato, BD, nulls, etc.)
                    e.printStackTrace();

                    // Sanitiza el mensaje para no romper el JSON
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
                // Acción no soportada -> 400 Bad Request con mensaje
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
        }
    }
}
