# CLD - LABO 01


# connect with ssh to ssh host
```bash
ssh SSH_USER@SSH_HOST 22 -i ~/.ssh/CLD_KEY_DMZ_SSH_CLD_DEVOPSTEAM05-DS.pem
```
Connect to Proxy Host
```bash
ssh –L 2222:SSH_HOST:22
```
Connect to our Host
```bash
ssh host -p 2222
```

## Réalisation

Initialize variables we will use for the commands later
```bash
SSH_HOST="15.188.43.46"
SSH_USER="devopsteam05"
HOST="10.0.5.10"

PROFILE="cld-team05"
CIDRBLOCK_VPC="10.0.0.0/24"
CIDRBLOCK_SUBNET="10.0.5.0/24"
CIDRBLOCK_INTERNET="0.0.0.0/0"
GROUP_NAME="DEVOPSTEAM05"
INSTANCE_TYPE="t3.micro"

VPC_ID="vpc-03d46c285a2af77ba"
INTERNET_GATEWAY_ID="igw-0da47f5a441df46e0"
SUBNET_ID="subnet-052a4a1a63b6df5f4"
ROUTE_TABLE_ID="rtb-0ef959c81b78fa5d8"
SECURITY_GROUP_ID="sg-062486a11cff2fedb"
```


### Get VPC ID

We used the following command to get the id of the VPC.
The ID we received, we saved as a constant in the variables on the top of this document.

[INPUT]
```bash
aws ec2 describe-vpcs \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "Vpcs": [
        {
            "CidrBlock": "10.0.0.0/16",
            "DhcpOptionsId": "dopt-e979f380",
            "State": "available",
            "VpcId": "vpc-03d46c285a2af77ba",
            "OwnerId": "709024702237",
            "InstanceTenancy": "default",
            "CidrBlockAssociationSet": [
                {
                    "AssociationId": "vpc-cidr-assoc-0f2a3a4908a2a1ad4",
                    "CidrBlock": "10.0.0.0/16",
                    "CidrBlockState": {
                        "State": "associated"
                    }
                }
            ],
            "IsDefault": false,
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "VPC-CLD"
                }
            ]
        }
    ]
}
```

### Get Internet Gateway ID

We used the following command to get the id of the Internet Gateway.
The ID we received, we saved as a constant in the variables on the top of this document.

[INPUT]
```bash
aws ec2 describe-internet-gateways \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "InternetGateways": [
        {
            "Attachments": [
                {
                    "State": "available",
                    "VpcId": "vpc-03d46c285a2af77ba"
                }
            ],
            "InternetGatewayId": "igw-0da47f5a441df46e0",
            "OwnerId": "709024702237",
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "CLD-IGW"
                }
            ]
        }
    ]
}
```

### CREATE Subnet

[Documentation AWS - Subnet](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-subnet.html)

[INPUT]
```bash
aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block $CIDRBLOCK_SUBNET \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "Subnet": {
        "AvailabilityZone": "eu-west-3a",
        "AvailabilityZoneId": "euw3-az1",
        "AvailableIpAddressCount": 251,
        "CidrBlock": "10.0.5.0/24",
        "DefaultForAz": false,
        "MapPublicIpOnLaunch": false,
        "State": "available",
        "SubnetId": "subnet-034cdc7715a263f4d",
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237",
        "AssignIpv6AddressOnCreation": false,
        "Ipv6CidrBlockAssociationSet": [],
        "SubnetArn": "arn:aws:ec2:eu-west-3:709024702237:subnet/subnet-034cdc7715a263f4d",
        "EnableDns64": false,
        "Ipv6Native": false,
        "PrivateDnsNameOptionsOnLaunch": {
            "HostnameType": "ip-name",
            "EnableResourceNameDnsARecord": false,
            "EnableResourceNameDnsAAAARecord": false
        }
    }
}
```
The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Route Table

[Documentation AWS - Route Table](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-route-table.html)

[INPUT]
```bash
aws ec2 create-route-table \
    --vpc-id $VPC_ID \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "RouteTable": {
        "Associations": [],
        "PropagatingVgws": [],
        "RouteTableId": "rtb-0ef959c81b78fa5d8",
        "Routes": [
            {
                "DestinationCidrBlock": "10.0.0.0/16",
                "GatewayId": "local",
                "Origin": "CreateRouteTable",
                "State": "active"
            }
        ],
        "Tags": [],
        "VpcId": "vpc-03d46c285a2af77ba",
        "OwnerId": "709024702237"
    }
}
```
The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Routes

[Documentation AWS - Route](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-route.html)

[INPUT]
```bash
aws ec2 create-route \
    --route-table-id $ROUTE_TABLE_ID \
    --destination-cidr-block $CIDRBLOCK_INTERNET \
    --gateway-id $INTERNET_GATEWAY_ID \
    --profile $PROFILE
```

[OUTPUT]
```
```
The ID we received, we saved as a constant in the variables on the top of this document.

### Associate Subnet to route table

[INPUT]
```bash
aws ec2 associate-route-table \
    --subnet-id $SUBNET_ID \
    --route-table-id $ROUTE_TABLE_ID \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "AssociationId": "rtbassoc-0a1f7a5ee7f933ee4",
    "AssociationState": {
        "State": "associated"
    }
}
```

The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Security Group

[Documentation AWS - Security Group](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-security-group.html)

[INPUT]
```bash
aws ec2 create-security-group \
    --vpc-id $VPC_ID \
    --group-name $GROUP_NAME \
    --description $GROUP_NAME \
    --profile $PROFILE
```

[OUTPUT]
```
{
    "GroupId": "sg-062486a11cff2fedb"
}
```

The ID we received, we saved as a constant in the variables on the top of this document.

### Create Security Group Rules

[Documentation AWS - Security Group Rules](https://docs.aws.amazon.com/cli/latest/userguide/cli-services-ec2-sg.html#configuring-a-security-group)
[Documentation AWS - commande authorize-security-group-ingress](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/authorize-security-group-ingress.html)

Create SSH Security Group Rule
```bash
aws ec2 authorize-security-group-ingress \
--group-id $SECURITY_GROUP_ID \
--protocol tcp \
--port 22 \
--cidr 10.0.0.0/28 \
--profile $PROFILE
```

[OUTPUT]
```
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0e14ef8bd7e9b9b5f",
            "GroupId": "sg-062486a11cff2fedb",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 22,
            "ToPort": 22,
            "CidrIpv4": "10.0.0.0/28"
        }
    ]
}
```

Create HTTP Security Group Rule
```bash
aws ec2 authorize-security-group-ingress \
--group-id $SECURITY_GROUP_ID \
--protocol tcp \
--port 8080 \
--cidr 10.0.0.0/28 \
--profile $PROFILE
```

[OUTPUT]
```
{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-02e412cbe52f12f39",
            "GroupId": "sg-062486a11cff2fedb",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.0.0/28"
        }
    ]
}
```

### Create Instance for Drupal 

[Documentation AWS - Run Instance](https://docs.aws.amazon.com/cli/latest/reference/ec2/run-instance.html)
[Documentation Deploy Drupal](https://aws.amazon.com/getting-started/hands-on/deploy-drupal-with-amazon-rds/)

[INPUT]
```bash
aws ec2 run-instances \
    --image-id <ami-id> \
    --instance-type $INSTANCE_TYPE \
    --subnet-id $SUBNET_ID \
    --security-group-ids <security-group-id> <security-group-id> … \
    --key-name <ec2-key-pair-name>
```

[OUTPUT]
```
```

The ID we received, we saved as a constant in the variables on the top of this document.