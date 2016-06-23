package br.com.lemke.tcc.filemanipulation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public DenseMatrix importMatrixFromFile(Context context) throws IOException
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_train);
        reader = new BufferedReader(new InputStreamReader(inputStream));
        return readMatrix();
    }

    public DenseMatrix importMatrixFromFile(String path) throws IOException
    {
        File file = new File(path);
        if (!file.exists())
            throw new IOException();
        else
            reader = new BufferedReader(new FileReader(file));

        return readMatrix();
    }

    // the first line of dataset file must be the number of rows and columns,and
    // number of classes if neccessary
    // the first column is the norminal class value 0,1,2...
    // if the class value is 1,2...,number of classes should plus 1
    private DenseMatrix readMatrix() throws IOException
    {
        String firstlineString = reader.readLine();
        String[] strings = firstlineString.split(" ");
        int m = Integer.parseInt(strings[0]);
        int n = Integer.parseInt(strings[1]);
        if (strings.length > 2) numberofOutputNeurons = Integer.parseInt(strings[2]);

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

    public void exportElm(String elmName, ElmData elmData, File filesDir)
    {
        File ELMsDir = createELMsDir(filesDir);
        String folderCanonicalPath = createElmFolder(elmName, ELMsDir);

        exportElmProperties(elmName, elmData, folderCanonicalPath);
        exportElmMatrixes(elmName, elmData, folderCanonicalPath);
        exportElmAttributeNames(elmName, elmData, folderCanonicalPath);
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        Log.d("CreateElmFolder", "não criou");
        return null;
    }

    private void exportElmProperties(String elmName, ElmData elmData, String folderCanonicalPath)
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

            properties.store(bufferedWriter, "Elm properties");
            bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void exportElmMatrixes(String elmName, ElmData elmData, String folderCanonicalPath)
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
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void exportElmAttributeNames(String elmName, ElmData elmData, String folderCanonicalPath)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".att");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileAndPath));

            String[] attributeNames = elmData.getAttributeNames();
            for (int i = 0; i < attributeNames.length; i++)
            {
                bufferedWriter.write(attributeNames[i]);
                bufferedWriter.write(" ");
            }

            bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ElmData importElm(String elmName, File filesDir)
    {
        elmData = new ElmData();
        File ELMsDir = findELMsDir(filesDir);
        String folderCanonicalPath = findElmFolder(elmName, ELMsDir);
        importElmProperties(elmName, folderCanonicalPath);
        importElmMatrixes(elmName, folderCanonicalPath);
        importElmAttributeNames(elmName, folderCanonicalPath, false);
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

    private String findElmFolder(String elmName, File ELMsDir)
    {
        File folder = new File(ELMsDir, elmName);
        if (folder.exists())
            try
            {
                return folder.getCanonicalPath();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        Log.d("FindElmFolder", "não achou");
        return null;
    }

    private void importElmProperties(String elmName, String folderCanonicalPath)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".properties");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileAndPath));

            Properties properties = new Properties();
            properties.load(bufferedReader);

            elmData.setFunc(properties.getProperty("Func"));
            elmData.setNumberofInputNeurons(Integer.parseInt(properties.getProperty("NumberofInputNeurons")));
            elmData.setNumberofHiddenNeurons(Integer.parseInt(properties.getProperty("NumberofHiddenNeurons")));
            elmData.setNumberofOutputNeurons(Integer.parseInt(properties.getProperty("NumberofOutputNeurons")));

            bufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void importElmMatrixes(String elmName, String folderCanonicalPath)
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
            Log.d("matrix", type + " " + denseMatrix.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return denseMatrix;
    }

    public String[] importElmAttributeNames(String elmName, String folderCanonicalPath, boolean isReturn)
    {
        try
        {
            File fileAndPath = new File(folderCanonicalPath, elmName + ".att");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileAndPath));

            String line = bufferedReader.readLine();
            String[] attributeNames = line.split("\\s+");
            if (!isReturn)
                elmData.setAttributeNames(attributeNames);

            bufferedReader.close();

            return attributeNames;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String[] importAttributeNamesFromFile(String attributeNamesFilePath) throws IOException
    {
        File file = new File(attributeNamesFilePath);
        if (!file.exists())
            throw new IOException();
        reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        return line.split("\\s+");
    }

    public String[] importAttributeNamesFromFile(Context context) throws IOException
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.diabetes_attributenames);
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line = reader.readLine();
        return line.split("\\s+");
    }

    public File[] getTrainedElms(Context context)
    {
        File ELMsDir = findELMsDir(context.getFilesDir());
        return ELMsDir.listFiles();
    }

    public String getElmFolderCanonicalPath(String elmName, Context context) throws IOException
    {
        File ELMsDir = findELMsDir(context.getFilesDir());
        for (File file : ELMsDir.listFiles())
            if (file.getName().equals(elmName))
                return file.getCanonicalPath();
        return null;
    }
}
