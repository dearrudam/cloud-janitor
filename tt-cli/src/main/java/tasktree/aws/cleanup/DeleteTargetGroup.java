package tasktree.aws.cleanup;

import software.amazon.awssdk.services.elasticloadbalancingv2.model.DeleteTargetGroupRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import tasktree.Configuration;

public class DeleteTargetGroup extends AWSDelete {
    private final TargetGroup resource;

    public DeleteTargetGroup(Configuration config, TargetGroup resource) {
        super(config);
        this.resource = resource;
    }

    @Override
    public void run() {
        log().debug("Deleting Target group {}", resource.targetGroupArn());
        var request = DeleteTargetGroupRequest.builder()
                .targetGroupArn(resource.targetGroupArn())
                .build();
        getELBClientV2().deleteTargetGroup(request);
    }

    @Override
    public String toString() {
        return super.toString("Target Group", resource.targetGroupName());
    }
}