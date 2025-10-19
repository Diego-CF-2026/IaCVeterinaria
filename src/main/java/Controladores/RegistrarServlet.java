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

@WebServlet("/RegistrarServlet")
public class RegistrarServlet extends HttpServlet {

  
    private static final String RECAPTCHA_SECRET = "6LdCzuorAAAAAH0HlJk25R0fZlVHfPRCmT_aoHts";
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        boolean captchaOk = verifyRecaptchaNoJson(recaptchaResponse, request.getRemoteAddr());
        if (!captchaOk) {
            // repoblar para el modal
            request.setAttribute("valNombres",   request.getParameter("nombres"));
            request.setAttribute("valApellidos", request.getParameter("apellidos"));
            request.setAttribute("valDni",       request.getParameter("dni"));
            request.setAttribute("valTelefono",  request.getParameter("telefono"));
            request.setAttribute("valCorreo",    request.getParameter("correo"));

            request.setAttribute("errorRegistro", "captcha");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        // 1) Capturar datos
        String nombres    = request.getParameter("nombres");
        String apellidos  = request.getParameter("apellidos");
        String dni        = request.getParameter("dni");
        String telefono   = request.getParameter("telefono");
        String correo     = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        // 2) Mantener valores en caso de error
        request.setAttribute("valNombres", nombres);
        request.setAttribute("valApellidos", apellidos);
        request.setAttribute("valDni", dni);
        request.setAttribute("valTelefono", telefono);
        request.setAttribute("valCorreo", correo);

        // 3) Modelos
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContra(contrasena);
        usuario.setIntentos(0);
        usuario.setEstado(true);

        Cliente cliente = new Cliente();
        cliente.setNombre(nombres);
        cliente.setApellido(apellidos);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);

        // 4) DAO
        UsuarioDAO dao = new UsuarioDAO();
        String resultado = dao.insertarCliente(usuario, cliente);

        // 5) Respuesta
        if ("ok".equals(resultado)) {
            request.setAttribute("exitoRegistro", "ok");
        } else {
            request.setAttribute("errorRegistro", resultado);
        }

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    // ===== Verificación reCAPTCHA SIN JSON =====
    private boolean verifyRecaptchaNoJson(String recaptchaResponse, String userIp) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) return false;

        HttpURLConnection conn = null;
        try {
            URL url = new URL(RECAPTCHA_VERIFY_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 'remoteip' es opcional; si quieres, puedes omitirlo
            String postData = "secret="   + URLEncoder.encode(RECAPTCHA_SECRET, StandardCharsets.UTF_8)
                            + "&response=" + URLEncoder.encode(recaptchaResponse, StandardCharsets.UTF_8)
                            + "&remoteip=" + URLEncoder.encode(userIp != null ? userIp : "", StandardCharsets.UTF_8);

            byte[] out = postData.getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setFixedLengthStreamingMode(out.length);
            conn.connect();

            try (OutputStream os = conn.getOutputStream()) { os.write(out); }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }

            String body = sb.toString();
            // Ejemplo de respuesta: {"success": true, ...}
            // Buscamos literalmente success:true (con espacios opcionales)
            Pattern p = Pattern.compile("\"success\"\\s*:\\s*true");
            Matcher m = p.matcher(body);
            return m.find();

        } catch (Exception e) {
            e.printStackTrace();
            return false; // ante error, considerar inválido
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}
