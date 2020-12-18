package org.ikasan.dashboard.ui.search.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class SearchFormTest extends UITest {

    @Test
    public void test_security_wiretap_read()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_READ))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_wiretap_write()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_WRITE))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_wiretap_admin()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.WIRETAP_ADMIN))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_error_read()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_READ))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_error_write()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_WRITE))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_error_admin()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ERROR_ADMIN))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_exclusion_read()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_READ))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_exclusion_write()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_WRITE))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_exclusion_admin()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.EXCLUSION_ADMIN))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_replay_read()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_READ))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_replay_write()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_WRITE))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_replay_admin()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.REPLAY_ADMIN))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(false, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_security_all()
    {
        Mockito.when(ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        SecurityContextHolder.getContext().setAuthentication(ikasanAuthentication);

        SearchForm searchForm = new SearchForm();

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "hospitalCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "replayCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "wiretapCheckButton")).isVisible());
        Assert.assertEquals(true, ((Button)ReflectionTestUtils.getField(searchForm, "errorCheckButton")).isVisible());
    }

    @Test
    public void test_wiretap_button_click()
    {
        SearchForm searchForm = _get(SearchForm.class);

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));

        _click(_get(Button.class, spec -> spec.withId("wiretapCheckButton")));

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));

        _click(_get(Button.class, spec -> spec.withId("wiretapCheckButton")));

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "wiretapChecked"));
    }

    @Test
    public void test_error_button_click()
    {
        SearchForm searchForm = _get(SearchForm.class);

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        _click(_get(Button.class, spec -> spec.withId("errorCheckButton")));

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "errorChecked"));

        _click(_get(Button.class, spec -> spec.withId("errorCheckButton")));

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "errorChecked"));
    }

    @Test
    public void test_exclusion_button_click()
    {
        SearchForm searchForm = _get(SearchForm.class);

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));

        _click(_get(Button.class, spec -> spec.withId("hospitalCheckButton")));

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));

        _click(_get(Button.class, spec -> spec.withId("hospitalCheckButton")));

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "hospitalChecked"));
    }

    @Test
    public void test_replay_button_click()
    {
        SearchForm searchForm = _get(SearchForm.class);

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));

        _click(_get(Button.class, spec -> spec.withId("replayCheckButton")));

        Assert.assertEquals(false, ReflectionTestUtils.getField(searchForm, "replayChecked"));

        _click(_get(Button.class, spec -> spec.withId("replayCheckButton")));

        Assert.assertEquals(true, ReflectionTestUtils.getField(searchForm, "replayChecked"));
    }

    @Override
    public void setup_expectations() {

    }
}
