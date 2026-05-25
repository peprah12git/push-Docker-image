package org.example.pushdockerimage.controller;

import org.example.pushdockerimage.model.Quote;
import org.example.pushdockerimage.repository.QuoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    private final QuoteRepository quoteRepository;

    public QuoteController(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @GetMapping("/random")
    public Quote getRandomQuote() {
        return quoteRepository.findRandom();
    }
}