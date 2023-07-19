package br.com.tpprojects.personrequestapirest.services;

import br.com.tpprojects.personrequestapirest.controllers.BookController;
import br.com.tpprojects.personrequestapirest.controllers.PersonController;
import br.com.tpprojects.personrequestapirest.exceptions.RequiredObjectIsNullException;
import br.com.tpprojects.personrequestapirest.exceptions.ResourceNotFoundException;
import br.com.tpprojects.personrequestapirest.mapper.DozerMapper;
import br.com.tpprojects.personrequestapirest.model.Book;
import br.com.tpprojects.personrequestapirest.repositories.BookRepository;
import br.com.tpprojects.personrequestapirest.vo.v1.BookVO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import br.com.tpprojects.personrequestapirest.vo.v1.PersonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class BookServices {
    private Logger logger = Logger.getLogger(BookServices.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
        logger.info("Finding all books in library");

        var libraryPage = repository.findAll(pageable);
        var libraryVoPage = libraryPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
        libraryVoPage.map(p -> p.add(WebMvcLinkBuilder.linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(libraryVoPage, link);
    }

    public BookVO findById(Integer id) {
        logger.info("Finding one book in library");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var object = DozerMapper.parseObject(entity, BookVO.class);
        object.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return object;
    }

    public BookVO create(BookVO book) {
        if(book == null) throw new RequiredObjectIsNullException();
        logger.info("Publishing a book in library");

        var entity = DozerMapper.parseObject(book, Book.class);
        var object = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        object.add(linkTo(methodOn(BookController.class).findById(object.getKey())).withSelfRel());
        return object;
    }

    public BookVO update(BookVO book) {
        if(book == null) throw new RequiredObjectIsNullException();
        logger.info("Updating one book in library");

        var entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var object = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        object.add(linkTo(methodOn(BookController.class).findById(object.getKey())).withSelfRel());
        return object;
    }

    public void delete(Integer id) {
        logger.info("Deleting one book in library");

        var object = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        repository.delete(object);
    }

}
