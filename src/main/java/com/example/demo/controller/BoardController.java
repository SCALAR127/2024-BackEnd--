package com.example.demo.controller;

import java.util.List;

import com.example.demo.controller.Error.ErrorCode;
import com.example.demo.controller.Error.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.dto.request.BoardCreateRequest;
import com.example.demo.controller.dto.request.BoardUpdateRequest;
import com.example.demo.controller.dto.response.BoardResponse;
import com.example.demo.service.BoardService;

@RestController
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards")
    public List<BoardResponse> getBoards() {
        return boardService.getBoards();
    }

    @GetMapping("/boards/{id}")
    public BoardResponse getBoard(
        @PathVariable Long id
    ) {
        return boardService.getBoardById(id);
    }

    @PostMapping("/boards")
    public BoardResponse createBoard(
        @RequestBody BoardCreateRequest request
    ) {
        return boardService.createBoard(request);
    }

    @PutMapping("/boards/{id}")
    public BoardResponse updateBoard(
        @PathVariable Long id,
        @RequestBody BoardUpdateRequest updateRequest
    ) {
        return boardService.update(id, updateRequest);
    }

    @DeleteMapping("/boards/{id}")
    public ResponseEntity<?> deleteBoard(
        @PathVariable Long id
    ) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.noContent().build();
        }catch (DataIntegrityViolationException e){
            final ErrorResponse response = ErrorResponse.of(ErrorCode.BOARD_EXIST);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        }

    }
}
