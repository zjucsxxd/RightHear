//
// Haptic.c
//
// Haptic feedback message processing and execution.  This file will contain methods to do the following things:
//    Interpret haptic feedback messages from a processor.
//    Issue a callback to alert the user based on a haptic feedback message.
//
// Memory Requirements:  This module requires 32 bytes of working memory.
//
// Jesse Rosalia
//

#include "Haptic.h"

// Incoming buffer is only 32 bytes, because we're only specifying small bits of information
byte_t incoming[32];
int    incomingPos = 0;
int     msgStarted = 0;
int       msgReady = 0;

void setupHaptic() {
  //nothing to do yet
}

void doHapticLoop() {
  if (msgReady) {
    //first byte is the region
    //TODO: we can expand this, or wrap a struct around it, or reduce the size of incoming.
    hapticCallback(incoming[0]);
  }
}

void hapticByteReceived(byte_t inByte) {
  //process the incoming byte, and decode any haptic feedback messages we may see.
  switch(inByte) {
    case HAPTIC_MSG_START:
      incomingPos = 0;
      msgStarted  = 1;
      break;
    case HAPTIC_MSG_END:
      msgStarted  = 0;
      msgReady    = 1;
      break;
    default:
      if (msgStarted) {
        incoming[incomingPos] = inByte;
        incomingPos++;
      }
  }
}
