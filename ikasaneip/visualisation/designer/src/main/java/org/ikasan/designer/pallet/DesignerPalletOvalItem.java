package org.ikasan.designer.pallet;

public class DesignerPalletOvalItem extends DesignerPalletItem {

    /**
     * Constructor
     *
     * @param src
     * @param canvasAddAction
     * @param width
     * @param height
     */
    public DesignerPalletOvalItem(String src, CanvasAddAction canvasAddAction, int width, int height) {
        super(src, DesignerPalletItemType.OVAL, canvasAddAction, width, height);
    }

}
