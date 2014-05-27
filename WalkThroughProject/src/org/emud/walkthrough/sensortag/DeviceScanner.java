package org.emud.walkthrough.sensortag;

import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceScanner {
	private volatile boolean scanning;
	private BluetoothAdapter bluetoothAdapter;
	private LeScanCallback scanCallback;
	
	public DeviceScanner(BluetoothAdapter adapter, LeScanCallback callback){
		bluetoothAdapter = adapter;
		scanCallback = callback;
		scanning = false;
	}
	
	public void startScan(){
		if(!scanning){
			scanning = true;
			
			UUID[] uuids = new UUID[1];
			uuids[0] = SensorTag.SERVICE_UUID;
			android.util.Log.i("DS", "startingScanner");
			//bluetoothAdapter.startLeScan(uuids, scanCallback);
			bluetoothAdapter.startLeScan(scanCallback);
		}
	}
	
	public void stopScan(){
		if(scanning){
			scanning = false;
			
			bluetoothAdapter.stopLeScan(scanCallback);
		}
	}
}
