/* -------------------- MODO NOCHE -------------------- */

document.addEventListener("DOMContentLoaded", () => {
    // 1. Obtener elementos de la interfaz
    const modoNocheBtn = document.getElementById('modoNocheBtn');
    if (!modoNocheBtn) return; // Salir si el botón principal no existe

    const logo = document.getElementById('logo');
    const logoAdmin = document.getElementById('logoAdmin');
    const logoSeleccionInicial = document.getElementById('logoSeleccionInicial');
    const logoModalEquipo = document.getElementById('logoModalEquipo');
    const logoRegistro = document.getElementById('logoRegistro');
    const iconoUsuario = document.getElementById('iconoUsuario');
    const imagenesModal = document.querySelectorAll('.modal img');
    
    // 2. Función genérica para cambiar la fuente (src) de una imagen
    function actualizarImagen(elemento, claro, oscuro, activado) {
        if (elemento) {
            const nuevaSrc = activado ? oscuro : claro;
            // Solo actualiza si la fuente es diferente para evitar recargas innecesarias
            if (elemento.getAttribute('src') !== nuevaSrc) { 
                elemento.setAttribute('src', nuevaSrc);
            }
        }
    }

    // 3. Función principal para aplicar el modo
    function aplicarModo(activado) {
        // Toggle de clase en el body y actualización del texto del botón
        document.body.classList.toggle('modo-noche', activado);
        modoNocheBtn.textContent = activado ? '☀️' : '🌙';
        
        // Guardar la preferencia en el almacenamiento local
        localStorage.setItem('modo-noche', activado ? 'activado' : 'desactivado');

        // Actualizar imágenes que usan la función genérica
        // Importante: No cambies 'w' ni 'v' según tu restricción.
        actualizarImagen(logo, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
        actualizarImagen(logoAdmin, '../../Recursos/Logo.png', '../../Recursos/Logo-oscuro.png', activado);
        actualizarImagen(logoSeleccionInicial, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
        actualizarImagen(logoModalEquipo, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
        actualizarImagen(logoRegistro, 'Recursos/Logo.png', 'Recursos/Logo-oscuro.png', activado);
        actualizarImagen(iconoUsuario, 'Recursos/IconUser.svg', 'Recursos/IconUser-dark.svg', activado);

        // Actualizar imágenes dentro de modales usando iteración y replace()
        imagenesModal.forEach(imagen => {
            if (!imagen.src) return; // Saltar si la imagen no tiene fuente

            if (activado && imagen.src.includes('IconUser.svg') && !imagen.src.includes('IconUser-dark.svg')) {
                // Modo oscuro: cambiar a la versión dark
                imagen.src = imagen.src.replace('IconUser.svg', 'IconUser-dark.svg');
            } else if (!activado && imagen.src.includes('IconUser-dark.svg')) {
                // Modo claro: cambiar a la versión normal
                imagen.src = imagen.src.replace('IconUser-dark.svg', 'IconUser.svg');
            }
        });
    }
    
    // 4. Inicialización: Cargar modo guardado SOLO para el texto del botón
    // La clase del body ya fue aplicada instantáneamente en el JSP (previene parpadeo).
    const modoGuardado = localStorage.getItem('modo-noche');
    const activadoInicial = modoGuardado === 'activado';
    modoNocheBtn.textContent = activadoInicial ? '☀️' : '🌙';
    
    // 5. Evento de click para cambiar el modo
    modoNocheBtn.addEventListener('click', () => {
        // El nuevo estado es el opuesto al estado actual
        const nuevoEstado = !document.body.classList.contains('modo-noche');
        aplicarModo(nuevoEstado);
    });
});