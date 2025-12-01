package Controladores;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;


@ApplicationPath("resources")
public class JakartaRestConfiguration extends Application {
    // Clase vacía intencional: la configuración se hace mediante la anotación.
    // Puedes registrar recursos manualmente en el futuro sobrescribiendo getClasses() o getSingletons()
}
