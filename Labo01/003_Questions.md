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
TODO
```

* Determine the IP address seen by the operating system in the EC2
  instance by running the `ifconfig` command. What type of address
  is it? Compare it to the address displayed by the ping command
  earlier. How do you explain that you can successfully communicate
  with the machine?

```
TODO
```
