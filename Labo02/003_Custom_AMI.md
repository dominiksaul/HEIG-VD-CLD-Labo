# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

```bash
[INPUT]
aws ec2 stop-instance \
    --instance-id $INSTANCE_A_ID \
    --profile $PROFILE

[OUTPUT]

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
    --profile $PROFILE

[OUTPUT]

```

// TODO: save image id in variable IMAGE_ID_DRUPAL_LABO_2

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1
```bash
[INPUT]
aws ec2 start-instance \
    --instance-id $INSTANCE_A_ID \
    --profile $PROFILE

[OUTPUT]

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
    --security-group-ids ${SECURITY_GROUP_DMZ_ID} ${SECURITY_GROUP_RDS_ID} \
    --tag-specifications "ResourceType=instance, Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_${GROUP_NAME}_B}]" \
    --key-name $KEYPAIR_DRUPAL \
    --private-ip-address 10.0.5.140 \
    --profile $PROFILE
    
[OUTPUT]
```

// TODO: save image id in variable INSTANCE_B_ID


## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
// updated string connection
ssh devopsteam05@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM05.pem -L 2224:10.0.5.140:22
ssh bitnami@localhost -p 2224 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM05.pem
```

## Check SQL Accesses

```bash
[INPUT]
// sql string connection from A
mysql -h dbi-devopsteam05.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p
DEVOPSTEAM05!

[OUTPUT]
```

```bash
[INPUT]
// sql string connection from B
mysql -h dbi-devopsteam05.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p
DEVOPSTEAM05!

[OUTPUT]
```

### Check HTTP Accesses

```bash
// connection string updated
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
//TODO
```

### Change the profil picture

* Observations ?

```
//TODO
```