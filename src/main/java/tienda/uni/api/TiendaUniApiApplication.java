package tienda.uni.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TiendaUniApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaUniApiApplication.class, args);
    }

}
