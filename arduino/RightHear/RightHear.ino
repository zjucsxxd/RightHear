//
// RightHear.ino
//
// This is the main sketch for the RightHear voice processing and recognition system.  This sketch is responsible for:
//    Sampling the microphones
//    Rejecting silence/garbage input
//    Interlacing two channels of audio
//    Sending interlaced data over bluetooth to processor
//    Receiving haptic feedback messages, and engaging feedback mechanisms.
//
// Memory Requirements:
//   Sound.c/h - 528 bytes per channel
//   Haptic.c/h - 32 bytes.
//
//

#include "Haptic.h"
#include "Sound.h"

// Digital pins
int hapVibLeftPin  = 7;
int hapVibRightPin = 8;

// Function prototypes
void setupADCInterruptOnPin(int analogInPin);

//#define ARDUINO_PLATFORM
#define TEENSYPP_PLATFORM
#ifdef TEENSYPP_PLATFORM
  // Analog pins
  #define NUM_MICS 2
  int micPins[NUM_MICS] = {6, 7};
  int curMicPinInx = 0;

  int txLedPin = 6;
  int txRTSPin = 4;
  int txCTSPin = 7;
  usb_serial_class S = Serial;
  usb_serial_class Console = Serial;
#else
  // Analog pins
  int micInPin = 0; //analog in for microphone

  int txLedPin = 13;
  int txRTSPin = 4;
  int txCTSPin = 7;
  HardwareSerial S = Serial;
  HardwareSerial Console = Serial;
#endif

// Initialization
void setup(){

  setupHaptic();
  setupSound();

  //set up the ADC to sample from and kick off the first ADC
  setupADCInterruptOnPin(micPins[curMicPinInx]);
  
  // Callback for haptic feedback/alerts
  hapticCallback=alertUserHaptic;

  // Callback for sending sound buffer
  txSoundCallback=sendSoundBuffer;

  // For the bluetooth connection
  Console.begin(115200);
  S.begin(115200);
 
  // Haptic feedback vibration motors
  pinMode(hapVibLeftPin,  OUTPUT);
  pinMode(hapVibRightPin, OUTPUT);

  // TX light
  pinMode(txLedPin, OUTPUT);
  pinMode(txRTSPin, OUTPUT);
  pinMode(txCTSPin, INPUT);
}


// Processor loop
void loop() {

  //NOTE: these both work on callbacks.

  //run the sound subroutines
  doSoundLoop();
  //run the haptic subroutines
  doHapticLoop();
  if (Serial.available()) {
    serialEvent();
  }
}

void alertUserHaptic(int region) {
  
}

int counter = 0;
// Callback to send sound buffer to processor via serial (Bluetooth)
void sendSoundBuffer(byte *buffer, int len) {
    counter++;
    //signal TX
    if (counter % 100 == 0) {
//      digitalWrite(txLedPin, HIGH);
    }
    // Write the ready buffer out to the serial line, to transmit it over bluetooth
//    Serial.write(len);
    S.write(buffer, len);
//    S.flush();
    //clear TX signal
    if (counter % 200 == 0) {
//      digitalWrite(hapVibRightPin, LOW);
//      digitalWrite(txLedPin, LOW);  
      counter = 0;
    }
}

void setNextMic() {
    curMicPinInx = ++curMicPinInx % NUM_MICS;
    setADCInputPin(micPins[curMicPinInx]);  
}

/** ---------------  ADC Interrupt routines for audio sampling --------------- **/

void setupADCInterruptOnPin(int analogInPin) {
 analogReference(EXTERNAL); // 3.3V to AREF
//  adc_save = ADCSRA; // Save ADC setting for restore later

  // Start up ADC in free-run mode for audio sampling:
  DIDR0 |= _BV(ADC0D); // Disable digital input buffer on ADC0
  ADMUX = analogInPin; // Channel sel, right-adj, AREF to 3.3V regulator
  ADCSRB = 0; // Free-run mode
  ADCSRA = _BV(ADEN) | // Enable ADC
    _BV(ADSC) | // Start conversions
    _BV(ADATE) | // Auto-trigger enable
    _BV(ADIE) | // Interrupt enable
    _BV(ADPS2) | // 128:1 prescale...
    _BV(ADPS1) | // ...yields 125 KHz ADC clock...
    _BV(ADPS0); // ...13 cycles/conversion = ~9615 Hz
    
    //TODO: need to change this to 64:1 presale, to get 2 channels at 9615 Hz...but to do that, we need to use the usb_serial.c/h library for high speed usb serial.
}

void setADCInputPin(int analogInPin) {
  ADMUX = ADMUX & ~0x1F | (analogInPin & 0xF); //protection to avoid overrunning
 
}
//void setupADCInterruptOnPin(int analogInPin) {
//  analogReference(EXTERNAL);
//  // clear ADLAR in ADMUX (0x7C) to right-adjust the result
//  // ADCL will contain lower 8 bits, ADCH upper 2 (in last two bits)
//  ADMUX &= B11011111;
// 
//  // Set REFS1..0 in ADMUX (0x7C) to change reference voltage to the
//  // proper source (01)
////  ADMUX |= B11000000;
//   ADMUX |= B01000000;
//  // Clear MUX3..0 in ADMUX (0x7C) in preparation for setting the analog
//  // input
//  ADMUX &= B11110000;
// 
//  // Set MUX3..0 in ADMUX (0x7C) to read from AD8 (Internal temp)
//  // Do not set above 15! You will overrun other parts of ADMUX. A full
//  // list of possible inputs is available in Table 24-4 of the ATMega328
//  // datasheet
//  ADMUX |= (analogInPin & 0xFFFF); //protection to avoid overrunning
//  // ADMUX |= B00001000; // Binary equivalent
// 
//  // Set ADEN in ADCSRA (0x7A) to enable the ADC.
//  // Note, this instruction takes 12 ADC clocks to execute
//  ADCSRA |= B10000000;
//  
//  // Set ADATE in ADCSRA (0x7A) to enable auto-triggering.
//  ADCSRA |= B00100000;
// 
//  // Clear ADTS2..0 in ADCSRB (0x7B) to set trigger mode to free running.
//  // This means that as soon as an ADC has finished, the next will be
//  // immediately started.
//  ADCSRB &= B11111000;
// 
//  // Set the Prescaler to 128 (16000KHz/128 = 125KHz)
//  // Above 200KHz 10-bit results are not reliable.
//  ADCSRA |= B00000111;
// 
//  // Set ADIE in ADCSRA (0x7A) to enable the ADC interrupt.
//  // Without this, the internal interrupt will not trigger.
//  ADCSRA |= B00001000;
// 
//  DIDR0 = 0x1;
//
//  // Enable global interrupts
//  // AVR macro included in <avr/interrupts.h>, which the Arduino IDE
//  // supplies by default.
//  sei();
// 
//  // Set ADSC in ADCSRA (0x7A) to start the ADC conversion
//  ADCSRA |=B01000000;
//}

// Interrupt service routine for the ADC completion
ISR(ADC_vect){

  // Must read low first
  soundSampleReceived(ADCL | (ADCH << 8));
  
  setNextMic();
  // Not needed because free-running mode is enabled.
  // Set ADSC in ADCSRA (0x7A) to start another ADC conversion
  // ADCSRA |= B01000000;
}

/** ---------------  Serial routines for bluetooth->Arduino communications --------------- **/

/*
  SerialEvent occurs whenever a new data comes in the
 hardware serial RX.  This routine is run between each
 time loop() runs, so using delay inside loop can delay
 response.  Multiple bytes of data may be available.
 */
void serialEvent() {
  digitalWrite(txLedPin, HIGH);
  Serial.println("EVENT");  
  while (Serial.available()) {
    Serial.println(Serial.read());
    digitalWrite(hapVibRightPin, HIGH);
    // get the new byte:
    hapticByteReceived(Serial.read());
  }
    delay(200);
    digitalWrite(hapVibRightPin, LOW);
  digitalWrite(txLedPin, LOW);
}



