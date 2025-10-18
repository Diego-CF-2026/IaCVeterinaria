package Controladores;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Modelo.Cita;
import Modelo.Usuario;
import Modelo.Cliente;
import Modelo.Veterinario;
import ModeloDAO.CitaDAO;
import ModeloDAO.VeterinarioDAO;
import ModeloDAO.ClienteDAO;

@WebServlet("/UsuarioCitasServlet")
public class UsuarioCitasServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UsuarioCitasServlet.class.getName());
    private final CitaDAO citaDAO = new CitaDAO();
    private final VeterinarioDAO vetDAO = new VeterinarioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🟢 1. Obtener usuario logueado desde la sesión
        HttpSession session = request.getSession();
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion != null) {
            // 🟢 2. Buscar datos del cliente asociado al usuario
            Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());
            request.setAttribute("cliente", cliente);
        }

        // 🟢 3. Obtener lista de veterinarios
        List<Veterinario> listaVeterinarios = vetDAO.listarVeterinarios();
        request.setAttribute("listaVeterinarios", listaVeterinarios);

        // 🟢 4. Mensajes
        String mensaje = request.getParameter("mensaje");
        request.setAttribute("mensaje", mensaje);

        // 🟢 5. Redirigir al JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("registrar".equalsIgnoreCase(accion)) {
            try {
                int idCliente = Integer.parseInt(request.getParameter("idCliente"));
                int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String motivo = request.getParameter("motivo");

                SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

                java.util.Date fechaUtil = sdfFecha.parse(fechaStr);
                java.util.Date horaUtil = sdfHora.parse(horaStr);

                Date fecha = new Date(fechaUtil.getTime());
                Time hora = new Time(horaUtil.getTime());

                Cita cita = new Cita();
                cita.setIdCliente(idCliente);
                cita.setIdVeterinario(idVeterinario);
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                cita.setEstado("Pendiente");

                boolean exito = citaDAO.agregarCita(cita);

                if (exito) {
                    response.sendRedirect("UsuarioCitasServlet?mensaje=registrado");
                } else {
                    response.sendRedirect("UsuarioCitasServlet?mensaje=error_registro");
                }

            } catch (NumberFormatException | ParseException e) {
                LOGGER.log(Level.SEVERE, "Error al registrar cita", e);
                response.sendRedirect("UsuarioCitasServlet?mensaje=error_registro");
            }
        } else {
            response.sendRedirect("UsuarioCitasServlet");
        }
    }
}