package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");
        String elmName = intent.getStringExtra("ElmName");
        double accuracy = intent.getDoubleExtra("Accuracy", 0);
        float time = intent.getFloatExtra("Time", 0);

        updateLayout(type, elmName, accuracy, time);
    }

    private void updateLayout(String type, String elmName, double accuracy, double time)
    {
        TextView textView = (TextView) findViewById(R.id.textView_Results_Result);
        textView.setText(type + " Result");
        TextView textView1 = (TextView) findViewById(R.id.textView_Results_ElmNameString);
        textView1.setText(elmName);
        TextView textView2 = (TextView) findViewById(R.id.textView_Results_AccuracyFloat);
        textView2.setText(String.valueOf(accuracy));
        TextView textView3 = (TextView) findViewById(R.id.textView_Results_TimeFloat);
        textView3.setText(String.valueOf(time));
    }

    public void imageButtonReturnClick(View view)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
