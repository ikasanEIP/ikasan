package org.ikasan.configurationService.metadata.configuration;

import java.util.List;
import java.util.Map;

public class DummyConfiguration
{
    private String string;
    private Long aLong;
    private Integer integer ;
    private Boolean aBoolean;
    private Map<String, String> map;
    private List<String> list;

    public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }

    public Long getaLong()
    {
        return aLong;
    }

    public void setaLong(Long aLong)
    {
        this.aLong = aLong;
    }

    public Integer getInteger()
    {
        return integer;
    }

    public void setInteger(Integer integer)
    {
        this.integer = integer;
    }

    public Boolean getaBoolean()
    {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean)
    {
        this.aBoolean = aBoolean;
    }

    public Map<String, String> getMap()
    {
        return map;
    }

    public void setMap(Map<String, String> map)
    {
        this.map = map;
    }

    public List<String> getList()
    {
        return list;
    }

    public void setList(List<String> list)
    {
        this.list = list;
    }
}
