import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections4.map.StaticBucketMap;
import org.apache.poi.dev.OOXMLLister;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;


import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	private static Map<String, Locale> localeMap;
	public static void main(String args[])throws Exception {
	
	
		ArrayList<Country> countriesList =new ArrayList<Country>();
		Double rates[] = new Double[18] ;
	
		initCountryCodeMapping();	
		readAvgWages(countriesList);
		readFxRate(countriesList);
		rates=rateUSDtoEUR(countriesList);

		outputCurrentPricesNCU(countriesList,rates);
	
		 countriesList.remove(countriesList.size()-1);

		readMinWages(countriesList);
		 readFxRate(countriesList);	//NEED TO READ RATES AGAIN FOR NEW ADDED COUNTRIES


	for(Country c:countriesList)
	{
		//System.out.println(c.getNameString());
		for(MinWage m:c.getMinWages())
		{
			//System.out.println(m.getYear()+" "+ m.getWage());
		}
	}

	 countriesList.remove(countriesList.size()-1);
	 Country country= new Country() ;
	Collections.sort(countriesList, country.StuNameComparator);

	printMinWages(countriesList, rates);
	
	
	//for(int i = 0 ; i < countriesList.size();i++)
	//countriesList.remove(i);
	
	
	rationMinAverage();

		
		
	}
	
	public static void rationMinAverage() throws IOException
	{

		ArrayList<Integer> year1= new ArrayList<Integer>();
		ArrayList<Integer> year2= new ArrayList<Integer>();
		
		ArrayList<MinWage> data2 = null;
		ArrayList<MinWage> min = new ArrayList<MinWage>();
		ArrayList<MinWage> max = new ArrayList<MinWage>();
		ArrayList<Series> seriesList = null;
		
		String country1= new String();
		String country2= new String();
		
		File inFile1 = 	new File("Minimum wages in Euro.xlsx");
		File inFile2 = 	new File("Average wages in Euro.xlsx");

		FileInputStream fis1 = new FileInputStream(inFile1);
		FileInputStream fis2 = new FileInputStream(inFile2);
		
		XSSFWorkbook workbook1 = new XSSFWorkbook(fis1);
		XSSFSheet sheet1 = workbook1.getSheetAt(0);
		
		XSSFWorkbook workbook2 = new XSSFWorkbook(fis2);
		XSSFSheet sheet2 = workbook2.getSheetAt(0);
		
		   XSSFWorkbook workbook3 = new XSSFWorkbook();
		   Sheet sheet3 = workbook3.createSheet("Ratio");
		   Font headerFont =  workbook3.createFont();
		   headerFont.setBold(true);
		   headerFont.setFontHeightInPoints((short) 14);
		   headerFont.setColor(IndexedColors.RED.getIndex());
		   
		   CellStyle headerCellStyle = workbook3.createCellStyle();
		   headerCellStyle.setFont(headerFont);
		   
		   Row headerRow = sheet3.createRow(0); 
		   ArrayList<String> columns = new ArrayList<String>();
		   columns.add("Country");
		   columns.add("Unit");		
		Iterator<Row> rowIterator1=  sheet1.iterator();
		//READ YEARS FILE 1
		{
			Row row1 = rowIterator1.next(); //READ LINE FILE 1
			Iterator<Cell> cellIterator1= row1.cellIterator(); // READ ROW FILE1
			while(cellIterator1.hasNext())
			{
				Cell cell1= cellIterator1.next();
				if(cell1.getColumnIndex()>=2)						//READ THE DATA FROM THE SECON FILE
						year1.add(Integer.parseInt(cell1.toString()));
			}
		}	
		//READ YEARS FILE 2
				{
					Iterator<Row> rowIterator2=  sheet2.iterator();
					Row row2 = rowIterator2.next(); //READ LINE FILE 1
					Iterator<Cell> cellIterator2= row2.cellIterator(); // READ ROW FILE1
					while(cellIterator2.hasNext())
					{
						Cell cell2= cellIterator2.next();
						if(cell2.getColumnIndex()>=3)						//READ THE DATA FROM THE SECON FILE
								year2.add(Integer.parseInt(cell2.toString()));
					}
				}
		findSameYears(year1, year2);								//ADD SAME YEARS TO THE COLUMNS
		ArrayList<Integer> tempArrayList =new ArrayList<Integer>();
		tempArrayList= findSameYears(year1, year2);
		for(int i = 0; i < tempArrayList.size();i++)
			columns.add(tempArrayList.get(i).toString());	
			   
		   for(int i = 0 ; i<columns.size();i++)
		   {
			   Cell cell = headerRow.createCell(i);
			   cell.setCellValue(columns.get(i));
			   cell.setCellStyle(headerCellStyle);
		   }
		   
		   int rowNum  =  1;
		   int cellNum=0;
		
		while(rowIterator1.hasNext()) // FOR EVERY COUNTRY IN FIRST FILE FIND DATA IN SECOND 		//1WHILE
		{
			int not_found =0;
			int yr1=0;
			Row row1 = rowIterator1.next(); //READ LINE FILE 1

			Iterator<Cell> cellIterator1= row1.cellIterator(); // READ ROW FILE1
			ArrayList<MinWage> data1= new ArrayList<MinWage>();
			while(cellIterator1.hasNext())												//2WHILE
			{							
				Cell cell1= cellIterator1.next();	
							
				if(cell1.getColumnIndex()==0)	//FIRST COLUMN IN FIRST FILE
				{
					country1=cell1.toString();			//SAVE COUNTRY NAME
														  //FIND THE SAME COUNTRY IN FILE	
					Iterator<Row> rowIterator2=  sheet2.iterator();								
					while(rowIterator2.hasNext()) // FOR EVERY COUNTRY IN FIRST FILE FIND DATA IN SECOND		//3WHILE
					{
						Row row2 = rowIterator2.next();
						Iterator<Cell> cellIterator2= row2.cellIterator();						
						int yr2=0;
						while(cellIterator2.hasNext())															//4WHILE
						{
							Cell cell2= cellIterator2.next();
							if(cell2.toString().equals(country1) && cell2.getColumnIndex()==0 )			//FIND THE ROW WITH THE SAME COUNTRY 
							{
								not_found =1;
								data2= new ArrayList<MinWage>();
							}
										
							if(not_found==0)
								break;											
							MinWage minWage= new MinWage();
							if(cell2.getColumnIndex()>=3)						//READ THE DATA FROM THE SECON FILE
							{	
								if(cell2.getCellType()==0)
								{	
									minWage.setWage(cell2.getNumericCellValue());
									minWage.setYear(year2.get(yr2++));												
									data2.add(minWage);											
								}											
								else 
								{
									minWage.setWage(0.0);
									minWage.setYear(year2.get(yr2++));
									data2.add(minWage);											
								}
							}										
						}							//4WHILE			
										
						if(not_found==1) //NASAO
							break;										
					}									//3While								
				}
				if(not_found==0) //NOT FOUND, CONTINUE
					break;			
							// FIRST COLUMN IN FIRST FILRE
							//READ THE DATA FROM THE FIRST FILE
				MinWage minWage = new MinWage();
				if(cell1.getColumnIndex()>=2)
				{
					if(cell1.getCellType()==0)
					{	
						minWage.setWage(cell1.getNumericCellValue());
						minWage.setYear(year1.get(yr1++));										
						data1.add(minWage);									
					}								
					else 
					{
						minWage.setWage(0.0);
						minWage.setYear(year1.get(yr1++));
						data1.add(minWage);		
					}
				}	
			} //WHILE ROW FIRST FILE					
			if(not_found==1)
			{//	System.out.println("NASAO " + country1);			
				cellNum=0;
				Row row3 = sheet3.createRow(rowNum++);
				row3.createCell(cellNum++).setCellValue(country1); //IME DRZAVE
				row3.createCell(cellNum++).setCellValue("Euro"); //IME VALUTA SERIJE
								
				for(MinWage m:data1)
				{			
					for(MinWage w:data2)
					{	
						if(m.getYear()==w.getYear())
						{
							if(m.getWage()==0.0 || w.getWage()==0.0)
								row3.createCell(cellNum++).setCellValue("NA"); //IME VALUTA SERIJE
							else
							{
								row3.createCell(cellNum++).setCellValue(m.getWage()/w.getWage()); //IME VALUTA SERIJE
								int e=0;
								for(MinWage mn:min)
								{
									if(mn.getYear()==m.getYear())
									{
										if(mn.getWage()>(m.getWage()/w.getWage()))	//ako je novi manji
											mn.setWage(m.getWage()/w.getWage());
										e=1;
										break;
									}
								}
								if(e==0)
								{
									MinWage mnMinWage = new MinWage();
									mnMinWage.setWage(m.getWage()/w.getWage());
									mnMinWage.setYear(m.getYear());
									min.add(mnMinWage);
								}
								e=0;
								for(MinWage mn:max)
								{
									if(mn.getYear()==m.getYear())
									{
										if(mn.getWage()<=(m.getWage()/w.getWage()))	//ako je novi manji
											mn.setWage(m.getWage()/w.getWage());
										e=1;
										break;
									}
								}
								if(e==0)
								{
									MinWage mnMinWage = new MinWage();
									mnMinWage.setWage(m.getWage()/w.getWage());
									mnMinWage.setYear(m.getYear());
									max.add(mnMinWage);
								}
													//MAX FIND MAXIMUM
							}//System.out.println(m.getYear() + " "+ m.getWage()+" " + w.getYear()+ " "+ w.getWage());			
						}										
					}
				}
			}			
		}
		Row bottomRow = sheet3.createRow(rowNum++); 
		for(int a = 0 ; a < 2; a++)
		{			
			if(a==1)
			bottomRow =sheet3.createRow(rowNum);
			Cell cell = bottomRow.createCell(0);
			if(a==0)
			cell.setCellValue("Min");
			else cell.setCellValue("Max");
			cell.setCellStyle(headerCellStyle); 
			cell = bottomRow.createCell(1);
			cell.setCellValue("Euro");
			   
			for(int i =0; i < min.size();i++)
			{	
				if(a==0)
				   bottomRow.createCell(i+2).setCellValue(min.get(i).getWage());
				else
					bottomRow.createCell(i+2).setCellValue(max.get(i).getWage());
			}
		}
		for (int i = 0 ; i < columns.size();i++)
			sheet3.autoSizeColumn(i);
		   
		 	fis2.close();
		 	workbook2.close();
		   FileOutputStream fileOut = new FileOutputStream("MIN-AVG ratio and EMA7.xlsx");		   
		   
		   printEma7(fileOut,workbook3,sheet3,rowNum);
		   fileOut.close();
		   workbook3.close();		
						
		} 				//WHILE LINE
		
	public static void printEma7(FileOutputStream fileOut, XSSFWorkbook workbook2,Sheet sheet2,int rowNum) throws IOException
	{		
		File inFile1 = 	new File("Average wages in Euro.xlsx");
		FileInputStream fis1 = new FileInputStream(inFile1);
		ArrayList<Double> ema7=new ArrayList<Double>();
		
		XSSFWorkbook workbook1 = new XSSFWorkbook(fis1);
		XSSFSheet sheet1 = workbook1.getSheetAt(0);
		
		Iterator<Row> rowIterator1=  sheet1.iterator();
		rowIterator1.next();
		
		int EMA7 = 7;
		Double smoothing= 2/(EMA7+1.0);
		
		 Font headerFont =  workbook2.createFont();
		   headerFont.setBold(true);
		   headerFont.setFontHeightInPoints((short) 14);
		   headerFont.setColor(IndexedColors.RED.getIndex());
		   
		   CellStyle headerCellStyle = workbook2.createCellStyle();
		   headerCellStyle.setFont(headerFont);
		   rowNum+=5;
		   
		   Row row2 = sheet2.createRow(rowNum++);
		   		
				row2.createCell(0).setCellValue("Country");;
				row2.getCell(0).setCellStyle(headerCellStyle);
				row2.createCell(1).setCellValue("2017");;
				row2.getCell(1).setCellStyle(headerCellStyle);
	
			int prvi = 0;

		while(rowIterator1.hasNext())
		{
			Row row1 = rowIterator1.next(); //READ LINE FILE 1
			 row2 = sheet2.createRow(rowNum++);
			
			int cellNum=0;
			Iterator<Cell> cellIterator1= row1.cellIterator(); // READ ROW FILE1
			Double avg=0.0;
			int i=0;
			Double emaPrevious=0.0;
		String country="";
		Double wage2017=0.0;
			while(cellIterator1.hasNext())
			{
				Cell cell1= cellIterator1.next();
				if(cell1.getColumnIndex()==0)
					country=cell1.toString();

				if(cell1.getColumnIndex()<3)
				{
				}
												//COUNTING FOR EMA7
				else
				{								//FIND FIRST 7 AVG
					if(i<EMA7-1)
						avg+=cell1.getNumericCellValue();					
					else if(i ==EMA7 -1)
					{
						avg+=cell1.getNumericCellValue();
						avg=avg/EMA7;
						ema7.add(avg);
						emaPrevious=avg;
					}					
					else 
					{
						wage2017=cell1.getNumericCellValue();
						ema7.add((cell1.getNumericCellValue()-emaPrevious)*smoothing+ema7.get(ema7.size()-1));
						emaPrevious=(cell1.getNumericCellValue()-emaPrevious)*smoothing+emaPrevious;
					}
					i++;				
				}
			}
			row2.createCell(0).setCellValue(country);
			row2.createCell(1).setCellValue(ema7.get(ema7.size()-1)/wage2017);
			ema7.removeAll(ema7);
		}
		 for (int i = 0 ; i<=20;i++)
			   sheet2.autoSizeColumn(i);
		workbook2.write(fileOut);
		workbook2.close();
		
	}
	
	public static void printMinWages(ArrayList<Country> countriesList,Double rates[]) throws IOException
	{
		
			   ArrayList<String>  columns=new ArrayList<String>();
			   columns.add("Country");
			   columns.add("Unit");
			   
			   Workbook workbook1 = new XSSFWorkbook();
			   Sheet sheet1 = workbook1.createSheet("MinimumWages");
			   Font headerFont =  workbook1.createFont();
			   headerFont.setBold(true);
			   headerFont.setFontHeightInPoints((short) 14);
			   headerFont.setColor(IndexedColors.RED.getIndex());
			   
			   CellStyle headerCellStyle = workbook1.createCellStyle();
			   headerCellStyle.setFont(headerFont);
			   
			   Row headerRow = sheet1.createRow(0);
			   ArrayList<MinWage> minWage = new ArrayList<MinWage>();
			   minWage = null;
			   
			   int o = 0;
			   while(minWage==null)			//GET LIST OF YEAR -YEAR COLUMNS
			   {
				   minWage= countriesList.get(o++).getMinWages();
			   }
			   
			   for(MinWage m:minWage)
			   {
				   columns.add(String.valueOf(m.getYear()));
			   }
			   
			   for(int i = 0 ; i<columns.size();i++)
			   {
				   Cell cell = headerRow.createCell(i);
				   cell.setCellValue(columns.get(i));
				   cell.setCellStyle(headerCellStyle);
			   }    
			 
			   int rowNum  =  1;
			   int cellNum=0;
			   for( Country c: countriesList)
				{
					cellNum=0;
					Row row = sheet1.createRow(rowNum++);
					row.createCell(cellNum++).setCellValue(c.getNameString()); //IME DRZAVE
					row.createCell(cellNum++).setCellValue("Euro"); //IME VALUTA SERIJE
				
					//NO DATA COUNTRIES
					if(c.getMinWages().size()==0)
						for(int i = 0 ; i < minWage.size();i++)
							row.createCell(i+2).setCellValue("NA"); 

					int k=0;
					int rate_ind = 10;
					//System.out.println(c.getNameString());
					int a = c.getIndexOfRate(2010);
					
					for(MinWage s : c.getMinWages())
					{						
						if(c.getMinWageUnit().equals("Euro"))								
								row.createCell(cellNum++).setCellValue(Math.round(s.getWage())); 						
						else 
						{
							int b=0;
							int i ;
							row.createCell(cellNum++).setCellValue(Math.round((s.getWage()/c.getFxRates().get(a++).getRate()) * rates[rate_ind++])); //IME DRZAVE
						}
					}
				}
			   
			   for (int i = 0 ; i < columns.size();i++)
				   sheet1.autoSizeColumn(i);

			   FileOutputStream fileOut = new FileOutputStream("Minimum Wages in Euro.xlsx");
			   workbook1.write(fileOut);
			   fileOut.close();
			   workbook1.close();
	}
	
	
	public static void readMinWages(ArrayList<Country> countriesList) throws IOException
	{	
		ArrayList<Integer> year= new ArrayList<Integer>();
		ArrayList<Series> seriesList = null;
		
		File outFile = 	new File("Dataset minimum wages.xlsx");
		FileInputStream fis = new FileInputStream(outFile);
		
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator=  sheet.iterator();
		rowIterator.next();
		rowIterator.next();
		rowIterator.next();
		Row row1 = rowIterator.next();
		Iterator<Cell> cellIterator1= row1.cellIterator();

		while(cellIterator1.hasNext())  // READ YEARS
		{
			Cell cell = cellIterator1.next();
			if(cell.getColumnIndex()>=3)
				year.add(Integer.parseInt(cell.getStringCellValue()));
		}
		rowIterator.next();
		int serija=0;
		int t = 0;
		//POCNI OD REDA
		int year_count=0;
		int nema =1;
		while(rowIterator.hasNext())
		{
			
			ArrayList<MinWage> minWages =new ArrayList<MinWage>();
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			Country country2 = null;
							//ROW
						int index=0;
						year_count=0;
						while(cellIterator.hasNext())
						{							
							Cell cell= cellIterator.next();
							MinWage minWage = new MinWage();
							
							if( cell.getColumnIndex() ==0 ) //CHECK IF I HAVE COUNTRY
							{
								for(Country c: countriesList)
								{
									if(c.getNameString().equals(cell.getStringCellValue()))
										break;
									index++;										
								}					
							}							
							if(index >= countriesList.size()) //NO COUNTRY IN THE LIST
							{
								if( cell.getColumnIndex() ==0 )
								{
									country2 = new Country();
									country2.setNameString(cell.getStringCellValue());
									country2.setSeries(null);
								}
								if( cell.getColumnIndex() ==1 )
									country2.setMinWageUnit(cell.getStringCellValue());								
								if( cell.getColumnIndex() >=3  )
								{ 	
									if(cell.getCellType()==0)
										minWage.setWage(cell.getNumericCellValue());
									else
									
										minWage.setWage(0.0);									
									minWage.setYear(year.get(year_count++));
									minWages.add(minWage);
									
								}
							}								
								else 
								{	
									if( cell.getColumnIndex() ==1 )										
										countriesList.get(index).setMinWageUnit(cell.getStringCellValue());									
									if( cell.getColumnIndex() >=3  )
									{
										if(cell.getCellType()==0)										
											minWage.setWage(cell.getNumericCellValue());
										else 
											minWage.setWage(0.0);
											minWages.add(minWage);
											
											minWage.setYear(year.get(year_count++));
									}
										
								}
							}
						if(index >= countriesList.size())
							{
								country2.setMinWages(minWages);
								countriesList.add(country2);
							}
						else
							countriesList.get(index).setMinWages(minWages);			
		}
	}
	
	public static ArrayList<Integer> findSameYears (ArrayList<Integer> a, ArrayList<Integer> b)
	{
		ArrayList<Integer> newArrayList= new ArrayList<Integer>();
		for(int i = 0 ; i < a.size();i++)
		{
			if(b.contains(a.get(i)))
				newArrayList.add(a.get(i));
		}		
		return newArrayList;
	}
	
	public static Double[] rateUSDtoEUR(ArrayList<Country> countries)
	{
		int i =0;
		Double rate[]=new Double[19];
		for(Country c:countries)
		{
			if(c.getNameString().equals("Germany"))
			{
				for(FXRate f: c.getFxRates())
				{
					if(f.getYear()>=2000)
						rate[i++]=f.getRate();
				}				
			}
		}
		return rate;
	}
	
	public static void readAvgWages( ArrayList<Country> countriesList) throws IOException
	{
		Country country =null;
		ArrayList<Series> seriesList = null;
		
		File outFile = 	new File("Dataset annual wages.xlsx");
		FileInputStream fis = new FileInputStream(outFile);
		
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator=  sheet.iterator();
		rowIterator.next();
		rowIterator.next();
		rowIterator.next();
		rowIterator.next();
		int serija=0;
		int t = 0;
	
		//POCNI OD REDA
		while(rowIterator.hasNext())
		{
			//AKO JE 0(4) RED SERIJA
			if(serija==0)
			{
				country = new Country();
				seriesList = new ArrayList<Series>();
				ArrayList<MinWage> minWages = new ArrayList<MinWage>();				
				country.setMinWages(minWages);
				country.setMinWageUnit("");
			}
			
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();			
			  int wage = 0;
			  			
				ArrayList<Double> a= new ArrayList<Double>();
				Series series = new  Series();	
							//ROW
						while(cellIterator.hasNext())
						{	
							Cell cell= cellIterator.next();
							if(serija==0 && cell.getColumnIndex() ==0 )
							{	country.setNameString(cell.getStringCellValue());	
								ArrayList<FXRate> mmmArrayList= new ArrayList<FXRate>();								
								country.setFxRates(mmmArrayList);
							}
							if( cell.getColumnIndex() ==1 )
								series.setNameString(cell.getStringCellValue());
							if( cell.getColumnIndex() ==2 )
								series.setUnitString(cell.getStringCellValue());							
							if( cell.getColumnIndex() >3  )
								a.add(cell.getNumericCellValue());				
						}	
			series.setWage(a);
			seriesList.add(series);
	
			serija++;
			if(serija==4)
			{
				serija=0;
				country.setSeries(seriesList);	
				countriesList.add(country);				
			}			
		}		
	}	
	
	public static void readFxRate(ArrayList<Country> countriesList) throws IOException
	{
		String fileName = ("FXrates.csv");
		File file = new File(fileName);
		Scanner inputStream = new Scanner(file);
		inputStream.next();
		inputStream.next();
		String prvi="a", drugi="b";
		Country country = null;
		int t =0;
		int nasao = 0;
		while(inputStream.hasNext())
		{
			String string= inputStream.next();	
			String data[]= string.split(",");

			String f1 = iso3CountryCodeToIso2CountryCode(data[0].substring(1,4));
			Locale locale1 = new Locale("en",f1);
			drugi = prvi;
			prvi= f1;
			
			if(prvi != drugi) //IF IT IS THE NEW COUNTRY
			{	
				nasao =0;
				country = new Country();		
				for(Country c : countriesList)
				{
					if(c.getNameString().equals(locale1.getDisplayCountry()))
					{ 
						country=c;
						nasao = 1;
					}			
				}				
			}
			if(nasao ==1 )
			{
				if(country.getFxRates()==null)
				{
					ArrayList<FXRate> fxRates = new ArrayList<FXRate>();
					country.setFxRates(fxRates);
				}		
				FXRate fxRate = new FXRate();
				fxRate.setYear(Integer.parseInt(data[5].substring(1, 5)));
				fxRate.setRate(Double.parseDouble(data[6]));
				country.addFxRate(fxRate);
			}
			t++;			
		}			
	}

	public static void outputCurrentPricesNCU(ArrayList<Country> countryList,Double[] rates) throws IOException {
		
		   final String columns[]= {"Country","Series","Unit","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017"};	
		   
		   Workbook workbook1 = new XSSFWorkbook();
		   Sheet sheet1 = workbook1.createSheet("Wages");
		   Font headerFont =  workbook1.createFont();
		   headerFont.setBold(true);
		   headerFont.setFontHeightInPoints((short) 14);
		   headerFont.setColor(IndexedColors.RED.getIndex());
		   
		   CellStyle headerCellStyle = workbook1.createCellStyle();
		   headerCellStyle.setFont(headerFont);
		   
		   Row headerRow = sheet1.createRow(0);
		   
		   for(int i = 0 ; i<columns.length;i++)
		   {
			   Cell cell = headerRow.createCell(i);
			   cell.setCellValue(columns[i]);
			   cell.setCellStyle(headerCellStyle);
		   }
		   
		   int rowNum  =  1;
		   
		   for( Country c: countryList)
			{				
				Row row = sheet1.createRow(rowNum++);
				row.createCell(0).setCellValue(c.getNameString()); //IME DRZAVE
					
				for(Series s : c.getSeries())
				{			
					if(s.getNameString().equals("Current prices in NCU")){				
					row.createCell(1).setCellValue(s.getNameString()); //IME SERIJE
					row.createCell(2).setCellValue("Euro"); //IME VALUTA SERIJE

					if(s.getUnitString().equals("Euro"))
					for(int i = 0 ; i < s.getWage().size();i++)
							row.createCell(i+3).setCellValue(Math.round(s.getWage().get(i))); //IME DRZAVE					
					else {					
						int a = c.getIndexOfRate(2000);
						int b=0;					
						for(int i = 0 ; i < s.getWage().size();i++)
							row.createCell(i+3).setCellValue(Math.round((s.getWage().get(i)/c.getFxRates().get(a++).getRate()) * rates[i])); 	
						}	
					}

				}
			}
		   
		   for (int i = 0 ; i < columns.length;i++)
			   sheet1.autoSizeColumn(i);

		   FileOutputStream fileOut = new FileOutputStream("NCU Wages in Euro.xlsx");
		   workbook1.write(fileOut);
		   fileOut.close();
		   workbook1.close();
 	}
	
	public static void initCountryCodeMapping() {
	    String[] countries = Locale.getISOCountries();
	    localeMap = new HashMap<String, Locale>(countries.length);
	    for (String country : countries) {
	        Locale locale = new Locale("", country);
	        localeMap.put(locale.getISO3Country().toUpperCase(), locale);
	      
	    }
	}

	public static String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {

				try {
				    return localeMap.get(iso3CountryCode).getCountry();

				} catch (Exception e) {
					// TODO: handle exception
				
		return "";
	}
	}
	
}
