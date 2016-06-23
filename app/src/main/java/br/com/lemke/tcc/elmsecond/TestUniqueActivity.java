package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.lemke.tcc.elm.ElmTest;
import br.com.lemke.tcc.filemanipulation.FileManipulation;
import br.com.lemke.tcc.view.InputAttributeValueDialog;
import br.com.lemke.tcc.view.TrainedElmsDialog;

public class TestUniqueActivity extends AppCompatActivity implements
        InputAttributeValueDialog.InputAttributeValuePositiveListener, TrainedElmsDialog.TrainedElmsListener
{
    private String attributeNames[] = null;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private String values[] = null;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_unique);
    }

    public void buttonChooseELM(View view)
    {
        TrainedElmsDialog trainedElmsDialog = new TrainedElmsDialog();
        trainedElmsDialog.show(getFragmentManager(), "Trained ELMs");
    }

    @Override
    public void onElmChoosed(String elmName)
    {
        EditText editText = (EditText) findViewById(R.id.editText_TestUnique_ELMName);
        editText.setText(elmName);
        initializeListViewWithAttributes(elmName);
    }

    // Gerar barra de carregamento
    private void initializeListViewWithAttributes(String elmName)
    {
        try
        {
            FileManipulation fileManipulation = new FileManipulation();
            String elmFolderCanonicalPath = fileManipulation.getElmFolderCanonicalPath(elmName, this);
            attributeNames = fileManipulation.importElmAttributeNames(elmName, elmFolderCanonicalPath, true);
            values = new String[attributeNames.length];
            for (int i = 0; i < values.length; i++)
                values[i] = null;

            listView = (ListView) findViewById(R.id.listView_TestUnique_Attributes);
            data = new ArrayList<>();
            for (int i = 0; i < attributeNames.length; i++)
            {
                Map<String, String> datum = new HashMap<>(2);
                datum.put("value", getString(R.string.emptyValueAttribute_listView));
                datum.put("attributeName", attributeNames[i]);
                data.add(datum);
            }
            adapter = new SimpleAdapter(this, data,
                    android.R.layout.simple_list_item_2,
                    new String[]{"value", "attributeName"},
                    new int[]{android.R.id.text1,
                            android.R.id.text2});

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    InputAttributeValueDialog inputAttributeValueDialog = new InputAttributeValueDialog();
                    inputAttributeValueDialog.setAttributeName(attributeNames[position]);
                    inputAttributeValueDialog.setPosition(position);
                    inputAttributeValueDialog.show(getFragmentManager(), "Input value");
                }

            });
            listView.setAdapter(adapter);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPositiveButtonClick(String inputText, int position)
    {
        values[position] = inputText;
        Map<String, String> datum = new HashMap<>(2);
        datum.put("value", inputText);
        datum.put("attributeName", attributeNames[position]);
        data.set(position, datum);
        adapter.notifyDataSetChanged();
    }

    public void imageButtonRunTestUniqueClick(View view)
    {
        boolean hasNull = false;
        EditText editTextElmName = (EditText) findViewById(R.id.editText_TestUnique_ELMName);
        String elmName = editTextElmName.getText().toString();

        if (!elmName.matches(""))
        {
            for (int i = 0; i < values.length; i++)
                if (values[i] == null)
                {
                    listView.getChildAt(i).requestFocus(); // isso funciona?

                    Toast.makeText(this, "The attribute " + attributeNames[i] + " is undefined!", Toast.LENGTH_SHORT).show();
                    hasNull = true;
                    break;
                }
            if (!hasNull)
            {
                double[][] data = new double[1][values.length];
                for (int i = 0; i < values.length; i++)
                    data[0][i] = Double.parseDouble(values[i]);

                ElmTest elmTest = new ElmTest();
                elmTest.test(elmName, data, getBaseContext());

                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra("Type", "Test");
                intent.putExtra("ElmName", elmName);
                intent.putExtra("Accuracy", elmTest.getTestingAccuracy());
                intent.putExtra("Time", elmTest.getTestingTime());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else
        {
            editTextElmName.setError(getString(R.string.null_alert));
            editTextElmName.requestFocus();
        }
    }
}
