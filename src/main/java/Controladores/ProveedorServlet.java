package Controladores;

import Modelo.Proveedor;
import ModeloDAO.ProveedorDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet encargado de gestionar todas las operaciones CRUD de proveedores
 * en el sistema de la veterinaria. Permite listar, agregar, actualizar,
 * eliminar, cambiar estado y reactivar proveedores.
 */
@WebServlet("/ProveedorServlet")
public class ProveedorServlet extends HttpServlet {

    /**
     * Método principal que procesa todas las solicitudes GET y POST.
     * Determina la acción a ejecutar según el parámetro "accion".
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtenemos el parámetro "accion" de la solicitud
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // Acción por defecto si no se envía ninguna
        }

        // 2. Abrimos la conexión a la base de datos usando el pool/DAO
        try (Connection con = Conexion.getConnection()) {
            ProveedorDAO proveedorDAO = new ProveedorDAO(con); // DAO con la conexión

            // 3. Ejecutamos la acción correspondiente según el parámetro
            switch (accion) {
                case "listar": // Listar proveedores activos
                    listar(request, response, proveedorDAO);
                    break;
                case "listarInactivos": // Listar proveedores inactivos
                    listarInactivos(request, response, proveedorDAO);
                    break;
                case "ver": // Ver detalles de un proveedor específico
                    ver(request, response, proveedorDAO);
                    break;
                case "guardar": // Guardar o actualizar un proveedor
                    guardar(request, response, proveedorDAO);
                    break;
                case "eliminar": // Eliminar un proveedor (lo deja inactivo)
                    eliminar(request, response, proveedorDAO);
                    break;
                case "cambiarEstado": // Cambiar estado activo/inactivo
                    cambiarEstado(request, response, proveedorDAO);
                    break;
                case "reactivar": // Reactivar un proveedor inactivo
                    reactivarProveedor(request, response, proveedorDAO);
                    break;
                default: // Acción no reconocida
                    response.sendRedirect("error.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Imprime el error en consola
            response.sendRedirect("error.jsp"); // Redirige a página de error
        }
    }

    /**
     * Lista todos los proveedores activos y permite filtrar por nombre o dirección.
     */
    private void listar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        String filtro = request.getParameter("busqueda"); // Filtro de búsqueda
        List<Proveedor> proveedores;

        if (filtro != null && !filtro.trim().isEmpty()) {
            // 4. Buscar proveedores activos por nombre o dirección
            proveedores = dao.buscarPorNombreODireccion(filtro.trim());
            request.setAttribute("valorBusqueda", filtro); // Mantener el valor en la vista
        } else {
            // 5. Listar todos los proveedores activos si no hay filtro
            proveedores = dao.listar();
        }

        // 6. Enviar la lista de proveedores al JSP
        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("vista", "activos"); // Atributo que indica vista activa
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProveedores.jsp")
               .forward(request, response); // 7. Redirigir a la vista JSP
    }

    /**
     * Lista todos los proveedores inactivos y permite filtrar.
     */
    private void listarInactivos(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        String filtro = request.getParameter("busqueda");
        List<Proveedor> proveedores;

        if (filtro != null && !filtro.trim().isEmpty()) {
            // 8. Buscar proveedores inactivos por nombre o dirección
            proveedores = dao.buscarPorNombreODireccionInactivos(filtro.trim());
            request.setAttribute("valorBusqueda", filtro);
        } else {
            // 9. Listar todos los proveedores inactivos si no hay filtro
            proveedores = dao.listarInactivos();
        }

        // 10. Enviar la lista de proveedores al JSP
        request.setAttribute("listaProveedores", proveedores);
        request.setAttribute("vista", "inactivos"); // Atributo que indica vista inactiva
        request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProveedores.jsp")
               .forward(request, response);
    }

    /**
     * Obtiene los detalles de un proveedor específico y redirige según su estado.
     */
    private void ver(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {
        try {
            // 11. Obtener ID del proveedor
            int id = Integer.parseInt(request.getParameter("id"));
            Proveedor proveedor = dao.obtenerPorId(id); // Obtener datos del proveedor
            request.setAttribute("proveedorSeleccionado", proveedor); // Enviar al JSP

            // 12. Redirigir según el estado del proveedor
            if (proveedor.getEstado() == 1) {
                listar(request, response, dao); // Activo
            } else {
                listarInactivos(request, response, dao); // Inactivo
            }

        } catch (NumberFormatException e) {
            // 13. Si el ID es inválido, redirige al listado general
            response.sendRedirect("ProveedorServlet?accion=listar");
        }
    }

    /**
     * Guarda un nuevo proveedor o actualiza uno existente.
     */
    private void guardar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws ServletException, IOException {

        // 14. Determinar si es nuevo o actualización
        int id = 0;
        try {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) {
                id = Integer.parseInt(idStr);
            }
        } catch (NumberFormatException ignored) {
        }

        // 15. Crear objeto proveedor con datos del formulario
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(request.getParameter("razonSocial"));
        proveedor.setRuc(request.getParameter("ruc"));
        proveedor.setDireccion(request.getParameter("direccion"));
        proveedor.setCorreo(request.getParameter("correo"));
        proveedor.setEstado(1); // Siempre activo al guardar

        if (id == 0) {
            // 16. Agregar proveedor nuevo
            dao.agregar(proveedor);
            Proveedor nuevo = dao.obtenerPorRuc(proveedor.getRuc());
            request.setAttribute("proveedorSeleccionado", nuevo);
        } else {
            // 17. Actualizar proveedor existente
            proveedor.setIdProveedor(id);
            dao.actualizar(proveedor);
            request.setAttribute("proveedorSeleccionado", dao.obtenerPorId(id));
        }

        // 18. Redirigir al listado actualizado
        listar(request, response, dao);
    }

    /**
     * Marca un proveedor como eliminado (inactivo).
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            // 19. Obtener ID y eliminar proveedor
            int id = Integer.parseInt(request.getParameter("id"));
            dao.eliminar(id);
        } catch (NumberFormatException ignored) {
        }
        // 20. Redirigir al listado de proveedores activos
        response.sendRedirect("ProveedorServlet?accion=listar");
    }

    /**
     * Cambia el estado de un proveedor: activo a inactivo o viceversa.
     */
    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int estado = Integer.parseInt(request.getParameter("estado"));

            if (estado == 1) {
                dao.eliminar(id); // Cambia a inactivo
                response.sendRedirect("ProveedorServlet?accion=listar");
            } else {
                dao.reactivar(id); // Cambia a activo
                response.sendRedirect("ProveedorServlet?accion=listarInactivos");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("error.jsp"); // Si hay error de formato
        }
    }

    /**
     * Reactiva un proveedor que estaba inactivo.
     */
    private void reactivarProveedor(HttpServletRequest request, HttpServletResponse response, ProveedorDAO dao)
            throws IOException {
        try {
            // 21. Obtener ID del proveedor y reactivarlo
            int id = Integer.parseInt(request.getParameter("id"));
            dao.reactivar(id);
        } catch (NumberFormatException ignored) {
        }
        // 22. Redirigir al listado de proveedores inactivos
        response.sendRedirect("ProveedorServlet?accion=listarInactivos");
    }

    // 23. Redirige todas las solicitudes GET al processRequest
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    // 24. Redirige todas las solicitudes POST al processRequest
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
