package hu.nye.doragongasukidesu.ledcontroller;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.slider.Slider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.BLUETOOTH_ADMIN
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
//		registerReceiver(receiver, filter);
		bluetoothAdapter.startDiscovery();
		bluetoothLE();
	}

/*	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = device.getName();
				String deviceHardwareAddress = device.getAddress(); // MAC address

				log("Bluetooth device: %s -> %s\n", deviceName, deviceHardwareAddress);

				// Új kód kezdete
				// Létrehozunk egy BluetoothSocket objektumot a kiválasztott eszközzel
				BluetoothSocket socket = null;
				try {
					// UUID egy egyedi azonosító, amelyet a Bluetooth szolgáltatások használnak
					// Egy általános UUID-t használunk, amelyet a BluetoothSerial alkalmazás is használ
					UUID uuid = UUID.randomUUID();
					// Megpróbáljuk létrehozni a BluetoothSocket objektumot a createRfcommSocketToServiceRecord() metódussal
					socket = device.createRfcommSocketToServiceRecord(uuid);
				} catch (IOException e) {
					// Ha nem sikerül, akkor logoljuk a kivételt
					loge(e, "Bluetooth", "Socket creation failed");
				}

				// Ha sikerült létrehozni a BluetoothSocket objektumot, akkor megpróbáljuk csatlakozni az eszközhöz
				if (socket != null) {
					try {
						bluetoothAdapter.cancelDiscovery();
						// Csatlakozunk az eszközhöz a connect() metódussal
						socket.connect();
						// Ha sikerült a csatlakozás, akkor logoljuk az eseményt
						log("Connected to " + deviceName);
						// Lekérjük a BluetoothSocket kimeneti adatfolyamát az getOutputStream() metódussal
						OutputStream out = socket.getOutputStream();
						// Elküldjük a 0xffffff egész számot a kimeneti adatfolyamon a write() metódussal
						// Ehhez először átalakítjuk a számot egy 4 bájtos tömbbé
						byte[] data = new byte[4];
						data[0] = (byte) (0xffffff & 0xff); // Alsó bájt
						data[1] = (byte) ((0xffffff >> 8) & 0xff); // Második bájt
						data[2] = (byte) ((0xffffff >> 16) & 0xff); // Harmadik bájt
						data[3] = 0; // Felső bájt (0, mert a szám nem nagyobb, mint 0xffffff)
						// Elküldjük a tömböt a kimeneti adatfolyamon
						out.write(data);
						// Logoljuk a küldött adatot
						log("Sent data: " + Arrays.toString(data));
						// Bezárjuk a BluetoothSocket objektumot a close() metódussal
						socket.close();
					} catch (IOException e) {
						// Ha nem sikerül a csatlakozás vagy a küldés, akkor logoljuk a kivételt
						loge(e, "Connection or sending failed");
					}
				}
				// Új kód vége
			}
		}
	};*/

	private void bluetoothLE () {
		// Létrehozunk egy ScanCallback objektumot, amely kezeli a talált BLE eszközöket
		ScanCallback scanCallback = new ScanCallback() {
			@Override
			public void onScanResult(int callbackType, ScanResult result) {
				// Ha találtunk egy BLE eszközt, akkor logoljuk a nevét és a címét
				BluetoothDevice device = result.getDevice();
				Log.d("BLE", "Found device: " + device.getName() + " -> " + device.getAddress());
			}

			@Override
			public void onBatchScanResults(List<ScanResult> results) {
				// Ha több BLE eszközt találtunk egyszerre, akkor logoljuk őket
				for (ScanResult result : results) {
					BluetoothDevice device = result.getDevice();
					Log.d("BLE", "Found device: " + device.getName() + " -> " + device.getAddress());
				}
				log("Batch scan results: " + results);
			}

			@Override
			public void onScanFailed(int errorCode) {
				// Ha nem sikerült a BLE keresés, akkor logoljuk a hiba kódját
				Log.e("BLE", "Scan failed with error code: " + errorCode);
			}
		};

		// Létrehozunk egy BluetoothLeScanner objektumot, amely végzi a BLE keresést
		BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
		// Megadjuk a keresési paramétereket, például az időtartamot és a teljesítményt
		ScanSettings scanSettings = new ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Magas teljesítményű keresés
				.setReportDelay(0) // Azonnali eredmények
				.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
				.build();
		// Megadjuk a keresési szűrőket, például az eszköz nevét vagy a szolgáltatás UUID-jét
		List<ScanFilter> scanFilters = new ArrayList<>();
// Itt hozzáadhatunk szűrőket, ha szükséges
// Például: scanFilters.add(new ScanFilter.Builder().setDeviceName("MyDevice").build());

// Elindítjuk a BLE keresést a startScan() metódussal, amelynek átadjuk a szűrőket, a paramétereket és a visszahívást
		bluetoothLeScanner.startScan(null, scanSettings, scanCallback);
// Logoljuk, hogy elindult a keresés
		Log.d("BLE", "Scan started");

// Megállítjuk a BLE keresést a stopScan() metódussal, amelynek átadjuk a visszahívást
//		bluetoothLeScanner.stopScan(scanCallback);
// Logoljuk, hogy leállt a keresés
//		runOnUiThread(() -> bluetoothLeScanner.stopScan(scanCallback));
		new Handler().postDelayed(() -> {
			bluetoothLeScanner.stopScan(scanCallback);
			Log.d("BLE", "Scan stopped");
		}, 10000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Don't forget to unregister the ACTION_FOUND receiver.
//		unregisterReceiver(receiver);
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