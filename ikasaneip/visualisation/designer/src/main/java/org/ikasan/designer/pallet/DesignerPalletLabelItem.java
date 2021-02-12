package org.ikasan.designer.pallet;

public class DesignerPalletLabelItem extends DesignerPalletItem {

    /**
     * Constructor
     *
     * @param src
     * @param canvasAddAction
     * @param width
     * @param height
     */
    public DesignerPalletLabelItem(String src, CanvasAddAction canvasAddAction, int width, int height) {
        super(src, DesignerPalletItemType.LABEL, canvasAddAction, width, height);
    }

}
