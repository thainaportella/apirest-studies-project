package br.com.tpprojects.personrequestapirest.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;

import br.com.tpprojects.personrequestapirest.exceptions.RequiredObjectIsNullException;
import br.com.tpprojects.personrequestapirest.model.Book;
import br.com.tpprojects.personrequestapirest.repositories.BookRepository;
import br.com.tpprojects.personrequestapirest.unittests.mapper.mocks.MockBook;
import br.com.tpprojects.personrequestapirest.vo.v1.BookVO;
import br.com.tpprojects.personrequestapirest.services.BookServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

	MockBook input;

	@InjectMocks
	private BookServices service;
	@Mock
	BookRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() {
		Book entity = input.mockEntity(1);
		entity.setId(1);

		when(repository.findById(1)).thenReturn(Optional.of(entity));

		var result = service.findById(1);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());

		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals(new Date(2023, 07, 9), result.getLaunchDate());
		assertEquals(Double.valueOf(1.0), result.getPrice());
		assertEquals("Title Test1", result.getTitle());
	}

	@Test
	void testCreate() {
		Book entity = input.mockEntity(1);
		Book persisted = entity;
		persisted.setId(1);

		BookVO vo = input.mockVO(1);
		vo.setKey(1);

		when(repository.save(entity)).thenReturn(persisted);

		var result = service.create(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());

		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals(new Date(2023, 07, 9), result.getLaunchDate());
		assertEquals(Double.valueOf(1.0), result.getPrice());
		assertEquals("Title Test1", result.getTitle());
	}

	@Test
	void createWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});

		String expectedMessage = "It is not allowed to persist a null object.";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1);

		Book persisted = entity;
		persisted.setId(1);

		BookVO vo = input.mockVO(1);
		vo.setKey(1);

		when(repository.findById(1)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);

		var result = service.update(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());

		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals(new Date(2023, 07, 9), result.getLaunchDate());
		assertEquals(Double.valueOf(1.0), result.getPrice());
		assertEquals("Title Test1", result.getTitle());
	}

	@Test
	void updateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});

		String expectedMessage = "It is not allowed to persist a null object.";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testDelete() {
		Book entity = input.mockEntity(1);
		entity.setId(1);

		when(repository.findById(1)).thenReturn(Optional.of(entity));

		service.delete(1);
	}
	/*
	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();

		when(repository.findAll()).thenReturn(list);

		var books = service.findAll();
		assertNotNull(books);
		assertEquals(14, books.size());

		var bookOne = books.get(1);
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());
		System.out.println(bookOne.toString());
		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", bookOne.getAuthor());
		assertEquals(new Date(2023, 07, 9), bookOne.getLaunchDate());
		assertEquals(Double.valueOf(1.0), bookOne.getPrice());
		assertEquals("Title Test1", bookOne.getTitle());

		var bookFour = books.get(4);
		assertNotNull(bookFour);
		assertNotNull(bookFour.getKey());
		assertNotNull(bookFour.getLinks());
		System.out.println(bookFour.toString());
		assertTrue(bookFour.toString().contains("links: [</api/book/v1/4>;rel=\"self\"]"));
		assertEquals("Author Test4", bookFour.getAuthor());
		assertEquals(new Date(2023, 07, 9), bookFour.getLaunchDate());
		assertEquals(Double.valueOf(4.0), bookFour.getPrice());
		assertEquals("Title Test4", bookFour.getTitle());

		var bookSeven = books.get(7);
		assertNotNull(bookSeven);
		assertNotNull(bookSeven.getKey());
		assertNotNull(bookSeven.getLinks());
		System.out.println(bookSeven.toString());
		assertTrue(bookSeven.toString().contains("links: [</api/book/v1/7>;rel=\"self\"]"));
		assertEquals("Author Test7", bookSeven.getAuthor());
		assertEquals(new Date(2023, 07, 9), bookSeven.getLaunchDate());
		assertEquals(Double.valueOf(7.0), bookSeven.getPrice());
		assertEquals("Title Test7", bookSeven.getTitle());
	}*/

}