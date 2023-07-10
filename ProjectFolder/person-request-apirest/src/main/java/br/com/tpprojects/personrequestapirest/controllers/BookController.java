package br.com.tpprojects.personrequestapirest.controllers;

import br.com.tpprojects.personrequestapirest.services.BookServices;
import br.com.tpprojects.personrequestapirest.util.MediaType;
import br.com.tpprojects.personrequestapirest.vo.v1.BookVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/book/v1")
@RestController
@Tag(name="Books", description = "Endpoints for managing Books")
public class BookController {

    @Autowired
    private BookServices service;

    @Operation(summary = "Finding all books", description = "Finding all books", tags = {"Books"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookVO.class)))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @GetMapping(produces = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<BookVO> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Finding one book", description = "Finding one book", tags = {"Books"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BookVO findById(@PathVariable(value ="id") Integer id) {
        return service.findById(id);
    }
    @Operation(summary = "Publishing a book", description = "Publishing a book", tags = {"Books"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @PostMapping(consumes = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}, produces = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BookVO create(@RequestBody BookVO book) {
        return service.create(book);
    }
    @Operation(summary = "Updating a book", description = "Updating a book", tags = {"Books"}, responses = {
            @ApiResponse(description = "Updated", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @PutMapping(consumes = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}, produces = {MediaType.APPLICATION_YML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BookVO update(@RequestBody BookVO book) { return service.update(book); }
    @Operation(summary = "Deleting a book", description = "Deleting a book", tags = {"Books"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value ="id") Integer id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
