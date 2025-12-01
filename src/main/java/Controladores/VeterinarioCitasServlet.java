package Controlador;

import ModeloDAO.VeterinarioDAO;
import Modelo.Cita;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet que maneja las operaciones CRUD (Lectura, Registro de Tratamiento, Reprogramación)
 * relacionadas con las citas asignadas a un veterinario específico.
 * * Mapeo: /VeterinarioCitasServlet
 */
@WebServlet(name = "VeterinarioCitasServlet", urlPatterns = {"/VeterinarioCitasServlet"})
public class VeterinarioCitasServlet extends HttpServlet {

    // Instancia del Data Access Object (DAO) para interactuar con la base de datos.
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

    // =========================================================================
    //                            MÉTODO GET (Lectura de Citas)
    // =========================================================================
    /**
     * Procesa las peticiones GET para mostrar la agenda de citas del veterinario.
     * * @param request  Objeto HttpServletRequest que contiene la petición del cliente.
     * @param response Objeto HttpServletResponse que contiene la respuesta que el servlet envía al cliente.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener la sesión actual sin crear una nueva si no existe.
        HttpSession session = request.getSession(false);
        Integer idVeterinario = null;
        
        // 1. **SEGURIDAD Y AUTENTICACIÓN:** Recuperar y validar el ID del Veterinario de la sesión
        if (session != null && session.getAttribute("idVeterinario") != null) {
            try {
                // Intentar obtener el ID del veterinario autenticado.
                idVeterinario = (Integer) session.getAttribute("idVeterinario");
            } catch (ClassCastException e) {
                // Manejo de error si el atributo no es un Integer (caso raro, pero seguro).
                System.out.println("Error casteando idVeterinario: " + e.getMessage());
            }
        }

        if (idVeterinario == null || idVeterinario <= 0) {
            // No hay ID o ID inválido (no autenticado), redirigir al login o a una página de error de acceso.
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return; // Detener la ejecución del método.
        }

        // 2. Cargar la lista de citas usando el ID autenticado
        try {
            // Llamar al DAO para obtener todas las citas asignadas a este veterinario.
            List<Cita> listaCitas = veterinarioDAO.vistaVeterinarioListarMisCitas(idVeterinario);
            
            // Colocar la lista de citas en el objeto request para que el JSP pueda acceder a ella.
            request.setAttribute("listaCitas", listaCitas);
            
            // Despachar (reenviar) la petición y la respuesta al JSP de la agenda.
            request.getRequestDispatcher("VistasWeb/VistasVeterinario/agendaCitas.jsp").forward(request, response);
        } catch (Exception e) {
            // En caso de error de base de datos o excepción, registrar el error.
            e.printStackTrace();
            // Mostrar un mensaje de error en el dashboard si la carga falla.
            request.setAttribute("errorMessage", "Error al cargar citas: " + e.getMessage());
            // Redirigir al dashboard principal del veterinario.
            request.getRequestDispatcher("VistasWeb/VistasVeterinario/VeterinarioDash.jsp").forward(request, response);
        }
    }

    // =========================================================================
    //                            MÉTODO POST (Acciones de Cita)
    // =========================================================================
    /**
     * Procesa las peticiones POST, decidiendo qué acción realizar (registrarTratamiento o reprogramarCita).
     * * @param request  Objeto HttpServletRequest que contiene la petición del cliente (form submission).
     * @param response Objeto HttpServletResponse.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el parámetro 'accion' que define la operación solicitada por el formulario.
        String accion = request.getParameter("accion");

        if (accion == null) {
            // Si no se especifica ninguna acción, redirigir de vuelta a la lista de citas.
            response.sendRedirect("VeterinarioCitasServlet");
            return;
        }

        // Estructura de control para dirigir la petición al método correspondiente.
        switch (accion) {
            case "registrarTratamiento":
                registrarTratamiento(request, response);
                break;
            case "reprogramarCita":
                reprogramarCita(request, response);
                break;
            default:
                // Si la acción no es reconocida, redirigir a la lista de citas.
                response.sendRedirect("VeterinarioCitasServlet");
                break;
        }
    }

    // -------------------------------------------------------------------------
    // 💡 Método 1: Registrar Tratamiento y Completar Cita
    // -------------------------------------------------------------------------
    /**
     * Maneja el registro de diagnóstico, tratamiento y completa el estado de la cita.
     * También requiere obtener el DNI del cliente asociado para fines de auditoría/registro.
     */
   private void registrarTratamiento(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    try {
        // 1. **Recolección de datos del formulario**
        int idCita = Integer.parseInt(request.getParameter("idCita"));
        String nombreMascota = request.getParameter("nombreMascota");
        String diagnostico = request.getParameter("diagnostico");
        String tratamiento = request.getParameter("tratamiento");
        String notas = request.getParameter("notas");

        // 2. Obtener el DNI del cliente asociado a la cita
        String dniCliente = veterinarioDAO.obtenerDniPorCita(idCita);

        if (dniCliente == null) {
             request.getSession().setAttribute("alertaError", "Error: No se encontró DNI para la cita.");
             response.sendRedirect("VeterinarioCitasServlet");
             return;
        }

        // 3. Concatenar y preparar datos para el DAO (Formato de registro interno)
        // **ESTO ES LO QUE SE GUARDARÁ EN LA COLUMNA `diagnostico`**
        String diagnosticoFinal = "  Nombre de Mascota: " + nombreMascota + " - Diagnóstico: " + diagnostico;

        // **ESTO ES LO QUE SE GUARDARÁ EN LA COLUMNA `tratamiento`**
        String tratamientoFinal = tratamiento; // Ya no incluye " - Tratamiento: "

        // 4. Ejecutar la operación en el DAO
        // Se llama al DAO con la nueva estructura de parámetros.
        boolean exito = veterinarioDAO.registrarTratamientoCompletarCita(
                         idCita, diagnosticoFinal, tratamientoFinal, notas, dniCliente);
        // NOTA: EL DAO AHORA NECESITA EL DNI QUE OBTUVISTE AQUÍ.

        // 5. Gestión de la respuesta y alertas
        if (exito) {
            request.getSession().setAttribute("alerta", "Tratamiento registrado y cita completada correctamente.");
        } else {
            request.getSession().setAttribute("alertaError", "Error al registrar tratamiento o completar cita.");
        }

    } catch (NumberFormatException e) {
         request.getSession().setAttribute("alertaError", "Error de formato de ID. Verifique los datos.");
    } catch (Exception e) {
        e.printStackTrace();
        request.getSession().setAttribute("alertaError", "Excepción al registrar tratamiento: " + e.getMessage());
    }

    response.sendRedirect("VeterinarioCitasServlet");
}

    // -------------------------------------------------------------------------
    // 💡 Método 2: Reprogramar Cita
    // -------------------------------------------------------------------------
    /**
     * Maneja la actualización de la fecha y hora de una cita existente.
     */
    private void reprogramarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Recolección de datos del formulario
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String fechaStr = request.getParameter("nuevaFecha");
            String horaStr = request.getParameter("nuevaHora");

            // 2. Conversión de String a tipos SQL
            // Convierte YYYY-MM-DD a java.sql.Date
            Date nuevaFecha = Date.valueOf(fechaStr);
            
            // Convierte HH:MM o HH:MM:SS a java.sql.Time
            // Se asegura que tenga el formato HH:MM:SS, que Time.valueOf necesita si solo viene HH:MM.
            Time nuevaHora = Time.valueOf(horaStr.length() == 5 ? horaStr + ":00" : horaStr);

            // 3. Ejecutar la operación en el DAO
            boolean exito = veterinarioDAO.reprogramarCita(idCita, nuevaFecha, nuevaHora);

            // 4. Gestión de la respuesta y alertas
            if (exito) {
                request.getSession().setAttribute("alerta", "Cita reprogramada correctamente.");
            } else {
                request.getSession().setAttribute("alertaError", "Error al reprogramar cita.");
            }

        } catch (NumberFormatException e) {
             request.getSession().setAttribute("alertaError", "Error de formato de ID. Verifique los datos.");  // Captura si idCita no es un número válido.
        } catch (IllegalArgumentException e) {
             request.getSession().setAttribute("alertaError", "Error de formato de fecha/hora. Use YYYY-MM-DD y HH:MM.");   // Captura si Date.valueOf o Time.valueOf fallan (formato incorrecto).
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("alertaError", "Excepción al reprogramar: " + e.getMessage());   // Captura cualquier otra excepción (ej. error de base de datos)
        }

        response.sendRedirect("VeterinarioCitasServlet");
    }
}