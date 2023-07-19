package br.com.tpprojects.personrequestapirest.services;

import br.com.tpprojects.personrequestapirest.controllers.PersonController;
import br.com.tpprojects.personrequestapirest.exceptions.RequiredObjectIsNullException;
import br.com.tpprojects.personrequestapirest.exceptions.ResourceNotFoundException;
import br.com.tpprojects.personrequestapirest.model.Person;
import br.com.tpprojects.personrequestapirest.mapper.DozerMapper;
import br.com.tpprojects.personrequestapirest.repositories.PersonRepository;
import br.com.tpprojects.personrequestapirest.vo.v1.PersonVO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

@Service // serve para que sb encare como um objeto que vai ser injetado em runtime na app
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());
    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    //@Autowired
    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {

        logger.info("Finding all people!");

        var personPage = repository.findAll(pageable);
        var personVoPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVoPage.map(p -> p.add(WebMvcLinkBuilder.linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(personVoPage, link);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding a person!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(WebMvcLinkBuilder.linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {
        if(person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating a person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(WebMvcLinkBuilder.linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {
        if(person == null) throw new RequiredObjectIsNullException();
        logger.info("Updating a person!");
        var entity = repository.findById(person.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(WebMvcLinkBuilder.linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }
    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Disabling a person!");

        repository.disablePerson(id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(WebMvcLinkBuilder.linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting a person!");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        repository.delete(entity);
    }
}
