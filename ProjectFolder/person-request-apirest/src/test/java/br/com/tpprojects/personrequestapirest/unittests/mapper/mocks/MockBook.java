package br.com.tpprojects.personrequestapirest.unittests.mapper.mocks;

import br.com.tpprojects.personrequestapirest.model.Book;
import br.com.tpprojects.personrequestapirest.vo.v1.BookVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {


    public Book mockEntity() {
        return mockEntity(0);
    }
    
    public BookVO mockVO() {
        return mockVO(0);
    }
    
    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookVO> mockVOList() {
        List<BookVO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockVO(i));
        }
        return books;
    }
    
    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setId(number.intValue());
        book.setAuthor("Author Test" + number);
        book.setLaunchDate(new Date(2023, 07, 9));
        book.setPrice(number.doubleValue());
        book.setTitle("Title Test" + number);
        return book;
    }

    public BookVO mockVO(Integer number) {
        BookVO book = new BookVO();
        book.setKey(number.intValue());
        book.setAuthor("Author Test" + number);
        book.setLaunchDate(new Date(2023, 07, 9));
        book.setPrice(number.doubleValue());
        book.setTitle("Title Test" + number);
        return book;
    }

}
