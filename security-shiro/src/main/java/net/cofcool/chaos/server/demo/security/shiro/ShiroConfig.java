package net.cofcool.chaos.server.demo.security.shiro;

import lombok.extern.slf4j.Slf4j;
import net.cofcool.chaos.server.security.shiro.authorization.ShiroApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class ShiroConfig {

    @EventListener(classes = ShiroApplicationEvent.class)
    public void shiroEventListener(ShiroApplicationEvent shiroApplicationEvent) {
        log.info("login event: {}", shiroApplicationEvent);
    }

}
