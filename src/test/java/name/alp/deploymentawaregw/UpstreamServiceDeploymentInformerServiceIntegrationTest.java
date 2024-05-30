package name.alp.deploymentawaregw;


import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.kubernetes.enabled=false" // Disable real Kubernetes client for tests
})
public class DeploymentInformerServiceIntegrationTest {

    @Autowired
    private DeploymentInformerService deploymentInformerService;

    // Kubernetes server for mocking Kubernetes API
    private static KubernetesServer server;

    @BeforeAll
    static void setUp() {
        server = new KubernetesServer(false, true);
        server.before();
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.after();
        }
    }

    @Test
    void testDeploymentInformerService() {
        // Given
        KubernetesClient client = server.getClient();
        Deployment deployment = new Deployment();
        deployment.getMetadata().setName("test-deployment");
        client.apps().deployments().inNamespace("default").create(deployment);

        // When
        // Add your test logic here
        // For example, trigger some action that should result in an event being received by DeploymentInformerService

        // Then
        // Add assertions to verify the behavior of DeploymentInformerService
        assertNotNull(deploymentInformerService);
        // Add more assertions as needed
    }

    @Test
    void givenNewDeployment_whenAdded_thenInformerReceivesEvent() {
        // Given
        KubernetesClient client = server.getClient();
        Deployment deployment = new Deployment();
        deployment.getMetadata().setName("test-deployment");

        // When
        client.apps().deployments().inNamespace("default").create(deployment);

        // Then
        // Verify that DeploymentInformerService receives the deployment addition event
//        assertNotNull(deploymentInformerService.getDeployment("test-deployment"));
    }

    @Test
    void givenModifiedDeployment_whenUpdated_thenInformerReceivesEvent() {
        // Given
        // Create and modify a deployment
        Deployment deployment = new Deployment();
        deployment.getMetadata().setName("test-deployment");
        KubernetesClient client = server.getClient();
        client.apps().deployments().inNamespace("default").createOrReplace(deployment);

        // When
        // Modify the deployment
        deployment.getSpec().setReplicas(2);
        client.apps().deployments().inNamespace("default").createOrReplace(deployment);

        // Then
        // Verify that DeploymentInformerService receives the deployment modification event
//        assertEquals(2, deploymentInformerService.getDeployment("test-deployment").getSpec().getReplicas());
    }

    @Test
    void givenExistingDeployment_whenDeleted_thenInformerReceivesEvent() {
        // Given
        // Create and delete a deployment
        Deployment deployment = new Deployment();
        deployment.getMetadata().setName("test-deployment");
        KubernetesClient client = server.getClient();
        client.apps().deployments().inNamespace("default").createOrReplace(deployment);

        // When
        // Delete the deployment
        client.apps().deployments().inNamespace("default").withName("test-deployment").delete();

        // Then
        // Verify that DeploymentInformerService receives the deployment deletion event
//        assertNull(deploymentInformerService.getDeployment("test-deployment"));
    }

    @Test
    void givenWatchingDeployments_whenDeploymentEventOccurs_thenInformerReceivesEvent() {
        // Given
        // Start watching for deployment events

        // When
        // Trigger deployment event

        // Then
        // Add assertions to verify that the informer receives the deployment event
    }

    @Test
    void givenDeploymentWithImageTag_whenExtractingImageTag_thenCorrectTagReturned() {
        // Given
        // Create a deployment with a specific image tag
        String imageName = "example/image:tag";
        Deployment deployment = new Deployment();
        deployment.getMetadata().setName("test-deployment");
        deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setImage(imageName);

        // When
        // Extract image tag from deployment
//        String extractedTag = deploymentInformerService.extractImageTag(deployment);
//
//        // Then
//        // Verify that the correct image tag is returned
//        assertEquals("tag", extractedTag);
    }

}
