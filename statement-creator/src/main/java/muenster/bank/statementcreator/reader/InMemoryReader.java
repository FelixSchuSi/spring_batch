package muenster.bank.statementcreator.reader;

import org.springframework.batch.core.JobExecution;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

public class InMemoryReader implements ItemReader<Object> {
    private StepExecution stepExecution;

    @Override
    public Object read() throws Exception {
        // ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        // Object result = stepContext.get("Account");
        JobExecution jobExecution = this.stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        Object result = jobContext.get("Account");
        // System.out.println("###########");
        System.out.println(result);
        // System.out.println("###########");
        return result;
    }

    @BeforeStep
    public void saveStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
