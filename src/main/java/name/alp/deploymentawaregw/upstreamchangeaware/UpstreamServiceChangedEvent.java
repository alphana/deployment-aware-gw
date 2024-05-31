package name.alp.deploymentawaregw.upstreamchangeaware;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class UpstreamServiceChangedEvent extends ApplicationEvent {
    private final UpstreamServiceChangeEventSource eventSource;

    public UpstreamServiceChangedEvent(UpstreamServiceChangeEventSource source) {
        super(source);
        this.eventSource = source;
    }

    public UpstreamServiceChangeEventSource getEventSource() {
        return eventSource;
    }

    @Override
    public String toString() {
        return "UpstreamServiceChangedEvent{" +
               "timestamp=" + Instant.ofEpochMilli( this.getTimestamp() )+
                                                   "eventSource=" + eventSource +
                                                   '}';
    }
}

