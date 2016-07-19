/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package io.onedecision.engine;

import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "io.onedecision.engine" })
@EntityScan({ "io.onedecision.engine.decisions", "io.onedecision.engine.domain" })
@EnableJpaRepositories({ "io.onedecision.engine.domain.repositories",
		"io.onedecision.engine.decisions.repositories" })
@Import(OneDecisionConfig.class)
public class TestApplication extends WebMvcConfigurerAdapter {

	protected static final Logger LOGGER = LoggerFactory
			.getLogger(TestApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(TestApplication.class, args);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Beans registered:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				LOGGER.info("  " + beanName);
			}
		}
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("login");
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
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
					.antMatchers("/css/**", "/data/**", "/docs/**",
							"/fonts/**", "/images/**", "/js/**", "/webjars/**")
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
			auth.inMemoryAuthentication().withUser("admin")
					.password("onedecision")
                    .roles("user", "admin");
        }
    }

}
