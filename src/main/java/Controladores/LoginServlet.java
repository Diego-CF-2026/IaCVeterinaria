package Controladores;

import Modelo.Cliente;
import Modelo.Usuario;
import Modelo.Veterinario;
import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioDAO;
import ModeloDAO.VeterinarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * Servlet para manejar el inicio de sesión de los usuarios.
 * Incluye control de intentos fallidos, bloqueo temporal y redirección según el rol.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

    private static final int MAX_INTENTOS = 3;      // Número máximo de intentos fallidos
    private static final int TIEMPO_BLOQUEO_MINUTOS = 2; // Duración del bloqueo temporal

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Captura de credenciales desde el formulario
        String correo = request.getParameter("correo");
        // Este campo contiene la contraseña en TEXTO PLANO.
        String contraTextoPlano = request.getParameter("contrasena");
        String contextPath = request.getContextPath();

        // Consultar intentos y estado de bloqueo previo del usuario
        Usuario usuarioInfo = usuarioDAO.obtenerIntentosYBloqueo(correo);

        // Verificar si la cuenta está temporalmente bloqueada
        if (usuarioInfo != null) {
            Timestamp tiempoBloqueo = usuarioInfo.getTiempoBloqueo();
            if (tiempoBloqueo != null && tiempoBloqueo.getTime() > System.currentTimeMillis()) {
                long segundosRestantes = (tiempoBloqueo.getTime() - System.currentTimeMillis()) / 1000;
                long minutosRestantes = segundosRestantes / 60;

                // Informar al usuario sobre el tiempo restante de bloqueo
                request.setAttribute("errorLogin",
                        "Tu cuenta está bloqueada temporalmente. Intenta nuevamente en " + (minutosRestantes + 1) + " minutos.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return; 
            }
        }

        // Intento de login con las credenciales proporcionadas
        Usuario usuario = usuarioDAO.login(correo, contraTextoPlano);

        if (usuario != null && usuario.isEstado()) {
            // Si el login es exitoso, reiniciar contador de intentos
            usuarioDAO.reiniciarIntentos(correo);

            // Crear sesión y almacenar datos del usuario
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("idRol", usuario.getIdRol());

            // Redirigir según rol del usuario
            switch (usuario.getIdRol()) {
                case 1: // Administrador
                    response.sendRedirect(contextPath + "/VistasWeb/VistasAdmin/AdminDash.jsp");
                    break;

                case 2: // Recepcionista
                    response.sendRedirect(contextPath + "/VistasWeb/VistasRecep/RecepDash.jsp");
                    break;

                case 3: // Cliente
                    // Buscar información adicional del cliente
                    Cliente cliente = clienteDAO.buscarPorIdUsuario(usuario.getIdUsuario());
                    if (cliente != null) {
                        sesion.setAttribute("cliente", cliente);
                        sesion.setAttribute("idClienteSesion", cliente.getIdCliente());
                        sesion.setAttribute("NombreCliente", cliente.getNombre());
                    }
                    // Redirigir a la vista de cliente
                    response.sendRedirect(contextPath + "/VistasWeb/VistasCliente/Nosotros.jsp");
                    break;

                case 4: // Veterinario
                    // Obtener ID del veterinario asociado al usuario
                    int idUsuario = usuario.getIdUsuario();
                    Integer idVeterinario = veterinarioDAO.obtenerIdVeterinarioPorIdUsuario(idUsuario);

                    if (idVeterinario != null) {
                        // Guardar ID de veterinario en la sesión
                        sesion.setAttribute("idVeterinario", idVeterinario);
                    } else {
                        // Manejo de error si no existe registro de veterinario
                        sesion.setAttribute("errorLogin",
                                "Error de configuración: Usuario Veterinario incompleto. Contacte a soporte.");
                        response.sendRedirect(contextPath + "/index.jsp");
                        return;
                    }

                    response.sendRedirect(contextPath + "/VistasWeb/VistasVeterinario/VeterinarioDash.jsp");
                    break;

                default:
                    // Rol desconocido
                    request.setAttribute("errorLogin", "Rol no válido.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    break;
            }

        } else {
            // Manejo de login fallido
            String mensajeError = "Correo o contraseña incorrectos, o usuario inactivo.";

            // Incrementar contador de intentos si existe registro previo
            if (usuarioInfo != null) {
                int nuevosIntentos = usuarioDAO.incrementarIntentos(correo);

                // Bloquear usuario si se supera el máximo de intentos
                if (nuevosIntentos >= MAX_INTENTOS) {
                    usuarioDAO.bloquearUsuario(correo, TIEMPO_BLOQUEO_MINUTOS);
                    mensajeError = "Demasiados intentos fallidos. Tu cuenta ha sido bloqueada por "
                            + TIEMPO_BLOQUEO_MINUTOS + " minutos.";
                } else {
                    mensajeError += " Te quedan " + (MAX_INTENTOS - nuevosIntentos) + " intentos.";
                }
            }

            // Enviar mensaje de error al JSP de login
            request.setAttribute("errorLogin", mensajeError);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

        // Explicación general:
        // Se valida bloqueo temporal antes de autenticar
        // Se reinicia contador si login es exitoso
        // Se maneja sesión con HttpSession
        // Se redirige según rol (Administrador, Recepcionista, Cliente, Veterinario)
        // Se incrementan intentos fallidos y se bloquea temporalmente si es necesario
        // Se maneja error de rol desconocido y de usuario Veterinario sin registro
        // Seguridad: contraseña no se almacena en sesión
        // Todos los flujos terminan con forward o redirect apropiado
        // Recomendable usar HTTPS para proteger credenciales
        // Los mensajes de error son amigables y claros para el usuario
        // Este servlet es el punto central de login y control de sesión inicial
        // Permite fácilmente agregar más roles en el futuro
        // Evita crear sesión innecesaria hasta después de login exitoso
        // Garantiza que los datos de Cliente o Veterinario estén disponibles en sesión
        // Manejo de tiempo de bloqueo usando Timestamp para control preciso
    }
}
