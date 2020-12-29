package muenster.bank.statementcreator.reader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

public class InMemoryReader<T> implements ItemReader<T> {
    private List<T> data;
    private int index;
    private String key;

    public InMemoryReader(String key) {
        this.key = key;
    }

    @Override
    public T read() throws Exception {
        T result = null;
        if (data.size() != 0 && index < data.size()) {
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
            this.data = (List<T>) jobContext.get(key);
        } catch (Exception e) {
            this.data = new ArrayList<T>();
        }
        this.index = 0;
    }
}
