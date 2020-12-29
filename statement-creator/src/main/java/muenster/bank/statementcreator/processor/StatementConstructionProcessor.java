package muenster.bank.statementcreator.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Customer;
import muenster.bank.statementcreator.domain.Statement;

public class StatementConstructionProcessor implements ItemProcessor<Customer, Statement> {
    private List<Account> accounts;

    @Override
    public Statement process(Customer customer) throws Exception {
        Statement statement = null;
        List<Long> accountIds = customer.getAccountIds();
        List<Account> accountsOfCustomer = new ArrayList<Account>();
        for (Long accountId : accountIds) {
            List<Account> accountsWithId = accounts.stream().filter(filteredAcc -> filteredAcc.getId() == accountId)
                    .collect(Collectors.toList());
            accountsOfCustomer.addAll(accountsWithId);
        }
        statement = new Statement(customer, accountsOfCustomer);
        return statement;
    }

    @SuppressWarnings("unchecked")
    @BeforeStep
    public void prepare(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        try {
            this.accounts = (List<Account>) jobContext.get("Account");
        } catch (Exception e) {
            this.accounts = new ArrayList<Account>();
        }
    }
}
