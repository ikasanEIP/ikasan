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
    private boolean hasWiretapBefore = false;
    private boolean hasWiretapAfter = false;
    private boolean hasLogWiretapBefore = false;
    private boolean hasLogWiretapAfter = false;

    private List<DecoratorMetaData> decoratorMetaDataList;

    public AbstractWiretapNode(String id,  String name, String image) {
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(image));
        super.setWiretapAfterImage(WIRETAP_IMAGE);
        super.setWiretapBeforeImage(WIRETAP_IMAGE);
        super.setLogWiretapAfterImage(LOG_WIRETAP_IMAGE);
        super.setLogWiretapBeforeImage(LOG_WIRETAP_IMAGE);
    }

    public boolean hasWiretapBefore() {
        return hasWiretapBefore;
    }

    public void setHasWiretapBefore(boolean hasWiretapBefore) {
        this.hasWiretapBefore = hasWiretapBefore;
        if(this.hasWiretapBefore) {
            super.setWiretapBeforeStatus(NodeFoundStatus.FOUND);
        }
        else if(this.hasWiretapBefore) {
            super.setWiretapBeforeStatus(NodeFoundStatus.NOT_FOUND);
        }
    }

    public boolean hasWiretapAfter() {
        return hasWiretapAfter;
    }

    public void setHasWiretapAfter(boolean hasWiretapAfter) {
        this.hasWiretapAfter = hasWiretapAfter;

        if(this.hasWiretapAfter) {
            super.setWiretapAfterStatus(NodeFoundStatus.FOUND);
        }
        else if(this.hasWiretapAfter) {
            super.setWiretapAfterStatus(NodeFoundStatus.NOT_FOUND);
        }
    }

    public boolean hasLogWiretapBefore() {
        return hasLogWiretapBefore;
    }

    public void setHasLogWiretapBefore(boolean hasLogWiretapBefore) {
        this.hasLogWiretapBefore = hasLogWiretapBefore;

        if(this.hasLogWiretapBefore) {
            super.setLogWiretapBeforeStatus(NodeFoundStatus.FOUND);
        }
        else if(this.hasLogWiretapBefore) {
            super.setLogWiretapBeforeStatus(NodeFoundStatus.NOT_FOUND);
        }
    }

    public boolean hasLogWiretapAfter() {
        return hasLogWiretapAfter;
    }

    public void setHasLogWiretapAfter(boolean hasLogWiretapAfter) {
        this.hasLogWiretapAfter = hasLogWiretapAfter;

        if(this.hasLogWiretapAfter) {
            super.setLogWiretapAfterStatus(NodeFoundStatus.FOUND);
        }
        else if(this.hasLogWiretapAfter) {
            super.setLogWiretapAfterStatus(NodeFoundStatus.NOT_FOUND);
        }
    }

    public boolean wiretapBeforeClickedOn(double x, double y)
    {
        if(super.getWiretapBeforeStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + super.wiretapBeforeImageX
                && x <= super.getX() + super.wiretapBeforeImageX + super.wiretapBeforeImageW
                && y >= super.getY() + super.wiretapBeforeImageY
                && y <= super.getY() + super.wiretapBeforeImageY + super.wiretapBeforeImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean wiretapAfterClickedOn(double x, double y)
    {
        if(super.getWiretapAfterStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + super.wiretapAfterImageX
                && x <= super.getX() + super.wiretapAfterImageX + super.wiretapAfterImageW
                && y >= super.getY() + super.wiretapAfterImageY
                && y <= super.getY() + super.wiretapAfterImageY + super.wiretapAfterImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean logWiretapBeforeClickedOn(double x, double y)
    {
        if(super.getLogWiretapBeforeStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + super.getLogWiretapBeforeImageX()
                && x <= super.getX() + super.getLogWiretapBeforeImageX() + super.logWiretapBeforeImageW
                && y >= super.getY() + super.getLogWiretapBeforeImageY()
                && y <= super.getY() + super.getLogWiretapBeforeImageY() + super.logWiretapBeforeImageH)
            {
                return true;
            }
        }

        return false;
    }

    public boolean logWiretapAfterClickedOn(double x, double y)
    {
        if(super.getLogWiretapAfterStatus().equals(NodeFoundStatus.FOUND))
        {
            if(x >= super.getX() + super.getLogWiretapAfterImageX()
                && x <= super.getX() + super.getLogWiretapAfterImageX() + super.logWiretapAfterImageW
                && y >= super.getY() + super.getLogWiretapAfterImageY()
                && y <= super.getY() + super.getLogWiretapAfterImageY() + super.logWiretapAfterImageH)
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
