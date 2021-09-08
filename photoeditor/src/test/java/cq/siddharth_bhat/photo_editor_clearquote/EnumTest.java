package cq.siddharth_bhat.photo_editor_clearquote;

import org.junit.Test;

import cq.siddharth_bhat.photo_editor_clearquote.shape.ShapeType;

import static org.junit.Assert.assertEquals;

public class EnumTest {

    @Test
    public void testNumberOfViewTypes() {
        assertEquals(ViewType.values().length, 4);
    }

    @Test
    public void testNumberOfShapeTypes() {
        assertEquals(ShapeType.values().length, 4);
    }

    @Test
    public void testNumberOfPhotoFilterTypes() {
        assertEquals(PhotoFilter.values().length, 24);
    }

}
