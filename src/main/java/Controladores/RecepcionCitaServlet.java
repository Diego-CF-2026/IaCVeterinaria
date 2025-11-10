package Controladores;
import Modelo.Cita;
import ModeloDAO.CitaDAO;
import Modelo.Cliente;
import ModeloDAO.ClienteDAO; 
import ModeloDAO.VeterinarioDAO; // Necesario para el dropdown de Veterinarios
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/RecepcionCitaServlet")
public class RecepcionCitaServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RecepcionCitaServlet.class.getName());
    ClienteDAO clienteDAO = new ClienteDAO();
    VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "buscarCliente"; // Acción por defecto: buscar un cliente para la cita
        }

        switch (accion) {
            case "buscarCliente":
            case "verCitas": // <--- O puedes agregar una acción explícita si el menú la usa
                mostrarBuscador(request, response);
                break;
            case "buscar":
                realizarBusqueda(request, response);
                break;
            case "seleccionarCliente":
                // Muestra la vista con el formulario de cita precargado con el idCliente
                prepararCreacionCita(request, response);
                break;
            case "gestionarCitas":
                 // Redirige al CitaServlet para ver/editar/cancelar citas de un cliente
                 response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=verCitasCliente&idCliente=" + request.getParameter("idCliente"));
                 break;
            default:
                mostrarBuscador(request, response);
                break;
        }
    }
    
    // El doPost NO se usa aquí, ya que la acción de GUARDAR la cita se hace en CitaServlet

    /**
     * Muestra la vista inicial con el formulario de búsqueda.
     */
    private void mostrarBuscador(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Cargar la lista de todas las citas (Usando el método que añadiste al DAO)
        List<Cita> listaCitas = citaDAO.listarCitas();
        
        // 2. Enviar la lista de citas al JSP (Clave para que el JSP no falle)
        request.setAttribute("listaCitas", listaCitas);
        
        // 3. Redirigir a la vista
        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
    }
    
    /**
     * Ejecuta la búsqueda de clientes usando ClienteDAO.buscarClientes().
     */
    private void realizarBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String termino = request.getParameter("terminoBusqueda");
        
        if (termino == null || termino.trim().isEmpty()) {
            request.setAttribute("aviso", "Ingrese un Nombre, Apellido o DNI para buscar.");
        } else {
            List<Cliente> clientes = clienteDAO.buscarClientes(termino.trim()); 
            request.setAttribute("listaClientes", clientes);
            if (clientes.isEmpty()) {
                 request.setAttribute("aviso", "No se encontraron clientes para: '" + termino + "'.");
            }
        }
        
        request.setAttribute("terminoBusqueda", termino);
        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp").forward(request, response);
    }

    /**
     * Prepara la vista del formulario de creación de cita con los datos necesarios.
     */
    private void prepararCreacionCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idClienteStr = request.getParameter("idCliente");
        
        try {
            int idCliente = Integer.parseInt(idClienteStr);
            Cliente cliente = clienteDAO.buscarIdCliente(idCliente);
            
            if (cliente != null) {
                // 1. Cargar el cliente seleccionado para mostrar su nombre
                request.setAttribute("clienteSeleccionado", cliente);
                
                // 2. Cargar la lista de veterinarios disponibles para el dropdown
                // ⚠️ ASUME UN MÉTODO EN VETERINARIODAO (ej: listarVeterinariosParaDropdown())
                request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());
                
                // 3. Redirigir al formulario de creación (que luego hará POST a CitaServlet)
                request.getRequestDispatcher("/VistasWeb/VistasRecep/CrearCitaRecep.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("mensaje", "❌ Cliente no encontrado.");
                response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de cliente inválido para crear cita: " + idClienteStr, e);
            request.getSession().setAttribute("mensaje", "❌ ID de cliente inválido.");
            response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
        } catch (Exception e) {
             LOGGER.log(Level.SEVERE, "Error al preparar la creación de cita.", e);
             request.getSession().setAttribute("mensaje", "❌ Error al cargar datos necesarios.");
             response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
        }
    }
}