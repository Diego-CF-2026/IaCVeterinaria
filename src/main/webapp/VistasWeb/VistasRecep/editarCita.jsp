<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Editar Cita - N° ${citaSeleccionada.idCita}</title>
    <link rel="stylesheet" href="ruta/a/tus/estilos.css">
</head>
<body>

    <div class="container mt-5">
        <h1>Editar Cita N° ${citaSeleccionada.idCita}</h1>
        
        <c:if test="${not empty sessionScope.mensaje}">
            <div class="alert alert-${sessionScope.tipoMensaje}" role="alert">
                ${sessionScope.mensaje}
            </div>
            <c:remove var="mensaje" scope="session"/>
            <c:remove var="tipoMensaje" scope="session"/>
        </c:if>

        <c:choose>
            <c:when test="${citaSeleccionada != null}">
                
                <form action="${pageContext.request.contextPath}/CitaServlet?accion=actualizar" method="POST">
                    
                    <input type="hidden" name="idCita" value="${citaSeleccionada.idCita}">
                    <input type="hidden" name="origen" value="global"> 

                    <div class="form-group mb-3">
                        <label for="idCliente">Cliente</label>
                        <select name="idCliente" id="idCliente" class="form-control" required>
                            <option value="">-- Seleccione un Cliente --</option>
                            <c:forEach var="cliente" items="${listaClientes}">
                                <option 
                                    value="${cliente.idCliente}" 
                                    <c:if test="${citaSeleccionada.idCliente == cliente.idCliente}">
                                        selected
                                    </c:if>
                                >
                                    ${cliente.nombre} <c:if test="${not empty cliente.apellido}">${cliente.nombre}</c:if> (ID: ${cliente.idCliente})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group mb-3">
                        <label for="idVeterinario">Veterinario</label>
                        <select name="idVeterinario" id="idVeterinario" class="form-control" required>
                            <option value="">-- Seleccione un Veterinario --</option>
                            <c:forEach var="veterinario" items="${listaVeterinarios}">
                                <option 
                                    value="${veterinario.idVeterinario}"
                                    <c:if test="${citaSeleccionada.idVeterinario == veterinario.idVeterinario}">
                                        selected
                                    </c:if>
                                >
                                    ${veterinario.nombre} ${veterinario.apellido}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group mb-3">
                        <label for="fecha">Fecha</label>
                        <fmt:formatDate var="fechaFormato" value="${citaSeleccionada.fecha}" pattern="yyyy-MM-dd" />
                        <input type="date" class="form-control" id="fecha" name="fecha" 
                               value="${fechaFormato}" required>
                    </div>

                    <div class="form-group mb-3">
                        <label for="hora">Hora</label>
                        <fmt:formatDate var="horaFormato" value="${citaSeleccionada.hora}" pattern="HH:mm" />
                        <input type="time" class="form-control" id="hora" name="hora" 
                               value="${horaFormato}" required>
                    </div>
                    
                    <div class="form-group mb-3">
                        <label for="motivo">Motivo</label>
                        <textarea class="form-control" id="motivo" name="motivo" rows="3" required>${citaSeleccionada.motivo}</textarea>
                    </div>
                    
                    <div class="form-group mb-4">
                        <label for="estado">Estado</label>
                        <select name="estado" id="estado" class="form-control" required>
                            <option value="Pendiente" <c:if test="${citaSeleccionada.estadoNombre == 'Pendiente'}">selected</c:if>>Pendiente</option>
                            <option value="Completado" <c:if test="${citaSeleccionada.estadoNombre == 'Completado'}">selected</c:if>>Completada</option>
                            <option value="Cancelado" <c:if test="${citaSeleccionada.estadoNombre == 'Cancelado'}">selected</c:if>>Cancelada</option>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=verGlobal" class="btn btn-secondary">Cancelar</a>

                </form>
            </c:when>
            <c:otherwise>
                <div class="alert alert-danger">Cita no encontrada.</div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>