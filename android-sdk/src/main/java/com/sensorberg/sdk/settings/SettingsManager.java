package com.sensorberg.sdk.settings;

import com.sensorberg.sdk.Logger;
import com.sensorberg.sdk.internal.interfaces.BeaconHistoryUploadIntervalListener;
import com.sensorberg.sdk.internal.interfaces.MessageDelayWindowLengthListener;
import com.sensorberg.sdk.internal.interfaces.Transport;
import com.sensorberg.sdk.internal.transport.TransportSettingsCallback;

import org.json.JSONObject;

import android.content.SharedPreferences;

import lombok.Setter;

public class SettingsManager {

    private final Transport transport;

    private final SharedPreferences preferences;

    @Setter
    private SettingsUpdateCallback settingsUpdateCallback = SettingsUpdateCallback.NONE;

    @Setter
    private MessageDelayWindowLengthListener messageDelayWindowLengthListener = MessageDelayWindowLengthListener.NONE;

    private OldSettings settings;

    public SettingsManager(Transport trans, SharedPreferences prefs) {
        transport = trans;
        preferences = prefs;
        transport.setBeaconHistoryUploadIntervalListener(mBeaconHistoryUploadIntervalListener);
        updateSettings(new OldSettings(prefs));
    }

    private void updateSettings(OldSettings stgs) {
        settings = stgs;
        messageDelayWindowLengthListener.setMessageDelayWindowLength(getMessageDelayWindowLength());

        settings.persistToPreferences(preferences);
    }

    public void updateSettingsFromNetwork() {
        transport.loadSettings(transportSettingsCallback);
    }

    TransportSettingsCallback transportSettingsCallback = new TransportSettingsCallback() {
        @Override
        public void nothingChanged() {
            //all is good nothing to do
            Logger.log.logSettingsUpdateState("nothingChanged");
        }

        @Override
        public void onFailure(Exception e) {
            Logger.log.logSettingsUpdateState("onFailure");
            Logger.log.logError("settings update failed", e);
        }

        @Override
        public void onSettingsFound(JSONObject jsonSettings) {
            OldSettings newSettings;

            if (jsonSettings == null) {
                newSettings = new OldSettings();
                preferences.edit().clear().apply();
            } else {
                newSettings = new OldSettings(jsonSettings, settingsUpdateCallback);
            }

            updateSettings(newSettings);
        }
    };

    BeaconHistoryUploadIntervalListener mBeaconHistoryUploadIntervalListener = new BeaconHistoryUploadIntervalListener() {
        @Override
        public void historyUploadIntervalChanged(Long newHistoryUploadIntervalMillis) {
            if (newHistoryUploadIntervalMillis != settings.getHistoryUploadInterval()) {
                settings.setHistoryUploadInterval(newHistoryUploadIntervalMillis);
                settingsUpdateCallback.onHistoryUploadIntervalChange(newHistoryUploadIntervalMillis);
                settings.persistToPreferences(preferences);
            }
        }
    };

    private OldSettings getSettings() {
        return settings;
    }

    public long getExitTimeoutMillis() {
        return getSettings().getExitTimeoutMillis();
    }

    public long getCleanBeaconMapRestartTimeout() {
        return getSettings().getCleanBeaconMapRestartTimeout();
    }

    public long getForeGroundWaitTime() {
        return getSettings().getForeGroundWaitTime();
    }

    public long getForeGroundScanTime() {
        return getSettings().getForeGroundScanTime();
    }

    public long getBackgroundWaitTime() {
        return getSettings().getBackgroundWaitTime();
    }

    public long getBackgroundScanTime() {
        return getSettings().getBackgroundScanTime();
    }

    public long getCacheTtl(){
        return getSettings().getCacheTtl();
    }

    public boolean isShouldRestoreBeaconStates() {
        return getSettings().isShouldRestoreBeaconStates();
    }

    public int getMaxRetries() {
        return getSettings().getMaxRetries();
    }

    public long getMillisBetweenRetries() {
        return getSettings().getMillisBetweenRetries();
    }

    public long getLayoutUpdateInterval() {
        return getSettings().getLayoutUpdateInterval();
    }

    public long getSettingsUpdateInterval() {
        return getSettings().getSettingsUpdateInterval();
    }

    public long getHistoryUploadInterval() {
        return getSettings().getHistoryUploadInterval();
    }

    public long getMessageDelayWindowLength() {
        return getSettings().getMessageDelayWindowLength();
    }
}
