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

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            display: flex;
            background-color: var(--body-color);
            color: var(--text-color);
            margin: 0;
            overflow-x: hidden;
            transition: background-color 0.3s, color 0.3s;
        }

        main {
            margin-left: 270px;
            padding: 40px;
            width: calc(100% - 270px);
            min-height: 100vh;
            transition: 0.3s ease;
        }

        h1 {
            font-size: 2rem;
            color: var(--primary-color);
            text-align: center;
            margin-bottom: 20px;
        }

        /* ======= TARJETAS ======= */
        .cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(230px, 1fr));
            gap: 20px;
            margin-bottom: 40px;
        }

        .card {
            background: var(--sidebar-color);
            padding: 20px;
            border-radius: 15px;
            box-shadow: 0 3px 10px rgba(0,0,0,0.2);
            transition: transform 0.3s ease, background-color 0.3s ease;
        }

        body.modo-claro .card {
            background: #fff;
            color: #333;
        }

        .card:hover {
            transform: translateY(-5px);
        }

        .card i {
            font-size: 2.5rem;
            color: var(--primary-color);
            margin-bottom: 10px;
        }

        .card h3 {
            font-size: 1.3rem;
            margin-bottom: 5px;
        }

        .card p {
            font-size: 0.95rem;
            opacity: 0.8;
        }

        /* ======= MINI JUEGO ======= */
        .game-card {
            background: var(--sidebar-color);
            border-radius: 15px;
            padding: 25px;
            text-align: center;
            box-shadow: 0 3px 10px rgba(0,0,0,0.3);
        }

        body.modo-claro .game-card {
            background: #fff;
            color: #333;
        }

        #gameContainer {
            position: relative;
            width: 100%;
            height: 200px;
            background: linear-gradient(180deg, #222, #111);
            border-radius: 10px;
            overflow: hidden;
            margin-top: 10px;
        }

        body.modo-claro #gameContainer {
            background: linear-gradient(180deg, #e3e3e3, #cfcfcf);
        }

        #cat {
            width: 40px;
            height: 40px;
            background: #ffce54;
            position: absolute;
            bottom: 0;
            left: 40px;
            border-radius: 10px;
        }

        #obstacle {
            width: 30px;
            height: 30px;
            background: #e74c3c;
            position: absolute;
            bottom: 0;
            right: -40px;
            border-radius: 5px;
        }

        /* ======= BOTÓN MODO NOCHE ======= */
        .modo-noche-flotante {
            position: fixed;
            bottom: 20px;
            right: 20px;
            border: none;
            background: var(--primary-color);
            color: #fff;
            border-radius: 50%;
            font-size: 1.5rem;
            width: 45px;
            height: 45px;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            transition: transform 0.3s;
        }

        .modo-noche-flotante:hover {
            transform: scale(1.1);
        }

        @media (max-width: 900px) {
            main { margin-left: 0; width: 100%; padding: 20px; }
        }
    </style>
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
                <li class="nav-link active">
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
