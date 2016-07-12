package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.lemke.tcc.elm.ElmTestUnique;
import br.com.lemke.tcc.util.ElmData;
import br.com.lemke.tcc.util.FileManipulation;
import br.com.lemke.tcc.view.InputAttributeValueDialog;
import br.com.lemke.tcc.view.TrainedElmsDialog;

public class TestUniqueActivity extends AppCompatActivity implements
        InputAttributeValueDialog.InputAttributeValuePositiveListener, TrainedElmsDialog.TrainedElmsListener
{
    private String attributesNames[] = null;
    private String classesNames[] = null;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private String values[] = null;

    private ElmData elmData;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        assert editText != null;
        editText.setText(elmName);

        // Gerar barra de carregamento
        FileManipulation fileManipulation = new FileManipulation();
        elmData = fileManipulation.importToTest(elmName, getBaseContext().getFilesDir());
        initializeListViewWithAttributes();
    }

    // Gerar barra de carregamento
    private void initializeListViewWithAttributes()
    {
        attributesNames = elmData.getAttributesNames();

        // Se for tipo 0 então vai ser null
        classesNames = elmData.getClassesNames();

        values = new String[attributesNames.length];
        for (int i = 0; i < values.length; i++)
            values[i] = null;

        listView = (ListView) findViewById(R.id.listView_TestUnique_Attributes);
        data = new ArrayList<>();
        for (String attributesName : attributesNames)
        {
            Map<String, String> datum = new HashMap<>(2);
            datum.put("value", getString(R.string.emptyValueAttribute_listView));
            datum.put("attributeName", attributesName);
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
                inputAttributeValueDialog.setAttributeName(attributesNames[position]);
                inputAttributeValueDialog.setPosition(position);
                inputAttributeValueDialog.show(getFragmentManager(), "Input value");
            }

        });
        listView.setAdapter(adapter);

    }

    @Override
    public void onPositiveButtonClick(String inputText, int position)
    {
        values[position] = inputText;
        Map<String, String> datum = new HashMap<>(2);
        datum.put("value", inputText);
        datum.put("attributeName", attributesNames[position]);
        data.set(position, datum);
        adapter.notifyDataSetChanged();
    }

    public void imageButtonRunTestUniqueClick(View view)
    {
        boolean hasNull = false;
        EditText editTextElmName = (EditText) findViewById(R.id.editText_TestUnique_ELMName);
        assert editTextElmName != null;
        String elmName = editTextElmName.getText().toString();

        if (!elmName.matches(""))
        {
            for (int i = 0; i < values.length; i++)
                if (values[i] == null)
                {
                    Toast.makeText(this, "The attribute " + attributesNames[i] + " is undefined!", Toast.LENGTH_SHORT).show();
                    hasNull = true;
                    break;
                }
            if (!hasNull)
            {
                double[] input = new double[values.length + 1];
                input[0] = 0;
                for (int i = 1; i < input.length; i++)
                    input[i] = Double.parseDouble(values[i - 1]);

                ElmTestUnique elmTestUnique = new ElmTestUnique();
                elmTestUnique.test(input, elmData);

                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra("Type", "Test Unique");
                intent.putExtra("ElmName", elmName);
                intent.putExtra("Time", elmTestUnique.getTestingTime());
                intent.putExtra("ElmType", elmData.getElm_Type());
                if (elmData.getElm_Type() == 1)
                    intent.putExtra("Class", elmTestUnique.getTestingClass());
                else
                    intent.putExtra("Result", elmTestUnique.getTestingResult());

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
