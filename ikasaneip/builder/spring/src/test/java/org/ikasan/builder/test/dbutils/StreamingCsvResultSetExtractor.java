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

import org.h2.jdbc.JdbcClob;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.stream.Collectors;


/**
 * Streams result set db data to a csv string for comparison in tests
 */
public class StreamingCsvResultSetExtractor
        implements ResultSetExtractor<Void> {

    private char DELIMITER = ',';

    private final PrintWriter pw;


    public StreamingCsvResultSetExtractor(final PrintWriter pw) {
        this.pw = pw;
    }

    @Override
    public Void extractData(final ResultSet rs) {
        try (pw) {
            final ResultSetMetaData rsmd = rs.getMetaData();
            final int columnCount = rsmd.getColumnCount();
            writeHeader(rsmd, columnCount, pw);
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    final Object value = rs.getObject(i);
                    String strValue = "";
                    if (value instanceof JdbcClob) {
                        strValue = new BufferedReader(((Clob) value).getCharacterStream()).lines().collect(Collectors.joining());
                    } else {
                        strValue = value == null ? "" : value.toString();
                    }
                    pw.write(strValue.contains(",") ? "\"" + strValue + "\"" : strValue);
                    if (i != columnCount) {
                        pw.append(DELIMITER);
                    }
                }
                pw.println();
            }
            pw.flush();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void writeHeader(final ResultSetMetaData rsmd,
                             final int columnCount, final PrintWriter pw) throws SQLException {
        for (int i = 1; i <= columnCount; i++) {
            pw.write(rsmd.getColumnName(i));
            if (i != columnCount) {
                pw.append(DELIMITER);
            }
        }
        pw.println();
    }
}
