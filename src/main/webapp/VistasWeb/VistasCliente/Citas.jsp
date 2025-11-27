<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Solicitar Cita | Veterinaria</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/contacto.css" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ModoNoche-Sidebar.css">
        <style>
            body {
                background-color: #f5f9fb;
            }

            .container {
                max-width: 600px;
                margin-top: 50px;
                margin-bottom: 60px;
            }

            .card {
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                border-radius: 18px;
                border: none;
            }

            .card-title {
                font-weight: 600;
            }

            /* Asegurar legibilidad del select en modo claro */
            select.form-select,
            option {
                color: #000000 !important;
            }

            /* ======== MODO NOCHE ESPECÍFICO PARA ESTA PÁGINA ======== */

            body.modo-noche {
                background-color: #020617; /* gris muy oscuro */
                color: #e5e7eb;
            }

            /* Navbar oscura */
            body.modo-noche .navbar {
                background-color: #020617;
                box-shadow: 0 2px 10px rgba(0,0,0,0.7);
            }

            body.modo-noche .center-links a {
                color: #e5e7eb;
            }

            body.modo-noche .center-links a:hover,
            body.modo-noche .center-links a.active-link {
                color: #ffffff !important;
                border-bottom: 2px solid #f9fafb !important;
                font-weight: 600;
            }

            /* Botón "Ver perfil" en modo noche */
            body.modo-noche .btn.perfil {
                background-color: #f9fafb !important;
                color: #111827 !important;
            }

            /* Título principal y nombre del cliente */
            body.modo-noche h1,
            body.modo-noche .card-title {
                color: #e5e7eb;
            }

            /* Tarjeta del formulario en modo noche */
            body.modo-noche .card {
                background-color: #020617;
                box-shadow: 0 20px 40px rgba(0,0,0,0.8);
            }

            /* Labels y textos de formulario */
            body.modo-noche .form-label {
                color: #e5e7eb;
            }

            /* Inputs, selects y textarea */
            body.modo-noche .form-control,
            body.modo-noche .form-select,
            body.modo-noche textarea.form-control {
                background-color: #020617;
                border-color: #374151;
                color: #e5e7eb;
            }

            body.modo-noche .form-control:focus,
            body.modo-noche .form-select:focus,
            body.modo-noche textarea.form-control:focus {
                border-color: #22c55e;
                box-shadow: 0 0 0 0.2rem rgba(34,197,94,0.35);
            }

            body.modo-noche .form-control::placeholder,
            body.modo-noche textarea.form-control::placeholder {
                color: #9ca3af;
            }

            /* Select y opciones en modo noche */
            body.modo-noche select.form-select,
            body.modo-noche select.form-select option {
                background-color: #020617;
                color: #f9fafb !important;
            }

            /* Alert info del costo estimado */
            body.modo-noche .alert-info {
                background-color: rgba(34,197,94,0.08);
                border-color: #22c55e;
                color: #bbf7d0;
            }

            /* Botón principal del formulario */
            body.modo-noche .btn-primary {
                background-color: #22c55e;
                border-color: #16a34a;
            }

            body.modo-noche .btn-primary:hover {
                background-color: #16a34a;
                border-color: #15803d;
            }

            /* Botón flotante modo noche: oscuro con icono claro */
            body.modo-noche .modo-noche-flotante {
                background-color: #111827;
                color: #facc15;
            }
            /* Sidebar perfil en oscuro */
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
            /* Navbar oscura (por si contacto.css no lo define) */
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

            /* Contenedor principal de contacto */
            body.modo-noche .contacto-texto {
                background-color: #111827;
                color: #e5e7eb;
                box-shadow: 0 12px 32px rgba(0,0,0,0.7);
            }
            body.modo-noche .contacto-texto h1 {
                color: #f9fafb;
            }
            body.modo-noche .contacto-texto p {
                color: #e5e7eb;
            }

            /* Caja de datos de contacto */
            body.modo-noche .contacto-datos {
                background-color: #020617;
                border-color: #1f2937;
                color: #e5e7eb;
            }

            /* Texto extra */
            body.modo-noche .contacto-extra p {
                color: #e5e7eb;
            }

        </style>
    </head>
    <body>

        <nav class="navbar">
            <div class="logo-container">
                <a href="${pageContext.request.contextPath}/index.jsp">
                    <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo de Veterinaria Santa Cruz" class="logo" />
                </a>
            </div>
            <div class="hamburger" id="hamburger" aria-label="Menú" aria-expanded="false">
                <span></span><span></span><span></span>
            </div>
            <div class="nav-links" id="nav-links">
                <div class="center-links">
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
                    <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos">Productos</a>
                    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto" class="active-link">Contacto</a>
                </div>
                <div class="buttons">
                    <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver Perfil</a>
                </div>
            </div>
        </nav>  

        <!-- Sidebar perfil -->
        <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
            <h2 id="perfilTitle">Mi Perfil</h2>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
            <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
            <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet">Citas agendadas</a>
            <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
        </div>
        <div id="sidebarOverlay"></div>

        <div class="container">

            <h1 class="mb-4 text-center">Solicitar Nueva Cita</h1>

            <c:if test="${mensaje != null}">
                <div class="alert
                     <c:choose>
                         <c:when test="${mensaje.startsWith('✅')}">alert-success</c:when>
                         <c:when test="${mensaje eq 'cancelado'}">alert-warning</c:when>
                         <c:otherwise>alert-danger</c:otherwise>
                     </c:choose>
                     " role="alert">
                    <c:choose>
                        <c:when test="${mensaje.startsWith('✅')}">
                            ${mensaje}
                        </c:when>
                        <c:when test="${mensaje eq 'error_campos_vacios'}">
                            ❌ Error: Todos los campos son obligatorios.
                        </c:when>
                        <c:when test="${mensaje eq 'error_formato_numerico'}">
                            ❌ Error: El ID del veterinario o el precio tienen un formato inválido.
                        </c:when>
                        <c:otherwise>
                            ${mensaje}
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <div class="card p-4">
                <h2 class="card-title">${cliente.nombre} ${cliente.apellido}</h2>

                <form action="${pageContext.request.contextPath}/UsuarioCitasServlet" method="post">
                    <input type="hidden" name="accion" value="registrar">

                    <div class="mb-3">
                        <label for="idEspecialidad" class="form-label">Especialidad Requerida:</label>
                        <select class="form-select" id="idEspecialidad" name="idEspecialidad" required
                                onchange="actualizarCostoYFiltrarVets()">
                            <option value="">Seleccione una especialidad</option>
                            <c:forEach var="esp" items="${listaEspecialidades}">
                                <option value="${esp.idEspecialidad}" data-precio="${esp.precio}">${esp.nombreEspecialidad}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="idVeterinario" class="form-label">Veterinario:</label>
                        <select class="form-select" id="idVeterinario" name="idVeterinario" required>
                            <option value="">Seleccione un veterinario</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="fecha" class="form-label">Fecha:</label>
                        <input type="date" class="form-control" id="fecha" name="fecha" required>
                    </div>

                    <div class="mb-3">
                        <label for="hora" class="form-label">Hora:</label>
                        <input type="time" class="form-control" id="hora" name="hora" required>
                    </div>

                    <div class="mb-3">
                        <label for="motivo" class="form-label">Motivo de la Cita:</label>
                        <textarea class="form-control" id="motivo" name="motivo" rows="3" required></textarea>
                    </div>

                    <input type="hidden" name="precio" id="precioCita" value="0.0">

                    <div class="alert alert-info mt-3" role="alert">
                        Costo Estimado de la Cita: **S/ <span id="displayPrecio"></span>**
                    </div>

                    <button type="submit" class="btn btn-primary w-100 mt-3">Registrar Cita</button>
                </form>
            </div>
        </div>

        <!-- Botón flotante de modo noche -->
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

        <script>
                                    // Abrir/cerrar sidebar perfil
                                    document.getElementById('verPerfilBtn').addEventListener('click', function () {
                                        document.getElementById('sidebarPerfil').classList.add('active');
                                        document.getElementById('sidebarOverlay').classList.add('active');
                                    });

                                    document.getElementById('sidebarOverlay').addEventListener('click', function () {
                                        document.getElementById('sidebarPerfil').classList.remove('active');
                                        this.classList.remove('active');
                                    });

                                    // Hamburger (navbar responsive)
                                    document.getElementById('hamburger').addEventListener('click', function () {
                                        this.classList.toggle('active');
                                        document.getElementById('nav-links').classList.toggle('active');
                                    });

                                    // Restricciones de fecha y hora
                                    function establecerRestriccionesTiempo() {
                                        const inputFecha = document.getElementById('fecha');
                                        const inputHora = document.getElementById('hora');

                                        const now = new Date();
                                        const year = now.getFullYear();
                                        const month = String(now.getMonth() + 1).padStart(2, '0');
                                        const day = String(now.getDate()).padStart(2, '0');
                                        const today = `${year}-${month}-${day}`;
                                                inputFecha.min = today;

                                                inputFecha.addEventListener('change', function () {
                                                    const fechaSeleccionada = this.value;
                                                    if (fechaSeleccionada === today) {
                                                        const currentHour = String(now.getHours()).padStart(2, '0');
                                                        const currentMinute = String(now.getMinutes() + 1).padStart(2, '0');
                                                        inputHora.min = `${currentHour}:${currentMinute}`;
                                                                        if (inputHora.value && inputHora.value < inputHora.min) {
                                                                            inputHora.value = '';
                                                                        }
                                                                    } else {
                                                                        inputHora.min = '00:00';
                                                                    }
                                                                });

                                                                if (inputFecha.value === today) {
                                                                    const currentHour = String(now.getHours()).padStart(2, '0');
                                                                    const currentMinute = String(now.getMinutes() + 1).padStart(2, '0');
                                                                    inputHora.min = `${currentHour}:${currentMinute}`;
                                                                            } else {
                                                                                inputHora.min = '00:00';
                                                                            }
                                                                        }

                                                                        // Actualizar precio y filtrar veterinarios por especialidad (AJAX)
                                                                        function actualizarCostoYFiltrarVets() {
                                                                            const selectEspecialidad = document.getElementById('idEspecialidad');
                                                                            const precioInput = document.getElementById('precioCita');
                                                                            const displayPrecio = document.getElementById('displayPrecio');
                                                                            const selectVeterinario = document.getElementById('idVeterinario');

                                                                            const selectedOption = selectEspecialidad.options[selectEspecialidad.selectedIndex];
                                                                            if (selectedOption && selectedOption.hasAttribute('data-precio')) {
                                                                                const precio = selectedOption.getAttribute('data-precio');
                                                                                precioInput.value = precio;
                                                                                displayPrecio.textContent = parseFloat(precio).toFixed(2);
                                                                            } else {
                                                                                precioInput.value = '0.0';
                                                                                displayPrecio.textContent = '0.00';
                                                                            }

                                                                            const idEspecialidad = selectEspecialidad.value;
                                                                            selectVeterinario.innerHTML = '<option value="">Cargando veterinarios...</option>';

                                                                            if (idEspecialidad) {
                                                                                const url = '${pageContext.request.contextPath}/AjaxCitasServlet?accion=listarVeterinariosPorEspecialidad&idEspecialidad=' + idEspecialidad;

                                                                                fetch(url)
                                                                                        .then(response => {
                                                                                            if (!response.ok) {
                                                                                                return response.json().then(err => {
                                                                                                    throw new Error('Error ' + response.status + ': ' + (err.error || 'Respuesta de servidor inválida.'));
                                                                                                }).catch(() => {
                                                                                                    throw new Error('Error ' + response.status + ': La respuesta del servidor no fue JSON.');
                                                                                                });
                                                                                            }
                                                                                            return response.json();
                                                                                        })
                                                                                        .then(data => {
                                                                                            selectVeterinario.innerHTML = '<option value="">Seleccione un veterinario</option>';

                                                                                            if (Array.isArray(data) && data.length > 0) {
                                                                                                data.forEach(vet => {
                                                                                                    const option = document.createElement('option');
                                                                                                    option.value = vet.idVeterinario;
                                                                                                    const nombre = String(vet.nombreVeterinario || '');
                                                                                                    const apellido = String(vet.apellidoVeterinario || '');
                                                                                                    option.textContent = nombre + ' ' + apellido;
                                                                                                    selectVeterinario.appendChild(option);
                                                                                                });
                                                                                            } else if (Array.isArray(data) && data.length === 0) {
                                                                                                selectVeterinario.innerHTML = '<option value="">No hay veterinarios disponibles para esta especialidad</option>';
                                                                                            }
                                                                                        })
                                                                                        .catch(error => {
                                                                                            console.error('❌ Error AJAX al cargar veterinarios:', error);
                                                                                            selectVeterinario.innerHTML = `<option value="">ERROR: ${error.message}</option>`;
                                                                                        });
                                                                            } else {
                                                                                selectVeterinario.innerHTML = '<option value="">Seleccione un veterinario</option>';
                                                                            }
                                                                        }

                                                                        document.addEventListener('DOMContentLoaded', function () {
                                                                            establecerRestriccionesTiempo();
                                                                            actualizarCostoYFiltrarVets();
                                                                        });
        </script>
    </body>
</html>
