<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel del Veterinario - Veterinaria Santa Cruz</title>
    <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/VeterinarioMenu.css">
    
</head>

<body>
    <!-- ===== SIDEBAR ===== -->
    <nav class="sidebar">
        <header>
            <div class="image-text">
                <span class="image">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo">
                </span>
                <div class="header-text">
                    <span class="name">Veterinario</span>
                    <span class="profession">Veterinaria Santa Cruz</span>
                </div>
            </div>
        </header>

        <div class="menu-bar">
            <ul class="menu-links">
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasVeterinario/VeterinarioDash.jsp">
                        <i class='bx bx-home-alt icon'></i><span class="text">General</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/VeterinarioCitasServlet?accion=listar">
                        <i class='bx bx-calendar-check icon'></i><span class="text">Citas</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/BuscarHistorialServlet">
                        <i class='bx bx-book icon'></i><span class="text">Ver Historial Médico</span>
                    </a>
                </li>
                <li class="nav-link">
                    <a href="${pageContext.request.contextPath}/LogoutServlet">
                        <i class='bx bx-log-out icon'></i><span class="text">Salir</span>
                    </a>
                </li>
            </ul>
        </div>
    </nav>

    <!-- ===== CONTENIDO PRINCIPAL ===== -->
    <main>
        <h1><i class='bx bx-home-heart'></i> Bienvenido al Panel del Veterinario</h1>

        <div class="cards">
            <div class="card">
                <i class='bx bx-calendar-check'></i>
                <h3>Próximas Citas</h3>
                <p>Consulta tus citas programadas para hoy.</p>
            </div>

            <div class="card">
                <i class='bx bx-plus-medical'></i>
                <h3>Tratamientos</h3>
                <p>Gestiona los tratamientos médicos de los pacientes.</p>
            </div>

            <div class="card">
                <i class='bx bx-book-heart'></i>
                <h3>Historial Médico</h3>
                <p>Revisa el historial de tus clientes y mascotas atendidas.</p>
            </div>

            <div class="card game-card">
                <i class='bx bx-heart'></i>
                <h3>Mini Juego 🐱</h3>
                <p>Presiona espacio para saltar y esquiva los obstáculos</p>
                <div id="gameContainer">
                    <div id="cat"></div>
                    <div id="obstacle"></div>
                </div>
            </div>
        </div>
    </main>

    <!-- ===== BOTÓN MODO NOCHE ===== -->
    <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar modo">
        <i class='bx bx-moon'></i>
    </button>

    <script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
</body>
</html>
