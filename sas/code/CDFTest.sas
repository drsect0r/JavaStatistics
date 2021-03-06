/* Test case for non-centrality distribution - for comparison
* with the Java Statistics library
*/

PROC IML SYMSIZE=2000 WORKSIZE=6000;
RESET SPACES=4;
%INCLUDE "common.sas";
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\BASEPA1.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\BASEPE1.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\BASEQ1.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\POWUA1.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\POWHA1.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\QLIB01.IML"/NOSOURCE2;
%INCLUDE "&GLUECK_MULLER_IML_DIRECTORY\BASEU4.IML"/NOSOURCE2;


X_WITH_COVARIATE = {1 0 0 9.949629083027475,
1 0 0 10.36470049131072,
1 0 0 11.360042563286273,
1 0 0 9.561945394957043,
1 0 0 8.894045051023681,
0 1 0 8.204900590858518,
0 1 0 7.558819636114959,
0 1 0 11.429090558422853,
0 1 0 9.535811826259893,
0 1 0 8.764062170141981,
0 0 1 10.989129470161636,
0 0 1 8.614065713117894,
0 0 1 7.555128339710249,
0 0 1 8.612569781619342,
0 0 1 10.388511083771677};

/* design matrix which corresponds with Java non-centrality 
* cdf test */
X={1 0 0 ,
   1 0 0 ,
   1 0 0 ,
   1 0 0 ,
   1 0 0 ,
   0 1 0 ,
   0 1 0 ,
   0 1 0 ,
   0 1 0 ,
   0 1 0 ,
   0 0 1 ,
   0 0 1 ,
   0 0 1 ,
   0 0 1 ,
   0 0 1 };

*ESSENCEF = X[*,1:3];
ESSENCEF = X_WITH_COVARIATE[*,1:3];
ESSENCEF = {1 0 0, 0 1 0, 0 0 1};
* get number of observations;
SIGMAYY={1 0,
		 0 1};
SIGMAYG={0.9,
		 0};
SIGMAGG={1.0};
PRINT SIGMAYY SIGMAYG SIGMAGG;

SIGMAE=SIGMAYY-SIGMAYG*INV(SIGMAGG)*SIGMAYG`;
BETAG=INV(SIGMAGG)*SIGMAYG`;
PRINT SIGMAE BETAG;

CF={1 -1 0, 
    1 0 -1};
CG={1, 1};
C= CF||CG;
RANKX=NCOL(C);
PRINT CG C;
A=NROW(C);
Q=NCOL(C);

U=I(2);
B=NCOL(U);

ALPHA={.05};

*Assume pure treatment-gender interaction;
ESSBETAF={1 0,
          0 0,
          0 0};
THETA0 = J(A,B,0);

H_OR_U="H";
VARG=SIGMAGG;
SIGSTAR=U`*SIGMAE*U;

MAXPDIFF=.001;
MAXITER=50;
PROBLIST={0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1.0};

DO REPN=5 TO 5 BY 1;
  N=REPN#NROW(ESSENCEF);
  BETA = (ESSBETAF)//BETAG;
  PRINT BETA;
  THETA = C*BETA*U;
  PRINT C U;
  PRINT THETA;
  THETAD=THETA-THETA0;
  DO IPROB=1 TO NCOL(PROBLIST);
    /* Inverse cdf of non-centrality distribution */
    RUN BASEQ1("A",H_OR_U,ESSENCEF,REPN,CF,CG,VARG,THETAD,SIGSTAR,
                 PROBLIST[IPROB],MAXITER,MAXPDIFF, ITERE,QA,FE);
	QUANTILES=QUANTILES//(PROBLIST[IPROB]||QA);
	/* Quantile power */
	APOWERAQ=APOWH2(A,B,N,RANKX,ALPHA,QA);
    POWER=POWER//(QA||APOWERAQ);

  END;
  DO I=1 to 14 by 1;
  	/* */
	APROB = BASEPA1(I,H_OR_U,ESSENCEF,REPN,CF,CG,VARG,THETAD,SIGSTAR);
	HOLDPROB = HOLDPROB//(I||APROB);
  end;
  PROBNAMES = {"Critical Value" "Cumulative Probability"};
  PRINT HOLDPROB[COLNAME=PROBNAMES];
  NAMES = {"Quantile" "InverseCDF"};
  PRINT QUANTILES[COLNAME=NAMES];
  PRINT POWER;
END;

QUIT;

