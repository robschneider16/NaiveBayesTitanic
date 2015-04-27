import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.*;




//a class for storing information from the table. Pass the constructor an array of strings, 
// and creates a passenger with an array containing all the information.
//see below for discription of indecies and what they represent. 
//-1 is a missing value, and does not contribute to probability computation.

public class Passenger {
	//other variables for creating dicrete bins
	public static Integer numYearsPerDiscreteBinForAge = 10;
	public static Integer age_max = 8; 
	public static Integer sib_sp_max_num = 5; 
	public static Integer par_ch_max_num = 5; 
	public static Integer fare_max_num = 10; 
	public static Integer fare_DiscreteBin_num = 10; 

	public Integer[] stats;
	//********stats index Information****
	//Index null = passengerId;
	//Index 0 = survival;	//0=no, 1=yes
	//Index 1 = pclass;		//1st, 2nd, 3rd class
	//Index 2 = gender; 	//0 = Male, 1 = female
	//Index 3 = age;		//Age bins,
	//Index 4 = sib_sp;		//0-5>
	//Index 5 = par_ch;		//0-3>
	//Index null = ticket;
	//Index 6 = fare;		//0-10>
	//Index 7 = cabin = 0;	//?a0 b1 c2 d3 e4 f5 g6 T7
	//Index 8 = embarked;	//0=C, 1=Q or 2=S



	public Passenger(String a[]){
		stats = new Integer[9];
		//PassengerId, Useless?
		
		//Survived,
		int p = Integer.parseInt(a[1]);//1,0
		if(p>=0){
			stats[0] = p;
		}else{
			stats[0] = -1;
		}
		//Pclass,
		p = Integer.parseInt(a[2]);//1,2,3
		if(p>=1){
			stats[1] = p;
		}else{
			stats[1] = -1;
		}
		
		//Name, Useless
		//Gender,
		if(a[4].equals("male")){
			stats[2] = 0;//males
		}else{
			if(a[4].equals("female")){
				stats[2] = 1;//female
			}else{
				stats[2] = -1;//null
			}
		}

		//Age,
		if(!a[5].equals("")){
			stats[3] = (int)Math.round(Double.parseDouble(a[5])/numYearsPerDiscreteBinForAge); //bins of 5 years
			if(stats[3] >= age_max){
				stats[3] = age_max;
			}
		}else{
			stats[3] = -1; // So we know when there are null values
		}

		//SibSp
		if(!a[6].equals("")){
			stats[4] = Integer.parseInt(a[6]);
			if(stats[4] >= sib_sp_max_num){
				stats[4] = sib_sp_max_num;
			}
		}else{
			stats[4] = -1;
		}


		//Parch
		if(!a[7].equals("")){
			stats[5] = Integer.parseInt(a[7]);//3 is max
			if(stats[5] >= par_ch_max_num){
				stats[5] = par_ch_max_num;
			}
		}else{
			stats[5] = -1;
		}

		//Ticket, useless?s
		//Fare
		if(!a[9].equals("")){
			stats[6] = (int)Math.round(Double.parseDouble(a[9])/fare_DiscreteBin_num);//10 is max, its > 100 bucks
			if(stats[6] >= fare_max_num){
				stats[6] = fare_max_num;
			}
		}else{
			stats[6] = -1;
		}

		//Cabin
		if(!a[10].equals("")){
			Character c = a[10].charAt(0);
			if(c.equals('A')){
				stats[7] = 0;
			}
			if(c.equals('B')){
				stats[7] = 1;	
			}
			if(c.equals('C')){
				stats[7] = 2;
			}
			if(c.equals('D')){
				stats[7] = 3;
			}
			if(c.equals('E')){
				stats[7] = 4;
			}
			if(c.equals('F')){
				stats[7] = 5;
			}
			if(c.equals('G')){
				stats[7] = 6;
			}
			if(c.equals('T')){
				stats[7] = 7;
			}
		}else{
			stats[7] = -1;
		}

		//Embarked
		Character x = a[11].charAt(0);
		if(x.equals('C')){
			stats[8] = 0;
		}else{
			if(x.equals('Q')){
				stats[8] = 1;
			}else{
				if(x.equals('S')){
					stats[8] = 2;
				}else{
					stats[8] = -1;
				} //there is empty data.
			}
		}
	}
}