package com.marte.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.marte.gdx.MissaoMarteGame;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Missao Marte UNIFOR - libGDX");
        config.setWindowedMode(960, 640);
        config.useVsync(true);
        new Lwjgl3Application(new MissaoMarteGame(), config);
    }
}
