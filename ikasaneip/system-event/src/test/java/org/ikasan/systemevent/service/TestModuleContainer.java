package org.ikasan.systemevent.service;

import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.systemevent.model.TestModule;

import java.util.ArrayList;
import java.util.List;

public class TestModuleContainer implements ModuleContainer {

    private List<Module> modules;

    @Override
    public Module getModule(String moduleName) {
        return null;
    }

    @Override
    public List<Module> getModules() {
        this.modules = new ArrayList<>();

        TestModule testModule = new TestModule("url", "1.0.0", "name",
            null, "description");
        this.modules.add(testModule);

        return this.modules;
    }

    @Override
    public void add(Module module) {

    }

    @Override
    public void remove(String moduleName) {

    }
}
