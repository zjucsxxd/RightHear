/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.
 
  This example code is in the public domain.
 */
 
// #include "usb_serial.h"
 
// Pin 13 has an LED connected on most Arduino boards.
// Pin 11 has the LED on Teensy 2.0
// Pin 6  has the LED on Teensy++ 2.0
// Pin 13 has the LED on Teensy 3.0
// give it a name:
int led = 6;
int vibe = 8;

#define LED_CONFIG	(DDRD |= (1<<6))
#define LED_ON		(PORTD &= ~(1<<6))
#define LED_OFF		(PORTD |= (1<<6))
#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))

#define _delay_ms delay
void send_str(const char *s);

#define CLEAR_TIMER0_OVERFLOW() (TIFR0 |= (1<<TOV0))
#define IS_TIMER0_OVERFLOW() (TIFR0 & (1<<TOV0))

// the setup routine runs once when you press reset:
void setup() {                
//	// set for 16 MHz clock, and turn on the LED
//	CPU_PRESCALE(0);
//	LED_CONFIG;
//	LED_OFF;
//	DDRC |= (1<<2);  // Pin C2 to show % cpu usage
//
//	// configure timer0 to overflow every 4 ms, prescale=256, top=250
//	// 250 * 256 / 16 MHz = 4 ms
//	TIMSK0 = 0;
//	TCCR0A = (1<<WGM01)|(1<<WGM00);
//	OCR0A = 250;
//	TCCR0B = (1<<WGM02)|(1<<CS02);
//
//	// initialize the USB, and then wait for the host
//	// to set configuration.  If the Teensy is powered
//	// without a PC connected to the USB port, this 
//	// will wait forever.
//	usb_init();
//	while (!usb_configured()) /* wait */ ;
//	delay(1000);
}

// the loop routine runs over and over again forever:
void loop() {
	uint8_t n;
	uint16_t count;
	const uint8_t test_string[] = {  
		"USB Fast Serial Transmit Bandwidth Test, capture this text.\r\n"};

		// wait for the user to run their terminal emulator program
		// which sets DTR to indicate it is ready to receive.
		while (!Serial.dtr()) /* wait */ ;

		// Turn the LED on while sending data
		LED_ON;

		// give the user 5 seconds to enable text capture in their
		// terminal emulator, or do whatever to get ready
		for (n=5; n; n--) {
			send_str(PSTR("10 second speed test begins in "));
			Serial.write(n);//usb_serial_putchar(n + '0');
			send_str(PSTR(" sec.\r\n"));
			if (!Serial.dtr()) break;
			_delay_ms(1000);
		}

		// wait for a 4 ms timer0 period to begin
		CLEAR_TIMER0_OVERFLOW();
		while (!IS_TIMER0_OVERFLOW()) /* wait */ ;
		CLEAR_TIMER0_OVERFLOW();
		count=0;

		// send a string as fast as possible, for 10 seconds
		while (1) {
			Serial.write(test_string, (uint16_t)(sizeof(test_string)-1));
			if (IS_TIMER0_OVERFLOW()) {
				CLEAR_TIMER0_OVERFLOW();
				count++;
				if (count == 2500) break;   // 10 sec
    			        if (!Serial.dtr()) break;
			}
		}
		PORTC &= ~(1<<2);
		send_str(PSTR("done!\r\n"));
		LED_OFF;

		// after the test, wait forever doing nothing,
		// well, at least until the terminal emulator quits
		// or drops DTR for some reason.
		while (Serial.dtr()) /* wait */ ;
}

// Send a string to the USB serial port.  The string must be in
// flash memory, using PSTR
//
void send_str(const char *s)
{
	char c;
	while (1) {
		c = pgm_read_byte(s++);
		if (!c) break;
                Serial.write(c);
	}
}

