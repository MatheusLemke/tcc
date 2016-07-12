package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.com.lemke.tcc.util.Constants;
import br.com.lemke.tcc.elm.ElmTestFile;
import br.com.lemke.tcc.view.TrainedElmsDialog;

public class TestFileActivity extends AppCompatActivity implements TrainedElmsDialog.TrainedElmsListener
{
    private String testFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_test_file);
    }

    public void buttonChooseELM(View view)
    {
        TrainedElmsDialog trainedElmsDialog = new TrainedElmsDialog();
        trainedElmsDialog.show(getFragmentManager(), "Trained ELMs");
    }

    public void buttonChooseTestFile(View view)
    {
        Intent intent = new Intent(this, FileDialogActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CHOOSE_TEST_FILE);
    }

    public void imageButtonRunTestClick(View view)
    {
        EditText editTextElmName = (EditText) findViewById(R.id.editText_TestFile_ELMName);
        EditText editTextTestFilePath = (EditText) findViewById(R.id.editText_TestFile_TestFilePath);

        editTextElmName.setError(null);
        editTextTestFilePath.setError(null);

        String elmName = editTextElmName.getText().toString();
        if (elmName.matches(""))
            elmName = null;

        testFilePath = editTextTestFilePath.getText().toString();
        if (testFilePath.matches(""))
            testFilePath = null;

        if (elmName == null)
        {
            editTextElmName.setError(getString(R.string.choose_elm_alert));
            editTextElmName.requestFocus();
        }
        else if (testFilePath == null)
        {
            editTextTestFilePath.setError(getString(R.string.null_alert));
            editTextTestFilePath.requestFocus();
        }
        else
        {
            ElmTestFile elmTestFile = new ElmTestFile();
            elmTestFile.test(elmName, testFilePath, getBaseContext());

            Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
            intent.putExtra("Type", "Test");
            intent.putExtra("ElmName", elmName);
            intent.putExtra("Accuracy", elmTestFile.getTestingAccuracy());
            intent.putExtra("Time", elmTestFile.getTestingTime());
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
            if (requestCode == Constants.REQUEST_CHOOSE_TEST_FILE)
            {
                testFilePath = (String) data.getSerializableExtra("FileCanonicalPath");
                EditText editTextTrainFilePath = (EditText) findViewById(R.id.editText_TestFile_TestFilePath);
                editTextTrainFilePath.setText(testFilePath);
            }
        }
    }

    @Override
    public void onElmChoosed(String elmName)
    {
        EditText editText = (EditText) findViewById(R.id.editText_TestFile_ELMName);
        editText.setText(elmName);
    }
}
