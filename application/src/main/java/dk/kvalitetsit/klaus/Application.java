package dk.kvalitetsit.klaus;


import dk.kvalitetsit.klaus.configuration.JacksonConfiguration;
import dk.kvalitetsit.klaus.configuration.MainConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		JacksonConfiguration.class,
		MainConfiguration.class
})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
