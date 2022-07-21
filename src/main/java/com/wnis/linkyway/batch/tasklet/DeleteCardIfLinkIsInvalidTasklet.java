package com.wnis.linkyway.batch.tasklet;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.repository.card.CardRepository;
import com.wnis.linkyway.repository.cardtag.CardTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteCardIfLinkIsInvalidTasklet implements Tasklet {
    
    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    
    @Value("${batch.page-size}")
    private int PAGE_SIZE;
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Slice<Card> cardSlice = cardRepository.findAllUsingPage(PageRequest.of(0, PAGE_SIZE));
       
        do {
            cardSlice = deleteInvalidLink(cardSlice);
        } while(cardSlice.hasNext());
        
        return RepeatStatus.FINISHED;
    }
    
    private boolean checkLink(Card card) {
        String domain = card.getLink();
        URL url;
        HttpURLConnection http;
        
        try {
            url = new URL(domain);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("HEAD");
            if (http.getResponseCode() < 400) {
                return true;
            }
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        
        return false;
    }
    
    private Slice<Card> deleteInvalidLink(Slice<Card> cardSlice) {
        
        int lastIndex = cardSlice.getContent().size() - 1;
        long lastCardId = cardSlice.getContent().get(lastIndex).getId();
        List<Long> ids = new ArrayList<>();
    
        for (Card card : cardSlice.getContent()) {
            if (!checkLink(card)) {
                ids.add(card.getId());
            }
        }
        List<Long> cardTagIds = cardTagRepository.findAllCardTagIdInCardIds(ids);
        cardTagRepository.deleteAllCardTagInIds(cardTagIds);
        cardRepository.deleteAllByIdInBatch(ids);
    
        Slice<Card> nextCardSlice = cardRepository.findAllUsingCursorPage(lastCardId, PageRequest.of(0, PAGE_SIZE));
        return nextCardSlice;
    }
}
