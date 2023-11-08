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
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.outbound.command.BaseFileTransferCommandJUnitHelper;
import org.ikasan.connector.basefiletransfer.persistence.FileFilter;
import org.ikasan.spec.search.PagedSearchResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(classes = { TestConfiguration.class })
public class HibernateBaseFileTransferDaoImplTest
{
    @Resource
    DataSource xaDataSource;

    @Resource
    BaseFileTransferDao uut;

    JdbcTemplate jdbcTemplate;

    String insertSql = """
        insert\s
            into
                FTFileFilter
                ( ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime)\s
            values
                ( ?, ?, ?, ?, ?, ?)\
        """;

    String deleteAllSql = "delete from FTFileFilter";

    String selectSql = "SELECT ClientId, Criteria, LastModified, LastAccessed, Size FROM  FTFileFilter";

    @Before
    public void setup()
    {
        jdbcTemplate = new JdbcTemplate(xaDataSource);
    }

    @After
    public void teardown()
    {
        jdbcTemplate.update(deleteAllSql, new Object[] {}, new int[] {});
    }

    @Test
    public void housekeep_when_nothing_to_process()
    {
        uut.housekeep("clientId", 1, 1);
    }

    @Test
    public void housekeep_when_single_file_to_be_deleted()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -30);
        //ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime
        Object[] params = new Object[] { "clientId", "/ful/path", cal.getTime(), cal.getTime(), 11111,
            cal.getTime().getTime()
        };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT,
            Types.BIGINT
        };
        // add data to FileFilter table
        int row = jdbcTemplate.update(insertSql, params, types);
        // do test
        uut.housekeep("clientId", 1, 1);
        List<FileFilter> result = jdbcTemplate.query(selectSql, new FileFilterRowMapper());
        assertEquals(0, result.size());
    }

    @Test
    public void housekeep_when_single_file__not_old_enough()
    {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.add(Calendar.DATE, -1);
        //ClientId, Criteria, LastModified, LastAccessed, Size, CreatedDateTime
        Object[] params = new Object[] { "clientId", "/ful/path", cal.getTime(), cal.getTime(), 11111,
            cal.getTime().getTime()
        };
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.BIGINT,
            Types.BIGINT
        };
        // add data to FileFilter table
        int row = jdbcTemplate.update(insertSql, params, types);
        // do test
        uut.housekeep("clientId", 5, 1);
        List<FileFilter> result = jdbcTemplate.query(selectSql, new FileFilterRowMapper());
        assertEquals(1, result.size());
    }

    @Test
    public void persistClientListEntry() throws URISyntaxException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        // test
        uut.persistClientListEntry(entry);

        List<FileFilter> result = jdbcTemplate.query(selectSql, new FileFilterRowMapper());
        assertEquals(1, result.size());
    }

    @Test
    public void isDuplicateWhenFilteringOnFileNameAndModifiedDate() throws URISyntaxException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        // insert Test Data
        uut.persistClientListEntry(entry);

        assertEquals(true, uut.isDuplicate(entry, true, true));
    }

    @Test
    public void isDuplicateWhenFilteringOnFileNameAndModifiedDateAndLastModifiedIsDifferent()
        throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        // insert Test Data
        uut.persistClientListEntry(entry);

        Thread.sleep(100);

        ClientListEntry entry2 = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry2.setFullPath("logback-test.xml");
        entry2.setLongFilename("logback-test.xml");

        assertEquals(false, uut.isDuplicate(entry2, true, true));
    }

    @Test
    public void isDuplicateWhenFilteringOnFileNameAndLastModifiedIsDifferent()
        throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        // insert Test Data
        uut.persistClientListEntry(entry);

        Thread.sleep(100);

        ClientListEntry entry2 = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry2.setFullPath("logback-test.xml");
        entry2.setLongFilename("logback-test.xml");

        assertEquals(true, uut.isDuplicate(entry2, true, false));
    }

    @Test
    public void findById() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        FileFilter result = uut.findById(fileFilter.getId());

        assertEquals("TestClient", result.getClientId());
        assertEquals("logback-test.xml", result.getCriteria());
        assertEquals(1024, result.getSize());

    }

    @Test
    public void findByIdWhenNoResults() throws URISyntaxException, InterruptedException
    {

        // do test
        FileFilter result = uut.findById(665);

        assertEquals(null,result);

    }

    @Test
    public void deleteById() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        uut.delete(fileFilter);

        //verify no data in db
        FileFilter result = uut.findById(fileFilter.getId());

        assertEquals(null,result);

    }

    @Test
    public void findByClientId() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        PagedSearchResult<FileFilter> results = uut.find(0, 100, null, "TestClient");
        assertEquals(1, results.getPagedResults().size());

        FileFilter result = results.getPagedResults().get(0);

        assertEquals("TestClient", result.getClientId());
        assertEquals("logback-test.xml", result.getCriteria());
        assertEquals(1024, result.getSize());
    }

    @Test
    public void findByFileName() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        PagedSearchResult<FileFilter> results = uut.find(0, 100, "logback-test.xml", null);
        assertEquals(1, results.getPagedResults().size());

        FileFilter result = results.getPagedResults().get(0);

        assertEquals("TestClient", result.getClientId());
        assertEquals("logback-test.xml", result.getCriteria());
        assertEquals(1024, result.getSize());
    }

    @Test
    public void findByFileNameUsingLikeWildcard() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        PagedSearchResult<FileFilter> results = uut.find(0, 100, "logback%", null);
        assertEquals(1, results.getPagedResults().size());

        FileFilter result = results.getPagedResults().get(0);

        assertEquals("TestClient", result.getClientId());
        assertEquals("logback-test.xml", result.getCriteria());
        assertEquals(1024, result.getSize());
    }

    @Test
    public void findWhenNoResult() throws URISyntaxException, InterruptedException
    {

        ClientListEntry entry = BaseFileTransferCommandJUnitHelper.createEntry("file://logback-test.xml");
        entry.setFullPath("logback-test.xml");
        entry.setLongFilename("logback-test.xml");

        FileFilter fileFilter = entry.toPersistObject();

        // insert Test Data
        uut.save(fileFilter);

        // do test
        PagedSearchResult<FileFilter> results = uut.find(0, 100, "xxx%", null);
        assertEquals(0, results.getPagedResults().size());


    }

    class FileFilterRowMapper implements RowMapper
    {
        @Override
        public FileFilter mapRow(ResultSet resultSet, int i) throws SQLException
        {
            //ClientId, Criteria, LastModified, LastAccessed, Size
            FileFilter fileFilter = new FileFilter(resultSet.getString("ClientId"), resultSet.getString("Criteria"),
                resultSet.getDate("LastModified"), resultSet.getDate("LastAccessed"), resultSet.getInt("Size")
            );
            return fileFilter;
        }
    }
}
