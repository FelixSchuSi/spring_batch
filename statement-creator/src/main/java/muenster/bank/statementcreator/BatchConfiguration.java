package muenster.bank.statementcreator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Customer;
import muenster.bank.statementcreator.domain.Statement;
import muenster.bank.statementcreator.processor.BalanceCalculatorProcessor;
import muenster.bank.statementcreator.processor.HttpTransactionProcessor;
import muenster.bank.statementcreator.processor.LoggerProcessor;
import muenster.bank.statementcreator.processor.StatementConstructionProcessor;
import muenster.bank.statementcreator.reader.InMemoryReader;
import muenster.bank.statementcreator.writer.InMemoryWriter;
import muenster.bank.statementcreator.writer.StatementHeaderWriter;
import muenster.bank.statementcreator.writer.StatementLineWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  public LoggerProcessor<Customer> customerLogger = new LoggerProcessor<Customer>();
  public LoggerProcessor<Account> accountLogger = new LoggerProcessor<Account>();
  public LoggerProcessor<Object> objectLogger = new LoggerProcessor<Object>();

  @Bean
  public FlatFileItemWriter<Account> accountCsvWriter() {
    // Create writer instance
    FlatFileItemWriter<Account> writer = new FlatFileItemWriter<>();
    FileSystemResource outputResource = new FileSystemResource("output/outputData.csv");
    // Set output file location
    writer.setResource(outputResource);

    // All job repetitions should "append" to same output file
    writer.setAppendAllowed(true);
    // Name field values sequence based on object properties
    writer.setLineAggregator(new DelimitedLineAggregator<Account>() {
      {
        setDelimiter(";");
        setFieldExtractor(new BeanWrapperFieldExtractor<Account>() {
          {
            setNames(new String[] { "id", "balance", "lastStatementDate", "iban" });
          }
        });
      }
    });
    return writer;
  }

  @Bean
  public ConversionService stringToDateConversionService() {
    DefaultConversionService testConversionService = new DefaultConversionService();
    DefaultConversionService.addDefaultConverters(testConversionService);
    testConversionService.addConverter(new Converter<String, Date>() {
      @Override
      public Date convert(String text) {
        Long timeLong = Long.parseLong(text);
        System.out.println("timeLong: " + timeLong + " string: " + text);
        Instant instant = Instant.ofEpochMilli(timeLong);
        return Date.from(instant);
      }
    });

    return testConversionService;
  }

  @Bean
  public FlatFileItemReader<Account> accountCsvReader() {
    // Create reader instance
    FlatFileItemReader<Account> reader = new FlatFileItemReader<Account>();

    // Set input file location
    reader.setResource(new FileSystemResource("src/main/resources/input-data/accounts.csv"));

    // Set number of lines to skips. Use it if file has header rows.
    reader.setLinesToSkip(1);

    // Configure how each line will be parsed and mapped to different values
    reader.setLineMapper(new DefaultLineMapper<Account>() {
      {
        // 3 columns in each row
        setLineTokenizer(new DelimitedLineTokenizer() {
          {
            setNames(new String[] { "id", "balance", "lastStatementDate", "iban" });
            setDelimiter(";");
          }
        });
        // Set values in Employee class
        setFieldSetMapper(new BeanWrapperFieldSetMapper<Account>() {
          {
            setTargetType(Account.class);
            setDistanceLimit(0);
            setConversionService(stringToDateConversionService());
          }
        });
      }
    });
    return reader;
  }

  @Bean
  public Job createStatementsJob() {
    return jobBuilderFactory.get("createStatementsJob").start(importCustomersStep()).next(importAccountsStep())
        .next(fetchTransactionsStep()).next(calculateNewBalancesStep()).next(generateStatementsStep()).build();
  }

  @Bean
  public Step importCustomersStep() {
    return stepBuilderFactory.get("importCustomersStep").<Customer, Customer>chunk(10).reader(customerJsonReader())
        .writer(new InMemoryWriter<Customer>()).listener(promotionListener()).build();
  }

  @Bean
  public Step importAccountsStep() {
    return stepBuilderFactory.get("importAccountsStep").<Account, Account>chunk(1).reader(accountCsvReader())
        .writer(new InMemoryWriter<Account>()).listener(promotionListener()).build();
  }

  @Bean
  public Step fetchTransactionsStep() {
    return stepBuilderFactory.get("fetchTransactionsStep").<Account, Account>chunk(1)
        .reader(new InMemoryReader<Account>("Account")).processor(httpTransactionProcessor(null))
        .writer(accountCsvWriter()).faultTolerant().retryLimit(3).retry(HttpServerErrorException.class).build();
  }

  @Bean
  public Step calculateNewBalancesStep() {
    return stepBuilderFactory.get("calculateNewBalancesStep").<Account, Account>chunk(1)
        .reader(new InMemoryReader<Account>("Account")).processor(new BalanceCalculatorProcessor())
        .writer(new InMemoryWriter<Account>()).build();
  }

  @Bean
  public Step generateStatementsStep() {
    return this.stepBuilderFactory.get("generateStatementsStep").<Customer, Statement>chunk(1)
        .reader(new InMemoryReader<Customer>("Customer")).processor(new StatementConstructionProcessor())
        .writer(statementItemWriter()).build();
  }

  @Bean
  public MultiResourceItemWriter<Statement> statementItemWriter() {
    Path accountsJsonPath = Paths.get(System.getProperty("user.dir"), "target", "kontoauszug");
    FileSystemResource resource = new FileSystemResource(accountsJsonPath);
    return new MultiResourceItemWriterBuilder<Statement>().name("statementItemWriter").resource(resource)
        .itemCountLimitPerResource(1).delegate(individualStatementItemWriter())
        .resourceSuffixCreator(index -> "-" + index + ".txt").build();
  }

  @Bean
  public FlatFileItemWriter<Statement> individualStatementItemWriter() {
    FlatFileItemWriter<Statement> itemWriter = new FlatFileItemWriter<>();

    itemWriter.setName("individualStatementItemWriter");
    itemWriter.setHeaderCallback(new StatementHeaderWriter());
    itemWriter.setLineAggregator(new StatementLineWriter());

    return itemWriter;
  }

  @Bean
  public ExecutionContextPromotionListener promotionListener() {
    // Promotes Data that is stored in StepExecutionContext to JobExecutionContext
    ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
    listener.setKeys(new String[] { "Account", "Customer" });
    return listener;
  }

  @Bean
  public JsonItemReader<Customer> customerJsonReader() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonJsonObjectReader<Customer> jsonObjectReader = new JacksonJsonObjectReader<>(Customer.class);
    jsonObjectReader.setMapper(objectMapper);
    Path customersJsonPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "input-data",
        "customers.json");
    FileSystemResource resource = new FileSystemResource(customersJsonPath);
    return new JsonItemReaderBuilder<Customer>().jsonObjectReader(jsonObjectReader).resource(resource)
        .name("customersJsonReader").build();
  }

  @Bean
  public JsonItemReader<Account> accountJsonReader() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonJsonObjectReader<Account> jsonObjectReader = new JacksonJsonObjectReader<>(Account.class);
    jsonObjectReader.setMapper(objectMapper);
    Path accountsJsonPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "input-data",
        "accounts.json");
    FileSystemResource resource = new FileSystemResource(accountsJsonPath);
    return new JsonItemReaderBuilder<Account>().jsonObjectReader(jsonObjectReader).resource(resource)
        .name("accountsJsonReader").build();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public HttpTransactionProcessor httpTransactionProcessor(RestTemplate restTemplate) {
    return new HttpTransactionProcessor(restTemplate);
  }

}