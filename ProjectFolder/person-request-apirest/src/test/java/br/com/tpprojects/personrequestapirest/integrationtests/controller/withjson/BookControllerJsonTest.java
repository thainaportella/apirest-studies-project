package br.com.tpprojects.personrequestapirest.integrationtests.controller.withjson;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.BookVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.wrappers.WrapperBookVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

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
						.body(book)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();

		book = objectMapper.readValue(content, BookVO.class);
		assertNotNull(book);

		assertNotNull(book.getId());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getPrice());
		assertNotNull(book.getTitle());

		assertTrue(book.getId() > 0);

		assertEquals("Andremis", book.getAuthor());
		assertEquals(45.0, book.getPrice());
		assertEquals("Você ainda lembra de mim?", book.getTitle());

	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonProcessingException {
		book.setAuthor("Andremis Aricieri");

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.body(book)
						.when()
						.post()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		BookVO bookUpdated = objectMapper.readValue(content, BookVO.class);

		assertNotNull(bookUpdated.getId());
		assertNotNull(bookUpdated.getAuthor());
		assertNotNull(bookUpdated.getPrice());
		assertNotNull(bookUpdated.getTitle());

		assertEquals(book.getId(), bookUpdated.getId());

		assertEquals("Andremis Aricieri", bookUpdated.getAuthor());
		assertEquals(45.0, bookUpdated.getPrice());
		assertEquals("Você ainda lembra de mim?", bookUpdated.getTitle());
	}

	@Test
	@Order(3)
	public void testFindById() throws JsonProcessingException {
		mockBook();

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.pathParam("id", book.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.asString();

		BookVO foundBook = objectMapper.readValue(content, BookVO.class);

		assertNotNull(foundBook.getId());
		assertNotNull(foundBook.getAuthor());
		assertNotNull(foundBook.getPrice());
		assertNotNull(foundBook.getTitle());

		assertEquals(foundBook.getId(), book.getId());

		assertEquals("Andremis Aricieri", foundBook.getAuthor());
		assertEquals(45.0, foundBook.getPrice());
		assertEquals("Você ainda lembra de mim?", foundBook.getTitle());
	}

	@Test
	@Order(4)
	public void testDelete() throws JsonProcessingException {

		given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
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
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_JSON)
						.queryParams("page", 0 , "limit", 5, "direction", "asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
		List<BookVO> books = wrapper.getEmbedded().getBooks();
		BookVO foundBookOne = books.get(0);

		assertNotNull(foundBookOne.getId());
		assertNotNull(foundBookOne.getAuthor());
		assertNotNull(foundBookOne.getPrice());
		assertNotNull(foundBookOne.getTitle());
		assertTrue(foundBookOne.getId() > 0);

		assertEquals(12, foundBookOne.getId());
		assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
		assertEquals(54.0, foundBookOne.getPrice());
		assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());

		BookVO foundBookThree = books.get(4);

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
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {

		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.queryParams("page", 0 , "size", 12, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.asString();

		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/3\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/5\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}}"));

		assertTrue(content.contains("{\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\"}}"));

		assertTrue(content.contains("\"page\":{\"size\":12,\"totalElements\":15,\"totalPages\":2,\"number\":0"));
	}

	private void mockBook() {
		book.setAuthor("Andremis");
		book.setLaunchDate(new Date());
		book.setPrice(45.0);
		book.setTitle("Você ainda lembra de mim?");
	}

}
