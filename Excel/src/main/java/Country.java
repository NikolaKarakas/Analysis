import java.util.ArrayList;
import java.util.Comparator;

public class Country  {

	
	private String nameString;
	private ArrayList<Series> series;
	private ArrayList<FXRate> fxRates;
	private ArrayList<MinWage> minWages;
	private String minWageUnit;
	
	public String getNameString() {
		return nameString;
	}
	
	public void addFxRate (FXRate fxRate) {
	//	if(fxRates==null)
		//	System.out.println("EMPTY " +  fxRate.getYear()+ " "+ fxRate.getRate());
		fxRates.add(fxRate);
		//System.out.println("FULL");

		
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}
	public ArrayList<Series> getSeries() {
		return series;
	}
	public void setSeries(ArrayList<Series> series) {
		this.series = series;
	}
	public ArrayList<FXRate> getFxRates() {
		return fxRates;
	}
	public void setFxRates(ArrayList<FXRate> fxRates) {
		this.fxRates = fxRates;
	}
	
	
	public FXRate returnRate(int i) {
		return fxRates.get(i);
		
	}
	
	public int getIndexOfRate(int n )
	{
		
		int a=0;
		
		for(FXRate fx:fxRates)
		{
			if(fx.getYear()==n)
			{
				break;
			}
			a++;
		}
		return a;
	}

	public ArrayList<MinWage> getMinWages() {
		return minWages;
	}

	public void setMinWages(ArrayList<MinWage> minWages) {
		this.minWages = minWages;
	}

	public String getMinWageUnit() {
		return minWageUnit;
	}

	public void setMinWageUnit(String minWageUnit) {
		this.minWageUnit = minWageUnit;
	}

	  public  Comparator<Country> StuNameComparator = new Comparator<Country>() {

			public int compare(Country s1, Country s2) {
			   String CountrytName1 = s1.getNameString().toUpperCase();
			   String CountryName2 = s2.getNameString().toUpperCase();

			   //ascending order
			   return CountrytName1.compareTo(CountryName2);

			   //descending order
			   //return StudentName2.compareTo(StudentName1);
		    }

			/*public int compare(Country arg0, Country arg1) {
				// TODO Auto-generated method stub
				return 0;
			}*/
			};
	
	
	
}
