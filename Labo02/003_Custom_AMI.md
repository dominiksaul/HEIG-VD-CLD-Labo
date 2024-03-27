# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

```bash
[INPUT]
aws ec2 stop-instances \
    --instance-id $INSTANCE_A_ID \
    --profile $PROFILE

[OUTPUT]
{
    "StoppingInstances": [
        {
            "CurrentState": {
                "Code": 64,
                "Name": "stopping"
            },
            "InstanceId": "i-0bff093f394535638",
            "PreviousState": {
                "Code": 16,
                "Name": "running"
            }
        }
    ]
}
```

|Key|Value for GUI Only|
|:--|:--|
|Name|AMI_DRUPAL_DEVOPSTEAM[XX]_LABO02_RDS|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 create-image \
    --instance-id $INSTANCE_A_ID \
    --name "AMI_DRUPAL_${GROUP_NAME}_LABO02_RDS" \
    --tag-specifications "ResourceType=image, Tags=[{Key=Name,Value=AMI_DRUPAL_${GROUP_NAME}_LABO02_RDS}]" \
    --profile $PROFILE

[OUTPUT]
{
    "ImageId": "ami-0b73a2deda2ac345f"
}
```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1
```bash
[INPUT]
aws ec2 start-instances \
    --instance-id $INSTANCE_A_ID \
    --profile $PROFILE

[OUTPUT]
{
    "StartingInstances": [
        {
            "CurrentState": {
                "Code": 0,
                "Name": "pending"
            },
            "InstanceId": "i-0bff093f394535638",
            "PreviousState": {
                "Code": 80,
                "Name": "stopped"
            }
        }
    ]
}
```


* Deploy Drupal Instance based on AMI in Az2

|Key|Value for GUI Only|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM[XX]_B|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 run-instances \
    --image-id $IMAGE_ID_DRUPAL_LABO_2 \
    --instance-type $INSTANCE_TYPE \
    --subnet-id $SUBNET_B_ID \
    --security-group-ids ${SECURITY_GROUP_DMZ_ID} \
    --tag-specifications "ResourceType=instance, Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_${GROUP_NAME}_B}]" \
    --key-name $KEYPAIR_DRUPAL \
    --private-ip-address 10.0.5.140 \
    --profile $PROFILE
    
[OUTPUT]
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-0b73a2deda2ac345f",
            "InstanceId": "i-011240acf0dd8ea3d",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM05",
            "LaunchTime": "2024-03-21T15:43:58+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3b",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-5-140.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.5.140",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-0fe3940f8eec03cf3",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "4d2ba103-5cd0-42b5-92f1-93a0cf7a9a25",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-21T15:43:58+00:00",
                        "AttachmentId": "eni-attach-0a201145e35078c74",
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
                    "MacAddress": "0a:b2:33:e6:cc:cf",
                    "NetworkInterfaceId": "eni-006172d84664a7d73",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.5.140",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.5.140"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-0fe3940f8eec03cf3",
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
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM05_B"
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
    "ReservationId": "r-0c134be67e147ceb2"
}
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
// updated string connection
// HOST A
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 2223:10.0.5.10:22
ssh bitnami@localhost -p 2223 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM05.pem

// HOST B
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 2224:10.0.5.140:22
ssh bitnami@localhost -p 2224 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM05.pem
```

## Check SQL Accesses

```bash
[INPUT]
// sql string connection from A
// initialise variables on the host from variables.sh first
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_DRUPAL_USER --password=$RDS_DB_DRUPAL_PW


[OUTPUT]
mysql: Deprecated program name. It will be removed in a future release, use '/opt/bitnami/mariadb/bin/mariadb' instead
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 110
Server version: 10.11.7-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]> 
```

```bash
[INPUT]
// sql string connection from B
// initialise variables on the host from variables.sh first
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_DRUPAL_USER --password=$RDS_DB_DRUPAL_PW

[OUTPUT]
mysql: Deprecated program name. It will be removed in a future release, use '/opt/bitnami/mariadb/bin/mariadb' instead
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 107
Server version: 10.11.7-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]> 
```

### Check HTTP Accesses

```bash
// connection string updated
// HOST A
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 8888:10.0.5.10:8080

// HOST B
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 8889:10.0.5.140:8080
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
If we update the password for the user on one webapp it is updated on the other when we refresh the site.
This works, because both instances are connected with the same database.
```

### Change the profil picture

* Observations ?

```
If we configure a profile picture for the user, we see on the other instance that the user does have a profile picture configured.
However the picture is not available on the other instance. Probably because drupal doesn't save images in the DB but locally on the server.
```
