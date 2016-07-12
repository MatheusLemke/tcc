package br.com.lemke.tcc.elm;

/**
 * Created by matheuslemke on 13/05/16.
 */
/*
 * This library is free software;
 * The original version is a matlab programmer,I rewrote it in Java
 * The original Authors: MR QIN-YU ZHU AND DR GUANG-BIN HUANG,
 * The original WEBSITE: http://www.ntu.edu.sg/eee/icis/cv/egbhuang.htm
 * */

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import br.com.lemke.tcc.util.FileManipulation;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.io.MatrixVectorWriter;

public class Elm
{
    private DenseMatrix train_set;
    private DenseMatrix test_set;
    private int numTrainData;
    private int numTestData;
    private DenseMatrix InputWeight;
    private float TrainingTime, TestingTime;
    private double TrainingAccuracy, TestingAccuracy;
    private int Elm_Type;
    private int NumberofHiddenNeurons;
    private int NumberofOutputNeurons; // also the number of classes
    private int NumberofInputNeurons; // also the number of attribution
    private String func;
    private int[] label;
    // this class label employ a lazy and easy method,any class must written in
    // 0,1,2...so the preprocessing is required

    // the blow variables in both train() and test()
    private DenseMatrix BiasofHiddenNeurons;
    private DenseMatrix OutputWeight;
    private DenseMatrix testP;
    private DenseMatrix testT;
    private DenseMatrix Y;
    private DenseMatrix T;

    /**
     * Construct an ELM
     *
     * @param elm_type              - 0 for regression; 1 for (both binary and multi-classes)
     *                              classification
     * @param numberofHiddenNeurons - Number of hidden neurons assigned to the ELM
     * @param ActivationFunction    - Type of activation function: 'sig' for Sigmoidal function
     *                              'sin' for Sine function 'hardlim' for Hardlim function
     *                              'tribas' for Triangular basis function 'radbas' for Radial
     *                              basis function (for additive type of SLFNs instead of RBF type
     *                              of SLFNs)
     * @throws NotConvergedException
     */

    public Elm(int elm_type, int numberofHiddenNeurons, String ActivationFunction)
    {

        Elm_Type = elm_type;
        NumberofHiddenNeurons = numberofHiddenNeurons;
        func = ActivationFunction;

        TrainingTime = 0;
        TestingTime = 0;
        TrainingAccuracy = 0;
        TestingAccuracy = 0;
        NumberofOutputNeurons = 1;

    }

    public Elm()
    {

    }


    // Salva InputWeight, BiasofHiddenNeurons, OutputWeight
    public void exportWeights(String filename, Context context)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            MatrixVectorWriter mvw = new MatrixVectorWriter(fos);
            mvw.printArray(InputWeight.getData());
            mvw.printArray(BiasofHiddenNeurons.getData());
            mvw.printArray(OutputWeight.getData());
            mvw.close();
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void importWeights(String filename)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename + "_return")));
            MatrixVectorWriter mvw = new MatrixVectorWriter(writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



    public double[] testOut(double[][] inpt)
    {
        test_set = new DenseMatrix(inpt);
        return testOut();
    }

    public double[] testOut(double[] inpt)
    {
        test_set = new DenseMatrix(new DenseVector(inpt));
        return testOut();
    }

    // Output numTestData*NumberofOutputNeurons
    private double[] testOut()
    {
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
        return result;
    }

    public float getTrainingTime()
    {
        return TrainingTime;
    }

    public double getTrainingAccuracy()
    {
        return TrainingAccuracy;
    }

    public float getTestingTime()
    {
        return TestingTime;
    }

    public double getTestingAccuracy()
    {
        return TestingAccuracy;
    }

    public int getNumberofInputNeurons()
    {
        return NumberofInputNeurons;
    }

    public int getNumberofHiddenNeurons()
    {
        return NumberofHiddenNeurons;
    }

    public int getNumberofOutputNeurons()
    {
        return NumberofOutputNeurons;
    }

    public DenseMatrix getInputWeight()
    {
        return InputWeight;
    }

    public DenseMatrix getBiasofHiddenNeurons()
    {
        return BiasofHiddenNeurons;
    }

    public DenseMatrix getOutputWeight()
    {
        return OutputWeight;
    }

    // for predicting a data file based on a trained model.
    public void testgetoutput(String filename, Context context) throws IOException
    {
        FileManipulation fm = new FileManipulation();
        /*
        try
        {
            test_set = fm.importMatrixFromFile(filename, context);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
        numTestData = test_set.numRows();
        NumberofInputNeurons = test_set.numColumns() - 1;

        double rsum = 0;
        double[] actual = new double[numTestData];

        double[][] data = new double[numTestData][NumberofInputNeurons];
        for (int i = 0; i < numTestData; i++)
        {
            actual[i] = test_set.get(i, 0);
            for (int j = 0; j < NumberofInputNeurons; j++)
                data[i][j] = test_set.get(i, j + 1);
        }

        double[] output = testOut(data);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("Output")));
        for (int i = 0; i < numTestData; i++)
        {

            writer.write(String.valueOf(output[i]));
            writer.newLine();

            if (Elm_Type == 0)
            {
                rsum += (output[i] - actual[i]) * (output[i] - actual[i]);
            }

            if (Elm_Type == 1)
            {
                if (output[i] == actual[i]) rsum++;
            }

        }
        writer.flush();
        writer.close();

        if (Elm_Type == 0)
            System.out.println("Regression GetOutPut RMSE: " + Math.sqrt(rsum * 1.0f / numTestData));
        else if (Elm_Type == 1)
            System.out.println("Classfy GetOutPut Right: " + rsum * 1.0f / numTestData);
    }

}
