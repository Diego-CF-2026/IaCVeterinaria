/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Seguridad;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

        // -------- CSP optimizada para ZAP + reCAPTCHA + CDN --------
        res.setHeader("Content-Security-Policy",
            "default-src 'self'; "
          + "base-uri 'self'; "
          + "form-action 'self' https://www.google.com https://www.recaptcha.net; "
          + "object-src 'none'; "
          + "frame-ancestors 'self'; " // Ayuda con Clickjacking (además de X-Frame-Options)

          // Scripts permitidos - ELIMINAR 'unsafe-eval'
          + "script-src 'self' "
          + "https://www.google.com "
          + "https://www.gstatic.com "
          + "https://www.recaptcha.net "
          + "https://cdn.jsdelivr.net "
          + "https://cdnjs.cloudflare.com "
          + "'unsafe-inline'; " // Idealmente, eliminar esto también

          // Styles permitidos - SE MANTIENE 'unsafe-inline' (común en libs)
          + "style-src 'self' "
          + "https://fonts.googleapis.com "
          + "https://cdn.jsdelivr.net "
          + "https://cdnjs.cloudflare.com "
          + "'unsafe-inline'; "

          // Fonts
          + "font-src 'self' https://fonts.gstatic.com data:; "

          // Imágenes
          + "img-src 'self' https: data: blob:; "

          // Frames (reCAPTCHA)
          + "frame-src 'self' https://www.google.com https://www.gstatic.com https://www.recaptcha.net; "

          // AJAX / fetch
          + "connect-src 'self' https://www.google.com https://www.gstatic.com https://www.recaptcha.net;"
        );

        // NUEVA LINEA: Soluciona Cross-Domain Misconfiguration
        res.setHeader("Cross-Origin-Resource-Policy", "same-origin"); 
        // o si necesitas que subdominios accedan, puedes usar "same-site"

        res.setHeader("X-Frame-Options", "SAMEORIGIN");
        res.setHeader("X-XSS-Protection", "1; mode=block");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setHeader("Referrer-Policy", "no-referrer-when-downgrade");

        chain.doFilter(request, response);
    }
}
