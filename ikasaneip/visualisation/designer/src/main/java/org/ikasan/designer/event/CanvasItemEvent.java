package org.ikasan.designer.event;

import org.ikasan.designer.model.Figure;
import org.ikasan.designer.pallet.DesignerPalletItem;

public abstract class CanvasItemEvent {
    private DesignerPalletItem designerPalletItem;
    private int clickLocationX;
    private int clickLocationY;
    private Figure figure;

    protected CanvasItemEvent(DesignerPalletItem designerPalletItem, int clickLocationX,
                              int clickLocationY, Figure figure) {
        this.designerPalletItem = designerPalletItem;
        this.clickLocationX = clickLocationX;
        this.clickLocationY = clickLocationY;
        this.figure = figure;
    }

    public DesignerPalletItem getItem() {
        return designerPalletItem;
    }

    public int getClickLocationX() {
        return clickLocationX;
    }

    public int getClickLocationY() {
        return clickLocationY;
    }

    public Figure getFigure() {
        return figure;
    }
}
