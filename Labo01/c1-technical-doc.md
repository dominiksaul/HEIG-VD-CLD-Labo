# CLD - LABO 01

# SSH Srv - DMZ
```bash
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem
ssh devopsteam05@15.188.43.46 22 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem
```

# Connect to our Host
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
CIDRBLOCK_SUBNET="10.0.5.0/28"
CIDRBLOCK_INTERNET="0.0.0.0/0"
GROUP_NAME="DEVOPSTEAM05"
INSTANCE_TYPE="t3.micro"

VPC_ID="vpc-03d46c285a2af77ba"
INTERNET_GATEWAY_ID="igw-0da47f5a441df46e0"
IMAGE_ID="ami-00b3a1b7cfab20134"

SUBNET_ID="subnet-052a4a1a63b6df5f4"
ROUTE_TABLE_ID="rtb-01a7d54e59ff42b92"
SECURITY_GROUP_ID="sg-0867c32d68bac6981"
KEYPAIR_ID_DMZ="key-01fa0354ab95d2bc4"
KEYPAIR_ID_DRUPAL="key-0864f1c5a64dd4248"
```

"KeyPairId": "key-01fa0354ab95d2bc4",
"KeyName": "CLD_KEY_DMZ_DEVOPSTEAM05",
"KeyPairId": "key-0864f1c5a64dd4248",
"KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM05",



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
    --key-name $KEYPAIR_ID_DRUPAL \
    --profile $PROFILE

[OUTPUT]

```

The ID we received, we saved as a constant in the variables on the top of this document.