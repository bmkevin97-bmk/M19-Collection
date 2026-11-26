package com.m19collection.api.config;

import com.m19collection.api.entity.Role;
import com.m19collection.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioRepository usuarioRepository;
    private final String adminEmail;

    public AdminBootstrap(UsuarioRepository usuarioRepository,
                          @Value("${app.bootstrap-admin-email:}") String adminEmail) {
        this.usuarioRepository = usuarioRepository;
        this.adminEmail = adminEmail;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank()) {
            return;
        }

        usuarioRepository.findByEmailIgnoreCase(adminEmail.trim()).ifPresent(usuario -> {
            if (usuario.getRole() != Role.ADMIN) {
                usuario.setRole(Role.ADMIN);
                usuarioRepository.save(usuario);
                log.info("User {} promoted to ADMIN", usuario.getEmail());
            }
        });
    }
}