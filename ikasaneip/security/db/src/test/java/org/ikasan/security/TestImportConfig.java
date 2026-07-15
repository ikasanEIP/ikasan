package org.ikasan.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( locations={
    "classpath:h2-config.xml",
    "classpath:test-transaction.xml",
})
public class TestImportConfig
{

}
