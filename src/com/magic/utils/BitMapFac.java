package com.magic.utils;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: haribo
 * Date: 10.05.13
 * Time: 17:55
 */
public class BitMapFac {

    private Set<Bitmap> hackedBitmaps = new HashSet<>();
    private VMRuntimeHack runtime = new VMRuntimeHack();

    // создать картинку
    public Bitmap alloc(int dx, int dy) {
        Bitmap bmp = Bitmap.createBitmap(dx, dy, Bitmap.Config.RGB_565);
        runtime.trackFree(bmp.getRowBytes() * bmp.getHeight());
        hackedBitmaps.add(bmp);
        return bmp;
    }

    // освободить картинку
    public void free(Bitmap bmp) {
        bmp.recycle();
        runtime.trackAlloc(bmp.getRowBytes() * bmp.getHeight());
        hackedBitmaps.remove(bmp);
    }

    // освоболить все картинки (удобно для тестирования)
    public void freeAll() {
        for (Bitmap bmp : new LinkedList<>(hackedBitmaps)) {
            free(bmp);
        }
    }
}