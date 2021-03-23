package fr.sg.bankaccount.service;

import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.domain.Account;
import fr.sg.bankaccount.domain.AccountEvent;
import fr.sg.bankaccount.eventstore.EventStore;
import fr.sg.bankaccount.projections.AccountProjector;
import fr.sg.bankaccount.validator.BankAccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.Validator;
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
    private final Validator validator;


    public void makeADeposit(DepositCommand depositCommand) {
        new BankAccountValidator<>(validator, depositCommand).validate();
        log.info("DepositCommand [{}] ", depositCommand.toString());
        Account account = recalculateBankAccount(depositCommand.accountId());
        // make a deposit
        AccountEvent depositedEvent = account.makeADeposit(depositCommand);
        eventStore.addEvent(account.getId(), depositedEvent);
        projection.project(depositedEvent);
    }

    public void makeAWithDrawl(@Valid WithdrawlCommand withdrawlCommand) {
        new BankAccountValidator<>(validator, withdrawlCommand).validate();
        log.info("WithDrawlCommand [{}] ", withdrawlCommand.toString());
        Account account = recalculateBankAccount(withdrawlCommand.accountId());
        // make a withdrawl
        AccountEvent withDrawnEvent = account.makeAWithDrawl(withdrawlCommand);
        // save the withDrawnEvent in the event store
        eventStore.addEvent(account.getId(), withDrawnEvent);
        // synchronize with the read repository
        projection.project(withDrawnEvent);
    }

    private Account recalculateBankAccount(String s) {
        return new Account(eventStore.getEvents(s).stream().map(AccountEvent.class::cast)
                .collect(Collectors.toList()));
    }

}
