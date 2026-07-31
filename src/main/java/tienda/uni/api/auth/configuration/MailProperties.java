package tienda.uni.api.auth.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public record MailProperties(
        String host,
        int port,
        String username,
        String password,
        Smtp smtp,
        String debug
) {

    public record Smtp(
            boolean auth,
            Starttls starttls
    ) {

        public record Starttls(
                boolean enable,
                boolean required
        ) {
        }
    }
}
