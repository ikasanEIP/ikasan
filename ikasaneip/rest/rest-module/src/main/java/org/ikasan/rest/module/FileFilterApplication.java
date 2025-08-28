package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.systemevent.SystemEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * File Filter controller exposing CRUD to Message Filter entity
 */
@RequestMapping("/rest/filefilter")
@RestController
public class FileFilterApplication
{
    private static Logger logger = LoggerFactory.getLogger(FileFilterApplication.class);

    @Autowired
    private BaseFileTransferDao baseFileTransferDao;

    @Autowired
    private SystemEventService systemEventService;

    private ObjectMapper mapper;

    public FileFilterApplication() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule m = new SimpleModule();
        this.mapper.registerModule(m);
    }

    /**
     * Retrieves a paged result of FileFilter objects based on the provided criteria and client ID.
     *
     * @param pageNumber the page number to retrieve, default is 0
     * @param pageSize the size of the page, default is 20
     * @param criteria the criteria to search for, can be null
     * @param clientId the client ID to filter the results by, can be null
     * @return ResponseEntity containing the paged search result of FileFilter objects
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/search",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
        @RequestParam(value = "criteria", required = false) String criteria,
        @RequestParam(value = "clientId", required = false) String clientId
                             )
    {
        PagedSearchResult<FileFilter>  pagedResult = baseFileTransferDao.find(pageNumber, pageSize, criteria,clientId);
        return new ResponseEntity(pagedResult, HttpStatus.OK);

    }

    /**
     * Retrieves a FileFilter object based on the provided ID.
     *
     * @param id the ID of the FileFilter to retrieve
     * @return ResponseEntity containing the retrieved FileFilter object
     */
    @RequestMapping(method = RequestMethod.GET,
                    value = "/",
                    produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(@RequestParam(value = "id") Integer id)
    {
        FileFilter fileFilter = baseFileTransferDao.findById(id.intValue());
        return new ResponseEntity(fileFilter, HttpStatus.OK);
    }

    /**
     * Deletes a FileFilter object based on the provided ID.
     *
     * @param id the ID of the FileFilter to be deleted
     * @return ResponseEntity indicating the result of the deletion operation
     */
    @RequestMapping(method = RequestMethod.DELETE,
                    value = "/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity delete(@RequestParam(value = "id") Integer id)
    {
        FileFilter fileFilter = baseFileTransferDao.findById(id.intValue());
        if ( fileFilter != null )
        {
            baseFileTransferDao.delete(fileFilter);
            try
            {
                String fileFilterJson = mapper.writeValueAsString(fileFilter);

                this.systemEventService.logSystemEvent(fileFilter.getClientId() +"_"+ fileFilter.getCriteria(),
                    "File Filter deleted [%s]".formatted(fileFilterJson), UserUtil.getUser()
                                                      );
            }
            catch (JsonProcessingException e)
            {
                logger.warn("Issue converting file filter to json." + fileFilter, e);
            }
            return new ResponseEntity(HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Creates a new file filter object in the system.
     *
     * @param fileFilter the file filter object to be created
     * @return ResponseEntity representing the HTTP status of the create operation
     */
    @RequestMapping(method = RequestMethod.POST,
                    value = "/",
                    consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity create(@RequestBody FileFilter fileFilter)
    {
        baseFileTransferDao.save(fileFilter);
        try
        {
            String fileFilterJson = mapper.writeValueAsString(fileFilter);

            this.systemEventService.logSystemEvent(fileFilter.getClientId() +"_"+ fileFilter.getCriteria(),
                "File Filter created [%s]".formatted(fileFilterJson), UserUtil.getUser());
        }
        catch (JsonProcessingException e)
        {
            logger.warn("Issue converting file filter to json." + fileFilter, e);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * Sets the BaseFileTransferDao to be used for file transfer operations.
     *
     * @param baseFileTransferDao the BaseFileTransferDao implementation to be set
     */
    public void setBaseFileTransferDao(BaseFileTransferDao baseFileTransferDao)
    {
        this.baseFileTransferDao = baseFileTransferDao;
    }
}
