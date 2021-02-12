package org.ikasan.designer.event;

import org.ikasan.designer.model.Figure;
import org.ikasan.designer.pallet.DesignerPalletItem;

public class CanvasItemDoubleClickEvent extends CanvasItemEvent {

    /**
     * Constructor
     *
     * @param designerPalletItem
     * @param clickLocationX
     * @param clickLocationY
     * @param figure
     */
    public CanvasItemDoubleClickEvent(DesignerPalletItem designerPalletItem, int clickLocationX,
                                      int clickLocationY, Figure figure) {
        super(designerPalletItem, clickLocationX, clickLocationY, figure);
    }
}
