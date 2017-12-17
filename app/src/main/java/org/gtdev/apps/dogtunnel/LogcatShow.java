package org.gtdev.apps.dogtunnel;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by henorvell on 10/15/17.
 */

public class LogcatShow extends AppCompatActivity {
    String cBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat);
        setTitle("Logcat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.LogcatCopy);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Logcat",cBuffer);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, R.string.msg_log_copied, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }
            TextView tv = (TextView)findViewById(R.id.textView_logcat);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(log.toString());
            cBuffer = log.toString();
            //final int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
            //tv.scrollTo(0, scrollAmount);

            final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollViewLogCat));
            scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    scrollview.post(new Runnable() {
                        public void run() {
                            scrollview.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            });

        } catch (IOException e) {
        }
    }

}
