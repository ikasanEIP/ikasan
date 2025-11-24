package org.ikasan.module.migration.util.maven.model;

public class CompilationFailureMissingPackage {
    private String packageName;

    public CompilationFailureMissingPackage(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Retrieves the symbol associated with this object.
     *
     * @return The symbol as a String.
     */
    public String getPackageName() {
        return packageName;
    }
}
