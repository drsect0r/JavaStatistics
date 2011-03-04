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
/* 
* Conditional power for multivariate design with confidence limits 
* based on example 6 from POWERLIB (Johnson et al., 2009)
*/
TITLE "Conditional Power, Fixed Design, Multivariate with confidence limits";
%INCLUDE "common.sas";

LIBNAME DATA_DIR "&DATA_DIRECTORY";

PROC IML SYMSIZE=1000 WORKSIZE=2000;
%INCLUDE "&POWERLIB_IML_FILE"/NOSOURCE2;
%INCLUDE "XMLUTILITIES.IML"/NOSOURCE2;

* set output options ;
OPT_OFF={WARN COLLAPSE};
OPT_ON={UN BOX HF GG WLK PBT HLT ORTHU NOPRINT}; 

* create power inputs ;
RNM={ANT LEFT POST RIGHT};
ALPHA = .05/6;

SIGMA = {0.08380 0.05020 0.03560 0.05330,
		 0.05020 0.05370 0.03250 0.03330,                         
         0.03560 0.03250 0.04410 0.03860,                          
         0.05330 0.03330 0.03860 0.07220};

ESSENCEX = I(10);
REPN={2,4,8};

P = 4;
Q = 2;
* Pattern of means for Gender by Region *;
BETARG= J(2,4, 3.2) + {.30}#({ -1  0 1  0,
                               -1  0 1  0}) ;	
PRINT BETARG[COLNAME=RNM];
BETASCAL={1};

* contrasts;
C = {1 -1} @ J(1,5,1);

REGION = {1,2,3,4};
RUN UPOLY1(REGION,"REGION",U1,REGU);
U=U1;

* Output full precision ;
ROUND = 15;

* Describe dataset from whence the estimates came;
* needed to construct CI's *;
CLTYPE = 1;     *Estimated variance only, with fixed means*;
N_EST = 21;     *# Obs for variance estimate*;
RANK_EST = 1;   *# Model DF for study giving variance estimate*;
ALPHA_CL = .025;   	*Lower confidence limit tail size*;
ALPHA_CU = .025;   	*Upper confidence limit tail size*;

FREE HOLDALL;
DO DELTA=0 TO .20 BY .0008;
  * Creation of Beta matrix based on varying Gender differences *;
  BETARGD=BETARG + (J(2,2,0)||(DELTA//(-DELTA))||J(2,1,0));	
  * Final Beta matrix with age groups added *;
  BETA= BETARGD @ J(5,1,1) ;
print beta;
  RUN POWER;
  HOLDALL=HOLDALL//( _HOLDPOWER||J(NROW(_HOLDPOWER),1,DELTA) );
END;

NAMES = _HOLDPOWERLBL || "DELTA";
print (NCOL(HOLDALL)) (NROW(HOLDALL));

/* write the data to an XML file 
* note - we compute all of the powers to keep holdpower aligned
* but only output GG 
*/
TEST_LIST = {'unirepGG'};
filename out "&DATA_DIRECTORY\TestConditionalMultivariateWithConfidenceLimits.xml";
RUN powerAndCIResultsToXML(out, holdall, TEST_LIST, 0);

QUIT;
