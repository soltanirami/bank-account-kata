package fr.sg.bankaccount.projections;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import fr.sg.bankaccount.domain.AccountEvent;
import fr.sg.bankaccount.domain.DepositedEvent;
import fr.sg.bankaccount.domain.WithDrawnEvent;
import fr.sg.bankaccount.query.OperationType;
import fr.sg.bankaccount.query.Transaction;
import fr.sg.bankaccount.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * AccountProjection permits to project the triggered event to the Query repository(cQrs) with the new state
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Slf4j
@Component
public class AccountProjector {
    private final TransactionRepository transactionRepository;
    private final EventBus eventBus;

    @Autowired
    public AccountProjector(TransactionRepository transactionRepository, EventBus eventBus) {
        this.transactionRepository = transactionRepository;
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Subscribe
    public void project(AccountEvent event) {
        if (event instanceof DepositedEvent) {
            apply((DepositedEvent) event);
        }
        if (event instanceof WithDrawnEvent) {
            apply((WithDrawnEvent) event);
        }
    }

    private void apply(DepositedEvent event) {
        saveTransaction(event.id(), OperationType.DEPOSIT, event.accountId(), event.time(), event.amount(), event.balance());
    }

    private void apply(WithDrawnEvent event) {
        saveTransaction(event.id(), OperationType.WITHDRAWL, event.accountId(), event.time(), event.amount(), event.balance());
    }

    private void saveTransaction(String id, OperationType operationType, String accountId, Instant time, BigDecimal amount, BigDecimal balance) {
        Transaction transacation = new Transaction(id, accountId, operationType, LocalDateTime.ofInstant(time, ZoneOffset.UTC), amount, balance);
        transactionRepository.save(transacation);
        log.info("Saved Transaction : [{}]", transacation.toString());
    }


}
