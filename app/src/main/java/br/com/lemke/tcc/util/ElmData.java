package br.com.lemke.tcc.util;

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
    private int elm_Type;
    private String[] AttributesNames;
    private String[] ClassesNames;
    private int n_Attributes;

    public ElmData()
    {
    }

    public ElmData(String elmName, int numberofInputNeurons, int numberofHiddenNeurons, int numberofOutputNeurons, DenseMatrix InputWeight, DenseMatrix BiasofHiddenNeurons, DenseMatrix OutputWeight, String func, int elm_Type, String[] AttributesNames, String[] ClassesNames, int n_Attributes)
    {
        this.elmName = elmName;
        this.numberofInputNeurons = numberofInputNeurons;
        this.numberofHiddenNeurons = numberofHiddenNeurons;
        this.numberofOutputNeurons = numberofOutputNeurons;
        this.InputWeight = InputWeight;
        this.BiasofHiddenNeurons = BiasofHiddenNeurons;
        this.OutputWeight = OutputWeight;
        this.func = func;
        this.elm_Type = elm_Type;
        this.AttributesNames = AttributesNames;
        this.ClassesNames = ClassesNames;
        this.n_Attributes = n_Attributes;
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

    public String[] getAttributesNames()
    {
        return AttributesNames;
    }

    public void setAttributesNames(String[] attributesNames)
    {
        AttributesNames = attributesNames;
    }

    public String getElmName()
    {
        return elmName;
    }

    public void setElmName(String elmName)
    {
        this.elmName = elmName;
    }

    public int getElm_Type()
    {
        return elm_Type;
    }

    public void setElm_Type(int elm_Type)
    {
        this.elm_Type = elm_Type;
    }

    public String[] getClassesNames()
    {
        return ClassesNames;
    }

    public void setClassesNames(String[] classesNames)
    {
        ClassesNames = classesNames;
    }

    public int getN_Attributes()
    {
        return n_Attributes;
    }

    public void setN_Attributes(int n_Attributes)
    {
        this.n_Attributes = n_Attributes;
    }
}
