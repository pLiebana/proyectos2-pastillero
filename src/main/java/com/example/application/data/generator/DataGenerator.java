package com.example.application.data.generator;

import com.example.application.data.Role;
import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;
import com.example.application.views.medicinas.Usuario;
import com.example.application.views.medicinas.UsuarioAPI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            UsuarioAPI apiusers = new UsuarioAPI();
            List<Usuario> Listaus = apiusers.GetUsuarios();

            int i = 0;
            while (i < Listaus.size()) {
                User user = new User();
                user.setName(Listaus.get(i).getNombre());
                user.setUsername(Listaus.get(i).getNombre_usuario());
                user.setHashedPassword(passwordEncoder.encode(Listaus.get(i).getContrasenia()));
                user.setRoles(Collections.singleton(Role.USER));
                userRepository.save(user);
                i++;
            }

            /*User admin = new User();
            admin.setName("Administrador");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);*/

        };
    }

}