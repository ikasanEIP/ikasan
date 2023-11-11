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

    @GetMapping(
        value = "/search",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) Integer criteria,
        @RequestParam(required = false) String clientId,
        @RequestParam(required = false) String fromDateTime,
        @RequestParam(required = false) String untilDateTime
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

    @GetMapping(
        value = "/",
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity get(
        @RequestParam Integer criteria,
        @RequestParam String clientId
    )
    {
        FilterEntry filterEntry = managementFilterService.find(criteria, clientId);
        return new ResponseEntity(filterEntry, HttpStatus.OK);
    }

    @DeleteMapping("/")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity delete(
        @RequestParam Integer criteria,
        @RequestParam String clientId
    )
    {
        managementFilterService.delete(criteria, clientId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(
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

    public void setManagementFilterService(ManagementFilterService managementFilterService)
    {
        this.managementFilterService = managementFilterService;
    }
}
