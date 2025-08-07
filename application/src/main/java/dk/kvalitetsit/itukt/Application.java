package dk.kvalitetsit.itukt;


import dk.kvalitetsit.itukt.configuration.JacksonConfiguration;
import dk.kvalitetsit.itukt.configuration.MainConfiguration;
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
