# Database migration

In this task you will migrate the Drupal database to the new RDS database instance.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

```bash
[INPUT]
//help : path /home/bitnami/bitnami_credentials
//Inside the drupal machine
more /home/bitnami/bitnami_credentials

[OUTPUT]
Welcome to the Bitnami package for Drupal

******************************************************************************
The default username and password is 'user' and 'dT:XPfs2/bqE'.
******************************************************************************

You can also use this password to access the databases and any other component the stack includes.

Please refer to https://docs.bitnami.com/ for more details.
```

### Get Database Name of Drupal

```bash
[INPUT]
//add string connection
mariadb --user=root --password=dT:XPfs2/bqE
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| test               |
+--------------------+
6 rows in set (0.002 sec)
```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

```bash
[INPUT]
drush sql-dump > dbDump.sql

[OUTPUT]
// no output
// but we can check the creation of the file with the command ls
```

### Create the new Data base on RDS

```sql
// connection string:
// mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW

[INPUT]
CREATE DATABASE bitnami_drupal;
```

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h" parameter.

```sql
[INPUT]
// help: mysql -h <rds-end-point> -u <rds_admin_user> -p <db_target> < <pathToDumpFileToImport>.sql
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW bitnami_drupal < dbDump.sql
```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

```bash
[INPUT]
// help : same settings.php as before
more /bitnami/drupal/sites/default/settings.php

[OUTPUT]
// at the end of the file you will find connection string parameters
username = bn_drupal
password = 20e91a2b1ef64b0912e6044d5fd4e8d3b62738609df7229ffb58e170793e688d
```

### Replace the current host with the RDS FQDN

```bash
[INPUT]
sudo nano /bitnami/drupal/sites/default/settings.php
//settings.php

$databases['default']['default'] = array (
   [...] 
  'host' => '$RDS_DB_DNS_NAME',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.
* [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
* [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
* [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

```sql
// connection string:
// mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW
[INPUT]
// CREATE USER bn_drupal@'10.0.[XX].0/[Subnet Mask - A]]' IDENTIFIED BY '<Drupal password>';
CREATE USER bn_drupal@'10.0.5.0/255.255.255.240' IDENTIFIED BY '20e91a2b1ef64b0912e6044d5fd4e8d3b62738609df7229ffb58e170793e688d';

// GRANT ALL PRIVILEGES ON bitnami_drupal.* TO '<yourNewUser>';
GRANT ALL PRIVILEGES ON bitnami_drupal.* TO bn_drupal@'10.0.5.0/255.255.255.240';

// DO NOT FOREGT TO FLUSH PRIVILEGES
FLUSH PRIVILEGES;
```

```sql
//validation
[INPUT]
// SHOW GRANTS for 'bn_drupal'@'10.0.[XX].0/[yourMask]]';
SHOW GRANTS for 'bn_drupal'@'10.0.5.0/255.255.255.240';

[OUTPUT]
+---------------------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.5.0/255.255.255.240                                                                                   |
+---------------------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.5.0/255.255.255.240` IDENTIFIED BY PASSWORD '*9C2E58CAAD3FD52E0FEF8C568462BD378AAFD802' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.5.0/255.255.255.240`                                            |
+---------------------------------------------------------------------------------------------------------------------------------+
2 rows in set (0.000 sec)
```

### Validate access (on the drupal instance)

```sql
[INPUT]
// mysql -h dbi-devopsteam[XX].xxxxxxxx.eu-west-3.rds.amazonaws.com -u bn_drupal -p
mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_DRUPAL_USER --password=$RDS_DB_DRUPAL_PW

[INPUT]
SHOW DATABASES;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
2 rows in set (0.001 sec)
```

* Repeat the procedure to enable the instance on subnet 2 to also talk to your RDS instance.
```sql
// mysql -h $RDS_DB_DNS_NAME -u $RDS_DB_ADMIN_USER --password=$RDS_DB_ADMIN_PW

[INPUT]
CREATE USER bn_drupal@'10.0.5.128/255.255.255.240' IDENTIFIED BY '20e91a2b1ef64b0912e6044d5fd4e8d3b62738609df7229ffb58e170793e688d';
GRANT ALL PRIVILEGES ON bitnami_drupal.* TO bn_drupal@'10.0.5.128/255.255.255.240';
SHOW GRANTS for 'bn_drupal'@'10.0.5.128/255.255.255.240';

[OUTPUT]
+-----------------------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.5.128/255.255.255.240                                                                                   |
+-----------------------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.5.128/255.255.255.240` IDENTIFIED BY PASSWORD '*9C2E58CAAD3FD52E0FEF8C568462BD378AAFD802' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.5.128/255.255.255.240`                                            |
+-----------------------------------------------------------------------------------------------------------------------------------+
2 rows in set (0.001 sec)
```