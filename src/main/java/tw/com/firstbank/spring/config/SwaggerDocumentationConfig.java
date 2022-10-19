package tw.com.firstbank.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;

@Configuration
//@EnableOpenApi
@EnableSwagger2
public class SwaggerDocumentationConfig {

  private static final String PACKAGE = "tw.com.firstbank.rest";
  private static final String VERSION = "1.0.0";
  private static final String TITLE = "API";
  private static final String DESC = "This is a project for API";
  private static final String CONTACT_EMAIL = "andy_lee@fstop.com.tw";
  private static final String CONTACT_NAME = "";
  private static final String CONTACT_URL = "";
  private static final String LICENSE_URL = "";
  private static final String LICENSE = "";

  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_2)  //Docket(DocumentationType.OAS_30)
        .select()
        .apis(RequestHandlerSelectors.basePackage(PACKAGE))
        .paths(PathSelectors.any())
        .build()
        // .directModelSubstitute(org.threeten.bp.LocalDate.class, java.sql.Date.class)
        // .directModelSubstitute(org.threeten.bp.OffsetDateTime.class, java.util.Date.class)
        .apiInfo(apiInfo());
  }

  ApiInfo apiInfo() {
    return new ApiInfoBuilder().title(TITLE).description(DESC).license(LICENSE)
        .licenseUrl(LICENSE_URL).termsOfServiceUrl("").version(VERSION)
        .contact(new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL)).build();
  }
  /*
  @Bean
  public OpenAPI openApi() {
    return new OpenAPI().info(new Info().title(TITLE).description(DESC).termsOfService("")
        .version(VERSION).license(new License().name(LICENSE).url(LICENSE_URL))
        .contact(new io.swagger.v3.oas.models.info.Contact().email(CONTACT_EMAIL)));
  }
  */

}
