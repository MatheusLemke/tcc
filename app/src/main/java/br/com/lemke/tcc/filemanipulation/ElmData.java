package br.com.lemke.tcc.filemanipulation;

import no.uib.cipr.matrix.DenseMatrix;

/**
 * Created by lemke on 22/05/16.
 */
public class ElmData
{
    private String elmName;
    private int numberofInputNeurons;
    private int numberofHiddenNeurons;
    private int numberofOutputNeurons;
    private DenseMatrix InputWeight;
    private DenseMatrix BiasofHiddenNeurons;
    private DenseMatrix OutputWeight;
    private String func;
    private String[] AttributeNames;

    public ElmData()
    {
    }

    public ElmData(String elmName, int numberofInputNeurons, int numberofHiddenNeurons, int numberofOutputNeurons, DenseMatrix InputWeight, DenseMatrix BiasofHiddenNeurons, DenseMatrix OutputWeight, String func, String[] AttributeNames)
    {
        this.numberofInputNeurons = numberofInputNeurons;
        this.numberofHiddenNeurons = numberofHiddenNeurons;
        this.numberofOutputNeurons = numberofOutputNeurons;
        this.InputWeight = InputWeight;
        this.BiasofHiddenNeurons = BiasofHiddenNeurons;
        this.OutputWeight = OutputWeight;
        this.func = func;
        this.AttributeNames = AttributeNames;
        this.elmName = elmName;
    }

    public int getNumberofInputNeurons()
    {
        return numberofInputNeurons;
    }

    public void setNumberofInputNeurons(int numberofInputNeurons)
    {
        this.numberofInputNeurons = numberofInputNeurons;
    }

    public int getNumberofHiddenNeurons()
    {
        return numberofHiddenNeurons;
    }

    public void setNumberofHiddenNeurons(int numberofHiddenNeurons)
    {
        this.numberofHiddenNeurons = numberofHiddenNeurons;
    }

    public int getNumberofOutputNeurons()
    {
        return numberofOutputNeurons;
    }

    public void setNumberofOutputNeurons(int numberofOutputNeurons)
    {
        this.numberofOutputNeurons = numberofOutputNeurons;
    }

    public DenseMatrix getInputWeight()
    {
        return InputWeight;
    }

    public void setInputWeight(DenseMatrix inputWeight)
    {
        InputWeight = inputWeight;
    }

    public DenseMatrix getBiasofHiddenNeurons()
    {
        return BiasofHiddenNeurons;
    }

    public void setBiasofHiddenNeurons(DenseMatrix biasofHiddenNeurons)
    {
        BiasofHiddenNeurons = biasofHiddenNeurons;
    }

    public String getFunc()
    {
        return func;
    }

    public void setFunc(String func)
    {
        this.func = func;
    }

    public DenseMatrix getOutputWeight()
    {
        return OutputWeight;
    }

    public void setOutputWeight(DenseMatrix outputWeight)
    {
        OutputWeight = outputWeight;
    }

    public String[] getAttributeNames()
    {
        return AttributeNames;
    }

    public void setAttributeNames(String[] attributeNames)
    {
        AttributeNames = attributeNames;
    }

    public String getElmName()
    {
        return elmName;
    }

    public void setElmName(String elmName)
    {
        this.elmName = elmName;
    }
}
