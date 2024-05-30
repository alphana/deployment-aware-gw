package name.alp.deploymentawaregw;


import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuples;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class DeploymentInformerService implements ApplicationEventPublisherAware {

    private final ConcurrentMap<String, Long> deploymentGenerations = new ConcurrentHashMap<>();

    private final KubernetesClient kubernetesClient;

    private ApplicationEventPublisher applicationEventPublisher;

    public DeploymentInformerService(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @PostConstruct
    public void init() {
        SharedInformerFactory informerFactory = kubernetesClient.informers();



        SharedIndexInformer<Deployment> deploymentInformer = informerFactory
                .inNamespace("ns1")
                .sharedIndexInformerFor(Deployment.class, 0);

        deploymentInformer.addEventHandler(new ResourceEventHandler<>() {
            @Override
            public void onAdd(Deployment deployment) {
                handleDeploymentEvent(deployment, "ADDED");
            }

            @Override
            public void onUpdate(Deployment oldDeployment, Deployment newDeployment) {
                handleDeploymentEvent(newDeployment, "MODIFIED");
            }

            @Override
            public void onDelete(Deployment deployment, boolean deletedFinalStateUnknown) {
                handleDeploymentEvent(deployment, "DELETED");
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

        if ("MODIFIED".equals(eventType) && isDeploymentUpdate) {
            log.info("Deployment Update Started: {}", deploymentName);
            deploymentGenerations.put(deploymentName, currentGeneration);
            // Add your logic here to handle the start of a deployment update
            applicationEventPublisher.publishEvent(
                    new UpstreamServiceChangedEvent(
                            Tuples.of(deploymentName,currentGeneration,UpstreamServiceChangedEventTypes.UPDATE_STARTED))
            );
        } else if (deploymentInProgress) {
            log.info("Deployment Update In Progress: {}", deploymentName);
            // Add your logic here to handle ongoing deployment updates
            applicationEventPublisher.publishEvent(
                    new UpstreamServiceChangedEvent(
                            Tuples.of(deploymentName,currentGeneration,UpstreamServiceChangedEventTypes.UPDATE_INPROGRESS))
            );
        } else if ("MODIFIED".equals(eventType) && !deploymentInProgress) {
            log.info("Deployment Update Finished: {}", deploymentName);
            deploymentGenerations.remove(deploymentName);
            applicationEventPublisher.publishEvent(
                    new UpstreamServiceChangedEvent(
                            Tuples.of(deploymentName,currentGeneration,UpstreamServiceChangedEventTypes.UPDATE_FINISHED))
            );
            // Add your logic here to handle the completion of a deployment update
        }

        String image = deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String[] parts = image.split(":");
        String imageTag = parts.length > 1 ? parts[1] : "latest";

        log.info("Event Type: {}", eventType);
        log.info("Deployment Name: {}", deploymentName);
        log.info("Image: {}", image);
        log.info("Image Tag: {}", imageTag);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher= applicationEventPublisher;
    }
}
