package fr.sg.bankaccount.domain;

import java.time.Instant;

/**
 * Event is the base model for all events
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/

public interface Event {
    String id();

    Instant time();
}
