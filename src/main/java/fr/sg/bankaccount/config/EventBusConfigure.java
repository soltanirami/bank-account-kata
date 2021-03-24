package fr.sg.bankaccount.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rami SOLTANI created on 24/03/2021
 **/
@Configuration
public class EventBusConfigure {
    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }
}
