<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="Modelo.Carrito"%>
<%@page import="Modelo.DetalleCarrito"%>
<%@page import="Modelo.Producto"%>
<%@page import="Modelo.TipoDePago"%>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Historial de Compras - Veterinaria</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/accesorios.css">
  <link href="https://fonts.googleapis.com/css?family=Poppins:400,600&display=swap" rel="stylesheet" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>

    .historial-container {
      max-width: 700px;
      margin: 40px auto 0 auto;
      background: #fff;
      border-radius: 18px;
      box-shadow: 0 4px 24px rgba(0,0,0,0.10);
      padding: 2.5rem 2.2rem 2rem 2.2rem;
    }
    .historial-container h2 {
      text-align: center;
      margin-bottom: 1.8rem;
      font-size: 2rem;
      font-weight: 700;
      color: #2bb673;
    }
    .historial-lista {
      list-style: none;
      padding: 0;
      margin: 0;
    }
    .historial-lista li {
      background: #f6f6f6;
      margin-bottom: 15px;
      border-radius: 9px;
      padding: 1.1rem 1.4rem;
      font-size: 1.13rem;
      color: #333;
      font-weight: 500;
      box-shadow: 0 1px 4px rgba(0,0,0,0.05);
      display: flex;
      align-items: center;
      gap: 12px;
    }
    .historial-lista li .icon {
      color: #2bb673;
      font-size: 1.3em;
    }
    .btn-regresar {
      background: #777;
      color: #fff;
      border: none;
      padding: .7rem 1.3rem;
      border-radius: 7px;
      cursor: pointer;
      font-weight: 600;
      font-size: 1rem;
      margin-top: 1.2rem;
      transition: background 0.2s;
      display: block;
      margin-left: auto;
      margin-right: auto;
    }
    .btn-regresar:hover {
      background: #444;
    }

    .sidebar-perfil {
      display: none;
      position: fixed;
      top: 0; right: 0;
      width: 280px;
      height: 100%;
      background-color: #fff;
      box-shadow: -2px 0 5px rgba(0,0,0,0.3);
      z-index: 2000;
      padding-top: 50px;
      overflow-y: auto;
    }
    .sidebar-perfil.active {
      display: block;
    }
    .sidebar-perfil h2 {
      margin: 0 0 20px 20px;
      font-weight: 600;
      font-size: 22px;
    }
    .sidebar-perfil a {
      display: block;
      padding: 15px 25px;
      color: #333;
      text-decoration: none;
      border-bottom: 1px solid #eee;
      font-size: 16px;
      transition: background-color 0.2s;
    }
    .sidebar-perfil a:hover {
      background-color: #f0f0f0;
    }
    #sidebarOverlay {
      display: none;
      position: fixed;
      top: 0; left: 0;
      width: 100vw; height: 100vh;
      background-color: rgba(0,0,0,0.4);
      z-index: 1500;
    }
    #sidebarOverlay.active {
      display: block;
    }
    .center-links a.active-link {
      border-bottom: 2px solid #000;
      font-weight: 600;
    }
    
    .modal {
    display: none; 
    position: fixed;
    z-index: 3000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0,0,0,0.4); 
    justify-content: center;
    align-items: center;    
  }

  .modal-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 12px;
    width: 400px;
    max-width: 90%;
    box-shadow: 0 5px 15px rgba(0,0,0,0.3);
    animation: fadeIn 0.3s ease;
  }

  .modal-header {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 15px;
    color: #2bb673;
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    gap: 10px;
  }

  .modal-footer button {
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 600;
  }

  .modal-footer button[type="button"] {
    background: #ccc;
  }

  .modal-footer button[type="submit"] {
    background: #2bb673;
    color: white;
  }

  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(-20px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  </style>
</head>
<body>

  <!-- NAVBAR -->
  <nav class="navbar">
    <div class="logo-container">
      <a href="${pageContext.request.contextPath}/index.jsp">
        <img src="${pageContext.request.contextPath}/Recursos/Logo.png" alt="Logo" class="logo" />
      </a>
    </div>
    <div class="nav-links">
      <div class="center-links">
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Nosotros.jsp">Nosotros</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/servicios.jsp">Servicios</a>
        <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente">Productos</a>
        <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/Contacto.jsp">Contacto</a>
      </div>
      <div class="buttons">
        <a href="javascript:void(0)" class="btn perfil" id="verPerfilBtn">Ver perfil</a>
      </div>    
    </div>
    <div class="hamburger" id="hamburger-menu">
      <span></span><span></span><span></span>
    </div>
  </nav>

  <!-- HISTORIAL -->
  <div class="historial-container">
    <h2>Historial de Compras</h2>

    <%
      List<Carrito> historial = (List<Carrito>) request.getAttribute("historialCompras");
      List<TipoDePago> tiposPago = (List<TipoDePago>) request.getAttribute("tiposPago"); // viene del servlet
      if (historial != null && !historial.isEmpty()) {
        for (Carrito carrito : historial) {
    %>

      <table>
        <thead>
          <tr>
            <th>Producto</th><th>Cantidad</th><th>Precio Unitario</th><th>Subtotal</th>
          </tr>
        </thead>
        <tbody>
          <%
            if (carrito.getDetalles() != null) {
              for (DetalleCarrito det : carrito.getDetalles()) {
                Producto p = det.getProducto();
          %>
            <tr>
              <td><%= (p != null ? p.getNombreProducto() : "Producto ID " + det.getIdProducto()) %></td>
              
              
            <td>
              <form action="${pageContext.request.contextPath}/ActualizarCantidadServlet" method="post" style="display:inline;">
                <input type="hidden" name="idDetalleCarrito" value="<%= det.getIdDetalleCarrito() %>">
                <input type="hidden" name="idCarrito" value="<%= carrito.getIdCarrito() %>">
                <input type="hidden" name="accion" value="restar">
                <button type="submit" style="background:none;border:none;color:blue;cursor:pointer;">➖</button>
              </form>

              <%= det.getCantidadProducto() %>

              <form action="${pageContext.request.contextPath}/ActualizarCantidadServlet" method="post" style="display:inline;">
                <input type="hidden" name="idDetalleCarrito" value="<%= det.getIdDetalleCarrito() %>">
                <input type="hidden" name="idCarrito" value="<%= carrito.getIdCarrito() %>">
                <input type="hidden" name="accion" value="sumar">
                <button type="submit" style="background:none;border:none;color:green;cursor:pointer;">➕</button>
              </form>
            </td>
              
              
              
              <td>S/ <%= (p != null ? p.getPrecio() : "0.00") %></td>
              <td>S/ <%= (p != null ? p.getPrecio().multiply(new java.math.BigDecimal(det.getCantidadProducto())) : "0.00") %></td>
              
              <!-- 🔹 Botón eliminar -->
              <td>
                  <form action="${pageContext.request.contextPath}/EliminarProductoCarritoServlet" method="post" style="display:inline;">
                    <input type="hidden" name="idDetalleCarrito" value="<%= det.getIdDetalleCarrito() %>">
                    <input type="hidden" name="idCarrito" value="<%= carrito.getIdCarrito() %>">
                    <button type="submit" style="background:none;border:none;color:red;cursor:pointer;font-weight:bold;">
                      ❌
                    </button>
                  </form>
              </td>
            </tr>
          <%
              }
            }
          %>
          <tr>
            <td colspan="3" style="text-align:right;font-weight:bold;">Total:</td>
            <td style="font-weight:bold;">S/ <%= carrito.getTotal() %></td>
          </tr>
        </tbody>
      </table>

      <button class="btn-confirmar" onclick="abrirModal(<%= carrito.getIdCarrito() %>, <%= carrito.getTotal() %>)">
        Confirmar compra
      </button>

    <%
        }
      } else {
    %>
      <p style="text-align:center;color:#888;">No hay compras registradas.</p>
    <%
      }
    %>

    <button class="btn-regresar" onclick="window.location.href='${pageContext.request.contextPath}/ProductoServlet?accion=listarCliente'">
      Regresar a productos
    </button>
  </div>    

  <div id="modalCompra" class="modal">
    <div class="modal-content">
      <div class="modal-header">Confirmar compra</div>
      <form action="${pageContext.request.contextPath}/ConfirmarCompraServlet" method="post">
        <input type="hidden" id="idCarrito" name="idCarrito">
        <p><b>Total:</b> S/ <span id="montoTotal"></span></p>
        <label>Método de pago:</label>
        <select id="idPago" name="idPago">
          <option value="">-- Selecciona --</option>
          <%
            if (tiposPago != null) {
              for (TipoDePago tp : tiposPago) {
          %>
                <option value="<%= tp.getIdPago() %>"><%= tp.getNombrePago() %></option>
          <%
              }
            }
          %>
        </select>
        <div class="modal-footer">
          <button type="button" onclick="cerrarModal()">Cancelar</button>
          <button type="submit">Confirmar</button>
        </div>
      </form>
    </div>
  </div>  
        
    <div id="modalExito" class="modal">
      <div class="modal-content">
        <div class="modal-header">✅ Compra exitosa</div>
        <p>Tu compra ha sido confirmada correctamente.</p>
        <div class="modal-footer">
          <button type="button" onclick="cerrarModalExito()">Cerrar</button>
        </div>
      </div>
    </div>

  <div id="sidebarPerfil" class="sidebar-perfil" role="dialog" aria-modal="true" aria-labelledby="perfilTitle">
    <h2 id="perfilTitle">Mi Perfil</h2>
    <a href="${pageContext.request.contextPath}/VistasWeb/VistasCliente/MiPerfil.jsp">Mi perfil</a>
    <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
    <a href="${pageContext.request.contextPath}/UsuarioMisCitasServlet">Citas agendadas</a>
    <a href="${pageContext.request.contextPath}/LogoutServlet">Cerrar sesión</a>
  </div>

  <div id="sidebarOverlay"></div>

  <script>
    document.getElementById('verPerfilBtn').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.add('active');
      document.getElementById('sidebarOverlay').classList.add('active');
    });
    document.getElementById('sidebarOverlay').addEventListener('click', function() {
      document.getElementById('sidebarPerfil').classList.remove('active');
      this.classList.remove('active');
    });

    document.getElementById('hamburger-menu').onclick = function () {
      document.querySelector('.nav-links').classList.toggle('active');
    };

    window.addEventListener('DOMContentLoaded', () => {
      const navLinks = document.querySelectorAll('.center-links a');
      const currentPath = window.location.pathname;
      navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
          link.classList.add('active-link');
        }
      });
    });
    
    function abrirModal(idCarrito, total) {
      document.getElementById("idCarrito").value = idCarrito;
      document.getElementById("montoTotal").innerText = total;
      document.getElementById("modalCompra").style.display = "flex";
    }

    function cerrarModal() {
      document.getElementById("modalCompra").style.display = "none";
    }
    
    function abrirModalExito() {
        document.getElementById("modalExito").style.display = "flex";
      }

    function cerrarModalExito() {
        document.getElementById("modalExito").style.display = "none";
      }
    
  </script>

  <%
    Boolean compraExitosa = (Boolean) request.getAttribute("compraExitosa");
    if (compraExitosa != null && compraExitosa) {
  %>
    <script>
      window.addEventListener("DOMContentLoaded", () => {
        abrirModalExito();
      });
    </script>
  <%
    }
  %>
</body>
</html>
