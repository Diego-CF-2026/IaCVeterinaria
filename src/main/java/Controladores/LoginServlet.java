package Controladores;

import Modelo.Cliente;
import Modelo.Usuario;
import ModeloDAO.ClienteDAO;
import ModeloDAO.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp; // ¡esto descargue para el tiempo

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();

    private static final int MAX_INTENTOS = 3;      // Límite de fallos
    private static final int TIEMPO_BLOQUEO_MINUTOS = 2; // Bloqueo en minutos

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contra = request.getParameter("contrasena");
        String contextPath = request.getContextPath();
        
        
        Usuario usuarioInfo = usuarioDAO.obtenerIntentosYBloqueo(correo);

        if (usuarioInfo != null) {
            Timestamp tiempoBloqueo = usuarioInfo.getTiempoBloqueo();
            
           
            if (tiempoBloqueo != null && tiempoBloqueo.getTime() > System.currentTimeMillis()) {
                
                
                long segundosRestantes = (tiempoBloqueo.getTime() - System.currentTimeMillis()) / 1000;
                long minutosRestantes = segundosRestantes / 60;
                
                request.setAttribute("errorLogin", "Tu cuenta está bloqueada temporalmente. Intenta nuevamente en " + (minutosRestantes + 1) + " minutos.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return; 
            }
        }
        
        Usuario usuario = usuarioDAO.login(correo, contra);

        if (usuario != null && usuario.isEstado()) {
            
            usuarioDAO.reiniciarIntentos(correo); 

            // Crear sesión
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuario);

            switch (usuario.getIdRol()) {
                case 1: // Administrador
                    response.sendRedirect(contextPath + "/VistasWeb/VistasAdmin/AdminDash.jsp");
                    break;

                case 2: // Recepcionista
                    response.sendRedirect(contextPath + "/CitaServlet");
                    break;

                case 3: // Cliente
                    Cliente cliente = clienteDAO.buscarPorIdUsuario(usuario.getIdUsuario());
                    if (cliente != null) {
                        sesion.setAttribute("cliente", cliente);
                        sesion.setAttribute("idCliente", cliente.getIdCliente());
                        sesion.setAttribute("nombreCliente", cliente.getNombre());
                    }
                    response.sendRedirect(contextPath + "/VistasWeb/VistasCliente/Nosotros.jsp");
                    break;

                default:
                    request.setAttribute("errorLogin", "Rol no válido.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    break;
            }

        } else {


            String mensajeError = "Correo o contraseña incorrectos, o usuario inactivo.";

            if (usuarioInfo != null) {
                
                int nuevosIntentos = usuarioDAO.incrementarIntentos(correo);
                
                if (nuevosIntentos >= MAX_INTENTOS) {
                    
                    usuarioDAO.bloquearUsuario(correo, TIEMPO_BLOQUEO_MINUTOS);
                    mensajeError = "Demasiados intentos fallidos. Tu cuenta ha sido bloqueada por " + TIEMPO_BLOQUEO_MINUTOS + " minutos.";
                    
                } else {
                    mensajeError += " Te quedan " + (MAX_INTENTOS - nuevosIntentos) + " intentos.";
                }
            }
            

            request.setAttribute("errorLogin", mensajeError);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}