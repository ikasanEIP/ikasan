![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan H2 Backup

The Ikasan H2 back up module provides the opportunity for the default Ikasan H2 database to be
backed up on a regular schedule and also takes a backup of the default Ikasan H2 database on module shutdown.

The backup is performed by delegating to the H2 SQL command 'BACKUP TO <file.name>'.

Backups are held in the <persistence.dir>/db-backup directory.

The process that performs the backup also validates the state of the backup to make sure that the backed up database is
not corrupt and discards the corrupted backup leaving the last good backup on the file system.

There is also the feature that takes a backup of the database when the module is shut down.

| Property                                    | Description                                                                       | Default Value |
|---------------------------------------------|-----------------------------------------------------------------------------------|---------------|
| persistence.dir                             | The base persistence directory where the database resides                         |               |
| datasource.url                              | The H2 database connection URL                                                    |               |
| datasource.username                         | The H2 database username                                                          |               |
| datasource.password                         | The H2 database password                                                          |               |
| default.ikasan.h2.backup.num.to.retain      | The number of backups to retain                                                   | 2             |
| default.ikasan.h2.backup.cron.expression    | The Quartz cron schedule on which the backups are taken                           | 0 0/1 * * * ? |
| default.ikasan.h2.backup.on.module.shutdown | Flag to configure if a database backup should be taken when the module shuts down | true          |

If other H2 databases are used by a module, developers can add a configuration to their module to support H2 database 
backups using the pattern provided in [IkasanBackupAutoConfiguration.java](./src/main/java/org/ikasan/backup/IkasanBackupAutoConfiguration.java).

It is possible to disable this feature by adding:

```properties
spring.autoconfigure.exclude=org.ikasan.backup.IkasanBackupAutoConfiguration
```