// Könyvtárak importálása
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define PIN_RED		 25
#define PIN_GREEN	 26
#define PIN_BLUE	 27

// Szolgáltatás UUID-je
#define SERVICE_UUID				"4fafc201-1fb5-459e-8fcc-c5c9c331914b"
// Jellemző UUID-je
#define CHARACTERISTIC_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"

// BLE szerver objektum létrehozása
BLEServer* pServer = NULL;
// BLE jellemző objektum létrehozása
BLECharacteristic* pCharacteristic = NULL;
// BLE kliens objektum létrehozása
BLEClient* pClient = NULL;

void setColor(int r, int g, int b);

// Visszahívó osztály a szerver eseményekhez
class MyServerCallbacks: public BLEServerCallbacks {
	void onConnect(BLEServer* pServer) {
		// Logoljuk, hogy csatlakozott egy kliens
		Serial.println("Client connected");
	};

	void onDisconnect(BLEServer* pServer) {
		// Logoljuk, hogy lecsatlakozott egy kliens
		Serial.println("Client disconnected");
	}
};

// Visszahívó osztály a jellemző eseményekhez
class MyCallbacks: public BLECharacteristicCallbacks {
	void onWrite(BLECharacteristic *pCharacteristic) {
		
		// Olvassuk ki a jellemző értékét, amit a kliens küldött
		std::string value = pCharacteristic->getValue();

		if (value.length() > 0) {
			// Logoljuk a kapott adatot
			Serial.print("Received data: ");
			for (int i = 0; i < value.length(); i++) {
				Serial.print(value[i]);
			}
			char control = value[0];
			if(control == '0') {
				setColor(255, 0, 0);
			}
			if(control == '1') {
				setColor(255, 255, 0);
			}
			Serial.println();
		}
	}
};

void setup() {
	// Seriális port inicializálása
	Serial.begin(115200);

	// BLE eszköz inicializálása
	BLEDevice::init("ESP32 BLE Server");

	// BLE szerver létrehozása
	pServer = BLEDevice::createServer();
	// Visszahívó függvény beállítása a szerverhez
	pServer->setCallbacks(new MyServerCallbacks());

	// BLE szolgáltatás létrehozása
	BLEService *pService = pServer->createService(SERVICE_UUID);

	// BLE jellemző létrehozása
	pCharacteristic = pService->createCharacteristic(
		CHARACTERISTIC_UUID,
		BLECharacteristic::PROPERTY_READ |
		BLECharacteristic::PROPERTY_WRITE |
		BLECharacteristic::PROPERTY_NOTIFY |
		BLECharacteristic::PROPERTY_INDICATE
	);

	// Visszahívó függvény beállítása a jellemzőhöz
	pCharacteristic->setCallbacks(new MyCallbacks());

	// Jellemző értékének beállítása
	pCharacteristic->setValue("Hello World");

	// Szolgáltatás indítása
	pService->start();

	// BLE reklámozás indítása
	pServer->getAdvertising()->start();
	// Logoljuk, hogy elindult a reklámozás
	Serial.println("Advertising started...");
}

void loop() {
	// Itt írhatunk további kódot, ha szükséges
	// Például küldhetünk adatot a kliensnek a notify() metódussal
	// pCharacteristic->notify();
}



/**
Szín beállítása egy egész számban megadott érték alapján
A számot érdemes hexadecimális formában megadni,
ha nem egy változó értéke kerül átadásra0
**/
void setColorHEX (int color) {
	int r = color >> 16;
	int g = (color >> 8) & 0x00ff;
	int b = color & 0x0000ff;
	setRed(r);
	setGreen(g);
	setBlue(b);
}

void setColor (int r, int g, int b) {
	setRed(r);
	setGreen(g);
	setBlue(b);
}

void setRed (int red) {
	analogWrite(PIN_RED, red);
}

void setGreen (int green) {
	analogWrite(PIN_GREEN, green);
}

void setBlue (int blue) {
	analogWrite(PIN_BLUE, blue);
}
