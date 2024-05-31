package name.alp.deploymentawaregw.upstreamchangeaware;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kube.client")
@Getter
@Setter
public class KubeClientConfigProperties {
    private String clientNamespace;
    private String username;
}
