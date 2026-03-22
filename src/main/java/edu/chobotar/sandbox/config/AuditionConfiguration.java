package edu.chobotar.sandbox.config;

/*
    @author  User
    @project  sandbox
    @class  AuditionConfiguration
    @version  1.0.0
    @since  22.03.2026 - 21.23
*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@Configuration
public class AuditionConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

}
