package com.github.ridicuturing.guard.config.secure;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableGlobalAuthentication
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   /* @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ChatUserMapper chatUserMapper) {
        return http
                .csrf().disable()
                .authorizeExchange().anyExchange().permitAll()
                .and()
                .formLogin()
                .and()
                //.addFilterAt(new MyAnonymousAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    *//*@Primary
    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(SecurityProperties properties,
                                                                    ObjectProvider<PasswordEncoder> passwordEncoder) {
        SecurityProperties.User user = properties.getUser();
        UserDetails userDetails = getUserDetails(user, getOrDeducePassword(user, passwordEncoder.getIfAvailable()));
        User.UserBuilder
        new UserDetailsManagerConfigurer.UserDetailsBuilder().
                .
        UserDetailsManagerConfigurer.UserDetailsBuilder
        return new MapReactiveUserDetailsService(userDetails);
    }*//*

*/
}