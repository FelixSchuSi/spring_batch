package muenster.bank.statementcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatementCreatorApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(StatementCreatorApplication.class, args)));
    }
}
