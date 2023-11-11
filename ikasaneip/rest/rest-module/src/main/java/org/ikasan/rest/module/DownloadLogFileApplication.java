package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/rest/logs")
@RestController
public class DownloadLogFileApplication {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadLogFileApplication.class);

    @GetMapping( path = {"/listLogFiles"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity listLogFiles(@RequestParam(required = true, name = "maxFileSize") long maxFileSizeInBytes) {

        try {
            Path path = Path.of(System.getProperty("user.dir"), "logs");

            // If directory does not exist then return nothing
            File[] filesList = new File(path.toUri()).listFiles();
            if (filesList == null) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }

            // Get all files in the directory and put them in a Map
            Map<String, String> files = new HashMap<>();
            for (File file : filesList) {
                if (file.getAbsoluteFile().isFile()) {
                    if (StringUtils.containsAny(file.getName(), "application.log", "h2.log", "h2-server.log")
                                && file.length() < maxFileSizeInBytes) {
                        files.put(file.getName(), file.getAbsoluteFile().toString());
                    }
                }
            }

            // If no files in directory then return nothing
            if (files.isEmpty()) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(files);
                return new ResponseEntity(jsonResponse, HttpStatus.OK);

            } catch (JsonProcessingException e) {
                LOG.error("Unable to convert the list of files to JSON", e);
                return new ResponseEntity("Unable to convert the list of files to JSON", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Bad request made and unable to list the files in the log folder", e);
            return new ResponseEntity("Bad request made and unable to list the files in the log folder", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping( path = {"/downloadLogFile"}, produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("hasAnyAuthority('ALL','WebServiceAdmin')")
    public ResponseEntity<StreamingResponseBody> downloadLogFile(@RequestParam(required = true) String fullFilePath,
                                                                 @RequestParam(required = true, name = "maxFileSize") long maxFileSizeInBytes) {
        try {
            File file = new File(fullFilePath);
            String filename = file.getName();
            if (StringUtils.containsAny(filename, "application.log", "h2.log", "h2-server.log")
                        && file.length() < maxFileSizeInBytes) {
                InputStream inputStream = new FileInputStream(file);
                StreamingResponseBody body = outputStream -> FileCopyUtils.copy(inputStream, outputStream);

                return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment;filename=" + filename)
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(body);
            } else {
                return ResponseEntity
                    .internalServerError()
                    .header("Content-Disposition", "attachment;filename=" + filename)
                    .contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(outputStream -> {
                        String errorMsg = "Not able to download the file for [" + filename + "]. " +
                            "Log file maybe too big to download. Maximum size allowed is " + maxFileSizeInBytes + " bytes.";
                        outputStream.write(errorMsg.getBytes());
                        outputStream.close();
                    });
            }
        } catch (Exception e) {
            LOG.error("Something has gone wrong when trying to download the file [{}]", fullFilePath);
            return ResponseEntity
                .internalServerError()
                .header("Content-Disposition", "attachment;filename=error.txt")
                .contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .body(outputStream -> {
                    String errorMsg = "Something has gone wrong when trying to download the file [" + fullFilePath + "]";
                    outputStream.write(errorMsg.getBytes());
                    outputStream.close();
                });
        }
    }

}
