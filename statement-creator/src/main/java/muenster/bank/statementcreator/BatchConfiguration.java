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
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Customer;
import muenster.bank.statementcreator.processor.LoggerProcessor;
import muenster.bank.statementcreator.reader.InMemoryReader;
import muenster.bank.statementcreator.writer.InMemoryWriter;

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
        .next(logInMemoryDataStep())
        .build();
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
  public Step logInMemoryDataStep() {
    return stepBuilderFactory.get("logInMemoryDataStep").<Object, Object>chunk(10).reader(new InMemoryReader())
        // .processor(objectLogger)
        .writer(new InMemoryWriter()).build();
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
    FileSystemResource ressource = new FileSystemResource(customersJsonPath);
    return new JsonItemReaderBuilder<Customer>().jsonObjectReader(jsonObjectReader).resource(ressource)
        .name("customersJsonReader").build();
  }

  @Bean
  public JsonItemReader<Account> accountJsonReader() {
    ObjectMapper objectMapper = new ObjectMapper();
    JacksonJsonObjectReader<Account> jsonObjectReader = new JacksonJsonObjectReader<>(Account.class);
    jsonObjectReader.setMapper(objectMapper);
    Path accountsJsonPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "input-data",
        "accounts.json");
    FileSystemResource ressource = new FileSystemResource(accountsJsonPath);
    return new JsonItemReaderBuilder<Account>().jsonObjectReader(jsonObjectReader).resource(ressource)
        .name("accountsJsonReader").build();
  }

}