<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> <%-- ¡AGREGADO! Necesario para substring(hora) --%>

<%-- 
    **********************************************************
    CORRECCIÓN DE SEGURIDAD (Se pasa de Scriptlet a JSTL)
    Esto es una buena práctica y asegura la validación temprana.
    **********************************************************
--%>
<c:if test="${empty sessionScope.idClienteSesion}">
    <c:redirect url="${pageContext.request.contextPath}/index.jsp"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Citas Agendadas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Cuenta.css" /> 
    <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <style>
        /* Estilos CSS (Se mantienen) */
        .perfil-info { padding-top: 20px; }
        .citas-listado { width: 100%; margin-top: 20px; border-collapse: collapse; }
        .citas-listado th, .citas-listado td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        .citas-listado th { background-color: #f2f2f2; color: #333; font-weight: 600; }
        .estado-Pendiente { color: orange; font-weight: bold; }
        .estado-Confirmada { color: green; font-weight: bold; }
        .estado-Cancelada { color: red; font-weight: bold; }
        .alerta-citas {
            padding: 15px; background-color: #fff3cd; 
            color: #856404; border: 1px solid #ffeeba;
            border-radius: 8px; margin-top: 20px;
        }
        .btn-cancelar {
            background-color: #dc3545; color: white; border: none;
            padding: 5px 10px; border-radius: 5px; text-decoration: none;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    
    <%-- ********************************************************** --%>
    <%-- DIAGNÓSTICO (Quitar si ya funciona) --%>
    <p style="color: blue; font-weight: bold; background-color: #f0f8ff; padding: 10px;">
        DEBUG: ID Sesión: <c:out value="${sessionScope.idClienteSesion}" default="[ERROR DE SESIÓN]"/> | 
        ¿Lista Vacía?: ${empty listaCitas} | 
        Tamaño: ${fn:length(listaCitas)}
    </p>
    <%-- ********************************************************** --%>

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

    <section class="perfil-container">
        
        <div class="perfil-img">
            <img src="${pageContext.request.contextPath}/Recursos/perro.jpeg" alt="Foto de perfil">
        </div>

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
                <c:when test="${empty listaCitas}"> 
                    <div class="alerta-citas">
                        <p><strong>¡Hola!</strong> Actualmente no tienes citas programadas o pendientes.</p>
                        <p>Si deseas agendar una, por favor contáctanos.</p>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <table class="citas-listado">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Fecha</th>
                                <th>Hora</th>
                                <th>Motivo</th>
                                <th>Veterinario</th>
                                <th>Estado</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cita" items="${listaCitas}">
                                <tr>
                                    <td><c:out value="${cita.idCita}" /></td>
                                    <td><c:out value="${cita.fecha}" /></td>
                                    <%-- CORREGIDO: Usando fn:substring para la hora --%>
                                    <td><c:out value="${fn:substring(cita.hora, 0, 5)}" /></td>
                                    <td><c:out value="${cita.motivo}" /></td>
                                    <%-- Verificado: Usa nombreVeterinario y apellidoVeterinario (debe coincidir con tu Modelo Cita) --%>
                                    <td>Dr/a. <c:out value="${cita.nombreVeterinario} ${cita.apellidoVeterinario}" /></td> 
                                    <td>
                                        <span class="estado-<c:out value="${cita.estado}" />">
                                            <c:out value="${cita.estado}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cita.estado eq 'Pendiente'}">
                                                <a href="${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=${cita.idCita}" class="btn-cancelar" onclick="return confirm('¿Estás seguro de que deseas cancelar esta cita?');">
                                                    Cancelar
                                                </a>
                                            </c:when>
                                            <c:when test="${cita.estado eq 'Confirmada'}">
                                                <span style="color:#6c757d; font-size: 0.9rem;">No cancelable</span>
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
                </c:otherwise>
            </c:choose>

        </div>
    </section>

    <script>
        // Lógica JavaScript (Se mantiene)
        document.getElementById('hamburger-menu').onclick = function() {
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