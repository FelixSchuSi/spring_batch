package hello.world.helloworld;

import org.springframework.batch.item.ItemProcessor;

public class GreetingProcessor implements ItemProcessor<String, String> {

    @Override
    public String process(String name) throws Exception {
        return "Hello " + name + "!";
    }

}
