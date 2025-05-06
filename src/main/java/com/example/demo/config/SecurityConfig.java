package com.example.demo.config;

import com.example.demo.exception.CustomAccessDeniedHandler;
import com.example.demo.exception.CustomAuthenticationEntryPoint;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.userinfo.UserInfoUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
//@EnableWebSecurity
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfig {

    /* @Autowired
    private JwtAuthenticationFilter authenticationFilter;
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    //authentication
    public UserDetailsService userDetailsService() {
       /* UserDetails admin = User.withUsername("adminboot")
                .password(encoder.encode("pwd1"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("userboot")
                .password(encoder.encode("pwd2"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);*/
        return new UserInfoUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@SecurityFilterChain");

        return  http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> {
                    //auth.requestMatchers("/users/**").permitAll()
                    auth.requestMatchers("/users/signUp","/users/signIn","/users/signIning").permitAll()
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/index.html").permitAll()
                            //.requestMatchers("/users/home").permitAll()
                            //.requestMatchers("/api/lectures/**").authenticated();
                            .requestMatchers("/users/home").authenticated();
                            //.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/users/signIn")
                        .loginProcessingUrl("/users/signIning")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/users/home")
                        .failureUrl("/error.html")
                        .permitAll()
                )
                //.formLogin().disable()
             *//*   .logout((logout) -> logout.logoutUrl("/app-logout")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/")
                )*//*
               *//* .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())*//*
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                //JWT session을 만들지 않는다 SessionCreationPolicy.STATELESS

                //.formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                //.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.authenticationProvider(authenticationProvider())
                //JWT
                //.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
               *//* .exceptionHandling(authManager -> authManager
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )*//*
                .build();*/

        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/users/signUp","/users/signIn","/users/signIning").permitAll()
                            .requestMatchers("/index.html").permitAll()
                            .requestMatchers("/*").authenticated();
                })
                .formLogin(Customizer.withDefaults())
                .build();


    }

    /*//401
    //인증 예외 밠생시 처리
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@AuthenticationEntryPoint");
        return new CustomAuthenticationEntryPoint();
    }

    //403
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@AccessDeniedHandler");
        return new CustomAccessDeniedHandler();
    }
*/

    @Bean
    public AuthenticationProvider authenticationProvider(){
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@AuthenticationProvider");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@AuthenticationManager");
        return config.getAuthenticationManager();
    }

    /*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/v3/api-docs/**","/swagger-ui.html", "/swagger-ui/**");
    }*/


}
