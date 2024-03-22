package org.ikasan.backup.h2;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class IkasanBackupWithEaiAutoTestConfiguration extends IkasanBackupAutoTestConfiguration {

    @Bean("eaiXaDataSource")
    public DataSource eaiXaDataSource() {
        return Mockito.mock(DataSource.class);
    }

}
