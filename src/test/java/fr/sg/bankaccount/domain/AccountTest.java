package fr.sg.bankaccount.domain;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.exceptions.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

/**
 * @author Rami SOLTANI created on 23/03/2021
 **/

class AccountTest {
    @Test
    void should_throw_insufficientBalanceException_when_zeroBalance() {
        String id = "123";
        Account account = new Account(singletonList(new AccountCreatedEvent(id, "soltani", BigDecimal.ZERO)));
        WithdrawlCommand withdrawlCommand = new WithdrawlCommand(id, BigDecimal.ONE);
        assertThatThrownBy(() -> account.makeAWithDrawl(withdrawlCommand)).isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void should_deposit_whithout_any_problem() {
        String id = "333";
        Account account = new Account(singletonList(new AccountCreatedEvent(id, "croixRouge", BigDecimal.ZERO)));
        BigDecimal amount = BigDecimal.valueOf(Integer.MAX_VALUE);
        DepositCommand depositCommand = new DepositCommand(id, amount);
        AccountEvent accountEvent = account.makeADeposit(depositCommand);
        assertThat(areEqual(account.balance, amount)).isTrue();
        assertThat(accountEvent)
                .asInstanceOf(type(DepositedEvent.class))
                .matches(de -> areEqual(de.amount(), amount))
                .matches(de -> areEqual(de.balance(), amount));
    }

    private static boolean areEqual(BigDecimal amount1, BigDecimal amount2) {
        return amount1.compareTo(amount2) == 0;
    }
}

