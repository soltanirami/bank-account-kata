package fr.sg.bankaccount.eventstore;

import com.google.common.collect.Lists;
import fr.sg.bankaccount.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventStore represent the repository of events
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Slf4j
@Component
public class EventStore {
    private final Map<String, List<Event>> events = new HashMap<>();

    public void addEvent(String accountId, Event event) {
        this.events.computeIfAbsent(accountId, s -> Lists.newArrayList());
        this.events.computeIfPresent(accountId, (s, eventList) -> {
            eventList.add(event);
            return eventList;
        });
        log.info("Adding event :[{}] to Account : [{}]", event, accountId);
    }

    public List<Event> getEvents(String ressourceId) {
        return this.events.get(ressourceId);
    }

}
