package br.com.lemke.tcc.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import br.com.lemke.tcc.elmsecond.R;
import no.uib.cipr.matrix.DenseMatrix;

/**
 * Created by matheuslemke on 13/05/16.
 */
public class FileManipulation
{
    private int numberofOutputNeurons;
    private BufferedReader reader = null;
    private ElmData elmData;

    public FileManipulation()
    {
        numberofOutputNeurons = 1;
    }

    public DenseMatrix importMatrixFromFile(Context context, int elm_Type) throws IOException
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_train);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        return readMatrix(elm_Type);
    }

    public DenseMatrix importMatrixToTest(Context context, int elm_Type) throws IOException
    {
        // TODO: arquivo de teste por padrão
        //InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_test);
        InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_test);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        return readMatrix(elm_Type);
    }

    public DenseMatrix importMatrixFromFile(String path, int elm_Type) throws IOException
    {
        File file = new File(path);
        if (!file.exists())
            throw new IOException();
        else
            reader = new BufferedReader(new FileReader(file));

        return readMatrix(elm_Type);
    }

    // the first line of dataset file must be the number of rows and columns,and
    // number of classes if neccessary
    // the first column is the norminal class value 0,1,2...
    // if the class value is 1,2...,number of classes should plus 1
    private DenseMatrix readMatrix(int elm_Type) throws IOException
    {
        String firstlineString = reader.readLine();
        String[] strings = firstlineString.split(" ");
        int m = Integer.parseInt(strings[0]);
        int n = Integer.parseInt(strings[1]);
        if (elm_Type == 1)
            numberofOutputNeurons = Integer.parseInt(strings[2]);

        DenseMatrix matrix = new DenseMatrix(m, n);

        firstlineString = reader.readLine();
        int i = 0;
        while (i < m)
        {
            String[] datatrings = firstlineString.split(" ");
            for (int j = 0; j < n; j++)
            {
                matrix.set(i, j, Double.parseDouble(datatrings[j]));
            }
            i++;
            firstlineString = reader.readLine();
        }
        /*
         * for(int ii = 0; ii<m; ii++) matrix.add(ii, 0, -1);
		 */

        reader.close();

        return matrix;
    }

    public int getNumberofOutputNeurons()
    {
        return numberofOutputNeurons;
    }

    public void export(String elmName, ElmData elmData, File filesDir)
    {
        File ELMsDir = createELMsDir(filesDir);
        String folderCanonicalPath = createElmFolder(elmName, ELMsDir);

        exportProperties(elmName, elmData, folderCanonicalPath);
        exportMatrixes(elmData, folderCanonicalPath);
        exportAttributesClassesNames(elmName, elmData, folderCanonicalPath);
    }

    private File createELMsDir(File filesDir)
    {
        File folder = new File(filesDir, "ELMs");
        boolean successOnCreateFolder = true;
        if (!folder.exists())
            successOnCreateFolder = folder.mkdirs();
        if (successOnCreateFolder)
            return folder;
        Log.d("CreateELMsDir", "não criou");
        return null;
    }

    private String createElmFolder(String elmName, File ELMsDir)
    {
        File folder = new File(ELMsDir, elmName);
        boolean successOnCreateFolder = true;
        if (!folder.exists())
            successOnCreateFolder = folder.mkdirs();
        if (successOnCreateFolder)
            try
            {
                return folder.getCanonicalPath();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        Log.d("CreateElmFolder", "não criou");
        return null;
    }

    private void exportProperties(String elmName, ElmData elmData, String folderCanonicalPath)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".properties");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileAndPath));

            Properties properties = new Properties();
            properties.setProperty("NumberofInputNeurons", String.valueOf(elmData.getNumberofInputNeurons()));
            properties.setProperty("NumberofHiddenNeurons", String.valueOf(elmData.getNumberofHiddenNeurons()));
            properties.setProperty("NumberofOutputNeurons", String.valueOf(elmData.getNumberofOutputNeurons()));
            properties.setProperty("Func", elmData.getFunc());
            properties.setProperty("Elm_Type", String.valueOf(elmData.getElm_Type()));
            properties.setProperty("N_Attributes", String.valueOf(elmData.getN_Attributes()));

            properties.store(bufferedWriter, "Elm properties");
            bufferedWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void exportMatrixes(ElmData elmData, String folderCanonicalPath)
    {
        exportDenseMatrix(elmData.getInputWeight(), "in", folderCanonicalPath);
        exportDenseMatrix(elmData.getBiasofHiddenNeurons(), "bias", folderCanonicalPath);
        exportDenseMatrix(elmData.getOutputWeight(), "out", folderCanonicalPath);
    }

    /**
     * @param type in -> Input; bias -> Bias; out -> Output
     */
    private void exportDenseMatrix(DenseMatrix denseMatrix, String type, String folderCanonicalPath)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, type + ".matrix");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileAndPath));

            int nRows = denseMatrix.numRows(), nColumns = denseMatrix.numColumns();
            for (int i = 0; i < nRows; i++)
            {
                for (int j = 0; j < nColumns; j++)
                {
                    bufferedWriter.write(String.valueOf(denseMatrix.get(i, j)));
                    bufferedWriter.write(" ");
                }
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void exportAttributesClassesNames(String elmName, ElmData elmData, String folderCanonicalPath)
    {
        try
        {
            //a from attributes and c from classes
            File fileAndPath = new File(folderCanonicalPath, elmName + ".ac");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileAndPath));

            String[] attributeNames = elmData.getAttributesNames();
            for (int i = 0; i < attributeNames.length; i++)
            {
                bufferedWriter.write(attributeNames[i]);
                bufferedWriter.newLine();
            }

            if (elmData.getElm_Type() == 1)
            {
                bufferedWriter.newLine();
                String[] classesNames = elmData.getClassesNames();
                for (int i = 0; i < classesNames.length; i++)
                {
                    bufferedWriter.append(classesNames[i]);
                    bufferedWriter.newLine();
                }
            }

            bufferedWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ElmData importToTest(String elmName, File filesDir)
    {
        elmData = new ElmData();
        File ELMsDir = findELMsDir(filesDir);
        String folderCanonicalPath = findFolder(elmName, ELMsDir);
        importProperties(elmName, folderCanonicalPath, false);
        importMatrixes(elmName, folderCanonicalPath);
        importAttributesClassesNamesToTest(elmName, folderCanonicalPath, elmData.getN_Attributes(), elmData.getElm_Type(), elmData.getNumberofOutputNeurons(), false);
        return elmData;
    }

    private File findELMsDir(File filesDir)
    {
        File folder = new File(filesDir, "ELMs");
        if (folder.exists())
            return folder;
        Log.d("FindElmFolder", "não achou");
        return null;
    }

    private String findFolder(String elmName, File ELMsDir)
    {
        File folder = new File(ELMsDir, elmName);
        if (folder.exists())
            try
            {
                return folder.getCanonicalPath();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        Log.d("FindElmFolder", "não achou");
        return null;
    }

    public Properties importProperties(String elmName, String folderCanonicalPath, boolean isReturn)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".properties");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileAndPath));

            Properties properties = new Properties();
            properties.load(bufferedReader);

            if (!isReturn)
            {
                elmData.setFunc(properties.getProperty("Func"));
                elmData.setElm_Type(Integer.parseInt(properties.getProperty("Elm_Type")));
                elmData.setNumberofInputNeurons(Integer.parseInt(properties.getProperty("NumberofInputNeurons")));
                elmData.setNumberofHiddenNeurons(Integer.parseInt(properties.getProperty("NumberofHiddenNeurons")));
                elmData.setNumberofOutputNeurons(Integer.parseInt(properties.getProperty("NumberofOutputNeurons")));
                elmData.setN_Attributes(Integer.parseInt(properties.getProperty("N_Attributes")));
            }
            bufferedReader.close();
            return properties;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void importMatrixes(String elmName, String folderCanonicalPath)
    {
        int nHiddenNeurons = elmData.getNumberofHiddenNeurons();
        elmData.setBiasofHiddenNeurons(importDenseMatrix(elmName, folderCanonicalPath, "bias", nHiddenNeurons, 1));
        elmData.setInputWeight(importDenseMatrix(elmName, folderCanonicalPath, "in", nHiddenNeurons, elmData.getNumberofInputNeurons()));
        elmData.setOutputWeight(importDenseMatrix(elmName, folderCanonicalPath, "out", nHiddenNeurons, elmData.getNumberofOutputNeurons()));
    }

    /**
     * @param type in -> Input; bias -> Bias; out -> Output
     */
    private DenseMatrix importDenseMatrix(String filename, String folderCanonicalPath, String type, int nRows, int nColumns)
    {
        DenseMatrix denseMatrix = null;
        try
        {
            File fileAndPath = new File(folderCanonicalPath, type + ".matrix");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileAndPath));

            double[][] data = new double[nRows][nColumns];

            for (int i = 0; i < nRows; i++)
            {
                String[] numbers = bufferedReader.readLine().split(" ");
                for (int j = 0; j < nColumns; j++)
                    data[i][j] = Double.parseDouble(numbers[j]);
            }

            denseMatrix = new DenseMatrix(data);

            bufferedReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return denseMatrix;
    }

    public String[][] importAttributesClassesNamesToTest(String elmName, String folderCanonicalPath, int n_Attributes, int elm_Type, int n_Classes, boolean isReturn)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".ac");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileAndPath));

            String[] attributesNames = new String[n_Attributes];
            String[] classesNames = null;
            for (int i = 0; i < n_Attributes; i++)
            {
                attributesNames[i] = bufferedReader.readLine();
                if (((i + 1) == n_Attributes) && (elm_Type == 1))
                {
                    bufferedReader.readLine();
                    classesNames = new String[n_Classes];
                    for (int j = 0; j < n_Classes; j++)
                        classesNames[j] = bufferedReader.readLine();
                }
            }

            if (!isReturn)
            {
                elmData.setAttributesNames(attributesNames);
                elmData.setClassesNames(classesNames);
            }

            bufferedReader.close();

            return new String[][]{attributesNames, classesNames};
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String[][] importAttributesClassesNamesToTrain(String attributeNamesFilePath, int n_Attributes, int elm_Type, int n_Classes) throws IOException
    {
        File file = new File(attributeNamesFilePath);
        if (!file.exists())
            throw new IOException();
        reader = new BufferedReader(new FileReader(file));

        return importAttributesClassesNamesToTrain(n_Attributes, elm_Type, n_Classes);
    }

    public String[][] importAttributesClassesNamesToTrain(Context context, int n_Attributes, int elm_Type, int n_Classes) throws IOException
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_attributes_classes_names);
        reader = new BufferedReader(new InputStreamReader(inputStream));

        return importAttributesClassesNamesToTrain(n_Attributes, elm_Type, n_Classes);
    }

    private String[][] importAttributesClassesNamesToTrain(int n_Attributes, int elm_Type, int n_Classes) throws IOException
    {
        String[] attributes = new String[n_Attributes];
        String[] classes = null;

        for (int i = 0; i < n_Attributes; i++)
        {
            attributes[i] = reader.readLine();
            if (((i + 1) == n_Attributes) && (elm_Type == 1))
            {
                reader.readLine();
                classes = new String[n_Classes];
                for (int j = 0; j < n_Classes; j++)
                    classes[j] = reader.readLine();
            }
        }

        return new String[][]{attributes, classes};
    }

    public File[] getTrainedElms(Context context)
    {
        File ELMsDir = findELMsDir(context.getFilesDir());
        return ELMsDir.listFiles();
    }

    public String getFolderCanonicalPath(String elmName, Context context) throws IOException
    {
        File ELMsDir = findELMsDir(context.getFilesDir());
        for (File file : ELMsDir.listFiles())
            if (file.getName().equals(elmName))
                return file.getCanonicalPath();
        return null;
    }

    public boolean createELMTestFolder()
    {
        File elmTestFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.ELM_NAME);
        return elmTestFolder.mkdirs();
    }

    public String createELMTestFolderFunction(String func)
    {
        File elmTestFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.ELM_NAME);
        if (elmTestFolder.exists())
        {
            String elmFolderCanonicalPath = null;
            try
            {
                elmFolderCanonicalPath = elmTestFolder.getCanonicalPath();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            File folderFunction = new File(elmFolderCanonicalPath, func);
            if (folderFunction.mkdirs())
                try
                {
                    return folderFunction.getCanonicalPath();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public String createELMTestFolderNeurons(String func, String n_neurons)
    {
        File elmTestFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Constants.ELM_NAME);
        if (elmTestFolder.exists())
        {
            String elmFolderCanonicalPath = null;
            try
            {
                elmFolderCanonicalPath = elmTestFolder.getCanonicalPath();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            File folderFunction = new File(elmFolderCanonicalPath, func);
            if (folderFunction.exists())
            {
                String folderFunctionCanonicalPath = null;
                try
                {
                    folderFunctionCanonicalPath = folderFunction.getCanonicalPath();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                File folderNeurons = new File(folderFunctionCanonicalPath, n_neurons);
                if (folderNeurons.mkdirs())
                    try
                    {
                        return folderNeurons.getCanonicalPath();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
            }
        }
        return null;
    }

    public void writeOnTestFile(String elmTest_function_neurons_folderCanonicalPath, ArrayList<Double> accuracies, ArrayList<Float> trainTimes, ArrayList<Float> testTimes) throws IOException
    {
        File file = new File(elmTest_function_neurons_folderCanonicalPath, Constants.ELM_NAME + ".ac");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Double accuracy : accuracies)
        {
            bufferedWriter.write(String.valueOf(accuracy));
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        file = new File(elmTest_function_neurons_folderCanonicalPath, Constants.ELM_NAME + ".te");
        bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Float time : testTimes)
        {
            bufferedWriter.write(String.valueOf(time));
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        file = new File(elmTest_function_neurons_folderCanonicalPath, Constants.ELM_NAME + ".tr");
        bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Float time : trainTimes)
        {
            bufferedWriter.write(String.valueOf(time));
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
