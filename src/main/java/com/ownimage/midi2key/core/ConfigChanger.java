package com.ownimage.midi2key.core;

public interface ConfigChanger extends ConfigSuppier {

    void config(Config config);

    void saveConfig();

    void stopMapping();

    void startMapping();

    void openConfig();
}
