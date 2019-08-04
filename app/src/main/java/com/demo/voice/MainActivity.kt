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
    }

    override fun onResults(bundle: Bundle?) {

        val matches = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null)
            emailAddress.setText(matches.get(0));

    }

    var t1: TextToSpeech? = null
    val mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        setSpeech()
    }

    private fun setSpeech() {
        val mSpeechRecognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        );
        mSpeechRecognizer.setRecognitionListener(this)

        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                t1?.setLanguage(Locale.ENGLISH)
                t1?.speak(
                    "The safer, easier way to pay in India and around the world",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                var handler: Handler = Handler()
                handler.postDelayed({

                    t1?.speak(
                        "Enter User Name",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent)
                    emailAddress.setText("")
                }, 5000)

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
}
