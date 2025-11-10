package Controladores;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configura Jakarta RESTful Web Services (JAX-RS) para la aplicación.
 * Esta clase habilita el contexto base para todos los endpoints REST.
 * 
 * @author Juneau
 * 
 * Explicación detallada:
 * 
 * La anotación @ApplicationPath("resources") define la ruta raíz
 * bajo la cual estarán disponibles todos los servicios REST de la aplicación.
 * Por ejemplo, si un recurso REST se mapea como /ProductoResource, entonces
 * la URL completa sería /contextoApp/resources/ProductoResource.
 * 
 * Esta clase extiende Application, que es la clase base de JAX-RS.
 * Al extender Application, el contenedor de servlets reconoce automáticamente
 * que esta aplicación tiene endpoints REST y los configura.
 * 
 * No es necesario agregar métodos aquí a menos que quieras registrar
 * manualmente recursos o proveedores específicos. En su forma básica,
 * basta con la anotación @ApplicationPath.
 * 
 * Beneficios:
 * - Centraliza la configuración REST.
 * - Permite cambiar el path raíz de los servicios REST de manera sencilla.
 * - Compatible con cualquier implementación JAX-RS (Jakarta EE, Jersey, RESTEasy, etc.)
 * 
 * Notas adicionales:
 * - Todos los recursos REST deben estar anotados con @Path.
 * - Esta clase se inicializa automáticamente al desplegar la aplicación en el servidor.
 * - Puedes agregar filtros, interceptores o proveedores REST en clases separadas.
 * - Mantener esta clase simple es recomendable para no complicar la inicialización.
 * - Se puede usar para documentar rutas base de la API y versiones futuras.
 * - Sirve como punto central de configuración si decides usar autenticación a nivel de aplicación.
 * - Puede coexistir con otros servlets tradicionales como LoginServlet, ProductoServlet, etc.
 * - No afecta el funcionamiento de JSP o servlets normales, solo REST.
 * - Permite estructurar tu API de manera modular y ordenada.
 * - Es parte de la migración moderna de javax.ws.rs a jakarta.ws.rs.
 */
@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {
    // Clase vacía intencional: la configuración se hace mediante la anotación.
    // Puedes registrar recursos manualmente en el futuro sobrescribiendo getClasses() o getSingletons()
}
