package Controladores;

import Modelo.Veterinario;
import ModeloDAO.VeterinarioDAO;
import Controladores.AdminVeterinarioServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Importaciones necesarias de iTextPDF para generar documentos PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;

@WebServlet(name = "ReporteEmpleadosServlet", urlPatterns = {"/ReporteEmpleadosServlet"})
public class ReporteEmpleadosServlet extends HttpServlet {

    // Logger para registrar información y errores
    private static final Logger LOGGER = Logger.getLogger(ReporteEmpleadosServlet.class.getName());

    /**
     * Método GET que se ejecuta cuando se accede al servlet desde un navegador.
     * Genera un reporte PDF con el listado de empleados (veterinarios en este caso).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuramos la respuesta HTTP para que sea un PDF descargable
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Reporte_Empleados.pdf\"");

        try (OutputStream os = response.getOutputStream()) { // OutputStream para escribir el PDF en la respuesta
            // Creamos un documento PDF
            Document document = new Document();
            PdfWriter.getInstance(document, os); // Asociamos el documento con el OutputStream
            document.open(); // Abrimos el documento para agregar contenido

            // Definimos diferentes tipos de fuentes para títulos, subtítulos, encabezados y contenido
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE); // Título principal
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY); // Subtítulos
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE); // Encabezado de tabla
            Font fontContenido = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK); // Contenido de tabla

            // --- Título del reporte ---
            Paragraph titulo = new Paragraph("Reporte de Empleados - Veterinaria Santa Cruz", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER); // Centramos el título
            titulo.setSpacingAfter(20); // Espacio después del título
            document.add(titulo);

            // --- Sección de Veterinarios ---
            document.add(new Paragraph(" ")); // Espacio en blanco entre secciones
            Paragraph subTituloVeterinarios = new Paragraph("Listado de Veterinarios", fontSubtitulo);
            subTituloVeterinarios.setAlignment(Element.ALIGN_LEFT);
            subTituloVeterinarios.setSpacingAfter(10);
            document.add(subTituloVeterinarios);

            // Creamos instancia del DAO para obtener la lista de veterinarios desde la base de datos
            VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
            List<Veterinario> listaVeterinarios = veterinarioDAO.listarVeterinarios();

            // Verificamos si la lista tiene datos
            if (listaVeterinarios != null && !listaVeterinarios.isEmpty()) {
                // Creamos la tabla para mostrar los veterinarios
                PdfPTable tablaVeterinarios = new PdfPTable(5); // 5 columnas: ID, Nombre, Apellido, Teléfono, Especialidad
                tablaVeterinarios.setWidthPercentage(100); // La tabla ocupa todo el ancho disponible
                tablaVeterinarios.setSpacingBefore(10f); // Espacio antes de la tabla
                tablaVeterinarios.setSpacingAfter(10f); // Espacio después de la tabla
                tablaVeterinarios.setWidths(new float[]{0.5f, 1.5f, 1.5f, 1f, 1f}); // Anchos relativos de columnas

                // Agregamos los encabezados de la tabla
                addTableHeader(tablaVeterinarios, fontHeader, new String[]{"ID", "Nombre", "Apellido", "Teléfono", "Especialidad"});

                // Agregamos las filas de datos de cada veterinario
                for (Veterinario vet : listaVeterinarios) {
                    addCell(tablaVeterinarios, String.valueOf(vet.getIdVeterinario()), fontContenido); // ID
                    addCell(tablaVeterinarios, vet.getNombreVeterinario(), fontContenido); // Nombre
                    addCell(tablaVeterinarios, vet.getApellidoVeterinario(), fontContenido); // Apellido
                    addCell(tablaVeterinarios, vet.getTelefonoVeterinario(), fontContenido); // Teléfono
                    addCell(tablaVeterinarios, String.valueOf(vet.getIdEspecialidad()), fontContenido); // Especialidad
                }

                // Agregamos la tabla al documento
                document.add(tablaVeterinarios);
            } else {
                // Si no hay veterinarios, mostramos un mensaje
                document.add(new Paragraph("No hay veterinarios registrados.", fontContenido));
            }

            // Cerramos el documento
            document.close();
            LOGGER.log(Level.INFO, "Reporte PDF de empleados generado exitosamente.");

        } catch (DocumentException e) {
            // Capturamos errores relacionados con la creación del documento PDF
            LOGGER.log(Level.SEVERE, "Error al generar el documento PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF.");
        } catch (IOException e) {
            // Capturamos errores de entrada/salida
            LOGGER.log(Level.SEVERE, "Error de E/S al generar el reporte PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de E/S al generar el reporte PDF.");
        } catch (Exception e) {
            // Capturamos cualquier otro error inesperado
            LOGGER.log(Level.SEVERE, "Error inesperado al generar el reporte PDF: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error inesperado al generar el reporte PDF.");
        }
    }

    /**
     * Método auxiliar para agregar encabezados a una tabla PDF
     * @param table La tabla a la que se agregarán los encabezados
     * @param font Fuente que se usará para el texto del encabezado
     * @param headers Arreglo con los nombres de los encabezados
     */
    private void addTableHeader(PdfPTable table, Font font, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new BaseColor(60, 100, 200)); // Color de fondo azul para encabezados
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Alineación horizontal centrada
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical centrada
            cell.setPadding(5); // Padding interno
            table.addCell(cell);
        }
    }

    /**
     * Método auxiliar para agregar celdas de datos a una tabla PDF
     * @param table La tabla a la que se agregarán las celdas
     * @param text Texto que irá dentro de la celda
     * @param font Fuente que se usará para el texto de la celda
     */
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT); // Alineación a la izquierda
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE); // Alineación vertical centrada
        cell.setPadding(5); // Padding interno
        table.addCell(cell);
    }
}
