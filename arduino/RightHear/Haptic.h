
#include "Types.h"

#define HAPTIC_LEFT 0
#define HAPTIC_RIGHT 1

#define HAPTIC_MSG_START 0xFF
#define HAPTIC_MSG_END 0xFE

#ifdef __cplusplus
 extern "C" {
#endif

// Function pointer for haptic callback
void (*hapticCallback)(int region);

void setupHaptic();
void doHapticLoop();
void hapticByteReceived(byte_t inByte);
//void swapBuffers();

#ifdef __cplusplus
}
#endif


