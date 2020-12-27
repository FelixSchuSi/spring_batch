const express = require("express");
const bodyParser = require("body-parser");
const http = require("http");
const {startDB} = require("./db.js")
// import startDB from "./db";

const port = 3443;

function configureApp(app) {
  app.get("/", (req, res) => {
    const healthCheckMessage = "health check successfull!";
    console.log(healthCheckMessage);
    res.send(healthCheckMessage);
  });
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: true }));
  app.get("/:id", async (req, res) => {
    const accountId = req.params.id;
    // TODO: read from DB
  });
  // app.use(cookieParser());
  // app.use((req, res, next) => {
  //   if (isOriginAllowed(req.get("Origin"))) {
  //     res.set("Access-Control-Allow-Origin", req.get("Origin"));
  //     res.set("Access-Control-Allow-Credentials", "true");
  //   }
  //   if (isPreflight(req)) {
  //     res.set("Access-Control-Allow-Headers", "Content-Type");
  //     res.set("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE");
  //     res.status(204).end();
  //   } else {
  //     next();
  //   }
  // });
  // app.use("/api/meals", meals);
  // app.use("/api/users", users);
  // app.use((req, res, next) => {
  //   const token = req.cookies["jwt-token"] || "";
  //   try {
  //     res.locals.user = jwt.verify(token, "mysecret");
  //     next();
  //   } catch (error) {
  //     res.status(401).json({ message: "Bitte melden Sie sich an!" });
  //   }
  // });
  // app.use("/api/tasks", tasks);
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
