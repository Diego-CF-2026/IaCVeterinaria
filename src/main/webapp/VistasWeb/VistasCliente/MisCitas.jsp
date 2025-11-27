<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${empty sessionScope.idClienteSesion}">
    <c:redirect url="${pageContext.request.contextPath}/index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mis Citas Agendadas</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Cuenta.css" /> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <style>
            /* ===== CONTENEDOR GENERAL (MODO CLARO) ===== */
            body {
                background-color: #f5f7fb;
                font-family: 'Poppins', sans-serif;
            }
            .perfil-info {
                width: 100%;
                padding-top: 40px;
                padding-left: 50px;
                padding-right: 50px;
            }

            .perfil-title {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 25px;
            }

            .perfil-title h2 {
                font-size: 1.9rem;
                letter-spacing: 0.08em;
                font-weight: 700;
            }

            .paw-img {
                width: 30px;
                height: 30px;
            }

            /* ===== WRAPPER TABLA ===== */
            .tabla-citas-wrapper {
                overflow-x: auto;
                width: 100%;
            }

            /* ===== TABLA DE CITAS (MODO CLARO – TU DISEÑO ORIGINAL) ===== */
            .citas-listado {
                width: 100%;
                min-width: 650px;
                border-collapse: collapse;
                font-size: 0.9rem;
            }

            .citas-listado thead tr {
                border-bottom: 2px solid #d0dbe5;
            }

            .citas-listado th,
            .citas-listado td {
                border: none;
                padding: 10px 12px;
                text-align: left;
                white-space: nowrap;
            }

            .citas-listado th {
                background-color: transparent;
                color: #6c7a89;
                font-weight: 600;
                text-transform: uppercase;
                font-size: 0.75rem;
                letter-spacing: 0.08em;
            }

            /* Columnas organizadas */
            .col-fecha   {
                width: 14%;
            }
            .col-hora    {
                width: 10%;
            }
            .col-motivo  {
                width: 20%;
            }
            .col-vete    {
                width: 22%;
            }
            .col-precio  {
                width: 10%;
                text-align: right;
            }
            .col-estado  {
                width: 12%;
            }
            .col-accion  {
                width: 12%;
                text-align: center;
            }

            .citas-listado td.col-precio {
                text-align: right;
            }
            .citas-listado td.col-accion {
                text-align: center;
            }

            /* Filas (modo claro) */
            .citas-listado tbody tr {
                border-bottom: 1px solid #e4ecf2;
                transition: background-color 0.2s ease, transform 0.15s ease;
            }

            .citas-listado tbody tr:hover {
                background-color: #e0f0ff;
                transform: translateY(-1px);
            }

            /* ===== ESTADO COMO ETIQUETA (PILL) ===== */
            .estado-Pendiente,
            .estado-Confirmada,
            .estado-Cancelada {
                display: inline-block;
                padding: 4px 10px;
                border-radius: 999px;
                font-size: 0.75rem;
                font-weight: 600;
            }

            .estado-Pendiente   {
                background-color: #fff3cd;
                color: #856404;
            }
            .estado-Confirmada  {
                background-color: #d4edda;
                color: #155724;
            }
            .estado-Cancelada   {
                background-color: #f8d7da;
                color: #721c24;
            }

            /* ===== BOTÓN CANCELAR ===== */
            .btn-cancelar {
                background-color: #dc3545;
                color: white;
                border: none;
                padding: 5px 12px;
                border-radius: 20px;
                text-decoration: none;
                font-size: 0.8rem;
                font-weight: 500;
                display: inline-block;
                transition: background-color 0.25s ease,
                    transform 0.1s ease,
                    box-shadow 0.2s ease;
            }
            .btn-cancelar:hover {
                background-color: #c82333;
                transform: translateY(-1px);
                box-shadow: 0 4px 10px rgba(220,53,69,0.4);
            }

            /* ===== ALERTA SIN CITAS / MENSAJES ===== */
            .alerta-citas {
                padding: 15px 18px;
                background-color: #fff3cd;
                color: #856404;
                border: 1px solid #ffeeba;
                border-radius: 12px;
                margin-top: 10px;
                font-size: 0.9rem;
            }

            /* Botón flotante modo noche */
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
                transition: background-color 0.3s ease,
                    transform 0.1s ease,
                    box-shadow 0.3s ease;
            }

            .modo-noche-flotante:hover {
                background-color: #000;
                transform: translateY(-1px);
                box-shadow: 0 6px 14px rgba(0,0,0,0.45);
            }

            /* ===== RESPONSIVE ===== */
            @media (max-width: 768px) {
                .perfil-container {
                    margin: 90px 16px 40px;
                    padding: 20px;
                    border-radius: 22px;
                }

                .perfil-title h2 {
                    font-size: 1.4rem;
                }

                .citas-listado {
                    min-width: 100%;
                    font-size: 0.82rem;
                }

                .modo-noche-flotante {
                    bottom: 15px;
                    right: 15px;
                    width: 40px;
                    height: 40px;
                    font-size: 18px;
                }
            }

            /* ========= MODO NOCHE PARA ESTA PÁGINA ========= */

            /* Fondo general en modo noche */
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

            /* Botón "Ver perfil" */
            body.modo-noche .btn.perfil {
                background-color: #000 !important;
                color: #fff !important;
            }

            /* Contenedor de citas en modo noche (la tarjeta) */
            body.modo-noche .citas-agendadas {
                background-color: #202124;
                box-shadow: 0 12px 32px rgba(0,0,0,0.7);
            }

            /* Caja interna (perfil-info) también oscura */
            body.modo-noche .perfil-info {
                background-color: #202124;  /* tapa el fondo claro de Cuenta.css */
                color: #f9fafb;
            }

            /* Título */
            body.modo-noche .perfil-title h2 {
                color: #f9fafb;
            }


            /* ===== TABLA DE CITAS EN MODO NOCHE – ESTILO PANEL ADMIN ===== */

            body.modo-noche .citas-listado {
                background-color: #242526;
                border-radius: 8px;
                overflow: hidden;
                color: #f5f5f5;
                border: 1px solid #3a3b3c;
            }

            body.modo-noche .citas-listado thead {
                background-color: #333333;
            }

            body.modo-noche .citas-listado thead tr {
                border-bottom: 1px solid #444;
            }

            body.modo-noche .citas-listado th {
                color: #f5f5f5;
            }

            body.modo-noche .citas-listado tbody tr {
                background-color: #18191A;
                border-bottom: 1px solid #303030;
                transform: translateY(0); /* anulamos el “salto” para que se parezca al admin */
            }

            body.modo-noche .citas-listado tbody tr:nth-child(even) {
                background-color: #202124;
            }

            body.modo-noche .citas-listado tbody tr:hover {
                background-color: #2a2a2a;
            }

            body.modo-noche .citas-listado td {
                color: #f5f5f5;
            }

            /* Estados en modo noche (los mantenemos, solo se ajustan sobre fondo gris) */
            body.modo-noche .estado-Pendiente {
                background-color: #5c4b1f;
                color: #fef3c7;
            }
            body.modo-noche .estado-Confirmada {
                background-color: #14532d;
                color: #bbf7d0;
            }
            body.modo-noche .estado-Cancelada {
                background-color: #7f1d1d;
                color: #fecaca;
            }

            /* Botón cancelar en modo noche */
            body.modo-noche .btn-cancelar {
                background-color: #b91c1c;
                color: #fff;
            }

            body.modo-noche .btn-cancelar:hover {
                background-color: #ef4444;
                box-shadow: 0 4px 10px rgba(248,113,113,0.4);
            }

            /* Alertas en modo noche */
            body.modo-noche .alerta-citas {
                background-color: #1f2937 !important;
                color: #fef3c7 !important;
                border-color: #4b5563 !important;
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

            /* Botón flotante en modo noche (también oscuro) */
            body.modo-noche .modo-noche-flotante {
                background-color: #111827;      /* círculo oscuro */
                color: #ffd54a;                 /* el sol se ve dorado */
                border: 1px solid #374151;
                box-shadow: 0 4px 10px rgba(0,0,0,0.6);
            }

        </style>


    </head>
    <body>

        <nav class="navbar">
            <div class="logo-container">
                <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo">
            </div>
            <div class="nav-links">
                <div class="center-links">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                    <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
                </div>
                <div class="buttons">
                    <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver perfil</a>
                </div>
            </div>
            <div class="hamburger" id="hamburger-menu">
                <span></span><span></span><span></span>
            </div>
        </nav>

        <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
            <h2 id="perfilTitle">Mi Perfil</h2>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
            <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
            <a href="${pageContext.request.contextPath}/CitaServlet?accion=listar" class="active-link">Citas agendadas</a> 
            <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
        </div>
        <div id="sidebarOverlay"></div>

        <section class="perfil-container citas-agendadas">
            <div class="perfil-info">
                <div class="perfil-title">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Patita" class="paw-img">
                    <h2>CITAS AGENDADAS</h2>
                </div>

                <%-- Muestra mensajes de la Sesión --%>
                <c:if test="${not empty sessionScope.mensaje}">
                    <div class="alerta-citas" style="background-color: ${sessionScope.tipoMensaje eq 'exito' ? '#d4edda' : sessionScope.tipoMensaje eq 'error' ? '#f8d7da' : '#fff3cd'}; color: ${sessionScope.tipoMensaje eq 'exito' ? '#155724' : sessionScope.tipoMensaje eq 'error' ? '#721c24' : '#856404'}; border-color: ${sessionScope.tipoMensaje eq 'exito' ? '#c3e6cb' : sessionScope.tipoMensaje eq 'error' ? '#f5c6cb' : '#ffeeba'};">
                        <p><strong><c:out value="${sessionScope.tipoMensaje}" />:</strong> <c:out value="${sessionScope.mensaje}" /></p>
                    </div>
                    <c:remove var="mensaje" scope="session"/>
                    <c:remove var="tipoMensaje" scope="session"/>
                </c:if>

                <c:choose>
                    <c:when test="${empty misCitas}"> 
                        <div class="alerta-citas">
                            <p><strong>¡Hola!</strong> Actualmente no tienes citas programadas o pendientes.</p>
                            <p>Si deseas agendar una, por favor contáctanos.</p>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="tabla-citas-wrapper">
                            <table class="citas-listado">
                                <thead>
                                    <tr>
                                        <th class="col-fecha">Fecha</th>
                                        <th class="col-hora">Hora</th>
                                        <th class="col-motivo">Motivo</th>
                                        <th class="col-vete">Veterinario</th>
                                        <th class="col-precio">Precio</th>
                                        <th class="col-estado">Estado</th>
                                        <th class="col-accion">Acción</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="cita" items="${misCitas}">
                                        <tr>
                                            <td class="col-fecha">
                                                <c:out value="${cita.fecha}" />
                                            </td>
                                            <td class="col-hora">
                                                <c:out value="${fn:substring(cita.hora, 0, 5)}" />
                                            </td>
                                            <td class="col-motivo">
                                                <c:out value="${cita.motivo}" />
                                            </td>
                                            <td class="col-vete">
                                                Dr/a. <c:out value="${cita.nombreVeterinario} ${cita.apellidoVeterinario}" />
                                            </td>
                                            <td class="col-precio">
                                                <c:choose>
                                                    <c:when test="${cita.precio > 0}">
                                                        S/. <c:out value="${cita.precio}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color: #6c757d;">Consultar</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="col-estado">
                                                <span class="estado-<c:out value='${cita.estadoNombre}' />">
                                                    <c:out value="${cita.estadoNombre}" />
                                                </span>
                                            </td>
                                            <td class="col-accion">
                                                <c:choose>
                                                    <c:when test="${cita.estadoNombre eq 'Pendiente'}">
                                                        <a href="${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=${cita.idCita}" 
                                                           class="btn-cancelar" 
                                                           onclick="return confirm('¿Estás seguro de que deseas cancelar esta cita?');">
                                                            Cancelar
                                                        </a>
                                                    </c:when>
                                                    <c:when test="${cita.estadoNombre eq 'Confirmada'}">
                                                        <span style="color:#6c757d; font-size: 0.8rem;">No cancelable</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color:#6c757d;">-</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>
        </section>

        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script>
                                                               document.getElementById('hamburger-menu').onclick = function () {
                                                                   document.querySelector('.nav-links').classList.toggle('active');
                                                               };

                                                               const verPerfilBtn = document.getElementById('verPerfilBtn');
                                                               const sidebarPerfil = document.getElementById('sidebarPerfil');
                                                               const sidebarOverlay = document.getElementById('sidebarOverlay');

                                                               verPerfilBtn.addEventListener('click', () => {
                                                                   sidebarPerfil.classList.add('active');
                                                                   sidebarOverlay.classList.add('active');
                                                               });

                                                               sidebarOverlay.addEventListener('click', () => {
                                                                   sidebarPerfil.classList.remove('active');
                                                                   sidebarOverlay.classList.remove('active');
                                                               });
        </script>
    </body>
</html>
