package fr.sg.bankaccount.eventStore;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.domain.AccountCreatedEvent;
import fr.sg.bankaccount.eventstorage.EventStore;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

/**
 * @author Rami SOLTANI created on 21/03/2021
 **/
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        Assertions.assertThat(accountCreatedEvent).isPresent();
        Assertions.assertThat(accountCreatedEvent.get()).extracting(AccountCreatedEvent::accountId)
                .isEqualTo(Constants.MY_ACCOUNT_ID);
    }
}
