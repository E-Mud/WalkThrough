package org.emud.walkthrough.analysisservice;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.emud.walkthrough.ActivitiesDataSource;
import org.emud.walkthrough.WalkThroughApplication;
import org.emud.walkthrough.analysis.AnalysisStation;
import org.emud.walkthrough.analysis.StationBuilder;
import org.emud.walkthrough.analysis.WalkDataReceiver;
import org.emud.walkthrough.analysisservice.ScreenBroadcastReceiver.ScreenOnOffListener;
import org.emud.walkthrough.model.Result;
import org.emud.walkthrough.model.User;
import org.emud.walkthrough.model.WalkActivity;
import org.emud.walkthrough.monitor.Monitor;
import org.emud.walkthrough.sensortag.DeviceScanner;
import org.emud.walkthrough.sensortag.SensorTagConnector;
import org.emud.walkthrough.sensortag.SensorTagDataReceiver;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

@SuppressLint("NewApi")
public class AnalysisService extends Service implements ScreenOnOffListener{
	private static final long SCANNING_TIME = 10000;
	public static final String RECEIVER_TYPE_KEY = "receiverType", RESULTS_TYPES_KEY = "resultsTypes", SCREEN_KEY = "screenPref";
	public static final String LIST_SIZE_KEY = "listSize",  LIST_ITEM_KEY = "resultBundle_";
	public static final String BUNDLE_KEY = "bundle";
	public static final String SUCCESS_KEY = "success",
			ACTIVITY_ID_KEY = "activityId",
			N_RECEIVER_KEY = "nReceiver",
			CONNECTED_KEY = "connected";
	
	public static final int
		SERVICE_UNSTARTED = -1,
		SERVICE_PREPARED = 0,
		SERVICE_RUNNING = 1,
		SERVICE_PAUSED = 2,
		SERVICE_STOPPED = 3,
		SERVICE_CONNECTING = 4;
	
	public static final int SINGLE_ACCELEROMETER = 0, TWO_ACCELEROMETERS = 1;
	
	private int currentState;
	private AnalysisStation station;
	private IBinder binder;
	private Handler handler;
	private boolean receiverRegistered;
	private ScreenBroadcastReceiver receiver;
	private int receiverType;
	private HashSet<Integer> setResultsTypes;
	
	private DeviceScanner scanner;
	private Monitor monitor;
	
	@Override
	public void onCreate(){
		super.onCreate();
		currentState = SERVICE_UNSTARTED;
		
		handler = new Handler();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(currentState == SERVICE_UNSTARTED){
			receiverType = intent.getIntExtra(RECEIVER_TYPE_KEY, -2);
			int[] resultsTypes = intent.getIntArrayExtra(RESULTS_TYPES_KEY);
			setResultsTypes = new HashSet<Integer>();
			int n = resultsTypes.length;

			for(int i=0; i<n; i++)
				setResultsTypes.add(Integer.valueOf(resultsTypes[i]));

			if(receiverType == SINGLE_ACCELEROMETER){
				WalkDataReceiver receiver = new LinearAccelerometerReceiver(this);
				initializeStation(receiver);
			}else{
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
					BluetoothManager bluetoothManager =
							(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
					BluetoothAdapter adapter = bluetoothManager.getAdapter();
					SensorTagConnector manager = new SensorTagConnector(this);
					scanner = new DeviceScanner(adapter, manager);
				}
			}

			binder = new LocalBinder();

			if(intent.getBooleanExtra(SCREEN_KEY, false)){
				receiver = new ScreenBroadcastReceiver();
				receiver.setScreenListener(this);
				registerReceiver(receiver, ScreenBroadcastReceiver.getIntentFilter());
				receiverRegistered = true;
			}else{
				receiverRegistered = false;
			}
		}
		
		return START_STICKY;
	}
	

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    private void initializeStation(WalkDataReceiver receiver){
    	StationBuilder stationBuilder = getStationBuilder();
    	station = stationBuilder.buildStation(receiver, receiverType, setResultsTypes);

    	currentState = SERVICE_PREPARED;
    }
    
    protected StationBuilder getStationBuilder(){
    	User user = ((WalkThroughApplication) getApplicationContext()).getUserDataSource().getProfile();
    	return new WAnalysisStationBuilder(receiverType, user.getLegLength());
    }
    
    public void connectSensor(){
    	if(currentState == SERVICE_UNSTARTED){
    		currentState = SERVICE_CONNECTING;
			android.util.Log.i("AS", "connectSensor");
    		scanner.startScan();

    		handler.postDelayed(new Runnable(){
    			@Override
    			public void run() {
    				AnalysisService.this.stopScanning();
    			}
    		}, SCANNING_TIME);
    	}
    }
    
    public void stopConnecting(){
    	if(currentState == SERVICE_CONNECTING){
			android.util.Log.i("AS", "stopConnecting");
    		currentState = SERVICE_UNSTARTED;
    		scanner.stopScan();
    	}
    }
    
    private void stopScanning(){
    	stopConnecting();
    	
    	if(currentState == SERVICE_UNSTARTED){
			android.util.Log.i("AS", "stopScanning");
    		Intent intent = new Intent();
    		intent.setAction(UpdateBroadcastReceiver.ACTION_CONNECTING_RESULT);

    		intent.putExtra(CONNECTED_KEY, false);

    		sendBroadcast(intent);
    	}
    }

	public int startAnalysis() {
		if(currentState == SERVICE_PREPARED){
			station.startAnalysis();
			currentState = SERVICE_RUNNING;
		}
		
		return currentState;
	}

	public int pauseAnalysis() {
		if(currentState == SERVICE_RUNNING){
			station.pauseAnalysis();
			currentState = SERVICE_PAUSED;
		}
		
		return currentState;
	}

	public int resumeAnalysis() {
		if(currentState == SERVICE_PAUSED){
			station.resumeAnalysis();
			currentState = SERVICE_RUNNING;
		}
		
		return currentState;
	}

	public int stopAnalysis() {
		if(currentState == SERVICE_RUNNING || currentState == SERVICE_PAUSED){
			stopStation();
			currentState = SERVICE_STOPPED;
			
			long id = insertResults(station.collectResults());
			broadcastResultInserted(id);
		}
		
		return currentState;
	}
	
	private void stopStation() {
		if(receiverRegistered){
			unregisterReceiver(receiver);
			receiverRegistered = false;
		}
		station.stopAnalysis();
	}

	private void broadcastResultInserted(long activityId) {
		Intent intent = new Intent(UpdateBroadcastReceiver.ACTION_RESULT_INSERTED);
		
		intent.putExtra(SUCCESS_KEY, activityId >= 0);
		intent.putExtra(ACTIVITY_ID_KEY, activityId);
		
		sendBroadcast(intent);
	}

	private long insertResults(List<Result> results) {
		WalkActivity activity;
		ActivitiesDataSource dataSource = ((WalkThroughApplication) getApplicationContext()).getActivitiesDataSource();
		
		activity = new WalkActivity((GregorianCalendar) GregorianCalendar.getInstance(), results);
        return dataSource.createNewActivity(activity);
	}

	public int getState() {
		return currentState;	
	}

	@Override
	public void onScreenOn() {
		if(currentState == SERVICE_RUNNING){
			station.pauseAnalysis();
			currentState = SERVICE_PAUSED;
		}
	}

	@Override
	public void onScreenOff() {
		if(currentState == SERVICE_PREPARED){
			station.startAnalysis();
			currentState = SERVICE_RUNNING;
		}else{
			if(currentState == SERVICE_PAUSED){
				station.resumeAnalysis();
				currentState = SERVICE_RUNNING;
			}
		}
	}

	public void receiverBuilt(SensorTagDataReceiver receiver) {
		stopScanning();

		monitor = new Monitor(this);
		receiver.addOnDataReceivedListener(monitor);
		
		android.util.Log.i("AS", "receiverBuilt");
		initializeStation(receiver);
		Intent intent = new Intent(UpdateBroadcastReceiver.ACTION_CONNECTING_RESULT);
		
		intent.putExtra(CONNECTED_KEY, true);
		intent.putExtra(N_RECEIVER_KEY, 1);
		
		sendBroadcast(intent);
	}
	
	public class LocalBinder extends Binder {
        public AnalysisService getService() {
            return AnalysisService.this;
        }
    }

	public void sensorDisconnected() {
		stopStation();
		currentState = SERVICE_STOPPED;
		Intent intent = new Intent(UpdateBroadcastReceiver.ACTION_RECEIVER_DISCONNECTED);
		
		sendBroadcast(intent);
	}
}
