package org.ikasan.designer.pallet;

public class DesignerPalletIconItem extends DesignerPalletItem {

    /**
     * Constructor
     *
     * @param src
     * @param canvasAddAction
     * @param width
     * @param height
     */
    public DesignerPalletIconItem(String src, CanvasAddAction canvasAddAction, int width, int height) {
        super(src, DesignerPalletItemType.ICON, canvasAddAction, width, height);
    }

}
