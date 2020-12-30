package muenster.bank.statementcreator.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Account {

    private long id;
    private BigDecimal balance;
    private BigDecimal newBalance;
    private Date lastStatementDate;
    private String iban;

    public final List<Transaction> transactions = new ArrayList<>();

    public Account() {
    }

    public Account(long id, BigDecimal balance, Date lastStatementDate, String iban, BigDecimal newBalance) {
        this.id = id;
        this.balance = balance;
        this.lastStatementDate = lastStatementDate;
        this.iban = iban;
        this.newBalance = newBalance;
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", iban=" + iban + ", balance=" + balance + ", newBalance=" + newBalance
                + ", lastStatementDate=" + lastStatementDate + ", transactions=" + transactions + '}';
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getNewBalance() {
        if (newBalance == null) {
            return balance; // "old" balance is used when newBalance is not yet calculated
        } else {
            return newBalance;
        }
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public Date getLastStatementDate() {
        return lastStatementDate;
    }

    public void setLastStatementDate(Date lastStatementDate) {
        this.lastStatementDate = lastStatementDate;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}