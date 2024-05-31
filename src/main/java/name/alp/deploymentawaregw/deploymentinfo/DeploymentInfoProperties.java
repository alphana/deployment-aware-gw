package name.alp.deploymentawaregw.deploymentinfo;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;

@ConfigurationProperties(prefix = "plateau.component.deployment-info")
@Getter
@Setter
public class DeploymentInfoProperties {

    private String nodeName="node1";
    private String namespace="ns1";
    private String podName="poddddname-1";
    private String podIP="ip";
    private String aciCode="accc";
    private String ukCode="gb";


    public Tags getCommonTags() {

        var nodeName=Tag.of("nodeName",this.getNodeName());
        var namespace=Tag.of("namespace",this.getNamespace());
        var podName=Tag.of("podName",this.getPodName());
        var podIP=Tag.of("podIP",this.getPodIP());
        var aciCode=Tag.of("aciCode",this.getAciCode());
        var ukCode=Tag.of("ukCode",this.getUkCode());



        return Tags.of(Arrays.asList(
                nodeName,
                namespace,
                podName,
                podIP,
                aciCode,
                ukCode
        ));

    }
}
