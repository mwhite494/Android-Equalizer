package com.visualizerdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnTouchListener{	
		
	private static Context context;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private OutputStream outStream;
	private InputStream inStream;
	private Thread thread;
	private byte[] readBuffer;
	private int readBufferPosition;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private static String address = "98:D3:31:B3:0E:70";
	private boolean deviceConnected = false, stopThread = true;
	private static volatile float[] spectrum = new float[6];
	private static String spectrumStr = "0 0 0 0 0 0";
	
	TextView testView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		for (int i = 0; i < spectrum.length; i++) {
			spectrum[i] = 0f;
		}
		checkBluetoothStatus();
		Button connectBtn = (Button) findViewById(R.id.connectBtn);
		connectBtn.setOnTouchListener(this);
		Button disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
		disconnectBtn.setOnTouchListener(this);
		testView = (TextView) findViewById(R.id.textView1);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch(v.getId()) {
			case R.id.connectBtn:
				connect();
				break;
			case R.id.disconnectBtn:
				disconnect();
				break;
			}
		}
		return false;
	}
	
	public static Context getContext() {
		return context;
	}
	
	private void checkBluetoothStatus() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && !adapter.isEnabled()) {
			Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
		}
	}
	
	public static float[] getSpectrum() {
		StringTokenizer tokenizer = new StringTokenizer(spectrumStr);
		for (int j = 0; j < spectrum.length; j++) {
			spectrum[j] = Float.parseFloat(tokenizer.nextToken()) - 0f;
		}
		return spectrum.clone();
	}
	
	public void connect() {
		if (!deviceConnected) {
			if (adapter.isEnabled()) {
				Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
				if (device == null) {
					if (BluetoothAdapter.checkBluetoothAddress(address)) {
						device = adapter.getRemoteDevice(address);
					}
					else {
						spectrum[1] = 250; // WHAT IS THIS?! WHY IS IT NOT VALID?!
					}
				}
				if (pairedDevices.contains(device)) {
					try {
						socket = device.createRfcommSocketToServiceRecord(MY_UUID);
						socket.connect();
						outStream = socket.getOutputStream();
						inStream = socket.getInputStream();
						deviceConnected = true;
					} catch (IOException e) {
						try {
							socket.close();
							deviceConnected = false;
						} catch (IOException e2) { }
					}
					beginListeningForData();
				}
				else {
					adapter.startDiscovery();
				}
			}
			else {
				Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
			}
		}
	}
	
	public void disconnect() {
		if (deviceConnected) {
			try {
				if (adapter != null) {
					adapter.cancelDiscovery();
				}
				outStream.close();
				inStream.close();
				socket.close();
				deviceConnected = false;
			} catch (IOException e) { }
		}
	}
	
	public void write(String data) {
		try {
			outStream = socket.getOutputStream();
		} catch (IOException e) { }
		byte[] buffer = data.getBytes();
		try {
			outStream.write(buffer);
		} catch (IOException e) { }
	}
	
	private void beginListeningForData() {
		final Handler handler = new Handler();
		final byte delimiter = 10; // ASCII code for newline
		stopThread = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		thread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && deviceConnected && !stopThread) {
					try {
						int bytesAvailable = inStream.available();
						if (bytesAvailable > 0) {
							byte[] packetBytes = new byte[bytesAvailable];
							inStream.read(packetBytes);
							for (int i = 0; i < bytesAvailable; i++) {
								if (packetBytes[i] == delimiter) {
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;
									handler.post(new Runnable() {
										public void run() {
											testView.setText(data);
											spectrumStr = data;
										}
									});
								}
								else {
									readBuffer[readBufferPosition++] = packetBytes[i];
								}
							}
						}
					} catch (IOException e) {
						stopThread = true;
					}
				}
			}
		});
		thread.start();
	}
	
}
