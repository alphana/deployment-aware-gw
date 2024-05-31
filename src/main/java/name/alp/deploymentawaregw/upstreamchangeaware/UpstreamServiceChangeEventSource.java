package name.alp.deploymentawaregw.upstreamchangeaware;

public record UpstreamServiceChangeEventSource(String deploymentName, String imageTagOld, String imageTagNew,
                                               Long currentGeneration,
                                               UpstreamServiceChangedEventTypes eventType) {
    @Override
    public String toString() {
        return "UpstreamServiceChangedEventSource{" +
               "eventType=" + eventType +
               ", deploymentName='" + deploymentName + '\'' +
               ", imageTagOld='" + imageTagOld + '\'' +
               ", imageTagNew='" + imageTagNew + '\'' +
               ", currentGeneration=" + currentGeneration +
               '}';
    }
}
