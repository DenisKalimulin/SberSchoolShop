package ru.kalimulin.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kalimulin.enums.RoleName;
import ru.kalimulin.models.Role;
import ru.kalimulin.repositories.RoleRepository;


import java.util.Arrays;

@Component
@Slf4j
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);

    @Autowired
    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (!roleRepository.findByRoleName(roleName).isPresent()) {
                Role role = Role.builder()
                        .roleName(roleName)
                        .build();
                roleRepository.save(role);
                logger.info("Создана роль: {}", roleName);
            } else {
                logger.debug("Роль уже существует: {}", roleName);
            }
        });
    }
}

