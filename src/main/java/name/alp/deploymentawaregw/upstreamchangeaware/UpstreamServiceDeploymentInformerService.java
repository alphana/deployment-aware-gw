package name.alp.deploymentawaregw.upstreamchangeaware;


import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class UpstreamServiceDeploymentInformerService { //implements ApplicationEventPublisherAware {

    public static final String ADDED = "ADDED";
    public static final String MODIFIED = "MODIFIED";
    public static final String DELETED = "DELETED";
    private final ConcurrentMap<String, Long> deploymentGenerations = new ConcurrentHashMap<>();

    private final KubernetesClient kubernetesClient;

    private ApplicationEventPublisher applicationEventPublisher;

    public UpstreamServiceDeploymentInformerService(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

//    @PostConstruct
    public void init() {
        SharedInformerFactory informerFactory = kubernetesClient.informers();


        SharedIndexInformer<Deployment> deploymentInformer = informerFactory.inNamespace("ns1").sharedIndexInformerFor(Deployment.class, 0);

        deploymentInformer.addEventHandler(new ResourceEventHandler<>() {
            @Override
            public void onAdd(Deployment deployment) {
                handleDeploymentEvent(deployment, ADDED);
            }

            @Override
            public void onUpdate(Deployment oldDeployment, Deployment newDeployment) {
                handleDeploymentEvent(newDeployment, MODIFIED);
            }

            @Override
            public void onDelete(Deployment deployment, boolean deletedFinalStateUnknown) {
                handleDeploymentEvent(deployment, DELETED);
            }
        });

        informerFactory.startAllRegisteredInformers();

    }

    private void handleDeploymentEvent(Deployment deployment, String eventType) {
        String deploymentName = deployment.getMetadata().getName();
        Long currentGeneration = deployment.getMetadata().getGeneration();
        Long observedGeneration = deployment.getStatus().getObservedGeneration();

        // Check if this is a deployment update
        boolean isDeploymentUpdate = currentGeneration != null && !currentGeneration.equals(deploymentGenerations.get(deploymentName));
        boolean deploymentInProgress = currentGeneration != null && !currentGeneration.equals(observedGeneration);


        String image = deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String[] parts = image.split(":");
        String imageTag = parts.length > 1 ? parts[1] : "latest";

        if (MODIFIED.equals(eventType) && isDeploymentUpdate) {

            deploymentGenerations.put(deploymentName, currentGeneration);
            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(new UpstreamServiceChangeEventSource(deploymentName, image, imageTag, currentGeneration, UpstreamServiceChangedEventTypes.UPDATE_STARTED));
            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update Started: {}", changeEvent);

        } else if (deploymentInProgress) {

            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(new UpstreamServiceChangeEventSource(deploymentName, image, imageTag, currentGeneration, UpstreamServiceChangedEventTypes.UPDATE_IN_PROGRESS));
            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update In Progress: {}", changeEvent);

        } else if (MODIFIED.equals(eventType) && !deploymentInProgress) {

            deploymentGenerations.remove(deploymentName);
            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(new UpstreamServiceChangeEventSource(deploymentName, image, imageTag, Objects.requireNonNull(currentGeneration), UpstreamServiceChangedEventTypes.UPDATE_FINISHED));
            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update Finished: {}", changeEvent);

        }


    }

//    @Override
//    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
}
