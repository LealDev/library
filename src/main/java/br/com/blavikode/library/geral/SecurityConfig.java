package br.com.blavikode.library.geral;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // desliga CSRF para todas as requisições
                .csrf(csrf -> csrf.disable())
                // passa a usar Basic Auth
                .httpBasic(Customizer.withDefaults())
                // exige autenticação para tudo
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }
}