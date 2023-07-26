package br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml.mapper.YmlMapper;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.BookVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.pagedmodels.PagedModelBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYmlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YmlMapper objectMapper;
	private static BookVO book;
	@BeforeAll
	public static void setUp() {
		objectMapper = new YmlMapper();

		book = new BookVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken =
				given()
						.config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.basePath("/auth/signin")
							.port(TestConfigs.SERVER_PORT)
							.contentType(TestConfigs.CONTENT_TYPE_YML)
							.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(user, objectMapper)
							.when()
						.post()
							.then()
								.statusCode(200)
									.extract()
									.body()
										.as(TokenVO.class, objectMapper)
									.getAccessToken();

		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}


	@Test
	@Order(1)
	public void testCreate() throws JsonProcessingException {
		mockBook();

		var persistedBook =
			given().spec(specification).config(RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
									.encodeContentTypeAs(
											TestConfigs.CONTENT_TYPE_YML,
											ContentType.TEXT)))
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(book, objectMapper)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.as(BookVO.class, objectMapper);

		book = persistedBook;
		assertNotNull(persistedBook);

		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertTrue(persistedBook.getId() > 0);

		assertEquals("Andremis", persistedBook.getAuthor());
		assertEquals(45.0, persistedBook.getPrice());
		assertEquals("Você ainda lembra de mim?", persistedBook.getTitle());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonProcessingException {
		book.setAuthor("Andremis Aricieri");

		var persistedBook =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(book, objectMapper)
						.when()
						.post()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(BookVO.class, objectMapper);

		book = persistedBook;
		assertNotNull(persistedBook);

		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertEquals(book.getId(), persistedBook.getId());

		assertEquals("Andremis Aricieri", persistedBook.getAuthor());
		assertEquals(45.0, persistedBook.getPrice());
		assertEquals("Você ainda lembra de mim?", persistedBook.getTitle());
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonProcessingException {
		mockBook();

		var persistedBook =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", book.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.as(BookVO.class, objectMapper);

		book = persistedBook;
		assertNotNull(persistedBook);

		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertEquals(book.getId(), persistedBook.getId());

		assertEquals("Andremis Aricieri", persistedBook.getAuthor());
		assertEquals(45.0, persistedBook.getPrice());
		assertEquals("Você ainda lembra de mim?", persistedBook.getTitle());
	}

	@Test
	@Order(4)
	public void testDelete() throws JsonProcessingException {

		given().spec(specification).config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
										TestConfigs.CONTENT_TYPE_YML,
										ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(5)
	public void testFindAll() throws JsonProcessingException {

		var response =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.queryParams("page", 0 , "limit", 12, "direction", "asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(PagedModelBook.class, objectMapper);


		List<BookVO> content = response.getContent();


		BookVO foundBookOne = content.get(0);

		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getTitle());
		assertTrue(foundBookOne.getId() > 0);

		assertEquals(12, foundBookOne.getId());
		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
		assertEquals(54.0, foundBookOne.getPrice());
		assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());

		BookVO foundBookThree = content.get(4);

		assertNotNull(foundBookThree.getId());
		assertNotNull(foundBookThree.getAuthor());
		assertNotNull(foundBookThree.getPrice());
		assertNotNull(foundBookThree.getTitle());
		assertTrue(foundBookThree.getId() > 0);

		assertEquals(8, foundBookThree.getId());
		assertEquals("Eric Evans", foundBookThree.getAuthor());
		assertEquals(92.0, foundBookThree.getPrice());
		assertEquals("Domain Driven Design", foundBookThree.getTitle());
	}

	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {

		var unthreatedContent = given()
				.config(
						RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 0 , "size", 12, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();

		var content = unthreatedContent.replace("\n", "").replace("\r", "");

		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/3\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/5\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/7\""));

		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));

		assertTrue(content.contains("page:  size: 12  totalElements: 16  totalPages: 2  number: 0"));
	}

	private void mockBook() {
		book.setAuthor("Andremis");
		book.setLaunchDate(new Date());
		book.setPrice(45.0);
		book.setTitle("Você ainda lembra de mim?");
	}

}
