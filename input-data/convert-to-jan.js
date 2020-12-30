const fs = require("fs");
const path = require("path");

// 1. Dates in transactions.json nach Dezember 20 transformieren
const transactionsBuffer = fs.readFileSync(
    path.join(__dirname, "new_transactions.json")
);
const oldTransactions = JSON.parse(transactionsBuffer.toString());
const transactionsInJan = oldTransactions.map((transaction) => {
    let { timestamp } = transaction;
    timestamp = new Date(timestamp).setUTCFullYear(2021);
    timestamp = new Date(timestamp).setUTCMonth(0);
    const oldDate = new Date(timestamp).getUTCDate();
    const newDate = (oldDate % 25)+1;
    console.log(`oldDay: ${oldDate} newDay:${newDate}`);
    timestamp = new Date(timestamp).setUTCDate(newDate);
    
    return { ...transaction, timestamp };
});

fs.writeFileSync(
    path.join(__dirname, "transactions_jan.json"),
    JSON.stringify(transactionsInJan)
);

const timestamps = transactionsInJan.map((account) => account.timestamp)
const max = timestamps.reduce((accumulator, currentValue) => {
    return currentValue > accumulator ? currentValue : accumulator
}, 0)

const min = timestamps.reduce((accumulator, currentValue) => {
    return currentValue < accumulator ? currentValue : accumulator
}, 1612043447000)
console.log(new Date(min), new Date(max))


// 2. Dates in accounts.json nach November 20 transformieren
const accountsBuffer = fs.readFileSync(path.join(__dirname, "new_accounts_IBAN.json"));
const oldAccounts = JSON.parse(accountsBuffer.toString());
const accountsInDec = oldAccounts.map((account) => {
    let { lastStatementDate } = account;
    lastStatementDate = new Date(lastStatementDate).setFullYear(2020);
    lastStatementDate = new Date(lastStatementDate).setUTCMonth(11);
    const day = new Date(lastStatementDate).getUTCDate();
    // console.log(`oldDay: ${day} newDay:${26 + day % 5}`);
    lastStatementDate = new Date(lastStatementDate).setUTCDate(26 + day % 5);
    return { ...account, lastStatementDate };
});



fs.writeFileSync(
    path.join(__dirname, "accounts-dec.json"),
    JSON.stringify(accountsInDec)
);