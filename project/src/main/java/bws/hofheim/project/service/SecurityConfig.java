package bws.hofheim.project.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Diese Klasse definiert die Sicherheitsfilterkette und die CORS-Konfiguration.
 * Erstellt von: [name]
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Erstellt die Sicherheitsfilterkette für die Anwendung.
     * Diese Methode konfiguriert die Sicherheitsrichtlinien, wie z.B. CORS, CSRF und Zugriffsrechte.
     * Erstellt von: [name]
     *
     * @param http Das HttpSecurity-Objekt, das zur Konfiguration der Sicherheitsrichtlinien verwendet wird.
     * @return Ein SecurityFilterChain-Objekt, das die Sicherheitsfilterkette darstellt.
     * @throws Exception Falls bei der Konfiguration ein Fehler auftritt.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults()) // CORS aktivieren
                .csrf(csrf -> csrf.disable()) // CSRF deaktivieren
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS ohne Authentifizierung erlauben
                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll() // Login ohne Authentifizierung erlauben
                        .requestMatchers(HttpMethod.POST, "/user/register").permitAll() // Registrierung ohne Authentifizierung erlauben
                        .anyRequest().authenticated() // Alle anderen Requests benötigen Authentifizierung
                )
                .httpBasic(withDefaults()) // HTTP Basic Authentifizierung aktivieren
                .build();
    }

    /**
     * Konfiguriert die CORS-Einstellungen der Anwendung.
     * Diese Methode definiert, welche Ursprünge, Methoden und Header für Cross-Origin-Anfragen erlaubt sind.
     * Erstellt von: [name]
     *
     * @return Ein WebMvcConfigurer-Objekt, das die CORS-Konfiguration enthält.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // Erlaubte Ursprünge
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Erlaubte HTTP-Methoden
                        .allowedHeaders("*") // Erlaubte Header
                        .allowCredentials(true); // Anmeldedaten erlauben
            }
        };
    }
}