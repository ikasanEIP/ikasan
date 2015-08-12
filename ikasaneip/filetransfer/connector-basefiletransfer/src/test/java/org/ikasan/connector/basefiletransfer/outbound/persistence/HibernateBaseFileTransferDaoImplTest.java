/*
 * $Id:$
 * $URL:$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.connector.basefiletransfer.outbound.persistence;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = { "/substitute-beans.xml" })
public class HibernateBaseFileTransferDaoImplTest
{

    @Resource SessionFactory sessionFactory;

    @Resource DataSource xaDataSource;

    BaseFileTransferDao uut;

    JdbcTemplate jdbcTemplate;

    String insertSql = "insert \n" + "    into\n" + "        FTFileFilter\n"
            + "        ( ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime) \n" + "    values\n"
            + "        ( ?, ?, ?, ?, ?, ?)";

    String selectSql = "SELECT ClientId, Criteria, LastModified, LastAccessed, Size FROM  FTFileFilter";

    @Before public void setup()
    {
        uut = new HibernateBaseFileTransferDaoImpl(sessionFactory);
        jdbcTemplate = new JdbcTemplate(xaDataSource);
    }

    @Test public void housekeep_when_nothing_to_process()
    {
        uut.housekeep("clientId", 1, 1);
    }

    @Test public void housekeep_when_single_file_to_be_deleted()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -30);
        //ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime
        Object[] params = new Object[] { "clientId", "/ful/path", cal.getTime(), cal.getTime(), 11111,
                cal.getTime().getTime() };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT,
                Types.BIGINT };
        // add data to FileFilter table
        int row = jdbcTemplate.update(insertSql, params, types);
        // do test
        uut.housekeep("clientId", 1, 1);
        List<FileFilter> result = jdbcTemplate.query(selectSql, new FileFilterRowMapper());
        assertEquals(0, result.size());
    }

    @Test public void housekeep_when_single_file__not_old_enough()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -1);
        //ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime
        Object[] params = new Object[] { "clientId", "/ful/path", cal.getTime(), cal.getTime(), 11111,
                cal.getTime().getTime() };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT,
                Types.BIGINT };
        // add data to FileFilter table
        int row = jdbcTemplate.update(insertSql, params, types);
        // do test
        uut.housekeep("clientId", 5, 1);
        List<FileFilter> result = jdbcTemplate.query(selectSql, new FileFilterRowMapper());
        assertEquals(1, result.size());
    }

    class FileFilterRowMapper implements RowMapper
    {
        @Override public FileFilter mapRow(ResultSet resultSet, int i) throws SQLException
        {
            //ClientId, Criteria, LastModified, LastAccessed, Size
            FileFilter fileFilter = new FileFilter(resultSet.getString("ClientId"), resultSet.getString("Criteria"),
                    resultSet.getDate("LastModified"), resultSet.getDate("LastAccessed"), resultSet.getInt("Size"));
            return fileFilter;
        }
    }
}
