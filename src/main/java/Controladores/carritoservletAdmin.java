package Controladores;

import Modelo.Carrito;
import Modelo.Conexion; // 💡 ¡CORREGIDO! Importando la clase 'Conexion' de tu paquete 'Modelo'
import ModeloDAO.CarritoDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador para la administración de carritos, incluyendo la generación del Reporte de Ventas.
 */
@WebServlet(name = "carritoservletAdmin", urlPatterns = {"/carritoservletAdmin"})
public class carritoservletAdmin extends HttpServlet {

    /**
     * Maneja las solicitudes GET y POST, enfocándose en el reporte de ventas.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        // --- 1. CAPTURA Y PROCESAMIENTO DE PARÁMETROS ---
        
        // Obtener mes y año. Si no se especifican, usar el mes y año actual.
        int anio = getIntParam(request, "anio", LocalDate.now().getYear());
        int mes = getIntParam(request, "mes", LocalDate.now().getMonthValue());
        
        String mensaje;
        BigDecimal ventasTotales = BigDecimal.ZERO;
        List<Carrito> ventasCompletadas = null;
        
        // --- 2. ACCESO AL DAO Y LÓGICA DE NEGOCIO ---
        
        Connection con = null;
        try {
            // Obtener la conexión a la BD usando tu clase 'Conexion'
            // 💡 ASUMO que tu clase 'Conexion' tiene un método estático 'getConnection()'
            Conexion conexionUtil = new Conexion();
            con = conexionUtil.getConnection(); 
            
            CarritoDAO carritoDAO = new CarritoDAO(con);

            // Obtener el listado de ventas filtrado por mes y año
            ventasCompletadas = carritoDAO.adminObtenerVentasPorMesAnio(mes, anio);
            
            // 🔹 Calcular el total global de las ventas
            if (ventasCompletadas != null && !ventasCompletadas.isEmpty()) {
                for (Carrito venta : ventasCompletadas) {
                    ventasTotales = ventasTotales.add(venta.getTotal());
                }
                mensaje = "✅ Reporte generado correctamente para el mes " + mes + " del año " + anio + ".";
            } else {
                mensaje = "⚠️ No se encontraron ventas de productos completadas para el periodo seleccionado.";
            }

        } catch (Exception e) {
            mensaje = "❌ Error al procesar el reporte: " + e.getMessage();
            e.printStackTrace();
        } finally {
            // Asegurarse de cerrar la conexión
            if (con != null) {
                try {
                    con.close();
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // --- 3. PREPARACIÓN Y REDIRECCIÓN A LA VISTA ---
        
        // Establecer atributos en el request para que la vista (JSP) los use
        request.setAttribute("ventasTotales", ventasTotales); // Total global (BigDecimal)
        request.setAttribute("totalVentas", ventasCompletadas != null ? ventasCompletadas.size() : 0); // Total de registros
        request.setAttribute("ventasCompletadas", ventasCompletadas); // Lista de ventas
        request.setAttribute("mes", mes);
        request.setAttribute("anio", anio);
        request.setAttribute("mensaje", mensaje);
        
        // Redirigir a la vista JSP del reporte (AJUSTAR RUTA SEGÚN TU PROYECTO)
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/CarritosEntregados.jsp").forward(request, response);
    }

    /**
     * Método auxiliar para obtener parámetros enteros.
     */
    private int getIntParam(HttpServletRequest request, String name, int defaultValue) {
        String param = request.getParameter(name);
        try {
            return (param != null && !param.isEmpty()) ? Integer.parseInt(param) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Controlador para la administración y reporte de ventas.";
    }
    // </editor-fold>
}