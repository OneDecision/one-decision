package link.omny.decisions;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.base.Predicate;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "link.omny.decisions", "link.omny.domain" })
@EntityScan({ "link.omny.decisions", "link.omny.domain" })
@EnableJpaRepositories({ "link.omny.domain.repositories",
        "link.omny.decisions.repositories" })
@EnableSwagger2
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

    @Bean
    public Docket decisionsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public-api").select()
                // Ignores controllers annotated with @CustomIgnore
//                    .apis(not(withClassAnnotation(CustomIgnore.class)) //Selection by RequestHandler
                .paths(publicPaths()) // and by paths
                .build();
//                  .apiInfo(apiInfo())
//                  .securitySchemes(securitySchemes())
//                  .securityContext(securityContext());
    }

    /**
     * 
     * @return public API.
     */
    private Predicate<String> publicPaths() {
        return or(regex("/.*/decisions.*"), regex("/.*/decision-ui-models.*"),
                regex("/.*/domain.*"));
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Allegedly sets welcome page though does not appear to be working
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        // registry.addViewController("/loginError").setViewName("loginError");
    }

    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }
    
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends
            WebSecurityConfigurerAdapter {

        @Autowired
        private DataSource dataSource;

        @Autowired
        private SecurityProperties security;
        
        // @Autowired
        // private CorsFilter corsFilter;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/css/**", "/data/**", "/fonts/**",
                            "/images/**", "/js/**")
                    .permitAll()
                    .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .antMatchers("/*.html", "/users/**")
                    .hasRole("user")
                    .antMatchers("/admin/**")
                    .hasRole("admin")
                    .anyRequest().authenticated()  
                    .and().formLogin()
                    .loginPage("/login").failureUrl("/login?error")
                    .successHandler(getSuccessHandler()).permitAll()
                    .and().csrf().disable().httpBasic();
                    //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             http.requestMatcher
            // org.apache.catalina.filters.CorsFilter corsFilter = new
            // org.apache.catalina.filters.CorsFilter();
            // corsFilter.
            // http.addFilterBefore(corsFilter,
            // BasicAuthenticationFilter.class);
        }

        private AuthenticationSuccessHandler getSuccessHandler() {
            SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler(
                    "/");
            successHandler.setTargetUrlParameter("redirect");
            return successHandler;
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            // auth.userDetailsService(activitiUserDetailsService);
            // auth.jdbcAuthentication().dataSource(dataSource)
            // .withDefaultSchema().withUser("user").password("password")
            // .roles("USER").and().withUser("admin").password("password")
            // .roles("USER", "ADMIN");
            auth.inMemoryAuthentication().withUser("user").password("user")
                    .roles("user");
            auth.inMemoryAuthentication().withUser("admin").password("admin")
                    .roles("user", "admin");
        }
    }


}
