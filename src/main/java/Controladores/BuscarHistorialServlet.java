package Controladores;

import Modelo.Tratamiento;
import ModeloDAO.TratamientoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Servlet que permite al veterinario buscar el historial médico de un cliente
 * según su DNI. Valida la sesión y el rol antes de acceder a la información.
 */
@WebServlet("/BuscarHistorialServlet")
public class BuscarHistorialServlet extends HttpServlet {

    private final TratamientoDAO tratamientoDAO = new TratamientoDAO();

    /**
     * Maneja las peticiones GET (cuando se accede por primera vez al historial).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        // Validación de sesión y rol (4 = Veterinario)
        if (sesion == null || sesion.getAttribute("idRol") == null
                || !(sesion.getAttribute("idRol") instanceof Integer)
                || (Integer) sesion.getAttribute("idRol") != 4) {

            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return;
        }

        // Redirige al JSP vacío para mostrar el formulario
        request.getRequestDispatcher("/VistasWeb/VistasVeterinario/HistorialCliente.jsp").forward(request, response);
    }

    /**
     * Maneja las peticiones POST (cuando el veterinario busca por DNI).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);

        // Validar sesión y rol
        if (sesion == null || sesion.getAttribute("idRol") == null
                || !(sesion.getAttribute("idRol") instanceof Integer)
                || (Integer) sesion.getAttribute("idRol") != 4) {

            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return;
        }

        // Obtener el parámetro del formulario
        String dniBusqueda = request.getParameter("dniBusqueda");
        String mensajeBusqueda;

        if (dniBusqueda == null || dniBusqueda.trim().isEmpty()) {
            mensajeBusqueda = "⚠️ Por favor, ingrese un número de DNI para la búsqueda.";
            request.setAttribute("mensajeBusqueda", mensajeBusqueda);
        } else {
            dniBusqueda = dniBusqueda.trim();

            try {
                // Consultar tratamientos
                List<Tratamiento> listaTratamientos = tratamientoDAO.listarTratamientosPorDni(dniBusqueda);

                request.setAttribute("dniBusqueda", dniBusqueda);
                request.setAttribute("listaTratamientos", listaTratamientos);

                if (listaTratamientos.isEmpty()) {
                    mensajeBusqueda = "❌ No se encontraron tratamientos registrados para el DNI: " + dniBusqueda;
                } else {
                    mensajeBusqueda = "✅ Resultados encontrados para el DNI: " + dniBusqueda;
                }

                request.setAttribute("mensajeBusqueda", mensajeBusqueda);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("mensajeBusqueda", "❌ Error al buscar el historial: " + e.getMessage());
            }
        }

        // Redirigir al JSP con los resultados
        request.getRequestDispatcher("/VistasWeb/VistasVeterinario/HistorialCliente.jsp").forward(request, response);
    }
}
