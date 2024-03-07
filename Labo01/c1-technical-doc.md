# CLD - LABO 01

# SSH Srv - DMZ
```bash
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem
```

# Connect to our Drupal Host
```bash
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 2223:10.0.5.10:22
ssh bitnami@localhost -p 2223 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM05.pem
```

## Réalisation

Initialize variables we will use for the commands later
```bash
SSH_HOST="15.188.43.46"
SSH_USER="devopsteam05"
HOST="10.0.5.10"

PROFILE="cld-team05"
CIDRBLOCK_VPC="10.0.0.0/24"
CIDRBLOCK_SUBNET="10.0.5.0/28"
CIDRBLOCK_INTERNET="0.0.0.0/0"
GROUP_NAME="DEVOPSTEAM05"
INSTANCE_TYPE="t3.micro"
DRUPAL_INSTANCE_IP="10.0.5.10"

VPC_ID="vpc-03d46c285a2af77ba"
INTERNET_GATEWAY_ID="igw-0da47f5a441df46e0"
IMAGE_ID="ami-00b3a1b7cfab20134"
IMAGE_ID_WEEK03="ami-06c78b25c6365dbd6"

SUBNET_ID="subnet-052a4a1a63b6df5f4"
ROUTE_TABLE_ID="rtb-01a7d54e59ff42b92"
SECURITY_GROUP_ID="sg-0867c32d68bac6981"
KEYPAIR_DMZ="CLD_KEY_DMZ_DEVOPSTEAM05"
KEYPAIR_DRUPAL="CLD_KEY_DRUPAL_DEVOPSTEAM05"
INSTANCE_ID="i-0007ac051b626d5b4"
```


### Get VPC ID

We used the following command to get the id of the VPC.
The ID we received, we saved as a constant in the variables on the top of this document.

```bash
[INPUT]
aws ec2 describe-vpcs \
    --profile $PROFILE

[OUTPUT]

```

### CREATE Subnet

[Documentation AWS - Subnet](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-subnet.html)

```bash
[INPUT]
aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block $CIDRBLOCK_SUBNET \
    --tag-specifications "ResourceType=subnet, Tags=[{Key=Name,Value=SUB-PRIVATE-$GROUP_NAME}]" \
    --profile $PROFILE

[OUTPUT]

```
The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Route Table

[Documentation AWS - Route Table](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-route-table.html)

```bash
[INPUT]
aws ec2 create-route-table \
    --vpc-id $VPC_ID \
    --tag-specifications "ResourceType=route-table, Tags=[{Key=Name,Value=RTBLE-PRIVATE-DRUPAL-$GROUP_NAME}]" \
    --profile $PROFILE

[OUTPUT]

```
The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Routes

[Documentation AWS - Route](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-route.html)

```bash
[INPUT]
aws ec2 create-route \
    --route-table-id $ROUTE_TABLE_ID \
    --destination-cidr-block $CIDRBLOCK_INTERNET \
    --gateway-id $INTERNET_GATEWAY_ID \
    --profile $PROFILE

[OUTPUT]

```
The ID we received, we saved as a constant in the variables on the top of this document.

### Associate Subnet to route table

```bash
[INPUT]
aws ec2 associate-route-table \
    --subnet-id $SUBNET_ID \
    --route-table-id $ROUTE_TABLE_ID \
    --profile $PROFILE

[OUTPUT]

```

The ID we received, we saved as a constant in the variables on the top of this document.

### CREATE Security Group

[Documentation AWS - Security Group](https://docs.aws.amazon.com/cli/latest/reference/ec2/create-security-group.html)

```bash
[INPUT]
aws ec2 create-security-group \
    --vpc-id $VPC_ID \
    --group-name $GROUP_NAME \
    --description $GROUP_NAME \
    --tag-specifications "ResourceType=security-group, Tags=[{Key=Name,Value=SG-PRIVATE-DRUPAL-$GROUP_NAME}]" \
    --profile $PROFILE

[OUTPUT]

```

The ID we received, we saved as a constant in the variables on the top of this document.

### Create Security Group Rules

[Documentation AWS - Security Group Rules](https://docs.aws.amazon.com/cli/latest/userguide/cli-services-ec2-sg.html#configuring-a-security-group)
[Documentation AWS - commande authorize-security-group-ingress](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/authorize-security-group-ingress.html)

Create SSH Security Group Rule
```bash
[INPUT]
aws ec2 authorize-security-group-ingress \
    --group-id $SECURITY_GROUP_ID \
    --protocol tcp \
    --port 22 \
    --cidr 10.0.0.0/28 \
    --profile $PROFILE

[OUTPUT]

```

Create HTTP Security Group Rule
```bash
[INPUT]
aws ec2 authorize-security-group-ingress \
--group-id $SECURITY_GROUP_ID \
--protocol tcp \
--port 8080 \
--cidr 10.0.0.0/28 \
--profile $PROFILE

[OUTPUT]

```

### Deploy Bitnami/Drupal Instance 

[Documentation AWS - Run Instance](https://docs.aws.amazon.com/cli/latest/reference/ec2/run-instance.html)
[Documentation Deploy Drupal](https://aws.amazon.com/getting-started/hands-on/deploy-drupal-with-amazon-rds/)


```bash
[INPUT]
aws ec2 run-instances \
    --image-id $IMAGE_ID \
    --instance-type $INSTANCE_TYPE \
    --subnet-id $SUBNET_ID \
    --security-group-ids $SECURITY_GROUP_ID \
    --tag-specifications "ResourceType=instance, Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_$GROUP_NAME}]" \
    --key-name $KEYPAIR_DRUPAL \
    --private-ip-address $DRUPAL_INSTANCE_IP \
    --profile $PROFILE

[OUTPUT]
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-00b3a1b7cfab20134",
            "InstanceId": "i-0007ac051b626d5b4",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM05",
            "LaunchTime": "2024-03-07T16:00:10+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3a",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-5-10.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.5.10",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-052a4a1a63b6df5f4",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "4625ec60-fac7-496a-b4a3-8cf9b83f0ca6",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-07T16:00:10+00:00",
                        "AttachmentId": "eni-attach-07eb8b30271069482",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                    },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM05",
                            "GroupId": "sg-0867c32d68bac6981"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "06:87:61:4f:25:ed",
                    "NetworkInterfaceId": "eni-0158c569d096d71b2",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.5.10",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.5.10"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-052a4a1a63b6df5f4",
                    "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM05",
                    "GroupId": "sg-0867c32d68bac6981"
                }
            ],
            "SourceDestCheck": true,
            "StateReason": {
                "Code": "pending",
                "Message": "pending"
            },
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM05"
                }
            ],
            "VirtualizationType": "hvm",
            "CpuOptions": {
                "CoreCount": 1,
                "ThreadsPerCore": 2
            },
            "CapacityReservationSpecification": {
                "CapacityReservationPreference": "open"
            },
            "MetadataOptions": {
                "State": "pending",
                "HttpTokens": "optional",
                "HttpPutResponseHopLimit": 1,
                "HttpEndpoint": "enabled",
                "HttpProtocolIpv6": "disabled",
                "InstanceMetadataTags": "disabled"
            },
            "EnclaveOptions": {
                "Enabled": false
            },
            "PrivateDnsNameOptions": {
                "HostnameType": "ip-name",
                "EnableResourceNameDnsARecord": false,
                "EnableResourceNameDnsAAAARecord": false
            },
            "MaintenanceOptions": {
                "AutoRecovery": "default"
            }
        }
    ],
    "OwnerId": "709024702237",
    "ReservationId": "r-0843ad929e651434e"
}

```

The ID we received, we saved as a constant in the variables on the top of this document.

### Task 04 - SSH Access to your private Drupal Instance
```bash
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 2223:10.0.5.10:22
ssh bitnami@localhost -p 2223 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM05.pem
```

### Task 05 - Web access to your private Drupal Instance

#### INSIDE THE SUBNET

* Test directly on the ssh srv (inside the private subnet)

```
[INPUT]
curl localhost

[OUTPUT]
you get the html content of the home page
```

* Change the default port of apache

```
file : /opt/bitnami/apache2/conf/httpd.conf
LISTEN 8080
```

```
file : /opt/bitnami/apache2/conf/bitnami/bitnami.conf
<VirtualHost _default_:8080>
```

```
file : /opt/bitnami/apache2/conf/vhosts/
<VirtualHost 127.0.0.1:8080 _default_:8080>
```

* Restart Apache Server

```
sudo /opt/bitnami/ctlscript.sh restart apache
```

#### FROM THE DMZ

* Test directly on the ssh srv (outside the private subnet)

```
[INPUT]
curl localhost

[OUTPUT]
you get the html content of the home page
```

#### FROM THE WEB (THROUGH SSH)

* Update your ssh string connection adding a http tunnel to your Drupal instance

```bash
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 8888:10.0.5.10:8080
```

http://localhost:8888

* Test directly on your localhost, using your browser

### Step 6
#### Stop the Instance

```bash
aws ec2 stop-instances \
    --instance-ids $INSTANCE_ID \
    --profile $PROFILE
```

#### Create a Image of the Instance
```bash
aws ec2 create-image \
    --instance-id $INSTANCE_ID \
    --name "EC2_PRIVATE_DRUPAL_DEVOPSTEAM05_WEEK03" \
    --description "EC2_PRIVATE_DRUPAL_DEVOPSTEAM05_WEEK03"
    --profile $PROFILE
```

#### Terminate the Instance
```bash
aws ec2 teminate-instances \
    --instance-ids $INSTANCE_ID \
    --profile $PROFILE
```