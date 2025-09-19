package dk.kvalitetsit.itukt;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Konfiguration for at serve en Single Page Application (SPA) med Angular.
 * Håndterer både direkte adgang til statiske ressourcer og routing inden for SPA'en.
 */
@Configuration
public class SpaResourceConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // app-mapninger til index.html
        registry.addViewController("/app").setViewName("forward:/app/index.html");
        registry.addViewController("/app/").setViewName("forward:/app/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/app/**")
                .addResourceLocations("classpath:/static/app/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requested = location.createRelative(resourcePath);

                        // 1) Findes filen? Servér den.
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }

                        // 2) Manglende "asset" med punktum -> send 404 tilbage
                        if (resourcePath.contains(".")) {
                            return null;
                        }

                        // 3) Ellers er det en Angular-route -> send over i index.html
                        return new ClassPathResource("/static/app/index.html");
                    }
                });
    }
}
