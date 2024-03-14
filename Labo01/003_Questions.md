* What is the smallest and the biggest instance type (in terms of
  virtual CPUs and memory) that you can choose from when creating an
  instance?

```
Source: Instance Types creation console
Smallest: t2.nano with 1vCPUs and 0.5 GB memory (512 MB) 
Biggest: u-6tb1.112xlarge with 448 vCPUs and 6144 GB memory (bit more than 4 TB)
```

* How long did it take for the new instance to get into the _running_
  state?

```
A few seconds.
```

* Using the commands to explore the machine listed earlier, respond to
  the following questions and explain how you came to the answer:

    * What's the difference between time here in Switzerland and the time set on
      the machine?
    ```
    We used the command 'date' to get te current date and time on the machine. The machine hash the Universal Date/Time (UTC) configured, which is one hour back from the CET we use.
    ```

    * What's the name of the hypervisor?
    ```
    INPUT:
    aws ec2 describe-instances --instance-ids i-022fa1860cf96d05b --profile cld-team05 --query "Reservations[*].Instances[*].Hypervisor"
    OUTPUT:
    [
        [
            "xen"
        ]
    ]
    ```

    * How much free space does the disk have?
    ```
    We used the command 'df' (disk free) to get the available space on the disk. For the root directory '/' around 9.6GB are available in total. From this space are currently 35% used. So there is a free space of around 5.9GB
    ```


* Try to ping the instance ssh srv from your local machine. What do you see?
  Explain. Change the configuration to make it work. Ping the
  instance, record 5 round-trip times.

```
We tried to ping the ssh server with the following command: ping 15.188.43.46. The SSH server doesn't answer back. That's because its security group only allow inbound SSH and HTTP. By creating a new inbound rule that allow ICMP-Echo request in IPV4. We are able to ping the SSH server.

gdomingo@CI39975 ~ % ping  15.188.43.46
PING 15.188.43.46 (15.188.43.46): 56 data bytes
64 bytes from 15.188.43.46: icmp_seq=0 ttl=41 time=184.201 ms
64 bytes from 15.188.43.46: icmp_seq=1 ttl=41 time=40.781 ms
64 bytes from 15.188.43.46: icmp_seq=2 ttl=41 time=254.108 ms
64 bytes from 15.188.43.46: icmp_seq=3 ttl=41 time=54.217 ms
64 bytes from 15.188.43.46: icmp_seq=4 ttl=41 time=606.677 ms
^C
--- 15.188.43.46 ping statistics ---
6 packets transmitted, 5 packets received, 16.7% packet loss
round-trip min/avg/max/stddev = 40.781/227.997/606.677/205.547 ms

Source: https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/security-group-rules-reference.html#sg-rules-ping
```

* Determine the IP address seen by the operating system in the EC2
  instance by running the `ifconfig` command. What type of address
  is it? Compare it to the address displayed by the ping command
  earlier. How do you explain that you can successfully communicate
  with the machine?

```
The 'ifconfig' command wasn't available. We used 'ip address' instead.
It is the private IP address (10.0.5.10) we defined for our instance when running it.
The address we used to ping the machine is a public IP address.
Thanks to the Internet Gateway's NAT capability, we are able to successfully reach the instance from outside the VPC.

Source: https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Internet_Gateway.html
```
