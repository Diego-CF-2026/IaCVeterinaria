<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.*,jakarta.servlet.*" %>
<%
    String errorLogin = (String) request.getAttribute("errorLogin");
    String exitoRegistro = (String) request.getAttribute("exitoRegistro");
    String errorRegistro = (String) request.getAttribute("errorRegistro");

    // valores para repoblar el formulario de registro si hubo error
    String valNombres = request.getAttribute("valNombres") != null ? (String) request.getAttribute("valNombres") : "";
    String valApellidos = request.getAttribute("valApellidos") != null ? (String) request.getAttribute("valApellidos") : "";
    String valDni = request.getAttribute("valDni") != null ? (String) request.getAttribute("valDni") : "";
    String valTelefono = request.getAttribute("valTelefono") != null ? (String) request.getAttribute("valTelefono") : "";
    String valCorreo = request.getAttribute("valCorreo") != null ? (String) request.getAttribute("valCorreo") : "";

%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>VeterinariaSantaCruz</title>
        <link rel="preload" as="image" href="Recursos/ImgDoctor.png">
        <link rel="stylesheet" href="css/index.css">
        <style>
            /* Estilos críticos en línea para mejorar el First Contentful Paint (FCP) */
            /* Nota: Se mantiene el estilo de error de login */
            #mensajeErrorLogin {
                color: red; 
                font-weight: bold;
                margin-top: 10px;
                text-align: center;
            }
        </style>
        </head>
    
    <body>
        
        <%-- Bloque JS para aplicar el modo noche inmediatamente antes de renderizar el contenido --%>
        <script>
            // Este script es CRÍTICO para evitar el FOUC (flash of unstyled content) en modo noche.
            const modoGuardado = localStorage.getItem('modo-noche');
            if (modoGuardado === 'activado') {
                document.body.classList.add('modo-noche');
            }
        </script>

        <nav class="navbar">
            <div class="logo-container">
                <a href="#">
                    <img id="logo" src="Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                </a>
            </div>
            <div class="hamburger" id="hamburger" aria-label="Menú" aria-expanded="false">
                <span></span>
                <span></span>
                <span></span>
            </div>
            <div class="nav-links" id="nav-links">
                <div class="center-links">
                    <a href="#">Nosotros</a>
                    <a href="#">Servicios</a>
                    <a href="#">Productos</a>
                    <a href="#">Contacto</a>
                </div>
                <div class="buttons">
                    <a href="#" class="btn login" onclick="abrirLogin()">Iniciar Sesión</a> 
                    <a href="#" onclick="abrirRegistro()" class="btn register">Registrarse</a>
                </div>
            </div>

            <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        </nav>

        <%-- MODAL LOGIN --%>
        <div id="modalLogin" class="modal">
            <div class="modal-content animado">
                <span class="cerrar" onclick="cerrarModal('modalLogin')">&times;</span>
                <h2 id="tituloLogin">Iniciar Sesión</h2>
                <img src="Recursos/IconUser.svg" alt="Icono Usuario" class="icono-usuario">

                <form id="formLogin" action="LoginServlet" method="post">
                    <input type="hidden" id="inputRol" name="rol" value="">
                    <input type="email" name="correo" placeholder="Correo electrónico" required>
                    <input type="password" name="contrasena" placeholder="Contraseña" required>

                    <button type="submit" class="btn1 iniciar-sesion">Ingresar</button>
                </form>

                <%-- Bloque para mostrar errores de login --%>
                <% if (request.getAttribute("errorLogin") != null) {%>
                <div id="mensajeErrorLogin" class="alert error" style="display: block;"><%= request.getAttribute("errorLogin")%></div>
                <% } else { %>
                <div id="mensajeErrorLogin" class="alert error" style="display: none;"></div>
                <% }%>

                <div id="opcionesRegistro">
                    <p>¿Aún no tienes una cuenta? <a href="#" onclick="abrirRegistroDesdeLogin()">Regístrate</a></p>
                </div>
            </div>
        </div>


        <%-- MODAL REGISTRO --%>
        <div id="modalRegistro" class="modal" 
             style="<%= (request.getAttribute("errorRegistro") != null
                     || request.getAttribute("exitoRegistro") != null) ? "display:flex;" : ""%>">
            <div class="modal-content animado">
                <span class="cerrar" onclick="cerrarModal('modalRegistro')">&times;</span>
                <img id="logoRegistro" src="Recursos/Logo.png" alt="Logo" class="icono-patita">
                <h2>Registrar Cliente</h2>

                <%-- Mensajes de Error/Éxito --%>
                <% if ("error".equals(String.valueOf(request.getAttribute("errorRegistro")))) { %>
                <div class="alert error">Error general al registrar. Intenta nuevamente.</div>
                <% } %>
                <% if ("dni".equals(request.getAttribute("errorRegistro"))) { %>
                <div class="alert error">DNI ya registrado.</div>
                <% } %>
                <% if ("correo".equals(request.getAttribute("errorRegistro"))) { %>
                <div class="alert error">Correo ya registrado.</div>
                <% } %>
                <% if ("telefono".equals(request.getAttribute("errorRegistro"))) { %>
                <div class="alert error">Teléfono ya registrado.</div>
                <% } %>
                <% if (request.getAttribute("exitoRegistro") != null) { %>
                <div class="alert success">¡Registro exitoso! Ya puedes iniciar sesión.</div>
                <% }%>

                <form class="form-registro" action="RegistrarServlet" method="post">
                    <div class="input-group">
                        <input type="text" name="nombres" placeholder="Nombres" required 
                               pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras"
                               value="<%= request.getAttribute("valNombres") != null ? request.getAttribute("valNombres") : ""%>">

                        <input type="text" name="apellidos" placeholder="Apellidos" required 
                               pattern="[A-Za-zÁÉÍÓÚáéíóúñÑ ]+" title="Solo letras"
                               value="<%= request.getAttribute("valApellidos") != null ? request.getAttribute("valApellidos") : ""%>">
                    </div>

                    <div class="input-group">
                        <div style="width: 100%;">
                            <input type="text" name="dni" placeholder="DNI" required minlength="8" maxlength="8"
                                   value="<%= request.getAttribute("valDni") != null ? request.getAttribute("valDni") : ""%>"
                                   class="<%= "dni".equals(request.getAttribute("errorRegistro")) ? "campo-error" : ""%>">
                        </div>

                        <div style="width: 100%;">
                            <input type="tel" name="telefono" placeholder="Número telefónico" required 
                                   pattern="9[0-9]{8}" title="Debe empezar con 9 y tener 9 dígitos"
                                   value="<%= request.getAttribute("valTelefono") != null ? request.getAttribute("valTelefono") : ""%>"
                                   class="<%= "telefono".equals(request.getAttribute("errorRegistro")) ? "campo-error" : ""%>">
                        </div>
                    </div>

                    <input type="email" name="correo" placeholder="Correo Electrónico" required maxlength="100"
                           value="<%= request.getAttribute("valCorreo") != null ? request.getAttribute("valCorreo") : ""%>"
                           class="<%= "correo".equals(request.getAttribute("errorRegistro")) ? "campo-error" : ""%>">

                    <input type="password" name="contrasena" placeholder="Contraseña" required minlength="8" maxlength="45">

                    <div class="g-recaptcha" data-sitekey="6LdCzuorAAAAAELJNXsllBliNLKG8Ko2Yg-Jd2Mj" 
                         data-callback="onCaptchaOk" data-expired-callback="onCaptchaExpired"></div> 
                    <% if ("captcha".equals(request.getAttribute("errorRegistro"))) { %>
                    <div class="alert error">Por favor completa el CAPTCHA.</div>
                    <% }%>


                    <button type="submit" class="btn1 btnRegister">Registrar</button>
                    <p class="switch-login">¿Ya tienes una cuenta? <a href="#" onclick="abrirLoginDesdeRegistro()">Iniciar Sesión</a></p>
                </form>
            </div>
        </div>

        <div class="overlay" id="overlay"></div>

        <section class="main-section">
            <div class="main-content">
                <div class="text-container">
                    <h1>Veterinaria Santa Cruz en Lima</h1>
                    <p class="subtext">Cuidamos a tu mascota con amor, experiencia y compromiso.</p>
                </div>
                <div class="image-container">
                    <img src="Recursos/ImgDoctor.png" alt="Veterinaria Santa Cruz" fetchpriority="high">
                </div>
            </div>
        </section>

        <section class="bloque-principal">
            <div class="div-texto">
                <div class="div-texto-centro">
                    <div class="div-titulo1">
                        <h2>Priorizando a tu compañero de vida</h2>
                    </div>
                    <div class="div-contenido1">
                        <p>
                            En <span>Santa Cruz</span>, nuestra principal misión es asegurar que cada mascota que atendemos disfrute de una vida feliz y saludable. 
                            Nos dedicamos a ofrecer el más alto nivel de atención veterinaria, con profesionalismo y un profundo compromiso de cariño.<br><br>
                            Nuestro equipo de veterinarios y personal especializado trabaja incansablemente para fomentar el cuidado preventivo, brindar tratamientos integrales y acompañar a tu querida mascota en cada etapa de su vida.
                        </p>
                    </div>
                </div>
            </div>
            <div class="div-imagen1">
                <img src="Recursos/Gato.svg" alt="Gato acostado" class="imagen" loading="lazy" decoding="async">
            </div>
        </section>


        <section class="bloque-servicios">
            <div class="div-contenido">
                <div class="titulo-servicios">
                    <h2>Nuestros Servicios</h2>
                </div>
                </div>

            <div class="div-slider">
                <div class="slider-contenedor">
                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/Consulta.svg" alt="Consulta General" loading="lazy" decoding="async">
                        </div>
                        </div>
                    </div>
                </div>
        </section>


        <section class="bloque-tienda">
            </section>


        <footer class="footer">
            </footer>
        
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        
        <script src="Js/Index.js"></script>
        <script src="Js/ModoNocheIndex.js"></script>

        <script>
            // Las funciones de apertura/cierre de modales y reCAPTCHA
            // se mantienen aquí para que el HTML (con los onclick) las encuentre globalmente.
            
            // Funciones globales (definidas en el script de abajo o en Index.js)
            // Se asume que ahora SÓLO se llama a las funciones definidas en Index.js,
            // pero para evitar errores de referencia en el HTML, las definiremos
            // con las llamadas correctas a las funciones principales de Index.js:
            
            function abrirModal(idModal) {
                const modal = document.getElementById(idModal);
                if (modal) modal.style.display = 'flex';
            }

            function cerrarModal(idModal) {
                const modal = document.getElementById(idModal);
                if (modal) modal.style.display = 'none';
            }

            function abrirRegistroDesdeLogin() {
                cerrarModal('modalLogin');
                abrirModal('modalRegistro');
            }

            function abrirLoginDesdeRegistro() {
                cerrarModal('modalRegistro');
                // Llama a la función principal que resetea el formulario
                abrirLogin(); 
            }
            
            // ReCAPTCHA callbacks (Globales para que reCAPTCHA los encuentre)
            function onCaptchaOk() {
                const btn = document.querySelector('.btnRegister');
                if (btn)
                    btn.disabled = false;
            }
            function onCaptchaExpired() {
                const btn = document.querySelector('.btnRegister');
                if (btn)
                    btn.disabled = true;
            }
        </script>
    </body>
</html>