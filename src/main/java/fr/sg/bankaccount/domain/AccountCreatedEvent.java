package fr.sg.bankaccount.domain;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * AccountCreatedEvent is an event for an Account Creation
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@Value
public class AccountCreatedEvent extends AccountEvent {
    String accountId;
    String owner;
    BigDecimal balance;

    @Override
    public void visit(Account account) {
        account.accept(this);
    }
}
