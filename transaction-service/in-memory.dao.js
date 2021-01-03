class InMemoryDAO {
  entities = new Map();

  async create(partEntity) {
    const entity = {
      ...partEntity,
      id: partEntity.transactionId || 0
    };
    this.entities.set(entity.id, entity);
    return Promise.resolve(entity);
  }

  async findOne(entityFilter) {
    for (const entity of this.entities.values()) {
      if (this._matches(entity, entityFilter)) {
        return Promise.resolve(entity);
      }
    }
    return Promise.resolve(null);
  }

  async findAll(entityFilter) {
    const result = [];
    for (const entity of this.entities.values()) {
      if (!entityFilter || this._matches(entity, entityFilter)) {
        result.push(entity);
      }
    }
    return Promise.resolve(result);
  }

  async update(entity) {
    if (entity.id && this.entities.has(entity.id)) {
      this._update(this.entities.get(entity.id), entity);
      return Promise.resolve(true);
    } else {
      return Promise.resolve(false);
    }
  }

  async deleteOne(id) {
    return Promise.resolve(this.entities.delete(id));
  }

  _matches(entity, filter) {
    for (const prop of Object.getOwnPropertyNames(filter)) {
      if (entity[prop] !== filter[prop]) {
        return false;
      }
    }
    return true;
  }

  _update(entity, updateEntity) {
    for (const prop of Object.getOwnPropertyNames(updateEntity)) {
      entity[prop] = updateEntity[prop];
    }
  }
}

exports.InMemoryDAO = InMemoryDAO;
