package org.ikasan.dashboard.ui.visualisation.correlate;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;

public class XpathCorrelator implements Correlator<String, String>
{
    private String xpath;

    public XpathCorrelator(String xpath)
    {
        this.xpath = xpath;
    }

    @Override
    public String correlate(String source)
    {
        String correlatingValue = new String();

        try
        {
            VTDGen vg = new VTDGen();
            vg.setDoc(source.getBytes());

            vg.parse(true);

            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath(this.xpath);
            ap.selectXPath(xpath);

            correlatingValue = ap.evalXPathToString();
        }
        catch (Exception e)
        {

            // Ignore as we don't really care
        }

        return correlatingValue;
    }
}
