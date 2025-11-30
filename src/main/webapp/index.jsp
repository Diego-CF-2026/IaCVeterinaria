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
        <link rel="stylesheet" href="css/index.css">
        <style>
            /* Estilos para el mensaje de error */
            /* Nota: Se usa la clase .alert.error definida en tu CSS para mantener la consistencia */
            #mensajeErrorLogin {
                color: red; /* Se asegura de que se vea el color del error */
                font-weight: bold;
                margin-top: 10px;
                text-align: center;
            }
        </style>
    </head>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>


    <body>

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
                    <a href="#" class="btn login" onclick="abrirLogin()">Iniciar Sesión</a> <a href="#" onclick="abrirRegistroDesdeLogin()" class="btn register">Registrarse</a>
                </div>
            </div>

            <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        </nav>

        <div id="modalLogin" class="modal">
            <div class="modal-content animado">
                <span class="cerrar" onclick="cerrarModal('modalLogin')">&times;</span>
                <h2>Iniciar Sesión</h2>
                <img src="Recursos/IconUser.svg" alt="Icono Usuario" class="icono-usuario">

                <form id="formLogin" action="LoginServlet" method="post" onsubmit="return hashearContrasenaLogin(event);">
                    <input type="email" name="correo" placeholder="Correo electrónico" required>
                    <input type="password" name="contrasena" placeholder="Contraseña" required autocomplete="off">


                    <button type="submit" class="btn1 iniciar-sesion">Ingresar</button>
                </form>

                <%-- Bloque para mostrar errores de login (bloqueo/intentos) --%>
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




        <div id="modalRegistro" class="modal" 
             style="<%= (request.getAttribute("errorRegistro") != null
                     || request.getAttribute("exitoRegistro") != null) ? "display:flex;" : ""%>">
            <div class="modal-content animado">
                <span class="cerrar" onclick="cerrarModal('modalRegistro')">&times;</span>
                <img id="logoRegistro" src="Recursos/Logo.png" alt="Logo" class="icono-patita">
                <h2>Registrar Cliente</h2>

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

                <form class="form-registro" action="RegistrarServlet" method="post" onsubmit="return hashearContrasenaRegistro(event);">
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

                    <input type="password" name="contrasena" placeholder="Contraseña" required minlength="8" maxlength="45" autocomplete="off">

                    <div class="g-recaptcha" data-sitekey="6LdCzuorAAAAAELJNXsllBliNLKG8Ko2Yg-Jd2Mj"></div>  
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
                    <img src="Recursos/ImgDoctor.png" alt="Veterinaria Santa Cruz">
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
                <img src="Recursos/Gato.svg" alt="Gato acostado" class="imagen">
            </div>
        </section>


        <section class="bloque-servicios">
            <div class="div-contenido">
                <div class="titulo-servicios">
                    <h2>Nuestros Servicios</h2>
                </div>
                <div class="subtitulo-servicios">
                    <p>
                        En Veterinaria Santa Cruz estamos plenamente capacitados para diagnosticar y tratar todas las enfermedades que pueda presentar tu mascota. 
                        Contamos con tecnología de diagnóstico de última generación y un equipo profesional en constante formación, actualizándonos en nuevas técnicas y métodos para brindarte una atención de calidad en medicina veterinaria para animales de compañía.
                    </p>
                </div>
            </div>

            <div class="div-slider">
                <button class="flecha izquierda">
                    <img src="Recursos/FIzquierda.svg" alt="Flecha Izquierda">
                </button>

                <div class="slider-contenedor">
                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/Consulta.svg" alt="Consulta General">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Consulta General</h3>
                            <p>Evaluamos el estado de salud de tu mascota a través de un chequeo físico completo, resolviendo dudas sobre alimentación, comportamiento o síntomas clínicos.</p>
                        </div>
                    </div>

                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/Vacunacion.svg" alt="Vacunación">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Vacunación</h3>
                            <p>Aplicamos vacunas esenciales para prevenir enfermedades contagiosas, adaptadas a la especie y edad de salud de tu mascota.</p>
                        </div>
                    </div>

                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/Desparacitacion.svg" alt="Desparasitación">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Desparasitación</h3>
                            <p>Eliminamos parásitos internos y externos para cuidar su bienestar y el de tu familia.</p>
                        </div>
                    </div>

                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/ImgAtencionE.svg" alt="Atención de Urgencias">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Atención de Urgencias</h3>
                            <p>Contamos con atención inmediata para emergencias médicas que pongan en riesgo la vida o salud de tu mascota.</p>
                        </div>
                    </div>

                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/ImgCertificados.svg" alt="Certificados de Salud">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Certificados de Salud</h3>
                            <p>Emitimos certificados veterinarios oficiales para viajes, adopciones o concursos, avalando el estado de salud de tu mascota.</p>
                        </div>
                    </div>

                    <div class="tarjeta">
                        <div class="tarjeta-imagen">
                            <img src="Recursos/ImgPeluqueria 1.svg" alt="Baño y Peluquería">
                        </div>
                        <div class="tarjeta-contenido">
                            <h3>Baño y Peluquería</h3>
                            <p>Ofrecemos servicios de estética y cuidados como baños, corte de uñas, limpieza de oídos y peluquería especializada.</p>
                        </div>
                    </div>

                </div>

                <button class="flecha derecha">
                    <img src="Recursos/FDerecha.svg" alt="Flecha Derecha">
                </button>
            </div>
        </section>


        <section class="bloque-tienda">
            <div class="div-contenido-tienda">
                <h2>Tienda Santa Cruz</h2>
            </div>

            <div class="div-slider-tienda">
                <div class="slider-tienda-contenedor">
                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/Gatotienda.svg" alt="Alimentos balanceados">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Alimentos balanceados</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Croquetas y alimentos húmedos para cachorros, adultos y mascotas con necesidades especiales (digestivas, renales, obesidad, etc.).</p>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/Suplementos.svg" alt="Suplementos y vitaminas">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Suplementos y vitaminas</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Apoyo nutricional para fortalecer articulaciones, sistema inmune, piel, pelaje y más.</p>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/Desparacitacion.svg" alt="Antipulgas y desparasitantes">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Antipulgas y desparasitantes</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Pipetas, collares, comprimidos y jarabes para prevenir y tratar pulgas, garrapatas y parásitos internos.</p>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/Higiene.svg" alt="Productos de higiene">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Productos de higiene</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Shampoos medicados, jabones dermatológicos, colonias y talcos para el cuidado diario.</p>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/ImgJuguetes 1.svg" alt="Juguetes y enriquecimiento">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Juguetes y enriquecimiento</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Pelotas, mordedores y juegos interactivos para estimular física y mentalmente a tu mascota.</p>
                            </div>
                        </div>
                    </div>

                    <div class="tarjeta-tienda">
                        <div class="tarjeta-imagen-tienda">
                            <img src="Recursos/ImgCollar 1.svg" alt="Collares, correas y arneses">
                        </div>
                        <div class="tarjeta-texto-tienda">
                            <div class="titulo-tarjeta">
                                <h3>Collares, correas y arneses</h3>
                            </div>
                            <div class="subtitulo-tarjeta">
                                <p>Variedad de estilos y tamaños para paseos cómodos y seguros.</p>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </section>


        <footer class="footer">
            <div class="footer-contenido">
                <h2>Veterinaria Santa Cruz</h2>
                <p>
                    Tu mascota merece lo mejor. Escríbenos o visítanos para agendar una consulta, resolver tus dudas o conocer más sobre nuestros servicios y productos.
                </p>
                <p>
                    Estamos aquí para cuidar a tu mejor amigo con cariño y profesionalismo.
                </p>

                <div class="footer-social">
                    <a href="#"><img src="Recursos/Tiktok.svg" alt="TikTok"></a>
                    <a href="#"><img src="Recursos/WhatsApp.svg" alt="WhatsApp"></a>
                    <a href="#"><img src="Recursos/Facebook.svg" alt="Facebook"></a>
                </div>

                <div class="footer-copy">
                    <p>©Grupo 3 / SolucionesWeb</p>
                </div>
            </div>
        </footer>
        <script src="Js/Index.js"></script>
        <script src="Js/ModoNocheIndex.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/js-sha256@0.9.0/build/sha256.min.js"></script>
        <script>
                // Variables de control (flags) para evitar que las funciones se ejecuten dos veces.
                let isLoginHashed = false;
                let isRegistroHashed = false;

                // Función para hashear la contraseña de REGISTRO
                function hashearContrasenaRegistro(event) {
                    const form = event.target;
                    const contrasenaInput = form.querySelector('input[name="contrasena"]');

                    // Solo hashear si no se ha hasheado aún
                    if (!isRegistroHashed && contrasenaInput && contrasenaInput.value) {
                        const contrasenaOriginal = contrasenaInput.value;

                        // Aplicamos el hash SHA-256
                        const hashedContrasena = sha256(contrasenaOriginal);

                        // Reemplazamos el valor original con el hash SHA-256
                        contrasenaInput.value = hashedContrasena;
                        isRegistroHashed = true; // Marcamos que ya está hasheado
                    }
                    // Permitimos el envío del formulario.
                    return true; 
                }

                // 🌟 FUNCIÓN CLAVE MODIFICADA para LOGIN 🌟
                function hashearContrasenaLogin(event) {
                    const form = event.target;
                    const contrasenaInput = form.querySelector('input[name="contrasena"]');
                    // NUEVO: Referencia al botón de submit
                    const submitButton = form.querySelector('button[type="submit"]'); 

                    // Solo hashear si no se ha hasheado aún
                    if (!isLoginHashed && contrasenaInput && contrasenaInput.value) {

                        // 1. Deshabilitar el botón para prevenir el doble click
                        if (submitButton) {
                            submitButton.disabled = true;
                            submitButton.textContent = 'Ingresando...'; 
                        }

                        const contrasenaOriginal = contrasenaInput.value;

                        // Aplicamos el hash SHA-256
                        const hashedContrasena = sha256(contrasenaOriginal);

                        // Reemplazamos el valor original con el hash SHA-256
                        contrasenaInput.value = hashedContrasena;
                        isLoginHashed = true; // Marcamos que ya está hasheado
                    } else if (isLoginHashed) {
                        // Si ya está hasheado (doble submit en la misma acción), 
                        // ya debería estar deshabilitado, pero permitimos el envío.
                        return true; 
                    }

                    // Permitimos el envío.
                    return true;
                }
        </script>
        <script>
                        // Funciones para abrir y cerrar modales
                        function abrirModal(idModal) {
                            document.getElementById(idModal).style.display = 'flex';
                        }

                        function cerrarModal(idModal) {
                            document.getElementById(idModal).style.display = 'none';
                        }

                        // ✅ Abre directamente el modal de login
                        function abrirLogin() {
                            // Limpia mensajes de error y resetea el formulario
                            const formLogin = document.getElementById('formLogin');
                            const mensajeErrorLogin = document.getElementById('mensajeErrorLogin');

                            if (formLogin)
                                formLogin.reset();
                            if (mensajeErrorLogin) {
                                // Ocultamos el div y limpiamos el texto si se abre sin error
                                mensajeErrorLogin.style.display = 'none';
                                mensajeErrorLogin.textContent = '';
                            }

                            abrirModal('modalLogin');
                        }

                        // ✅ Navegación entre login y registro
                        function abrirRegistroDesdeLogin() {
                            cerrarModal('modalLogin');
                            abrirModal('modalRegistro');
                        }

                        function abrirLoginDesdeRegistro() {
                            cerrarModal('modalRegistro');
                            abrirLogin(); // vuelve al login directo
                        }

                        // ✅ Cuando el DOM ya cargó
                        document.addEventListener('DOMContentLoaded', function () {
                            const modalLogin = document.getElementById('modalLogin');
                            const mensajeErrorLogin = document.getElementById('mensajeErrorLogin');
                            const btnLoginNavbar = document.querySelector('.navbar .btn.login');

                            // Mantenemos el modal de Login abierto si hay un error de Login al cargar la página.
                            // Esto asegura que el mensaje del Servlet sea visible inmediatamente.
                            if (mensajeErrorLogin && mensajeErrorLogin.textContent.trim().length > 0) {
                                modalLogin.style.display = 'flex';
                                mensajeErrorLogin.style.display = 'block';
                            }


                            // Botón de la navbar abre login
                            if (btnLoginNavbar) {
                                btnLoginNavbar.onclick = function () {
                                    abrirLogin();
                                    return false;
                                };
                            }
                        });
// reCAPTCHA callbacks
                        function onCaptchaOk() {
                            const btn = document.getElementById('btnRegister');
                            if (btn)
                                btn.disabled = false;
                        }
                        function onCaptchaExpired() {
                            const btn = document.getElementById('btnRegister');
                            if (btn)
                                btn.disabled = true;
                            // Si quieres obligar a resolver de nuevo:
                            // grecaptcha.reset();
                        }

                        // Guardia extra: no permitas submit sin token
                        document.addEventListener('DOMContentLoaded', function () {
                            const form = document.getElementById('formRegistro');
                            if (!form)
                                return;
                            form.addEventListener('submit', function (e) {
                                const token = (typeof grecaptcha !== 'undefined') ? grecaptcha.getResponse() : '';
                                if (!token || token.length === 0) {
                                    e.preventDefault();
                                    // Muestra alerta puntual si no hay token
                                    let alerta = document.querySelector('#captchaAlert');
                                    if (!alerta) {
                                        alerta = document.createElement('div');
                                        alerta.id = 'captchaAlert';
                                        alerta.className = 'alert error';
                                        alerta.textContent = 'Por favor completa el reCAPTCHA.';
                                        const captchaDiv = form.querySelector('.g-recaptcha');
                                        if (captchaDiv)
                                            captchaDiv.insertAdjacentElement('afterend', alerta);
                                    }
                                    return false;
                                }
                            });
                        });
        </script>
    </body>
</html>