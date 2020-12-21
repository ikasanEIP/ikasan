package org.ikasan.dashboard.ui.administration.view;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import liquibase.pro.packaged.S;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.administration.component.SystemEventDialog;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.util.SearchConstants;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.RoleModule;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.ArgumentMatchers.*;

public class SystemEventSearchViewTest extends UITest {
    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private Set<IkasanPrincipal> principals;

    @MockBean
    private IkasanPrincipal principal;

    @MockBean
    private Set<Role> roles;

    @MockBean
    private Role role;

    @MockBean
    private Set<RoleModule> roleModules;

    @MockBean
    private RoleModule roleModule;

    public void setup_expectations() {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(50));

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));
    }



    @Test
    public void test_search_admin_user()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("systemEvent")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(25));

        UI.getCurrent().navigate("adminSearchView");

        SystemEventSearchView systemEventSearchView = _get(SystemEventSearchView.class);
        Assertions.assertNotNull(systemEventSearchView);

        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));

        SolrSearchFilteringGrid searchResultsGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(systemEventSearchView, "searchResultsGrid");

        Assert.assertEquals(25, searchResultsGrid.getResultSize());
    }

    @Test
    public void test_search_non_admin_user_with_one_associated_module()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("systemEvent")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10));

        UI.getCurrent().navigate("adminSearchView");

        SystemEventSearchView systemEventSearchView = _get(SystemEventSearchView.class);
        Assertions.assertNotNull(systemEventSearchView);

        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));

        SolrSearchFilteringGrid searchResultsGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(systemEventSearchView, "searchResultsGrid");

        Assert.assertEquals(10, searchResultsGrid.getResultSize());
    }

    @Test
    public void test_search_non_admin_user_with_no_associated_module()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(new HashSet<RoleModule>().iterator(), new HashSet<RoleModule>().iterator());
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals(SearchConstants.NONSENSE_STRING)),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("systemEvent")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(0));

        UI.getCurrent().navigate("adminSearchView");

        SystemEventSearchView systemEventSearchView = _get(SystemEventSearchView.class);
        Assertions.assertNotNull(systemEventSearchView);

        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));

        SolrSearchFilteringGrid searchResultsGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(systemEventSearchView, "searchResultsGrid");

        Assert.assertEquals(0, searchResultsGrid.getResultSize());
    }

    @Test
    public void test_search_and_open_result_dialog()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("systemEvent")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        UI.getCurrent().navigate("adminSearchView");

        SystemEventSearchView systemEventSearchView = _get(SystemEventSearchView.class);
        Assertions.assertNotNull(systemEventSearchView);

        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("systemEventSearchFormSearchButton")));

        SolrSearchFilteringGrid searchResultsGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(systemEventSearchView, "searchResultsGrid");

        GridKt._doubleClickItem(searchResultsGrid, 0);

        SystemEventDialog systemEventDialog = _get(SystemEventDialog.class);

        Assert.assertEquals("admin", ((TextField)ReflectionTestUtils
            .getField(systemEventDialog, "actionedByTf")).getValue());
        Assert.assertEquals("murex-trade-tradeConsumer", ((TextField)ReflectionTestUtils
            .getField(systemEventDialog, "contextTf")).getValue());
        Assert.assertEquals("24/11/2020 07:39:20.055", ((TextField)ReflectionTestUtils
            .getField(systemEventDialog, "dateTimeTf")).getValue());
    }

    protected IkasanSolrDocumentSearchResults getSolrResults(int size) {

        ArrayList<IkasanSolrDocument> ikasanSolrDocuments = new ArrayList<>();

        IntStream.range(0, size).forEach(i -> {
            IkasanSolrDocument document = new IkasanSolrDocument();
            document.setId("id" +i);
            document.setType("systemEvent");
            document.setTimeStamp(1606203560055L);
            document.setEvent("{\"moduleName\":\"murex-trade\",\"action\":\"Configuration Updated OldConfig [{\\\"configurationId\\\":" +
                "\\\"murex-trade-tradeConsumer\\\",\\\"description\\\":null,\\\"parameters\\\":[{\\\"id\\\":54,\\\"name\\\":\\\"autoContentConversion\\\"" +
                ",\\\"value\\\":true,\\\"description\\\":null},{\\\"id\\\":55,\\\"name\\\":\\\"autoSplitBatch\\\",\\\"value\\\":true,\\\"description\\\":null}" +
                ",{\\\"id\\\":56,\\\"name\\\":\\\"batchMode\\\",\\\"value\\\":true,\\\"description\\\":null},{\\\"id\\\":57,\\\"name\\\":\\\"batchSize\\\"" +
                ",\\\"value\\\":0,\\\"description\\\":null},{\\\"id\\\":58,\\\"name\\\":\\\"cacheLevel\\\",\\\"value\\\":1,\\\"description\\\":null}" +
                ",{\\\"id\\\":59,\\\"name\\\":\\\"concurrentConsumers\\\",\\\"value\\\":1,\\\"description\\\":null},{\\\"id\\\":60,\\\"name\\\":" +
                "\\\"connectionFactoryJndiProperties\\\",\\\"value\\\":{\\\"java.naming.security.principal\\\":\\\"\\\",\\\"java.naming.factory.initial\\\":" +
                "\\\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\\\",\\\"java.naming.provider.url\\\":" +
                "\\\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\\\"" +
                ",\\\"java.naming.security.credentials\\\":\\\"\\\"},\\\"description\\\":null},{\\\"id\\\":61,\\\"name\\\":\\\"connectionFactoryName\\\",\\\"value\\\":" +
                "\\\"XAConnectionFactory\\\",\\\"description\\\":null},{\\\"id\\\":62,\\\"name\\\":\\\"connectionFactoryPassword\\\",\\\"value\\\":null," +
                "\\\"description\\\":null},{\\\"id\\\":63,\\\"name\\\":\\\"connectionFactoryUsername\\\",\\\"value\\\":null,\\\"description\\\":null}," +
                "{\\\"id\\\":64,\\\"name\\\":\\\"destinationJndiName\\\",\\\"value\\\":\\\"dynamicQueues/com.caixa.bank.murex.out\\\",\\\"description\\\":" +
                "null},{\\\"id\\\":65,\\\"name\\\":\\\"destinationJndiProperties\\\",\\\"value\\\":{\\\"java.naming.security.principal\\\":\\\"\\\"," +
                "\\\"java.naming.factory.initial\\\":\\\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\\\",\\\"java.naming.provider.url\\\":" +
                "\\\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\\\"," +
                "\\\"java.naming.security.credentials\\\":\\\"\\\"},\\\"description\\\":null},{\\\"id\\\":66,\\\"name\\\":\\\"durable\\\",\\\"value\\\":" +
                "true,\\\"description\\\":null},{\\\"id\\\":67,\\\"name\\\":\\\"durableSubscriptionName\\\",\\\"value\\\":\\\"murex-trade-tradeConsumer\\\"," +
                "\\\"description\\\":null},{\\\"id\\\":68,\\\"name\\\":\\\"maxConcurrentConsumers\\\",\\\"value\\\":1,\\\"description\\\":null},{\\\"id\\\":69," +
                "\\\"name\\\":\\\"pubSubDomain\\\",\\\"value\\\":false,\\\"description\\\":null},{\\\"id\\\":70,\\\"name\\\":\\\"sessionAcknowledgeMode\\\"," +
                "\\\"value\\\":null,\\\"description\\\":null},{\\\"id\\\":71,\\\"name\\\":\\\"sessionTransacted\\\",\\\"value\\\":false,\\\"description\\\":" +
                "null}]}] NewConfig [{\\\"configurationId\\\":\\\"murex-trade-tradeConsumer\\\",\\\"description\\\":null,\\\"parameters\\\":[{\\\"id\\\":54," +
                "\\\"name\\\":\\\"autoContentConversion\\\",\\\"value\\\":true,\\\"description\\\":null},{\\\"id\\\":55,\\\"name\\\":\\\"autoSplitBatch\\\"," +
                "\\\"value\\\":true,\\\"description\\\":null},{\\\"id\\\":56,\\\"name\\\":\\\"batchMode\\\",\\\"value\\\":true,\\\"description\\\":null}," +
                "{\\\"id\\\":57,\\\"name\\\":\\\"batchSize\\\",\\\"value\\\":0,\\\"description\\\":null},{\\\"id\\\":58,\\\"name\\\":\\\"cacheLevel\\\"," +
                "\\\"value\\\":1,\\\"description\\\":null},{\\\"id\\\":59,\\\"name\\\":\\\"concurrentConsumers\\\",\\\"value\\\":1,\\\"description\\\":null}," +
                "{\\\"id\\\":60,\\\"name\\\":\\\"connectionFactoryJndiProperties\\\",\\\"value\\\":{\\\"java.naming.security.principal\\\":\\\"\\\"," +
                "\\\"java.naming.factory.initial\\\":\\\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\\\",\\\"java.naming.provider.url\\\":" +
                "\\\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\\\"," +
                "\\\"java.naming.security.credentials\\\":\\\"\\\"},\\\"description\\\":null},{\\\"id\\\":61,\\\"name\\\":\\\"connectionFactoryName\\\"," +
                "\\\"value\\\":\\\"XAConnectionFactory\\\",\\\"description\\\":null},{\\\"id\\\":62,\\\"name\\\":\\\"connectionFactoryPassword\\\",\\\"value\\\":" +
                "null,\\\"description\\\":null},{\\\"id\\\":63,\\\"name\\\":\\\"connectionFactoryUsername\\\",\\\"value\\\":null,\\\"description\\\":null}," +
                "{\\\"id\\\":64,\\\"name\\\":\\\"destinationJndiName\\\",\\\"value\\\":\\\"dynamicQueues/com.caixa.bank.murex.out\\\",\\\"description\\\":" +
                "null},{\\\"id\\\":65,\\\"name\\\":\\\"destinationJndiProperties\\\",\\\"value\\\":{\\\"java.naming.security.principal\\\":\\\"\\\"," +
                "\\\"java.naming.factory.initial\\\":\\\"org.apache.activemq.jndi.ActiveMQInitialContextFactory\\\",\\\"java.naming.provider.url\\\":" +
                "\\\"tcp://localhost:61616?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=-1&jms.clientIDPrefix=murex-trade-tradeConsumer\\\"," +
                "\\\"java.naming.security.credentials\\\":\\\"\\\"},\\\"description\\\":null},{\\\"id\\\":66,\\\"name\\\":\\\"durable\\\",\\\"value\\\":true," +
                "\\\"description\\\":null},{\\\"id\\\":67,\\\"name\\\":\\\"durableSubscriptionName\\\",\\\"value\\\":\\\"murex-trade-tradeConsumer\\\"," +
                "\\\"description\\\":null},{\\\"id\\\":68,\\\"name\\\":\\\"maxConcurrentConsumers\\\",\\\"value\\\":1,\\\"description\\\":null}," +
                "{\\\"id\\\":69,\\\"name\\\":\\\"pubSubDomain\\\",\\\"value\\\":false,\\\"description\\\":null},{\\\"id\\\":70,\\\"name\\\":" +
                "\\\"sessionAcknowledgeMode\\\",\\\"value\\\":null,\\\"description\\\":null},{\\\"id\\\":71,\\\"name\\\":\\\"sessionTransacted\\\"," +
                "\\\"value\\\":false,\\\"description\\\":null}]}]\",\"actor\":\"admin\",\"id\":57,\"subject\":\"murex-trade-tradeConsumer\",\"timestamp\":" +
                "1606807854254,\"expiry\":1607412654254}");

            ikasanSolrDocuments.add(document);
        });

        return new IkasanSolrDocumentSearchResults(ikasanSolrDocuments
            , ikasanSolrDocuments.size(), 1);
    }
}
