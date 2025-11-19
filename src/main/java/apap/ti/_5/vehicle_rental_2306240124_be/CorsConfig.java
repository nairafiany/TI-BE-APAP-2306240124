package apap.ti._5.vehicle_rental_2306240124_be;

import org.springframework.beans.factory.annotation.Value; // <-- 1. IMPORT INI
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // 2. TAMBAHKAN INI UNTUK MEMBACA DARI ENV
    @Value("${CORS_ALLOWED_ORIGINS}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        // 3. (Opsional tapi disarankan) Sesuaikan path-nya
                        .addMapping("/**")
                        // 4. UBAH INI
                        .allowedOrigins(allowedOrigins) // <-- Ganti dari hardcode
                        
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Authorization");
            }
        };
    }
}