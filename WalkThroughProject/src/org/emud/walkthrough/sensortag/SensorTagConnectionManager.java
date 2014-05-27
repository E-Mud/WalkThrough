package org.emud.walkthrough.sensortag;

import org.emud.walkthrough.analysisservice.AnalysisService;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SensorTagConnectionManager implements LeScanCallback, SensorTag.ConnectionListener {
	private AnalysisService service;
	private SensorTag sensorTag;

	public SensorTagConnectionManager(AnalysisService serv){
		service = serv;
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		android.util.Log.i("STCM", "onLeScan " + device.getAddress());
		BluetoothManager bluetoothManager =
				(BluetoothManager) service.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter adapter = bluetoothManager.getAdapter();
		
		sensorTag = new SensorTag(adapter);
		sensorTag.setConnectionListener(this);
		sensorTag.connectToDevice(service, device.getAddress());
	}
	
	@Override
	public void connectionStateChanged(boolean success){
		android.util.Log.i("STCM", "connectionCompleted " + success);
		if(success){
			SensorTagDataReceiver receiver = new SensorTagDataReceiver(sensorTag);
			service.receiverBuilded(receiver);
		}else{
			service.sensorDisconnected();
		}
	}

}