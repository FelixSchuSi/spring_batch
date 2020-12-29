package muenster.bank.statementcreator.writer;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.util.CollectionUtils;

import muenster.bank.statementcreator.domain.Account;
import muenster.bank.statementcreator.domain.Customer;
import muenster.bank.statementcreator.domain.Statement;
import muenster.bank.statementcreator.domain.Transaction;

public class StatementLineWriter implements LineAggregator<Statement> {

    private static final String ADDRESS_LINE_ONE = String.format("%121s\n", "Münster Bank");
    private static final String ADDRESS_LINE_TWO = String.format("%120s\n", "Corrensstraße 25");
    private static final String ADDRESS_LINE_THREE = String.format("%120s\n\n", "48149 Münster");
    private static final String STATEMENT_LINE_LEFT = "Kontoauszug für Ihr Konto mit der IBAN: %s";
    private static final String STATEMENT_LINE_RIGHT = "Zeitraum: %tF bis %tF\n\n";
    private static final String STATEMENT_LINE = STATEMENT_LINE_LEFT + "                       " + STATEMENT_LINE_RIGHT;

    public String aggregate(Statement statement) {
        StringBuilder output = new StringBuilder();

        formatHeader(statement, output);
        formatAccount(statement, output);

        return output.toString();
    }

    private void formatAccount(Statement statement, StringBuilder output) {
        if (!CollectionUtils.isEmpty(statement.getAccounts())) {

            for (Account account : statement.getAccounts()) {

                output.append(
                        String.format(STATEMENT_LINE, account.getIban(), account.getLastStatementDate(), new Date()));

                BigDecimal creditAmount = new BigDecimal(0);
                BigDecimal debitAmount = new BigDecimal(0);
                if (account.getTransactions().size() == 0) {
                    output.append("               Es wurden keine Transaktionen im Zeitraum getätigt.");
                }
                for (Transaction transaction : account.getTransactions()) {
                    if (transaction.getCredit() != null) {
                        creditAmount = creditAmount.add(transaction.getCredit());
                    }

                    if (transaction.getDebit() != null) {
                        debitAmount = debitAmount.add(transaction.getDebit());
                    }

                    output.append(
                            String.format("               %tF          %-50s    %8.2f €\n", transaction.getTimestamp(),
                                    transaction.getDescription(), transaction.getTransactionAmount()));
                }

                output.append(String.format("%82s %14.2f €\n", "Summe Soll:", debitAmount));
                output.append(String.format("%83s %13.2f €\n", "Summe Haben:", creditAmount));
                output.append(String.format("%82s %14.2f €\n\n", "Kontostand:", account.getBalance()));
            }
        }
    }

    private void formatHeader(Statement statement, StringBuilder output) {
        Customer customer = statement.getCustomer();

        String customerName = String.format("\n%s %s", customer.getFirstName(), customer.getLastName());
        output.append(customerName + ADDRESS_LINE_ONE.substring(customerName.length()));

        output.append(customer.getAddress1() + ADDRESS_LINE_TWO.substring(customer.getAddress1().length()));

        String addressString = String.format("%s, %s %s", customer.getCity(), customer.getState(),
                customer.getPostalCode());
        output.append(addressString + ADDRESS_LINE_THREE.substring(addressString.length()));
    }
}
