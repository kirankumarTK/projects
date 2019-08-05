package com.demo.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), RecognitionListener {

    val mSpeechRecognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    var t1: TextToSpeech? = null
    val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    var currentField: String = ""
    var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        setSpeech()
    }

    private fun setSpeech() {
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        mSpeechRecognizer.setRecognitionListener(this)

        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                t1?.language = Locale.ENGLISH

                val voiceList = t1?.voices
                if (voiceList != null) {
                    for (voice in voiceList) {
                        if (voice.name.equals("ta", ignoreCase = true)) {
                            t1?.voice = voice
                        }
                    }
                }

                t1?.speak(
                    "இந்தியா மற்றும் உலகளவில் எளிதாக மற்றும் பாதுகாப்பாக பரிவர்த்தனை செய்ய  " +
                            "        தங்கள் மின்னஞ்சல் முகவரியை உள்ளிடவும்",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                t1?.isSpeaking
                handler.postDelayed({
                    currentField = "Email"
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                    emailAddress.setText("")
                }, 7000)

            }
        })
        next.setOnClickListener { startActivity(Intent(this, SecondActivity::class.java)) }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onPause() {
        if (t1 != null) {
            t1!!.stop()
            t1!!.shutdown()
        }
        super.onPause()
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.d("Tag", "onReadyForSpeech")
    }

    override fun onRmsChanged(p0: Float) {
        Log.d("Tag", "onRmsChanged")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.d("Tag", "onBufferReceived")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.d("Tag", "onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.d("Tag", "onEvent")
    }

    override fun onBeginningOfSpeech() {
        Log.d("Tag", "onReadyForSpeech")
    }

    override fun onEndOfSpeech() {
        Log.d("Tag", "onEndOfSpeech")
    }

    override fun onError(p0: Int) {
        Log.d("Tag", "onError")
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
    }

    override fun onResults(bundle: Bundle?) {

        val matches = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null) {
            if (currentField.equals("Email")) {
                emailAddress.setText(matches.get(0))
                currentField = "password"
                t1?.speak(
                    "கடவுச்சொல்லை உள்ளிடவும்",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                handler.postDelayed({

                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                }, 1000)
            } else if (currentField.equals("password")) {
                password.setText(matches.get(0))
            }
        }
    }


}
