package com.ikasan.sample.spring.boot.builderpattern;

import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.core.env.ConfigurableEnvironment;

public class EncryptedEnvironmentContextLoader extends SpringBootContextLoader {
    @Override
    protected ConfigurableEnvironment getEnvironment() {
        return new StandardEncryptableEnvironment();
    }
}
