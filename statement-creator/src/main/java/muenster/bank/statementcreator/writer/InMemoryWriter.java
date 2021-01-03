package muenster.bank.statementcreator.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class InMemoryWriter<T> implements ItemWriter<T> {
    private ExecutionContext stepContext;
    private List<T> resultList = new ArrayList<T>();
    private String key = null;

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            if (key == null) {
                key = item.getClass().getSimpleName();
            }
            resultList.add(item);
        }
        stepContext.put(key, resultList);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        stepContext = stepExecution.getExecutionContext();
    }
}
