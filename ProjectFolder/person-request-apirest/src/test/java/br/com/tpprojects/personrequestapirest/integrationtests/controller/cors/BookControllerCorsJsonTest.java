package br.com.tpprojects.personrequestapirest.integrationtests.controller.cors;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.BookVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerCorsJsonTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static BookVO book;
	@BeforeAll
	public static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		book = new BookVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken =
				given()
						.basePath("/auth/signin")
							.port(TestConfigs.SERVER_PORT)
							.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.body(user)
							.when()
						.post()
							.then()
								.statusCode(200)
									.extract()
									.body()
										.as(TokenVO.class)
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

		var content =
			given().spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.body(book)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();

		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		assertNotNull(persistedBook);

		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertTrue(persistedBook.getId() > 0);

		assertEquals("Andremis", persistedBook.getAuthor());
		assertEquals(new Date(2023, 06, 12), persistedBook.getLaunchDate());
		assertEquals(45.0, persistedBook.getPrice());
		assertEquals("Você ainda lembra de mim?", persistedBook.getTitle());
	}

	@Test
	@Order(2)
	public void testCreateWithWrongOrigin() throws JsonProcessingException {
		mockBook();

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
						.body(book)
					.when()
						.post()
					.then()
						.statusCode(403)
					.extract()
						.body()
						.asString();

		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonProcessingException {
		mockBook();

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", book.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.asString();

		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		assertNotNull(persistedBook);

		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());

		assertTrue(persistedBook.getId() > 0);

		assertEquals("Andremis", persistedBook.getAuthor());
		assertEquals(new Date(2023, 06, 12), persistedBook.getLaunchDate());
		assertEquals(45.0, persistedBook.getPrice());
		assertEquals("Você ainda lembra de mim?", persistedBook.getTitle());
	}

	@Test
	@Order(4)
	public void testFindByIdWithWrongOrigin() throws JsonProcessingException {
		mockBook();

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
						.pathParam("id", book.getId())
						.when()
						.get("{id}")
						.then()
						.statusCode(403)
						.extract()
						.body()
						.asString();

		assertNotNull(content);
		assertEquals("Invalid CORS request", content);
	}

	private void mockBook() {
		book.setAuthor("Andremis");
		book.setLaunchDate(new Date(2023, 06, 12));
		book.setPrice(45.0);
		book.setTitle("Você ainda lembra de mim?");
	}

}
