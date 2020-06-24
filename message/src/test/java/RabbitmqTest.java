import net.cofcool.chaos.server.demo.message.mq.MessageService;
import net.cofcool.chaos.server.demo.message.mq.RabbitConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RabbitmqTest {

    @Qualifier(value = "messageService")
    @Autowired
    private MessageService messageService;

    @Test
    public void init() {
        messageService.initMq();
    }

    @Test
    public void sendMessage() {
        messageService.sendMessage();
    }

    @Test
    public void receiveMessage() {
        messageService.receiveMessage();
    }

}
