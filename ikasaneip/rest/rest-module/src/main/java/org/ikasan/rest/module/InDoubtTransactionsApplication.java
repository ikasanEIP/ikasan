package org.ikasan.rest.module;

import org.ikasan.rest.module.dto.ErrorDto;
import org.ikasan.rest.module.dto.InDoubtTransactionDto;
import org.ikasan.rest.module.dto.TableRowCountDto;
import org.ikasan.rest.module.util.UserUtil;
import org.ikasan.spec.persistence.model.InDoubtTransaction;
import org.ikasan.spec.persistence.service.GeneralDatabaseService;
import org.ikasan.spec.persistence.service.InDoubtTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The InDoubtTransactionsApplication class is a RESTful API controller for handling
 * in-doubt transactions.
 */
@RequestMapping("/rest/transaction/inDoubt")
@RestController
public class InDoubtTransactionsApplication
{
    private static Logger logger = LoggerFactory.getLogger(InDoubtTransactionsApplication.class);

    @Autowired
    private InDoubtTransactionService inDoubtTransactionService;

    public InDoubtTransactionsApplication() {
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = "/all",
                    produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getAllInDoubtTransactions()
    {
        try {
            List<InDoubtTransaction> inDoubtTransactions = this.inDoubtTransactionService.getInDoubtTransactions();
            List<InDoubtTransactionDto> inDoubtTransactionDtos = inDoubtTransactions.stream()
                .map(inDoubtTransaction -> this.convert(inDoubtTransaction))
                .collect(Collectors.toList());

            return new ResponseEntity(inDoubtTransactionDtos, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred requesting all in doubt transactions! " +
                "Error[%s]",  e.getMessage())), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET,
        value = "/get/{transactionName}",
        produces = { "application/json" })
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getInDoubtTransaction(@PathVariable("transactionName") String transactionName)
    {
        try {
            InDoubtTransaction inDoubtTransaction = this.inDoubtTransactionService.getInDoubtTransaction(transactionName);

            if(inDoubtTransaction == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            InDoubtTransactionDto inDoubtTransactionDto = this.convert(inDoubtTransaction);

            return new ResponseEntity(inDoubtTransactionDto, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred requesting in doubt transaction " +
                "for transaction name [%s]. Error[%s]", transactionName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/commit/{transactionName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity commitTransaction(@PathVariable("transactionName") String transactionName) {
        try {
            this.inDoubtTransactionService.commitInDoubtTransaction(transactionName);
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred committing in doubt transaction" +
                "[%s]. Error[%s]", transactionName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(String.format("Transaction[%s] successfully committed!", transactionName), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
        value = "/rollback/{transactionName}")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity rollbackTransaction(@PathVariable("transactionName") String transactionName) {
        try {
            this.inDoubtTransactionService.rollbackInDoubtTransaction(transactionName);
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred rolling back in doubt transaction" +
                "[%s]. Error[%s]", transactionName, e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("Transaction[%s] successfully rolled back!".formatted(transactionName), HttpStatus.OK);
    }

    private InDoubtTransactionDto convert(InDoubtTransaction inDoubtTransaction) {
        InDoubtTransactionDto inDoubtTransactionDto = new InDoubtTransactionDto();
        inDoubtTransactionDto.setTransactionName(inDoubtTransaction.getTransactionName());
        inDoubtTransactionDto.setTransactionState(inDoubtTransaction.getTransactionState());

        return inDoubtTransactionDto;
    }
}
