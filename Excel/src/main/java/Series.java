import java.util.ArrayList;

public class Series {
	
	private String unitString;
	private int[] year;
	private ArrayList<Double> wage;
	
	private String nameString;
	public String getUnitString() {
		return unitString;
	}
	public void setUnitString(String unitString) {
		this.unitString = unitString;
	}
	public int[] getYear() {
		return year;
	}
	public void setYear(int[] year) {
		this.year = year;
	}
	public ArrayList<Double> getWage() {
		return wage;
	}
	public void setWage(ArrayList<Double> wage) {
		this.wage = wage;
	}
	public String getNameString() {
		return nameString;
	}
	public void setNameString(String nameString) {
		this.nameString = nameString;
	}

}
