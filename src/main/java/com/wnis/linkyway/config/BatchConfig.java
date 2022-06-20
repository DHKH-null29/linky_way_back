package com.wnis.linkyway.config;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CardRepository cardRepository;
    private final CardTagRepository cardTagRepository;
    @Bean
    public Job job() {
        return jobBuilderFactory.get("batchJob")
                .incrementer(new RunIdIncrementer())
                .start(deleteCardDataInDatabase())
                .next(deleteCardIfLinkIsInvalid())
                .build();
    }
    
    @Bean
    public Step deleteCardDataInDatabase() {
        final int SEVEN_DAYS_AGO = 7;
        final int FIVE_MINUTES_AGO = 5;
        
        return stepBuilderFactory.get("deleteCardDataInDatabase")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
                            throws Exception {
                        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusMinutes(FIVE_MINUTES_AGO);
                        List<Long> cardIds = cardRepository.findAllIdToDeletedCard(sevenDaysAgo);
                        List<Long> cardTagIds = cardTagRepository.findAllCardTagIdInCardIds(cardIds);
                        cardTagRepository.deleteAllCardTagInIds(cardTagIds);
                        
                        StringBuilder sb = new StringBuilder();
                        
                        cardRepository.deleteAllByIdInBatch(cardIds);
    
                        for (Long id : cardIds) {
                            sb.append(id + " ");
                        }
                        log.debug("delete ID: {}", sb);
                        
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }
    
    @Bean
    public Step deleteCardIfLinkIsInvalid() {
        
        final int PAGE_SIZE = 100;
        
        return stepBuilderFactory.get("deleteCardIfLinkIsInvalid")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
                            throws Exception {
                        
                        
                        Page<Card> cardPage = cardRepository.findAll(PageRequest.of(0, PAGE_SIZE));
                        int totalPage = cardPage.getTotalPages();
                        List<Long> ids = new ArrayList<>(100);

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
                }).build();
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