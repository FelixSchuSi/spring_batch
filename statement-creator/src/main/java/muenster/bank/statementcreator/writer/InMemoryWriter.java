package muenster.bank.statementcreator.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

public class InMemoryWriter implements ItemWriter<Object> {
    private StepExecution stepExecution;

    @Override
    public void write(List<? extends Object> items) throws Exception {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        String key = null;
        List<Object> resultList = new ArrayList<Object>();
        for (Object item: items){
            if (key == null){
                key = item.getClass().getSimpleName();
                resultList.add(item);
            }
        }
        stepContext.put(key, resultList);
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}

