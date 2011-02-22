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
package edu.cudenver.bios.power.test.published;

import java.io.File;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import edu.cudenver.bios.matrix.DesignEssenceMatrix;
import edu.cudenver.bios.matrix.FixedRandomMatrix;
import edu.cudenver.bios.matrix.RowMetaData;
import edu.cudenver.bios.power.GLMMPowerCalculator;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters;
import edu.cudenver.bios.power.parameters.GLMMPowerParameters.ConfidenceIntervalType;
import edu.cudenver.bios.power.test.PowerChecker;
import junit.framework.TestCase;

/**
 * Unit test for fixed univariate design with comparison against
 * simulation and SAS output.
 * @author Sarah Kreidler
 *
 */
public class TestConditionalUnivariateWithConfidenceLimits extends TestCase
{
	private static final String DATA_FILE =  "sas" + File.separator + "data" + File.separator + "TestConditionalUnivariateWithConfidenceLimits.xml";
	private static final String OUTPUT_FILE = "text" + File.separator + "results" + File.separator + "TestConditionalUnivariateWithConfidenceLimits.html";
	private static final String TITLE = "Power results for fixed univariate with confidence limits";
	private PowerChecker checker;
	
	public void setUp()
	{
		try
		{
			checker = new PowerChecker(DATA_FILE, true);
		}
		catch (Exception e)
		{
			System.err.println("Setup failed: " + e.getMessage());
			fail();
		}
	}

    /**
     * Compare 2 sample t-test results between JavaStatistics, 
     * POWERLIB, and simulation
     */
    public void testUnviariateWithConfidenceLimits()
    {
        GLMMPowerParameters params = new GLMMPowerParameters();
        
        // add tests
        params.addTest(GLMMPowerParameters.Test.UNIREP);
        
        // add alpha values
        params.addAlpha(0.01);

        // build beta matrix
        double [][] beta = {{0},{1}};
        params.setBeta(new FixedRandomMatrix(beta, null, false));
        // add beta scale values
        for(double betaScale = 0; betaScale <= 0.75; betaScale += 0.01) params.addBetaScale(betaScale);
        
        // build theta null matrix
        double [][] theta0 = {{0}};
        params.setTheta(new Array2DRowRealMatrix(theta0));
        
        // build sigma matrix
        double [][] sigma = {{0.068}};
        params.setSigmaError(new Array2DRowRealMatrix(sigma));
        // add sigma scale values
        params.addSigmaScale(1);
        
        // build design matrix
        double[][] essenceData = {{1,0},{0,1}};
        RowMetaData[] rowMd = {new RowMetaData(1), new RowMetaData(1)};
        DesignEssenceMatrix essenceMatrix = new DesignEssenceMatrix(essenceData, rowMd, null, null);
        params.setDesignEssence(essenceMatrix);
        // add sample size multipliers
        params.addSampleSize(10);
        
        // build between subject contrast
        double [][] between = {{1,-1}};
        params.setBetweenSubjectContrast(new FixedRandomMatrix(between, null, true));

        // parameters for confidence limits
        params.setConfidenceIntervalType(ConfidenceIntervalType.BETA_KNOWN_SIGMA_ESTIMATED);
        params.setSampleSizeForEstimates(24);
        params.setDesignMatrixRankForEstimates(2);
        params.setAlphaLowerConfidenceLimit(0.025);
        params.setAlphaUpperConfidenceLimit(0.025);
        
        // run the test
        System.out.println(TITLE);
        checker.checkPower(params);
		checker.outputResults();
		checker.outputResults(TITLE, OUTPUT_FILE);
		assertTrue(checker.isSASDeviationBelowTolerance());
		assertTrue(checker.isSimulationDeviationBelowTolerance());
		checker.reset();
    }

}