package muenster.bank.statementcreator.processor;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Transaction;

public class HttpTransactionProcessor implements ItemProcessor<Account, Account> {
    private final String apiUrl = "http://localhost:3443/transactions?accountId=";
    private RestTemplate restTemplate;

    public HttpTransactionProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Account process(Account account) {
        Long accountId = account.getId();
        List<Transaction> transactions = fetch(accountId);
        for (Transaction transaction : transactions) {
            account.addTransaction(transaction);
        }
        return account;
    }

    private List<Transaction> fetch(Long accountId) {
        ResponseEntity<Transaction[]> response = restTemplate.getForEntity(apiUrl + accountId.toString(),
                Transaction[].class);
        Transaction[] transactions = response.getBody();
        return Arrays.asList(transactions);
    }
}
