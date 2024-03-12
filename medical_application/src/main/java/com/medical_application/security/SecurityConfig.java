package com.medical_application.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
     private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/comment/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/comment/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/comment/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                .antMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/doctor/**").permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/appointments/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/post/**").permitAll()
                .anyRequest().authenticated()
                .and();
        http.authenticationProvider(daoAuthenticationProvider());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(this.customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
