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
    <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <style>
        /* ======================================= */
        /* CONTENEDOR - TAMAÑO ESTÁNDAR */
        /* ======================================= */
        .perfil-container {
            max-width: 1200px;
            margin: 40px auto;
            display: flex;
            background-color: white; 
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        }
        
        .perfil-img {
            flex: 0 0 40%;
            max-width: 40%;
            overflow: hidden;
            border-radius: 10px 0 0 10px;
        }
        
        .perfil-info { 
            flex: 0 0 60%;
            max-width: 60%;
            padding: 20px; 
            background-color: #f7f9fa;
            border-radius: 0 10px 10px 0;
            padding-top: 20px; 
        }
        
        .perfil-img img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }
        
        /* Título de la sección */
        .perfil-title {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 15px; 
        }
        .paw-img { width: 25px; height: 25px; } 
        
        /* Contenedor Responsivo de la Tabla */
        .tabla-citas-wrapper { 
            overflow-x: auto; 
            width: 100%;
            margin-top: 15px; 
        }
        
        /* ======================================= */
        /* ESTILOS PARA HÍPER-COMPACTAR LA TABLA */
        /* ======================================= */
        .citas-listado { 
            width: 100%; 
            min-width: 600px; 
            border-collapse: collapse; 
            font-size: 0.85rem; 
        }
        .citas-listado th, .citas-listado td { 
            border: none;
            padding: 5px 5px; /* ¡Mínimo padding! */
            text-align: left; 
        }
        
        /* Borde Separador sutil para las filas */
        .citas-listado tbody tr {
            border-bottom: 1px solid #e9ecef; 
        }
        
        .citas-listado th { 
            background-color: transparent; 
            color: #555; 
            font-weight: 600; 
            text-transform: uppercase;
            padding-top: 0;
            padding-bottom: 5px; /* Padding inferior también reducido */
        }
        
        /* Ajuste de Anchos de Columna (Optimización) */
        .citas-listado th:nth-child(1), .citas-listado td:nth-child(1) { width: 5%; text-align: center; } /* ID: 5% */
        .citas-listado th:nth-child(2), .citas-listado td:nth-child(2) { width: 10%; } /* Fecha: 10% */
        .citas-listado th:nth-child(3), .citas-listado td:nth-child(3) { width: 7%; text-align: center; } /* Hora: 7% */
        .citas-listado th:nth-child(4), .citas-listado td:nth-child(4) { width: 17%; } /* Motivo: 17% */
        .citas-listado th:nth-child(5), .citas-listado td:nth-child(5) { width: 17%; } /* Veterinario: 17% */
        .citas-listado th:nth-child(6), .citas-listado td:nth-child(6) { width: 17%; } /* Precio: 17% */
        .citas-listado th:nth-child(7), .citas-listado td:nth-child(7) { width: 13%; text-align: center; } /* Estado: 13% */
        .citas-listado th:nth-child(8), .citas-listado td:nth-child(8) { width: 15%; text-align: center; } /* Acción: 15% */


        /* Estilos de Estado Compactos */
        .estado-Pendiente { 
            font-weight: bold; 
            color: #8a6d3b;
            background-color: #fcf8e3;
            padding: 2px 5px; /* Padding reducido al mínimo */
            border-radius: 3px; 
            display: inline-block;
            border: 1px solid #faebcc;
            font-size: 0.8rem; 
        }
        
        /* Estilos de Botón Cancelar Compactos */
        .btn-cancelar {
            background-color: #dc3545; color: white; border: none;
            padding: 2px 6px; /* Padding reducido al mínimo */
            border-radius: 3px; 
            text-decoration: none;
            font-size: 0.8rem; 
            transition: background-color 0.3s;
        }
        .btn-cancelar:hover {
            background-color: #c82333;
        }
        
        .estado-Confirmada { color: #4caf50; font-weight: bold; }
        .estado-Cancelada { color: #f44336; font-weight: bold; }
        .alerta-citas { padding: 15px; background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; border-radius: 8px; margin-top: 20px; }

    </style>
</head>
<body>
    
    <%-- ********************************************************** --%>
    <p style="color: blue; font-weight: bold; background-color: #f0f8ff; padding: 10px;">
        DEBUG: ID Sesión: <c:out value="${sessionScope.idClienteSesion}" default="[ERROR DE SESIÓN]"/> | 
        ¿Lista Vacía?: ${empty misCitas} | 
        Tamaño: ${fn:length(misCitas)}
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
                                <c:forEach var="cita" items="${misCitas}">
                                    <tr>
                                        <td><c:out value="${cita.idCita}" /></td>
                                        <td><c:out value="${cita.fecha}" /></td>
                                        <td><c:out value="${fn:substring(cita.hora, 0, 5)}" /></td>
                                        <td><c:out value="${cita.motivo}" /></td>
                                        <td>Dr/a. <c:out value="${cita.nombreVeterinario} ${cita.apellidoVeterinario}" /></td> 
                                        <td>
                                            <span class="estado-<c:out value="${cita.estadoNombre}" />">
                                                <c:out value="${cita.estadoNombre}" />
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${cita.estadoNombre eq 'Pendiente'}">
                                                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=${cita.idCita}" class="btn-cancelar" onclick="return confirm('¿Estás seguro de que deseas cancelar esta cita?');">
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