package com.txacon.gap.infrastructure.configuracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.txacon.gap.application.adapter.JwtUserDetailsServiceImpl;
import com.txacon.gap.infrastructure.rest.security.JWTAuthenticationFilter;
import com.txacon.gap.infrastructure.rest.security.JWTAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.txacon.gap.domain.security.SecurityConstants.LOGIN_URL;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final JwtUserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("jwt.secret")
    private String secret;

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {       
        return MyBcryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B, 12);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        /*
         * 1. Se desactiva el uso de cookies 2. Se activa la configuración CORS con los
         * valores por defecto 3. Se desactiva el filtro CSRF 4. Se indica que el login
         * no requiere autenticación 5. Se indica que el resto de URLs esten securizadas
         */
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().cors().and()
                .csrf().disable()
                // Swagger
                .authorizeRequests()
                .antMatchers("/swagger-ui", "/swagger-resources/**", "/v2/api-docs/**", "/swagger-ui/**", "/webjars/**")
                .permitAll()
                // Login
                .antMatchers(HttpMethod.POST, LOGIN_URL).permitAll()
                // Create customer
                .antMatchers(HttpMethod.POST, "/customers").permitAll()
                .antMatchers(HttpMethod.GET, "/businesses/*/menu").permitAll()
                .anyRequest().authenticated().and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), objectMapper, secret))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), secret));

    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Se define la clase que recupera los usuarios y el algoritmo para procesar las
        // passwords
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(Lists.newArrayList("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*");
            }
        };
    }

}
