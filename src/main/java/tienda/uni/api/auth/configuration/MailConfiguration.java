package tienda.uni.api.auth.configuration;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    private final MailProperties mailProperties;

    public MailConfiguration(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.host());
        mailSender.setPort(mailProperties.port());
        mailSender.setUsername(mailProperties.username());
        mailSender.setPassword(mailProperties.password());
        mailSender.setJavaMailProperties(buildJavaMailProperties());

        return mailSender;
    }

    private Properties buildJavaMailProperties() {
        MailProperties.Smtp smtp = mailProperties.smtp();
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtp.auth());
        props.put("mail.smtp.starttls.enable", smtp.starttls().enable());
        props.put("mail.smtp.starttls.required", smtp.starttls().required());
        props.put("mail.debug", mailProperties.debug());
        return props;
    }

}
