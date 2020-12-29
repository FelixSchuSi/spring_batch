console.log('test');
const { generateIBAN } = require('./generateIBAN.js');
const fs = require("fs");
const path = require("path");

console.log(generateIBAN());

const accountsBuffer = fs.readFileSync(path.join(__dirname, "new_accounts.json"));
const oldAccounts = JSON.parse(accountsBuffer.toString());
const accountsWithIBAN = oldAccounts.map((account) => { account.iban = generateIBAN(); return account });
console.log(accountsWithIBAN);

fs.writeFileSync(
    path.join(__dirname, "new_accounts_IBAN.json"),
    JSON.stringify(accountsWithIBAN)
);