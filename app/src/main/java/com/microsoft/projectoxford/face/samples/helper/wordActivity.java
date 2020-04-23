package com.microsoft.projectoxford.face.samples.helper;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.microsoft.projectoxford.face.samples.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Synset;

public class wordActivity extends AppCompatActivity {
    java.util.ArrayList<String> str = new java.util.ArrayList<String>();
    int j = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wordnet);

        Intent intent = getIntent();
        String input = intent.getStringExtra("emotion");

        switch(input) {
            case "Anger": case "Fear":
                input = "calmness";
                break;
            case "Sadness":
                input = "pleasure";
                break;
            case "Contempt":
                input = "respect";
                break;
            case "Disgust":
                input = "liking";
                break;
            default:
                break;
        }

        try {
            str = testDictionary(input);
        } catch (IOException e) {
            Log.d("log", "Error : IOException. check the wordnet.");

            Toast.makeText(getApplicationContext(), "Error : IOException. check the wordnet.", Toast.LENGTH_LONG).show();

            return;
        }

        final LinearLayout lm = (LinearLayout) findViewById(R.id.ll);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        for (String btn_text : str) {

            if(j>=10) break;

            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);


            final Button btn = new Button(this);

            btn.setId(j + 1);
            btn.setText(btn_text);
            btn.setLayoutParams(params);
            final String position = btn_text;
            btn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    call_youtube(position);
                }
            });
            ll.addView(btn);
            lm.addView(ll);
            j++;
        }
    }
    public java.util.ArrayList<String> testDictionary (String input) throws IOException {
        java.util.ArrayList<String> a = new java.util.ArrayList<String>();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        URI uri = file.toURI();
        String path = uri.getPath() + "/wordnet/dict";

        URL url = new URL("file", null , path );

        IDictionary dict = new Dictionary(url);
        dict.open();

        IIndexWord idxWord = dict.getIndexWord (input, POS. NOUN );

        java.util.List<IWordID>  wordID = idxWord.getWordIDs () ;
        a.add(input);
        for(IWordID it : wordID) {
            IWord word = dict.getWord(it);
            for(ISynsetID w : word.getSynset().getRelatedSynsets()) {
                for(IWord m : dict.getSynset(w).getWords()) {
                    String synset = m.getSenseKey().getLemma();
                    if (dict.getSynset(w).getLexicalFile().getName() == "noun.feeling" && synset != "emotion" && synset != "feeling") {
                        a.add(synset);
                    }
                }
            }
        }
        return a;
    }
    public void call_youtube(String input) {
        String search = input+" song";
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", search);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}