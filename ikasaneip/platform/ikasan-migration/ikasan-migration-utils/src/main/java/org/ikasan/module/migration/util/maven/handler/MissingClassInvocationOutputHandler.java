package org.ikasan.module.migration.util.maven.handler;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.ikasan.module.migration.util.maven.model.CompilationFailureMissingClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ikasan.module.migration.util.maven.MavenProjectBuilder.*;

public class MissingClassInvocationOutputHandler implements InvocationOutputHandler {
    private boolean errorStarted = false;
    private boolean errorEnded = false;
    private List<CompilationFailureMissingClass> missingClassList = new ArrayList<>();
    private String symbol;

    @Override
    public void consumeLine(String s) throws IOException {
        System.out.println(s);
        if(s.contains(ERROR) && s.contains(CANNOT_FIND_SYMBOL)) {
            errorStarted = true;
        }
        else if(s.contains(ERROR) && s.contains(SYMBOL) && errorStarted) {
            symbol = s.substring(s.lastIndexOf("class ")+6, s.length());

            missingClassList.add(new CompilationFailureMissingClass(symbol, symbol));
            errorStarted = false;
            symbol = null;
        }
    }

    /**
     * Retrieves the list of CompilationFailureMissingClass instances representing missing classes
     * that were not found during the compilation process.
     *
     * @return The list of CompilationFailureMissingClass instances.
     */
    public List<CompilationFailureMissingClass> getMissingClassList() {
        return missingClassList;
    }
}
