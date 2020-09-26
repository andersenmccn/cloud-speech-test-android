package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


// Imports the Google Cloud client library
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
//import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.ByteString;


//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.ErrorCodes;
//import com.firebase.ui.auth.IdpResponse;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreException;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.WriteBatch;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private String transcript = "000000";


//    private FirebaseFirestore mFirestore;
//    private Query mQuery;



    public byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int l;
        byte[] data = new byte[1024];
        while ((l = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, l);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        startSignIn();
//    }
//
//    private void startSignIn() {
//        // Sign in with FirebaseUI
//        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(Collections.singletonList(
//                        new AuthUI.IdpConfig.EmailBuilder().build()))
//                .setIsSmartLockEnabled(false)
//                .build();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Enable Firestore logging
//        FirebaseFirestore.setLoggingEnabled(true);
//        mFirestore = FirebaseFirestore.getInstance();
//        mQuery = mFirestore.collection("speech").limit(1);

        Context applicationContext = this.getApplicationContext();

        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                try {
                    GoogleCredentials myCredentials = GoogleCredentials.fromStream(applicationContext.getResources().openRawResource(R.raw.credential));

                    CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(myCredentials);

                    SpeechSettings speechSettings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
                    SpeechClient speechClient = SpeechClient.create(speechSettings);



                    byte[] data = toByteArray(applicationContext.getResources().openRawResource(R.raw.audio));
                    ByteString audioBytes = ByteString.copyFrom(data);

                    // Builds the sync recognize request
                    RecognitionConfig config =
                            RecognitionConfig.newBuilder()
                                    .setEncoding(AudioEncoding.LINEAR16)
                                    .setSampleRateHertz(16000)
                                    .setLanguageCode("en-US")
                                    .build();
                    RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

                    // Performs speech recognition on the audio file
                    RecognizeResponse response = speechClient.recognize(config, audio);
                    List<SpeechRecognitionResult> results = response.getResultsList();

                    for (SpeechRecognitionResult result : results) {
                        // There can be several alternative transcripts for a given chunk of speech. Just use the
                        // first (most likely) one here.
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        System.out.printf("Transcription: %s%n", alternative.getTranscript());
                        transcript = alternative.getTranscript();
                    }
                }
                catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Thread t = new Thread(runnable);
        try {
            Thread.sleep(1000);
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TextView textView = (TextView) findViewById(R.id.text1);
        textView.setText(transcript);

    }
}