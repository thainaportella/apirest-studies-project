package br.com.tpprojects.personrequestapirest.unittests.mapper;

import br.com.tpprojects.personrequestapirest.mapper.DozerMapper;
import br.com.tpprojects.personrequestapirest.model.Book;
import br.com.tpprojects.personrequestapirest.vo.v1.BookVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DozerConverterBookTest {
    
    br.com.tpprojects.personrequestapirest.unittests.mapper.mocks.MockBook inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new br.com.tpprojects.personrequestapirest.unittests.mapper.mocks.MockBook();
    }

    @Test
    public void parseEntityToVOTest() {
        BookVO output = DozerMapper.parseObject(inputObject.mockEntity(), BookVO.class);
        assertEquals(Integer.valueOf(0), output.getKey());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(new Date(2023, 07, 9), output.getLaunchDate());
        assertEquals(Double.valueOf(0.0), output.getPrice());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parseEntityListToVOListTest() {
        List<BookVO> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), BookVO.class);
        BookVO outputZero = outputList.get(0);

        assertEquals(Integer.valueOf(0), outputZero.getKey());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputZero.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputZero.getPrice());
        assertEquals("Title Test0", outputZero.getTitle());

        BookVO outputSeven = outputList.get(7);

        assertEquals(Integer.valueOf(7), outputSeven.getKey());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputSeven.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputSeven.getPrice());
        assertEquals("Title Test7", outputSeven.getTitle());

        BookVO outputTwelve = outputList.get(12);

        assertEquals(Integer.valueOf(12), outputTwelve.getKey());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputTwelve.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputTwelve.getPrice());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }

    @Test
    public void parseVOToEntityTest() {
        Book output = DozerMapper.parseObject(inputObject.mockVO(), Book.class);
        assertEquals(Integer.valueOf(0), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(new Date(2023, 07, 9), output.getLaunchDate());
        assertEquals(Double.valueOf(0.0), output.getPrice());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parserVOListToEntityListTest() {
        List<Book> outputList = DozerMapper.parseListObjects(inputObject.mockVOList(), Book.class);
        Book outputZero = outputList.get(0);

        assertEquals(Integer.valueOf(0), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputZero.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputZero.getPrice());
        assertEquals("Title Test0", outputZero.getTitle());

        Book outputSeven = outputList.get(7);

        assertEquals(Integer.valueOf(7), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputSeven.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputSeven.getPrice());
        assertEquals("Title Test7", outputSeven.getTitle());

        Book outputTwelve = outputList.get(12);

        assertEquals(Integer.valueOf(12), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(new Date(2023, 07, 9), outputTwelve.getLaunchDate());
        assertEquals(Double.valueOf(0.0), outputTwelve.getPrice());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }
}
