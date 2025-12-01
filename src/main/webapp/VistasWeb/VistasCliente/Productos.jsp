<%@ include file="/proteger.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Productos Veterinaria PetCare</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet" />

        <style>
            /* ===== MODALES CARRITO / CONFIRMACIÓN ===== */
            /* ==== RESET Y FUENTE ==== */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            html, body {
                height: 100%;
                background-color: #ffffff;
                font-family: 'Poppins', sans-serif;
                color: #2c3e50;
                line-height: 1.6;
            }

            /* ==== NAVBAR ==== */
            .navbar {
                max-width: 100%;
                margin: 0 auto;
                padding: 0 30px;
                height: 80px;
                background-color: #ffffff;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                display: flex;
                align-items: center;
                justify-content: space-between;
                position: relative;
                z-index: 15;
            }
            .logo-container {
                display: flex;
                align-items: center;
                flex: 1;
            }
            .logo {
                height: 45px;
            }
            .hamburger {
                display: none;
                flex-direction: column;
                gap: 5px;
                cursor: pointer;
                z-index: 20;
                transition: transform 0.4s;
            }
            .hamburger span {
                width: 25px;
                height: 3px;
                background: #2c3e50;
                border-radius: 2px;
                transition: 0.4s;
            }
            .nav-links {
                display: flex;
                align-items: center;
                justify-content: space-between;
                flex: 2;
            }
            .center-links {
                display: flex;
                gap: 40px;
            }
            .center-links a {
                text-decoration: none;
                color: #2c3e50;
                font-family: 'Times New Roman', serif;
                font-size: 16px;
                transition: color 0.3s;
                padding: 0px 30px;
            }
            .center-links a:hover,
            .center-links a.active-link {
                color: #000;
                border-bottom: 2px solid #000 !important;
                font-weight: 600;
            }
            .buttons {
                display: flex;
                gap: 10px;
            }

            .btn {
                padding: 10px 25px;
                border-radius: 15px;
                font-family: 'Times New Roman', serif;
                font-size: 16px;
                text-align: center;
                text-decoration: none;
                transition: all 0.3s ease;
                box-shadow: 0 3px 5px rgba(0,0,0,0.2);
            }
            .perfil {
                background-color: #000 !important;
                color: #fff !important;
            }
            .perfil:hover {
                background-color: #222 !important;
                color: #fff !important;
            }

            /* ==== HERO SECTION ==== */
            .hero {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 40px;
                padding: 40px 20px;
                max-width: 1200px;
                margin: 50px auto;
                background: none;
            }
            .hero-img img {
                width: 350px;
                border-radius: 15px;
                box-shadow: 0 10px 25px rgba(58,175,169,0.4);
            }
            .hero-text {
                max-width: 600px;
            }
            .hero-text h2 {
                font-size: 2.8rem;
                color: #2c3e50;
                margin-bottom: 20px;
            }
            .hero-text p {
                font-size: 1.2rem;
                color: #556f7a;
            }

            /* ==== CATEGORÍAS ==== */
            .categorias {
                margin-bottom: 50px;
                max-width: 1200px;
                margin-left: auto;
                margin-right: auto;
            }
            .categorias h3 {
                text-align: center;
                margin-bottom: 25px;
                color: #2c3e50;
                font-size: 2rem;
            }
            .categorias-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
                gap: 30px;
                justify-items: center;
            }
            .categoria-card {
                background-color: #E6EFF1;
                border-radius: 15px;
                box-shadow: 0 8px 20px rgba(0,0,0,0.1);
                padding: 20px;
                width: 160px;
                text-align: center;
                cursor: pointer;
                transition: transform 0.3s ease;
            }
            .categoria-card:hover {
                transform: translateY(-8px);
                box-shadow: 0 12px 30px rgba(0,0,0,0.15);
            }
            .categoria-card img {
                width: 80px;
                margin-bottom: 15px;
            }
            .categoria-card h4 {
                color: #3aafa9;
                font-weight: 600;
            }

            /* ==== PRODUCTOS ==== */
            .productos {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 30px;
                max-width: 1200px;
                margin: 0 auto 10px auto;
            }
            .producto-card {
                background-color: #E6EFF1;
                border-radius: 15px;
                padding: 20px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                display: flex;
                flex-direction: column;
                align-items: center;
                transition: transform 0.3s ease;
                text-align: center;
            }
            .producto-card:hover {
                transform: translateY(-10px) scale(1.03);
                box-shadow: 0 15px 40px rgba(0,0,0,0.15);
            }
            .producto-img img {
                width: 100%;
                max-height: 180px;
                object-fit: contain;
                border-radius: 10px;
                margin-bottom: 15px;
            }
            .producto-card h3 {
                font-size: 1.3rem;
                margin-bottom: 10px;
                color: #2c3e50;
            }
            .producto-card p {
                color: #556f7a;
                margin-bottom: 10px;
            }
            .precio {
                font-weight: 700;
                font-size: 1.2rem;
                color: #3aafa9;
                margin-bottom: 15px;
            }
            .producto-card button {
                background-color: #3aafa9;
                color: white;
                border: none;
                padding: 10px 25px;
                border-radius: 25px;
                cursor: pointer;
                font-weight: 600;
                transition: background-color 0.3s ease;
            }
            .producto-card button:hover {
                background-color: #2c7a7b;
            }
            .btn-agregar { /* Mismo estilo que .producto-card button */
                background-color: #3aafa9;
                color: white;
                border: none;
                padding: 10px 25px;
                border-radius: 25px;
                cursor: pointer;
                font-weight: 600;
                transition: background-color 0.3s ease;
            }
            .btn-agregar:hover { /* Mismo estilo que .producto-card button:hover */
                background-color: #2c7a7b;
            }


            /* ==== CONSEJOS ==== */
            .consejos {
                padding: 80px 20px;
                background-color: #E6EFF1;
                padding: 40px 20px;
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                max-width: 900px;
                margin: 0 auto 10px auto;
              
            }
            .consejos h3 {
                text-align: center;
                margin-bottom: 25px;
                color: #2c3e50;
                font-size: 2rem;
            }
            .consejos ul {
                list-style: none;
                color: #556f7a;
                font-size: 1.1rem;
                padding-left: 20px;
            }
            .consejos ul li {
                margin-bottom: 20px;
                position: relative;
            }
            .consejos ul li::before {
                content: "✔";
                position: relative;
                left: 0;
                color: #3aafa9;
                font-weight: 700;
            }

            /* ==== TESTIMONIOS ==== */
            .testimonios {
                max-width: 900px;
                margin: 0 auto 5px auto;
            }
            .testimonios h3 {
                text-align: center;
                margin-bottom: 30px;
                color: #2c3e50;
                font-size: 2rem;
            }
            .testimonio-card {
                background-color: #E6EFF1;
                padding: 25px 30px;
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                font-style: italic;
                color: #444;
                position: relative;
                margin-bottom: 20px;
            }
            .testimonio-card span {
                display: block;
                margin-top: 15px;
                font-weight: 700;
                color: #3aafa9;
                text-align: right;
            }

            /* ==== FOOTER ==== */
            footer {
                padding-top: 200px;
                background-color: #2c3e50;
                color: #def2f1;
                padding: 30px 20px;
                text-align: center;
                font-size: 1rem;
            }

            .redes-sociales span {
                color: #def2f1;
                margin: 0 10px;
                font-weight: 600;
                background: #3a4a5d;
                border-radius: 50%;
                padding: 8px 15px;
                display: inline-block;
                font-size: 1rem;
                letter-spacing: 1px;
            }
            .redes-sociales span:hover {
                background: #3aafa9;
                color: #fff;
            }

            /* ==== SIDEBAR PERFIL ==== */
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
                transition: transform 0.3s ease;
                transform: translateX(100%);
            }
            .sidebar-perfil.active {
                display: block;
                transform: translateX(0);
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

            /* ==== OVERLAY PARA SIDEBAR PERFIL ==== */
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
                opacity: 1;
            }

            /* ===== MODALES CARRITO / CONFIRMACIÓN ===== */
            .modal {
                display: none;
                position: fixed;
                z-index: 10000;
                left: 0;
                top: 0;
                width: 100vw;
                height: 100vh;
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
                from {
                    transform: scale(0.93);
                    opacity: 0;
                }
                to   {
                    transform: scale(1);
                    opacity: 1;
                }
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
                left: 0;
                top: 0;
                width: 100vw;
                height: 100vh;
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
            .cerrar-modal {
                position: absolute;
                top: 1.3rem;
                right: 1.3rem;
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
                border: 1px solid #ccc;
                border-radius: 8px;
                padding: 10px;
                margin-bottom: 10px;
                display: flex;
                align-items: center;
                gap: 14px;
                font-size: 1.12rem;
            }
            #listaCarrito img {
                width: 54px;
                height: 54px;
                object-fit: cover;
                border-radius: 6px;
                margin-right: 8px;
            }
            .btn-finalizar {
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
                color: #fff;
                border: none;
                padding: .9rem 1.5rem;
                border-radius: 7px;
                cursor: pointer;
                font-weight: bold;
                font-size: 1.08rem;
                transition: background 0.2s;
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

            /* ===== BOTÓN MODO NOCHE FLOTANTE ===== */
            .modo-noche-flotante {
                position: fixed;
                bottom: 25px;
                right: 25px;
                width: 45px;
                height: 45px;
                border-radius: 50%;
                border: none;
                background-color: #111;
                color: #fff;
                font-size: 20px;
                cursor: pointer;
                box-shadow: 0 4px 10px rgba(0,0,0,0.3);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 3000;
                transition: background-color 0.3s ease,
                    transform 0.1s ease,
                    box-shadow 0.3s ease;
            }
            .modo-noche-flotante:hover {
                background-color: #000;
                transform: translateY(-1px);
                box-shadow: 0 6px 14px rgba(0,0,0,0.45);
            }


            /* ========= MODO NOCHE PARA ESTA PÁGINA ========= */

            /* Fondo general */
            body.modo-noche {
                background-color: #18191A !important; 
                color: #f5f5f5;
            }

            /* Navbar oscura */
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
            body.modo-noche .btn.perfil {
                background-color: #000 ;
                color: #fff ;
            }

            /* Hero productos */
            body.modo-noche .hero {
                background-color: #020617;
            }
            body.modo-noche .hero-text h2,
            body.modo-noche .hero-text p {
                color: #f9fafb;
            }

            /* Cards de productos */
            body.modo-noche .productos {
                /* 1. Aseguramos que el contenedor sea negro */
                background-color: #18191A !important;
                max-width: 100vw !important; /* O usar 100% si el padre tiene ancho completo */
                gap: 30px;
                padding: 150px; /* Añade un padding si es necesario para separar del borde de la pantalla */
            }
            body.modo-noche .producto-card {
                /* Fusionado de #242526 y #1f1f1f en la respuesta anterior */
                background-color: #1f1f1f !important;
                border: 1px solid #3a3b3c;
                color: #f1f1f1; 
                border-radius: 15px;
                padding: 20px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            }
            body.modo-noche .producto-card h4 {
                color: #f9fafb; /* Fusionado */
            }
            body.modo-noche .producto-card p {
                color: #e5e7eb; /* Fusionado */
            }
            body.modo-noche .producto-card .precio {
                color: #facc15; /* Fusionado de #facc15 y #f1f1f1 (se prioriza color de énfasis) */
            }
            body.modo-noche .producto-card img {
                filter: brightness(0.85); /* Tomado de #1f1f1f */
            }
            body.modo-noche .btn-agregar { /* Estilo unificado para botón */
                background-color: #333; /* Tomado de #1f1f1f */
                color: white; /* Tomado de #1f1f1f */
                border: 1px solid #555; /* Tomado de #1f1f1f */
            }
            body.modo-noche .btn-agregar:hover { /* Estilo unificado para botón */
                background-color: #444; /* Tomado de #1f1f1f */
            }

            /* Consejos y testimonios */
            body.modo-noche .consejos {
                background-color: #1e1e1e;
                color: #ffffff;
                border-radius: 10px;
                padding: 40px 20px; 
                box-shadow: 0 10px 30px rgba(0,0,0,0.5);

                /* Para que el contenedor ocupe todo el ancho de la página, como pediste antes */
                max-width: none !important; 
                margin-left: 0 !important;  
                margin-right: 0 !important;
            }
            
            body.modo-noche .consejos ul {
                padding-left: 0 !important; /* Fuerza el contenido a la izquierda */
                list-style: none;
                padding-left: 150px;
            }
            body.modo-noche .consejos h3,
            body.modo-noche .consejos li {
                color: #e5e7eb;
            }
            
            body.modo-noche .consejos ul li {
                color: #e5e7eb;
                margin-bottom: 20px;
                position: relative; 
                /* Agregamos padding IZQUIERDO al <li> para darle espacio al "tick" */
                padding-left: 180px;
            }
            
            body.modo-noche .consejos ul li::before {
                padding-left: 150px;
                content: "✔";
                position: absolute;
                /* Colocamos el ícono a 0px del padding-left del <li> */
                left: 0px; 
                color: #4c8bf5; /* Color azul de tu imagen */
                font-weight: 700;
                font-size: 1.1rem;
            }
            body.modo-noche .testimonios {
                /* Eliminamos el max-width para esta prueba y centramos si es necesario */
                max-width: none !important;
                margin-left: 0 !important;
                margin-right: 0px !important;
                padding-top: 50px; /* Separación superior */
                padding-bottom: 50px; /* Separación inferior */
                background-color: #18191A !important; /* Aseguramos que el fondo sea negro */
                
            }
            body.modo-noche .testimonios h3 {
                color: #f5f5f5; /* Aseguramos que el título sea blanco/claro */
                text-align: center;
            }
            body.modo-noche .testimonio-card {
                background-color: #1e1e1e;
                color: #f1f1f1;
                border-color: #333;
                /* Ajustes adicionales para la sombra de la imagen */
                box-shadow: 0 8px 20px rgba(0,0,0,0.4);
            }
            body.modo-noche .testimonio-card span {
                color: #aaa;
            }

            /* Footer */
            body.modo-noche footer {
                background-color: #020617;
                color: #9ca3af;
                max-width: none !important; 
                margin-left: 0 !important;
                margin-right: 0 !important;
                padding-left: 20px !important;
                padding-right: 20px !important;
            }
            body.modo-noche .redes-sociales span { /* Asegurar estilo de redes en footer */
                background: #1f2937;
                color: #fff;
            }
            body.modo-noche .redes-sociales span:hover {
                background: #3aafa9;
            }

            /* Sidebar perfil oscuro */
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

            /* Modales en modo noche */
            body.modo-noche .modal-content,
            body.modo-noche .modal-contenido {
                background-color: #242526;
                color: #f5f5f5;
                border: 1px solid #3a3b3c;
            }
            body.modo-noche .cerrar-modal {
                color: #ccc;
            }
            body.modo-noche .modal-btn { /* Botones modales */
                background: #5cb85c;
            }
            body.modo-noche .modal-btn:hover {
                background: #449d44;
            }
            body.modo-noche .btn-cancelar {
                background: #d9534f;
            }
            body.modo-noche .btn-cancelar:hover {
                background: #c9302c;
            }
            body.modo-noche .btn-finalizar {
                background: #2bb673;
            }
            body.modo-noche .btn-finalizar:hover {
                background: #228e59;
            }
            body.modo-noche .btn-regresar {
                background: #777;
            }
            body.modo-noche .btn-regresar:hover {
                background: #444;
            }
            body.modo-noche .btn-eliminar {
                background: #ff5555;
            }
            body.modo-noche .btn-eliminar:hover {
                background: #d22;
            }

            /* Botón flotante en modo noche */
            body.modo-noche .modo-noche-flotante {
                background-color: #f9fafb;
                color: #111827;
                box-shadow: 0 4px 10px rgba(255,255,255,0.3);
            }
            body.modo-noche .modo-noche-flotante:hover {
                background-color: #e0e0e0;
                box-shadow: 0 6px 14px rgba(255,255,255,0.45);
            }


            /* ==== RESPONSIVE ==== */
            @media (max-width: 900px) {
                .hamburger {
                    display: flex;
                }
                .nav-links {
                    flex-direction: column;
                    align-items: center;
                    background-color: #fff;
                    position: absolute;
                    top: 80px;
                    left: 0;
                    right: 0;
                    overflow: hidden;
                    max-height: 0;
                    transition: max-height 0.4s ease;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    z-index: 20;
                }
                .nav-links.active {
                    max-height: 600px;
                    padding: 20px 0;
                }
                .center-links {
                    flex-direction: column;
                    gap: 20px;
                    margin-bottom: 20px;
                }
                .buttons {
                    flex-direction: column;
                    gap: 15px;
                }
                .btn {
                    width: 220px;
                }
                .logo-container {
                    justify-content: center;
                    flex: none;
                    position: absolute;
                    left: 50%;
                    transform: translateX(-50%);
                }
                .hero {
                    flex-direction: column;
                    gap: 20px;
                    padding: 20px;
                }
                .hero-img img {
                    width: 100%;
                    max-width: 350px;
                }
                .hero-text {
                    max-width: 100%;
                    text-align: center;
                }
                .categorias-grid {
                    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
                    gap: 20px;
                }

                .consejos ul {
                    padding-left: 10px;
                }
                .testimonio-card {
                    padding: 20px;
                    margin: 0 10px 20px 10px;
                }

                .modo-noche-flotante {
                    bottom: 18px;
                    right: 18px;
                    width: 40px;
                    height: 40px;
                    font-size: 18px;
                }
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
            <a href="${pageContext.request.contextPath}/HistorialComprasServlet">Historial de compras/servicios</a>
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

        <!-- Botón flotante Modo Noche -->
        <button id="modoNocheBtn" class="modo-noche-flotante" aria-label="Cambiar a modo noche">🌙</button>

        <script>
            document.getElementById('hamburger').addEventListener('click', function () {
                this.classList.toggle('active');
                document.getElementById('nav-links').classList.toggle('active');
            });

            document.getElementById('verPerfilBtn').addEventListener('click', function () {
                document.getElementById('sidebarPerfil').classList.add('active');
                document.getElementById('sidebarOverlay').classList.add('active');
            });

            document.getElementById('sidebarOverlay').addEventListener('click', function () {
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

                fetch('${pageContext.request.contextPath}/CarritoServlet', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
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

            window.addEventListener('click', function (e) {
                if (e.target === modalConfirmar) {
                    modalConfirmar.style.display = 'none';
                }
            });
        </script>

        <!-- JS de modo noche compartido -->
        <script src="${pageContext.request.contextPath}/Js/ModoNocheIndex.js"></script>

    </body>
</html>
