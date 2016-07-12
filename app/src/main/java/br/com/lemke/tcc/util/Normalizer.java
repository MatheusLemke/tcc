package br.com.lemke.tcc.util;

import no.uib.cipr.matrix.DenseMatrix;

/**
 * Created by lemke on 29/06/16.
 */
public class Normalizer
{
    private double[] lowers;
    private double[] upers;

    public Normalizer(DenseMatrix input)
    {
        lowers = new double[input.numColumns()];
        upers = new double[input.numColumns()];

        for (int i = 0; i < input.numColumns(); i++)
        {
            lowers[i] = input.get(0, i);
            upers[i] = input.get(0, i);
        }

        for (int i = 0; i < input.numRows(); i++)
        {
            for (int j = 0; j < input.numColumns(); j++)
            {
                if (lowers[j] > input.get(i, j))
                    lowers[j] = input.get(i, j);
                if (upers[j] < input.get(i, j))
                    upers[j] = input.get(i, j);
            }
        }
    }

    public void normalize(DenseMatrix input)
    {
        double norm;
        for (int i = 0; i < input.numRows(); i++)
        {
            for (int j = 0; j < input.numColumns(); j++)
            {
                norm = (input.get(i, j) - lowers[j]) / (upers[j] - lowers[j]);
                input.set(i, j, norm);
            }
        }
    }
}
