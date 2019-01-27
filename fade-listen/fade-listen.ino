/*
  Started with code from http://www.arduino.cc/en/Tutorial/Fade

  This fades the LED on LED_PIN slowly.  If a number comes in over serial port 
  then the fade takes a break to flash the LED the given number of times.

  The intent here is to work toward Arduino triggering flash of a lightring
  when needed by Java app running on Raspberry Pi.
*/

int BAUD_RATE = 57600;
int LED_PIN = 5;

int brightness = 0;    // how bright the LED is
int fadeAmount = 2;    // how many points to fade the LED by

int flashCount = 0;

void setup() {
  Serial.begin(BAUD_RATE);
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  analogWrite(LED_PIN, brightness);

  if (Serial.available()) {
    flashCount = Serial.parseInt();
    if (flashCount > 0) {
      flash(flashCount);
      Serial.print("Flashed ");
      Serial.println(flashCount);
      Serial.flush();
    }
  }

  brightness = brightness + fadeAmount;
  if (brightness <= 0 || brightness >= 255) {
    brightness = (brightness < 0) ? 0 : 255;
    fadeAmount = -fadeAmount;
  }
  delay(30);
}

void flash(int n) {
  for (int i = 0; i < n; i++) {
    digitalWrite(LED_PIN, HIGH);
    delay(300);
    digitalWrite(LED_PIN, LOW);
    delay(300);
    analogWrite(LED_PIN, brightness);
  }
}
