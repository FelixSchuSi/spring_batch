package muenster.bank.statementcreator.processor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Transaction;

public class BalanceCalculatorProcessor implements ItemProcessor<Account, Account> {

    @Override
    public Account process(Account account) throws Exception {
        List<Transaction> transactions = account.getTransactions();

        return account;
    }

}
