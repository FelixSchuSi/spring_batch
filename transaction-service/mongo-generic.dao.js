class MongoGenericDAO {
  constructor(db, collection) {}

  async create(entity) {
    await this.db.collection(this.collection).insertOne(entity);
    return entity;
  }

  async findAll(entityFilter) {
    return this.db
      .collection(this.collection)
      .find(entityFilter)
      .sort({ createdAt: -1 })
      .toArray();
  }

  async findOne(entityFilter) {
    return this.db.collection(this.collection).findOne(entityFilter);
  }

  async update(entity) {
    const result = await this.db
      .collection(this.collection)
      .updateOne({ id: entity.id }, { $set: entity });
    return !!result.modifiedCount;
  }

  async deleteOne(id) {
    const result = await this.db.collection(this.collection).deleteOne({ id });
    return !!result.deletedCount;
  }

  async deleteAll(entityFilter) {
    const result = await this.db
      .collection(this.collection)
      .deleteMany(entityFilter);
    return !!result.deletedCount;
  }
}

exports.MongoGenericDAO = MongoGenericDAO;