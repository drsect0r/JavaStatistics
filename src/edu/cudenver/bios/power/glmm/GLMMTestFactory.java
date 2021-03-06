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

import org.apache.commons.math3.linear.RealMatrix;

import edu.cudenver.bios.matrix.FixedRandomMatrix;
import edu.cudenver.bios.power.PowerErrorEnum;
import edu.cudenver.bios.power.PowerException;
import edu.cudenver.bios.power.glmm.GLMMTest.FApproximation;
import edu.cudenver.bios.power.glmm.GLMMTest.UnivariateCdfApproximation;
import edu.cudenver.bios.power.glmm.GLMMTest.UnivariateEpsilonApproximation;

/**
 * Factory class for generating a GLMM test object
 *
 * @see GLMMTest
 * @author Sarah Kreidler
 *
 */
public class GLMMTestFactory {
    // the type of statistical test to use
    public enum Test {
        UNIREP,
        UNIREP_BOX,
        UNIREP_GEISSER_GREENHOUSE,
        UNIREP_HUYNH_FELDT,
        WILKS_LAMBDA,
        PILLAI_BARTLETT_TRACE,
        HOTELLING_LAWLEY_TRACE
    };

    public static GLMMTest createGLMMTestForDataAnalysis(Test test,
            FApproximation fMethod, UnivariateCdfApproximation cdfMethod,
            UnivariateEpsilonApproximation epsilonMethod,
            RealMatrix X, RealMatrix XtXinverse, int rank,
            RealMatrix Y, RealMatrix C, RealMatrix U, RealMatrix thetaNull) {
        switch (test) {
        case UNIREP:
            return new GLMMTestUnivariateRepeatedMeasures(fMethod, cdfMethod, epsilonMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case UNIREP_BOX:
            return new GLMMTestUnirepBox(fMethod, cdfMethod, epsilonMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case UNIREP_GEISSER_GREENHOUSE:
            return new GLMMTestUnirepGeisserGreenhouse(fMethod, cdfMethod, epsilonMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case UNIREP_HUYNH_FELDT:
            return new GLMMTestUnirepHuynhFeldt(fMethod, cdfMethod, epsilonMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case WILKS_LAMBDA:
            return new GLMMTestWilksLambda(fMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case HOTELLING_LAWLEY_TRACE:
            return new GLMMTestHotellingLawley(fMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        case PILLAI_BARTLETT_TRACE:
            return new GLMMTestPillaiBartlett(fMethod,
                    X, XtXinverse, rank, Y, C, U, thetaNull);
        default:
            throw new IllegalArgumentException("Unknown GLMM test statistic");
        }
    }

    public static GLMMTest createGLMMTestForPower(Test test,
                FApproximation fMethod, UnivariateCdfApproximation cdfMethod,
                UnivariateEpsilonApproximation epsilonMethod,
                RealMatrix Xessence, RealMatrix XtXInverse, int perGroupN, int rank,
                FixedRandomMatrix C, RealMatrix U, RealMatrix thetaNull,
                RealMatrix beta, RealMatrix sigmaError, int nuForEstimatedSigma)
            throws PowerException {
        try {
            switch (test) {
            case UNIREP:
                return new GLMMTestUnivariateRepeatedMeasures(fMethod, cdfMethod, epsilonMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError, nuForEstimatedSigma);
            case UNIREP_BOX:
                return new GLMMTestUnirepBox(fMethod, cdfMethod, epsilonMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError, nuForEstimatedSigma);
            case UNIREP_GEISSER_GREENHOUSE:
                return new GLMMTestUnirepGeisserGreenhouse(fMethod, cdfMethod, epsilonMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError, nuForEstimatedSigma);
            case UNIREP_HUYNH_FELDT:
                return new GLMMTestUnirepHuynhFeldt(fMethod, cdfMethod, epsilonMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError, nuForEstimatedSigma);
            case WILKS_LAMBDA:
                return new GLMMTestWilksLambda(fMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError);
            case HOTELLING_LAWLEY_TRACE:
                return new GLMMTestHotellingLawley(fMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError);
            case PILLAI_BARTLETT_TRACE:
                return new GLMMTestPillaiBartlett(fMethod,
                        Xessence, XtXInverse, perGroupN, rank, C, U, thetaNull,
                        beta, sigmaError);
            default:
                throw new PowerException("Unknown GLMM test statistic",
                        PowerErrorEnum.UNKNOWN_TEST_REQUESTED);
            }
        } catch (GLMMTest.GLMMTestException | GLMMTestUnivariateRepeatedMeasures.NoHdlssSupportException e) {
            throw new PowerException(e.getMessage(), PowerErrorEnum.GLMMTEST_CONSTRUCTION_FAILED);
        }
    }
}
