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

        // Aufgabe 1: Wie kann anhand des aktuellen Kontostandes des Accounts
        // und den vorliegenen Transaktionen der neue Kontostand ermittelt werden?
        BigDecimal newBalance = account.getBalance();

        for (Transaction transaction : transactions) {
            newBalance = newBalance.add(transaction.getTransactionAmount());
        }
        account.setNewBalance(newBalance);

        return account;
    }

}
