package com.minimarket.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionEventListener.class);

    @EventListener
    public void loginExitoso(AuthenticationSuccessEvent event) {
        logger.info("Autenticacion exitosa para el usuario: {}", event.getAuthentication().getName());
    }

    @EventListener
    public void loginFallido(AbstractAuthenticationFailureEvent event) {
        logger.warn("Intento de autenticacion fallido para el usuario: {}. Motivo: {}",
                event.getAuthentication().getName(),
                event.getException().getMessage());
    }
}
