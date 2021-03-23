package fr.sg.bankaccount.domain;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Accessors(fluent = true)
@Getter
public abstract class AccountEvent implements Event {
    protected String id = UUID.randomUUID().toString();
    protected Instant time = LocalDateTime.now().toInstant(ZoneOffset.UTC);

    public abstract void visit(BankAccount bankAccount);

}
