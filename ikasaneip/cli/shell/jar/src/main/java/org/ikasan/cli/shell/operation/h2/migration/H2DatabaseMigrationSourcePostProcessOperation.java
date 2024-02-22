package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.operation.ExecutableOperation;
import org.ikasan.h2.migration.H2DbExtractPostProcessor;
import org.springframework.util.ResourceUtils;

public class H2DatabaseMigrationSourcePostProcessOperation implements ExecutableOperation {

    private String inputFilePath;
    private String outputFilPath;

    public H2DatabaseMigrationSourcePostProcessOperation(String inputFilePath, String outputFilPath) {
        this.inputFilePath = inputFilePath;
        if (this.inputFilePath == null) {
            throw new IllegalArgumentException("inputFilePath cannot be null!");
        }
        this.outputFilPath = outputFilPath;
        if (this.outputFilPath == null) {
            throw new IllegalArgumentException("outputFilPath cannot be null!");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        try {
            H2DbExtractPostProcessor h2DbExtractPostProcessor = new H2DbExtractPostProcessor();
            h2DbExtractPostProcessor.filterInsertStatements(ResourceUtils.getFile(inputFilePath),
                ResourceUtils.getFile(outputFilPath));
            return String.format("Successfully perform migration process post process. Pre process file [%s] -" +
                    " Post process file [%s]",
                inputFilePath,
                outputFilPath );
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("An error has occurred post processing the extracted source " +
                "H2 database SQL file."), e);
        }
    }
}
