package fr.sg.bankaccount.repository;

import fr.sg.bankaccount.query.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TransactionRepository is the Query(read) repository (cQrs) for the Transactions (Withdrawl,Deposit)
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByAccountId(String accountId);
}
