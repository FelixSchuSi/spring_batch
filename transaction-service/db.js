const { InMemoryDAO } = require("./in-memory.dao.js");
const fs = require("fs");
const path = require("path");

async function startDB(app) {
  const transactionDAO = new InMemoryDAO();
  const transactionsBuffer = fs.readFileSync(
    path.join(__dirname, "transactions.json")
  );

  const transactions = JSON.parse(transactionsBuffer.toString());
  for (transaction of transactions) {
    await transactionDAO.create(transaction);
  }
  app.locals.transactionDAO = transactionDAO;
}

exports.startDB = startDB;
