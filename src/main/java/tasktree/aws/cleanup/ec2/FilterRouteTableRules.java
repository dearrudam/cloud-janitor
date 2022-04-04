package tasktree.aws.cleanup.ec2;

import software.amazon.awssdk.services.ec2.model.RouteTable;
import tasktree.aws.AWSFilter;
import tasktree.spi.Task;

import java.util.List;
import java.util.stream.Stream;

public class FilterRouteTableRules extends AWSFilter<RouteTable> {
    private String vpcId;

    public FilterRouteTableRules(String vpcId) {
        this.vpcId = vpcId;
    }

    private boolean match(RouteTable resource) {
        var prefix = getAwsCleanupPrefix();
        var match = resource.vpcId().equals(vpcId);
        match = match || resource.tags().stream()
                .anyMatch(tag -> tag.key().equals("Name")
                        && tag.value().startsWith(prefix));
        log().trace("Found Route Table {} {}", mark(match), resource);
        return match;
    }

    protected List<RouteTable> filterResources() {
        var client = newEC2Client();
        var resources = client.describeRouteTables().routeTables();
        var matches = resources.stream().filter(this::match).toList();
        return matches;
    }

    @Override
    protected Stream<Task> mapSubtasks(RouteTable resource) {

        return Stream.of(new DeleteRouteTableRules(resource));
    }

    @Override
    protected String getResourceType() {
        return "Route Table";
    }
}
