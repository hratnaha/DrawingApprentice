#include <stdio.h>
#include "NativeLib.h"
#include "SimpleAPI.h" //just the simple API
#include "Cornucopia.h" //includes everything necessary to use the library


MYAPI int print_line(const char* str) {
  printf("%s\n", str);

  return 900;
}

MYAPI Cornu::BasicPrimitive *getBasicPrimitives(int passin[][2], int size, int parmtype){
	 Cornu::Parameters params = Cornu::Parameters::presets()[parmtype]; //default values
	 std::vector<Cornu::Point> pts;

	 for(int i=0;i<size;i++){
		 pts.push_back(Cornu::Point(passin[i][0],passin[i][1]));
	 }

	 //pass it to the fitter and process it
	 std::vector<Cornu::BasicPrimitive> result = Cornu::fit(pts, params);
	 int resultsize = (int)result.size();
	 Cornu::BasicPrimitive* finalresult = new Cornu::BasicPrimitive[resultsize];
	 for(int i = 0; i < resultsize; ++i)
	 {
		  finalresult[i] = result[i];
	 }
	 passin[0][0] = resultsize;
	 return finalresult;
}

MYAPI Cornu::BasicBezier *getBasicBezier(int passin[][2], int size, int parmtype){
	 Cornu::Parameters params = Cornu::Parameters::presets()[parmtype];
	 std::vector<Cornu::Point> pts;

	 for(int i=0;i<size;i++){
		 pts.push_back(Cornu::Point(passin[i][0],passin[i][1]));
	 }

	 //pass it to the fitter and process it
	 std::vector<Cornu::BasicPrimitive> result = Cornu::fit(pts, params);
	 // convert the result to bezier curves
	 std::vector<Cornu::BasicBezier> bezier = Cornu::toBezierSpline(result, 1.);


	 int resultsize = (int)bezier.size();
	 Cornu::BasicBezier* finalresult = new Cornu::BasicBezier[resultsize];
	 for(int i = 0; i < resultsize; ++i)
	 {
		  finalresult[i] = bezier[i];
	 }
	 passin[0][0] = resultsize;
	 return finalresult;
}

//void simpleAPITest()
//    {
//        Cornu::Parameters params; //default values
//        std::vector<Cornu::Point> pts;
//
//        pts.push_back(Cornu::Point(100, 100));
//        pts.push_back(Cornu::Point(120, 130));
//        pts.push_back(Cornu::Point(140, 140));
//        pts.push_back(Cornu::Point(300, 140));
//
//        //pass it to the fitter and process it
//        std::vector<Cornu::BasicPrimitive> result = Cornu::fit(pts, params);
//
//        int nPrims[3] = { 0, 0, 0 };
//        for(int i = 0; i < (int)result.size(); ++i)
//        {
//            nPrims[result[i].type]++;
//        }
//
//        Cornu::Debugging::get()->printf("SimpleAPI Finished, #lines = %d, #arcs = %d, #clothoids = %d\n", nPrims[0], nPrims[1], nPrims[2]);
//
//        std::vector<Cornu::BasicBezier> bezier = Cornu::toBezierSpline(result, 1.);
//
//        Cornu::Debugging::get()->printf("Conversion to Bezier results in %d segments\n", bezier.size());
//    }