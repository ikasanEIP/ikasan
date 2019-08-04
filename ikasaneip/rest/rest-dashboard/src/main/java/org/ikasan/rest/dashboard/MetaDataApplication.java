package org.ikasan.rest.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.rest.dashboard.model.WiretapEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;


/**
 * Metadata application implementing the REST contract
 */

@RequestMapping("/rest")
@RestController
public class MetaDataApplication
{
    private static Logger logger = LoggerFactory.getLogger(MetaDataApplication.class);

    private ObjectMapper mapper;

    public MetaDataApplication()
    {
        this.mapper = new ObjectMapper();
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/module/metadata")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity harvestWiretap(@RequestBody String metadataJsonPayload)
    {
//        try
//        {
            logger.info(metadataJsonPayload);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            throw new ResponseStatusException(
//                HttpStatus.BAD_REQUEST, "Cannot parse wiretap JSON!", e);
//        }

        return new ResponseEntity("Module metadata successfully captured!", HttpStatus.OK);
    }
}
