package com.khmelenko.lab.miband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class MiBandManager {
    private static final String TAG = "MiBandManager";

    private Activity activity;
    private MiBand miBand;

    public MiBandManager(Activity activity) {
        this.activity = activity;
        this.miBand = new MiBand(activity.getApplicationContext());
    }

    public void ListenHeartRate (String macAddress, IHeartrateListener heartrateListener)
    {
        Log.d(TAG, "ListenHeartRate " + macAddress);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(macAddress);

        Log.d(TAG, "Device: " + device);

        miBand.connect(device)
                .subscribe(result -> {
                    Log.d(TAG, "ok");
                    miBand.setHeartRateScanListener(heartRate -> {
                        Log.d(TAG, "hearrate " + heartRate);
                        activity.runOnUiThread(() -> {
                            heartrateListener.OnHeartRate(heartRate);
                        });
                    });
                }, throwable -> {
                    Log.d(TAG, "failed");
                });
    }

    public void StartHeartRateScan ()
    {
        Log.d(TAG, "startHeartRateScan");
        miBand.startHeartRateScan()
                .subscribe(res -> Log.d(TAG, "startHeartRateScan result " + res));
    }
}
