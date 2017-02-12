package com.horical.appnote.TouchPaint;

import android.graphics.Color;

/**
 * Created by Phuong on 17/07/2015.
 */
class ColorUtil {
    private int RedChannel, GreenChannel, BlueChannel;

    ColorUtil(){
        RedChannel = GreenChannel = BlueChannel = 0;
    }
    public ColorUtil(int r, int g, int b){
        RedChannel = (r >= 0 && r < 256)?r:0;
        GreenChannel = (g >= 0 && g < 256)?g:0;
        BlueChannel = (b >= 0 && b < 256)?b:0;
    }

    public int getBlueChannel() {
        return BlueChannel;
    }

    public int getGreenChannel() {
        return GreenChannel;
    }

    public int getRedChannel() {
        return RedChannel;
    }

    private void setBlueChannel(int blueChannel) {
        BlueChannel = blueChannel;
    }

    private void setGreenChannel(int greenChannel) {
        GreenChannel = greenChannel;
    }

    private void setRedChannel(int redChannel) {
        RedChannel = redChannel;
    }

    void setRGB(int redChannel, int greenChannel, int blueChannel){
        this.setRedChannel(redChannel);
        this.setGreenChannel(greenChannel);
        this.setBlueChannel(blueChannel);
    }

    int getRGB(){
        return Color.rgb(RedChannel, GreenChannel, BlueChannel);
    }

    @Override
    public String toString() {
        return "RGB("+RedChannel+", "+GreenChannel+", "+BlueChannel+")";
    }

    int changeColorByChannel(int value, int channel){
        switch (channel){
            case Color.RED:
                this.setRedChannel(value);
                break;
            case Color.GREEN:
                this.setGreenChannel(value);
                break;
            case Color.BLUE:
                this.setBlueChannel(value);
                break;
        }
        return this.getRGB();
    }
}
