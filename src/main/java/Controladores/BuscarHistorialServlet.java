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
 * Servlet que permite al veterinario buscar el historial médico de un cliente por DNI.
 * - Valida sesión y rol (idRol = 4 para Veterinario) antes de permitir el acceso.
 * - GET: muestra la vista vacía con el formulario de búsqueda.
 * - POST: procesa el DNI, consulta tratamientos y retorna resultados/mensajes a la misma vista.
 *
 * Atributos de request usados en el JSP:
 *  - "dniBusqueda"        : String con el DNI ingresado (para repoblar el input).
 *  - "listaTratamientos"  : List<Tratamiento> con los resultados de la búsqueda.
 *  - "mensajeBusqueda"    : Mensaje informativo/alerta/éxito para el usuario.
 */
@WebServlet("/BuscarHistorialServlet")
public class BuscarHistorialServlet extends HttpServlet {

    // DAO para consultas de tratamientos (historial médico)
    private final TratamientoDAO tratamientoDAO = new TratamientoDAO();

    /**
     * Maneja peticiones GET.
     * Flujo:
     *  1) Valida sesión y rol (debe ser Veterinario).
     *  2) Si válido, despacha al JSP para mostrar el formulario en blanco.
     *  3) Si no válido, redirige a index con query 'error=accesoDenegado'.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtiene la sesión sin crear una nueva si no existe
        HttpSession sesion = request.getSession(false);

        // Validación de seguridad: se exige rol 4 (Veterinario)
        if (sesion == null || sesion.getAttribute("idRol") == null
                || !(sesion.getAttribute("idRol") instanceof Integer)
                || (Integer) sesion.getAttribute("idRol") != 4) {

            // No autorizado: redirige a index con mensaje de acceso denegado
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return;
        }

        // Autorizado: muestra el JSP de historial (formulario vacío)
        request.getRequestDispatcher("/VistasWeb/VistasVeterinario/HistorialCliente.jsp")
               .forward(request, response);
    }

    /**
     * Maneja peticiones POST.
     * Flujo:
     *  1) Valida sesión y rol (Veterinario).
     *  2) Lee el parámetro 'dniBusqueda'.
     *  3) Si está vacío, setea mensaje de advertencia y reenvía al JSP.
     *  4) Si tiene valor, consulta en DAO los tratamientos por DNI:
     *     - Carga atributos en request: dniBusqueda, listaTratamientos y mensajeBusqueda.
     *     - Maneja errores de consulta mostrando un mensaje amigable.
     *  5) Reenvía al JSP (misma vista) con los resultados/mensajes.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Reutiliza sesión existente (no crea una nueva)
        HttpSession sesion = request.getSession(false);

        // Validación de seguridad: requiere rol veterinario
        if (sesion == null || sesion.getAttribute("idRol") == null
                || !(sesion.getAttribute("idRol") instanceof Integer)
                || (Integer) sesion.getAttribute("idRol") != 4) {

            // Usuario no autorizado
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return;
        }

        // Lee el DNI desde el formulario
        String dniBusqueda = request.getParameter("dniBusqueda");
        String mensajeBusqueda;

        if (dniBusqueda == null || dniBusqueda.trim().isEmpty()) {
            // Validación de entrada: DNI requerido
            mensajeBusqueda = "⚠️ Por favor, ingrese un número de DNI para la búsqueda.";
            request.setAttribute("mensajeBusqueda", mensajeBusqueda);

        } else {
            // Normaliza entrada quitando espacios extra
            dniBusqueda = dniBusqueda.trim();

            try {
                // Consulta tratamientos asociados al DNI
                List<Tratamiento> listaTratamientos = tratamientoDAO.listarTratamientosPorDni(dniBusqueda);

                // Atributos para el JSP: repoblar input y mostrar resultados
                request.setAttribute("dniBusqueda", dniBusqueda);
                request.setAttribute("listaTratamientos", listaTratamientos);

                // Mensaje contextual según resultados
                if (listaTratamientos.isEmpty()) {
                    mensajeBusqueda = "❌ No se encontraron tratamientos registrados para el DNI: " + dniBusqueda;
                } else {
                    mensajeBusqueda = "✅ Resultados encontrados para el DNI: " + dniBusqueda;
                }
                request.setAttribute("mensajeBusqueda", mensajeBusqueda);

            } catch (Exception e) {
                // Log simple a consola (puedes reemplazar por logger)
                e.printStackTrace();
                // Mensaje de error genérico hacia la vista (sin exponer detalles sensibles)
                request.setAttribute("mensajeBusqueda", "❌ Error al buscar el historial: " + e.getMessage());
            }
        }

        // Reenvía a la misma vista con los atributos/mensajes configurados
        request.getRequestDispatcher("/VistasWeb/VistasVeterinario/HistorialCliente.jsp")
               .forward(request, response);
    }
}
