package com.wnis.linkyway.batch.tasklet;

import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteCardInDatabaseTasklet implements Tasklet {
    
    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    @Value("${batch.minutes-ago}")
    private long MINUTES_AGO;
    
    @Value("${batch.days-ago}")
    private long DAYS_AGO;
    
    @Value("${batch.page-size}")
    private int PAGE_SIZE;
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDateTime minutesAgo = LocalDateTime.now().minusMinutes(MINUTES_AGO);
        
        Slice<Long> cardIds = cardRepository.findAllIdToDeletedCardUsingPage(minutesAgo, PageRequest.of(0, PAGE_SIZE));
        if (cardIds.isEmpty()) {
            return RepeatStatus.FINISHED;
        }
        
        do {
            cardIds = deleteCard(minutesAgo, cardIds);
        } while (cardIds.hasNext());
    
        return RepeatStatus.FINISHED;
    }
    
    private Slice<Long> deleteCard(LocalDateTime minutesAgo, Slice<Long> cardIds) {
        List<Long> cardTagIds = cardTagRepository.findAllCardTagIdInCardIds(cardIds.getContent());
        cardTagRepository.deleteAllCardTagInIds(cardTagIds);
        StringBuilder sb = new StringBuilder();
    
        cardRepository.deleteAllByIdInBatch(cardIds);
    
        for (Long id : cardIds) {
            sb.append(id).append(" ");
        }
        log.debug("delete ID: {}", sb);
        int lastIndex = cardIds.getContent().size() - 1;
        long lastCardId = cardIds.getContent().get(lastIndex);
        cardIds = cardRepository.findAllIdToDeletedCardUsingCursorPage(minutesAgo, lastCardId, PageRequest.of(0, PAGE_SIZE));
        return cardIds;
    }
}
