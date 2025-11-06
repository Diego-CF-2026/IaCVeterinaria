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
        .container { max-width: 600px; margin-top: 50px; }
        .card { box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
        /* Añadido para descartar problemas de color/CSS en el select */
        select.form-select, option {
            color: #000000 !important;
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
                    <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Carrito</a>
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
                    <%-- Si no es éxito ni cancelación, asumimos que es un mensaje de error, incluyendo los de validación --%>
                    <c:otherwise>alert-danger</c:otherwise>
                </c:choose>
            " role="alert">
                <c:choose>
                    <%-- Caso 1: Mensaje que empieza con ✅ (Éxito del DAO) --%>
                    <c:when test="${mensaje.startsWith('✅')}">
                        ${mensaje}
                    </c:when>
                    <%-- Caso 2: Mensaje de error interno conocido (del Servlet) --%>
                    <c:when test="${mensaje eq 'error_campos_vacios'}">
                        ❌ Error: Todos los campos son obligatorios.
                    </c:when>
                    <c:when test="${mensaje eq 'error_formato_numerico'}">
                        ❌ Error: El ID del veterinario o el precio tienen un formato inválido.
                    </c:when>
                    <%-- Caso 3: Si no es un caso conocido, mostramos el mensaje directo (incluye errores de fecha/hora) --%>
                    <c:otherwise>
                        ${mensaje}
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <div class="card p-4">
            <h2 class="card-title ">${cliente.nombre} ${cliente.apellido}</h2>
            
            <form action="${pageContext.request.contextPath}/UsuarioCitasServlet" method="post">
                <input type="hidden" name="accion" value="registrar">
                
                <div class="mb-3">
                    <label for="idEspecialidad" class="form-label">Especialidad Requerida:</label>
                    <select class="form-select" id="idEspecialidad" name="idEspecialidad" required
                            onchange="actualizarCostoYFiltrarVets()">
                        <option value="">Seleccione una especialidad</option>
                        <c:forEach var="esp" items="${listaEspecialidades}">
                            <option value="${esp.idEspecialidad}" data-precio="${esp.precio}">${esp.nombreEspecialidad} (S/. ${esp.precio})</option>
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
                    Costo Estimado de la Cita: **S/. <span id="displayPrecio">0.00</span>**
                </div>

                <button type="submit" class="btn btn-primary w-100 mt-3">Registrar Cita</button>
            </form>
        </div>
    </div>
            <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
    });

    
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.classList.remove('active');
    });

        // 1. FUNCIÓN PRINCIPAL DE RESTRICCIÓN DE FECHA Y HORA (HTML/JS - Front-end)
        function establecerRestriccionesTiempo() {
            const inputFecha = document.getElementById('fecha');
            const inputHora = document.getElementById('hora');
            
            // Obtener la fecha y hora actual en formato local
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            
            // Establecer la fecha mínima como HOY
            const today = `${year}-${month}-${day}`;
            inputFecha.min = today;
            
            // Escuchar cambios en la fecha para actualizar la restricción de hora
            inputFecha.addEventListener('change', function() {
                const fechaSeleccionada = this.value;
                
                // Si la fecha seleccionada es HOY, limitar la hora mínima
                if (fechaSeleccionada === today) {
                    const currentHour = String(now.getHours()).padStart(2, '0');
                    const currentMinute = String(now.getMinutes() + 1).padStart(2, '0'); // +1 minuto para evitar seleccionar la hora exacta actual
                    inputHora.min = `${currentHour}:${currentMinute}`;
                    
                    // Asegurar que, si la hora ya seleccionada es anterior, se borre
                    if (inputHora.value && inputHora.value < inputHora.min) {
                        inputHora.value = '';
                    }
                } else {
                    // Si es cualquier otro día (futuro), no hay restricción mínima de hora
                    // Nota: Las restricciones de horario de 9:00 a 17:00 y Domingo se validan en el BACKEND (DAO)
                    inputHora.min = '00:00';
                }
            });

            // Inicializar la restricción de hora al cargar la página
            if (inputFecha.value === today) {
                const currentHour = String(now.getHours()).padStart(2, '0');
                const currentMinute = String(now.getMinutes() + 1).padStart(2, '0');
                inputHora.min = `${currentHour}:${currentMinute}`;
            } else {
                inputHora.min = '00:00';
            }
        }
        
        // --- CÓDIGO AJAX PARA FILTRAR VETERINARIOS Y CALCULAR PRECIO ---
        function actualizarCostoYFiltrarVets() {
            const selectEspecialidad = document.getElementById('idEspecialidad');
            const precioInput = document.getElementById('precioCita');
            const displayPrecio = document.getElementById('displayPrecio');
            const selectVeterinario = document.getElementById('idVeterinario');
            
            // 1. Lógica de precio
            const selectedOption = selectEspecialidad.options[selectEspecialidad.selectedIndex];
            if (selectedOption && selectedOption.hasAttribute('data-precio')) {
                const precio = selectedOption.getAttribute('data-precio');
                precioInput.value = precio;
                displayPrecio.textContent = parseFloat(precio).toFixed(2);
            } else {
                precioInput.value = '0.0';
                displayPrecio.textContent = '0.00';
            }

            // 2. Lógica AJAX para Filtrar Veterinarios
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
                            
                            // Concatenación robusta
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

        // Ejecutar ambas funciones al cargar la página
        document.addEventListener('DOMContentLoaded', function() {
            establecerRestriccionesTiempo();
            actualizarCostoYFiltrarVets();
        });
    </script>
</body>
</html>