package fr.sg.bankaccount.service;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.domain.AccountEvent;
import fr.sg.bankaccount.domain.BankAccount;
import fr.sg.bankaccount.eventstorage.EventStore;
import fr.sg.bankaccount.projections.AccountProjector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * AccountService permits to synchronize data between Command side (Cqrs) and the Query side (cQrs)
 *
 * @author Rami SOLTANI created on 22/03/2021
 **/
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {
    private final EventStore eventStore;
    private final AccountProjector projection;


    public void makeADeposit(DepositCommand depositCommand) {
        log.info("DepositCommand [{}] ", depositCommand.toString());
        BankAccount bankAccount = recalculateBankAccount(depositCommand.accountId());
        // make a deposit
        AccountEvent depositedEvent = bankAccount.makeADeposit(depositCommand);
        eventStore.addEvent(bankAccount.getId(), depositedEvent);
        projection.project(depositedEvent);
    }

    public void makeAWithDrawl(WithdrawlCommand withdrawlCommand) {
        log.info("WithDrawlCommand [{}] ", withdrawlCommand.toString());
        BankAccount bankAccount = recalculateBankAccount(withdrawlCommand.accountId());
        // make a withdrawl
        AccountEvent withDrawnEvent = bankAccount.makeAWithDrawl(withdrawlCommand);
        // save the withDrawnEvent in the event store
        eventStore.addEvent(bankAccount.getId(), withDrawnEvent);
        // synchronize with the read repository
        projection.project(withDrawnEvent);
    }

    private BankAccount recalculateBankAccount(String s) {
        return new BankAccount(eventStore.getEvents(s).stream().map(AccountEvent.class::cast)
                .collect(Collectors.toList()));
    }

}
