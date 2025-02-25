package com.palmabrahma.slidetraylitho;

import android.app.Application;
import com.facebook.soloader.SoLoader;

/**
 * Application class for initializing Litho's SoLoader.
 * This is required for Litho to function properly.
 */
public class EmojiDrawerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize SoLoader for Litho
        SoLoader.init(this, false);
    }
}