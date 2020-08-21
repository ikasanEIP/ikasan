package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.spec.metadata.DecoratorMetaData;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

import java.util.List;

public abstract class AbstractWiretapNode extends Node {
    private static final String WIRETAP_IMAGE = "frontend/images/wiretap.png";
    private static final String LOG_WIRETAP_IMAGE = "frontend/images/log-wiretap.png";

    private String wiretapBeforeStatus = NodeFoundStatus.EMPTY;
    private String wiretapAfterStatus = NodeFoundStatus.EMPTY;
    private String logWiretapBeforeStatus = NodeFoundStatus.EMPTY;
    private String logWiretapAfterStatus = NodeFoundStatus.EMPTY;

    protected double wiretapBeforeImageX = -80;
    protected double wiretapBeforeImageY = -40;
    protected int wiretapBeforeImageH = 30;
    protected int wiretapBeforeImageW = 30;

    protected double wiretapAfterImageX = 50;
    protected double wiretapAfterImageY = -40;
    protected int wiretapAfterImageH = 30;
    protected int wiretapAfterImageW = 30;

    protected double logWiretapBeforeImageX = -80;
    protected double logWiretapBeforeImageY = -80;
    protected int logWiretapBeforeImageH = 30;
    protected int logWiretapBeforeImageW = 30;

    protected double logWiretapAfterImageX = 50;
    protected double logWiretapAfterImageY = -80;
    protected int logWiretapAfterImageH = 30;
    protected int logWiretapAfterImageW = 30;


    private List<DecoratorMetaData> decoratorMetaDataList;

    public AbstractWiretapNode(String id,  String name, String image) {
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(image));
    }

    public String getWiretapBeforeStatus() {
        return wiretapBeforeStatus;
    }

    public void setWiretapBeforeStatus(String wiretapBeforeStatus) {
        this.wiretapBeforeStatus = wiretapBeforeStatus;
    }

    public String getWiretapAfterStatus() {
        return wiretapAfterStatus;
    }

    public void setWiretapAfterStatus(String wiretapAfterStatus) {
        this.wiretapAfterStatus = wiretapAfterStatus;
    }

    public double getWiretapBeforeImageX() {
        return wiretapBeforeImageX;
    }

    public void setWiretapBeforeImageX(double wiretapBeforeImageX) {
        this.wiretapBeforeImageX = wiretapBeforeImageX;
    }

    public double getWiretapBeforeImageY() {
        return wiretapBeforeImageY;
    }

    public void setWiretapBeforeImageY(double wiretapBeforeImageY) {
        this.wiretapBeforeImageY = wiretapBeforeImageY;
    }

    public int getWiretapBeforeImageH() {
        return wiretapBeforeImageH;
    }

    public void setWiretapBeforeImageH(int wiretapBeforeImageH) {
        this.wiretapBeforeImageH = wiretapBeforeImageH;
    }

    public int getWiretapBeforeImageW() {
        return wiretapBeforeImageW;
    }

    public void setWiretapBeforeImageW(int wiretapBeforeImageW) {
        this.wiretapBeforeImageW = wiretapBeforeImageW;
    }

    public double getWiretapAfterImageX() {
        return wiretapAfterImageX;
    }

    public void setWiretapAfterImageX(double wiretapAfterImageX) {
        this.wiretapAfterImageX = wiretapAfterImageX;
    }

    public double getWiretapAfterImageY() {
        return wiretapAfterImageY;
    }

    public void setWiretapAfterImageY(double wiretapAfterImageY) {
        this.wiretapAfterImageY = wiretapAfterImageY;
    }

    public int getWiretapAfterImageH() {
        return wiretapAfterImageH;
    }

    public void setWiretapAfterImageH(int wiretapAfterImageH) {
        this.wiretapAfterImageH = wiretapAfterImageH;
    }

    public int getWiretapAfterImageW() {
        return wiretapAfterImageW;
    }

    public void setWiretapAfterImageW(int wiretapAfterImageW) {
        this.wiretapAfterImageW = wiretapAfterImageW;
    }

    public String getLogWiretapBeforeStatus() {
        return logWiretapBeforeStatus;
    }

    public void setLogWiretapBeforeStatus(String logWiretapBeforeStatus) {
        this.logWiretapBeforeStatus = logWiretapBeforeStatus;
    }

    public String getLogWiretapAfterStatus() {
        return logWiretapAfterStatus;
    }

    public void setLogWiretapAfterStatus(String logWiretapAfterStatus) {
        this.logWiretapAfterStatus = logWiretapAfterStatus;
    }

    public double getLogWiretapBeforeImageX() {
        return logWiretapBeforeImageX;
    }

    public void setLogWiretapBeforeImageX(double logWiretapBeforeImageX) {
        this.logWiretapBeforeImageX = logWiretapBeforeImageX;
    }

    public double getLogWiretapBeforeImageY() {
        return logWiretapBeforeImageY;
    }

    public void setLogWiretapBeforeImageY(double logWiretapBeforeImageY) {
        this.logWiretapBeforeImageY = logWiretapBeforeImageY;
    }

    public int getLogWiretapBeforeImageH() {
        return logWiretapBeforeImageH;
    }

    public void setLogWiretapBeforeImageH(int logWiretapBeforeImageH) {
        this.logWiretapBeforeImageH = logWiretapBeforeImageH;
    }

    public int getLogWiretapBeforeImageW() {
        return logWiretapBeforeImageW;
    }

    public void setLogWiretapBeforeImageW(int logWiretapBeforeImageW) {
        this.logWiretapBeforeImageW = logWiretapBeforeImageW;
    }

    public double getLogWiretapAfterImageX() {
        return logWiretapAfterImageX;
    }

    public void setLogWiretapAfterImageX(double logWiretapAfterImageX) {
        this.logWiretapAfterImageX = logWiretapAfterImageX;
    }

    public double getLogWiretapAfterImageY() {
        return logWiretapAfterImageY;
    }

    public void setLogWiretapAfterImageY(double logWiretapAfterImageY) {
        this.logWiretapAfterImageY = logWiretapAfterImageY;
    }

    public int getLogWiretapAfterImageH() {
        return logWiretapAfterImageH;
    }

    public void setLogWiretapAfterImageH(int logWiretapAfterImageH) {
        this.logWiretapAfterImageH = logWiretapAfterImageH;
    }

    public int getLogWiretapAfterImageW() {
        return logWiretapAfterImageW;
    }

    public void setLogWiretapAfterImageW(int logWiretapAfterImageW) {
        this.logWiretapAfterImageW = logWiretapAfterImageW;
    }

    public boolean wiretapBeforeClickedOn(double x, double y)
    {
        if(this.getWiretapBeforeStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + this.wiretapBeforeImageX
                && x <= super.getX() + this.wiretapBeforeImageX + this.wiretapBeforeImageW
                && y >= super.getY() + this.wiretapBeforeImageY
                && y <= super.getY() + this.wiretapBeforeImageY + this.wiretapBeforeImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean wiretapAfterClickedOn(double x, double y)
    {
        if(this.getWiretapAfterStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + this.wiretapAfterImageX
                && x <= super.getX() + this.wiretapAfterImageX + this.wiretapAfterImageW
                && y >= super.getY() + this.wiretapAfterImageY
                && y <= super.getY() + this.wiretapAfterImageY + this.wiretapAfterImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean logWiretapBeforeClickedOn(double x, double y)
    {
        if(this.getLogWiretapBeforeStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + this.getLogWiretapBeforeImageX()
                && x <= super.getX() + this.getLogWiretapBeforeImageX() + this.logWiretapBeforeImageW
                && y >= super.getY() + this.getLogWiretapBeforeImageY()
                && y <= super.getY() + this.getLogWiretapBeforeImageY() + this.logWiretapBeforeImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean logWiretapAfterClickedOn(double x, double y)
    {
        if(this.getLogWiretapAfterStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + this.getLogWiretapAfterImageX()
                && x <= super.getX() + this.getLogWiretapAfterImageX() + this.logWiretapAfterImageW
                && y >= super.getY() + this.getLogWiretapAfterImageY()
                && y <= super.getY() + this.getLogWiretapAfterImageY() + this.logWiretapAfterImageH)
            {
                return true;
            }
        }

        return false;
    }

    public List<DecoratorMetaData> getDecoratorMetaDataList() {
        return decoratorMetaDataList;
    }

    public void setDecoratorMetaDataList(List<DecoratorMetaData> decoratorMetaDataList) {
        this.decoratorMetaDataList = decoratorMetaDataList;
    }
}
