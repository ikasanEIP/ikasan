package org.ikasan.ootb.scheduler.agent.rest;

import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.bigqueue.service.BigQueueDirectoryManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Module application implementing the REST contract for queue management in the agents.
 */
@RequestMapping("/rest/big/queue")
@RestController
public class BigQueueManagementApplication {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueueManagementApplication.class);

    @Autowired
    private BigQueueDirectoryManagementService bigQueueDirectoryManagementService;

    @GetMapping(
        value = "/",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity getQueues() {
        try {
            List<String> queues = bigQueueDirectoryManagementService.listQueues();
            return new ResponseEntity(queues, HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to list queues. Error [%s]".formatted(e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
        value = "/size/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity size(@PathVariable String queueName) {
        try {
            long size = bigQueueDirectoryManagementService.size(queueName);
            return new ResponseEntity(size, HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to get size for queue [%s]. Error [%s]".formatted(queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
        value = "/size",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity size(@RequestParam(required = false, defaultValue = "true") boolean includeZeros) {
        try {
            Map<String, Long> size = bigQueueDirectoryManagementService.size(includeZeros);
            return new ResponseEntity(size, HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to get size for all queue. Error [%s]".formatted(e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
        value = "/peek/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity peek(@PathVariable String queueName) {
        try {
            BigQueueMessage bigQueueMessage = bigQueueDirectoryManagementService.peek(queueName);
            return new ResponseEntity(bigQueueMessage, HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to peek queue for queue [%s]. Error [%s]".formatted(queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(
        value = "/messages/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity messages(@PathVariable String queueName) {
        try {
            List<BigQueueMessage> messages = bigQueueDirectoryManagementService.getMessages(queueName);
            return new ResponseEntity(messages, HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to get message for queue [%s]. Error [%s]".formatted(queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(
        value = "/delete/{queueName}/{messageId}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteMessageId(@PathVariable String queueName,
                                          @PathVariable String messageId) {
        try {
            bigQueueDirectoryManagementService.deleteMessage(queueName, messageId);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            String message
                = "Got exception trying to delete message for queue [%s] message [%s]. Error [%s]".formatted(queueName, messageId, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(
        value = "/delete/allMessages/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteAllMessages(@PathVariable String queueName) {
        try {
            bigQueueDirectoryManagementService.deleteAllMessage(queueName);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            String message
                = "Got exception trying to delete all messages for queue [%s]. Error [%s]".formatted(queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(
        value = "/delete/{queueName}",
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity deleteQueue(@PathVariable String queueName) {
        try {
            bigQueueDirectoryManagementService.deleteQueue(queueName);
            return new ResponseEntity(HttpStatus.OK);

        } catch (Exception e) {
            String message = "Got exception trying to delete queue for queue [%s]. Error [%s]".formatted(queueName, e.getMessage());
            LOG.warn(message);
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
    }
}
