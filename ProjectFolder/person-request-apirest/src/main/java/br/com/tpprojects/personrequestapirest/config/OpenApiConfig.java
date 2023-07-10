package br.com.tpprojects.personrequestapirest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    //Utilizado para gerenciar classes que utilizamos que não são nossas
    // por exemplo a OpenAPI que não é uma classe desse projeto e sim de um
    // framework. A partir do uso desse @Bean, eu posso então configurar e
    // instanciar esse objeto no meu projeto.
    // Essa configuração é para dar informações gerais da API que criei
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RESTful API with Java and Spring Boot")
                .version("v1")
                .description("Some description about the API")
                .termsOfService("https://pub.erudio.com.br/meus-cursos")
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://pub.erudio.com.br/meus-cursos")));
    }
}
