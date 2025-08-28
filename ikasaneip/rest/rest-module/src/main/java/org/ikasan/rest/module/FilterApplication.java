package org.ikasan.rest.module;

import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.service.ManagementFilterService;
import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.rest.module.util.DateTimeConverter;
import org.ikasan.spec.search.PagedSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * Filter controller exposing CRUD to Message Filter entity
 */
@RequestMapping("/rest/filter")
@RestController
public class FilterApplication
{
    @Autowired
    private ManagementFilterService managementFilterService;

    private DateTimeConverter dateTimeConverter = new DateTimeConverter();

    /**
     * Retrieves a paged list of FilterEntry entities based on the provided criteria.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize the number of entries per page
     * @param criteria optional search criteria
     * @param clientId optional client ID for filtering
     * @param fromDateTime optional start date/time filter
     * @param untilDateTime optional end date/time filter
     * @return ResponseEntity containing the paged results of FilterEntry entities
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
        @RequestParam(value = "criteria", required = false) Integer criteria,
        @RequestParam(value = "clientId", required = false) String clientId,
        @RequestParam(value = "fromDateTime", required = false) String fromDateTime,
        @RequestParam(value = "untilDateTime", required = false) String untilDateTime
    )
    {
        PagedSearchResult<FilterEntry> pagedResult = null;
        try
        {
            pagedResult = managementFilterService.findMessagesByPage(pageNumber, pageSize,
                clientId, criteria, dateTimeConverter.getDate(fromDateTime), dateTimeConverter.getDate(untilDateTime));
            return new ResponseEntity(pagedResult, HttpStatus.OK);
        }
        catch (ParseException e)
        {
            return new ResponseEntity(new ErrorDto("fromDateTime or untilDateTime has invalid dateTime format not following yyyy-MM-dd'T'HH:mm:ss."),
                HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a FilterEntry based on the provided criteria and clientId.
     *
     * @param criteria the criteria for filtering
     * @param clientId the client ID for filtering
     * @return ResponseEntity containing the retrieved FilterEntry
     */
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(value = "criteria") Integer criteria,
        @RequestParam(value = "clientId") String clientId
    )
    {
        FilterEntry filterEntry = managementFilterService.find(criteria, clientId);
        return new ResponseEntity(filterEntry, HttpStatus.OK);
    }

    /**
     * Delete operation for a specific criteria and clientId.
     *
     * @param criteria the criteria value to identify the entry to delete
     * @param clientId the client ID associated with the entry to delete
     * @return ResponseEntity indicating the status of the delete operation
     */
    @RequestMapping(
        method = RequestMethod.DELETE,
        value = "/"
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity delete(
        @RequestParam(value = "criteria") Integer criteria,
        @RequestParam(value = "clientId") String clientId
    )
    {
        managementFilterService.delete(criteria, clientId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Creates a new FilterEntry based on the provided DefaultFilterEntry object and saves it using the managementFilterService.
     *
     * @param filterEntry the DefaultFilterEntry object to create and save
     * @return ResponseEntity representing the HTTP response entity with status CREATED
     */
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/",
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity create(
        @RequestBody DefaultFilterEntry filterEntry
    )
    {
        managementFilterService.save(filterEntry);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * Set the ManagementFilterService for the FilterApplication.
     *
     * @param managementFilterService the ManagementFilterService to be set
     */
    public void setManagementFilterService(ManagementFilterService managementFilterService)
    {
        this.managementFilterService = managementFilterService;
    }
}
