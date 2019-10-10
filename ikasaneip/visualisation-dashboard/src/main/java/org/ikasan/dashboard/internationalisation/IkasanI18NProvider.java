package org.ikasan.dashboard.internationalisation;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
public class IkasanI18NProvider implements I18NProvider
{
    Logger logger = LoggerFactory.getLogger(IkasanI18NProvider.class);

    public static final String RESOURCE_BUNDLE_NAME = "ikasanapp";

    private static final ResourceBundle RESOURCE_BUNDLE_EN = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME , Locale.ENGLISH);
    private static final ResourceBundle RESOURCE_BUNDLE_DE = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME , Locale.GERMAN);
    private static final ResourceBundle RESOURCE_BUNDLE_JP = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME , Locale.JAPAN);

    private static final List<Locale> providedLocales;

    static
    {
        providedLocales = new ArrayList<>();
        providedLocales.add(Locale.ENGLISH);
        providedLocales.add(Locale.GERMAN);
        providedLocales.add(Locale.JAPAN);
    }


    @Override
    public List<Locale> getProvidedLocales()
    {
        return providedLocales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params)
    {
        ResourceBundle resourceBundle = RESOURCE_BUNDLE_EN;

        if(Locale.GERMAN.equals(locale))
        {
            resourceBundle = RESOURCE_BUNDLE_DE;
        }
        else if(Locale.JAPAN.equals(locale))
        {
            resourceBundle = RESOURCE_BUNDLE_JP;
        }

        //resourceBundle = RESOURCE_BUNDLE_JP;

        if (!resourceBundle.containsKey(key))
        {
            logger.info("missing resource key (i18n) " + key);
            return key + " - " + locale;
        }
        else
        {
            try
            {
                return new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return "";
            }
//            return (resourceBundle.containsKey(key)) ? resourceBundle.getString(key) : key;
        }
    }
}
