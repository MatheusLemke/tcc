package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");
        String elmName = intent.getStringExtra("ElmName");
        float time = intent.getFloatExtra("Time", 0);

        if (type.equals("Test Unique"))
            if (intent.getIntExtra("ElmType", -1) == 1)
                updateLayout(intent.getStringExtra("Class"));
            else
                updateLayout(intent.getDoubleExtra("Result", -1), false);
        else
            updateLayout(intent.getDoubleExtra("Accuracy", 0), true);

        updateLayout(type, elmName, time);
    }

    private void updateLayout(double value, boolean isAccuracy)
    {
        if (!isAccuracy)
        {
            TextView textView = (TextView) findViewById(R.id.textView_Results_Accuracy);
            textView.setText("Result");
        }
        TextView textView2 = (TextView) findViewById(R.id.textView_Results_AccuracyFloat);
        textView2.setText(String.valueOf(value));
    }

    private void updateLayout(String classs)
    {
        TextView textView = (TextView) findViewById(R.id.textView_Results_Accuracy);
        textView.setText("Class");
        TextView textView2 = (TextView) findViewById(R.id.textView_Results_AccuracyFloat);
        textView2.setText(classs);
    }

    private void updateLayout(String type, String elmName, double time)
    {
        TextView textView = (TextView) findViewById(R.id.textView_Results_Result);
        textView.setText(type + " Result");
        TextView textView1 = (TextView) findViewById(R.id.textView_Results_ElmNameString);
        textView1.setText(elmName);
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
