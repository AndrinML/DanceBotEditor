#ifndef NATIVE_SOUND_HANDLER_H_
#define NATIVE_SOUND_HANDLER_H_

#include <jni.h>

#include "Mp3Decoder.h"
#include "Mp3Encoder.h"
#include "BeatExtractor.h"

class NativeSoundHandler {

public:

    static const char* LOG_TAG = "NATIVE_SOUND_HANDLER";

    static const int NO_ERROR = 0;
    static const int ERROR = -1;

private:

    SoundFile m_sound_file;
    Mp3Decoder m_mp3_decoder;
    Mp3Encoder m_mp3_encoder;
    BeatExtractor m_beat_extractor;

};

#endif