package muenster.bank.statementcreator.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("Transaction")
public class Transaction {

    private long transactionId;

    private long accountId;

    private String description;

    private BigDecimal credit;

    private BigDecimal debit;

    private Date timestamp;

    private BigDecimal transactionAmount;

    public Transaction() {
    }

    public Transaction(long transactionId, long accountId, String description, BigDecimal credit, BigDecimal debit,
            Date timestamp, BigDecimal transactionAmount) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.description = description;
        this.credit = credit;
        this.debit = debit;
        this.timestamp = timestamp;
        this.transactionAmount = transactionAmount;
    }

    @Override
    public String toString() {
        return "Transaction{" + "transactionId=" + transactionId + ", accountId=" + accountId + ", description='"
                + description + '\'' + ", credit=" + credit + ", debit=" + debit + ", timestamp=" + timestamp + '}';
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public void setDebit(BigDecimal debit) {
        this.debit = debit;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getAccountId() {
        return accountId;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public BigDecimal getDebit() {
        return debit;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTransactionAmount() {
        if (credit != null && debit != null) {
            transactionAmount = credit.add(debit);
        } else if (credit != null) {
            transactionAmount = credit;
        } else if (debit != null) {
            transactionAmount = debit;
        } else {
            transactionAmount = new BigDecimal(0);
        }
        return transactionAmount;
    }
}