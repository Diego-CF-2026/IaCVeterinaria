<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Modelo.Cliente"%>
<%@page import="Modelo.Usuario"%>
<%
    Cliente cliente = (Cliente) session.getAttribute("cliente");
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (cliente == null && usuario == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mi Perfil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Cuenta.css" />
    <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <!-- NAVBAR -->
    <nav class="navbar">
        <div class="logo-container">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo">
        </div>
        <div class="nav-links">
            <div class="center-links">
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp"  id="link-contacto">Contacto</a>
            </div>
            <div class="buttons">
                <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver perfil</a>
            </div>
        </div>
        <div class="hamburger" id="hamburger-menu">
            <span></span><span></span><span></span>
        </div>
    </nav>

    <!-- Sidebar perfil -->
    <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
        <h2 id="perfilTitle">Mi Perfil</h2>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
        <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MisCitas.jsp">Citas agendadas</a>
        <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
    </div>
    <div id="sidebarOverlay"></div>

    <!-- PERFIL -->
    <section class="perfil-container">
        <div class="perfil-img">
            <img src="${pageContext.request.contextPath}/Recursos/perro.jpeg" alt="Foto de perfil">
        </div>
        <div class="perfil-info">
            <div class="perfil-title">
                <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Patita" class="paw-img">
                <h2>MI PERFIL</h2>
            </div>

            <!-- Formulario editar perfil -->
            <form class="perfil-form" id="perfilForm" action="${pageContext.request.contextPath}/EditarPerfilServlet" method="post">
                <div class="perfil-row">
                    <input type="text" name="nombre" value="<%= cliente.getNombre() %>" readonly>
                    <input type="text" name="apellido" value="<%= cliente.getApellido() %>" readonly>
                </div>
                <div class="perfil-row">
                    <input type="text" name="dni" value="<%= cliente.getDni() %>" readonly>
                    <input type="text" name="telefono" value="<%= cliente.getTelefono() %>" readonly>
                </div>
                <div class="perfil-row">
                    <input type="email" name="correo" value="<%= usuario.getCorreo() %>" readonly>
                </div>

                <!-- Campo oculto con ID -->
                <input type="hidden" name="usuarioId" value="<%= usuario.getIdUsuario() %>">

                <div class="perfil-actions">
                    <button type="button" class="btn-editar" id="btnEditar">
                        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Patita" class="paw-btn">
                        Editar
                    </button>
                    <button type="submit" class="btn-guardar" id="btnGuardar" style="display:none;">
                        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Patita" class="paw-btn">
                        Guardar
                    </button>
                    <button type="button" class="btn-eliminar" id="btnEliminar">
                        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Patita" class="paw-btn">
                        Eliminar
                    </button>
                </div>
            </form>

            <!-- Formulario eliminar perfil -->
            <form id="eliminarForm" action="${pageContext.request.contextPath}/EliminarPerfilServlet" method="post" style="display:none; margin-top:20px;">
                <p>Escribe <strong>ELIMINAR</strong> para confirmar:</p>
                <input type="text" id="confirmEliminarInput" name="confirmText" placeholder="ELIMINAR" style="padding:10px; border-radius:8px; border:1px solid #ccc; font-size:1rem;">
                <input type="hidden" name="usuarioId" value="<%= usuario.getIdUsuario() %>">
                <button type="submit" id="confirmEliminarBtn" disabled style="background-color:#ff0000; color:#fff; border:none; padding:10px; border-radius:8px; font-weight:600; cursor:pointer;">
                    Confirmar eliminación
                </button>
            </form>
        </div>
    </section>

    <script>
        // Hamburger menu
        document.getElementById('hamburger-menu').onclick = function() {
            document.querySelector('.nav-links').classList.toggle('active');
        };

        // Sidebar perfil open/close
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

        // Editar / Guardar funcionalidad
        const btnEditar = document.getElementById('btnEditar');
        const btnGuardar = document.getElementById('btnGuardar');
        const btnEliminar = document.getElementById('btnEliminar');
        const perfilForm = document.getElementById('perfilForm');
        const inputs = perfilForm.querySelectorAll('input[type="text"], input[type="email"]');

        let editando = false;

        btnEditar.addEventListener('click', () => {
            if (!editando) {
                inputs.forEach(input => input.readOnly = false);
                btnEditar.style.display = 'none';
                btnGuardar.style.display = 'inline-flex';
                btnEliminar.style.display = 'none';
                editando = true;
            }
        });

        btnGuardar.addEventListener('click', () => {
            inputs.forEach(input => input.readOnly = true);
            btnEditar.style.display = 'inline-flex';
            btnGuardar.style.display = 'none';
            btnEliminar.style.display = 'inline-flex';
            editando = false;
        });

        // Eliminar perfil funcionalidad
        const eliminarForm = document.getElementById('eliminarForm');
        const confirmEliminarInput = document.getElementById('confirmEliminarInput');
        const confirmEliminarBtn = document.getElementById('confirmEliminarBtn');

        btnEliminar.addEventListener('click', () => {
            eliminarForm.style.display = 'block';
        });

        confirmEliminarInput.addEventListener('input', () => {
            confirmEliminarBtn.disabled = confirmEliminarInput.value !== 'ELIMINAR';
        });
    </script>
</body>
</html>
