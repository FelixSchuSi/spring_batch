const express = require("express");
const http = require("http");
const { startDB } = require("./start-db.js");
const { random500 } = require("./random-500.js");

const port = 3443;
const RANDOMLY_RETURN_500 = true;

function configureApp(app) {
  app.get("/", (req, res) => {
    const HEALTHMSG = "health check successfull!";
    console.log(HEALTHMSG);
    res.send(HEALTHMSG);
  });
  app.get("/transactions", async (req, res) => {
    const accountId = Number(req.query.accountId);
    if (Number.isNaN(accountId)) { res.sendStatus(400); return }
    if (RANDOMLY_RETURN_500 && random500(accountId)) { res.sendStatus(500); return }

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
