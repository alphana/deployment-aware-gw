package name.alp.deploymentawaregw.upstreamchangeaware;

public record UpstreamServiceChangEventSource(String deploymentName, String imageTagOld, String imageTagNew,
                                              Long currentGeneration,
                                              UpstreamServiceChangedEventTypes eventType) {
}
