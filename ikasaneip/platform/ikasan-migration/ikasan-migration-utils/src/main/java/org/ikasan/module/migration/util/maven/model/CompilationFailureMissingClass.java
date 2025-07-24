package org.ikasan.module.migration.util.maven.model;

public class CompilationFailureMissingClass {
    private String symbol;
    private String location;

    /**
     * Constructs a new CompilationFailureMissingClass object with the given symbol and location.
     *
     * @param symbol The symbol associated with the missing class.
     * @param location The location where the missing class was referenced.
     */
    public CompilationFailureMissingClass(String symbol, String location) {
        this.symbol = symbol;
        this.location = location;
    }

    /**
     * Retrieves the symbol associated with this object.
     *
     * @return The symbol as a String.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Retrieves the location associated with this object.
     *
     * @return The location as a String.
     */
    public String getLocation() {
        return location;
    }
}
