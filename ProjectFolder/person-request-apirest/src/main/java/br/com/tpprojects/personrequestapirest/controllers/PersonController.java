package br.com.tpprojects.personrequestapirest.controllers;

import br.com.tpprojects.personrequestapirest.vo.v1.PersonVO;
import br.com.tpprojects.personrequestapirest.services.PersonServices;
import br.com.tpprojects.personrequestapirest.util.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin - se colocado no controller fica global
@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for Managing People")
public class PersonController {

    @Autowired
    private PersonServices service;

    @Operation(summary = "Finds all People", description = "Finds all People", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PersonVO.class)))}),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})

    @GetMapping(produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Finds people by name", description = "Finds people by name", tags = {"People"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PersonVO.class)))}),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})

    @GetMapping(value= "/findPersonByName/{firstName}", produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findPersonsByName(
            @PathVariable(value = "firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
        return ResponseEntity.ok(service.findPersonsByName(firstName, pageable));
    }

    @Operation(summary = "Finds a Person", description = "Finds a Person", tags = {"People"},
            responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO findById(@PathVariable(value="id") Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Adds a new Person", description = "Adds a new Person by passing in a JSON XML, or YML, representation of the Person", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @CrossOrigin(origins = {"http://localhost:8080", "https://erudio.com.br"})
    @PostMapping(consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    public PersonVO create(@RequestBody PersonVO person) {
        return service.create(person);
    }

    @Operation(summary = "Updates a Person", description = "Updates a Person by passing in a JSON XML, or YML, representation of the Person", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})

    @PutMapping(consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
    public PersonVO update(@RequestBody PersonVO person) {
        return service.update(person);
    }

    @Operation(summary = "Disables a Person by ID", description = "Disables a Person by ID", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonVO.class))),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @PatchMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    public PersonVO disablePerson(@PathVariable(value="id") Long id) {
        return service.disablePerson(id);
    }

    @Operation(summary = "Deletes a Person", description = "Deletes a Person by passing in a JSON XML, or YML, representation of the Person", tags = {"People"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value="id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
