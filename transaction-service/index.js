const express = require("express");
const http = require("http");
const { startDB } = require("./db.js");

const port = 3443;

function configureApp(app) {
  app.get("/", (req, res) => {
    const healthCheckMessage = "health check successfull!";
    console.log(healthCheckMessage);
    res.send(healthCheckMessage);
  });
  app.get("/transactions", async (req, res) => {
    const accountId = Number(req.query.accountId);
    console.log(accountId, req.query.accountId);
    if (!accountId) { res.sendStatus(400); return }

    const { transactionDAO } = req.app.locals;
    const transactions = await transactionDAO.findAll({ accountId });

    console.log(`request for accountId: ${accountId}`);
    console.log(`returning ${transactions.length} transactions.`);

    res.json({ transactions });
  });
}

async function start() {
  const app = express();
  configureApp(app);
  await startDB(app);
  startHttpServer(app);
}

function startHttpServer(app) {
  const httpServer = http.createServer(app);
  httpServer.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
  });
}

start();
