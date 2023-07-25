package br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.controller.withyml.mapper.YmlMapper;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.PersonVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerYmlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static YmlMapper objectMapper;
	private static PersonVO person;
	@BeforeAll
	public static void setUp() {
		objectMapper = new YmlMapper();

		person = new PersonVO();
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
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}


	@Test
	@Order(1)
	public void testCreate() throws JsonProcessingException {
		mockPerson();

		var persistedPerson =
			given().spec(specification).config(RestAssuredConfig
							.config()
							.encoderConfig(EncoderConfig.encoderConfig()
									.encodeContentTypeAs(
											TestConfigs.CONTENT_TYPE_YML,
											ContentType.TEXT)))
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(person, objectMapper)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.as(PersonVO.class, objectMapper);

		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertTrue(persistedPerson.getId() > 0);

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonProcessingException {
		person.setLastName("Piquet Souto Maior");

		var persistedPerson =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.body(person, objectMapper)
						.when()
						.post()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonProcessingException {
		mockPerson();

		var persistedPerson =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", person.getId())
						.when()
						.get("{id}")
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(4)
	public void testFindById() throws JsonProcessingException {
		mockPerson();

		var persistedPerson =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", person.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.as(PersonVO.class, objectMapper);

		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonProcessingException {

		given().spec(specification).config(RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(
										TestConfigs.CONTENT_TYPE_YML,
										ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonProcessingException {

		var wrapper =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.queryParams("page",3,"size",10,"direction","asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.as(PagedModelPerson.class, objectMapper);


		var people = wrapper.getContent();
		PersonVO foundPersonOne = people.get(0);

		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		assertTrue(foundPersonOne.getEnabled());

		assertEquals(672, foundPersonOne.getId());

		assertEquals("Alic", foundPersonOne.getFirstName());
		assertEquals("Terbrug", foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());

		PersonVO foundPersonThree = people.get(3);

		assertNotNull(foundPersonThree.getId());
		assertNotNull(foundPersonThree.getFirstName());
		assertNotNull(foundPersonThree.getLastName());
		assertNotNull(foundPersonThree.getAddress());
		assertNotNull(foundPersonThree.getGender());
		assertFalse(foundPersonThree.getEnabled());

		assertEquals(404, foundPersonThree.getId());

		assertEquals("Alister", foundPersonThree.getFirstName());
		assertEquals("Etheridge", foundPersonThree.getLastName());
		assertEquals("333 Lakewood Gardens Street", foundPersonThree.getAddress());
		assertEquals("Male", foundPersonThree.getGender());
	}

	@Test
	@Order(7)
	public void testHATEOAS() throws JsonProcessingException, JsonProcessingException {

		var untreatedContent =
				given().spec(specification).config(RestAssuredConfig
								.config()
								.encoderConfig(EncoderConfig.encoderConfig()
										.encodeContentTypeAs(
												TestConfigs.CONTENT_TYPE_YML,
												ContentType.TEXT)))
						.contentType(TestConfigs.CONTENT_TYPE_YML)
						.accept(TestConfigs.CONTENT_TYPE_YML)
						.queryParams("page",3,"size",10,"direction","asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		var content = untreatedContent.replace("\n", "").replace("\r", "");

		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));

		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/672\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/409\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/841\""));

		assertTrue(content.contains("page:  size: 10  totalElements: 1004  totalPages: 101  number: 3"));
	}


	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
