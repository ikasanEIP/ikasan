package org.ikasan.ootb.data.sharing.module.component.splitter;

import org.ikasan.ootb.data.sharing.module.model.ModuleAndConfigMetaData;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.metadata.model.ModuleMetaData;

import java.util.List;

public class ModuleMetaDataSplitter implements Splitter<ModuleAndConfigMetaData, ModuleMetaData> {

    @Override
    public List<ModuleMetaData> split(ModuleAndConfigMetaData moduleAndConfigMetaData) {
        return moduleAndConfigMetaData.getModules();
    }
}
