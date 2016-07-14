package br.com.lemke.tcc.elm;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import br.com.lemke.tcc.util.FileManipulation;
import br.com.lemke.tcc.util.ElmData;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.NotConvergedException;

/**
 * Created by matheuslemke on 13/05/16.
 */
public class ElmTrain
{
    private DenseMatrix train_set;
    private int numTrainData;
    private DenseMatrix InputWeight;
    private int numberofHiddenNeurons;
    private int numberofOutputNeurons; // also the number of classes
    private int numberofInputNeurons; // also the number of attribution
    private int elm_Type;
    private String func;
    private int[] label;

    private DenseMatrix BiasofHiddenNeurons;
    private DenseMatrix OutputWeight;
    private DenseMatrix T;
    private DenseMatrix Y;
    private float TrainingTime;
    private double TrainingAccuracy;

    public ElmTrain(int elm_type, int numberofHiddenNeurons, String ActivationFunction)
    {

        elm_Type = elm_type;
        this.numberofHiddenNeurons = numberofHiddenNeurons;
        func = ActivationFunction;

        TrainingTime = 0;
        TrainingAccuracy = 0;
    }

    public void train(String elmName, String trainFilePath, String attributesClassesNamesFilePath, int elm_Type, Context context) throws NotConvergedException
    {
        FileManipulation fm = new FileManipulation();
        String[] attributesNames;
        String[] classesNames = null;
        String[][] attributesClassesNames;
        this.elm_Type = elm_Type;
        try
        {
            if (trainFilePath == null)
            {
                this.train_set = fm.importMatrixFromFile(context, elm_Type);
                attributesClassesNames = fm.importAttributesClassesNamesToTrain(context, train_set.numColumns() - 1, elm_Type, fm.getNumberofOutputNeurons());
            } else
            {
                this.train_set = fm.importMatrixFromFile(trainFilePath, elm_Type);
                attributesClassesNames = fm.importAttributesClassesNamesToTrain(attributesClassesNamesFilePath, train_set.numColumns() - 1, elm_Type, fm.getNumberofOutputNeurons());
            }

            attributesNames = attributesClassesNames[0];
            if (elm_Type == 1)
                classesNames = attributesClassesNames[1];

            // NÃO ESQUECER DE FAZER ISSO AQUI
            this.numberofOutputNeurons = fm.getNumberofOutputNeurons();

            //Log.d("train", "Carregou");
            train();
            exportElm(elmName, attributesNames, classesNames, elm_Type, context.getFilesDir());
            //Log.d("train", "Exportou");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void exportElm(String elmName, String[] attributesNames, String[] classesNames, int elm_Type, File filesDir)
    {
        FileManipulation fileManipulation = new FileManipulation();

        ElmData elmData = new ElmData(elmName, numberofInputNeurons, numberofHiddenNeurons, numberofOutputNeurons,
                InputWeight, BiasofHiddenNeurons, OutputWeight, func, elm_Type, attributesNames, classesNames, attributesNames.length);


        fileManipulation.export(elmName, elmData, filesDir);
    }

    private void train() throws NotConvergedException
    {

        numTrainData = train_set.numRows();
        numberofInputNeurons = train_set.numColumns() - 1;
        InputWeight = (DenseMatrix) Matrices.random(numberofHiddenNeurons, numberofInputNeurons);

        DenseMatrix transT = new DenseMatrix(numTrainData, 1);
        DenseMatrix transP = new DenseMatrix(numTrainData, numberofInputNeurons);
        for (int i = 0; i < numTrainData; i++)
        {
            transT.set(i, 0, train_set.get(i, 0));
            for (int j = 1; j <= numberofInputNeurons; j++)
                transP.set(i, j - 1, train_set.get(i, j));
        }
        T = new DenseMatrix(1, numTrainData);
        DenseMatrix P = new DenseMatrix(numberofInputNeurons, numTrainData);
        transT.transpose(T);
        transP.transpose(P);

        if (elm_Type != 0) //CLASSIFIER
        {
            // LABEL SEMPRE É O MESMO. ENTÃO PODE SER GERADO NO TESTE
            label = new int[numberofOutputNeurons];
            for (int i = 0; i < numberofOutputNeurons; i++)
            {
                label[i] = i;                            //class label starts form 0
            }
            DenseMatrix tempT = new DenseMatrix(numberofOutputNeurons, numTrainData);
            tempT.zero();
            for (int i = 0; i < numTrainData; i++)
            {
                int j = 0;
                for (j = 0; j < numberofOutputNeurons; j++)
                {
                    if (label[j] == T.get(0, i)) break;
                }
                tempT.set(j, i, 1);
            }

            T = new DenseMatrix(numberofOutputNeurons, numTrainData);    // T=temp_T*2-1;
            for (int i = 0; i < numberofOutputNeurons; i++)
            {
                for (int j = 0; j < numTrainData; j++)
                    T.set(i, j, tempT.get(i, j) * 2 - 1);
            }

            transT = new DenseMatrix(numTrainData, numberofOutputNeurons);
            T.transpose(transT);

        }    //end if CLASSIFIER

        long start_time_train = System.currentTimeMillis();
        // Random generate input weights InputWeight (w_i) and biases BiasofHiddenNeurons (b_i) of hidden neurons
        // InputWeight=rand(numberofHiddenNeurons,numberofInputNeurons)*2-1;

        BiasofHiddenNeurons = (DenseMatrix) Matrices.random(numberofHiddenNeurons, 1);

        DenseMatrix tempH = new DenseMatrix(numberofHiddenNeurons, numTrainData);
        InputWeight.mult(P, tempH);
        //DenseMatrix ind = new DenseMatrix(1, numTrainData);

        DenseMatrix BiasMatrix = new DenseMatrix(numberofHiddenNeurons, numTrainData);

        for (int j = 0; j < numTrainData; j++)
        {
            for (int i = 0; i < numberofHiddenNeurons; i++)
            {
                BiasMatrix.set(i, j, BiasofHiddenNeurons.get(i, 0));
            }
        }

        tempH.add(BiasMatrix);
        DenseMatrix H = new DenseMatrix(numberofHiddenNeurons, numTrainData);

        if (func.startsWith("sig"))
        {
            for (int j = 0; j < numberofHiddenNeurons; j++)
            {
                for (int i = 0; i < numTrainData; i++)
                {
                    double temp = tempH.get(j, i);
                    temp = 1.0f / (1 + Math.exp(-temp));
                    H.set(j, i, temp);
                }
            }
        } else if (func.startsWith("sin"))
        {
            for (int j = 0; j < numberofHiddenNeurons; j++)
            {
                for (int i = 0; i < numTrainData; i++)
                {
                    double temp = tempH.get(j, i);
                    temp = Math.sin(temp);
                    H.set(j, i, temp);
                }
            }
        } else if (func.startsWith("hardlim"))
        {
            //If you need it ,you can absolutely complete it yourself
        } else if (func.startsWith("tribas"))
        {
            //If you need it ,you can absolutely complete it yourself
        } else if (func.startsWith("radbas"))
        {
            //If you need it ,you can absolutely complete it yourself
        }

        DenseMatrix Ht = new DenseMatrix(numTrainData, numberofHiddenNeurons);
        H.transpose(Ht);
        Inverse invers = new Inverse(Ht);
        DenseMatrix pinvHt = invers.getMPInverse();            //numberofHiddenNeurons*numTrainData
        //DenseMatrix pinvHt = invers.getMPInverse(0.000001); //fast method, PLEASE CITE in your paper properly:
        //Guang-Bin Huang, Hongming Zhou, Xiaojian Ding, and Rui Zhang, "Extreme Learning Machine for Regression and Multi-Class Classification," submitted to IEEE Transactions on Pattern Analysis and Machine Intelligence, October 2010.

        OutputWeight = new DenseMatrix(numberofHiddenNeurons, numberofOutputNeurons);
        //OutputWeight=pinv(H') * T';
        pinvHt.mult(transT, OutputWeight);

        long end_time_train = System.currentTimeMillis();
        TrainingTime = (end_time_train - start_time_train) * 1.0f / 1000;

        DenseMatrix Yt = new DenseMatrix(numTrainData, numberofOutputNeurons);
        Ht.mult(OutputWeight, Yt);
        Y = new DenseMatrix(numberofOutputNeurons, numTrainData);
        Yt.transpose(Y);

        if (elm_Type == 0)
        {
            double MSE = 0;
            for (int i = 0; i < numTrainData; i++)
            {
                MSE += (Yt.get(i, 0) - transT.get(i, 0)) * (Yt.get(i, 0) - transT.get(i, 0));
            }
            TrainingAccuracy = Math.sqrt(MSE / numTrainData);
        }

        //CLASSIFIER
        else if (elm_Type == 1)
        {
            float MissClassificationRate_Training = 0;

            for (int i = 0; i < numTrainData; i++)
            {
                double maxtag1 = Y.get(0, i);
                int tag1 = 0;
                double maxtag2 = T.get(0, i);
                int tag2 = 0;
                for (int j = 1; j < numberofOutputNeurons; j++)
                {
                    if (Y.get(j, i) > maxtag1)
                    {
                        maxtag1 = Y.get(j, i);
                        tag1 = j;
                    }
                    if (T.get(j, i) > maxtag2)
                    {
                        maxtag2 = T.get(j, i);
                        tag2 = j;
                    }
                }
                if (tag1 != tag2) MissClassificationRate_Training++;
            }
            TrainingAccuracy = 1 - MissClassificationRate_Training * 1.0f / numTrainData;
        }
    }

    public double getTrainingAccuracy()
    {
        return TrainingAccuracy;
    }

    public float getTrainingTime()
    {
        return TrainingTime;
    }
}
