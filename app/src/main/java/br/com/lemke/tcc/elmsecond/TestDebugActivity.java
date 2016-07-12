package br.com.lemke.tcc.elmsecond;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import br.com.lemke.tcc.elm.ElmTestFile;
import br.com.lemke.tcc.elm.ElmTrain;
import br.com.lemke.tcc.util.Constants;
import br.com.lemke.tcc.util.FileManipulation;
import no.uib.cipr.matrix.NotConvergedException;

public class TestDebugActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_debug);
    }

    public void buttonTrainTestDefaultClick(View view)
    {
        ElmTrain elmTrain;
        ElmTestFile elmTestFile;
        FileManipulation fileManipulation = new FileManipulation();
        ArrayList<Double> accuracies;
        ArrayList<Float> trainTimes;
        ArrayList<Float> testTimes;

        if (fileManipulation.createELMTestFolder())
            for (int functionID = 0; functionID < Constants.FUNCTIONS.length; functionID++)
            {
                fileManipulation.createELMTestFolderFunction(Constants.FUNCTIONS[functionID]);
                for (int numberOfHiddenNeurons = Constants.MIN_NUMBER_OF_HIDDEN_NEURONS;
                     numberOfHiddenNeurons < Constants.MAX_NUMBER_OF_HIDDEN_NEURONS + 10;
                     numberOfHiddenNeurons += 10)
                {
                    accuracies = new ArrayList<>(Constants.N_EXECUTIONS);
                    trainTimes = new ArrayList<>(Constants.N_EXECUTIONS);
                    testTimes = new ArrayList<>(Constants.N_EXECUTIONS);

                    for (int execID = 0; execID < Constants.N_EXECUTIONS; execID++)
                    {
                        Log.d("Testes" + Constants.ELM_NAME, "Function = " + Constants.FUNCTIONS[functionID]);
                        Log.d("Testes" + Constants.ELM_NAME, "Neurons = " + numberOfHiddenNeurons);
                        Log.d("Testes" + Constants.ELM_NAME, "Exec" + execID);
                        elmTrain = new ElmTrain(Constants.ELM_TYPE, numberOfHiddenNeurons, Constants.FUNCTIONS[functionID]);
                        try
                        {
                            elmTrain.train(Constants.ELM_NAME, null, null, Constants.ELM_TYPE, this);
                            trainTimes.add(elmTrain.getTrainingTime());
                            System.out.println(Constants.ELM_NAME + "TrainingTime: " + elmTrain.getTrainingTime());

                            elmTestFile = new ElmTestFile();
                            elmTestFile.test(Constants.ELM_NAME, null, this);

                            accuracies.add(elmTestFile.getTestingAccuracy());
                            testTimes.add(elmTestFile.getTestingTime());

                            System.out.println(Constants.ELM_NAME + "Accuracy: " + elmTestFile.getTestingAccuracy());
                            System.out.println(Constants.ELM_NAME + "TestingTime: " + elmTestFile.getTestingTime());
                            System.out.println("---------------------------------------------------------------");
                        }
                        catch (NotConvergedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("---------------------------------------------------------------");
                    try
                    {
                        fileManipulation.writeOnTestFile(
                                fileManipulation.createELMTestFolderNeurons(Constants.FUNCTIONS[functionID],
                                        String.valueOf(numberOfHiddenNeurons)), accuracies, trainTimes, testTimes);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    accuracies.clear();
                    trainTimes.clear();
                    testTimes.clear();
                }
                System.out.println("---------------------------------------------------------------");
            }
        else Log.d("Testes" + Constants.ELM_NAME, "Don't created directory " + Constants.ELM_NAME);

        Log.d("Testes" + Constants.ELM_NAME, "TESTS ENDED");
/*
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        intent.putExtra("Type", "Test");
        intent.putExtra("ElmName", Constants.ELM_NAME);
        intent.putExtra("Accuracy", elmTestFile.getTestingAccuracy());
        intent.putExtra("Time", elmTestFile.getTestingTime());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        */

        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        intent.putExtra("Type", "Test");
        intent.putExtra("ElmName", Constants.ELM_NAME);
        intent.putExtra("Accuracy", 0d);
        intent.putExtra("Time", 0f);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
