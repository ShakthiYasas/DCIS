package com.example.dcis2
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSService : Service() {
    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    Log.i("TTS", "TTS initialized successfully")
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            Log.d("TTS", "Speech started")
                        }

                        override fun onDone(utteranceId: String?) {
                            Log.d("TTS", "Speech finished")
                        }

                        @Deprecated("Deprecated in Java",
                            ReplaceWith("Log.e(\"TTS\", \"Speech error\")", "android.util.Log")
                        )
                        override fun onError(utteranceId: String?) {
                            Log.e("TTS", "Speech error")
                        }
                    })
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("text")?.let { text ->
            speakText(text)
        }
        return START_NOT_STICKY
    }

    private fun speakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}