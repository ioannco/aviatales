package ru.ioannco.aviasales.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "db")
@Getter
@Setter
@ToString
public class DatabaseCredentialsConfig {
    private String url;
    private String username;
    private String password;
}
