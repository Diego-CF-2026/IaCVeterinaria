package Controladores;

import ModeloDAO.UsuarioDAO;
import Modelo.Cliente;
import Modelo.Usuario;

import org.apache.commons.lang3.StringEscapeUtils;
// Importa las clases que ya usas
import jakarta.servlet.http.*;
import jakarta.servlet.*;
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

        // =========================================================
        // 1. CAPTURAR Y SANITIZAR TODA LA ENTRADA DE USUARIO
        // Esto previene el XSS reflejado.
        // =========================================================
        
        // --- 1.1) Captura los valores originales ---
        String nombresOriginal  = request.getParameter("nombres");
        String apellidosOriginal = request.getParameter("apellidos");
        String dniOriginal      = request.getParameter("dni");
        String telefonoOriginal = request.getParameter("telefono");
        String correoOriginal   = request.getParameter("correo");
        String contrasena = request.getParameter("realContrasena"); // Contraseña en texto plano para hash seguro
        
        // --- 1.2) Codificación de Salida (Output Encoding) ---
        // Sanitizamos los campos que serán reflejados en el JSP en caso de error.
        String valNombres = StringEscapeUtils.escapeHtml4(nombresOriginal);
        String valApellidos = StringEscapeUtils.escapeHtml4(apellidosOriginal);
        String valDni = StringEscapeUtils.escapeHtml4(dniOriginal);
        String valTelefono = StringEscapeUtils.escapeHtml4(telefonoOriginal);
        String valCorreo = StringEscapeUtils.escapeHtml4(correoOriginal);


        // =========================================================
        // 2. VERIFICACIÓN DE RECAPTCHA
        // =========================================================
        String recaptchaResponse = request.getParameter("g-recaptcha-response");
        boolean captchaOk = verifyRecaptchaNoJson(recaptchaResponse, request.getRemoteAddr());

        if (!captchaOk) {
            // Si el captcha falla, repoblamos con los valores SANITIZADOS (valNombres, etc.)
            request.setAttribute("valNombres", valNombres); 
            request.setAttribute("valApellidos", valApellidos);
            request.setAttribute("valDni", valDni);
            request.setAttribute("valTelefono", valTelefono);
            request.setAttribute("valCorreo", valCorreo);

            request.setAttribute("errorRegistro", "captcha");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return; // Salimos del método
        }

        // Ya no necesitas la sección "Captura de datos del formulario" (tu sección 2)
        // porque ya capturamos y sanitizamos los datos arriba (nombresOriginal vs valNombres).
        // Los campos de Cliente usarán las variables valXXX.

        // =========================================================
        // 3. MANTENER VALORES EN CASO DE ERROR DE BD
        // =========================================================
        // Establecemos los atributos sanitizados, que se usarán en caso de
        // errores de duplicado (DNI, Correo, Teléfono, etc.)
        request.setAttribute("valNombres", valNombres);
        request.setAttribute("valApellidos", valApellidos);
        request.setAttribute("valDni", valDni);
        request.setAttribute("valTelefono", valTelefono);
        request.setAttribute("valCorreo", valCorreo);

        // =========================================================
        // 4. CREAR OBJETOS DE MODELO (Usando datos sanitizados o el original si es numérico)
        // =========================================================
        Usuario usuario = new Usuario();
        usuario.setCorreo(correoOriginal); // El correo no necesita escape para la BD, pero es buena práctica validarlo/sanitizarlo si es para la DB. Usaremos el original aquí.
        usuario.setContra(contrasena);
        usuario.setIntentos(0); 
        usuario.setEstado(true); 

        Cliente cliente = new Cliente();
        // Usamos las variables sanitizadas (valNombres, valApellidos) para el objeto Cliente
        cliente.setNombre(valNombres);
        cliente.setApellido(valApellidos); 
        cliente.setDni(dniOriginal); // DNI es numérico, no necesita escape para la DB
        cliente.setTelefono(telefonoOriginal); // Teléfono es numérico, no necesita escape para la DB

        // =========================================================
        // 5. LLAMADA AL DAO PARA INSERTAR EN LA BASE DE DATOS
        // =========================================================
        UsuarioDAO dao = new UsuarioDAO();
        // Aquí debes asegurar que tu DAO esté preparado para manejar la 'contrasena' 
        // y aplicar BCrypt/hashing seguro ANTES de guardarla en la base de datos.
        String resultado = dao.insertarCliente(usuario, cliente); 

        // =========================================================
        // 6. PREPARAR LA RESPUESTA SEGÚN EL RESULTADO
        // =========================================================
        if ("ok".equals(resultado)) {
            request.setAttribute("exitoRegistro", "ok"); 
        } else {
            // Si hay un error de DB (duplicado, etc.), se envían los valores sanitizados 
            // que se establecieron en el paso 3.
            request.setAttribute("errorRegistro", resultado); 
        }

        // Redirigimos de nuevo a index.jsp
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
