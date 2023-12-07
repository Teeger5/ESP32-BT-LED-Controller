package hu.nye.doragongasukidesu.ledcontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import hu.nye.doragongasukidesu.ledcontroller.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;
	private Slider sliderRed, sliderGreen, sliderBlue;
	private TextView textValueRed, textValueGreen, textValueBlue;
	private BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		sliderRed = binding.sliderRed;
		sliderGreen = binding.sliderGreen;
		sliderBlue = binding.sliderBlue;
		textValueRed = binding.textValueRed;
		textValueGreen = binding.textValueGreen;
		textValueBlue = binding.textValueBlue;

		sliderRed.addOnChangeListener(new SliderChangeListener(sliderRed, textValueRed));
		sliderGreen.addOnChangeListener(new SliderChangeListener(sliderGreen, textValueGreen));
		sliderBlue.addOnChangeListener(new SliderChangeListener(sliderBlue, textValueBlue));

		ActivityCompat.requestPermissions(this, (String[]) Arrays.asList(
				Manifest.permission.ACCESS_FINE_LOCATION
		).toArray(), 200);

		log("Creating BluetoothAdapter");
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			// Device doesn't support Bluetooth
		}
		if (!bluetoothAdapter.isEnabled()) {
			System.out.println("Bluetooth is not enabled");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent, REQ);
		}
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(receiver, filter);
		bluetoothAdapter.startDiscovery();
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = device.getName();
				String deviceHardwareAddress = device.getAddress(); // MAC address

				log("Bluetooth device: %s -> %s\n", deviceName, deviceHardwareAddress);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Don't forget to unregister the ACTION_FOUND receiver.
		unregisterReceiver(receiver);
	}

	static void log(String format, Object...objects) {
		Log.d("Tigers", String.format(format, objects));
	}

	static void loge(Exception e, String format, Object...objects) {
		Log.e("Tigers", String.format(format, objects), e);
	}

	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			// Use a temporary object that is later assigned to mmServerSocket
			// because mmServerSocket is final.
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client code.
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth name", UUID.randomUUID());
			} catch (Exception e) {
				loge(e, "Socket's listen() method failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned.
			while (true) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					loge(e, "Socket's accept() method failed");
					break;
				}

				if (socket != null) {
					// A connection was accepted. Perform work associated with
					// the connection in a separate thread.
//					manageMyConnectedSocket(socket);
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					break;
				}
			}
		}

		// Closes the connect socket and causes the thread to finish.
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				loge(e, "Could not close the connect socket");
			}
		}
	}
}