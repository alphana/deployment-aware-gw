package name.alp.deploymentawaregw;

import io.fabric8.kubernetes.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
//
//    @Value("${kubernetes.config.file:}")
//    private String kubeConfigFile;
//
//    @Bean
//    public KubernetesClient kubernetesClient() {
//        if (!kubeConfigFile.isEmpty()) {
//            try {
//                io.fabric8.kubernetes.client.AppConfig config = io.fabric8.kubernetes.client.AppConfig.fromKubeconfig(kubeConfigFile);
//                return new io.fabric8.kubernetes.client.KubernetesClientBuilder().withConfig(config).build();
//            } catch (Exception e) {
//                throw new KubernetesClientException("Error loading Kubernetes config from file", e);
//            }
//        } else {
//            return new io.fabric8.kubernetes.client.DefaultKubernetesClient();
//        }
//    }


    @Bean
    public KubernetesClient kubernetesClient() {

        return new KubernetesClientBuilder()
                .withConfig(
                        new ConfigBuilder()
//                        .withMasterUrl("https://api.sandbox.x8i5.example.com:6443")
//                        .withOauthToken("sha256~secret")
                                .withNamespace("ns1")
                                .withUsername("gateway-sa")
                                .withTrustCerts(true)
//                        .withCaCertFile("/certs/ca.crt")
//                        .withClientCertFile("/kube-apiserver.cer")
//                        .withClientKeyFile("/home/foo/.minikube/profiles/minikube/client.key")
//                        .withClientKeyAlgo("RSA")
                                .build()

                )
                .build();



    }
}
