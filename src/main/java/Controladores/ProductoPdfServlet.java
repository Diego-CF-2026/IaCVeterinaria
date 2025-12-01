package Controladores;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import Modelo.Conexion;
import Modelo.Producto;
import ModeloDAO.ProductoDAO;
import Modelo.Proveedor;
import ModeloDAO.ProveedorDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

// Cambios de importación de javax.servlet a jakarta.servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para generar un PDF con el listado de productos de la veterinaria.
 * Incluye información de ID, nombre, descripción, precio, stock, proveedor y estado.
 */
@WebServlet("/ProductoPdfServlet")
public class ProductoPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Método GET que genera el PDF y lo envía como respuesta.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. Indicar que la respuesta es de tipo PDF
        response.setContentType("application/pdf");
        // 2. Definir encabezado para forzar descarga con nombre de archivo
        response.setHeader("Content-Disposition", "attachment; filename=\"listado_productos.pdf\"");

        try (Connection conexion = Conexion.getConnection()) { // 3. Obtener conexión a la base de datos

            // 4. Crear DAOs para productos y proveedores
            ProductoDAO productoDAO = new ProductoDAO(conexion);
            ProveedorDAO proveedorDAO = new ProveedorDAO(conexion);

            // 5. Obtener lista de todos los productos
            List<Producto> productos = productoDAO.listarTodos();

            // 6. Crear el documento PDF
            Document document = new Document();

            // 7. Obtener el flujo de salida de la respuesta HTTP
            OutputStream out = response.getOutputStream();

            // 8. Asociar PdfWriter con el documento y el OutputStream
            PdfWriter.getInstance(document, out);

            // 9. Abrir el documento para agregar contenido
            document.open();

            // 10. Definir fuente para el título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

            // 11. Crear párrafo de título
            Paragraph titulo = new Paragraph("Listado de Productos - Veterinaria Santa Cruz", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER); // 12. Centrar el título
            titulo.setSpacingAfter(20); // 13. Espacio después del título
            document.add(titulo); // 14. Agregar título al documento

            // 15. Crear tabla con 7 columnas: ID, Nombre, Descripción, Precio, Stock, Proveedor, Estado
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100); // 16. Ocupa todo el ancho del documento
            table.setSpacingBefore(10f);    // 17. Espacio antes de la tabla
            table.setSpacingAfter(10f);     // 18. Espacio después de la tabla

            float[] columnWidths = {0.8f, 2f, 3f, 1.2f, 1f, 2f, 1f};  // 19. Definir ancho relativo de cada columna
            table.setWidths(columnWidths);

            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);              // 20. Fuente para encabezados de tabla

            // 21. Crear encabezados de tabla
            String[] headers = {"ID", "Nombre", "Descripción", "Precio", "Stock", "Proveedor", "Estado"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER); // 22. Centrar horizontalmente
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   // 23. Centrar verticalmente
                table.addCell(cell); // 24. Agregar celda a la tabla
            }

            // 25. Agregar datos de los productos
            if (productos != null && !productos.isEmpty()) {
                for (Producto prod : productos) {
                    table.addCell(String.valueOf(prod.getIdProducto()));       // 26. ID
                    table.addCell(prod.getNombreProducto());                    // 27. Nombre
                    table.addCell(prod.getDescripcion());                       // 28. Descripción
                    table.addCell("S/ " + prod.getPrecio());                    // 29. Precio
                    table.addCell(String.valueOf(prod.getStock()));             // 30. Stock

                    Proveedor proveedor = proveedorDAO.obtenerPorId(prod.getIdProveedor());   // 31. Obtener nombre del proveedor
                    table.addCell(proveedor != null ? proveedor.getRazonSocial() : "N/A"); // 32. Proveedor

                    // 33. Estado activo/inactivo
                    table.addCell(prod.getEstado() == 1 ? "Activo" : "Inactivo"); // 34. Estado
                }
            } else {
                // 35. Mensaje si no hay productos
                PdfPCell noDataCell = new PdfPCell(new Phrase("No hay productos registrados."));
                noDataCell.setColspan(7); // 36. Ocupa todas las columnas
                noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER); // 37. Centrar texto
                table.addCell(noDataCell); // 38. Agregar celda a la tabla
            }

            // 39. Agregar tabla al documento
            document.add(table);

            // 40. Cerrar el documento
            document.close();

            // 41. Cerrar OutputStream
            out.close();

        } catch (DocumentException e) {
            e.printStackTrace(); // 42. Manejo de errores de iText
            throw new ServletException("Error al generar el PDF: " + e.getMessage());
        } catch (java.sql.SQLException e) {
            e.printStackTrace(); // 43. Manejo de errores de base de datos
            throw new ServletException("Error de base de datos al obtener productos: " + e.getMessage());
        }
    }
}
