package com.khmelenko.lab.miband;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

public class MiBandManager {
    private static final String TAG = "MiBandManager";

    private Activity activity;
    private MiBand miBand;
    private Handler handler;

    public MiBandManager(Activity activity) {
        this.handler = new Handler ();
        this.activity = activity;
        this.miBand = new MiBand(activity.getApplicationContext());
    }

    public void Connect (String macAddress, IMiBandManagerStateHandler handler)
    {
        Log.d(TAG, "Connect " + macAddress + " thread: " + Thread.currentThread());

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(macAddress);

        Log.d(TAG, "Device: " + device);

        miBand.connect(device)
                .subscribe(result -> {
                    if (!result)
                        return;

                    this.handler.post (() -> {
                        handler.OnConnected();
                    });
                }, throwable -> {
                    Log.e(TAG, "failed", throwable);
                    this.handler.post(() -> handler.OnConnectionFailed());
                });
    }

    public void SetHeartRateListener (IHeartrateListener heartrateListener)
    {
        Log.d(TAG, "SetHeartRateListener thread: " + Thread.currentThread());

        miBand.setHeartRateScanListenerMiBand2(heartRate -> {
            Log.d(TAG, "hearrate " + heartRate);
            handler.post(() -> {
                Log.d(TAG, "OnHeartRate " + Thread.currentThread());
                heartrateListener.OnHeartRate(heartRate);
            });
        });
    }

    public void StartHeartRateScan (IHeartRateScanStartHandler handler)
    {
        Log.d(TAG, "StartHeartRateScan " + Thread.currentThread());

        miBand
                .startHeartRateScan()
                .subscribe(result -> {
                    Log.d(TAG, "result " + result);
                    this.handler.post(() -> handler.OnSuccess());
                }, throwable -> {
                    Log.e(TAG, "Heartrate scan failed", throwable);
                    this.handler.post(() -> handler.OnFailed());
                });
    }
}
