package br.com.lemke.tcc.elmsecond;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import br.com.lemke.tcc.elm.ElmTestFile;
import br.com.lemke.tcc.elm.ElmTrain;
import br.com.lemke.tcc.util.Constants;
import br.com.lemke.tcc.util.FileManipulation;
import no.uib.cipr.matrix.NotConvergedException;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_WRITE_STORAGE);

        if (Constants.isTestDebug)
        {
            Intent intent = new Intent(this, TestDebugActivity.class);
            startActivity(intent);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_WRITE_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.d("Permissions", "Permission Granted!");
            else finish();
        }
    }
}
