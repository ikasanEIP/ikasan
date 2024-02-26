package org.ikasan.cli.shell.operation;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultCleanTransientDirectoriesExecutableOperationImpl implements ExecutableOperation {
    private List<File> directoriesToRemove;

    public DefaultCleanTransientDirectoriesExecutableOperationImpl(List<File> directoriesToRemove) {
        this.directoriesToRemove = directoriesToRemove;
        if(this.directoriesToRemove == null) {
            throw new IllegalArgumentException("directoriesToRemove cannot be null!");
        }
    }
    @Override
    public String execute() throws RuntimeException {
        this.directoriesToRemove.forEach(dir -> {
            try {
                FileUtils.deleteDirectory(dir);
            } catch (IOException e) {
                throw new RuntimeException("An error has occurred attempting " +
                    "to clean up transient working directory", e);
            }
        });

        return String.format("Successfully deleted the following transient directories[%s]"
            , this.directoriesToRemove.stream().map(dir -> dir.getName()).collect(Collectors.joining(", ")));
    }
}
