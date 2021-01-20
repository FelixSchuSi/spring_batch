FROM node:14

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

ENV PORT=3443

EXPOSE 3443

CMD [ "npm", "start" ]