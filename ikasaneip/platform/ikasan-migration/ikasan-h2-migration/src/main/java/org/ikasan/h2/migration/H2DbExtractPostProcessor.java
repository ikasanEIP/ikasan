package org.ikasan.h2.migration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class H2DbExtractPostProcessor {

    private static final String LOB_TABLE_CREATION = "CREATE TABLE IF NOT EXISTS SYSTEM_LOB_STREAM(ID INT NOT NULL, " +
        "PART INT NOT NULL, CDATA VARCHAR, BDATA BINARY LARGE OBJECT, PRIMARY KEY(ID, PART));";

    private static final String LEGACY_LOB_TABLE_CREATION =  "CREATE TABLE IF NOT EXISTS SYSTEM_LOB_STREAM(ID INT NOT NULL, " +
        "PART INT NOT NULL, CDATA VARCHAR, BDATA BINARY);";
    private static final String LEGACY_LOB_TABLE_PK_CREATION = "CREATE PRIMARY KEY SYSTEM_LOB_STREAM_PRIMARY_KEY ON " +
        "SYSTEM_LOB_STREAM(ID, PART);";

    private static final String CRLF = "\r\n";

    public void filterInsertStatements(File in, File outfile) throws IOException {
        Scanner read = new Scanner(in);
        FileWriter out = new FileWriter(outfile);

        while (read.hasNextLine()) {
            String line = read.nextLine();
            if(line.strip().equals(LEGACY_LOB_TABLE_CREATION.strip())) {
                out.write(LOB_TABLE_CREATION);
                out.write(CRLF);
            }
            else if(line.strip().equals(LEGACY_LOB_TABLE_PK_CREATION.strip())){
                // we simply ignore this line as the PK creation is bundled in the table definition
            }
            else {
                out.write(line);
                out.write(CRLF);
            }
        }

        read.close();
        out.close();
    }
}
