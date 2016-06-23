package br.com.lemke.tcc.elm;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import br.com.lemke.tcc.filemanipulation.ElmData;
import br.com.lemke.tcc.filemanipulation.FileManipulation;
import no.uib.cipr.matrix.DenseMatrix;

/**
 * Created by lemke on 22/05/16.
 */
public class ElmTest
{
    private DenseMatrix Test_set;
    private int numTestData;
    private int numberofHiddenNeurons;
    private int numberofOutputNeurons; // also the number of classes
    private int numberofInputNeurons; // also the number of attribution
    private DenseMatrix InputWeight;
    private String func;
    private int[] label;
    private int elm_Type;

    private DenseMatrix BiasofHiddenNeurons;
    private DenseMatrix TestP;
    private DenseMatrix TestT;
    private DenseMatrix OutputWeight;
    private float TestingTime;
    private double TestingAccuracy;

    public ElmTest()
    {
        TestingTime = 0;
        TestingAccuracy = 0;
    }

    private void importElm(FileManipulation fm, String elmName, File filesDir)
    {
        ElmData elmData = fm.importElm(elmName, filesDir);

        numberofOutputNeurons = elmData.getNumberofOutputNeurons();
        BiasofHiddenNeurons = elmData.getBiasofHiddenNeurons();
        func = elmData.getFunc();
        InputWeight = elmData.getInputWeight();
        numberofHiddenNeurons = elmData.getNumberofHiddenNeurons();
        OutputWeight = elmData.getOutputWeight();
        label = initializeLabel();
    }

    private int[] initializeLabel()
    {
        int[] label = new int[numberofOutputNeurons];
        for (int i = 0; i < numberofOutputNeurons; i++)
            label[i] = i;                            //class label starts form 0
        return label;
    }

    public void test(String elmName, double[][] data, Context context)
    {
        FileManipulation fileManipulation = new FileManipulation();
        importElm(fileManipulation, elmName, context.getFilesDir());
        Log.d("test", "Importou " + elmName);

        Test_set = new DenseMatrix(data);
        test();
    }

    public void test(String elmName, String testFilePath, Context context)
    {
        FileManipulation fileManipulation = new FileManipulation();
        importElm(fileManipulation, elmName, context.getFilesDir());
        Log.d("test", "Importou " + elmName);

        try
        {
            if (elmName.equals("diabetes") && testFilePath == null)
                Test_set = fileManipulation.importMatrixFromFile(context);
            else
                Test_set = fileManipulation.importMatrixFromFile(testFilePath);
            Log.d("test", "Importou matriz de teste");
            test();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void test()
    {
        numTestData = Test_set.numRows();
        numberofInputNeurons = Test_set.numColumns() - 1;

        DenseMatrix ttestT = new DenseMatrix(numTestData, 1);
        DenseMatrix ttestP = new DenseMatrix(numTestData, numberofInputNeurons);
        for (int i = 0; i < numTestData; i++)
        {
            ttestT.set(i, 0, Test_set.get(i, 0));
            for (int j = 1; j <= numberofInputNeurons; j++)
                ttestP.set(i, j - 1, Test_set.get(i, j));
        }

        TestT = new DenseMatrix(1, numTestData);
        TestP = new DenseMatrix(numberofInputNeurons, numTestData);
        ttestT.transpose(TestT);
        ttestP.transpose(TestP);

        long start_time_test = System.currentTimeMillis();
        DenseMatrix tempH_test = new DenseMatrix(numberofHiddenNeurons, numTestData);
        InputWeight.mult(TestP, tempH_test);
        DenseMatrix BiasMatrix2 = new DenseMatrix(numberofHiddenNeurons, numTestData);

        for (int j = 0; j < numTestData; j++)
        {
            for (int i = 0; i < numberofHiddenNeurons; i++)
            {
                BiasMatrix2.set(i, j, BiasofHiddenNeurons.get(i, 0));
            }
        }

        tempH_test.add(BiasMatrix2);
        DenseMatrix H_test = new DenseMatrix(numberofHiddenNeurons, numTestData);

        if (func.startsWith("sig"))
        {
            for (int j = 0; j < numberofHiddenNeurons; j++)
            {
                for (int i = 0; i < numTestData; i++)
                {
                    double temp = tempH_test.get(j, i);
                    temp = 1.0f / (1 + Math.exp(-temp));
                    H_test.set(j, i, temp);
                }
            }
        }
        else if (func.startsWith("sin"))
        {
            for (int j = 0; j < numberofHiddenNeurons; j++)
            {
                for (int i = 0; i < numTestData; i++)
                {
                    double temp = tempH_test.get(j, i);
                    temp = Math.sin(temp);
                    H_test.set(j, i, temp);
                }
            }
        }
        else if (func.startsWith("hardlim"))
        {

        }
        else if (func.startsWith("tribas"))
        {

        }
        else if (func.startsWith("radbas"))
        {

        }

        DenseMatrix transH_test = new DenseMatrix(numTestData, numberofHiddenNeurons);
        H_test.transpose(transH_test);
        DenseMatrix Yout = new DenseMatrix(numTestData, numberofOutputNeurons);
        transH_test.mult(OutputWeight, Yout);

        DenseMatrix testY = new DenseMatrix(numberofOutputNeurons, numTestData);
        Yout.transpose(testY);

        long end_time_test = System.currentTimeMillis();
        TestingTime = (end_time_test - start_time_test) * 1.0f / 1000;

        // REGRESSION
        if (elm_Type == 0)
        {
            double MSE = 0;
            for (int i = 0; i < numTestData; i++)
            {
                MSE += (Yout.get(i, 0) - TestT.get(0, i)) * (Yout.get(i, 0) - TestT.get(0, i));
            }
            TestingAccuracy = Math.sqrt(MSE / numTestData);
        }

        // CLASSIFIER
        else if (elm_Type == 1)
        {

            DenseMatrix temptestT = new DenseMatrix(numberofOutputNeurons, numTestData);
            for (int i = 0; i < numTestData; i++)
            {
                int j = 0;
                for (j = 0; j < numberofOutputNeurons; j++)
                {
                    if (label[j] == TestT.get(0, i)) break;
                }
                temptestT.set(j, i, 1);
            }

            TestT = new DenseMatrix(numberofOutputNeurons, numTestData);
            for (int i = 0; i < numberofOutputNeurons; i++)
            {
                for (int j = 0; j < numTestData; j++)
                    TestT.set(i, j, temptestT.get(i, j) * 2 - 1);
            }

            float MissClassificationRate_Testing = 0;

            for (int i = 0; i < numTestData; i++)
            {
                double maxtag1 = testY.get(0, i);
                int tag1 = 0;
                double maxtag2 = TestT.get(0, i);
                int tag2 = 0;
                for (int j = 1; j < numberofOutputNeurons; j++)
                {
                    if (testY.get(j, i) > maxtag1)
                    {
                        maxtag1 = testY.get(j, i);
                        tag1 = j;
                    }
                    if (TestT.get(j, i) > maxtag2)
                    {
                        maxtag2 = TestT.get(j, i);
                        tag2 = j;
                    }
                }
                if (tag1 != tag2) MissClassificationRate_Testing++;
            }
            TestingAccuracy = 1 - MissClassificationRate_Testing * 1.0f / numTestData;

        }
        Log.d("test", "testou");
    }

    public float getTestingTime()
    {
        return TestingTime;
    }

    public double getTestingAccuracy()
    {
        return TestingAccuracy;
    }
}
