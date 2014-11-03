package com.end507.project1.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.end507.project1.model.HouseholdObject;

/**
 * 
 * @author ceyhunkarimov
 *	Class for IO operations. 
 *	Mainly for reading the input from the specified location
 */
public class FileReader {
	
	/**
	 * 
	 * @param pathToInput 
	 * @return list of HouseholdObjects
	 * Read each line of input and construct HouseholdObject object, put them in List and return
	 */
	public static List<HouseholdObject> readInputFile(Path pathToInput){
	    Charset charset = Charset.forName("ISO-8859-1");
	    List<HouseholdObject> allObjects = new ArrayList<>();
	    try {
	      List<String> lines = Files.readAllLines(pathToInput, charset);
	      for (String line : lines) {
	    	// records are seperated with ';', so seperate them into Strings
	        String[] seperatedValues = line.split(";");
	        // First element is date object,although we do not use it as a feature vector
	        Date opDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).parse(seperatedValues[0].concat(" ").concat(seperatedValues[1]));
	        //Construct HouseholdObject object with its constructor
	        HouseholdObject ho = new HouseholdObject(opDate, new Double(seperatedValues[2]), new Double(seperatedValues[3]), 
	        		new Double(seperatedValues[4]), new Double(seperatedValues[5]), new Double(seperatedValues[6]), 
	        		new Double(seperatedValues[7]), new Double(seperatedValues[8]));
	        allObjects.add(ho);
	      }
	    } catch (Exception e) {
	      System.out.println(e);
	    }
	    return allObjects;

	}
}
