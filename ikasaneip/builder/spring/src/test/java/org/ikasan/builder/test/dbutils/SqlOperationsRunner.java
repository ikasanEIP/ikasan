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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This is used by test classes to export, import and run queries against the database directly by running
 * sql this allows tests to exist independently of the hibernate infrastructure. It requires a DataSource and
 * a transactionManager to be passed in the constructor
*/
public class SqlOperationsRunner {


        private final DataSource dataSource;

        private TransactionTemplate transactionTemplate;

        private final JdbcTemplate jdbcTemplate;




        public SqlOperationsRunner(DataSource dataSource, PlatformTransactionManager platformTransactionManager) {
            this.dataSource = dataSource;
            this.jdbcTemplate=new JdbcTemplate(dataSource);
            this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        }

        /**
         * For use against H2 tables will dump the data in the chosen tables as a series of insert statements
         *
         * @param tables
         * @return
         */
        public String dumpH2TableDataAsInsertStatements(String ... tables){
            try {
                final StringWriter stringWriter = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(stringWriter);
                transactionTemplate.executeWithoutResult(transactionStatus -> jdbcTemplate.query("SCRIPT BLOCKSIZE 30000", new InsertSqlResultSetExtractor(
                    printWriter, tables
                )));
                return stringWriter.toString();
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        /**
         * Run any sql update statement
         * @param sql
         */
        public void update(String sql){
            try {
                transactionTemplate.executeWithoutResult(transactionStatus -> jdbcTemplate.update(sql));
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        /**
         * Deletes all rows from database table
         */
        public void deleteAllRows(String tableName){
            try {
                String sql = "delete from " + tableName;
                transactionTemplate.executeWithoutResult(transactionStatus -> jdbcTemplate.update(sql));
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        /**
         * Gets the number of rows in the passed in table
         */
        public int count(String table){
            try {

                String sql = "select count(*) from " + table;
                Integer count = transactionTemplate.execute(status -> jdbcTemplate.queryForObject(sql, Integer.class));
                return count;
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        /**
         * Returns the contents of a table out as csv
         */
        public String exportAllDataToCsv(String table){
            try {
                String sql = "select * from " + table;
                final StringWriter stringWriter = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(stringWriter);
                transactionTemplate.executeWithoutResult(transactionStatus -> jdbcTemplate.query(sql, new StreamingCsvResultSetExtractor(printWriter)));
                return nlFix(stringWriter.toString());
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        /**
         * Returns the contents of a table out as csv
         */
        public String exportRowsToCsv(String sql){
            try {
                final StringWriter stringWriter = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(stringWriter);
                transactionTemplate.executeWithoutResult(transactionStatus -> jdbcTemplate.query(sql, new StreamingCsvResultSetExtractor(printWriter)));
                return nlFix(stringWriter.toString());
            } catch(Exception exc){
                throw new RuntimeException(exc);
            }
        }

        public static String nlFix(String str) {
            return str.replace("\r\n", "\n");
        }
}
