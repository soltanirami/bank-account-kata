package fr.sg.bankaccount.eventstorage;

import com.google.common.collect.Lists;
import fr.sg.bankaccount.domain.Event;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EventStore represent the repository of events
 *
 * @author Rami SOLTANI created on 21/03/2021
 **/
@Component
public class EventStore {
    private final Map<String, List<Event>> events = new HashMap<>();

    public void addEvent(String accountId, Event event) {
        this.events.computeIfAbsent(accountId, s -> Lists.newArrayList());
        this.events.computeIfPresent(accountId, (s, eventList) -> {
            eventList.add(event);
            return eventList;
        });
    }

    public List<Event> getEvents(String ressourceId) {
        return this.events.get(ressourceId);
    }

}
