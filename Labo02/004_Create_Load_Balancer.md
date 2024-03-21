### Deploy the elastic load balancer

In this task you will create a load balancer in AWS that will receive
the HTTP requests from clients and forward them to the Drupal
instances.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 Prerequisites for the ELB

* Create a dedicated security group

|Key|Value|
|:--|:--|
|Name|SG-DEVOPSTEAM[XX]-LB|
|Inbound Rules|Application Load Balancer|
|Outbound Rules|Refer to the infra schema|

```bash
[INPUT]
// Create security groups
aws ec2 create-security-group \
    --vpc-id $VPC_ID \
    --group-name SG-DEVOPSTEAM${GROUP_NAME}-LD \
    --description ${GROUP_NAME}-LD \
    --tag-specifications "ResourceType=security-group, Tags=[{Key=Name,Value=SG-DEVOPSTEAM-${GROUP_NAME}-LD}]" \
    --profile $PROFILE

// Create inbound rule for load balancer
aws ec2 authorize-security-group-ingress \
    --group-id $SECURITY_GROUP_LD_ID \
    --protocol tcp \
    --port 8080 \
    --cidr $CIDRBLOCK_VPC \
    --profile $PROFILE
    
// Create inbound rule for instance 
aws ec2 authorize-security-group-ingress \
    --group-id $SECURITY_GROUP_DMZ_ID \
    --protocol tcp \
    --port 8080 \
    --source-group $SECURITY_GROUP_LD_ID \
    --profile $PROFILE
    
[OUTPUT]
// Create security group
{
    "GroupId": "sg-0fdbe692da3bc47eb",
    "Tags": [
        {
            "Key": "Name",
            "Value": "SG-DEVOPSTEAM-DEVOPSTEAM05-LD"
        }
    ]
}

//Create rule for load balancer
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0f8419a490a05935d",
            "GroupId": "sg-0fdbe692da3bc47eb",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.0.0/24"
        }
    ]
}

//Create rule for drupal instances
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0fdabf4b36a063656",
            "GroupId": "sg-0867c32d68bac6981",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "ReferencedGroupInfo": {
                "GroupId": "sg-0fdbe692da3bc47eb",
                "UserId": "709024702237"
            }
        }
    ]
}
```

* Create the Target Group

|Key|Value|
|:--|:--|
|Target type|Instances|
|Name|TG-DEVOPSTEAM[XX]|
|Protocol and port|Refer to the infra schema|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Protocol version|HTTP1|
|Health check protocol|HTTP|
|Health check path|/|
|Port|Traffic port|
|Healthy threshold|2 consecutive health check successes|
|Unhealthy threshold|2 consecutive health check failures|
|Timeout|5 seconds|
|Interval|10 seconds|
|Success codes|200|

[Source]([https://docs.aws.amazon.com/elasticloadbalancing/latest/application/create-application-load-balancer.html](https://awscli.amazonaws.com/v2/documentation/api/2.1.29/reference/elbv2/create-target-group.html))
```bash
[INPUT]
//Create target group
aws elbv2 create-target-group \
    --name TG-${GROUP_NAME} \
    --protocol HTTP \
    --port 8080 \
    --health-check-protocol HTTP \
    --health-check-enabled \
    --health-check-interval-seconds 10 \
    --healthy-threshold-count 2 \
    --unhealthy-threshold-count 2 \
    --target-type instance \
    --vpc-id $VPC_ID \
    --profile $PROFILE
// Health check timeout is by default 5 seconds by default for HTTP Target groups
// Unhealthy threshold is already 2 by default
// Success code are already by default 200

// Registers intances to target groups
aws elbv2 register-targets \
    --target-group-arn $TARGETGROUP_ARN \
    --targets Id=$INSTANCE_A_ID Id=$INSTANCE_B_ID \
    --profile $PROFILE

[OUTPUT]
// Create target group
{
    "TargetGroups": [
        {
            "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM05/90c93af8c3baa5b3",
            "TargetGroupName": "TG-DEVOPSTEAM05",
            "Protocol": "HTTP",
            "Port": 8080,
            "VpcId": "vpc-03d46c285a2af77ba",
            "HealthCheckProtocol": "HTTP",
            "HealthCheckPort": "traffic-port",
            "HealthCheckEnabled": true,
            "HealthCheckIntervalSeconds": 10,
            "HealthCheckTimeoutSeconds": 5,
            "HealthyThresholdCount": 2,
            "UnhealthyThresholdCount": 2,
            "HealthCheckPath": "/",
            "Matcher": {
                "HttpCode": "200"
            },
            "TargetType": "instance",
            "ProtocolVersion": "HTTP1",
            "IpAddressType": "ipv4"
        }
    ]
}

// Register targets to the target group
//No output if successful
```


## Task 02 Deploy the Load Balancer

[Source](https://aws.amazon.com/elasticloadbalancing/)

* Create the Load Balancer

|Key|Value|
|:--|:--|
|Type|Application Load Balancer|
|Name|ELB-DEVOPSTEAM99|
|Scheme|Internal|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Security group|Refer to the infra schema|
|Listeners Protocol and port|Refer to the infra schema|
|Target group|Your own target group created in task 01|

Provide the following answers (leave any
field not mentioned at its default value):

```bash
[INPUT]
// Create application Load Balancers
aws elbv2 create-load-balancer \
    --name ELB-${GROUP_NAME} \
    --scheme internal \
    --ip-address-type ipv4 \
    --subnets $SUBNET_A_ID $SUBNET_B_ID \
    --security-groups $SECURITY_GROUP_LD_ID\
    --profile $PROFILE

// Create and add listener to load balancer
 aws elbv2 create-listener \
    --load-balancer-arn $LOADBALANCER_ARN \
    --protocol HTTP \
    --port 8080 \
    --default-actions Type=forward,TargetGroupArn=$TARGETGROUP_ARN \
    --profile $PROFILE
[OUTPUT]
// Create application load balancers
{
    "LoadBalancers": [
        {
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM05/b36d3ae26f6f6e43",
            "DNSName": "internal-ELB-DEVOPSTEAM05-995522892.eu-west-3.elb.amazonaws.com",
            "CanonicalHostedZoneId": "Z3Q77PNBQS71R4",
            "CreatedTime": "2024-03-21T15:40:09.410000+00:00",
            "LoadBalancerName": "ELB-DEVOPSTEAM05",
            "Scheme": "internal",
            "VpcId": "vpc-03d46c285a2af77ba",
            "State": {
                "Code": "provisioning"
            },
            "Type": "application",
            "AvailabilityZones": [
                {
                    "ZoneName": "eu-west-3a",
                    "SubnetId": "subnet-0ae144aabdbd0ca14",
                    "LoadBalancerAddresses": []
                },
                {
                    "ZoneName": "eu-west-3b",
                    "SubnetId": "subnet-0fe3940f8eec03cf3",
                    "LoadBalancerAddresses": []
                }
            ],
            "SecurityGroups": [
                "sg-0fdbe692da3bc47eb"
            ],
            "IpAddressType": "ipv4"
        }
    ]
}

//Create Listener
{
    "Listeners": [
        {
            "ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-DEVOPSTEAM05/b36d3ae26f6f6e43/83c10b2794f36108",
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM05/b36d3ae26f6f6e43",
            "Port": 8080,
            "Protocol": "HTTP",
            "DefaultActions": [
                {
                    "Type": "forward",
                    "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM05/90c93af8c3baa5b3",
                    "ForwardConfig": {
                        "TargetGroups": [
                            {
                                "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM05/90c93af8c3baa5b3",
                                "Weight": 1
                            }
                        ],
                        "TargetGroupStickinessConfig": {
                            "Enabled": false
                        }
                    }
                }
            ]
        }
    ]
}
```

* Get the ELB FQDN (DNS NAME - A Record)

```bash
[INPUT]
//Via ClI ?
aws elb describe-load-balancers \
--load-balancer-name LB.name \
--query "LoadBalancerDescriptions[*].DNSName" \
--profile $PROFILE \
--output table

// Connect to the LD and launch the following command
hostname --fqdn

[OUTPUT]

```

* Get the ELB deployment status

Note : In the EC2 console select the Target Group. In the
       lower half of the panel, click on the **Targets** tab. Watch the
       status of the instance go from **unused** to **initial**.

* Ask the DMZ administrator to register your ELB with the reverse proxy via the private teams channel

* Update your string connection to test your ELB and test it

```bash
//connection string updated
```

* Test your application through your ssh tunneling

```bash
[INPUT]
curl localhost:[local port forwarded]

[OUTPUT]

```

#### Questions - Analysis

* On your local machine resolve the DNS name of the load balancer into
  an IP address using the `nslookup` command (works on Linux, macOS and Windows). Write
  the DNS name and the resolved IP Address(es) into the report.

```
//TODO
```

* From your Drupal instance, identify the ip from which requests are sent by the Load Balancer.

Help : execute `tcpdump port 8080`

```
//TODO
```

* In the Apache access log identify the health check accesses from the
  load balancer and copy some samples into the report.

```
//TODO
```
