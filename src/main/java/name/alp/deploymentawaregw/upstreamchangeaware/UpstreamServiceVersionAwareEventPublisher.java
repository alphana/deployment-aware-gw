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
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class UpstreamServiceVersionAwareEventPublisher implements ApplicationEventPublisherAware {


    private final ConcurrentMap<String, Long> deploymentGenerations = new ConcurrentHashMap<>();

    private final KubernetesClient kubernetesClient;

    private ApplicationEventPublisher applicationEventPublisher;

    private final KubeClientConfigProperties kubeClientConfigProperties;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public UpstreamServiceVersionAwareEventPublisher(KubernetesClient kubernetesClient,KubeClientConfigProperties kubeClientConfigProperties) {
        this.kubernetesClient = kubernetesClient;
        this.kubeClientConfigProperties = kubeClientConfigProperties;
    }

    @PostConstruct
    public void init() {
        SharedInformerFactory informerFactory = kubernetesClient.informers();


        SharedIndexInformer<Deployment> deploymentInformer = informerFactory
                .inNamespace(kubeClientConfigProperties.getClientNamespace())
                .sharedIndexInformerFor(Deployment.class, 0);

        deploymentInformer.addEventHandler(new ResourceEventHandler<>() {
            @Override
            public void onAdd(Deployment deployment) {
                log.trace("{} , {}", deployment, "ADDED");
            }

            @Override
            public void onUpdate(Deployment oldDeployment, Deployment newDeployment) {
                log.trace("Deployment updated with new image: {}", newDeployment.getMetadata().getName());
                handleImageVersionChange(oldDeployment, newDeployment);
            }

            @Override
            public void onDelete(Deployment deployment, boolean deletedFinalStateUnknown) {
                log.trace("{} , {}", deployment, "DELETED");
            }
        });

        informerFactory.startAllRegisteredInformers();

    }

    private void handleImageVersionChange(Deployment oldDeployment, Deployment newDeployment) {

        String oldImage = oldDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
        String newImage = newDeployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();


        String deploymentName = newDeployment.getMetadata().getName();
        Long currentGeneration = newDeployment.getMetadata().getGeneration();
        Long observedGeneration = newDeployment.getStatus().getObservedGeneration();

        // Check if this is a deployment update
        boolean isDeploymentUpdate = currentGeneration != null && !currentGeneration.equals(deploymentGenerations.get(deploymentName));
        boolean deploymentInProgress = currentGeneration != null && !currentGeneration.equals(observedGeneration);


        if (isDeploymentUpdate && !oldImage.equals(newImage)) {

            deploymentGenerations.put(deploymentName, currentGeneration);
            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(
                    new UpstreamServiceChangeEventSource(deploymentName, oldImage, newImage, currentGeneration, UpstreamServiceChangedEventTypes.UPDATE_STARTED));
            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update Started: {}", changeEvent);

        } else if (deploymentInProgress) {

            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(
                    new UpstreamServiceChangeEventSource(deploymentName, oldImage, newImage, currentGeneration, UpstreamServiceChangedEventTypes.UPDATE_IN_PROGRESS));
//            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update In Progress: {}", changeEvent);

        } else if (oldImage.equals(newImage) && deploymentGenerations.containsKey(deploymentName)) {

            deploymentGenerations.remove(deploymentName);
            ApplicationEvent changeEvent = new UpstreamServiceChangedEvent(
                    new UpstreamServiceChangeEventSource(deploymentName, oldImage, newImage, Objects.requireNonNull(currentGeneration), UpstreamServiceChangedEventTypes.UPDATE_FINISHED));
            applicationEventPublisher.publishEvent(changeEvent);
            log.trace("Deployment Update Finished: {}", changeEvent);

        }

    }

}
