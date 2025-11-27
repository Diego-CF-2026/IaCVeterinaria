<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Servicios | Veterinaria Santa Cruz</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/servicios.css">
        <style>
            /* Sidebar perfil */
            .sidebar-perfil {
                display: none;
                position: fixed;
                top: 0;
                right: 0;
                width: 280px;
                height: 100%;
                background-color: #fff;
                box-shadow: -2px 0 5px rgba(0,0,0,0.3);
                z-index: 2000;
                padding-top: 50px;
                overflow-y: auto;
                transition: transform 0.3s ease;
                transform: translateX(100%);
            }
            .sidebar-perfil.active {
                display: block;
                transform: translateX(0);
            }
            .sidebar-perfil h2 {
                margin: 0 0 20px 20px;
                font-weight: 600;
                font-size: 22px;
            }
            .sidebar-perfil a {
                display: block;
                padding: 15px 25px;
                color: #333;
                text-decoration: none;
                border-bottom: 1px solid #eee;
                font-size: 16px;
                transition: background-color 0.2s;
            }
            .sidebar-perfil a:hover {
                background-color: #f0f0f0;
            }
            /* Overlay */
            #sidebarOverlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100vw;
                height: 100vh;
                background-color: rgba(0,0,0,0.4);
                z-index: 1500;
                transition: opacity 0.3s ease;
            }
            #sidebarOverlay.active {
                display: block;
                opacity: 1;
            }

            /* Link activo en navbar */
            .center-links a.active-link {
                border-bottom: 2px solid #000;
                font-weight: 600;
            }

            /* Contenedor flotante para cita + modo noche */
            .flotante-cita {
                position: fixed;
                bottom: 30px;
                right: 30px;
                z-index: 1000;
                display: flex;
                flex-direction: column;
                align-items: flex-end;
                gap: 10px;
            }

            .btn-cita-flotante {
                background: linear-gradient(135deg, #2c3342 0%, #5b657a 100%);
                color: white;
                padding: 15px 25px;
                border-radius: 50px;
                font-weight: 600;
                box-shadow: 0 10px 25px rgba(44, 51, 66, 0.3);
                display: flex;
                align-items: center;
                gap: 10px;
                text-decoration: none;
                font-size: 1rem;
                transition: all 0.3s;
            }
            .btn-cita-flotante:hover {
                transform: translateY(-3px) scale(1.07);
                box-shadow: 0 15px 30px rgba(44, 51, 66, 0.4);
            }

            /* Botón flotante Modo Noche */
            .modo-noche-flotante {
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
                transition: background-color 0.3s ease,
                    transform 0.1s ease,
                    box-shadow 0.3s ease;
            }
            .modo-noche-flotante:hover {
                background-color: #000;
                transform: translateY(-1px);
                box-shadow: 0 6px 14px rgba(0,0,0,0.45);
            }

            @media (max-width: 768px) {
                .flotante-cita {
                    bottom: 20px;
                    right: 20px;
                    gap: 8px;
                }
                .modo-noche-flotante {
                    width: 40px;
                    height: 40px;
                    font-size: 18px;
                }
                .btn-cita-flotante {
                    padding: 12px 20px;
                    font-size: 0.9rem;
                }
            }

            /* ========= MODO NOCHE ========= */

            /* Fondo general */
            body.modo-noche {
                background-color: #18191A;
                color: #f5f5f5;
            }

            /* Navbar oscura */
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

            /* Botón "Ver Perfil" oscuro */
            body.modo-noche .btn.perfil {
                background-color: #000 !important;
                color: #fff !important;
            }

            /* Hero en modo noche (fondo azul -> degradado oscuro) */
            body.modo-noche .hero-servicios {
                background: linear-gradient(180deg, #111827 0%, #1f2937 100%);
                color: #f9fafb;
            }
            body.modo-noche .hero-servicios h1,
            body.modo-noche .hero-servicios p {
                color: #f9fafb;
                text-shadow: 0 3px 8px rgba(0,0,0,0.7);
            }

            /* Cards de servicios */
            body.modo-noche .servicios-grid {
                background-color: transparent;
            }
            body.modo-noche .servicio-card {
                background-color: #242526;
                box-shadow: 0 8px 24px rgba(0,0,0,0.5);
                border: 1px solid #3a3b3c;
            }
            body.modo-noche .servicio-card h3 {
                color: #f9fafb;
            }
            body.modo-noche .servicio-card ul li {
                color: #e5e7eb;
            }
            body.modo-noche .btn-servicio {
                background-color: #2563eb;
                color: #f9fafb;
            }
            body.modo-noche .btn-servicio:hover {
                background-color: #1d4ed8;
            }

            /* CTA y footer */
            body.modo-noche .cta-section {
                background-color: #111827;
                color: #f9fafb;
            }
            body.modo-noche .footer-servicios {
                background-color: #020617;
                color: #9ca3af;
            }

            /* Sidebar perfil oscuro */
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

            /* Botones flotantes en modo noche */
            body.modo-noche .modo-noche-flotante {
                background-color: #111827;
                color: #ffd54a;
                border: 1px solid #374151;
                box-shadow: 0 4px 10px rgba(0,0,0,0.6);
            }
            body.modo-noche .btn-cita-flotante {
                background: linear-gradient(135deg, #374151 0%, #4b5563 100%);
                box-shadow: 0 10px 25px rgba(0,0,0,0.6);
            }
        </style>
    </head>
    <body>

        <!-- Barra de Navegación -->
        <nav class="navbar">
            <div class="logo-container">
                <a href="${pageContext.request.contextPath}/index.jsp">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo" />
                </a>
            </div>
            <div class="hamburger" id="hamburger" aria-label="Menú">
                <span></span><span></span><span></span>
            </div>
            <div class="nav-links" id="nav-links">
                <div class="center-links">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                    <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp"  id="link-contacto">Contacto</a>
                </div>
                <div class="buttons">
                    <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
                </div>
            </div>
        </nav>

        <!-- Hero Section -->
        <section class="hero-servicios">
            <div class="hero-content">
                <h1>Nuestros Servicios</h1>
                <p>Cuidado integral con tecnología de vanguardia</p>
            </div>
        </section>

        <section class="servicios-grid">
            <!-- Peluquería -->
            <div class="servicio-card">
                <div class="servicio-icon">
                    <img src="${pageContext.request.contextPath}/Recursos/ImgPeluqueria 1.svg" alt="Peluquería">
                </div>
                <h3>Peluquería Canina</h3>
                <ul>
                    <li>Corte de raza específica</li>
                    <li>Baño terapéutico</li>
                    <li>Limpieza dental</li>
                </ul>
                <a href="#contacto" class="btn-servicio">Más información</a>
            </div>
            <!-- Baños -->
            <div class="servicio-card">
                <div class="servicio-icon">
                    <img src="${pageContext.request.contextPath}/Recursos/Higiene.svg" alt="Baños">
                </div>
                <h3>Spa & Bienestar</h3>
                <ul>
                    <li>Baño deshedding</li>
                    <li>Hidroterapia</li>
                    <li>Aromaterapia</li>
                </ul>
                <a href="#contacto" class="btn-servicio">Más información</a>
            </div>

            <div class="servicio-card">
                <div class="servicio-icon">
                    <img src="${pageContext.request.contextPath}/Recursos/ImgJuguetes 1.svg" alt="Juegos">
                </div>
                <h3>Área Recreativa</h3>
                <ul>
                    <li>Socialización controlada</li>
                    <li>Juegos interactivos</li>
                    <li>Entrenamiento básico</li>
                </ul>
                <a href="#contacto" class="btn-servicio">Más información</a>
            </div>

            <div class="servicio-card">
                <div class="servicio-icon">
                    <img src="${pageContext.request.contextPath}/Recursos/Vacunacion.svg" alt="Médico">
                </div>
                <h3>Chequeo Completo</h3>
                <ul>
                    <li>Examen físico general</li>
                    <li>Pruebas de laboratorio</li>
                    <li>Plan preventivo</li>
                </ul>
                <a href="#contacto" class="btn-servicio">Más información</a>
            </div>
        </section>

        <section class="cta-section">
            <div class="cta-content">
                <h2>¿Listo para cuidar de tu mascota?</h2>
                <p>Nuestro equipo está preparado para brindarle la mejor atención</p>
            </div>
        </section>

        <footer class="footer-servicios">
            <p>© 2025 Veterinaria Santa Cruz - Todos los derechos reservados</p>
        </footer>

        <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
            <h2 id="perfilTitle">Mi Perfil</h2>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
            <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
            <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet">Citas agendadas</a>
            <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
        </div>

        <div id="sidebarOverlay"></div>

        <!-- Botones flotantes: Modo noche + Crear cita -->
        <div class="flotante-cita">
            <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
            <a href="${pageContext.request.contextPath}/UsuarioCitasServlet" class="btn-cita-flotante" title="Crear cita">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
                </svg>
                Crear cita
            </a>
        </div>

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

            // Marcar Servicios como activo
            window.addEventListener('DOMContentLoaded', () => {
                document.getElementById('link-servicios').classList.add('active-link');
            });
        </script>

        <!-- JS modo noche (misma lógica que usas en las otras páginas) -->
        <script src="${pageContext.request.contextPath}/Js/ModoNocheIndex.js"></script>

    </body>
</html>
