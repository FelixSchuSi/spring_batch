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
    private static final String STATEMENT_LINE = "Kontoauszug für Ihr Konto mit der IBAN: %s\n";

    public String aggregate(Statement statement) {
        StringBuilder output = new StringBuilder();

        formatHeader(statement, output);
        formatAccount(statement, output);

        return output.toString();
    }

    private void formatAccount(Statement statement, StringBuilder output) {
        if (!CollectionUtils.isEmpty(statement.getAccounts())) {

            for (Account account : statement.getAccounts()) {

                output.append(String.format(STATEMENT_LINE, account.getIban()));
                output.append(String.format("%78s %tF: %9.2f €\n", "Kontostand am", account.getLastStatementDate(),
                        account.getBalance()));
                BigDecimal creditAmount = new BigDecimal(0);
                BigDecimal debitAmount = new BigDecimal(0);
                if (account.getTransactions().size() == 0) {
                    output.append("               Es wurden keine Transaktionen im Zeitraum getätigt.\n");
                }
                for (Transaction transaction : account.getTransactions()) {

                    if (transaction.getCredit() != null) {
                        creditAmount = creditAmount.add(transaction.getCredit());
                    }

                    if (transaction.getDebit() != null) {
                        debitAmount = debitAmount.add(transaction.getDebit());
                    }

                    output.append(String.format("               %tF                    %-40s       %8.2f €\n",
                            transaction.getTimestamp(), transaction.getDescription(),
                            transaction.getTransactionAmount()));
                }
                if (account.getTransactions().size() > 0) {
                    output.append(String.format("%76s %23.2f €\n", "Summe Soll:", debitAmount));
                    output.append(String.format("%77s %22.2f €\n", "Summe Haben:", creditAmount));
                }
                output.append(
                        String.format("%78s %tF: %9.2f €\n\n", "Kontostand am", new Date(), account.getNewBalance()));
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
