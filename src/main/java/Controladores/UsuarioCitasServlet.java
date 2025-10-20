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
    // Asumo que tienes un VeterinarioDAO y ClienteDAO funcionales
    private final VeterinarioDAO vetDAO = new VeterinarioDAO(); 
    private final ClienteDAO clienteDAO = new ClienteDAO();
    
    // Definición de SimpleDateFormat como constantes de clase
    private static final SimpleDateFormat SDF_FECHA = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SDF_HORA = new SimpleDateFormat("HH:mm");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener usuario logueado desde la sesión
        HttpSession session = request.getSession();
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion == null) {
            // Manejo de seguridad si la sesión expira o no existe
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=sesion_expirada");
            return;
        }

        // 2. Buscar datos del cliente asociado al usuario
        Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());
        request.setAttribute("cliente", cliente);
        
        // ⚠️ Nota: Para listar las citas de este cliente,
        // deberías añadir aquí la llamada:
        // List<Cita> misCitas = citaDAO.listarCitasPorCliente(cliente.getIdCliente());
        // request.setAttribute("misCitas", misCitas);
        
        // 3. Obtener lista de veterinarios
        // Se asume que vetDAO.listarVeterinarios() es funcional (lista todos)
        List<Veterinario> listaVeterinarios = vetDAO.listarVeterinarios();
        request.setAttribute("listaVeterinarios", listaVeterinarios);

        // 4. Mensajes
        String mensaje = request.getParameter("mensaje");
        request.setAttribute("mensaje", mensaje);

        // 5. Redirigir al JSP del formulario/listado de citas
        RequestDispatcher dispatcher = request.getRequestDispatcher("/VistasWeb/VistasCliente/Citas.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("registrar".equalsIgnoreCase(accion)) {
            
            HttpSession session = request.getSession();
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

            if (usuarioSesion == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=no_sesion");
                return;
            }
            
            try {
                // Obtener el idCliente de forma segura desde la BD o Sesión
                Cliente cliente = clienteDAO.buscarPorIdUsuario(usuarioSesion.getIdUsuario());

                if (cliente == null || cliente.getIdCliente() == 0) {
                    LOGGER.log(Level.WARNING, "❌ No se encontró el cliente asociado al usuario ID: " + usuarioSesion.getIdUsuario());
                    response.sendRedirect("UsuarioCitasServlet?mensaje=error_cliente_no_encontrado");
                    return;
                }
                
                int idClienteSeguro = cliente.getIdCliente();

                // Obtención del resto de los parámetros
                int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
                String fechaStr = request.getParameter("fecha");
                String horaStr = request.getParameter("hora");
                String motivo = request.getParameter("motivo");

                // Parseo de fechas y horas
                java.util.Date fechaUtil = SDF_FECHA.parse(fechaStr);
                java.util.Date horaUtil = SDF_HORA.parse(horaStr);

                Date fecha = new Date(fechaUtil.getTime());
                Time hora = new Time(horaUtil.getTime());

                Cita cita = new Cita();
                cita.setIdCliente(idClienteSeguro); // Usamos el ID seguro
                cita.setIdVeterinario(idVeterinario);
                cita.setFecha(fecha);
                cita.setHora(hora);
                cita.setMotivo(motivo);
                
                // 🟢 CORRECCIÓN CLAVE: setEstado se reemplaza por setEstadoNombre
                cita.setEstadoNombre("Pendiente");

                // DEBUG LOG: Verificar que los datos se cargaron en el objeto Cita
                LOGGER.log(Level.INFO, "DEBUG: Cita lista para insertar. Cliente: {0}, Veterinario: {1}", 
                        new Object[]{idClienteSeguro, idVeterinario});

                boolean exito = citaDAO.agregarCita(cita);

                if (exito) {
                    response.sendRedirect("UsuarioCitasServlet?mensaje=registrado");
                } else {
                    response.sendRedirect("UsuarioCitasServlet?mensaje=error_registro");
                }

            } catch (NumberFormatException | ParseException e) {
                LOGGER.log(Level.SEVERE, "❌ Error al registrar cita (Parseo o Formato numérico).", e);
                response.sendRedirect("UsuarioCitasServlet?mensaje=error_formato");
            }
        } else {
            // Si la acción no es "registrar", redirigir al listado por defecto
            response.sendRedirect("UsuarioCitasServlet");
        }
    }
}