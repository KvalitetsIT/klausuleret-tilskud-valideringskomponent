package dk.kvalitetsit.itukt;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /app og /app/ -> index.html
        registry.addViewController("/app").setViewName("forward:/app/index.html");
        registry.addViewController("/app/").setViewName("forward:/app/index.html");

        // TODO: Vi mangler noget understøttelse af Angular-routes
  }
}
