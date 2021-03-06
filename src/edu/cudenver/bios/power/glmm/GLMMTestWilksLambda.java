/*
 * Java Statistics.  A java library providing power/sample size estimation for 
 * the general linear model.
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.cudenver.bios.power.glmm;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import edu.cudenver.bios.matrix.FixedRandomMatrix;

/**
 * Implementation of the Wilk's Lambda (WL) test for the
 * general linear multivariate model
 * 
 * @author Sarah Kreidler
 *
 */
public class GLMMTestWilksLambda extends GLMMTest
{
	private static final double TOLERANCE = 1.0E-15;
	/**
	 * Create a Wilk's Lambda test object for the specified parameters
	 * @param params GLMM input parameters
	 */
    public GLMMTestWilksLambda(FApproximation fMethod,
    		RealMatrix Xessence, RealMatrix XtXInverse, int perGroupN, int rank,
    		FixedRandomMatrix C, RealMatrix U, RealMatrix thetaNull, 
    		RealMatrix beta, RealMatrix sigmaError)
    {
        super(fMethod, Xessence, XtXInverse, perGroupN, rank,
        		C, U, thetaNull, beta, sigmaError);
    }
    
	/**
	 * Create a Wilks lambda test object for data analysis.  Used for
	 * simulation.
	 * @param params GLMM input parameters
	 */
    public GLMMTestWilksLambda(FApproximation fMethod,
    		RealMatrix X, RealMatrix XtXInverse, int rank, RealMatrix Y,
    		RealMatrix C, RealMatrix U, RealMatrix thetaNull)
    {
        super(fMethod, X, XtXInverse, rank, Y, C, U, thetaNull);
    }
    
    /**
     * Calculate the denominator degrees of freedom for the WL, based on
     * whether the null or alternative hypothesis is assumed true.  
     * 
     * @param type distribution type
     * @return denominator degrees of freedom
     * @throws IllegalArgumentException
     */
    @Override
    public double getDenominatorDF(DistributionType type)
    {        
        // a = #rows in between subject contrast matrix, C
        double a = C.getRowDimension();
        // b = #columns in within subject contrast matrix
        double b = U.getColumnDimension();
        
        double df = Double.NaN;
        
//        double gDenominator = (a*a + b*b - 5);
//        if (gDenominator == 0)
//            throw new IllegalArgumentException("Within and between subject contrasts yielded divide by zero: row of C=" + a + ", cols of U=" + b);
//        double g = Math.sqrt((a*a*b*b - 4) / gDenominator);
//        df = (g*((N - r) - (b - a +1)/2)) - (a*b - 2)/2;
        
        if (a*a*b*b <= 4)
        {
            df = totalN - rank - b + 1;
        }
        else
        {
            double gDenominator = (a*a + b*b - 5);
            if (gDenominator == 0)
                throw new IllegalArgumentException("Within and between subject contrasts yielded divide by zero: row of C=" + a + ", cols of U=" + b);
            double g = Math.sqrt((a*a*b*b - 4) / gDenominator);
            df = (g*((totalN - rank) - (b - a +1)/2)) - (a*b - 2)/2;
        }
        
        return df;
    }

    /**
     * Calculate the non-centrality parameter for the WL, based on
     * whether the null or alternative hypothesis is assumed true.  
     * 
     * @param type distribution type
     * @return non-centrality parameter
     * @throws IllegalArgumentException
     */
    @Override
    public double getNonCentrality(DistributionType type)
    {
        // calculate the hypothesis and error sum of squares matrices
        RealMatrix hypothesisSumOfSquares = getHypothesisSumOfSquares();
        RealMatrix errorSumOfSquares = getErrorSumOfSquares();
        
        // a = #rows in between subject contrast matrix, C
        double a = C.getRowDimension();
        // b = #columns in within subject contrast matrix, U
        double b = U.getColumnDimension();
        double s = (a < b) ? a : b;  
        double p = beta.getColumnDimension();
        
        double adjustedW = Double.NaN;
        double g = Double.NaN;
        double W = getWilksLambda(hypothesisSumOfSquares, errorSumOfSquares, type);
        if (a*a*b*b <= 4) 
        {
        	g = 1;
            adjustedW = W;
        }
        else
        {
            g = Math.sqrt((a*a*b*b - 4) / (a*a + b*b - 5));
            adjustedW = Math.pow(W, 1/g);
        }
        
        double omega;
        if ((s == 1 && p > 1) ||
                fMethod == FApproximation.RAO_TWO_MOMENT_OMEGA_MULT)
        {
            omega = totalN * g * (1 - adjustedW) / adjustedW;
        }
        else
        {
            omega = getDenominatorDF(type) * (1 - adjustedW) / adjustedW;
        }
        if (Math.abs(omega) < TOLERANCE) omega = 0;
        return Math.abs(omega);
    }

    /**
     * Calculate the numerator degrees of freedom for the WL, based on
     * whether the null or alternative hypothesis is assumed true.  
     * 
     * @param type distribution type
     * @return numerator degrees of freedom
     * @throws IllegalArgumentException
     */
    @Override
    public double getNumeratorDF(DistributionType type)
    {
        double a = C.getRowDimension();
        double b = U.getColumnDimension();

        return a*b;
    }

    /**
     * Calculate the observed F for the WL, based on
     * whether the null or alternative hypothesis is assumed true.  
     * 
     * @param type distribution type
     * @return observed F
     * @throws IllegalArgumentException
     */
    @Override
    public double getObservedF(DistributionType type)
    {
        // calculate the hypothesis and error sum of squares matrices
        RealMatrix hypothesisSumOfSquares = getHypothesisSumOfSquares();
        RealMatrix errorSumOfSquares = getErrorSumOfSquares();
        
        // a = #rows in between subject contrast matrix, C
        double a = C.getRowDimension();
        // b = #columns in within subject contrast matrix, U
        double b = U.getColumnDimension();
        
        double association = 0.0;
        
        double W = getWilksLambda(hypothesisSumOfSquares, errorSumOfSquares, type);
        if (a*a*b*b <= 4) 
        {
            association = 1 - W;
        }
        else
        {
            double g = Math.sqrt((a*a*b*b - 4) / (a*a + b*b - 5));
            association = 1 - Math.pow(W, 1/g);
        }
        
        double ddf = getDenominatorDF(type);
        double ndf = getNumeratorDF(type);
        return ((association) / ndf) / ((1 - association) / ddf);
    }
    
    /**
     * Compute a Wilks Lamba statistic
     * 
     * @param H hypothesis sum of squares matrix
     * @param E error sum of squares matrix
     * @returns F statistic
     */
    private double getWilksLambda(RealMatrix H, RealMatrix E, DistributionType type)
    throws IllegalArgumentException
    {
        if (!H.isSquare() || !E.isSquare() || H.getColumnDimension() != E.getRowDimension())
            throw new IllegalArgumentException("Failed to compute Wilks Lambda: hypothesis and error matrices must be square and same dimensions");
        
        double a = C.getRowDimension();
        double b = U.getColumnDimension();
        double s = (a < b) ? a : b;  
        double p = beta.getColumnDimension();
        
        RealMatrix adjustedH = H;
        if (type != DistributionType.DATA_ANALYSIS_NULL && ((s == 1 && p > 1) ||
                fMethod == FApproximation.RAO_TWO_MOMENT_OMEGA_MULT))
        {
            adjustedH = H.scalarMultiply((totalN - rank)/totalN);
        }
        
        RealMatrix T = adjustedH.add(E);
        RealMatrix inverseT = new LUDecomposition(T).getSolver().getInverse();

        RealMatrix EinverseT = E.multiply(inverseT);
        
        double lambda = new LUDecomposition(EinverseT).getDeterminant();
        return lambda;
    }
}
