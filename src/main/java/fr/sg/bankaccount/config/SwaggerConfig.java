package fr.sg.bankaccount.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * SwaggerConfig is the main class for Swagger's configuration
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("fr.sg.bankaccount"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Bank Account Kata",
                "App to demonstrate a bank account transactions ( Withdrawn, Deposite)",
                "0.0.1-SNAPSHOT",
                "",
                new Contact("Rami Soltani",
                        "https://www.linkedin.com/in/soltanirami/",
                        "soltany.ramy@gmail.com"),
                "",
                "",
                Collections.emptyList());
    }
}
