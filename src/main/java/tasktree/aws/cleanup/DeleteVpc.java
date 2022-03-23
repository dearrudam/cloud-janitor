package tasktree.aws.cleanup;

import software.amazon.awssdk.services.ec2.model.DeleteVpcRequest;
import software.amazon.awssdk.services.ec2.model.Vpc;
import tasktree.Configuration;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class DeleteVpc extends AWSDelete<Vpc> {
    public DeleteVpc(Vpc resource) {
        super(resource);
    }

    @Override
    public void cleanup(Vpc resource) {
        log().debug("Deleting VPC [{}]", resource.vpcId());
        var request = DeleteVpcRequest.builder()
                .vpcId(resource.vpcId())
                .build();
        newEC2Client().deleteVpc(request);
    }

    @Override
    protected String getResourceType() {
        return "VPC";
    }

    @Override
    public Stream<Task> mapSubtasks(Vpc resource) {
        var vpcId = resource.vpcId();
        return Stream.of(
                //TODO: Filter by VPC
                new FilterTargetGroups(),
                new FilterLoadBalancersV2(),
                new FilterNATGateways(),
                new FilterAddresses(),
                new FilterInstances(),
                //VPC Resoruces
                new FilterLoadBalancers(vpcId),
                new FilterVPCEndpoints(vpcId),
                new FilterNetworkInterfaces(vpcId),
                new FilterRouteTables(vpcId),
                new FilterSecurityGroups(vpcId),
                new FilterSubnets(vpcId),
                new FilterInternetGateways(vpcId)
        );
    }
}
