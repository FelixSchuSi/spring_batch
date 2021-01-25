const requestMap = new Map();
const MESSAGE_500 = "500 Internal Server Error"

function random500(accountId) {
    const return500 = Math.random() > 0.5;
    const ERROR_MESSAGE = `Error while handling request with parameter accountId=${accountId}: ${MESSAGE_500}`;
    if (return500 && requestMap.has(accountId) && requestMap.get(accountId) + 1 < 3) {
        const numberOfRequestsForId = requestMap.get(accountId) + 1;
        console.log(ERROR_MESSAGE);
        requestMap.set(accountId, numberOfRequestsForId);
        return true;
    } else if (return500 && !requestMap.has(accountId)) {
        console.log(ERROR_MESSAGE);
        requestMap.set(accountId, 1);
        return true;
    } else if (requestMap.has(accountId) && requestMap.get(accountId) + 1 >= 3) {
        requestMap.delete(accountId);
    }
    return false;
}

exports.random500 = random500;