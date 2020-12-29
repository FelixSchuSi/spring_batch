package muenster.bank.statementcreator.writer;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class StatementHeaderWriter implements FlatFileHeaderCallback {
    public void writeHeader(Writer writer) throws IOException {
        writer.write(String.format("%120s\n", "Kundenservice Hotline"));
        writer.write(String.format("%120s\n", "(0800) 12345"));
        writer.write(String.format("%120s\n", "Rund um die Uhr für Sie erreichbar"));
        writer.write("\n");
    }
}