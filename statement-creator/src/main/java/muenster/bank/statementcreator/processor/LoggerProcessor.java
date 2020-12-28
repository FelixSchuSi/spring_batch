package muenster.bank.statementcreator.processor;

import org.springframework.batch.item.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProcessor<T> implements ItemProcessor<T, T> {
    Logger logger = LoggerFactory.getLogger(LoggerProcessor.class);

    @Override
    public T process(T item) throws Exception {
        logger.info(item.toString());
        return item;
    }
}
