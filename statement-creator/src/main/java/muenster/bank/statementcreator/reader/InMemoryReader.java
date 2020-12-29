package muenster.bank.statementcreator.reader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

public class InMemoryReader implements ItemReader<Object> {
    private List<Object> data;
    private int index;

    @Override
    public Object read() throws Exception {
        Object result = null;
        if (data.size() != 0 && index < data.size()){
            result = data.get(index);
            index++;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @BeforeStep
    public void prepare(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        try {
            this.data = (List<Object>) jobContext.get("Account");
        } catch (Exception e) {
            this.data =  new ArrayList<Object>();
        }
        this.index = 0;
    }
}
