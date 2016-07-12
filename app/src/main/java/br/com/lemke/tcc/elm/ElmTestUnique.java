package br.com.lemke.tcc.elm;

import android.content.Context;
import android.util.Log;

import br.com.lemke.tcc.util.ElmData;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;

/**
 * Created by lemke on 29/06/16.
 */
public class ElmTestUnique
{
    private DenseMatrix test_set;
    private int numTestData;
    private int NumberofInputNeurons;

    private DenseMatrix testP;
    private DenseMatrix testT;

    private DenseMatrix InputWeight;
    private DenseMatrix OutputWeight;
    private DenseMatrix BiasofHiddenNeurons;
    private int NumberofHiddenNeurons;
    private int NumberofOutputNeurons;
    private String func;
    private int Elm_Type;

    private String[] classesNames;

    private float TestingTime;
    private String TestingClass;
    private double TestingResult;

    public double[] test(double[] input, ElmData elmData)
    {
        test_set = new DenseMatrix(new DenseVector(input));

        InputWeight = elmData.getInputWeight();
        OutputWeight = elmData.getOutputWeight();
        BiasofHiddenNeurons = elmData.getBiasofHiddenNeurons();
        NumberofHiddenNeurons = elmData.getNumberofHiddenNeurons();
        NumberofOutputNeurons = elmData.getNumberofOutputNeurons();
        func = elmData.getFunc();
        Elm_Type = elmData.getElm_Type();
        classesNames = elmData.getClassesNames();

        Log.d("Unique", String.valueOf(InputWeight.numRows()));
        Log.d("Unique", String.valueOf(OutputWeight.numRows()));
        Log.d("Unique", String.valueOf(BiasofHiddenNeurons.numRows()));
        Log.d("Unique", String.valueOf(NumberofHiddenNeurons));
        Log.d("Unique", String.valueOf(NumberofOutputNeurons));

        return test();
    }

    // Output numTestData*NumberofOutputNeurons
    private double[] test()
    {
        DenseMatrix aux = test_set;
        test_set = new DenseMatrix(test_set.numColumns(), test_set.numRows());
        aux.transpose(test_set);
        numTestData = test_set.numRows();
        NumberofInputNeurons = test_set.numColumns() - 1;

        DenseMatrix ttestT = new DenseMatrix(numTestData, 1);
        DenseMatrix ttestP = new DenseMatrix(numTestData, NumberofInputNeurons);
        for (int i = 0; i < numTestData; i++)
        {
            ttestT.set(i, 0, test_set.get(i, 0));
            for (int j = 1; j <= NumberofInputNeurons; j++)
                ttestP.set(i, j - 1, test_set.get(i, j));
        }

        testT = new DenseMatrix(1, numTestData);
        testP = new DenseMatrix(NumberofInputNeurons, numTestData);
        ttestT.transpose(testT);
        ttestP.transpose(testP);
        // test_set.transpose(testP);

        long start_time_test = System.currentTimeMillis();

        DenseMatrix tempH_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);
        InputWeight.mult(testP, tempH_test);
        DenseMatrix BiasMatrix2 = new DenseMatrix(NumberofHiddenNeurons, numTestData);

        for (int j = 0; j < numTestData; j++)
        {
            for (int i = 0; i < NumberofHiddenNeurons; i++)
            {
                BiasMatrix2.set(i, j, BiasofHiddenNeurons.get(i, 0));
            }
        }

        tempH_test.add(BiasMatrix2);
        DenseMatrix H_test = new DenseMatrix(NumberofHiddenNeurons, numTestData);

        if (func.startsWith("sig"))
        {
            for (int j = 0; j < NumberofHiddenNeurons; j++)
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
            for (int j = 0; j < NumberofHiddenNeurons; j++)
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

        DenseMatrix transH_test = new DenseMatrix(numTestData, NumberofHiddenNeurons);
        H_test.transpose(transH_test);
        DenseMatrix Yout = new DenseMatrix(numTestData, NumberofOutputNeurons);
        transH_test.mult(OutputWeight, Yout);

        // DenseMatrix testY = new
        // DenseMatrix(NumberofOutputNeurons,numTestData);
        // Yout.transpose(testY);

        double[] result = new double[numTestData];

        if (Elm_Type == 0)
        {
            for (int i = 0; i < numTestData; i++)
                result[i] = Yout.get(i, 0);
        }

        else if (Elm_Type == 1)
        {
            for (int i = 0; i < numTestData; i++)
            {
                int tagmax = 0;
                double tagvalue = Yout.get(i, 0);
                for (int j = 1; j < NumberofOutputNeurons; j++)
                {
                    if (Yout.get(i, j) > tagvalue)
                    {
                        tagvalue = Yout.get(i, j);
                        tagmax = j;
                    }

                }
                result[i] = tagmax;
            }
        }

        long end_time_test = System.currentTimeMillis();
        TestingTime = (end_time_test - start_time_test) * 1.0f / 1000;

        if (Elm_Type == 1)
            TestingClass = classesNames[(int) result[0]]; // desnormalizar
        else
            TestingResult = result[0];
        return result;
    }

    public float getTestingTime()
    {
        return TestingTime;
    }

    public String getTestingClass()
    {
        return TestingClass;
    }

    public double getTestingResult()
    {
        return TestingResult;
    }
}
