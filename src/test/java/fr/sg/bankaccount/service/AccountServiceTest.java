package fr.sg.bankaccount.service;

import fr.sg.bankaccount.Constants;
import fr.sg.bankaccount.command.DepositCommand;
import fr.sg.bankaccount.command.WithdrawlCommand;
import fr.sg.bankaccount.domain.DepositedEvent;
import fr.sg.bankaccount.domain.WithDrawnEvent;
import fr.sg.bankaccount.eventstore.EventStore;
import fr.sg.bankaccount.query.OperationType;
import fr.sg.bankaccount.query.Transaction;
import fr.sg.bankaccount.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountServiceTest {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final EventStore eventStore;

    @Test
    void should_addAmount_when_depositCommand() {
        BigDecimal depositAmount = BigDecimal.valueOf(100);
        DepositCommand depositCommand = new DepositCommand(Constants.MY_ACCOUNT_ID, depositAmount);
        accountService.makeADeposit(depositCommand);
        // expecting transaction repository to contain the deposit transaction
        List<Transaction> allByAccountId = transactionRepository.findAllByAccountId(Constants.MY_ACCOUNT_ID);
        assertThat(allByAccountId)
                .allMatch(e -> e.getOperation().equals(OperationType.DEPOSIT));

        Transaction depositTransaction = allByAccountId.get(0);
        assertThat(areEqual(depositTransaction.getAmount(), depositAmount)).isTrue();
        assertThat(areEqual(depositTransaction.getBalance(), depositAmount)).isTrue();

        // expecting event store to contain the deposited event
        Optional<DepositedEvent> depositedEvent = eventStore.getEvents(Constants.MY_ACCOUNT_ID)
                .stream()
                .filter(DepositedEvent.class::isInstance)
                .map(DepositedEvent.class::cast)
                .findFirst();

        assertThat(depositedEvent).get()
                .matches(event -> areEqual(depositAmount, event.amount()))
                .matches(event -> areEqual(depositAmount, event.balance()));
    }

    @Test
    void should_substractAmount_when_withdrawlCommand() {
        // we have to credit the account before any withdrawl
        DepositCommand depositCommand = new DepositCommand(Constants.MY_ACCOUNT_ID, BigDecimal.valueOf(100));
        accountService.makeADeposit(depositCommand);

        WithdrawlCommand withdrawlCommand = new WithdrawlCommand(Constants.MY_ACCOUNT_ID, BigDecimal.valueOf(99));
        accountService.makeAWithDrawl(withdrawlCommand);

        // expecting the transaction repo to have both transactions (Deposit and Withdrawl)
        List<Transaction> allByAccountId = transactionRepository.findAllByAccountId(Constants.MY_ACCOUNT_ID);
        assertThat(allByAccountId)
                .hasSize(2)
                .extracting(Transaction::getOperation).contains(OperationType.DEPOSIT, OperationType.WITHDRAWL);

        Optional<Transaction> firstWithdrawl = allByAccountId.stream()
                .filter(transaction -> transaction.getOperation().equals(OperationType.WITHDRAWL))
                .findFirst();

        assertThat(firstWithdrawl)
                .get()
                .matches(event -> areEqual(BigDecimal.valueOf(99), event.getAmount()))
                .matches(event -> areEqual(BigDecimal.valueOf(1), event.getBalance()));

        // expecting the event store to contains the Withdrawn event
        Optional<WithDrawnEvent> withdrawnEvent = eventStore.getEvents(Constants.MY_ACCOUNT_ID)
                .stream()
                .filter(WithDrawnEvent.class::isInstance)
                .map(WithDrawnEvent.class::cast)
                .findFirst();

        assertThat(withdrawnEvent)
                .get()
                .matches(event -> areEqual(BigDecimal.valueOf(99), event.amount()))
                .matches(event -> areEqual(BigDecimal.valueOf(1), event.balance()));
    }

    private static boolean areEqual(BigDecimal amount1, BigDecimal amount2) {
        return amount1.compareTo(amount2) == 0;
    }
}
