package br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml.mapper.YmlMapper;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.BookVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
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

		var content =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(BookVO[].class, objectMapper);

		List<BookVO> books = Arrays.asList(content);

		BookVO foundBookOne = books.get(0);

		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getTitle());

		assertEquals(1, foundBookOne.getId());
		//'Michael C. Feathers', '2017-11-29 13:50:05.878000', 49.00, 'Working effectively with legacy code'
		assertEquals("Michael C. Feathers", foundBookOne.getAuthor());
		assertEquals(49.0, foundBookOne.getPrice());
		assertEquals("Working effectively with legacy code", foundBookOne.getTitle());

		BookVO foundBookThree = books.get(3);

		assertNotNull(foundBookThree.getId());
		assertNotNull(foundBookThree.getAuthor());
		assertNotNull(foundBookThree.getPrice());
		assertNotNull(foundBookThree.getTitle());

		assertEquals(4, foundBookThree.getId());
		//'Crockford', '2017-11-07 15:09:01.674000', 67.00, 'JavaScript'
		assertEquals("Crockford", foundBookThree.getAuthor());
		assertEquals(67.0, foundBookThree.getPrice());
		assertEquals("JavaScript", foundBookThree.getTitle());
	}

	private void mockBook() {
		book.setAuthor("Andremis");
		book.setLaunchDate(new Date());
		book.setPrice(45.0);
		book.setTitle("Você ainda lembra de mim?");
	}

}
