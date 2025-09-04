package org.ikasan.module.service;

import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.Module;

import java.util.ArrayList;
import java.util.List;

public class ModuleHarvestServiceImpl implements HarvestService<Module> {

    private ModuleContainer moduleContainer;

    /**
     * Constructs a new ModuleHarvestServiceImpl with the provided ModuleContainer.
     *
     * @param moduleContainer the ModuleContainer instance to be used with the service
     * @throws IllegalArgumentException if moduleContainer is null
     */
    public ModuleHarvestServiceImpl(ModuleContainer moduleContainer) {
        this.moduleContainer = moduleContainer;
        if(moduleContainer == null)
        {
            throw new IllegalArgumentException("moduleContainer cannot be 'null'");
        }
    }

    @Override
    public List<Module> harvest(int transactionBatchSize) {
        List<Module> modules =  moduleContainer.getModules();
        if(modules != null) {
            return modules;
        }
        else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean harvestableRecordsExist() {
        // we always have a record to harvest!
        return true;
    }

    @Override
    public void saveHarvestedRecord(Module harvestedRecord) {
        // not relevant, therefore not implemented!
    }

    @Override
    public void updateAsHarvested(List<Module> events) {
        // not relevant, therefore not implemented!
    }
}
