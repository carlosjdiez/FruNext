package com.svalero.frunext.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.svalero.frunext.FruNext;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("FruNext");
        config.setWindowedMode(1280, 720);   // ventana grande
        config.useVsync(true);
        config.setForegroundFPS(60);
        return new Lwjgl3Application(new FruNext(), config);
    }
}
