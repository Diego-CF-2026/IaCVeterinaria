<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Contacto | Veterinaria Santa Cruz</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contacto.css" />
        <!-- CSS general de modo noche (mismo que en las otras páginas) -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css" />
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />

        <style>
            /* ===== BOTÓN FLOTANTE MODO NOCHE ===== */
            .modo-noche-flotante {
                position: fixed;
                bottom: 20px;
                right: 20px;
                width: 45px;
                height: 45px;
                border-radius: 50%;
                border: none;
                background-color: #111;
                color: #fff;
                font-size: 20px;
                cursor: pointer;
                box-shadow: 0 4px 10px rgba(0,0,0,0.3);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 3000;
                transition: background-color 0.3s ease, transform 0.1s ease, box-shadow 0.3s ease;
            }
            .modo-noche-flotante:hover {
                background-color: #000;
                transform: translateY(-1px);
                box-shadow: 0 6px 14px rgba(0,0,0,0.45);
            }

            /* ===== AJUSTES DE ESTA PÁGINA EN MODO NOCHE ===== */

            /* Fondo general */
            body.modo-noche {
                background-color: #18191A;
                color: #f5f5f5;
            }

            /* Navbar oscura (por si contacto.css no lo define) */
            body.modo-noche .navbar {
                background-color: #242526;
                box-shadow: 0 2px 8px rgba(0,0,0,0.8);
            }
            body.modo-noche .center-links a {
                color: #e5e7eb;
            }
            body.modo-noche .center-links a.active-link,
            body.modo-noche .center-links a:hover {
                color: #ffffff;
                border-bottom-color: #ffffff;
            }

            /* Contenedor principal de contacto */
            body.modo-noche .contacto-texto {
                background-color: #111827;
                color: #e5e7eb;
                box-shadow: 0 12px 32px rgba(0,0,0,0.7);
            }
            body.modo-noche .contacto-texto h1 {
                color: #f9fafb;
            }
            body.modo-noche .contacto-texto p {
                color: #e5e7eb;
            }

            /* Caja de datos de contacto */
            body.modo-noche .contacto-datos {
                background-color: #020617;
                border-color: #1f2937;
                color: #e5e7eb;
            }

            /* Texto extra */
            body.modo-noche .contacto-extra p {
                color: #e5e7eb;
            }

            /* Sidebar perfil en oscuro */
            body.modo-noche .sidebar-perfil {
                background-color: #242526;
                color: #e5e7eb;
            }
            body.modo-noche .sidebar-perfil a {
                color: #e5e7eb;
                border-bottom-color: #333;
            }
            body.modo-noche .sidebar-perfil a:hover {
                background-color: #2a2a2a;
            }

            /* Footer oscuro */
            body.modo-noche footer {
                background-color: #020617;
                color: #e5e7eb;
            }
            body.modo-noche .footer-container,
            body.modo-noche .footer-bottom {
                color: #e5e7eb;
            }
            body.modo-noche .redes-sociales span {
                background-color: #111827;
                color: #f9fafb;
            }

            /* Botón flotante en modo noche: invertimos colores (icono sol) */
            body.modo-noche .modo-noche-flotante {
                background-color: #f9fafb;
                color: #111827;
            }
        </style>
    </head>
    <body>

        <nav class="navbar">
            <div class="logo-container">
                <a href="${pageContext.request.contextPath}/index.jsp">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo" />
                </a>
            </div>
            <div class="hamburger" id="hamburger" aria-label="Menú" aria-expanded="false">
                <span></span><span></span><span></span>
            </div>
            <div class="nav-links" id="nav-links">
                <div class="center-links">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                    <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto" class="active-link">Contacto</a>
                </div>
                <div class="buttons">
                    <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
                </div>
            </div>
        </nav>

        <!-- Sidebar perfil -->
        <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
            <h2 id="perfilTitle">Mi Perfil</h2>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
            <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
            <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet">Citas agendadas</a>
            <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
        </div>
        <div id="sidebarOverlay"></div>

        <main class="contacto-texto">
            <h1>Contacto</h1>
            <p>
                ¿Tienes alguna pregunta, sugerencia o necesitas información sobre nuestros servicios?<br>
                Puedes comunicarte con nosotros a través de los siguientes medios:
            </p>
            <div class="contacto-datos">
                <p><strong>Dirección:</strong> Av. Siempre Viva 742, Lima, Perú</p>
                <p><strong>Teléfono:</strong> +51 987 654 321</p>
                <p><strong>Email:</strong> contacto@veterinariasantacruz.com</p>
                <p><strong>Horario de atención:</strong> Lunes a Viernes, 9:00 - 18:00 hrs</p>
            </div>
            <div class="contacto-extra">
                <p>
                    También puedes visitarnos en nuestra clínica o escribirnos por correo electrónico.<br>
                    ¡Estaremos encantados de ayudarte y cuidar de tu mascota!
                </p>
            </div>
        </main>

        <footer>
            <div class="footer-container">
                <div class="footer-section">
                    <h4>Contacto</h4>
                    <p>Av. Siempre Viva 742, Lima, Perú</p>
                    <p>Teléfono: +51 987 654 321</p>
                    <p>Email: contacto@veterinariasantacruz.com</p>
                </div>
                <div class="footer-section">
                    <h4>Síguenos</h4>
                    <div class="redes-sociales">
                        <span>Facebook</span>
                        <span>Instagram</span>
                        <span>Twitter</span>
                    </div>
                </div>
            </div>
            <div class="footer-bottom">
                <p>© 2025 Veterinaria Santa Cruz - Todos los derechos reservados</p>
            </div>
        </footer>

        <!-- Botón flotante de modo noche -->
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <!-- JS de modo noche (el mismo que ya usas en otras vistas) -->
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

        <script>
            document.getElementById('verPerfilBtn').addEventListener('click', function () {
                document.getElementById('sidebarPerfil').classList.add('active');
                document.getElementById('sidebarOverlay').classList.add('active');
            });

            document.getElementById('sidebarOverlay').addEventListener('click', function () {
                document.getElementById('sidebarPerfil').classList.remove('active');
                this.classList.remove('active');
            });

            document.getElementById('hamburger').addEventListener('click', function () {
                this.classList.toggle('active');
                document.getElementById('nav-links').classList.toggle('active');
            });
        </script>

    </body>
</html>
