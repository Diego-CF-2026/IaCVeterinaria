package Controlador;

import Modelo.Cita;
import ModeloDAO.CitaDAO;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

// Importaciones para Jakarta EE
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "ReporteGananciasServlet", urlPatterns = {"/ReporteGananciasServlet"})
public class ReporteGananciasServlet extends HttpServlet { 

    // Ruta a tu JSP
    private static final String VISTA_REPORTE = "/VistasWeb/VistasAdmin/GestionGanancias.jsp"; 

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener mes y año (o usar el actual por defecto)
        String mesParam = request.getParameter("mes");
        String anioParam = request.getParameter("anio");
        
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH) + 1;
        int anio = cal.get(Calendar.YEAR);
        
        String mensaje = null;

        if (mesParam != null && anioParam != null) {
            // Viene de un envío de formulario
            try {
                mes = Integer.parseInt(mesParam);
                anio = Integer.parseInt(anioParam);
                
                if (mes < 1 || mes > 12 || anio < 2020 || anio > 2030) {
                    mensaje = "❌ Error: El mes o el año están fuera de los rangos permitidos.";
                    mes = cal.get(Calendar.MONTH) + 1;
                    anio = cal.get(Calendar.YEAR);
                }
            } catch (NumberFormatException e) {
                mensaje = "❌ Error: El mes o el año deben ser números enteros válidos.";
                mes = cal.get(Calendar.MONTH) + 1;
                anio = cal.get(Calendar.YEAR);
            }
        }
        
        // 2. Ejecutar la lógica de negocio (DAO)
        CitaDAO dao = new CitaDAO();
        double ganancias = 0.0;
        List<Cita> citas = null;

        if (mensaje == null || !mensaje.startsWith("❌")) {
            try {
                // LLAMADA AL DAO
                citas = dao.listarCitasCompletadasPorMesYAnio(mes, anio);
                
                if (citas != null && !citas.isEmpty()) {
                    // CÁLCULO DE GANANCIAS Y CONTEO
                    for (Cita cita : citas) {
                        // OJO: Si cita.getPrecio() da null, esto puede fallar. Asegúrate que siempre sea double/cero.
                        ganancias += cita.getPrecio(); 
                    }
                    mensaje = "✅ Reporte generado correctamente para el mes " + mes + " (" + anio + "). Citas encontradas: " + citas.size();
                } else {
                    ganancias = 0.0;
                    mensaje = "⚠️ No se encontraron citas completadas para el mes " + mes + " y año " + anio + "."; 
                }
            } catch (Exception e) {
                System.err.println("Error en Servlet al generar reporte: " + e.getMessage());
                // Importante: Registra la traza del error para debugging en consola
                e.printStackTrace(); 
                mensaje = "❌ Error en el servidor al consultar la base de datos: " + e.getMessage();
            }
        }

        // 3. Establecer atributos para el JSP
        // 🔑 CORRECCIÓN CLAVE: Nombres de atributos consistentes con el JSP.
        request.setAttribute("citasCompletadas", citas); // <<-- ¡CORREGIDO!
        request.setAttribute("gananciasTotales", ganancias); // <<-- ¡CORREGIDO!
        request.setAttribute("mes", mes); // Pasamos el mes/año como Integer
        request.setAttribute("anio", anio); 
        request.setAttribute("mensaje", mensaje);

        // 4. Redirigir la solicitud de vuelta al mismo JSP (GestionGanancias.jsp)
        // Usamos forward, lo cual mantiene los atributos del request.
        request.getRequestDispatcher(VISTA_REPORTE).forward(request, response);
    }

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
        return "Servlet para generar el reporte de citas completadas y ganancias";
    }
}