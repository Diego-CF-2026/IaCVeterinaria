package Controladores;

import ModeloDAO.UsuarioDAO;
import Modelo.Cliente;
import Modelo.Usuario;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// ====== SOLO clases estándar, nada de JSON ======
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Servlet encargado de registrar nuevos usuarios y clientes.
 * 
 * Este servlet captura los datos del formulario de registro,
 * verifica reCAPTCHA, y llama al DAO para insertar en la base de datos.
 */
@WebServlet("/RegistrarServlet")
public class RegistrarServlet extends HttpServlet {

    // Clave secreta de reCAPTCHA para validar desde el servidor
    private static final String RECAPTCHA_SECRET = "6LdCzuorAAAAAH0HlJk25R0fZlVHfPRCmT_aoHts";
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    /**
     * Método POST que se ejecuta al enviar el formulario de registro.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- 1) Verificación de reCAPTCHA ---
        String recaptchaResponse = request.getParameter("g-recaptcha-response"); // Token enviado por el formulario
        boolean captchaOk = verifyRecaptchaNoJson(recaptchaResponse, request.getRemoteAddr());

        if (!captchaOk) {
            // Si el captcha falla, repoblamos los campos para que el usuario no pierda la información
            request.setAttribute("valNombres",   request.getParameter("nombres"));
            request.setAttribute("valApellidos", request.getParameter("apellidos"));
            request.setAttribute("valDni",       request.getParameter("dni"));
            request.setAttribute("valTelefono",  request.getParameter("telefono"));
            request.setAttribute("valCorreo",    request.getParameter("correo"));

            // Indicamos que hubo un error de captcha
            request.setAttribute("errorRegistro", "captcha");

            // Redirigimos de nuevo al index.jsp con el mensaje de error
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return; // Salimos del método
        }

        // --- 2) Captura de datos del formulario ---
        String nombres    = request.getParameter("nombres");
        String apellidos  = request.getParameter("apellidos");
        String dni        = request.getParameter("dni");
        String telefono   = request.getParameter("telefono");
        String correo     = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // --- 3) Mantener valores en caso de que haya error al insertar ---
        request.setAttribute("valNombres", nombres);
        request.setAttribute("valApellidos", apellidos);
        request.setAttribute("valDni", dni);
        request.setAttribute("valTelefono", telefono);
        request.setAttribute("valCorreo", correo);

        // --- 4) Crear objetos de modelo ---
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContra(contrasena);
        usuario.setIntentos(0); // Número de intentos inicial
        usuario.setEstado(true); // Usuario activo

        Cliente cliente = new Cliente();
        cliente.setNombre(nombres);
        cliente.setApellido(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);

        // --- 5) Llamada al DAO para insertar en la base de datos ---
        UsuarioDAO dao = new UsuarioDAO();
        String resultado = dao.insertarCliente(usuario, cliente); // Retorna "ok" si todo sale bien

        // --- 6) Preparar la respuesta según el resultado ---
        if ("ok".equals(resultado)) {
            request.setAttribute("exitoRegistro", "ok"); // Éxito
        } else {
            request.setAttribute("errorRegistro", resultado); // Error específico (p. ej., correo duplicado)
        }

        // Redirigimos de nuevo a index.jsp con los mensajes correspondientes
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    // ===== Verificación de reCAPTCHA SIN JSON =====
    /**
     * Verifica el reCAPTCHA enviando directamente la petición HTTP POST
     * a Google y analizando la respuesta como texto.
     * 
     * @param recaptchaResponse Token enviado por el cliente
     * @param userIp IP del usuario
     * @return true si el reCAPTCHA es válido, false si falla o hay error
     */
    private boolean verifyRecaptchaNoJson(String recaptchaResponse, String userIp) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) return false;

        HttpURLConnection conn = null;
        try {
            // Configuramos la conexión POST a la URL de verificación de Google
            URL url = new URL(RECAPTCHA_VERIFY_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Preparamos los datos a enviar (URL encoded)
            String postData = "secret="   + URLEncoder.encode(RECAPTCHA_SECRET, StandardCharsets.UTF_8)
                            + "&response=" + URLEncoder.encode(recaptchaResponse, StandardCharsets.UTF_8)
                            + "&remoteip=" + URLEncoder.encode(userIp != null ? userIp : "", StandardCharsets.UTF_8);

            byte[] out = postData.getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setFixedLengthStreamingMode(out.length);
            conn.connect();

            // Enviamos los datos
            try (OutputStream os = conn.getOutputStream()) { 
                os.write(out); 
            }

            // Leemos la respuesta de Google
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            String body = sb.toString();
            // La respuesta JSON contiene algo como: {"success": true, ...}
            // Buscamos literalmente success:true usando regex
            Pattern p = Pattern.compile("\"success\"\\s*:\\s*true");
            Matcher m = p.matcher(body);
            return m.find(); // Retorna true si se encontró success:true

        } catch (Exception e) {
            e.printStackTrace();
            return false; // ante error, consideramos inválido
        } finally {
            if (conn != null) conn.disconnect(); // Cerramos la conexión
        }
    }
}
