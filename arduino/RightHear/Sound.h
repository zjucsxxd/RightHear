
#include "Types.h"

#ifdef __cplusplus
 extern "C" {
#endif

// Function pointer for tx sound callback
void (*txSoundCallback)(byte_t *buffer, int len);

void setupSound();
void doSoundLoop();
void soundSampleReceived(short sample);  
void swapBuffers();

#ifdef __cplusplus
}
#endif



