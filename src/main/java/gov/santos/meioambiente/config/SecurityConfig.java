package gov.santos.meioambiente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import gov.santos.meioambiente.enums.Perfil;
import gov.santos.meioambiente.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/cadastro/**", "/css/**", "/js/**", "/login", "/error").permitAll()
                .requestMatchers("/cidadao/consulta/**", "/chamados/**").permitAll()
                .requestMatchers("/cidadao/**").hasRole("CIDADAO")
                .requestMatchers("/sabesp/**").hasRole("SABESP")
                .requestMatchers("/prefeitura/**").hasRole("PREFEITURA")
                .anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/painel", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }

    @Bean
    CommandLineRunner administradores(UsuarioService usuarios,
            @Value("${app.sabesp.password:sabesp123}") String senhaSabesp,
            @Value("${app.prefeitura.password:prefeitura123}") String senhaPrefeitura) {
        return args -> {
            usuarios.criarAdministrador("sabesp", "sabesp@interno.local", senhaSabesp, Perfil.SABESP);
            usuarios.criarAdministrador("prefeitura", "prefeitura@interno.local", senhaPrefeitura, Perfil.PREFEITURA);
        };
    }
}
