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

    /**
     * Retrieves all in-doubt transactions and converts them into a list of InDoubtTransactionDto objects.
     * This endpoint requires the user to have 'ALL' or 'WebServiceAdmin' authority.
     *
     * @return ResponseEntity containing a List of InDoubtTransactionDto objects representing the in-doubt transactions
     *         with HTTP status OK if successful, or an ErrorDto with an error message and HTTP status BAD_REQUEST if
     *         an error occurs.
     */
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

    /**
     * Retrieves the in-doubt transaction with the specified name.
     *
     * @param transactionName the name of the transaction to retrieve
     * @return ResponseEntity containing the InDoubtTransactionDto representing the in-doubt transaction with HTTP
     * status OK if successful or an ErrorDto with an error message and HTTP status BAD_REQUEST if an error occurs
     */
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

    /**
     * Commits an in-doubt transaction with the specified transaction name.
     *
     * @param transactionName the name of the in-doubt transaction to commit
     * @return ResponseEntity representing the status of the commit operation. Returns a ResponseEntity with
     *         a success message and HTTP status OK if the commit is successful. Returns a ResponseEntity with
     *         an error message and HTTP status BAD_REQUEST if an error occurs during the commit process.
     */
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

    /**
     * Commits all in-doubt transactions.
     *
     * This method initiates the commit process for all in-doubt transactions. An in-doubt transaction
     * is a transaction that has not yet been committed or rolled back and is in an uncertain state.
     * By calling this method, all in-doubt transactions will be committed and their state will be
     * updated accordingly.
     *
     * @return ResponseEntity representing the status of the commit operation. Returns a ResponseEntity with
     *         a success message and HTTP status OK if the commit is successful. Returns a ResponseEntity with
     *         an error message and HTTP status BAD_REQUEST if an error occurs during the commit process.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/commitAll")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity commitAllTransactions() {
        try {
            this.inDoubtTransactionService.commitAllInDoubtTransactions();
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred committing all in doubt transactions." +
                " Some of the transactions may have committed successfully. Please query the /rest/transaction/inDoubt/all service" +
                " to determine which transactions are still waiting to be committed." +
                " Error[%s]", e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(String.format("All in doubt transactions have been successfully committed!"), HttpStatus.OK);
    }

    /**
     * Rolls back an in-doubt transaction with the specified transaction name.
     *
     * @param transactionName the name of the in-doubt transaction to rollback
     * @return ResponseEntity containing a success message and HTTP status OK if the rollback is successful.
     *         If an error occurs during the rollback process, returns a ResponseEntity with an error message
     *         and HTTP status BAD_REQUEST.
     */
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

    /**
     * Rolls back all in-doubt transactions.
     *
     * This method initiates the rollback process for all in-doubt transactions. An in-doubt transaction
     * is a transaction that has not yet been committed or rolled back and is in an uncertain state.
     * By calling this method, all in-doubt transactions will be rolled back and their state will be
     * updated accordingly.
     *
     * @return ResponseEntity representing the status of the rollback operation. Returns a ResponseEntity with
     *         a success message and HTTP status OK if the rollback is successful. Returns a ResponseEntity with
     *         an error message and HTTP status BAD_REQUEST if an error occurs during the rollback process.
     */
    @RequestMapping(method = RequestMethod.PUT,
        value = "/rollbackAll")
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity rollbackAllTransactions() {
        try {
            this.inDoubtTransactionService.rollbackAllInDoubtTransactions();
        }
        catch (Exception e) {
            return new ResponseEntity(new ErrorDto(String.format("An error has occurred rolling back all in doubt transactions." +
                " Some of the transactions may have rolled back successfully. Please query the /rest/transaction/inDoubt/all service" +
                " to determine which transactions are still waiting to be rolled back." +
                " Error[%s]", e.getMessage())), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity("All in doubt transactions have been successfully rolled back!", HttpStatus.OK);
    }

    /**
     * Converts an InDoubtTransaction object to an InDoubtTransactionDto object.
     *
     * @param inDoubtTransaction the InDoubtTransaction to be converted
     * @return the converted InDoubtTransactionDto object
     */
    private InDoubtTransactionDto convert(InDoubtTransaction inDoubtTransaction) {
        InDoubtTransactionDto inDoubtTransactionDto = new InDoubtTransactionDto();
        inDoubtTransactionDto.setTransactionName(inDoubtTransaction.getTransactionName());
        inDoubtTransactionDto.setTransactionState(inDoubtTransaction.getTransactionState());

        return inDoubtTransactionDto;
    }
}
