package org.ikasan.designer.pallet;

public class DesignerPalletRectangleItem extends DesignerPalletItem {

    /**
     * Constructor
     *
     * @param src
     * @param canvasAddAction
     * @param width
     * @param height
     */
    public DesignerPalletRectangleItem(String src, CanvasAddAction canvasAddAction, int width, int height) {
        super(src, DesignerPalletItemType.RECTANGLE, canvasAddAction, width, height);
    }

}
