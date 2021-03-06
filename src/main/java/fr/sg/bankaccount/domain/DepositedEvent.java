package fr.sg.bankaccount.domain;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Value
public class DepositedEvent extends AccountEvent {
    String accountId;
    BigDecimal amount;
    BigDecimal balance;

    @Override
    public void visit(Account account) {
        account.accept(this);
    }
}
