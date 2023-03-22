/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */
package org.ikasan.builder.test.dbutils;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used to get only the insert statements out when the "SCRIPT" command is run against an h2 database
 */
public  class InsertSqlResultSetExtractor
        implements ResultSetExtractor<Void> {


    private final PrintWriter pw;

    private String[] tablesOfInterest;

    public InsertSqlResultSetExtractor(final PrintWriter pw, String... tablesOfInterest) {
        this.pw = pw;
        this.tablesOfInterest = tablesOfInterest;
    }

    @Override
    public Void extractData(final ResultSet rs) {
        pw.println();
        try (pw) {
            while (rs.next()) {
                final Object value = rs.getObject(1);
                String strValue = value == null ? "" : value.toString();
                if (strValue.contains("INSERT")) {
                    for (String tableOfInterest : tablesOfInterest){
                        if (strValue.contains(tableOfInterest.toUpperCase())) {
                            pw.write(strValue);
                            pw.println();
                            break;
                        }
                    }
                }

            }
            pw.flush();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}