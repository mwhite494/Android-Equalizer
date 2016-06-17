/*
6-Band Equalizer using an Arduino Uno, MSGEQ7, and a 16x32 RGB LED matrix.
By: Marco White
*/

#include <avr/pgmspace.h>
#include <Adafruit_GFX.h>
#include <RGBmatrixPanel.h>

#define INPUTPIN A3
#define STROBEPIN 12
#define RESETPIN 13
//#define CLK 11
//#define LAT 10
//#define OE 9
//#define A   A0
//#define B   A1
//#define C   A2

int spectrum[7];
int toAndroid[6];
int filter = 80;

//RGBmatrixPanel matrix(A, B, C, CLK, LAT, OE, true);

void setup() {
  Serial.begin(9600);
  //matrix.begin();
  pinMode(STROBEPIN, OUTPUT);
  pinMode(RESETPIN, OUTPUT);
  digitalWrite(RESETPIN, LOW);
  digitalWrite(STROBEPIN, HIGH);
}

void loop() {
  // Clear matrix
  //matrix.fillScreen(0);
  // Obtain spectrum information
  digitalWrite(RESETPIN, HIGH);
  digitalWrite(RESETPIN, LOW);
  for (int i=0; i<=6; i++) {
    digitalWrite(STROBEPIN, LOW);
    delay(5);
    spectrum[i] = analogRead(INPUTPIN);
    spectrum[i] = constrain(spectrum[i], filter, 1023);
    if (i != 0) {
      toAndroid[i-1] = map(spectrum[i], filter, 1023, 0, 500); // TEMPORARY, EVENTUALLY HAVE ARDUINO READ IN BYTE
      Serial.print(toAndroid[i-1]);
      Serial.print(" ");
    }
    spectrum[i] = map(spectrum[i], filter, 1023, 0, 15);
    digitalWrite(STROBEPIN, HIGH);
  }
  Serial.println();
  // Band 1
  /*matrix.fillRect(16,0,4,spectrum[1],matrix.Color333(7,0,0));
  matrix.fillRect(14,0,2,spectrum[1],matrix.Color333(7,0,0));
  // Band 2
  matrix.fillRect(18,0,2,spectrum[2],matrix.Color333(7,1,0));
  matrix.fillRect(12,0,2,spectrum[2],matrix.Color333(7,1,0));
  // Band 3
  matrix.fillRect(20,0,2,spectrum[3],matrix.Color333(7,4,0));
  matrix.fillRect(10,0,2,spectrum[3],matrix.Color333(7,4,0));
  // Band 4
  matrix.fillRect(22,0,2,spectrum[4],matrix.Color333(0,7,0));
  matrix.fillRect(8,0,2,spectrum[4],matrix.Color333(0,7,0));
  // Band 5
  matrix.fillRect(24,0,2,spectrum[5],matrix.Color333(0,0,7));
  matrix.fillRect(6,0,2,spectrum[5],matrix.Color333(0,0,7));
  // Band 6
  matrix.fillRect(26,0,2,spectrum[6],matrix.Color333(3,0,7));
  matrix.fillRect(4,0,2,spectrum[6],matrix.Color333(3,0,7));
  // Update matrix
  matrix.swapBuffers(false);*/
}

