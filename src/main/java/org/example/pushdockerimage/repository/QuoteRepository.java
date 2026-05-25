package org.example.pushdockerimage.repository;

import org.example.pushdockerimage.model.Quote;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

@Repository
public class QuoteRepository {

    private static final List<Quote> QUOTES = List.of(
        new Quote(1,  "Albert Einstein",   "Life is like riding a bicycle. To keep your balance, you must keep moving."),
        new Quote(2,  "Maya Angelou",      "You will face many defeats in life, but never let yourself be defeated."),
        new Quote(3,  "Nelson Mandela",    "It always seems impossible until it's done."),
        new Quote(4,  "Steve Jobs",        "The only way to do great work is to love what you do."),
        new Quote(5,  "Oprah Winfrey",     "Turn your wounds into wisdom."),
        new Quote(6,  "Winston Churchill", "Success is not final, failure is not fatal: it is the courage to continue that counts."),
        new Quote(7,  "Mark Twain",        "The secret of getting ahead is getting started."),
        new Quote(8,  "Abraham Lincoln",   "Whatever you are, be a good one."),
        new Quote(9,  "Helen Keller",      "Alone we can do so little; together we can do so much."),
        new Quote(10, "Confucius",         "It does not matter how slowly you go as long as you do not stop.")
    );

    private final Random random = new Random();

    public Quote findRandom() {
        return QUOTES.get(random.nextInt(QUOTES.size()));
    }
}