package br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml.finish;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml.mapper.YmlMapper;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYmlTest extends AbstractIntegrationTest {
    private static TokenVO tokenVO;
    private static YmlMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        objectMapper = new YmlMapper();
    }

    @Test
    @Order(1)
    public void testSignin() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        tokenVO =
                given()
                        .config(RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                        .accept(TestConfigs.CONTENT_TYPE_YML)
                        .basePath("/auth/signin")
                            .port(TestConfigs.SERVER_PORT)
                            .contentType(TestConfigs.CONTENT_TYPE_YML)
                        .body(user, objectMapper)
                            .when()
                        .post()
                            .then()
                                .statusCode(200)
                                    .extract()
                                    .body()
                                        .as(TokenVO.class, objectMapper);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }
    @Test
    @Order(2)
    public void testRefresh() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

       var newTokenVO =
                given().config(RestAssuredConfig
                                .config()
                                .encoderConfig(EncoderConfig.encoderConfig()
                                        .encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YML,
                                                ContentType.TEXT)))
                        .accept(TestConfigs.CONTENT_TYPE_YML)
                        .basePath("/auth/refresh")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_YML)
                        .pathParam("username", tokenVO.getUsername())
                        .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                        .when()
                        .put("{username}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class, objectMapper);

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }
}
