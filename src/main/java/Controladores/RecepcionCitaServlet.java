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

/**
 * Servlet encargado de gestionar la parte de Recepción para la creación
 * y búsqueda de citas de clientes en la veterinaria.
 * 
 * Este servlet funciona como un "intermediario" entre la vista JSP y los DAOs
 * de Cliente, Veterinario y Cita. Las operaciones de guardar o actualizar
 * citas se realizan en CitaServlet, este solo prepara datos y vistas.
 */
@WebServlet("/RecepcionCitaServlet")
public class RecepcionCitaServlet extends HttpServlet {

    // Logger para registrar información y errores
    private static final Logger LOGGER = Logger.getLogger(RecepcionCitaServlet.class.getName());

    // Instancias de DAOs para acceder a la base de datos
    ClienteDAO clienteDAO = new ClienteDAO();
    VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    CitaDAO citaDAO = new CitaDAO();

    /**
     * Método GET principal que determina qué acción ejecutar
     * según el parámetro "accion" enviado desde la vista.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtenemos el parámetro de acción
        String accion = request.getParameter("accion");
        if (accion == null || accion.isEmpty()) {
            accion = "buscarCliente"; // Acción por defecto si no se envía ninguna
        }

        // Determinamos la acción a ejecutar
        switch (accion) {
            case "buscarCliente":
            case "verCitas": 
                // Mostrar la vista principal con el buscador de clientes
                mostrarBuscador(request, response);
                break;
            case "buscar":
                // Ejecutar la búsqueda de clientes según término ingresado
                realizarBusqueda(request, response);
                break;
            case "seleccionarCliente":
                // Preparar la vista de creación de cita con el cliente seleccionado
                prepararCreacionCita(request, response);
                break;
            case "gestionarCitas":
                // Redirige al CitaServlet para ver, editar o cancelar citas de un cliente
                response.sendRedirect(request.getContextPath() 
                        + "/CitaServlet?accion=verCitasCliente&idCliente=" 
                        + request.getParameter("idCliente"));
                break;
            default:
                // Si la acción no coincide con ninguna, se muestra el buscador
                mostrarBuscador(request, response);
                break;
        }
    }

    // Nota: El doPost no se usa aquí, ya que el guardado de la cita se hace en CitaServlet

    /**
     * Muestra la vista inicial con el formulario de búsqueda y lista de citas.
     * @param request Objeto HttpServletRequest
     * @param response Objeto HttpServletResponse
     */
    private void mostrarBuscador(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Cargar la lista de todas las citas
        List<Cita> listaCitas = citaDAO.listarCitas();

        // 2. Enviar la lista de citas al JSP (clave para que la vista no falle)
        request.setAttribute("listaCitas", listaCitas);

        // 3. Redirigir a la vista principal de gestión de citas
        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp")
               .forward(request, response);
    }

    /**
     * Realiza la búsqueda de clientes según un término ingresado
     * en el formulario de búsqueda.
     * @param request Objeto HttpServletRequest
     * @param response Objeto HttpServletResponse
     */
    private void realizarBusqueda(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtenemos el término de búsqueda desde el formulario
        String termino = request.getParameter("terminoBusqueda");

        if (termino == null || termino.trim().isEmpty()) {
            // Si no se ingresa nada, mostramos un aviso
            request.setAttribute("aviso", "Ingrese un Nombre, Apellido o DNI para buscar.");
        } else {
            // Llamamos al DAO para buscar clientes que coincidan con el término
            List<Cliente> clientes = clienteDAO.buscarClientes(termino.trim());
            request.setAttribute("listaClientes", clientes);

            if (clientes.isEmpty()) {
                // Si no se encontraron resultados, mostramos mensaje
                request.setAttribute("aviso", "No se encontraron clientes para: '" + termino + "'.");
            }
        }

        // Guardamos el término de búsqueda para mantenerlo en el formulario
        request.setAttribute("terminoBusqueda", termino);

        // Redirigimos nuevamente al JSP de gestión de citas con los resultados
        request.getRequestDispatcher("/VistasWeb/VistasRecep/GestionCitas.jsp")
               .forward(request, response);
    }

    /**
     * Prepara la vista de creación de cita mostrando el cliente seleccionado
     * y cargando los veterinarios disponibles para el dropdown.
     * @param request Objeto HttpServletRequest
     * @param response Objeto HttpServletResponse
     */
    private void prepararCreacionCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtenemos el ID del cliente seleccionado
        String idClienteStr = request.getParameter("idCliente");

        try {
            int idCliente = Integer.parseInt(idClienteStr); // Convertimos a entero
            Cliente cliente = clienteDAO.buscarIdCliente(idCliente); // Buscamos el cliente en DB

            if (cliente != null) {
                // 1. Enviamos el cliente seleccionado al JSP para mostrar su información
                request.setAttribute("clienteSeleccionado", cliente);

                // 2. Cargamos la lista de veterinarios disponibles para el dropdown
                // ⚠️ Este método debe existir en VeterinarioDAO
                request.setAttribute("listaVeterinarios", veterinarioDAO.listarVeterinariosParaDropdown());

                // 3. Redirigimos al JSP de creación de cita (el POST se hace en CitaServlet)
                request.getRequestDispatcher("/VistasWeb/VistasRecep/CrearCitaRecep.jsp")
                       .forward(request, response);
            } else {
                // Si no se encuentra el cliente, mostramos mensaje y redirigimos
                request.getSession().setAttribute("mensaje", "❌ Cliente no encontrado.");
                response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
            }
        } catch (NumberFormatException e) {
            // Si el ID del cliente no es un número válido
            LOGGER.log(Level.WARNING, "ID de cliente inválido para crear cita: " + idClienteStr, e);
            request.getSession().setAttribute("mensaje", "❌ ID de cliente inválido.");
            response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
        } catch (Exception e) {
            // Captura de errores inesperados
            LOGGER.log(Level.SEVERE, "Error al preparar la creación de cita.", e);
            request.getSession().setAttribute("mensaje", "❌ Error al cargar datos necesarios.");
            response.sendRedirect(request.getContextPath() + "/RecepcionCitaServlet");
        }
    }
}
