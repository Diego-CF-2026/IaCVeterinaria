<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Productos Veterinaria PetCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productos.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />
    <style>
        .modal {
            display: none;
            position: fixed;
            z-index: 10000;
            left: 0; top: 0; width: 100vw; height: 100vh;
            background: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }
        .modal.active {
            display: flex;
        }
        .modal-content {
            background: #fff;
            padding: 2.5rem 2rem;
            border-radius: 18px;
            max-width: 500px;
            width: 95%;
            box-shadow: 0 4px 24px rgba(0,0,0,0.22);
            text-align: center;
            position: relative;
            animation: modalFadeIn 0.22s;
        }
        @keyframes modalFadeIn {
            from { transform: scale(0.93); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }
        .modal-title {
            margin-bottom: 1.5rem;
            font-size: 1.6rem;
            color: #333;
        }
        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 2rem;
        }
        .modal-btn {
            background: #5cb85c; 
            color: #fff;
            border: none;
            padding: .9rem 1.5rem;
            border-radius: 7px;
            cursor: pointer;
            font-weight: bold;
            font-size: 1.08rem;
            transition: background 0.2s;
        }
        .modal-btn:hover {
            background: #449d44;
        }
        .btn-cancelar {
            background: #d9534f; 
        }
        .btn-cancelar:hover {
            background: #c9302c;
        }

        .modal-carrito {
            display: none;
            position: fixed;
            z-index: 9999;
            left: 0; top: 0; width: 100vw; height: 100vh;
            background: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }
        .modal-carrito.active {
            display: flex;
        }
        .modal-contenido {
            background: #fff;
            padding: 2.5rem 2rem;
            border-radius: 18px;
            max-width: 600px;
            width: 95%;
            box-shadow: 0 4px 24px rgba(0,0,0,0.22);
            position: relative;
            animation: modalFadeIn 0.22s;
        }
        @keyframes modalFadeIn {
            from { transform: scale(0.93); opacity: 0; }
            to { transform: scale(1); opacity: 1; }
        }
        .cerrar-modal {
            position: absolute;
            top: 1.3rem; right: 1.3rem;
            font-size: 2.1rem;
            color: #888;
            cursor: pointer;
            font-weight: bold;
        }
        #listaCarrito {
            list-style: none;
            padding: 0;
            margin-bottom: 1.5rem;
            max-height: 320px;
            overflow-y: auto;
        }
        #listaCarrito li {
            margin-bottom: .9rem;
            border-bottom: 1px solid #eee;
            padding-bottom: .7rem;
            display: flex;
            align-items: center;
            gap: 14px;
            font-size: 1.12rem;
        }
        #listaCarrito li {
            margin-bottom: .9rem;
            border-bottom: 1px solid #eee;
            padding-bottom: .7rem;
            display: flex;
            align-items: center;
            gap: 14px;
            font-size: 1.12rem;
            border: 1px solid #ccc;
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 10px;
        }

        #listaCarrito img {
            width: 54px;
            height: 54px;
            object-fit: cover;
            border-radius: 6px;
            margin-right: 8px;
        }
        .btn-finalizar, .btn-regresar {
            background: #2bb673;
            color: #fff;
            border: none;
            padding: .9rem 1.5rem;
            border-radius: 7px;
            cursor: pointer;
            font-weight: bold;
            font-size: 1.08rem;
            margin-right: 1rem;
            transition: background 0.2s;
        }
        .btn-finalizar:hover {
            background: #228e59;
        }
        .btn-regresar {
            background: #777;
        }
        .btn-regresar:hover {
            background: #444;
        }
        .btn-eliminar {
            background: #ff5555;
            border: none;
            color: #fff;
            padding: 0.35rem 0.8rem;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1.1rem;
            margin-left: auto;
            transition: background 0.2s;
        }
        .btn-eliminar:hover {
            background: #d22;
        }
        .botones-carrito {
            display: flex;
            justify-content: flex-end;
            gap: 1rem;
        }
        
    </style>
</head>
<body>

<!-- Barra de Navegación -->
<nav class="navbar">
    <div class="logo-container">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo"/>
        </a>
    </div>
    <div class="hamburger" id="hamburger" aria-label="Menú">
        <span></span><span></span><span></span>
    </div>
    <div class="nav-links" id="nav-links">
        <div class="center-links">
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp" id="link-nosotros">Nosotros</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp" id="link-servicios">Servicios</a>
            <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente" id="link-productos" class="active-link">Productos</a>
            <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp" id="link-contacto">Contacto</a>
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


<section class="hero container">
    <div class="hero-img">
        <img src="${pageContext.request.contextPath}/Recursos/perroproducto.png" alt="Mascotas felices" />
    </div>
    <div class="hero-text">
        <h2>Productos para el cuidado integral de tus mascotas</h2>
        <p>Alimentos, medicamentos, accesorios y mucho más para perros, gatos y otras mascotas.</p>
    </div>
</section>
    
    
<section class="productos container">
    <c:forEach var="p" items="${productos}">
        <div class="producto-card">
            <div class="producto-img">
                <img src="${pageContext.request.contextPath}/${p.imagen}"
                     alt="${p.nombreProducto}"/>
            </div>
            <h4>${p.nombreProducto}</h4>
            <p>${p.descripcion}</p>
            <span class="precio">S/. ${p.precio}</span>
            <button type="button" class="btn-agregar"
                    data-id="${p.idProducto}"
                    data-nombre="${p.nombreProducto}"
                    data-precio="${p.precio}"
                    data-img="${pageContext.request.contextPath}/${p.imagen}">
                Agregar al carrito
            </button>
        </div>
    </c:forEach>
</section>  

<!-- MODAL PRINCIPAL -->
<div id="modalConfirmar" class="modal">
    <div class="modal-content">
        <h2>Agregar <span id="nombreProducto"></span></h2>
        <img id="imgProducto" src="" alt="" style="width:100px; height:100px; margin:10px auto; display:block;">
        <p>Precio: S/. <span id="precioProducto"></span></p>
        <label for="cantidadProducto">Cantidad:</label>
        <input type="number" id="cantidadProducto" min="1" value="1"
               style="width:80px; margin:10px auto; display:block;">
        <div class="modal-buttons">
            <button class="modal-btn btn-ok" id="confirmarAgregar">Agregar</button>
            <button class="modal-btn btn-cancelar" id="cancelarAgregar">Cancelar</button>
        </div>
    </div>
</div>

<!-- Modal éxito -->
<div id="modalExito" class="modal">
    <div class="modal-content">
        <h3>✅ Producto agregado al carrito</h3>
        <button class="modal-btn btn-ok" id="cerrarExito">Aceptar</button>
    </div>
</div>


<section class="consejos container">
    <h3>Consejos para el cuidado de tu mascota</h3>
    <ul>
        <li> Realiza chequeos veterinarios periódicos para prevenir enfermedades.</li>
        <li> Mantén una alimentación balanceada según la edad y raza de tu mascota.</li>
        <li> Proporciona espacios de juego y ejercicio para mantenerlos activos y felices.</li>
        <li> Vacuna y desparasita regularmente para evitar parásitos y enfermedades.</li>
        <li> Ofrece cariño y atención para fortalecer el vínculo con tu mascota.</li>
    </ul>
</section>


<section class="testimonios container">
    <h3>Lo que dicen nuestros clientes</h3>
    <div class="testimonio-card">
        <p>"Excelente atención y productos de calidad. Mi perro está feliz con su alimento nuevo."</p>
        <span>- María G.</span>
    </div>
    <div class="testimonio-card">
        <p>"Muy buena variedad de juguetes y accesorios. El personal siempre muy amable."</p>
        <span>- Juan P.</span>
    </div>
    <div class="testimonio-card">
        <p>"Los medicamentos antipulgas funcionan perfecto y a buen precio. Recomendado."</p>
        <span>- Laura R.</span>
    </div>
</section>

<footer>
    <div class="container footer-container">
        <p>© 2025 Veterinaria PetCare - Todos los derechos reservados</p>
        <p>Contacto: info@veterinariapetcare.com | Tel: +56 9 1234 5678</p>
        <div class="redes-sociales">
            <span>Facebook</span>
            <span>Instagram</span>
            <span>Twitter</span>
        </div>
    </div>
</footer>

<script>

    document.getElementById('hamburger').addEventListener('click', function() {
        this.classList.toggle('active');
        document.getElementById('nav-links').classList.toggle('active');
    });

    document.getElementById('verPerfilBtn').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
    });

    document.getElementById('sidebarOverlay').addEventListener('click', function() {
        document.getElementById('sidebarPerfil').classList.remove('active');
        this.classList.remove('active');
    });

    const modal = document.getElementById('modalConfirmar');
    const btnCancelar = document.getElementById('cancelarAgregar');
    const btnConfirmar = document.getElementById('confirmarAgregar');

    let productoSeleccionado = {};

    document.querySelectorAll('.btn-agregar').forEach(btn => {
        btn.addEventListener('click', () => {
            productoSeleccionado = {
                id: btn.dataset.id,
                nombre: btn.dataset.nombre,
                precio: btn.dataset.precio,
                img: btn.dataset.img
            };
            document.getElementById('nombreProducto').textContent = productoSeleccionado.nombre;
            document.getElementById('precioProducto').textContent = productoSeleccionado.precio;
            document.getElementById('imgProducto').src = productoSeleccionado.img;

            modal.style.display = 'flex';
        });
    });

    btnCancelar.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    btnConfirmar.addEventListener('click', () => {
        const cantidad = parseInt(document.getElementById('cantidadProducto').value) || 1;

        console.log("DEBUG → Enviando:", {
            idProducto: productoSeleccionado.id,
            cantidad: cantidad
        });

        fetch('${pageContext.request.contextPath}/CarritoServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({
                accion: 'agregar',
                idProducto: productoSeleccionado.id,
                cantidad: cantidad
            })
        })
        .then(res => {
            if (!res.ok) {
                throw new Error("Error HTTP: " + res.status);
            }
            return res.json();
        })
        .then(data => {
            if (data.exito) {
                document.getElementById('modalExito').style.display = 'flex';
            } else {
                alert(data.mensaje || "Error al agregar al carrito");
            }
        })
        .catch(err => console.error("Error en fetch:", err));

        modal.style.display = 'none';
    });

    document.getElementById('cerrarExito').addEventListener('click', () => {
        document.getElementById('modalExito').style.display = 'none';
    });

    window.addEventListener('click', function(e) {
        if (e.target == modalConfirmar) {
            cerrarModal(modalConfirmar);
        }
    });
</script>

</body>
</html>
