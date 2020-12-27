const fs = require("fs");
const path = require("path");
// 1. Dates in transactions.json nach Dezember 20 transformieren
const transactionsBuffer = fs.readFileSync(
  path.join(__dirname, "transactions.json")
);
const oldTransactions = JSON.parse(transactionsBuffer.toString());
const TransactionsInJune18 = oldTransactions.map((transaction) => {
  let { timestamp } = transaction;
  if (new Date(timestamp).getUTCMonth() <= 4) {
    timestamp += 3 * 60 * 60 * 1000; // 3 hours in ms
  }
  return { ...transaction, timestamp };
});
const allInDecember20 = TransactionsInJune18.map((transaction) => {
  let { timestamp } = transaction;
  timestamp = new Date(timestamp).setFullYear(2020);
  timestamp = new Date(timestamp).setUTCMonth(11);
  return { ...transaction, timestamp };
});
// const timestamps = allInDecember20.map((transaction) => transaction.timestamp)
// const max = timestamps.reduce((accumulator, currentValue) => {
//     return currentValue > accumulator ? currentValue: accumulator
// }, 0)

// const min = timestamps.reduce((accumulator, currentValue) => {
//     return currentValue < accumulator ? currentValue: accumulator
// }, 1609213847000)
// console.log(new Date(min), new Date(max))
fs.writeFileSync(
  path.join(__dirname, "new_transactions.json"),
  JSON.stringify(allInDecember20)
);

// 2. Dates in accounts.json nach November 20 transformieren
const accountsBuffer = fs.readFileSync(path.join(__dirname, "accounts.json"));
const oldAccounts = JSON.parse(accountsBuffer.toString());
const accountsInNovember20 = oldAccounts.map((transaction) => {
  let { lastStatementDate } = transaction;
  lastStatementDate = new Date(lastStatementDate).setFullYear(2020);
  lastStatementDate = new Date(lastStatementDate).setUTCMonth(10);
  return { ...transaction, lastStatementDate };
});
fs.writeFileSync(
  path.join(__dirname, "new_accounts.json"),
  JSON.stringify(accountsInNovember20)
);

// 3. Bei customers account feld zu Array transformieren
//     -> Alle customers mit gleicher account_id suchen und zusammenfügen

const customersBuffer = fs.readFileSync(path.join(__dirname, "customers.json"));
const oldCustomers = JSON.parse(customersBuffer.toString());
// key: customerID, value: Array mit accountsIDs
const map = new Map();
for (let customer of oldCustomers) {
  if (map.has(customer.id)) {
    const storedAcc = map.get(customer.id);
    let accountIds = [...storedAcc.accountIds, customer.accountId];
    delete customer.accountId
    map.set(customer.id, {...customer, accountIds});
  } else {
    const accountIds = [customer.accountId];
    delete customer.accountId
    map.set(customer.id, {...customer, accountIds});
  }
}

const newCustomers = [];
for(let [id, customer] of map.entries()){
    newCustomers.push(customer);
}
console.log(newCustomers.length, oldCustomers.length);
fs.writeFileSync(
    path.join(__dirname, "new_customers.json"),
    JSON.stringify(newCustomers)
  );