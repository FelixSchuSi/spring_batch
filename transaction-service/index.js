const express = require("express");
const http = require("http");
const { startDB } = require("./start-db.js");

const requestMap = new Map();
const port = 3443;

function configureApp(app) {
  app.get("/", (req, res) => {
    const HEALTHMSG = "health check successfull!";
    console.log(HEALTHMSG);
    res.send(HEALTHMSG);
  });
  app.get("/transactions", async (req, res) => {
    const accountId = Number(req.query.accountId);
    if (Number.isNaN(accountId)) { res.sendStatus(400); return }
    const return500 = Math.random() > 0.5;
    if (return500 && requestMap.has(accountId) && requestMap.get(accountId) + 1 < 3) {
      const numberOfRequestsForId = requestMap.get(accountId) + 1;
      console.log(`${numberOfRequestsForId} time for acc ${accountId} - Server error`);
      requestMap.set(accountId, numberOfRequestsForId);
      res.sendStatus(500);
      return;
    } else if (return500 && !requestMap.has(accountId)) {
      console.log(`First time for acc ${accountId} - Server error`);
      requestMap.set(accountId, 1);
      res.sendStatus(500);
      return;
    } else if (requestMap.has(accountId) && requestMap.get(accountId) + 1 >= 3) {
      console.log(`Third time for acc ${accountId} - request successful`);
      requestMap.delete(accountId);
    } else {
      console.log(`acc ${accountId} - request successful`);
    }
    const { transactionDAO } = req.app.locals;
    const transactions = await transactionDAO.findAll({ accountId });

    res.json(transactions);
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
