package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.validation.ValidationSequence;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/ex")
public class ExceptionTestController {

    @PostMapping("/cards")
    public ResponseEntity<String> cardRequestTest(
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {
        return ResponseEntity.ok(cardRequest.getContent());
    }

    @PostMapping("/numbers")
    public ResponseEntity<Integer> numberRequestTest(@Valid @RequestBody NumberDTO numberDTO) {
        return ResponseEntity.ok(numberDTO.getNumber());
    }

    @GetMapping("/path/{id}")
    public ResponseEntity<Long> pathVariableTypeTest(@PathVariable Long id) {
        return ResponseEntity.ok(id);
    }

    @Getter
    public static class NumberDTO {
        @PositiveOrZero
        private int number;
    }
}
