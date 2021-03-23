package fr.sg.bankaccount.eventStore;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.domain.AccountCreatedEvent;
import fr.sg.bankaccount.eventstore.EventStore;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Rami SOLTANI created on 21/03/2021
 **/
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class EventStoreTest {
    private final EventStore eventStore;

    @Test
    void should_initializeMyAccount() {
        Optional<AccountCreatedEvent> accountCreatedEvent = eventStore.getEvents(Constants.MY_ACCOUNT_ID)
                .stream()
                .filter(AccountCreatedEvent.class::isInstance)
                .map(AccountCreatedEvent.class::cast)
                .findAny();
        assertThat(accountCreatedEvent)
                .get()
                .extracting(AccountCreatedEvent::accountId)
                .isEqualTo(Constants.MY_ACCOUNT_ID);
    }
}
