package com.ikasan.sample.spring.boot.builderpattern;

import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricConfig;
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleAsymmetricStringEncryptor;
import org.jasypt.encryption.StringEncryptor;

import static com.ulisesbocchio.jasyptspringboot.util.AsymmetricCryptography.KeyFormat.PEM;

/**
 * This class is a simple implementation that has been used to encrypt the
 * database password 'sa' using the generated public key.
 */
public class PropertyEncryptor {
    public static void main(String[] args) {
        SimpleAsymmetricConfig config = new SimpleAsymmetricConfig();
        config.setKeyFormat(PEM);
        config.setPublicKeyLocation("jasypt_pub.pem");
        StringEncryptor encryptor = new SimpleAsymmetricStringEncryptor(config);
        String message = "sa";
        String encrypted = encryptor.encrypt(message);
        System.out.printf("Encrypted message %s\n", encrypted);
    }
}
