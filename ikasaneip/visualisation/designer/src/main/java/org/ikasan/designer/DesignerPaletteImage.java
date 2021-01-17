package org.ikasan.designer;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.AbstractStreamResource;

public class DesignerPaletteImage extends Image {
    private DesignerPaletteImageType designerPaletteImageType;

    public DesignerPaletteImage(DesignerPaletteImageType designerPaletteImageType) {
        super();

        this.designerPaletteImageType = designerPaletteImageType;
    }

    public DesignerPaletteImage(DesignerPaletteImageType designerPaletteImageType, String src, String alt) {
        super(src, alt);

        this.designerPaletteImageType = designerPaletteImageType;
    }

    public DesignerPaletteImage(DesignerPaletteImageType designerPaletteImageType, AbstractStreamResource src, String alt) {
        super(src, alt);

        this.designerPaletteImageType = designerPaletteImageType;
    }

    public DesignerPaletteImageType getDesignerPaletteImageType() {
        return designerPaletteImageType;
    }
}
