package name.alp.deploymentawaregw;

import lombok.extern.slf4j.Slf4j;
import name.alp.deploymentawaregw.upstreamchangeaware.UpstreamServiceChangedEvent;
import name.alp.deploymentawaregw.upstreamchangeaware.UpstreamServiceChangedEventTypes;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SomeFilterService {//Validate requestresponse

    @EventListener
    public void changeListener(UpstreamServiceChangedEvent upstreamServiceChangedEvent){
      log.info("Event : {}",upstreamServiceChangedEvent);

      if(UpstreamServiceChangedEventTypes.UPDATE_STARTED.equals(upstreamServiceChangedEvent.getEventSource().eventType())){
          log.info("maintenance started");
      } else if (UpstreamServiceChangedEventTypes.UPDATE_FINISHED.equals(upstreamServiceChangedEvent.getEventSource().eventType())) {
          log.info("post maintenance process started");
      }

    }
}
