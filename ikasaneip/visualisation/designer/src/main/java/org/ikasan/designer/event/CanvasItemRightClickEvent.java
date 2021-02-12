package org.ikasan.designer.event;

import org.ikasan.designer.model.Figure;
import org.ikasan.designer.pallet.DesignerPalletItem;

public class CanvasItemRightClickEvent extends CanvasItemEvent {

    /**
     * Constructor
     *
     * @param designerPalletItem
     * @param clickLocationX
     * @param clickLocationY
     * @param figure
     */
    public CanvasItemRightClickEvent(DesignerPalletItem designerPalletItem, int clickLocationX,
                                     int clickLocationY, Figure figure) {
        super(designerPalletItem, clickLocationX, clickLocationY, figure);
    }
}
