package fr.sg.bankaccount.config;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.domain.AccountCreatedEvent;
import fr.sg.bankaccount.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * @author Rami SOLTANI created on 23/03/2021
 **/
@Configuration
public class EventStoreConfigure {

    @Autowired
    public EventStoreConfigure(EventStore eventStore) {
        eventStore.addEvent(Constants.MY_ACCOUNT_ID, new AccountCreatedEvent(Constants.MY_ACCOUNT_ID, "Rami", BigDecimal.ZERO));
    }
}
