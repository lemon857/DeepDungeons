package com.deepdungeons.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.deepdungeons.game.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        double width_to_height_koef = 9.0 / 16.0;
        return new Lwjgl3Application(new Main(width_to_height_koef), getDefaultConfiguration(width_to_height_koef));
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(double width_to_height_koef) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("DeepDungeons");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

        int width = 1400;
        configuration.setWindowedMode(width, (int)(width_to_height_koef * width));
        // configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}