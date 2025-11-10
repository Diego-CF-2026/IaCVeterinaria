package Controladores;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import Modelo.Veterinario;
import ModeloDAO.VeterinarioDAO;

// NOTA: Si usas la librería Gson, debes descomentar la importación
// import com.google.gson.Gson; 

/**
 * Servlet AJAX para operaciones relacionadas a citas.
 * Actualmente expone:
 *  - GET ?accion=listarVeterinariosPorEspecialidad&idEspecialidad=ID
 *      Devuelve un JSON con {idVeterinario, nombreVeterinario, apellidoVeterinario}
 *      de todos los veterinarios asociados a la especialidad indicada.
 *
 * Notas:
 *  - Respuestas en formato JSON (UTF-8).
 *  - Manejo de errores con códigos HTTP 400 (parámetros inválidos) y 500 (error interno).
 *  - Por simplicidad, la conversión a JSON se hace manualmente; opcionalmente puedes usar Gson.
 */
@WebServlet("/AjaxCitasServlet")
public class AjaxCitasServlet extends HttpServlet {

    // DAO para acceder a la capa de datos de Veterinario
    private final VeterinarioDAO vetDAO = new VeterinarioDAO();
    // private final Gson gson = new Gson(); // Descomentar si usas la librería Gson

    /**
     * Enrutador de solicitudes GET para endpoints AJAX.
     * Actualmente soporta "listarVeterinariosPorEspecialidad".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("listarVeterinariosPorEspecialidad".equalsIgnoreCase(accion)) {
            
            // 1) Configurar cabeceras de respuesta JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            PrintWriter out = response.getWriter();
            String jsonOutput = "[]"; // Por defecto, lista vacía

            try {
                // 2) Leer y validar el parámetro idEspecialidad
                String idEspecialidadStr = request.getParameter("idEspecialidad");
                if (idEspecialidadStr == null || idEspecialidadStr.isEmpty()) {
                     throw new NumberFormatException("El ID de especialidad está vacío.");
                }
                int idEspecialidad = Integer.parseInt(idEspecialidadStr);

                // 3) Consultar el DAO (vista para cliente por especialidad)
                List<Veterinario> listaVets = vetDAO.vistaClienteListarPorEspecialidad(idEspecialidad);

                // 4) Serializar la lista a JSON
                // Opción A (con librería):
                // jsonOutput = gson.toJson(listaVets);
                // Opción B (sin librería): construcción manual segura (escapando comillas)
                jsonOutput = construirJsonVeterinarios(listaVets);

            } catch (NumberFormatException e) {
                // Parámetro ausente o no numérico -> 400 Bad Request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonOutput = "{\"error\": \"ID de Especialidad no válido o ausente: " 
                             + e.getMessage() + "\"}";
            } catch (Exception e) {
                // Cualquier otro error (BD, nulls inesperados, etc.) -> 500
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonOutput = "{\"error\": \"Error interno del servidor: " 
                             + e.getMessage().replace("\"", "'") + "\"}";
            } finally {
                // 5) Enviar respuesta
                out.print(jsonOutput);
                out.flush();
            }
        } else {
            // Acción no soportada -> 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Construcción manual de JSON para una lista de veterinarios.
     * Incluye escape sencillo de comillas en nombre y apellido.
     *
     * @param listaVets lista de Veterinario a serializar
     * @return arreglo JSON con campos idVeterinario, nombreVeterinario, apellidoVeterinario
     */
    private String construirJsonVeterinarios(List<Veterinario> listaVets) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < listaVets.size(); i++) {
            Veterinario vet = listaVets.get(i);

            // Evitar errores por null y escapar comillas
            String nombre = vet.getNombreVeterinario() == null ? "" 
                            : vet.getNombreVeterinario().replace("\"", "\\\"");
            String apellido = vet.getApellidoVeterinario() == null ? "" 
                              : vet.getApellidoVeterinario().replace("\"", "\\\"");
            
            sb.append("{");
            sb.append("\"idVeterinario\":").append(vet.getIdVeterinario()).append(",");
            sb.append("\"nombreVeterinario\":\"").append(nombre).append("\",");
            sb.append("\"apellidoVeterinario\":\"").append(apellido).append("\"");
            sb.append("}");
            if (i < listaVets.size() - 1) {
                sb.append(",");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Redirige las solicitudes POST al mismo manejo que GET (idempotente para esta operación).
     * Útil si el cliente envía POST por conveniencia.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
