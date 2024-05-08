package org.ikasan.rest.module;

import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.rest.module.dto.TableRowCountDto;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is the REST controller for the persistence operations.
 * It handles requests related to retrieving the total record count for a given database table.
 */
@RequestMapping("/rest/persistence")
@RestController
public class PersistenceApplication
{
    private static Logger logger = LoggerFactory.getLogger(PersistenceApplication.class);

    @Autowired
    private GeneralDatabaseService generalDatabaseService;

    public PersistenceApplication() {
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/rowCount/{tableName}",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity rowCount(@PathVariable("tableName") String tableName)
    {
        try {
            int rowCount = this.generalDatabaseService.getRecordCountForDatabaseTable(tableName);
            TableRowCountDto tableRowCount = new TableRowCountDto();
            tableRowCount.setRowCount(rowCount);
            tableRowCount.setTableName(tableName);

            return new ResponseEntity(tableRowCount, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred requesting row count " +
                "for table [%s]. Error[%s]", tableName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
    }
}
