//
// Sound.c
//
// Sound collection and processing routines.  This file will contain methods to do the following things:
//     Accumulate samples, and issue callback when buffer is full.
//     Filter samples based on silence/noise and eventually, meaningful values
//     Interlace multiple channels of audio for easy transmission
//
// Memory Requirements:  This module requires 528 bytes (2 x 256 byte buffers, plus pointers and indexes)
// of working memory per channel.
//
// Jesse Rosalia
//

#include "Sound.h"

//TODO: may have to use shorts for full resolution
// Audio buffers...we use two buffers so we can be transmitting one while we accumulate in the other
#define BUFFER_LENGTH 256
byte_t buffer1[BUFFER_LENGTH];
byte_t buffer2[BUFFER_LENGTH];

byte_t *curBuffer = buffer1;
byte_t *readyBuffer;
int bufPos = 0;

// High when a value is ready to be read
volatile int txFlag;

void setupSound() {
  //must be 0 initially, so we don't try and process an empty buffer 
  txFlag = 0; 
}

void doSoundLoop() {
  // Check to see if the value has been updated
  if (txFlag == 1) {
    txSoundCallback(readyBuffer, BUFFER_LENGTH);
    txFlag = 0;
  }
}

void soundSampleReceived(short sample) {

//  curBuffer[bufPos] = (byte_t)(sample >> 4); //take top 8 bits
  curBuffer[bufPos] = (byte_t)(sample >> 2); //take top 8 bits
  bufPos++;
  if (bufPos >= 256) {
    swapBuffers();
    // Done reading
    txFlag = 1;
  }
  
  // Not needed because free-running mode is enabled.
  // Set ADSC in ADCSRA (0x7A) to start another ADC conversion
  // ADCSRA |= B01000000;
}

// Swap the buffers, setting the readyBuffer as the buffer to send, and curBuffer
// as the next buffer to fill.
void swapBuffers() {
  
  readyBuffer = curBuffer;
  if (curBuffer == buffer1) {
    curBuffer = buffer2;
  } else {
    curBuffer = buffer1;
  }
  bufPos = 0;
}

