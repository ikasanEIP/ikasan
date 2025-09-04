/*
 * $Id$
 * $URL$
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
package org.ikasan.module.service;

import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for ModuleServiceImpl
 *
 * @author Ikasan Development Team
 */
public class ModuleHarvestServiceImplTest
{
    private Mockery mockery = new Mockery()
    {{
            setImposteriser(ClassImposteriser.INSTANCE);
            setThreadingPolicy(new Synchroniser());
    }};

    ModuleContainer moduleContainer = mockery.mock(ModuleContainer.class);
    Module module = mockery.mock(Module.class);

    @Test(expected = IllegalArgumentException.class)
    public void test_constructor_null_moduleContainer()
    {
        new ModuleHarvestServiceImpl(null);
    }


    @Test
    public void test_get_flow_states() {
        mockery.checking(new Expectations(){{
            exactly(1).of(moduleContainer).getModules();
            will(returnValue(List.of(module)));
        }});

        ModuleHarvestServiceImpl moduleHarvestService = new ModuleHarvestServiceImpl(this.moduleContainer);
        List<Module> modules = moduleHarvestService.harvest(-1);
        assertEquals(1, modules.size());

        assertTrue(moduleHarvestService.harvestableRecordsExist());
        mockery.assertIsSatisfied();
    }

    @Test
    public void test_get_flow_states_no_result_null_modules() {
        mockery.checking(new Expectations(){{
            exactly(1).of(moduleContainer).getModules();
            will(returnValue(null));
        }});

        ModuleHarvestServiceImpl moduleHarvestService = new ModuleHarvestServiceImpl(this.moduleContainer);

        List<Module> modules = moduleHarvestService.harvest(-1);
        assertEquals(0, modules.size());

        mockery.assertIsSatisfied();
    }

    @Test
    public void test_get_flow_states_no_result_empty_modules() {
        mockery.checking(new Expectations(){{
            exactly(1).of(moduleContainer).getModules();
            will(returnValue(List.of()));
        }});

        ModuleHarvestServiceImpl moduleHarvestService = new ModuleHarvestServiceImpl(this.moduleContainer);

        List<Module> modules = moduleHarvestService.harvest(-1);
        assertEquals(0, modules.size());

        mockery.assertIsSatisfied();
    }
}
