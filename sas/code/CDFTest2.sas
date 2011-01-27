PROC IML;

/* Test case for non-centrality distribution - for comparison
* with the Java Statistics library
*/

PROC IML SYMSIZE=2000 WORKSIZE=6000;
RESET SPACES=4;
%INCLUDE "..\IML\BASEPA1.IML"/NOSOURCE2;
%INCLUDE "..\IML\BASEPE1.IML"/NOSOURCE2;
%INCLUDE "..\IML\BASEQ1.IML"/NOSOURCE2;
%INCLUDE "..\IML\POWUA1.IML"/NOSOURCE2;
%INCLUDE "..\IML\POWHA1.IML"/NOSOURCE2;
%INCLUDE "..\IML\QLIB01.IML"/NOSOURCE2;
%INCLUDE "..\IML\BASEU4.IML"/NOSOURCE2;

XBONE = {1 0 0 9.949629083027475,
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

*Design matrix observed in bone density pilot data;
/* XBONE={1 0 0 0 0.750,
       1 0 0 0 0.874,
       1 0 0 0 0.730,
       1 0 0 0 0.878,
       1 0 0 0 0.830,
       0 1 0 0 0.719,
       0 1 0 0 0.592,
       0 1 0 0 0.821,
       0 1 0 0 0.622,
       0 1 0 0 0.705,
       0 0 1 0 0.532,
       0 0 1 0 0.891,
       0 0 1 0 0.799,
	   0 0 1 0 0.786,	
	   0 0 1 0 0.885,
	   0 0 0 1 0.675,
	   0 0 0 1 0.834,
	   0 0 0 1 0.786,
	   0 0 0 1 0.750,
	   0 0 0 1 0.879}; */

ESSENCEF = XBONE[*,1:3];
GOBS=XBONE[*,4];
NOBS=NROW(GOBS);
GBAR=GOBS[:];
GSS=GOBS[##];
GVARHAT=(GSS-NOBS#GBAR##2)/(NOBS-1);
GSTDHAT=SQRT(GVARHAT);
*RHOHAT=0.8530346;
RHOHAT = 0.85;
PRINT GBAR GVARHAT RHOHAT GSTDHAT;



P=2;
/*
SIGMAY_G=GVARHAT#(J(P+1,P+1,RHOHAT)+I(P+1)#(1-RHOHAT));
SIGMAYY=SIGMAY_G[1:P,1:P];
SIGMAYG=SIGMAY_G[1:P,P+1];
SIGMAGG=SIGMAY_G[P+1,P+1]; */
SIGMAYY={1 0.4,
		0.4 1};
SIGMAYG = {0.9, 0.8};
SIGMAGG = {1};
PRINT SIGMAYY SIGMAYG SIGMAGG;

SIGMAE=SIGMAYY-SIGMAYG*INV(SIGMAGG)*SIGMAYG`;
BETAG=INV(SIGMAGG)*SIGMAYG`;
PRINT SIGMAE BETAG;

CF={1 0 0};
CG={1};
C= CF||CG;
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

TEST="UG";
H_OR_U="H";
VARG=SIGMAGG;
SIGSTAR=U`*SIGMAE*U;
PRINT SIGSTAR;
MAXPDIFF=.001;
MAXITER=50;
PROBLIST=                  {0 0.1 0.2 0.3 0.4 0.5 0.6 0.7 0.8 0.9 1 };
HOLDNM={N DELTA1 SDELTA1}||{POWQ1 POWQ2 POWQ3 POWQ4 POWQ5}
        ||{UNPOW UNTERM1 UNTERM2};
  DO REPN=1 TO 1 BY 1;
  N=REPN#NROW(ESSENCEF);
    DO DELTA1=1 TO 1 BY 1;
    SDELTA1=DELTA1/GSTDHAT;
    BETA = (DELTA1#ESSBETAF)//BETAG;
    THETA = C*BETA*U;
	PRINT BETA THETA C U;
    THETAD=THETA-THETA0;
      DO IPROB=1 TO NCOL(PROBLIST);
      RUN BASEQ1("A",H_OR_U,ESSENCEF,REPN,CF,CG,VARG,THETAD,SIGSTAR,
                 PROBLIST[IPROB],MAXITER,MAXPDIFF, ITERE,QUANTE,FE);
	  
      *GGQ=GGQ||APOWU1("G", A,B,N,Q,ALPHA, SIGSTAR,QUANTE);
	  HOLD2=HOLD2//(PROBLIST[IPROB]||QUANTE);
      END;
    *APOWUNGG=BASEU4("E",TEST,ESSENCEF,REPN,CF,CG,VARG,THETAD,SIGSTAR,
             ALPHA,MAXPDIFF);
    *HOLD=HOLD//(N ||DELTA1||SDELTA1||GGQ||APOWUNGG`);
	
    *FREE GGQ;
    *PRINT HOLD[FORMAT=6.3 COLNAME=HOLDNM];
    END;
  END;

*PRINT HOLD[FORMAT=6.3 COLNAME=HOLDNM];

print HOLD2;
 QUIT;