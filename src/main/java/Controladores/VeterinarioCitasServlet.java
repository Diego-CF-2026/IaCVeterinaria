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

@WebServlet(name = "VeterinarioCitasServlet", urlPatterns = {"/VeterinarioCitasServlet"})
public class VeterinarioCitasServlet extends HttpServlet {

    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer idVeterinario = null;
        
        // 1. **SEGURIDAD:** Recuperar y validar el ID del Veterinario de la sesión
        if (session != null && session.getAttribute("idVeterinario") != null) {
            try {
                idVeterinario = (Integer) session.getAttribute("idVeterinario");
            } catch (ClassCastException e) {
                System.out.println("Error casteando idVeterinario: " + e.getMessage());
            }
        }

        if (idVeterinario == null || idVeterinario <= 0) {
            // No hay ID o ID inválido, redirigir al login
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=accesoDenegado");
            return;
        }

        // 2. Cargar la lista de citas y enviarla al JSP
        try {
            List<Cita> listaCitas = veterinarioDAO.vistaVeterinarioListarMisCitas(idVeterinario);
            request.setAttribute("listaCitas", listaCitas);
            request.getRequestDispatcher("VistasWeb/VistasVeterinario/agendaCitas.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error al cargar citas: " + e.getMessage());
            request.getRequestDispatcher("VistasWeb/VistasVeterinario/VeterinarioDash.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if (accion == null) {
            response.sendRedirect("VeterinarioCitasServlet");
            return;
        }

        switch (accion) {
            case "registrarTratamiento":
                registrarTratamiento(request, response);
                break;
            case "reprogramarCita":
                reprogramarCita(request, response);
                break;
            default:
                response.sendRedirect("VeterinarioCitasServlet");
                break;
        }
    }

    // -------------------------------------------------------------------------
    // 💡 Método 1: Registrar Tratamiento y Completar Cita
    // -------------------------------------------------------------------------
    private void registrarTratamiento(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String nombreMascota = request.getParameter("nombreMascota");
            String diagnostico = request.getParameter("diagnostico");
            String tratamiento = request.getParameter("tratamiento");
            String notas = request.getParameter("notas");

            // 1. Obtener el DNI del cliente desde la cita
            String dniCliente = veterinarioDAO.obtenerDniPorCita(idCita);
            
            if (dniCliente == null) {
                 request.getSession().setAttribute("alertaError", "Error: No se encontró DNI para la cita.");
                 response.sendRedirect("VeterinarioCitasServlet");
                 return;
            }

            // 2. Concatenar y preparar datos para el DAO
            String diagnosticoFinal = "Mascota: " + nombreMascota + " - Diagnóstico: " + diagnostico;
            String tratamientoFinal = "Mascota: " + nombreMascota + " - Tratamiento: " + tratamiento;

            // 3. Ejecutar la operación en el DAO
            // El DAO espera: idCita, diagnostico_final, tratamiento_final, notas, dniCliente
            boolean exito = veterinarioDAO.registrarTratamientoCompletarCita(
                        idCita, diagnosticoFinal, tratamientoFinal, notas, dniCliente);

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
    private void reprogramarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String fechaStr = request.getParameter("nuevaFecha");
            String horaStr = request.getParameter("nuevaHora");

            // Conversión segura de String a java.sql.Date y java.sql.Time
            Date nuevaFecha = Date.valueOf(fechaStr);
            // El operador ternario asegura que la hora tenga formato HH:MM:SS, que Time.valueOf necesita.
            Time nuevaHora = Time.valueOf(horaStr.length() == 5 ? horaStr + ":00" : horaStr);

            // Ejecutar la operación en el DAO
            boolean exito = veterinarioDAO.reprogramarCita(idCita, nuevaFecha, nuevaHora);

            if (exito) {
                request.getSession().setAttribute("alerta", "Cita reprogramada correctamente.");
            } else {
                request.getSession().setAttribute("alertaError", "Error al reprogramar cita.");
            }

        } catch (NumberFormatException e) {
             request.getSession().setAttribute("alertaError", "Error de formato de ID o fecha/hora. Verifique los datos.");
        } catch (IllegalArgumentException e) {
             request.getSession().setAttribute("alertaError", "Error de formato de fecha/hora. Use YYYY-MM-DD y HH:MM.");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("alertaError", "Excepción al reprogramar: " + e.getMessage());
        }

        response.sendRedirect("VeterinarioCitasServlet");
    }
}