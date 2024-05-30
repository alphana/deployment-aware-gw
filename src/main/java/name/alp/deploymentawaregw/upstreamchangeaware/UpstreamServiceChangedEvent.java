package name.alp.deploymentawaregw;

import org.springframework.context.ApplicationEvent;
import reactor.util.function.Tuple3;

public class UpstreamServiceChangedEvent extends ApplicationEvent {
    public UpstreamServiceChangedEvent(Tuple3<String, Long, UpstreamServiceChangedEventTypes> eventSource) {
        super(eventSource);
    }
}
