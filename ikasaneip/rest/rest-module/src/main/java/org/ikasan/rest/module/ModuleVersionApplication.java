package org.ikasan.rest.module;

import org.ikasan.IkasanVersion;
import org.ikasan.module.ApplicationContextProvider;
import org.ikasan.rest.module.dto.BuildPropertiesDto;
import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.metadata.FlowMetaDataProvider;
import org.ikasan.spec.metadata.ModuleMetaDataProvider;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleService;
import org.ikasan.spec.module.StartupControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * Module version info application implementing the REST contract
 */

@RequestMapping("/rest/module/version")
@RestController
public class ModuleVersionApplication
{
    private static Logger logger = LoggerFactory.getLogger(ModuleVersionApplication.class);


    @RequestMapping(method = RequestMethod.GET,
            value = "/info",
            produces = {"application/json"})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getModuleVersionInfo() {
        if(!ApplicationContextProvider.instance().getContext().containsBean("buildProperties")) {
            return new ResponseEntity(new ErrorDto("Spring build properties are not available within the " +
                "application context of your Ikasan integration module. Please include goal <goal>build-info</goal> in " +
                "the spring-boot-maven-plugin goals within your module jar pom.xml."), HttpStatus.NOT_FOUND);
        }

        BuildProperties buildProperties = (BuildProperties) ApplicationContextProvider.instance()
            .getContext().getBean("buildProperties");

        BuildPropertiesDto buildPropertiesDto = new BuildPropertiesDto();
        buildPropertiesDto.setName(buildProperties.getName());
        buildPropertiesDto.setArtifact(buildProperties.getArtifact());
        buildPropertiesDto.setGroup(buildProperties.getGroup());
        buildPropertiesDto.setVersion(buildProperties.getVersion());
        buildPropertiesDto.setIkasanVersion(IkasanVersion.getVersion());
        buildPropertiesDto.setBuildTimestamp(buildProperties.getTime().toEpochMilli());

        return new ResponseEntity(buildPropertiesDto, HttpStatus.OK);
    }
}
