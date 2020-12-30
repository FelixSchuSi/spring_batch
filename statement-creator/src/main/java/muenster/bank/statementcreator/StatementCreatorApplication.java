package muenster.bank.statementcreator;

import javax.swing.Spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatementCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.exit(SpringApplication.run(StatementCreatorApplication.class, args));
	}

}
