package com.wp.android_onvif;

import com.wp.android_onvif.util.FileUtils;

import java.io.File;

public class Constant {
    public static final String ROOT_PATH = FileUtils.getSDCardFile().getAbsolutePath() + File.separator + "onvif" + File.separator; // 根路径

}
