package com.wnis.linkyway.batch.tasklet;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
        Page<Card> cardPage = cardRepository.findAll(PageRequest.of(0, PAGE_SIZE));
        int totalPage = cardPage.getTotalPages();
        List<Long> ids = new ArrayList<>(PAGE_SIZE);
    
        for (int i = 0; i < totalPage; i++) {
            Slice<Card> cardSlice = cardRepository.findAll(PageRequest.of(i, PAGE_SIZE));
            for (Card card : cardSlice.getContent()) {
                if (!checkLink(card)) {
                    ids.add(card.getId());
                }
            }
            List<Long> cardTagIds = cardTagRepository.findAllCardTagIdInCardIds(ids);
            cardTagRepository.deleteAllCardTagInIds(cardTagIds);
            cardRepository.deleteAllByIdInBatch(ids);
            ids.clear();
        }
    
    
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
            if (http.getResponseCode() == 200) {
                return true;
            }
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        
        return false;
    }
}
