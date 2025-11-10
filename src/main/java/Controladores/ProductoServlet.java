package Controladores;

import Modelo.Producto;
import ModeloDAO.ProductoDAO;
import Modelo.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet encargado de gestionar todas las operaciones relacionadas con productos:
 * listar, ver, agregar, actualizar, eliminar (temporal y definitivo) y cambiar estado.
 * También maneja la carga de imágenes para los productos y la vista para clientes.
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB por archivo
        maxRequestSize = 1024 * 1024 * 15    // 15MB por petición
)
public class ProductoServlet extends HttpServlet {

    private ProductoDAO productoDAO; // DAO para interactuar con la base de datos

    /**
     * Inicialización del servlet, se obtiene la conexión y se crea el DAO
     */
    @Override
    public void init() throws ServletException {
        Connection con = Conexion.getConnection(); // Obtener conexión a BD
        productoDAO = new ProductoDAO(con); // Crear DAO con la conexión
    }

    /**
     * Procesa solicitudes GET: listar, ver, eliminar, listar inactivos y listar para clientes
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtener acción de la solicitud
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar"; // Acción por defecto
        }

        // 2. Determinar qué acción ejecutar
        switch (accion) {
            case "listar": // Listar todos los productos
                listarProductos(request, response);
                break;
            case "listarCliente": // Listar productos para la vista del cliente
                listarProductosCliente(request, response);
                break;
            case "listarInactivos": // Listar productos inactivos
                listarProductosInactivos(request, response);
                break;
            case "ver": // Ver detalles de un producto
                mostrarDetallesProducto(request, response);
                break;
            case "eliminar": // Eliminar un producto temporalmente
                eliminarProducto(request, response);
                break;
            default:
                response.sendRedirect("error.jsp"); // Acción desconocida
        }
    }

    /**
     * Procesa solicitudes POST: guardar, cambiar estado o eliminar definitivamente
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        switch (accion) {
            case "guardar": // Guardar o actualizar producto
                guardarProducto(request, response);
                break;
            case "cambiarEstado": // Cambiar estado activo/inactivo
                cambiarEstadoProducto(request, response);
                break;
            case "eliminarDefinitivo": // Eliminar producto permanentemente
                eliminarProductoDefinitivo(request, response);
                break;
            default:
                response.sendRedirect("error.jsp"); // Acción desconocida
        }
    }

    /**
     * Lista productos para la administración, activos o inactivos según el parámetro
     */
    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String busqueda = request.getParameter("busqueda"); // Filtro de búsqueda
            boolean inactivos = "true".equals(request.getParameter("inactivos")); // Lista inactivos?
            List<Producto> productos;

            if (busqueda != null && !busqueda.trim().isEmpty()) {
                // 3. Buscar productos filtrando por nombre
                productos = productoDAO.buscarPorNombre(busqueda.trim(), inactivos);
            } else {
                // 4. Listar todos o solo inactivos según parámetro
                productos = inactivos ? productoDAO.listarInactivos() : productoDAO.listarTodos();
            }

            // 5. Enviar lista de productos y estado de vista al JSP
            request.setAttribute("productos", productos);
            request.setAttribute("mostrarInactivos", inactivos);
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProductos.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // 6. Imprimir error si ocurre
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Lista únicamente productos inactivos
     */
    private void listarProductosInactivos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Producto> productos = productoDAO.listarInactivos(); // 7. Obtener inactivos
            request.setAttribute("productos", productos);
            request.setAttribute("mostrarInactivos", true); // 8. Indica que estamos viendo inactivos
            request.getRequestDispatcher("/VistasWeb/VistasAdmin/GestorProductos.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // 9. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Muestra detalles de un producto específico
     */
    private void mostrarDetallesProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id")); // 10. Obtener ID del producto
            Producto producto = productoDAO.obtenerPorId(id); // 11. Buscar en BD
            if (producto != null) {
                request.setAttribute("productoSeleccionado", producto); // 12. Enviar al JSP
                listarProductos(request, response); // 13. Refrescar listado
            } else {
                response.sendRedirect("ProductoServlet?accion=listar"); // 14. Si no existe, redirige
            }
        } catch (Exception e) {
            e.printStackTrace(); // 15. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Guarda un nuevo producto o actualiza uno existente
     */
    private void guardarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 16. Leer parámetros del formulario
            int idProducto = 0;
            String idStr = request.getParameter("idProducto");
            if (idStr != null && !idStr.isEmpty()) {
                idProducto = Integer.parseInt(idStr); // 17. ID existente para actualización
            }

            int idProveedor = Integer.parseInt(request.getParameter("idProveedor"));
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String unidadMedida = request.getParameter("unidadMedida");
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int estado = Integer.parseInt(request.getParameter("estado"));

            // 18. Manejo de imagen
            Part imagenPart = request.getPart("imagen");
            String nombreArchivo = null;

            if (imagenPart != null && imagenPart.getSize() > 0) {
                String nombreOriginal = imagenPart.getSubmittedFileName();
                nombreArchivo = System.currentTimeMillis() + "_" + nombreOriginal; // 19. Nombre único
                String rutaCarpeta = request.getServletContext().getRealPath("/Recursos/ProductosVenta");
                File carpeta = new File(rutaCarpeta);
                if (!carpeta.exists()) {
                    carpeta.mkdirs(); // 20. Crear carpeta si no existe
                }
                imagenPart.write(rutaCarpeta + File.separator + nombreArchivo); // 21. Guardar archivo
            }

            // 22. Crear objeto producto con datos
            Producto producto = new Producto();
            producto.setNombreProducto(nombre);
            producto.setDescripcion(descripcion);
            producto.setUnidadMedida(unidadMedida);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setEstado(estado);
            producto.setIdProveedor(idProveedor);
            producto.setIdProducto(idProducto);

            if (nombreArchivo != null) {
                producto.setImagen("Recursos/ProductosVenta/" + nombreArchivo);
            } else {
                Producto actual = productoDAO.obtenerPorId(idProducto);
                if (actual != null) {
                    producto.setImagen(actual.getImagen()); // Mantener imagen actual si no se cambia
                }
            }

            // 23. Guardar o actualizar en base de datos
            boolean exito;
            if (idProducto == 0) {
                exito = productoDAO.agregar(producto);
            } else {
                exito = productoDAO.actualizar(producto);
            }

            response.sendRedirect("ProductoServlet?accion=listar"); // 24. Redirigir al listado

        } catch (Exception e) {
            e.printStackTrace(); // 25. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Elimina un producto de forma temporal (lo marca inactivo)
     */
    private void eliminarProducto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id")); // 26. Obtener ID
            Producto producto = productoDAO.obtenerPorId(id);
            if (producto != null) {
                productoDAO.eliminar(id); // 27. Marcar como inactivo
                String redireccion = producto.getEstado() == 1 ? "listar" : "listarInactivos"; // 28. Determinar vista
                response.sendRedirect("ProductoServlet?accion=" + redireccion);
            } else {
                response.sendRedirect("error.jsp"); // 29. Producto no existe
            }
        } catch (Exception e) {
            e.printStackTrace(); // 30. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Elimina un producto de forma definitiva de la base de datos
     */
    private void eliminarProductoDefinitivo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id")); // 31. Obtener ID
            boolean exito = productoDAO.eliminarDefinitivo(id);

            // 32. Determinar contexto (activos o inactivos)
            boolean mostrarInactivos = request.getParameter("mostrarInactivos") != null 
                                    && request.getParameter("mostrarInactivos").equals("true");

            // 33. Redirigir según vista
            if(mostrarInactivos) {
                response.sendRedirect("ProductoServlet?accion=listarInactivos");
            } else {
                response.sendRedirect("ProductoServlet?accion=listar");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 34. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Cambia el estado de un producto: activo <-> inactivo
     */
    private void cambiarEstadoProducto(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id")); // 35. ID del producto
            int estadoActual = Integer.parseInt(request.getParameter("estado"));
            int nuevoEstado = (estadoActual == 1) ? 0 : 1; // 36. Alternar estado

            Producto producto = productoDAO.obtenerPorId(id);
            if (producto != null) {
                producto.setEstado(nuevoEstado);
                productoDAO.actualizar(producto); // 37. Guardar nuevo estado
            }

            // 38. Redirigir a la vista correspondiente
            String redireccion = nuevoEstado == 1 ? "listar" : "listarInactivos";
            response.sendRedirect("ProductoServlet?accion=" + redireccion);
        } catch (Exception e) {
            e.printStackTrace(); // 39. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Lista productos visibles para clientes en la tienda
     */
    private void listarProductosCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Producto> productosCliente = productoDAO.listarTodos(); // 40. Obtener todos
            request.setAttribute("productos", productosCliente);
            request.getRequestDispatcher("VistasWeb/VistasCliente/Productos.jsp").forward(request, response); // 41. Enviar al JSP
        } catch (Exception e) {
            e.printStackTrace(); // 42. Manejo de errores
            response.sendRedirect("error.jsp");
        }
    }
}
