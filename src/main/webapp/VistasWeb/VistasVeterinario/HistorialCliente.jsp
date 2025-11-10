<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  <!-- ✅ ESTA FALTA -->

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Historial Médico - Veterinaria Santa Cruz</title>
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
        }

        main {
            margin-left: 270px;
            padding: 40px;
            width: calc(100% - 270px);
            min-height: 100vh;
            transition: 0.3s ease;
        }

        h1 {
            text-align: center;
            font-size: 1.8rem;
            color: var(--primary-color);
            margin-bottom: 25px;
        }

        /* === FORMULARIO DE BÚSQUEDA === */
        .search-card {
            background: var(--sidebar-color);
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            max-width: 700px;
            margin: 0 auto 30px;
        }

        .search-label {
            font-weight: 600;
            margin-bottom: 10px;
            display: block;
            font-size: 1.05rem;
        }

        .search-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .search-input {
            flex: 1;
            padding: 12px 15px;
            border-radius: 10px;
            border: 2px solid var(--primary-color);
            font-size: 1rem;
            outline: none;
            background-color: var(--body-color);
            color: var(--text-color);
        }

        .search-btn {
            background-color: var(--primary-color);
            color: #fff;
            padding: 12px 20px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 6px;
            font-weight: 600;
            transition: 0.3s;
        }

        .search-btn:hover {
            background-color: var(--primary-color-light);
        }

        /* === MENSAJE === */
        .mensaje {
            text-align: center;
            font-weight: 600;
            font-size: 1.05rem;
            margin-bottom: 25px;
        }

        .mensaje.ok { color: #4CAF50; }
        .mensaje.warn { color: #FFC107; }
        .mensaje.error { color: #E53935; }

        /* === TABLA === */
        .table-container {
            background: var(--sidebar-color);
            border-radius: 15px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            padding: 20px;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: center;
        }

        th {
            background-color: var(--primary-color);
            color: white;
            font-weight: 600;
            padding: 12px;
        }

        td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }

        tr:hover {
            background-color: var(--hover-color);
        }

        /* === MODAL === */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            inset: 0;
            background-color: rgba(0,0,0,0.6);
            backdrop-filter: blur(3px);
        }

        .modal-content {
            background-color: var(--sidebar-color);
            color: var(--text-color);
            width: 60%;
            max-width: 700px;
            margin: 5% auto;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 0 20px rgba(0,0,0,0.4);
            position: relative;
        }

        .modal h2 {
            text-align: center;
            margin-bottom: 20px;
            color: var(--primary-color);
        }

        .modal .close {
            position: absolute;
            top: 15px;
            right: 25px;
            font-size: 28px;
            color: var(--primary-color);
            cursor: pointer;
        }

        @media (max-width: 900px) {
            main { margin-left: 0; width: 100%; padding: 20px; }
            .modal-content { width: 90%; padding: 25px; }
        }
    </style>
</head>
<body>

<!-- SIDEBAR -->
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
            <li class="nav-link active">
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

<!-- CONTENIDO PRINCIPAL -->
<main>
    <h1><i class='bx bx-book'></i> Historial Médico del Cliente</h1>

    <!-- FORMULARIO DE BÚSQUEDA -->
    <div class="search-card">
        <form action="${pageContext.request.contextPath}/BuscarHistorialServlet" method="POST">
            <label for="dniInput" class="search-label">Ingrese el DNI del cliente:</label>
            <div class="search-group">
                <input type="text" id="dniInput" name="dniBusqueda" class="search-input"
                       placeholder="Ej: 12345678" value="${dniBusqueda}" required>
                <button type="submit" class="search-btn">
                    <i class='bx bx-search'></i> Buscar
                </button>
            </div>
        </form>
    </div>

    <!-- MENSAJE DE BÚSQUEDA -->
    <c:if test="${not empty mensajeBusqueda}">
        <div class="mensaje
            <c:choose>
                <c:when test="${fn:contains(mensajeBusqueda, 'Resultados')}">ok</c:when>
                <c:when test="${fn:contains(mensajeBusqueda, 'No se encontraron')}">warn</c:when>
                <c:otherwise>error</c:otherwise>
            </c:choose>
        ">
            ${mensajeBusqueda}
        </div>
    </c:if>

    <!-- TABLA DE RESULTADOS -->
    <c:if test="${not empty listaTratamientos}">
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Diagnóstico</th>
                        <th>Tratamiento</th>
                        <th>Fecha</th>
                        <th>DNI Cliente</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="t" items="${listaTratamientos}">
                        <tr>
                            <td>${t.diagnostico}</td>
                            <td>${t.tratamiento}</td>
                            <td>
                                <fmt:formatDate value="${t.fechaRegistro}" pattern="dd/MM/yyyy" var="fechaFmt"/>
                                ${fechaFmt}
                            </td>
                            <td>${t.dniCliente}</td>
                            <td>
                                <button class="search-btn"
                                    onclick="verDetalle(
                                        '${fn:escapeXml(t.diagnostico)}',
                                        '${fn:escapeXml(t.tratamiento)}',
                                        '${fechaFmt}',
                                        '${t.dniCliente}'
                                    )">
                                    <i class='bx bx-show'></i> Ver
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</main>

<!-- MODAL DETALLE -->
<div id="modalDetalle" class="modal">
    <div class="modal-content">
        <span class="close" onclick="cerrarModal()">&times;</span>
        <h2><i class='bx bx-detail'></i> Detalle del Tratamiento</h2>
        <p><strong>Diagnóstico:</strong> <span id="detalleDiag"></span></p>
        <p><strong>Tratamiento:</strong> <span id="detalleTrat"></span></p>
        <p><strong>Fecha:</strong> <span id="detalleFecha"></span></p>
        <p><strong>DNI Cliente:</strong> <span id="detalleDni"></span></p>
    </div>
</div>

<button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>
<script src="${pageContext.request.contextPath}/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

<script>
    function verDetalle(diag, trat, fecha, dni) {
        document.getElementById("detalleDiag").innerText = diag;
        document.getElementById("detalleTrat").innerText = trat;
        document.getElementById("detalleFecha").innerText = fecha;
        document.getElementById("detalleDni").innerText = dni;
        document.getElementById("modalDetalle").style.display = "block";
    }

    function cerrarModal() {
        document.getElementById("modalDetalle").style.display = "none";
    }

    window.onclick = function(e) {
        if (e.target.classList.contains('modal')) cerrarModal();
    };
</script>
</body>
</html>
