package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.com.lemke.tcc.util.Constants;
import br.com.lemke.tcc.elm.ElmTrain;
import no.uib.cipr.matrix.NotConvergedException;

public class TrainActivity extends AppCompatActivity
{
    private String attributesClassesNamesFilePath;
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
        RadioGroup radioGroupType = (RadioGroup) findViewById(R.id.radioGroup_Train_Type);
        RadioGroup radioGroupFunction = (RadioGroup) findViewById(R.id.radioGroup_Train_ActivationFunction);
        EditText editTextNumberOfHiddenNeurons = (EditText) findViewById(R.id.editText_Train_NumberOfHiddenNeurons);
        EditText editTextTrainFilePath = (EditText) findViewById(R.id.editText_Train_TrainFilePath);
        EditText editTextAttributesClassesNamesFilePath = (EditText) findViewById(R.id.editText_Train_AttributesClassesNamesFilePath);

        assert editTextElmName != null;
        String elmName = editTextElmName.getText().toString();
        if (elmName.matches(""))
            elmName = null;

        assert editTextNumberOfHiddenNeurons != null;
        String numberOfHiddenNeuronsText = editTextNumberOfHiddenNeurons.getText().toString();
        int numberOfHiddenNeurons;
        if (numberOfHiddenNeuronsText.matches(""))
            numberOfHiddenNeurons = 20;
        else
            numberOfHiddenNeurons = Integer.parseInt(numberOfHiddenNeuronsText);

        assert editTextTrainFilePath != null;
        trainFilePath = editTextTrainFilePath.getText().toString();
        if (trainFilePath.matches(""))
            trainFilePath = null;

        assert editTextAttributesClassesNamesFilePath != null;
        attributesClassesNamesFilePath = editTextAttributesClassesNamesFilePath.getText().toString();
        if (attributesClassesNamesFilePath.matches(""))
            attributesClassesNamesFilePath = null;

        if (trainFilePath == null && attributesClassesNamesFilePath != null)
        {
            editTextTrainFilePath.setError(getString(R.string.null_alert));
            editTextTrainFilePath.requestFocus();
        }
        else if (trainFilePath != null && attributesClassesNamesFilePath == null)
        {
            editTextAttributesClassesNamesFilePath.setError(getString(R.string.null_alert));
            editTextAttributesClassesNamesFilePath.requestFocus();
        }
        else
        {
            editTextTrainFilePath.setError(null);
            editTextAttributesClassesNamesFilePath.setError(null);

            if (trainFilePath == null)
                elmName = Constants.ELM_NAME;

            String func;
            assert radioGroupFunction != null;
            int radioButtonFunctionID = radioGroupFunction.getCheckedRadioButtonId();
            View radioButtonFunction = radioGroupFunction.findViewById(radioButtonFunctionID);
            int indexFunction = radioGroupFunction.indexOfChild(radioButtonFunction);
            RadioButton rFunction = (RadioButton) radioGroupFunction.getChildAt(indexFunction);
            String functionName = rFunction.getText().toString();
            if (functionName.equals("Sigmoid"))
                func = "sig";
            else
                func = "sin";

            int elm_Type;
            assert radioGroupType != null;
            int radioButtonTypeID = radioGroupType.getCheckedRadioButtonId();
            View radioButtonType = radioGroupType.findViewById(radioButtonTypeID);
            int indexType = radioGroupType.indexOfChild(radioButtonType);
            RadioButton rType = (RadioButton) radioGroupType.getChildAt(indexType);
            String type = rType.getText().toString();

            if (type.equals("Multiclassification"))
                elm_Type = 1;
            else elm_Type = 0;

            ElmTrain elmTrain = new ElmTrain(elm_Type, numberOfHiddenNeurons, func);
            try
            {
                elmTrain.train(elmName, trainFilePath, attributesClassesNamesFilePath, elm_Type, this);
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
                trainFilePath = (String) data.getSerializableExtra("FileCanonicalPath");
                EditText editTextTrainFilePath = (EditText) findViewById(R.id.editText_Train_TrainFilePath);
                assert editTextTrainFilePath != null;
                editTextTrainFilePath.setText(trainFilePath);
            }
            else if (requestCode == Constants.REQUEST_CHOOSE_ATTRIBUTES_FILE)
            {
                attributesClassesNamesFilePath = (String) data.getSerializableExtra("FileCanonicalPath");
                EditText editTextAttributeNamesFilePath = (EditText) findViewById(R.id.editText_Train_AttributesClassesNamesFilePath);
                assert editTextAttributeNamesFilePath != null;
                editTextAttributeNamesFilePath.setText(attributesClassesNamesFilePath);
            }
        }
    }
}
