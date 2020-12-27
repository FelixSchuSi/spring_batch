const { MongoClient } = require("mongodb");
const { MongoGenericDAO } = require("./mongo-generic.dao.js");

async function startDB(app) {
  const db = await connect();
  app.locals.taskDAO = new MongoGenericDAO()(db, "transactions");
}

async function connect() {
  const url = "mongodb://localhost:27017";
  const options = {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    auth: { user: "root", password: "example" },
    authSource: "transactions",
  };

  try {
    const mongoClient = await MongoClient.connect(url, options);
    return mongoClient.db("transactions");
  } catch (err) {
    console.log("Could not connect to MongoDB: ", err.stack);
    process.exit(1);
  }
}

exports.startDB = startDB;