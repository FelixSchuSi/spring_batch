package hello.world.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloWorldJob() {
        return jobBuilderFactory.get("helloWorldJob")
                .start(helloWorldStep())
                .build();
    }

    @Bean
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloWorldStep")
                .<String, String>chunk(1)
                .reader(nameReader())
                .processor(new GreetingProcessor())
                .writer(greetingWriter())
                .build();
    }

    @Bean
    public ItemReader<String> nameReader() {
        return new FlatFileItemReaderBuilder<String>()
                .name("nameReader")
                .resource(new FileSystemResource("input.txt"))
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public FlatFileItemWriter<String> greetingWriter() {
        return new FlatFileItemWriterBuilder<String>()
                .name("greetingWriter")
                .resource(new FileSystemResource("output.txt"))
                .lineAggregator(new PassThroughLineAggregator<String>())
                .build();
    }
}
