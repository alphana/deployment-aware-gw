package name.alp.deploymentawaregw;

import name.alp.deploymentawaregw.deploymentinfo.DeploymentInfoProperties;
import name.alp.deploymentawaregw.upstreamchangeaware.KubeClientConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableConfigurationProperties(
        {DeploymentInfoProperties.class,
                KubeClientConfigProperties.class}
)
public class DeploymentAwareGwApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeploymentAwareGwApplication.class, args);
    }

}
