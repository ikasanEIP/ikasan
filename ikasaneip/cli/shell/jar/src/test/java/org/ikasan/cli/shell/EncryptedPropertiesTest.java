package org.ikasan.cli.shell;

import org.ikasan.cli.shell.command.H2MigrationCommand;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricStringEncryptor;
import java.security.KeyPairGenerator;
import java.util.Base64;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URLClassLoader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpServer;
import org.ikasan.cli.shell.command.EnvCommand;
import java.sql.Driver;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedPropertiesTest {
    @TempDir
    Path directory;

    @AfterEach
    void cleanMigrationWorkDirectory() throws Exception {
        FileUtils.deleteDirectory(Path.of("db-migration").toFile());
    }

    @Test
    void encryptedCredentialsMigrateLegacyDatabaseWithoutCommandLineOverrides() throws Exception {
        try (ConfigurableApplicationContext context = startShell(encrypt("sa"), "test-encryption-key")) {
            assertEquals("sa", context.getEnvironment().getProperty("datasource.password"));
            Files.copy(Path.of("src/test/resources/migration/h2_1_4_200_sample_db/non-esb.mv.db"),
                directory.resolve("esb.mv.db"));
            String result = context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", "", "", "", false);
            assertTrue(result.contains("migrated successfully"));
            assertTrue(result.contains("-password [REDACTED]"));
            assertFalse(result.contains("-password sa"));
            assertTrue(Files.exists(directory.resolve("esb.mv.db")));
            assertFalse(Files.exists(directory.resolve("esb-new.mv.db")));
            try (var files = Files.list(directory)) {
                assertTrue(files.anyMatch(p -> p.getFileName().toString().startsWith("esb.mv.db-backup-1.4.200")));
            }
            checkDatabase("2.4.240", "sa", false);
            // The same credentials must open the migrated database; this also verifies
            // that automatic migration is skipped after successful completion.
            assertTrue(context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", "", "", "", false).contains("run already"));
        }
    }

    @Test
    void plaintextPropertiesContinueToWork() throws Exception {
        try (ConfigurableApplicationContext context = startShell("plain-password", null)) {
            assertEquals("plain-password", context.getEnvironment().getProperty("datasource.password"));
        }
    }

    @Test
    void incorrectEncryptionKeyFailsBeforeMigrationCanStart() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            try (ConfigurableApplicationContext ignored = startShell(encrypt("sa"), "wrong-key")) {
                fail("An encrypted password must not be forwarded when decryption fails");
            }
        });
        assertFalse(Files.exists(directory.resolve("esb.mv.db")));
    }

    @Test
    void explicitCredentialsOverrideConfiguredCredentials() throws Exception {
        try (ConfigurableApplicationContext context = startShell(encrypt("different-password"), "test-encryption-key")) {
            Files.copy(Path.of("src/test/resources/migration/h2_1_4_200_sample_db/non-esb.mv.db"),
                directory.resolve("esb.mv.db"));
            assertTrue(context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", "sa", "sa", "", false).contains("migrated successfully"));
            checkDatabase("2.4.240", "sa", false);
        }
    }

    @Test
    void decryptedPasswordWithDollarAndBackslashSurvivesMigration() throws Exception {
        String password = "migration$test\\password";
        checkDatabase("1.4.200", password, true);
        try (ConfigurableApplicationContext context = startShell(encrypt(password), "test-encryption-key")) {
            String result = context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", null, null, "", false);
            assertTrue(result.contains("migrated successfully"));
            assertFalse(result.contains(password));
            checkDatabase("2.4.240", password, false);
        }
    }

    @Test
    void failedMigrationDoesNotExposeDecryptedPassword() throws Exception {
        String password = "incorrect-database-password";
        try (ConfigurableApplicationContext context = startShell(encrypt(password), "test-encryption-key")) {
            Files.copy(Path.of("src/test/resources/migration/h2_1_4_200_sample_db/non-esb.mv.db"),
                directory.resolve("esb.mv.db"));
            RuntimeException failure = assertThrows(RuntimeException.class, () -> context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", "", "", "", false));
            assertTrue(failure.getMessage().contains("-password [REDACTED]"));
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                assertFalse(String.valueOf(cause.getMessage()).contains(password));
            }
            assertFalse(Files.exists(directory.resolve("esb-new.mv.db")));
        }
    }

    private void checkDatabase(String version, String password, boolean create) throws Exception {
        Path jar = Path.of("src/test/resources/migration/lib/h2-" + version + ".jar");
        try (URLClassLoader loader = new URLClassLoader(new java.net.URL[] {jar.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            Driver driver = (Driver) loader.loadClass("org.h2.Driver").getDeclaredConstructor().newInstance();
            Properties credentials = new Properties();
            credentials.setProperty("user", "sa");
            credentials.setProperty("password", password);
            String url = "jdbc:h2:" + directory.resolve("esb") + (create ? "" : ";IFEXISTS=TRUE");
            try (var connection = driver.connect(url, credentials); var statement = connection.createStatement()) {
                if (create) {
                    statement.execute("CREATE TABLE MIGRATION_TEST(ID INTEGER PRIMARY KEY)");
                    statement.execute("INSERT INTO MIGRATION_TEST VALUES(1)");
                } else {
                    try (var result = statement.executeQuery("SELECT 1")) {
                        assertTrue(result.next());
                        assertEquals(1, result.getInt(1));
                    }
                }
            }
        }
    }

    @Test
    void asymmetricPrivateKeyDecryptsTheDatasourcePassword() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var keys = generator.generateKeyPair();
        SimpleAsymmetricConfig encryption = new SimpleAsymmetricConfig();
        encryption.setPublicKey(Base64.getEncoder().encodeToString(keys.getPublic().getEncoded()));
        String ciphertext = new SimpleAsymmetricStringEncryptor(encryption).encrypt("sa");
        Properties settings = new Properties();
        settings.setProperty("jasypt.encryptor.private-key-string",
            Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded()));
        try (ConfigurableApplicationContext context = startShell("ENC(" + ciphertext + ")", null, settings)) {
            assertEquals("sa", context.getEnvironment().getProperty("datasource.password"));
            Files.copy(Path.of("src/test/resources/migration/h2_1_4_200_sample_db/non-esb.mv.db"),
                directory.resolve("esb.mv.db"));
            assertTrue(context.getBean(H2MigrationCommand.class)
                .migrateH2("1.4.200", "2.4.240", "", "", "", false).contains("migrated successfully"));
            checkDatabase("2.4.240", "sa", false);
        }
    }

    @Test
    void plaintextCredentialsFromConfigServerWorkWithoutLocalDecryptionKey() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] response = ("{\"name\":\"application\",\"profiles\":[\"default\"],"
                + "\"propertySources\":[{\"name\":\"test-config-service\",\"source\":{"
                + "\"datasource.password\":\"config-service-plaintext\"}}]}").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (var output = exchange.getResponseBody()) {
                output.write(response);
            }
        });
        server.start();
        try {
            Properties settings = new Properties();
            settings.setProperty("spring.cloud.config.enabled", "true");
            settings.setProperty("spring.config.import", "configserver:http://127.0.0.1:" + server.getAddress().getPort());
            try (ConfigurableApplicationContext context = startShell("local-fallback", null, settings)) {
                assertEquals("config-service-plaintext", context.getEnvironment().getProperty("datasource.password"));
                assertEquals("config-service-plaintext", context.getBean(EnvCommand.class)
                    .env("^datasource.password$", false, true, true, true).trim());
            }
        } finally {
            server.stop(0);
        }
    }

    @Test
    void envCommandListsResolvedPropertiesAndFiltersNames() throws Exception {
        try (ConfigurableApplicationContext context = startShell(encrypt("sa"), "test-encryption-key")) {
            EnvCommand env = context.getBean(EnvCommand.class);
            assertEquals("sa", env.env("^datasource.password$", false, true, true, true).trim());
            assertEquals("datasource.password", env.env("^datasource.password$", true, false, true, true).trim());
            assertEquals("encrypted-properties-test", env.env("^module.name$", false, true, true, true).trim());
        }
    }

    private String encrypt(String value) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword("test-encryption-key");
        return "ENC(" + encryptor.encrypt(value) + ")";
    }

    private ConfigurableApplicationContext startShell(String password, String key) throws Exception {
        return startShell(password, key, new Properties());
    }

    private ConfigurableApplicationContext startShell(String password, String key, Properties settings) throws Exception {
        Properties properties = new Properties();
        try (var input = Files.newInputStream(Path.of("src/main/resources/application.properties"))) {
            properties.load(input);
        }
        for (String name : new String[] {"h2.script.command", "h2.runscript.command",
                "h2.changelog.runscript.command", "h2.determine.if.db.file.already.target.version.command"}) {
            properties.setProperty(name, properties.getProperty(name)
                .replace("./lib/migration/", "./src/test/resources/migration/lib/"));
        }
        properties.setProperty("shell.history.file", directory.resolve("shell-history.log").toString());
        properties.setProperty("spring.shell.history.name", directory.resolve("spring-shell.log").toString());
        properties.setProperty("module.name", "encrypted-properties-test");
        properties.setProperty("lib.dir", ".");
        properties.setProperty("h2.db.port", "9999");
        properties.setProperty("persistence.dir", directory.toString());
        properties.setProperty("h2.db.migration.module.persistence.database.path", directory.resolve("esb").toString());
        properties.setProperty("datasource.username", "sa");
        properties.setProperty("datasource.password", password);
        if (key != null) {
            properties.setProperty("jasypt.encryptor.password", key);
        }
        properties.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        properties.setProperty("jasypt.encryptor.iv-generator-classname", "org.jasypt.iv.NoIvGenerator");
        properties.setProperty("spring.cloud.config.enabled", "false");
        properties.setProperty("spring.cloud.compatibility-verifier.enabled", "false");
        properties.setProperty("spring.shell.interactive.enabled", "false");
        properties.setProperty("spring.shell.noninteractive.enabled", "false");
        properties.setProperty("spring.shell.script.enabled", "false");
        properties.setProperty("spring.main.web-application-type", "none");
        properties.putAll(settings);
        // Use the exact bootstrap used by main(), loading ENC(...) from a local file.
        Path configuration = directory.resolve("application.properties");
        try (var output = Files.newOutputStream(configuration)) {
            properties.store(output, "Test configuration, synthetic credentials only");
        }
        return IkasanShell.createApplication().run("--spring.config.location=" + configuration.toUri());
    }
}
