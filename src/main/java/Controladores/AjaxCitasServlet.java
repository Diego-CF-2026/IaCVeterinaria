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

@WebServlet("/AjaxCitasServlet")
public class AjaxCitasServlet extends HttpServlet {

    private final VeterinarioDAO vetDAO = new VeterinarioDAO();
    // private final Gson gson = new Gson(); // Descomentar si usas la librería Gson

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("listarVeterinariosPorEspecialidad".equalsIgnoreCase(accion)) {
            
            // 1. Configurar la respuesta como JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            PrintWriter out = response.getWriter();
            String jsonOutput = "[]"; // Valor por defecto: lista vacía

            try {
                // 2. Obtener y validar el ID
                String idEspecialidadStr = request.getParameter("idEspecialidad");
                
                if (idEspecialidadStr == null || idEspecialidadStr.isEmpty()) {
                     throw new NumberFormatException("El ID de especialidad está vacío.");
                }
                
                int idEspecialidad = Integer.parseInt(idEspecialidadStr);

                // 3. Obtener los datos del DAO
                // Usando la función vistaCliente que agregaste al DAO
                List<Veterinario> listaVets = vetDAO.vistaClienteListarPorEspecialidad(idEspecialidad);

                // 4. Convertir la lista de objetos Java (Veterinario) a JSON
                
                // Opción A: Usando Gson
                // jsonOutput = gson.toJson(listaVets);
                
                // Opción B: Construcción manual de JSON (como no hay librería GSON/Jackson)
                jsonOutput = construirJsonVeterinarios(listaVets);


            } catch (NumberFormatException e) {
                // El ID no es un número válido
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Código 400
                jsonOutput = "{\"error\": \"ID de Especialidad no válido o ausente: " + e.getMessage() + "\"}";
            } catch (Exception e) {
                // Error de servidor/DB
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Código 500
                jsonOutput = "{\"error\": \"Error interno del servidor: " + e.getMessage().replace("\"", "'") + "\"}";
            } finally {
                // 5. Enviar la respuesta JSON
                out.print(jsonOutput);
                out.flush();
            }
        } else {
            // Acción no reconocida
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Código 404
        }
    }

    /**
     * Construye manualmente el JSON para la lista de veterinarios.
     * @param listaVets La lista de veterinarios.
     * @return String en formato JSON.
     */
    private String construirJsonVeterinarios(List<Veterinario> listaVets) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < listaVets.size(); i++) {
            Veterinario vet = listaVets.get(i);
            // Asegurar que el nombre y apellido no causen problemas con comillas
            String nombre = vet.getNombreVeterinario().replace("\"", "\\\"");
            String apellido = vet.getApellidoVeterinario().replace("\"", "\\\"");
            
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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}