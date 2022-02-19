![Problem Domain](../docs/quickstart-images/Ikasan-title-transparent.png)
# Ikasan Maven Util
Provision of Maven projects and associated goals that are executable from within Java code.

The ```MavenFactory``` is used to create Maven instance objects.

| Instance | Description | Example |
| ----------- | ----------- | ----------- |
| Clean | Configured instance of the mvn 'clean' which can be invoked against a MavenProject | ```MavenFactory.getCleanCommand()``` |
| Build | Configured instance of the mvn 'build' which can be invoked against a MavenProject | ```MavenFactory.getBuildCommand()``` |
| Test | Configured instance of the mvn 'test' which can be invoked against a MavenProject | ```MavenFactory.getTestCommand()``` |
| Generate Archetype | Configured instance of the mvn 'generate:archetype' which creates a MavenProject | ```MavenFactory.getArchetypeCommand()``` |
| Generic goal | Generic instance against which any mvn goals can be specified | ```MavenFactory.getGenericCommand()``` |
| MavenProject | An instance of a Maven Project on the filesystem (based on the workingDir) against which Maven goals can be executed. | ```MavenFactory.getMavenProject(workingDir)``` |
| Maven POM Model | An instance of the Maven pom.xml as a Java POJO. | ```MavenFactory.getMavenModel(pomPath)``` |

## Maven Goal Configuration
### archetype:generate
Create a new Maven project based on an archetype.
#### Configuration
| Parameter | Description | Example |
| ----------- | ----------- | ----------- | 
| archetypeArtifactId | Artefact Id of the archetype to generate the project from. | ikasan-standalone-db-jms-im-maven-plugin |
| archetypeGroupId | Group Id of the archetype to generate the project from. | org.ikasan" |
| archetypeVersion | Version of the archetype to generate the project from. | ${version.ikasan} |
| groupId | Group Id of the project being created from the archetype. | com.sample |
| artifactId | Artefact Id of the project being created from the archetype. Usually the Integration Module name| db-jms-im |
| version | Version of the project being created from the archetype. | 1.0.0-SNAPSHOT |
| sourceFlowName | Name to be given to the source flow in the project being created from the archetype. | DB to JMS Flow |
| targetFlowName | Name to be given of the target flow in the project being created from the archetype. | JMS to DB Flow |
| debug | Whether to run Maven in debug mode or not. | false |
| batchmode | When true the Maven command will execute in batch mode, otherwise it will run interactively and prompt for user input. | true |
| workingDir | Directory location of where the project is to be created. | /tmp |

#### Usage
```java
        MavenArchetypeCommand mavenArchetypeCommand = MavenFactory.getArchetypeCommand();
        mavenArchetypeCommand.setArchetypeArtifactId("ikasan-standalone-db-jms-im-maven-plugin");
        mavenArchetypeCommand.setArchetypeGroupId("org.ikasan");
        mavenArchetypeCommand.setArchetypeVersion("3.2.2");
        mavenArchetypeCommand.setGroupId("com.sample");
        mavenArchetypeCommand.setArtifactId("db-jms-im");
        mavenArchetypeCommand.setVersion("1.0.0-SNAPSHOT");
        mavenArchetypeCommand.setSourceFlowName("DB to JMS Flow");
        mavenArchetypeCommand.setTargetFlowName("JMS To DB Flow");
        mavenArchetypeCommand.setBatchMode(true);
        mavenArchetypeCommand.setDebug(false);
        mavenArchetypeCommand.setWorkingDir( new File(Paths.get(getClass().getResource("/").toURI()).getParent().toString()) );

        try
        {
            MavenProject mavenProject = mavenArchetypeCommand.execute();
        }
        catch(CommandLineException|MavenInvocationException e)
        {
            Assert.fail("Project failed to create from the Maven archetype - " + e.getMessage());
        }

```

### clean
Run a Maven 'clean' command against the project.
#### Configuration
| Parameter | Description | Example |
| ----------- | ----------- | ----------- | 
| debug | Whether to run Maven in debug mode or not. | false |
| batchmode | When true the Maven command will execute in batch mode, otherwise it will run interactively and prompt for user input. | true |
| workingDir | Directory location of where the project is to be created. | /tmp |

#### Usage
```java
    mavenProject.invoke( MavenFactory.getCleanCommand() );
```

### build
Run a Maven 'build' command against the project.
#### Configuration
| Parameter | Description | Example |
| ----------- | ----------- | ----------- | 
| debug | Whether to run Maven in debug mode or not. | false |
| batchmode | When true the Maven command will execute in batch mode, otherwise it will run interactively and prompt for user input. | true |
| workingDir | Directory location of where the project is to be created. | /tmp |

#### Usage
```java
    mavenProject.invoke( MavenFactory.getBuildCommand() );
```

### test
Run a Maven 'test' command against the project.
#### Configuration
| Parameter | Description | Example |
| ----------- | ----------- | ----------- | 
| debug | Whether to run Maven in debug mode or not. | false |
| batchmode | When true the Maven command will execute in batch mode, otherwise it will run interactively and prompt for user input. | true |
| workingDir | Directory location of where the project is to be created. | /tmp |

#### Usage
```java
    mavenProject.invoke( MavenFactory.getTestCommand() );
```