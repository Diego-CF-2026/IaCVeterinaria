<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>VeterinariaSantaCruz</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="../../css/ModoNoche-Sidebar.css">  
        <link rel="stylesheet" href="../../css/RecepDash.css">
    </head>
    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Recepcionista</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <div class="menu-bar">
                <ul class="menu-links">
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasRecep/RecepDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="${pageContext.request.contextPath}/CitaServlet">
                            <i class='bx bx-calendar-check icon'></i><span class="text">Citas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="${pageContext.request.contextPath}/ProductoRecepServlet">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span></a>
                    </li> 
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/LogoutServlet">
                            <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- CONTENIDO PRINCIPAL (GENERAL) -->
        <main>
            <section class="contenido-general-recep">

                <!-- BARRA SUPERIOR: TÍTULO + ACCIONES RÁPIDAS -->
                <div class="recep-header">
                    <div class="recep-header-text">
                        <h1>Panel general</h1>
                        <p class="recep-subtitulo">
                            Buen día, recuerda que desde aquí puedes gestionar tus tareas principales.
                        </p>
                    </div>

                    <div class="recep-actions">
                        <!-- Botón: Citas de hoy -->
                        <button type="button"
                                class="recep-btn recep-btn-sec"
                                onclick="location.href = '${pageContext.request.contextPath}/CitaServlet'">
                            <i class='bx bx-calendar-event'></i>
                            <span>Citas de hoy</span>
                        </button>

                        <!-- Botón: Registrar venta -->
                        <button type="button"
                                class="recep-btn recep-btn-sec"
                                onclick="location.href = '${pageContext.request.contextPath}/ProductoRecepServlet'">
                            <i class='bx bx-package'></i>
                            <span>Productos</span>
                        </button>
                    </div>
                </div>
                <!-- TARJETA HORARIO -->
                <div class="card-aviso-horario">
                    <div class="icono-horario">
                        <i class='bx bx-time-five'></i>
                    </div>
                    <div class="texto-horario">
                        <h2>Horario de entrada</h2>
                        <p>
                            Recuerda que la hora de entrada del recepcionista es 
                            <strong>8:00 a.m.</strong>.
                        </p>
                        <small>La puntualidad es importante para brindar una buena atención a los clientes.</small>
                    </div>
                </div>

                <!-- ==== MENSAJES MOTIVACIONALES ==== -->
                <section class="mensajes-motivacionales">
                    <h2>Mensajes para tu día</h2>

                    <div class="mensajes-grid">

                        <!-- Solo texto -->
                        <article class="mensaje-card">
                            <p>
                                “Cada llegada puntual es una muestra de respeto hacia tu equipo 
                                y hacia los clientes. ¡Sigue así!”
                            </p>
                        </article>

                        <!-- Imagen + texto -->
                        <article class="mensaje-card mensaje-imagen">
                            <img src="<%= request.getContextPath()%>/Recursos/motivacion1.webp" 
                                 alt="Equipo de trabajo sonriente">
                            <div>
                                <h3>Atención con cariño</h3>
                                <p>
                                    Una sonrisa al recibir a cada cliente puede cambiarle el día 
                                    a una persona… y también a su mascota 🐾.
                                </p>
                            </div>
                        </article>

                        <!-- Icono + texto -->
                        <article class="mensaje-card mensaje-icono">
                            <i class='bx bx-smile'></i>
                            <p>
                                “Los pequeños detalles marcan la diferencia: saludar, escuchar 
                                y ayudar hacen de la veterinaria un lugar especial.”
                            </p>
                        </article>

                    </div>
                </section>
                <!-- ===== GALERÍA DE MASCOTAS FELICES ===== -->
                <section class="galeria-mascotas">


                    <div class="carousel-mascotas">
                        <button class="carousel-btn prev" type="button">&#10094;</button>

                        <div class="carousel-slides">
                            <div class="slide-mascota active">
                                <img src="<%= request.getContextPath()%>/Recursos/mascota1.webp" alt="Perro feliz">
                            </div>
                            <div class="slide-mascota">
                                <img src="<%= request.getContextPath()%>/Recursos/mascota2.jpg" alt="Gato relajado">
                            </div>
                            <div class="slide-mascota">
                                <img src="<%= request.getContextPath()%>/Recursos/mascota3.jpg" alt="Perro y dueño en recepción">
                            </div>
                            <!-- Puedes seguir agregando más imágenes -->
                        </div>

                        <button class="carousel-btn next" type="button">&#10095;</button>
                    </div>
                </section>


            </section>
        </main>


        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">?</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

        <script>
                                    document.addEventListener("DOMContentLoaded", function () {
                                        const slides = document.querySelectorAll(".slide-mascota");
                                        const prevBtn = document.querySelector(".carousel-btn.prev");
                                        const nextBtn = document.querySelector(".carousel-btn.next");

                                        if (!slides.length || !prevBtn || !nextBtn)
                                            return;

                                        let indice = 0;

                                        function mostrarSlide(i) {
                                            slides.forEach(s => s.classList.remove("active"));
                                            slides[i].classList.add("active");
                                        }

                                        function siguiente() {
                                            indice = (indice + 1) % slides.length;
                                            mostrarSlide(indice);
                                        }

                                        function anterior() {
                                            indice = (indice - 1 + slides.length) % slides.length;
                                            mostrarSlide(indice);
                                        }

                                        nextBtn.addEventListener("click", siguiente);
                                        prevBtn.addEventListener("click", anterior);

                                        // Cambio automático cada 5 segundos
                                        setInterval(siguiente, 5000);
                                    });
        </script>

    </body>
</html>
