package muenster.bank.statementcreator.processor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Transaction;

public class BalanceCalculatorProcessor implements ItemProcessor<Account, Account> {

    @Override
    public Account process(Account account) throws Exception {
        List<Transaction> transactions = account.getTransactions();

        BigDecimal newBalance = account.getBalance();

        for (Transaction transaction : transactions) {
            newBalance = newBalance.add(transaction.getTransactionAmount());
        }
        account.setNewBalance(newBalance);

        return account;
    }

}
