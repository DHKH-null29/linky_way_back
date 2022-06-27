package com.wnis.linkyway.config;

import com.wnis.linkyway.batch.tasklet.DeleteCardIfLinkIsInvalidTasklet;
import com.wnis.linkyway.batch.tasklet.DeleteCardInDatabaseTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    
    private final DeleteCardInDatabaseTasklet deleteCardInDatabaseTasklet;
    private final DeleteCardIfLinkIsInvalidTasklet deleteCardIfLinkIsInvalidTasklet;
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
        
        return stepBuilderFactory.get("deleteCardDataInDatabase")
                .tasklet(deleteCardInDatabaseTasklet)
                .build();
    }
    
    @Bean
    public Step deleteCardIfLinkIsInvalid() {
        return stepBuilderFactory.get("deleteCardIfLinkIsInvalid")
                .tasklet(deleteCardIfLinkIsInvalidTasklet)
                .build();
    }
    
}
