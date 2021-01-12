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
        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance = oldBalance;
        List<Transaction> transactions = account.getTransactions();

        for (Transaction t : transactions) {
            // Aufgabe 1 b)
            // Berechne den neuen Kontostand, indem du die Werte der Buchungen
            // zum alten Kontostand addierst.
            // Hinweis: nutze die .add() Funktion von newBalance.
            
        }

        account.setNewBalance(newBalance);
        return account;
    }

}
