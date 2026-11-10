package gov.santos.meioambiente.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/cidadao/**", "/chamados/**", "/css/**", "/js/**", "/login", "/error").permitAll()
                .requestMatchers("/sabesp/**").hasRole("SABESP")
                .requestMatchers("/prefeitura/**").hasRole("PREFEITURA")
                .anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/painel", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/"))
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return PasswordEncoderFactories.createDelegatingPasswordEncoder(); }

    @Bean
    UserDetailsService users(PasswordEncoder encoder,
            @Value("${app.sabesp.password:sabesp123}") String senhaSabesp,
            @Value("${app.prefeitura.password:prefeitura123}") String senhaPrefeitura) {
        return new InMemoryUserDetailsManager(
            User.withUsername("sabesp").password(encoder.encode(senhaSabesp)).roles("SABESP").build(),
            User.withUsername("prefeitura").password(encoder.encode(senhaPrefeitura)).roles("PREFEITURA").build());
    }
}
