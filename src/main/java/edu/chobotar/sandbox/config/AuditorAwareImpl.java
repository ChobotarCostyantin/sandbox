package edu.chobotar.sandbox.config;

/*
    @author  User
    @project  sandbox
    @class  AuditorAwareImpl
    @version  1.0.0
    @since  22.03.2026 - 21.22
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(System.getProperty("user.name"));
    }
}
