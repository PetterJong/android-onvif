package com.wp.android_onvif.onvifBean;

public class ImageSetting {
    private float brightness;
    private float colorSaturation;
    private float contrast;
    private Exposure exposure;

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getColorSaturation() {
        return colorSaturation;
    }

    public void setColorSaturation(float colorSaturation) {
        this.colorSaturation = colorSaturation;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public Exposure getExposure() {
        return exposure;
    }

    public void setExposure(Exposure exposure) {
        this.exposure = exposure;
    }

    public static class Exposure{
        private String mode;
        private int MinExposureTime;
        private int MaxExposureTime;
        private float ExposureTime;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public int getMinExposureTime() {
            return MinExposureTime;
        }

        public void setMinExposureTime(int minExposureTime) {
            MinExposureTime = minExposureTime;
        }

        public int getMaxExposureTime() {
            return MaxExposureTime;
        }

        public void setMaxExposureTime(int maxExposureTime) {
            MaxExposureTime = maxExposureTime;
        }

        public float getExposureTime() {
            return ExposureTime;
        }

        public void setExposureTime(float exposureTime) {
            ExposureTime = exposureTime;
        }

        @Override
        public String toString() {
            return "Exposure{" +
                    "mode=" + mode +
                    ", MinExposureTime=" + MinExposureTime +
                    ", MaxExposureTime=" + MaxExposureTime +
                    ", ExposureTime=" + ExposureTime +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ImageSetting{" +
                "brightness=" + brightness +
                ", colorSaturation=" + colorSaturation +
                ", contrast=" + contrast +
                ", exposure=" + exposure +
                '}';
    }
}
