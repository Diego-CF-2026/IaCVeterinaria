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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/HistorialClienteDiseno.css">
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
