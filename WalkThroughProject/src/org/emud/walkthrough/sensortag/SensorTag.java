package org.emud.walkthrough.sensortag;

import static java.util.UUID.fromString;

import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SensorTag extends BluetoothGattCallback {
	public final static UUID
    		SERVICE_UUID = fromString("f000aa10-0451-4000-b000-000000000000"),
    		DATA_UUID = fromString("f000aa11-0451-4000-b000-000000000000"),
    		CONFIGURATION_UUID = fromString("f000aa12-0451-4000-b000-000000000000"), // 0: disable, 1: enable
    		PERIOD_UUID = fromString("f000aa13-0451-4000-b000-000000000000"),
    		CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	
	private static final byte ENABLE_CODE = 1;
	
	private NotificationListener notificationListener;
	private ConnectionListener connectionListener;
	private volatile boolean busy = false;
	private BluetoothAdapter bluetoothAdapter;
	private String deviceAddress;
	private BluetoothGatt bluetoothGatt;

	private BluetoothGattCharacteristic dataCharacteristic, confCharacteristic, periodCharacteristic;

	public SensorTag(BluetoothAdapter adapter){
		bluetoothAdapter = adapter;
	}
	
	/**
	 * @return the connectionListener
	 */
	public NotificationListener getListener() {
		return notificationListener;
	}

	/**
	 * @param connectionListener the connectionListener to set
	 */
	public void setNotificationListener(NotificationListener listener) {
		this.notificationListener = listener;
	}
	
	/**
	 * @param conenctionListener the conenctionListener to set
	 */
	public void setConnectionListener(ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	private synchronized void setBusy(){
		busy = true;
	}
	
	private synchronized void setIdle(){
		if(busy){
			busy = false;
			notify();
		}
	}
	
	private synchronized void waitIfBusy(){
		if(busy)
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
		if (newState == BluetoothProfile.STATE_CONNECTED) {
            bluetoothGatt.discoverServices();
    		android.util.Log.i("ST", "onConnectionStateChange");
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        	close();
        	if(connectionListener != null)
        		connectionListener.connectionStateChanged(false);
        	setIdle();
        }
	}

	@Override
	public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		BluetoothGattService service = bluetoothGatt.getService(SERVICE_UUID);
		boolean success = service != null;
		android.util.Log.i("ST", "onServicesDiscovered " + success);
		
		if(success){
			dataCharacteristic = service.getCharacteristic(DATA_UUID);
			confCharacteristic = service.getCharacteristic(CONFIGURATION_UUID);
			periodCharacteristic = service.getCharacteristic(PERIOD_UUID);
		}
		
    	if(connectionListener != null)
    		connectionListener.connectionStateChanged(success);
		
		setIdle();
	}

	@Override
	public void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic,
			int status) {
		//TODO
		setIdle();
	}

	@Override
	public void onCharacteristicChanged(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		//TODO
		setIdle();
		if(notificationListener != null)
			notificationListener.onNotificationReceived(characteristic.getValue());
	}

	@Override
	public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
		//TODO
		setIdle();
	}

	@Override
	public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
		//TODO
		setIdle();
	}
	
	public boolean connectToDevice(Context context, String address){
        if(bluetoothAdapter == null || address == null)
            return false;

        if(deviceAddress != null && address.equals(deviceAddress)
                && bluetoothGatt != null)
            return bluetoothGatt.connect();

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null)
            return false;

		android.util.Log.i("ST", "connectToDevice " + address);
        bluetoothGatt = device.connectGatt(context, false, this);
        
        deviceAddress = address;

		waitIfBusy();
        setBusy();
        return true;
	}
	
	public void disconnect(){
		if(bluetoothGatt != null)
			bluetoothGatt.disconnect();
	}
	
	public void close(){
		if(bluetoothGatt != null){
			waitIfBusy();
			bluetoothGatt.close();
			bluetoothGatt = null;
		}
	}
	
	public boolean setPeriod(int period) {
		if (bluetoothAdapter == null || bluetoothGatt == null || periodCharacteristic == null)
			return false;
		
		byte[] val = new byte[1];
		val[0] = (byte) (period/10);
		
		periodCharacteristic.setValue(val);

		waitIfBusy();
		setBusy();
		return bluetoothGatt.writeCharacteristic(periodCharacteristic);
	}

	public boolean enableSensor(){		
		if (bluetoothAdapter == null || bluetoothGatt == null || confCharacteristic == null)
			return false;
		
		byte[] val = new byte[1];
		val[0] = ENABLE_CODE;
		
		confCharacteristic.setValue(val);

		waitIfBusy();
		setBusy();
		return bluetoothGatt.writeCharacteristic(confCharacteristic);
	}
	
	public boolean setNotificationsEnabled(boolean enabled){
		if (bluetoothAdapter == null || bluetoothGatt == null || dataCharacteristic == null)
			return false;

		bluetoothGatt.setCharacteristicNotification(dataCharacteristic, enabled);

		BluetoothGattDescriptor descriptor = dataCharacteristic.getDescriptor(
				CLIENT_CHARACTERISTIC_CONFIG_UUID);
		
		if (enabled) {
			descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		} else {
			descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
		}

		waitIfBusy();
		setBusy();
		return bluetoothGatt.writeDescriptor(descriptor);
	}
	
	public static interface NotificationListener{
		public void onNotificationReceived(byte[] values);
	}
	
	public static interface ConnectionListener{
		public void connectionStateChanged(boolean success);
	}
}
