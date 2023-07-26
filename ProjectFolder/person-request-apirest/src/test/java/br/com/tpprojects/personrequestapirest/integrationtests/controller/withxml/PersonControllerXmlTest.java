package br.com.tpprojects.personrequestapirest.integrationtests.controller.withxml;

import br.com.tpprojects.personrequestapirest.configs.TestConfigs;
import br.com.tpprojects.personrequestapirest.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.AccountCredentialsVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.PersonVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.TokenVO;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.pagedmodels.PagedModelPerson;
import br.com.tpprojects.personrequestapirest.integrationtests.vo.wrappers.WrapperPersonVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	private static PersonVO person;
	@BeforeAll
	public static void setUp() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		person = new PersonVO();
	}

	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

		var accessToken =
				given()
						.basePath("/auth/signin")
							.port(TestConfigs.SERVER_PORT)
							.contentType(TestConfigs.CONTENT_TYPE_XML)
							.accept(TestConfigs.CONTENT_TYPE_XML)
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

		var content =
			given().spec(specification)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
						.body(person)
					.when()
						.post()
					.then()
						.statusCode(200)
					.extract()
						.body()
							.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_XML)
						.accept(TestConfigs.CONTENT_TYPE_XML)
						.body(person)
						.when()
						.post()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
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

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_XML)
						.accept(TestConfigs.CONTENT_TYPE_XML)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", person.getId())
						.when()
						.patch("{id}")
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

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

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_XML)
						.accept(TestConfigs.CONTENT_TYPE_XML)
						.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
						.pathParam("id", person.getId())
					.when()
						.get("{id}")
					.then()
						.statusCode(200)
					.extract()
						.body()
						.asString();

		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		assertNotNull(persistedPerson);

		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());

		assertEquals(person.getId(), persistedPerson.getId());

		assertEquals("Nelson", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
	}

	@Test
	@Order(5)
	public void testDelete() throws JsonProcessingException {

		given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(6)
	public void testFindAll() throws JsonProcessingException {

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_XML)
						.accept(TestConfigs.CONTENT_TYPE_XML)
						.queryParams("page",3,"size",10,"direction","asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
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

		var content =
				given().spec(specification)
						.contentType(TestConfigs.CONTENT_TYPE_XML)
						.accept(TestConfigs.CONTENT_TYPE_XML)
						.queryParams("page",3,"size",10,"direction","asc")
						.when()
						.get()
						.then()
						.statusCode(200)
						.extract()
						.body()
						.asString();

		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=0&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>prev</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=2&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1?page=3&amp;size=10&amp;direction=asc</href></links>"));
		assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=4&amp;size=10&amp;sort=firstName,asc</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/person/v1?direction=asc&amp;page=100&amp;size=10&amp;sort=firstName,asc</href></links>"));

		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/672</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/409</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/841</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/person/v1/404</href></links>"));

		assertTrue(content.contains("<page><size>10</size><totalElements>1005</totalElements><totalPages>101</totalPages><number>3</number></page>"));
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
