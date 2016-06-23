package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.lemke.tcc.constants.Constants;
import br.com.lemke.tcc.elm.ElmTrain;
import no.uib.cipr.matrix.NotConvergedException;

public class TrainActivity extends AppCompatActivity
{
    private String attributeNamesFilePath;
    private String trainFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
    }

    public void buttonChooseTrainFileClick(View view)
    {
        Intent intent = new Intent(this, FileDialogActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CHOOSE_TRAIN_FILE);
    }

    public void buttonChooseAttributeNamesFileClick(View view)
    {
        Intent intent = new Intent(this, FileDialogActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CHOOSE_ATTRIBUTES_FILE);
    }

    public void imageButtonRunClick(View view)
    {
        // Gerar uma barra de carregamento
        EditText editTextElmName = (EditText) findViewById(R.id.editText_Train_ELMName);
        EditText editTextNumberOfHiddenNeurons = (EditText) findViewById(R.id.editText_Train_NumberOfHiddenNeurons);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup_Train_ActivationFunction);
        EditText editTextTrainFilePath = (EditText) findViewById(R.id.editText_Train_TrainFilePath);
        EditText editTextAttributeNamesFilePath = (EditText) findViewById(R.id.editText_Train_AttributeNamesFilePath);

        String elmName = editTextElmName.getText().toString();
        if (elmName.matches(""))
            elmName = null;

        String numberOfHiddenNeuronsText = editTextNumberOfHiddenNeurons.getText().toString();
        int numberOfHiddenNeurons;
        if (numberOfHiddenNeuronsText.matches(""))
            numberOfHiddenNeurons = 20;
        else
            numberOfHiddenNeurons = Integer.parseInt(numberOfHiddenNeuronsText);

        trainFilePath = editTextTrainFilePath.getText().toString();
        if (trainFilePath.matches(""))
            trainFilePath = null;

        attributeNamesFilePath = editTextAttributeNamesFilePath.getText().toString();
        if (attributeNamesFilePath.matches(""))
            attributeNamesFilePath = null;

        if (trainFilePath == null && attributeNamesFilePath != null)
        {
            editTextTrainFilePath.setError(getString(R.string.null_alert));
            editTextTrainFilePath.requestFocus();
        }
        else if (trainFilePath != null && attributeNamesFilePath == null)
        {
            editTextAttributeNamesFilePath.setError(getString(R.string.null_alert));
            editTextAttributeNamesFilePath.requestFocus();
        }
        else
        {
            editTextTrainFilePath.setError(null);
            editTextAttributeNamesFilePath.setError(null);

            if (trainFilePath == null)
                elmName = "diabetes";

            String func;
            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int index = radioGroup.indexOfChild(radioButton);
            RadioButton r = (RadioButton) radioGroup.getChildAt(index);
            String selectedText = r.getText().toString();
            if (selectedText.equals("Sigmoid"))
                func = "sig";
            else
                func = "sin";

            ElmTrain elmTrain = new ElmTrain(1, numberOfHiddenNeurons, func);
            try
            {
                elmTrain.train(elmName, trainFilePath, attributeNamesFilePath, this);
            }
            catch (NotConvergedException e)
            {
                e.printStackTrace();
            }

            Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
            intent.putExtra("Type", "Train");
            intent.putExtra("ElmName", elmName);
            intent.putExtra("Accuracy", elmTrain.getTrainingAccuracy());
            intent.putExtra("Time", elmTrain.getTrainingTime());

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if (requestCode == Constants.REQUEST_CHOOSE_TRAIN_FILE)
            {
                File trainFile = (File) data.getSerializableExtra("File");
                try
                {
                    trainFilePath = trainFile.getCanonicalPath();
                    EditText editTextTrainFilePath = (EditText) findViewById(R.id.editText_Train_TrainFilePath);
                    editTextTrainFilePath.setText(trainFilePath);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if (requestCode == Constants.REQUEST_CHOOSE_ATTRIBUTES_FILE)
            {
                File attFile = (File) data.getSerializableExtra("File");
                try
                {
                    attributeNamesFilePath = attFile.getCanonicalPath();
                    EditText editTextAttributeNamesFilePath = (EditText) findViewById(R.id.editText_Train_AttributeNamesFilePath);
                    editTextAttributeNamesFilePath.setText(attributeNamesFilePath);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
