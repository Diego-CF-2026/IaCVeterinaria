<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>VeterinariaSantaCruz</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AdminDash.css"> 
    </head>
    <body>
        <nav class="sidebar">
            <header>
                <div class="image-text">
                    <span class="image">
                        <img id="logoAdmin" src="<%= request.getContextPath()%>/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo">
                    </span>
                    <div class="header-text">
                        <span class="name">Administrador</span>
                        <span class="profession">Veterinaria Santa Cruz</span>
                    </div>
                </div>
            </header>

            <div class="menu-bar">
                <ul class="menu-links">
                    <li class="nav-link ">
                        <a href="<%= request.getContextPath()%>/VistasWeb/VistasAdmin/AdminDash.jsp">
                            <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                        </a>
                    </li>
                    <li class="nav-link">
                            <a href="<%= request.getContextPath()%>/ReporteGananciasServlet">
                            <i class='bx bx-bar-chart-alt-2 icon'></i><span class="text">Ganancias de Citas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/carritoservletAdmin">
                            <i class='bx bx-check-circle icon'></i><span class="text">Productos Entregados</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/AdminEmpleadoServlet"><i class='bx bx-group icon'></i><span class="text">Veterinarios</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/AdminRecepServlet?accion=listar">
                            <i class='bx bx-user-check icon'></i> <span class="text">Recepcionistas</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProductoServlet?accion=listar&idProveedor=1">
                            <i class='bx bx-package icon'></i><span class="text">Productos</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/ProveedorServlet?accion=listar">
                            <i class='bx bx-store icon'></i><span class="text">Proveedores</span>
                        </a>
                    </li>
                    <li class="nav-link">
                        <a href="#"><i class='bx bx-cog icon'></i><span class="text">Ajustes</span></a>
                    </li>
                    <li class="nav-link">
                        <a href="<%= request.getContextPath()%>/LogoutServlet">
                            <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>
       <!-- CONTENIDO PRINCIPAL -->
        <main class="admin-main">
            <section class="hero-dashboard">
                <div class="hero-text">
                    <h1>¡Bienvenido, Administrador!</h1>
                    <p>
                        Desde este panel podrás controlar las citas, veterinarios,
                        productos y todo lo necesario para la gestión de la
                        Veterinaria Santa Cruz.
                    </p>
                </div>

                <!-- CARRUSEL DE MASCOTAS FELICES -->
                <div class="hero-carousel">
                    <div class="carousel-track">
                        <div class="carousel-slide active">
                            <img src="<%= request.getContextPath()%>/Recursos/clinica1.jpg" alt="">
                        </div>
                        <div class="carousel-slide">
                            <img src="<%= request.getContextPath()%>/Recursos/equipovet.jpg" alt="">
                        </div>
                        <div class="carousel-slide">
                            <img src="<%= request.getContextPath()%>/Recursos/horarioatencion.png" alt="">
                        </div>
                    </div>

                    <button class="carousel-btn prev">&#10094;</button>
                    <button class="carousel-btn next">&#10095;</button>

                    <div class="carousel-dots">
                        <span class="dot active" data-index="0"></span>
                        <span class="dot" data-index="1"></span>
                        <span class="dot" data-index="2"></span>
                    </div>
                </div>
            </section>

            <!-- ENLACES RÁPIDOS (AHORA DENTRO DEL MAIN) -->
            <section class="quick-links">
                <h2>Gestión rápida</h2>

                <div class="quick-links-grid">
                    <!-- Ganancias de citas -->
                    <a href="<%= request.getContextPath()%>/ReporteGananciasServlet" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/gananciastotales.png" alt="Veterinarios">
                        </div>
                        <div class="quick-card-content">
                            <h3>Ganancias totales</h3>
                            <p>Gestiona las ganancias de la empresa.</p>
                        </div>
                    </a>
                        <!-- Productos entregados -->
                    <a href="<%= request.getContextPath()%>/carritoservletAdmin" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/productosentregados.webp" alt="Veterinarios">
                        </div>
                        <div class="quick-card-content">
                            <h3>Productos entregados</h3>
                            <p>Gestiona los productos entregados de la empresa.</p>
                        </div>
                    </a>
                    <!-- VETERINARIOS -->
                    <a href="<%= request.getContextPath()%>/AdminEmpleadoServlet" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/mascota1.webp" alt="Veterinarios">
                        </div>
                        <div class="quick-card-content">
                            <h3>Veterinarios</h3>
                            <p>Gestiona el equipo de médicos veterinarios.</p>
                        </div>
                    </a>

                    <!-- RECEPCIONISTAS -->
                    <a href="<%= request.getContextPath()%>/AdminRecepServlet?accion=listar" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/recepcionista.webp" alt="Recepcionistas">
                        </div>
                        <div class="quick-card-content">
                            <h3>Recepcionistas</h3>
                            <p>Administra al personal de recepción.</p>
                        </div>
                    </a>

                    <!-- PRODUCTOS -->
                    <a href="<%= request.getContextPath()%>/ProductoServlet?accion=listar&idProveedor=1" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/ProductosVenta/1760984424553_medicamento2.png" alt="Productos">
                        </div>
                        <div class="quick-card-content">
                            <h3>Productos</h3>
                            <p>Controla los productos y suministros.</p>
                        </div>
                    </a>

                    <!-- PROVEEDORES -->
                    <a href="<%= request.getContextPath()%>/ProveedorServlet?accion=listar" class="quick-card">
                        <div class="quick-card-image">
                            <img src="<%= request.getContextPath()%>/Recursos/proveedoresclinica.webp" alt="Proveedores">
                        </div>
                        <div class="quick-card-content">
                            <h3>Proveedores</h3>
                            <p>Revisa y gestiona tus proveedores.</p>
                        </div>
                    </a>
                </div>
            </section>
        </main>
        <!-- BOTÓN MODO NOCHE -->
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">?</button>

        <!-- SCRIPTS -->
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

        <!-- Script del carrusel -->
        <script>
            window.addEventListener("load", function () {
                const carousel = document.querySelector(".hero-carousel");
                if (!carousel)
                    return;

                const track = carousel.querySelector(".carousel-track");
                const slides = Array.from(carousel.querySelectorAll(".carousel-slide"));
                const prevBtn = carousel.querySelector(".carousel-btn.prev");
                const nextBtn = carousel.querySelector(".carousel-btn.next");
                const dots = Array.from(carousel.querySelectorAll(".carousel-dots .dot"));

                if (!track || slides.length === 0)
                    return;

                let index = 0;
                const total = slides.length;
                let autoSlideInterval = null;

                function updateCarousel(newIndex) {
                    index = (newIndex + total) % total;      // 0,1,2... y vuelve
                    const offset = -index * 100;             // -0%, -100%, -200%...
                    track.style.transform = "translateX(" + offset + "%)";

                    // Actualizar puntitos
                    dots.forEach(d => d.classList.remove("active"));
                    if (dots[index]) {
                        dots[index].classList.add("active");
                    }
                }

                function nextSlide() {
                    updateCarousel(index + 1);
                }

                function prevSlide() {
                    updateCarousel(index - 1);
                }

                // Botones
                if (nextBtn) {
                    nextBtn.addEventListener("click", function () {
                        nextSlide();
                        resetAutoSlide();
                    });
                }

                if (prevBtn) {
                    prevBtn.addEventListener("click", function () {
                        prevSlide();
                        resetAutoSlide();
                    });
                }

                // Puntos
                dots.forEach(function (dot) {
                    dot.addEventListener("click", function () {
                        const i = parseInt(dot.getAttribute("data-index"), 10);
                        updateCarousel(i);
                        resetAutoSlide();
                    });
                });

                // Auto-slide cada 4 segundos
                function startAutoSlide() {
                    autoSlideInterval = setInterval(nextSlide, 4000);
                }

                function resetAutoSlide() {
                    clearInterval(autoSlideInterval);
                    startAutoSlide();
                }

                // Estado inicial
                updateCarousel(0);
                startAutoSlide();
            });
        </script>

    </body>
</html>
