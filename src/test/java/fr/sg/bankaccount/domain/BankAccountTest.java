package fr.sg.bankaccount.domain;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.eventstorage.EventStore;
import fr.sg.bankaccount.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author Rami SOLTANI created on 23/03/2021
 **/
@SpringBootTest
class BankAccountTest {
    @MockBean
    EventStore eventStore;

    @Test()
    void should_throw_insufficientBalanceException_when_zeroBalance() {
        String id = "123";
        Mockito.when(eventStore.getEvents(id)).thenReturn(Collections.singletonList(new AccountCreatedEvent(id, "soltani", BigDecimal.ZERO)));
        BankAccount bankAccount = new BankAccount(eventStore.getEvents(id).stream().map(AccountCreatedEvent.class::cast).collect(Collectors.toList()));
        WithdrawlCommand withdrawlCommand = new WithdrawlCommand(id, BigDecimal.ONE);
        Assertions.assertThrows(InsufficientBalanceException.class, () -> bankAccount.makeAWithDrawl(withdrawlCommand));
    }

    @Test()
    void should_deposit_whithout_any_problem() {
        String id = "333";
        Mockito.when(eventStore.getEvents(id)).thenReturn(Collections.singletonList(new AccountCreatedEvent(id, "croixRouge", BigDecimal.ZERO)));
        BankAccount bankAccount = new BankAccount(eventStore.getEvents(id).stream().map(AccountCreatedEvent.class::cast).collect(Collectors.toList()));
        BigDecimal amount = BigDecimal.valueOf(Integer.MAX_VALUE);
        DepositCommand depositCommand = new DepositCommand(id, amount);
        AccountEvent accountEvent = bankAccount.makeADeposit(depositCommand);
        org.assertj.core.api.Assertions.assertThat(accountEvent).isInstanceOf(DepositedEvent.class);
        org.assertj.core.api.Assertions.assertThat((DepositedEvent) accountEvent).extracting(de -> de.amount().compareTo(amount)).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat((DepositedEvent) accountEvent).extracting(de -> de.balance().compareTo(amount)).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(bankAccount.balance.compareTo(amount)).isEqualByComparingTo(0);
    }
}
