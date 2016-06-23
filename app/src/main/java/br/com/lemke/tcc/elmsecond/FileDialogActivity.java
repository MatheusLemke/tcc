package br.com.lemke.tcc.elmsecond;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class FileDialogActivity extends AppCompatActivity
{
    private static AlertDialog.Builder builder;
    private static ArrayAdapter<String> arrayAdapter;
    private static ArrayList<File> Current_Files;
    private static AlertDialog alertDialog;

    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_dialog);
        builder = new AlertDialog.Builder(context);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File files_sd[] = externalStorageDirectory.listFiles();

        Current_Files = new ArrayList<>(files_sd.length);
        for (int i = 0; i < files_sd.length; i++)
            Current_Files.add(files_sd[i]);

        ArrayList<String> items = new ArrayList<>(Current_Files.size());
        for (int i = 0; i < Current_Files.size(); i++)
            items.add(files_sd[i].getName());

        ListView list = (ListView) new ListView(context);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, items);

        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long id)
            {
                if (Current_Files.get(pos).isFile())
                {
                    alertDialog.dismiss();
                    setResult(Activity.RESULT_OK, new Intent().putExtra("File", Current_Files.get(pos)));
                    Toast.makeText(context, Current_Files.get(pos).getName(), Toast.LENGTH_SHORT);
                    finish();
                }
                else
                {
                    File newFiles[] = Current_Files.get(pos).listFiles();

                    Current_Files = new ArrayList<>(newFiles.length);
                    for (int i = 0; i < newFiles.length; i++)
                        Current_Files.add(newFiles[i]);

                    ArrayList<String> newItems = new ArrayList<>(Current_Files.size());
                    for (int i = 0; i < Current_Files.size(); i++)
                        newItems.add(newFiles[i].getName());

                    ListView list = (ListView) new ListView(context);
                    arrayAdapter.clear();
                    arrayAdapter.addAll(newItems);
                    list.setAdapter(arrayAdapter);
                    builder.setView(list);
                }
            }
        });
        builder.setView(list);
        builder.setTitle(R.string.choose_file);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                onBackPressed();
            }
        });

        alertDialog = builder.show();
        alertDialog.setOnKeyListener(new AlertDialog.OnKeyListener()
        {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    setResult(Activity.RESULT_CANCELED);
                    alertDialog.dismiss();
                    finish();
                }
                return true;
            }
        });
        ;
    }

    @Override
    public void onBackPressed()
    {
        setResult(Activity.RESULT_CANCELED);
        alertDialog.dismiss();
        finish();
    }
}
