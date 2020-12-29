package muenster.bank.statementcreator;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.RestTemplate;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Customer;
import muenster.bank.statementcreator.domain.Statement;
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
  public Job createStatementsJob() {
    return jobBuilderFactory.get("createStatementsJob").incrementer(new RunIdIncrementer()).start(importCustomersStep())
        // .start(importAccountsStep())
        .next(importAccountsStep())
        // .next(logInMemoryDataStep())
        .next(fetchTransactionsStep()).next(generateStatementsStep()).build();
  }

  @Bean
  public Step importCustomersStep() {
    return stepBuilderFactory.get("importCustomerStep").<Customer, Customer>chunk(10).reader(customerJsonReader())
        // .processor(customerLogger)
        .writer(new InMemoryWriter()).listener(promotionListener()).build();
  }

  @Bean
  public Step importAccountsStep() {
    return stepBuilderFactory.get("importCustomerStep").<Account, Account>chunk(10).reader(accountJsonReader())
        // .processor(accountLogger)
        .writer(new InMemoryWriter()).listener(promotionListener()).build();
  }

  @Bean
  public Step fetchTransactionsStep() {
    return stepBuilderFactory.get("logInMemoryDataStep").<Account, Account>chunk(10)
        .reader(new InMemoryReader<Account>("Account")).processor(httpTransactionProcessor(null))
        .writer(new InMemoryWriter()).build();
  }

  @Bean
  public Step logInMemoryDataStep() {
    return stepBuilderFactory.get("logInMemoryDataStep").<Object, Object>chunk(10)
        .reader(new InMemoryReader<Object>("Account")).processor(objectLogger).writer(new InMemoryWriter()).build();
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