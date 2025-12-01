<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Recepcionistas - VeterinariaSantaCruz</title>

        <link href="https://unpkg.com/boxicons@2.1.1/css/boxicons.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/ModoNoche-Sidebar.css">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/GestorProductos.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">


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
                    <li class="nav-link ">
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
                        <a href="<%= request.getContextPath()%>/carritoservletAdmin">
                            <i class='bx bx-check-circle icon'></i><span class="text">Productos Entregados</span>
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

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="h2">Gestión de Recepcionistas</h1>
                <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#modalNuevoRecepcionista">
                    <i class='bx bx-plus'></i> Agregar
                </button>
            </div>

            <table class="tabla-clientes">
                <thead> 
                    <tr>
                        
                        <th>Nombres</th>
                        <th>Apellidos</th>
                        <th>Correo Electrónico</th>
                        <th>Teléfono</th>
                        <th>Estado</th>
                        <th style="width: 200px;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${requestScope.recepcionistas}"> 
                        <tr>
                           
                            <td><c:out value="${r.getNombreRecepcionista()}" /></td>
                            <td><c:out value="${r.getApellidoRecepcionista()}" /></td>
                            <td><c:out value="${r.getUsuario().getCorreo()}" /></td> 
                            <td><c:out value="${r.getTelefonoRecepcionista()}" /></td>

                            <td>
                                <c:choose>
                                    <c:when test="${r.getUsuario().isEstado()}">
                                        <span class="badge bg-success">Activo</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-danger">Inactivo</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td class="text-nowrap">
                                <c:choose>
                                    <c:when test="${r.getUsuario().isEstado()}">
                                        <a href="<%= request.getContextPath()%>/AdminRecepServlet?accion=desactivar&id=${r.getIdUsuario()}" 
                                           class="btn btn-danger btn-sm me-1" 
                                           onclick="return confirm('¿Seguro que desea DESACTIVAR a ${r.getNombreRecepcionista()}?');"
                                           title="Desactivar">
                                            Desactivar
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<%= request.getContextPath()%>/AdminRecepServlet?accion=activar&id=${r.getIdUsuario()}" 
                                           class="btn btn-success btn-sm me-1" 
                                           onclick="return confirm('¿Seguro que desea ACTIVAR a ${r.getNombreRecepcionista()}?');"
                                           title="Activar">
                                            Activar
                                        </a>
                                    </c:otherwise>
                                </c:choose>

                                <button class="btn btn-info btn-sm text-white" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#modalEditarRecepcionista" 
                                        onclick="cargarDatosEdicion(
                                                        '${r.getIdUsuario()}',
                                                        '${r.getNombreRecepcionista()}',
                                                        '${r.getApellidoRecepcionista()}',
                                                        '${r.getUsuario().getCorreo()}',
                                                        '${r.getTelefonoRecepcionista()}'
                                                        )"
                                        title="Editar">
                                    Editar
                                </button>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.recepcionistas}">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">No se encontraron recepcionistas.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>


            <c:if test="${not empty requestScope.mensaje}">
                <div class="alert alert-success mt-3" role="alert"><c:out value="${requestScope.mensaje}"/></div>
            </c:if>
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger mt-3" role="alert"><c:out value="${requestScope.error}"/></div>
            </c:if>

            <div class="modal" id="modalNuevoRecepcionista">
               //ambos modales
                <div class="modal-dialog modal-dialog-centered modal-lg">

                    <div class="modal-content"> <div class="modal-header bg-success text-white">
                            <h5 class="modal-title" id="nuevoModalLabel">Registrar Nuevo Recepcionista</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form action="<%= request.getContextPath()%>/AdminRecepServlet?accion=guardar" method="POST">
                            <div class="modal-body"> <div class="mb-3"><label class="form-label">Nombre(s)</label><input type="text" class="form-control" name="txtNombre" required></div>
                                <div class="mb-3"><label class="form-label">Apellido(s)</label><input type="text" class="form-control" name="txtApellido" required></div>
                                <div class="mb-3"><label class="form-label">Teléfono</label><input type="text" class="form-control" name="txtTelefono" required></div>
                                <hr>
                                <div class="mb-3"><label class="form-label">Correo Electrónico (Usuario)</label><input type="email" class="form-control" name="txtCorreo" required></div>
                                <div class="mb-3"><label class="form-label">Contraseña</label><input type="password" class="form-control" name="txtContrasena" required></div>
                            </div>
                            <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                                <button type="submit" class="btn btn-success">Guardar Registro</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
                            
//este modal se encarga de editar el recepcionista

            <div class="modal" id="modalEditarRecepcionista"">
                <!-- en ambos modales -->
                <div class="modal-dialog modal-dialog-centered modal-lg">

                    <div class="modal-content"> <div class="modal-header bg-info text-white">
                            <h5 class="modal-title" id="editarModalLabel">Editar Recepcionista</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form id="formEditar" action="<%= request.getContextPath()%>/AdminRecepServlet?accion=actualizar" method="POST">
                            <div class="modal-body"> 
                                <input type="hidden" id="editIdUsuario" name="txtIdUsuario"> 

                                <div class="mb-3"><label class="form-label">Nombre(s)</label><input type="text" class="form-control" id="editNombre" name="txtNombre" required></div>
                                <div class="mb-3"><label class="form-label">Apellido(s)</label><input type="text" class="form-control" id="editApellido" name="txtApellido" required></div>
                                <div class="mb-3"><label class="form-label">Teléfono</label><input type="text" class="form-control" id="editTelefono" name="txtTelefono" required></div>
                                <hr>
                                <div class="mb-3"><label class="form-label">Correo Electrónico (Usuario)</label><input type="email" class="form-control" id="editCorreo" name="txtCorreo" required></div>
                                <div class="mb-3"><label class="form-label">Contraseña (Nueva)</label><input type="password" class="form-control" id="editContrasena" name="txtContrasena" placeholder="Dejar vacío para no cambiar"></div>
                            </div>
                            <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                                <button type="submit" class="btn btn-info text-white">Actualizar Datos</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
                            //es el boton modo noche
        <button id="modoNocheBtn" class="modo-noche-flotante">🌙</button>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<%= request.getContextPath()%>/Js/JsAdmin/ModoNoche-Sidebar.js"></script>

        <script>
                                            function cargarDatosEdicion(id, nombre, apellido, correo, telefono) {
                                                document.getElementById('editIdUsuario').value = id;
                                                document.getElementById('editNombre').value = nombre;
                                                document.getElementById('editApellido').value = apellido;
                                                document.getElementById('editCorreo').value = correo;
                                                document.getElementById('editTelefono').value = telefono;
                                                document.getElementById('editContrasena').value = "";
                                            }
        </script>
    </body>
</html>