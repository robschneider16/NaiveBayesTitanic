import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.String;
import java.util.Arrays;
import java.util.Random;
 


public class EvaluateNaiveBayes {
		//other variables for creating dicrete bins
	public static Integer numYearsPerDiscreteBinForAge = 10;
	public static Integer age_max = 8; 
	public static Integer sib_sp_max_num = 5; 
	public static Integer par_ch_max_num = 5; 
	public static Integer fare_max_num = 10; 
	public static Integer fare_DiscreteBin_num = 10; 

	public static int samplingRate = 10; // 1/10 passengers will be chosen to be a random test

	//max of 8 children
	public Vector<Passenger> TrainingData = new Vector<Passenger>();
	public Vector<Passenger> TestData = new Vector<Passenger>();
	public int[] survivedStats = new int[2];
	public int[] survived_pclassStats = new int[3];
	public int[] survived_genderStats = new int[2];
	public int[] survived_ageStats = new int[age_max+1];//60 max, increments of 5 year bins.
	public int[] survived_sibSpStats = new int[sib_sp_max_num+1];
	public int[] survived_parchStats = new int[par_ch_max_num+1];
	public int[] survived_fareStats = new int[fare_max_num+1];
	public int[] survived_cabinStats = new int[8]; 
	public int[] survived_embarkedStats = new int[3];

	public int[] died_pclassStats = new int[3];
	public int[] died_genderStats = new int[2];
	public int[] died_ageStats = new int[age_max];//60 max, increments of 5 year bins.
	public int[] died_sibSpStats = new int[sib_sp_max_num+1];
	public int[] died_parchStats = new int[par_ch_max_num+1];
	public int[] died_fareStats = new int[fare_max_num+1]; 
	public int[] died_cabinStats = new int[8];
	public int[] died_embarkedStats = new int[3];
 	//PassengerId,
	//Survived 		//0=no, 1=yes
	//pclass;		//1st, 2nd, 3rd class
	//gender; 		//0 = Male, 1 = female
	//age;			//Age bins, every 5 years?
	//sib_sp;		//0-5>
	//par_ch;		//0-3>
	//fare;			//0-10>
	//cabin;		//?  might need this and just exclude null values., aka if everyone from cabin 1 died then thats important
	//embarked;		//0=C, 1=Q or 2=S
    
	public void Train_Model(String input, int numOfCols) throws FileNotFoundException {
		//create Scanner from input filename
		Scanner in = new Scanner(new File(input));
        //Set the delimiter used in file
        in.useDelimiter(",");
        //init and skip the headers
        String p;
        for(int y = 0; y<numOfCols; y++){ 
        	p = in.next();//System.out.println(p);
    	}

		Random k = new Random();
		int t = 0;
		int i = 1;
		while(in.hasNext()){
			t = k.nextInt(samplingRate); //i out of every 15 will be taken randomly to be tested
			String s[] = new String[12];//1 out of every 12 selected at random for testing
			s[0] = String.valueOf(i);
			i += 1;
			s[1] = in.next();//System.out.println(s[1]);
			s[2] = in.next();
			s[3] = (in.next() + in.next()); //comma between the last and first name. special case
			s[4] = in.next();
			s[5] = in.next();
			s[6] = in.next();
			s[7] = in.next();
			s[8] = in.next();
			s[9] = in.next();
			s[10] = in.next();//System.out.println(s[10]);
			s[11] = in.next();//System.out.println(s[11]);
			Passenger kk = new Passenger(s);
			if(t != 5){
				TrainingData.add(kk);
			}else{
				TestData.add(kk);
			}
		}
		in.close();
	}

	public double ModelAccuracy(){
		int numalive = 0;
		int numacorrect = 0;
		int numdead = 0;
		int totalnumberintest = 0;
		int i = 1;
		for(int f=0; f<TestData.size(); f++){
			int a = SurvivalOfNewPassenger(TestData.get(f));
			if(a == 1){
				numalive +=1;
			}else{
				numdead +=1;
			}
			if(a == TestData.get(f).stats[0]){
				numacorrect +=1;
			}
		}
		totalnumberintest = TestData.size();
		//return the percent we determined correctly.
		return (100.00*(double)numacorrect/(double)totalnumberintest);
	}

	public double Test_Model(String input, int numOfCols) throws FileNotFoundException {
		int numalive = 0;
		int numcorrect = 0;
		int numdead = 0;
		int totalnumberintest = 0;
		//create Scanner from input filename
		Scanner inp = new Scanner(new File(input));
        //Set the delimiter used in file
        inp.useDelimiter(",");
        //init
       	String p;
        for(int y = 0; y<numOfCols; y++){ 
        	p = inp.next();//System.out.println(p);
    	}

		int i = 0;
		while(inp.hasNext()){
			String s[] = new String[12];
			i += 1;
			s[0] = String.valueOf(i);
			s[1] = inp.next(); //ON TEST DATA, THIS COL IS MISSING> Change numOfCols to 11 and comment this line out
			s[2] = inp.next();
			s[3] = inp.next() + inp.next(); // comma betwen first and last name
			s[4] = inp.next();
			s[5] = inp.next();
			s[6] = inp.next();
			s[7] = inp.next();
			s[8] = inp.next();
			s[9] = inp.next();
			s[10] = inp.next();
			s[11] = inp.next();
			Passenger test = new Passenger(s);
			
			int a = SurvivalOfNewPassenger(test);
			if(a == test.stats[0]){
				numcorrect +=1;
			}
			if(a == 1){
				numalive +=1;
			}else{
				numdead +=1;
			}
		}
		totalnumberintest = i;

		System.out.println("evaluated " + (100*(float)numcorrect/(float)totalnumberintest) + " % correct on Test data");
		inp.close();
		//System.out.println(numalive + " survived out of " + totalnumberintest + " <---Test Data" );
		return ((double)numalive/(double)totalnumberintest);
	}

	public void GenerateStats(){
		for(int i = 0;i<TrainingData.size(); i++){
			Passenger p = TrainingData.get(i);

			if(!p.stats[0].equals(-1)){
				survivedStats[p.stats[0]] += 1;
			}

			if(p.stats[0].equals(1)){
				if(!p.stats[1].equals(-1)){
					survived_pclassStats[p.stats[1]-1] += 1; //0 = 1, 1=2, 2=3
				}
				if(!p.stats[2].equals(-1)){
					survived_genderStats[p.stats[2]] += 1;
				}
				if(!p.stats[3].equals(-1)){
					survived_ageStats[p.stats[3]] += 1; 
				}
				if(!p.stats[4].equals(-1)){
					survived_sibSpStats[p.stats[4]] += 1;
				}
				if(!p.stats[5].equals(-1)){
					survived_parchStats[p.stats[5]] += 1;
				}
				if(!p.stats[6].equals(-1)){
					survived_fareStats[p.stats[6]] += 1;  
				}
				if(!p.stats[7].equals(-1)){
					survived_cabinStats[p.stats[7]] += 1;
				}
				if(!p.stats[8].equals(-1)){
					survived_embarkedStats[p.stats[8]] += 1;
				}
			}
			if(p.stats[0].equals(0)){
				if(!p.stats[1].equals(-1)){
					died_pclassStats[p.stats[1]-1] += 1; //0 = 1, 1=2, 2=3
				}
				if(!p.stats[2].equals(-1)){
					died_genderStats[p.stats[2]] += 1;
				}
				if(!p.stats[3].equals(-1)){
					died_ageStats[p.stats[3]] += 1; 
				}
				if(!p.stats[4].equals(-1)){
					died_sibSpStats[p.stats[4]] += 1;
				}
				if(!p.stats[5].equals(-1)){
					died_parchStats[p.stats[5]] += 1;
				}
				if(!p.stats[6].equals(-1)){
					died_fareStats[p.stats[6]] += 1;  
				}
				if(!p.stats[7].equals(-1)){
					died_cabinStats[p.stats[7]] += 1;
				}
				if(!p.stats[8].equals(-1)){
					died_embarkedStats[p.stats[8]] += 1;
				}
			}



		}
	}


	public int SurvivalOfNewPassenger(Passenger p){
		int[] numeProbs = new int[9];
		int[] denomProbs = new int[9];
		for(int k =0; k<9; k++){
			numeProbs[k] = 0;
			denomProbs[k] = 0;
		}

		for(int i = 0; i <TrainingData.size(); i++){
			Passenger tp = TrainingData.get(i);
			//Calculating if survival v is 1, +, survived.
			//v = p(v) * PRODUCT p(a|v)
			//prob survival 
			if(tp.stats[0].equals(1)){
				numeProbs[0] += 1;
			}
			denomProbs[0] += 1;
			for(int w = 1; w<9; w++){
				if(!(p.stats[w]).equals(-1)){
					if(tp.stats[w].equals(p.stats[w])){
						if(tp.stats[0].equals(1)){
							numeProbs[w] += 1;
						}
						denomProbs[w] += 1;	
					}
				}else{
					numeProbs[w] = 1;
					denomProbs[w] = 1;
				}
			}
		}
		//k now all the numerators and denomenators have been calculated., so now lets compute v-out
		double v = 1;
		for(int j=0; j<9; j++){
			if(denomProbs[j]>0){
				v = v * ((double)numeProbs[j]/(double)denomProbs[j]);
			}
		}


		for(int k =0; k<9; k++){
			numeProbs[k] = 0;
			denomProbs[k] = 0;
		}for(int i = 0; i <TrainingData.size(); i++){
			Passenger tp = TrainingData.get(i);
			//Calculating if survival v is 1, +, survived.
			//v = p(v) * PRODUCT p(a|v)
			//prob survival
			if(tp.stats[0].equals(0)){
				numeProbs[0] += 1;
			}
			denomProbs[0] += 1;
			for(int w = 1; w<9; w++){
				if(!p.stats[w].equals(-1)){
					if(tp.stats[w].equals(p.stats[w])){
						if(tp.stats[0].equals(0)){
							numeProbs[w] += 1;
						}
						denomProbs[w] += 1;	
					}
				}else{
					numeProbs[w] = 1;
					denomProbs[w] = 1;
				}
			}
		}

		double notv = 1;
		for(int j=0; j<9; j++){ 
				notv = notv * ((double)numeProbs[j]/(double)denomProbs[j]);
		}
		int answer = 0;//BOOLEAN in the for of an int, 1 or 0
		if(v >= notv){
			answer = 1;
		}
		TrainingData.add(p);//after computing, we will add it to our representation.
		return answer;
	}



	public void PrintProbabilities(){
		System.out.println("Survival ratio [dead alive]" + Arrays.toString(survivedStats));
		System.out.println("Stats of those who lived");
		System.out.println("Pclass " 	+ Arrays.toString(survived_pclassStats));
		System.out.println("Gender " + Arrays.toString(survived_genderStats));
		System.out.println("Age " + Arrays.toString(survived_ageStats)) ;//60 max, increments of 5 year bins.
		System.out.println("Sib/Sp " + Arrays.toString(survived_sibSpStats));
		System.out.println("Par/Ch " + Arrays.toString(survived_parchStats));
		System.out.println("Fare " + Arrays.toString(survived_fareStats)) ; 
		System.out.println("cabin " + Arrays.toString(survived_cabinStats)) ; 
		System.out.println("Embarked " + Arrays.toString(survived_embarkedStats));
		System.out.println();
		System.out.println("Stats of those who didnt survive");
		System.out.println("Pclass " + Arrays.toString(died_pclassStats));
		System.out.println("Gender " + Arrays.toString(died_genderStats));
		System.out.println("Age " + Arrays.toString(died_ageStats)) ;//60 max, increments of 5 year bins.
		System.out.println("Sib/Sp " + Arrays.toString(died_sibSpStats));
		System.out.println("Par/Ch " + Arrays.toString(died_parchStats));
		System.out.println("Fare " + Arrays.toString(died_fareStats)) ; 
		System.out.println("cabin " + Arrays.toString(died_cabinStats)) ; 
		System.out.println("Embarked " + Arrays.toString(died_embarkedStats));
	}


public static void main(String[] args) throws FileNotFoundException {
	String trainFile = "Train2.csv";
	String testFile = "Test.csv";
	if(args.length == 2){
		trainFile = args[0];
		testFile = args[1];
	}


    EvaluateNaiveBayes rob = new EvaluateNaiveBayes();
    rob.Train_Model(trainFile, 12);
    float z = (float)rob.ModelAccuracy();
    rob.GenerateStats();
	rob.PrintProbabilities();
	System.out.println();  
	System.out.println(z+"% Correctly estimated (random sampling from training data)");
	System.out.println("training data contains " + rob.TrainingData.size() + " instances");
	System.out.println();       
	System.out.println("*****Test*****");
    double p = rob.Test_Model(testFile, 12);
    double g = ((double)rob.survivedStats[1] / ((double)rob.survivedStats[1]+(double)rob.survivedStats[0]));
    System.out.println(g + "	Probability of survival of Training data");
    System.out.println(p + " 	Probability of survival of Test data");
	System.out.println();
    System.exit(1);
    }
}