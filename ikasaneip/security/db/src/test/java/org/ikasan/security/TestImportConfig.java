package org.ikasan.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource( locations={
    "/h2-config.xml",
    "/test-transation.xml",
})
public class TestImportConfig
{

}
