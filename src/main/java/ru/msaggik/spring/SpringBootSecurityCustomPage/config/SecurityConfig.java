package ru.msaggik.spring.SpringBootSecurityCustomPage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.msaggik.spring.SpringBootSecurityCustomPage.services.PersonDetailsService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // внедрение PersonDetailsService для аутентификации
    private final PersonDetailsService personDetailsService;
    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    // переопределение формы для логина
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // отключение защиты от межсайтовой подделки запросов
                .csrf().disable()
                // конфигурация авторизации
                .authorizeRequests()
                // просмотр пришедшего запроса в приложение и допуск всех лишь по определённым адресам
                .antMatchers("/auth/login", "/error").permitAll()
                // для всех других запросов пользователь должен быть аутентифицирован
                .anyRequest().authenticated()
                // переход от авторизации к переопределению формы для логина
                .and()
                // переопределение формы для логина
                .formLogin().loginPage("/auth/login")
                // задание адреса куда отправлять данные с формы
                .loginProcessingUrl("/process_login")
                // после успешной аутентификации редирект на установленную страницу
                .defaultSuccessUrl("/hello")
                // после не успешной аутентификации редирект на стартовую страницу с ошибкой
                .failureUrl("/auth/login?error");
        // конфигурация Spring Security

    }

    // метод настройки аутентификации
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authProvider); // провайдер аутентификации
        auth.userDetailsService(personDetailsService);
    }

    // указание шифрования пароля
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // пароль без шифрования
    }
}
