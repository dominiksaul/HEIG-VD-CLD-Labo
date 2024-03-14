#!/bin/bash

PROFILE="cld-team05"
CIDRBLOCK_VPC="10.1.1.0/24"
CIDRBLOCK_SUBNET="10.1.1.0/24"
SECURITY_GROUP_NAME="Team5-Sec-Group"

# Create VPC
vpc_id=$(aws ec2 create-vpc --cidr-block $CIDRBLOCK --profile $PROFILE --query 'Vpc.{VpcId:VpcId}' --output text)

#aws ec2 modify-vpc-attribute --vpc-id $vpc_id --enable-dns-hostnames "{\"Value\":true}"

# Create public subnet
subnet_id=$(aws ec2 create-subnet --vpc-id $vpc_id --cidr-block $CIDRBLOCK_SUBNET --profile $PROFILE --query 'Subnet.{SubnetId:SubnetId}' --output text)

# Create an Internet Gateway
internet_gateway_id=$(aws ec2 create-internet-gateway --profile $PROFILE --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' --output text)

# Attach the Internet Gateway to the VPC
aws ec2 attach-internet-gateway --vpc-id $vpc_id --internet-gateway-id $internet_gateway_id --profile $PROFILE

# Create a route table for the vpc
route_table_id=$(aws ec2 create-route-table --vpc-id $vpc_id --profile $PROFILE --query 'RouteTable.{RouteTableId:RouteTableId}' --output text)

# Create route to Internet
aws ec2 create-route --route-table-id $route_table_id --destination-cidr-block '0.0.0.0/0' --gateway-id $internet_gateway_id --profile $PROFILE

# Associate Subnet to route table
aws ec2 associate-route-table --subnet-id $subnet_id --route-table-id $route_table_id --profile $PROFILE

#security_group_id=$(aws ec2 create-security-group)
#aws ec2 describe-security-groups

#aws ec2 authorize-security-group-ingress

aws ec2 describe-subnets --profile cld-team05

aws ec2 run-instances \
--image-id <ami-id> \
--instance-type <instance-type> \
--subnet-id <subnet-id> \
--security-group-ids <security-group-id> <security-group-id> … \
--key-name <ec2-key-pair-name>

# Create