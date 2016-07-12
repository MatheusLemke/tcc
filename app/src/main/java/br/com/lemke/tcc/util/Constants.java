package br.com.lemke.tcc.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.lemke.tcc.elmsecond.R;

/**
 * Created by lemke on 13/06/16.
 */
public class Constants
{
    public static final boolean isTestDebug = true;

    public static final int REQUEST_CHOOSE_TRAIN_FILE = 0;
    public static final int REQUEST_CHOOSE_ATTRIBUTES_FILE = 1;
    public static final int REQUEST_CHOOSE_TEST_FILE = 2;
    public static final int REQUEST_WRITE_STORAGE = 3;

    // Constants for tests
    public static final int N_EXECUTIONS = 30;


    public static final int MIN_NUMBER_OF_HIDDEN_NEURONS = 40;
    public static final int MAX_NUMBER_OF_HIDDEN_NEURONS = 50;
    public static String[] FUNCTIONS = {"sig", "sin"};

    /* Mudar de BASE
    public DenseMatrix importMatrixFromFile(Context context, int elm_Type) throws IOException
    public DenseMatrix importMatrixToTest(Context context, int elm_Type) throws IOException
    public String[][] importAttributesClassesNamesToTrain(Context context, int n_Attributes, int elm_Type, int n_Classes) throws IOException
     */
    public static final int ELM_TYPE = 1;
    public static final String ELM_NAME = "Liver";


}
