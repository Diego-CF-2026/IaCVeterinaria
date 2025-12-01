<%@ include file="/proteger.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="Modelo.Cita" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
    // --- LÓGICA DE RECUPERACIÓN DE DATOS ---
    Double gananciasTotales = (Double) request.getAttribute("gananciasTotales");
    String mensaje = (String) request.getAttribute("mensaje");
    
    // Formato para moneda
    DecimalFormat df = new DecimalFormat("#,##0.00");
    String gananciasFormateadas = (gananciasTotales != null) ? df.format(gananciasTotales) : "0.00";
    
    // Valores predeterminados para filtros de tiempo
    int mesSeleccionado = request.getAttribute("mes") != null ? (Integer) request.getAttribute("mes") : java.time.LocalDate.now().getMonthValue();
    int anioSeleccionado = request.getAttribute("anio") != null ? (Integer) request.getAttribute("anio") : java.time.LocalDate.now().getYear();

    // Mapa de meses para el Select
    Map<Integer, String> meses = new java.util.LinkedHashMap<>(); 
    meses.put(1, "Enero"); meses.put(2, "Febrero"); meses.put(3, "Marzo"); meses.put(4, "Abril");
    meses.put(5, "Mayo"); meses.put(6, "Junio"); meses.put(7, "Julio"); meses.put(8, "Agosto");
    meses.put(9, "Septiembre"); meses.put(10, "Octubre"); meses.put(11, "Noviembre"); meses.put(12, "Diciembre");
    request.setAttribute("mesesMap", meses); // Subir el mapa de meses

    // 🛑 LÓGICA DE VETERINARIO ELIMINADA.

    // 2. Calcular el total de citas
    List<Cita> citasCompletadas = (List<Cita>) request.getAttribute("citasCompletadas");
    int totalCitas = (citasCompletadas != null) ? citasCompletadas.size() : 0;
    request.setAttribute("totalCitas", totalCitas); 
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Veterinaria Santa Cruz - Reporte de Ganancias</title>
        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/GestorProductos.css">
        
        <style>
            /* Paleta de colores base para el modo día */
            :root {
                --primary-color: #3f51b5; 
                --success-color: #4CAF50; 
                --warning-color: #ff9800; 
                --text-color: #333; 
                --background-color: #f7f9fc; 
                --card-bg: #fff; 
            }
            
            /* Ajustes generales del layout */
            main {
                padding-top: 30px;
                padding-right: 30px;
                padding-bottom: 30px;
                padding-left: 50px;
                
                background-color: var(--background-color);
                min-height: 100vh;
            }
            .sidebar.close ~ main {
                margin-left: 73px;
            }
            .sidebar ~ main {
                margin-left: 250px;
            }

            /* --- HEADER Y ACCIONES (Controles de Filtro) --- */
            .header-actions {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
            }
            .header-actions h1 {
                font-size: 2.2rem;
                color: var(--primary-color);
                font-weight: 700;
                display: flex;
                align-items: center;
                margin: 0 0 20px 0;
            }
            .header-actions h1 i {
                margin-right: 15px;
                font-size: 2.5rem;
            }
            .acciones {
                display: flex;
                gap: 20px; 
                align-items: flex-end;
                flex-wrap: wrap;
                margin: 0 0 20px 0;
            }
            .acciones form {
                display: flex;
                gap: 15px;
                align-items: center;
                background: var(--card-bg);
                padding: 15px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }
            .acciones label {
                font-weight: 600;
                color: var(--text-color);
            }
            .acciones select, .btn-agregar {
                padding: 10px 15px;
                border-radius: 5px; 
                font-size: 1rem;
                border: 1px solid #ccc;
                transition: all 0.3s ease;
            }
            .btn-agregar {
                background-color: var(--primary-color);
                color: white;
                cursor: pointer;
                border: none;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 5px;
            }
            .btn-agregar:hover {
                background-color: #303f9f;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }
            
            /* --- SECCIÓN DE RESUMEN Y TOTALES (Tarjetas) --- */
            .seccion-reporte {
                margin-top: 10px;
            }

            /* Nuevo contenedor flex para las tarjetas */
            .reporte-cards {
                display: flex;
                gap: 25px; 
                margin-bottom: 25px; 
                flex-wrap: wrap; 
            }

            .ganancias-total-card, .citas-total-card {
                flex: 1;
                min-width: 250px; 
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); 
                display: flex;
                justify-content: space-between;
                align-items: center;
                transition: transform 0.3s ease;
                background: var(--card-bg);
            }

            .ganancias-total-card:hover, .citas-total-card:hover {
                transform: translateY(-3px); 
            }
            
            /* Estilo para Tarjeta de Ganancias (Verde) */
            .ganancias-total-card {
                border-left: 6px solid var(--success-color); 
            }
            .ganancias-total-card .valor {
                font-size: 3.5rem;
                color: var(--success-color);
                font-weight: 800;
                line-height: 1;
            }
            .ganancias-total-card i {
                font-size: 4.5rem;
                color: #ffc107; 
            }

            /* Estilo para Tarjeta de Citas (Azul - Color Primario) */
            .citas-total-card {
                border-left: 6px solid var(--primary-color);
            }
            .citas-total-card .valor {
                font-size: 3.5rem;
                color: var(--primary-color);
                font-weight: 800;
                line-height: 1;
            }
            .citas-total-card i {
                font-size: 4.5rem;
                color: var(--primary-color);
            }


            .ganancias-total-card .label, .citas-total-card .label {
                 font-size: 1.2rem;
                 color: #777;
                 font-weight: 500;
                 margin-top: 5px;
            }
            
            /* --- TABLA DE DETALLES --- */
            .tabla-clientes {
                width: 100%;
                border-collapse: separate;
                border-spacing: 0;
                overflow: hidden;
                background: var(--card-bg);
                border-radius: 8px; 
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            }
            .tabla-clientes thead tr {
                background-color: var(--primary-color);
                color: white;
            }
            .tabla-clientes th, .tabla-clientes td {
                padding: 15px;
                text-align: left;
                border-bottom: 1px solid #eee;
            }
            .tabla-clientes tbody tr:hover {
                background-color: #f5f5f5; 
            }
            .tabla-clientes td:nth-child(4) { 
                font-weight: 700;
                color: var(--success-color);
            }
            
            /* --- MENSAJES DE ESTADO (Alertas) --- */
            .mensaje-exito, .mensaje-advertencia {
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 8px;
                font-weight: 600;
                display: flex;
                align-items: center;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }
            .mensaje-exito {
                background-color: #e6ffed; 
                color: var(--success-color);
                border: 1px solid #c2e0c6;
            }
            .mensaje-advertencia {
                background-color: #fff8e1; 
                color: var(--warning-color);
                border: 1px solid #ffecb3;
            }
            .mensaje-exito i, .mensaje-advertencia i {
                margin-right: 15px;
                font-size: 1.5rem;
            }
            
            hr {
                border: 0;
                height: 1px;
                background-image: linear-gradient(to right, rgba(0, 0, 0, 0), rgba(63, 81, 181, 0.2), rgba(0, 0, 0, 0));
                margin: 20px 0;
            }
            
            .sidebar .nav-link a:hover {
        background-color: transparent !important;
        color: var(--text-color) !important;
    }

    .sidebar .nav-link a:hover::before,
    .sidebar .nav-link:hover .icon,
    .sidebar .nav-link:hover .text {
        border: none !important;
        color: var(--text-color) !important;
    }
        </style>
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
                    <li class="nav-link">
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
                        <a href="<%= request.getContextPath() %>/ProductoRecepServlet?accion=listarEntregados">
                            <i class='bx bx-check-circle icon'></i><span class="text">Productos Entregados</span>
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
        <main>
            <div class="header-actions">
                <h1><i class='bx bxs-calculator'></i> Reporte de Ganancias</h1>
                
                <div class="acciones">
                    <form method="get" action="<%= request.getContextPath()%>/ReporteGananciasServlet">
                        <label for="mes">Mes:</label>
                        <select name="mes" id="mes">
                            <% for (Map.Entry<Integer, String> entry : meses.entrySet()) { %>
                                <option value="<%= entry.getKey() %>" <%= entry.getKey() == mesSeleccionado ? "selected" : "" %>>
                                    <%= entry.getValue() %>
                                </option>
                            <% } %>
                        </select>
                        
                        <label for="anio">Año:</label>
                        <select name="anio" id="anio">
                            <option value="2024" <%= anioSeleccionado == 2024 ? "selected" : "" %>>2024</option>
                            <option value="2025" <%= anioSeleccionado == 2025 ? "selected" : "" %>>2025</option>
                            <option value="2026" <%= anioSeleccionado == 2026 ? "selected" : "" %>>2026</option>
                            <option value="2027" <%= anioSeleccionado == 2027 ? "selected" : "" %>>2027</option>
                            <option value="2028" <%= anioSeleccionado == 2028 ? "selected" : "" %>>2028</option>
                        </select>
                        
                        <button type="submit" class="btn-agregar">
                            <i class='bx bx-search-alt'></i> Buscar
                        </button>
                    </form>    
                </div>
            </div>
            
            <hr>
            
            <div class="seccion-reporte">
                <c:if test="${not empty mensaje}">
                    <c:choose>
                        <c:when test="${fn:startsWith(mensaje, '✅ Reporte generado correctamente')}">
                            <p class="mensaje-exito"><i class='bx bx-check-circle'></i> ${mensaje}</p>
                        </c:when>
                        <c:when test="${fn:startsWith(mensaje, '⚠️ No se encontraron citas') || fn:startsWith(mensaje, '❌ Error')}">
                            <p class="mensaje-advertencia"><i class='bx bx-error-alt'></i> ${mensaje}</p>
                        </c:when>
                        <c:otherwise>
                            <p class="mensaje-advertencia"><i class='bx bx-error-alt'></i> ${mensaje}</p>
                        </c:otherwise>
                    </c:choose>
                </c:if>
                
                <div class="reporte-cards">
                    <div class="ganancias-total-card">
                        <div class="ganancias-info">
                            <span class="label">Total de Ganancias Netas</span>
                            <span class="valor">$<%= gananciasFormateadas %></span>
                        </div>
                        <i class='bx bx-money-withdraw'></i>
                    </div>

                    <div class="citas-total-card">
                        <div class="citas-info">
                            <span class="label">Total de Citas Completadas</span>
                            <span class="valor">${totalCitas}</span>
                        </div>
                        <i class='bx bx-notepad'></i>
                    </div>
                </div>
                
                
                <c:if test="${not empty citasCompletadas}">
                    <table class="tabla-clientes">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Cliente (DNI)</th>
                                <th>Veterinario</th>
                                <th>Precio</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cita" items="${citasCompletadas}">
                                <tr>
                                    <td>${cita.fecha}</td>
                                    <td>${cita.nombreCliente} ${cita.apellidoCliente} (${cita.dniCliente})</td>
                                    <td>${cita.nombreVeterinario} ${cita.apellidoVeterinario}</td>
                                    <td>$<%= df.format(((Modelo.Cita)pageContext.getAttribute("cita")).getPrecio()) %></td>
                                    <td>${cita.estadoNombre}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </main>

        <button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/GestorProductos.js"></script>
        
        
    </body>
</html>