package br.com.lemke.tcc.elmsecond;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void buttonTrainClick(View view)
    {
        Intent intent = new Intent(this, TrainActivity.class);
        startActivity(intent);
    }

    public void buttonTestClick(View view)
    {
        Intent intent = new Intent(this, TestFileActivity.class);
        startActivity(intent);
    }

    public void buttonTestInputClick(View view)
    {
        Intent intent = new Intent(this, TestUniqueActivity.class);
        startActivity(intent);
    }
}
