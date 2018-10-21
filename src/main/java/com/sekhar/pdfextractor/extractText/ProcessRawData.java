package com.sekhar.pdfextractor.extractText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.graphbuilder.struc.LinkedList;
import com.sekhar.pdfextractor.beans.Voter;

public class ProcessRawData {

	public Set<Voter> removeHeaderAndTrailerFromRawFile(String rawFilePath) {

		Set<Voter> voterBeanSet = new HashSet<Voter>();
		try {
			List<String> allLines = Files.readAllLines(Paths.get(rawFilePath), StandardCharsets.ISO_8859_1);

			allLines.remove(allLines.size() - 2);
			allLines.remove(1);
			allLines.remove(0);
			
			System.out.println("size of all lines"+allLines.size());
			
			for(int i=0;i<allLines.size()-1;i+=5) {
				
				Set<Voter> voterSet = cleanseRawData(allLines.get(i),allLines.get(i+1),allLines.get(i+2),allLines.get(i+3),allLines.get(i+4));
				
				System.out.println(voterSet);
				
				voterBeanSet.addAll(voterSet);
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return voterBeanSet;
	}

	public static Set<Voter> cleanseRawData(String voterIDLine, String nameLine, String husbandAndFatherLine,
			String houseNumberLine, String ageAndGenderLine) {

		Set<Voter> VoterSet = new HashSet<Voter>();

		Voter firstVoter = new Voter();
		Voter secondvoter = new Voter();
		Voter thirdVoter = new Voter();
		
		//Setting VoterIDs
		List<String> voterList = getVoterIDList(voterIDLine);
		if(voterList.size()==3) {
		firstVoter.setVoterID(voterList.get(0));
		secondvoter.setVoterID(voterList.get(1));
		thirdVoter.setVoterID(voterList.get(2));
		}else {
			
					
			firstVoter.setVoterID( !(NullOREmptyCheck(voterList.get(0)))?voterList.get(0):null);
			secondvoter.setVoterID( !(NullOREmptyCheck(voterList.get(1)))?voterList.get(1):null);			
			thirdVoter.setVoterID(!(NullOREmptyCheck(voterList.get(2)))?voterList.get(2):null);
			
		}
		
		//Setting Names
		
		Map<String, String> namesListMap = getNamesListMap(nameLine);
		List<String> List = new ArrayList<String>(namesListMap.keySet());
		
		firstVoter.setUserName(List.get(0));
		secondvoter.setUserName(List.get(1));
		thirdVoter.setUserName(List.get(2));
		
		List.clear();
		
		
		//House Number
		try{
			Map<String, String> houseNumberListMap1 = getHouseNumbersMap(houseNumberLine);
		
		List.addAll(houseNumberListMap1.keySet());
		firstVoter.setHouseNumber(List.get(0));
		secondvoter.setHouseNumber(List.get(1));
		thirdVoter.setHouseNumber(List.get(2));
		List.clear();
		}catch(java.lang.IndexOutOfBoundsException e) {
			System.out.println("Handling Exeption");
		}
		
		//Setting Age
		Map<Integer,List> ageGenderMap = getAgeGenderMap(ageAndGenderLine);
		List.addAll(ageGenderMap.get(0));
		firstVoter.setAge(List.get(0));
		secondvoter.setAge(List.get(1));
		thirdVoter.setAge(List.get(2));
		List.clear();
		
		//Setting Gender
		List.addAll(ageGenderMap.get(1));
		firstVoter.setGender(List.get(0));
		secondvoter.setGender(List.get(1));
		thirdVoter.setGender(List.get(2));
		List.clear();
		
		
		Map<String,String> husbandAndFatherRelationshipMap = getHusbandAndFatherListMap(husbandAndFatherLine);
		//setting dependent Names
		List.addAll(husbandAndFatherRelationshipMap.keySet());
		firstVoter.setDependentName(List.get(0));
		secondvoter.setDependentName(List.get(1));
		thirdVoter.setDependentName(List.get(2));
		List.clear();
		
		
		//setting dependent type relationships
		List.addAll(husbandAndFatherRelationshipMap.values());
		firstVoter.setDependentType(List.get(0));
		secondvoter.setDependentType(List.get(1));
		thirdVoter.setDependentType(List.get(2));
		List.clear();
		
		
		
		VoterSet.add(firstVoter);
		VoterSet.add(secondvoter);
		VoterSet.add(thirdVoter);
		return VoterSet;

	}

	// Set<String>
	public static List<String> getVoterIDList(String voterIDLine) {

		System.out.println("VoterIDLine");

		List<String> voterList = new ArrayList<String>();
		
		
		voterIDLine = voterIDLine.replaceAll("OOt ", "001");
		StringTokenizer st = new StringTokenizer(voterIDLine, " ");

		while (st.hasMoreTokens()) {
			String voiterID = st.nextToken();
			if (voiterID.length() > 8) {
				voterList.add(voiterID);

			}
		}
		
		if(voterList.size()==3) {	return voterList;			
		}else {
			int diff = 3-voterList.size();
			if(diff==1) {
				voterList.add("");
				
			}else if(diff==2) {
				voterList.add("");
				voterList.add("");
				
			}
			else {
				voterList.add("");
				voterList.add("");
				voterList.add("");
				
			}
			return voterList;

		}

		
	}

	public static Map<String, String> getNamesListMap(String namesLine) {

		Map<String, String> namesListMap = new LinkedHashMap<String, String>();

		System.out.println("getNamesList--->" + namesLine.trim());

		Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(namesLine);
		boolean b = m.find();
		System.out.println("there is one delted Voter" + b);

		namesLine = namesLine.replaceAll("Name", "").replaceAll("Photo is","").trim();

		List<String> namesList = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(namesLine.trim(), ":");

		while (st.hasMoreTokens()) {
			String name = st.nextToken().trim();
			// namesList.add(name);
			if (name.contains("\\") || name.contains(".") || name.contains("\\")) {
				System.out.println("NotValid entry voter" + name);
				namesListMap.put(name, "NotValid");
			} else {
				namesListMap.put(name, "Valid");
			}

		}

		return namesListMap;

	}

	public static Map<String, String> getHusbandAndFatherListMap(String husbandAndFatherLine) {

		Map<String, String> namesListMap = new LinkedHashMap<String, String>();
		System.out.println("getHusbandAndFatherListMap45--->" + husbandAndFatherLine.trim());
		husbandAndFatherLine = husbandAndFatherLine.replaceAll("'s", "").replaceAll("Husband Name", "111")
				.replaceAll("Father Name", "222").replaceAll("Mother Name", "333").replaceAll("Husband", "444").replaceAll("Father", "555").replaceAll("Mother", "666")
				.replaceAll(":", "").trim();
		System.out.println("Processing--->" + husbandAndFatherLine);

		List<Integer> indexList = sortFatherHusbandIndexs(husbandAndFatherLine);

		if (indexList.size() == 3) {
			namesListMap = get3ValidVotersHusbandFatherMap(husbandAndFatherLine, indexList);
		} else {
			namesListMap = get3NotValidVotersHusbandFatherMap(husbandAndFatherLine, indexList);
		}

		return namesListMap;

	}

	public static Map<String, String> getHouseNumbersMap(String houseNumberLine) {

		Map<String, String> houseNumberListMap = new LinkedHashMap<String, String>();
		System.out.println("getHouseNumbersMap--->" + houseNumberLine.trim());
		houseNumberLine = houseNumberLine.replaceAll("House Number", "454").replaceAll("Ho", "545").replaceAll(" :", "")
				.trim();
		System.out.println("Processing--->" + houseNumberLine);

		List<Integer> indexList = sortHouseNumberIndex(houseNumberLine);

		System.out.println("Processing=======>" + indexList);

		houseNumberListMap = getHouseNumbersMap(houseNumberLine, indexList);

		return houseNumberListMap;

	}

	private static int[] patternMatchingString(String Line, String pattern) {
		int[] startIndex = new int[3];
		Arrays.fill(startIndex, -1);
		try {
			Pattern word = Pattern.compile(pattern);
			Matcher match = word.matcher(Line);
			int i = 0;
			while (match.find()) {
				startIndex[i] = match.start();
				i++;
			}
		} catch (IllegalStateException e) {
			System.out.println("No results found");

		}

		return startIndex;

	}

	private static List<Integer> sortHouseNumberIndex(String husbandAndFatherLine) {

		int[] validHouseIndex = patternMatchingString(husbandAndFatherLine, "454");
		int[] inValidHouseIndex = patternMatchingString(husbandAndFatherLine, "545");

		TreeSet<Integer> ts = new TreeSet<Integer>();
		ts = combineIndexes(ts, validHouseIndex);
		ts = combineIndexes(ts, inValidHouseIndex);

		return removeUnwantedEntriesFromList(ts);
	}

	private static List<Integer> removeUnwantedEntriesFromList(TreeSet<Integer> ts) {

		List<Integer> indexList = new ArrayList<Integer>();
		indexList.addAll(ts);
		Integer notrequired = new Integer(-1);
		indexList.remove(notrequired);
		Collections.sort(indexList);
		return indexList;

	}

	private static List<Integer> sortFatherHusbandIndexs(String husbandAndFatherLine) {

		int[] husbandValidStartIndex = patternMatchingString(husbandAndFatherLine, "111");
		int[] fatherValidStartIndex = patternMatchingString(husbandAndFatherLine, "222");
		int[] motherValidStartIndex = patternMatchingString(husbandAndFatherLine, "333");
		int[] husbandInValidStartIndex = patternMatchingString(husbandAndFatherLine, "444");
		int[] fatherInValidStartIndex = patternMatchingString(husbandAndFatherLine, "555");
		int[] motherInValidStartIndex = patternMatchingString(husbandAndFatherLine, "666");

		TreeSet<Integer> ts = new TreeSet<Integer>();

		ts = combineIndexes(ts, husbandValidStartIndex);
		ts = combineIndexes(ts, fatherValidStartIndex);
		ts = combineIndexes(ts, motherValidStartIndex);
		ts = combineIndexes(ts, husbandInValidStartIndex);
		ts = combineIndexes(ts, fatherInValidStartIndex);
		ts = combineIndexes(ts, motherInValidStartIndex);

		return removeUnwantedEntriesFromList(ts);
	}

	private static Map<String, String> get3ValidVotersHusbandFatherMap(String husbandAndFatherLine,
			List<Integer> indexList) {

		System.out.println("Processing from get3ValidVotersHusbandFatherMap====================================>>");
		System.out.println(husbandAndFatherLine+"========"+indexList);
		
		Map<String, String> HFListMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < 3; i++) {
			int index = indexList.get(i);
			String HorF = husbandAndFatherLine.substring(index, index + 3);
			String HorFName = null;
			if (i < 2) {
				HorFName = husbandAndFatherLine.substring(index + 3, indexList.get(i + 1));
			} else {
				HorFName = husbandAndFatherLine.substring(index + 3);
			}

			HorFName = HorFName.replaceAll("[-+.^:,?']", "").trim();
			
			

			if (HorF.equals("111")) {
				
				if(!HFListMap.containsKey(HorFName)) {
				HFListMap.put(HorFName, "Husband");}else {
					HorFName=HorFName+" ";
					HFListMap.put(HorFName, "Husband");
				}

			} else if (HorF.equals("222")) {
				if(!HFListMap.containsKey(HorFName)) {
					HFListMap.put(HorFName, "Father");}else {
						HorFName=HorFName+" ";
						HFListMap.put(HorFName, "Father");
					}

			} else if (HorF.equals("333")) {
				if(!HFListMap.containsKey(HorFName)) {
					HFListMap.put(HorFName, "Mother");}else {
						HorFName=HorFName+" ";
						HFListMap.put(HorFName, "Mother");
					}

			}  else {
				HFListMap.put(HorFName, "Invalid");
			}

		}

		return HFListMap;

	}

	private static Map<String, String> getHouseNumbersMap(String houseNumberLine, List<Integer> indexList) {

		Map<String, String> houseListMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < 3; i++) {
			int index = indexList.get(i);
			String validHouse = houseNumberLine.substring(index, index + 3);
			String houseNumber = null;
			if (i < 2) {
				houseNumber = houseNumberLine.substring(index + 3, indexList.get(i + 1)).trim();
			} else {
				houseNumber = houseNumberLine.substring(index + 3).trim();
			}

			if (validHouse.equals("454")) {

				houseNumber = houseNumber.replaceAll("[^0-9]", "-");
				System.out.println(houseNumber);

				if (houseNumber.equals("-")) {

					if (houseListMap.containsKey("No Address"))
						houseListMap.put("No Address", "VH");
					else if (houseListMap.containsKey("NoAddress"))
						houseListMap.put("NoAddress", "VH");

					else
						houseListMap.put("Not Available", "VH");

				} else
					houseListMap.put(houseNumber, "VH");

			} else {
				houseListMap.put(houseNumber, "NVH");

			}

		}

		return houseListMap;

	}

	private static Map<String, String> get3NotValidVotersHusbandFatherMap(String husbandAndFatherLine,
			List<Integer> indexList) {

		Map<String, String> HFListMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < 3; i++) {
			int index = indexList.get(i);
			String HorF = husbandAndFatherLine.substring(index, index + 3);
			String HorFName = null;
			if (i < 2) {
				HorFName = husbandAndFatherLine.substring(index + 4, indexList.get(i + 1));
			} else {
				HorFName = husbandAndFatherLine.substring(index + 4);
			}

			HorFName = HorFName.replaceAll("[-+.^:,?']", "").trim();

			if (HorF.equals("111")) {

				HFListMap.put(HorFName, "Husband");

			} else if(HorF.equals("222")) {
				HFListMap.put(HorFName, "Father");

			}else HFListMap.put(HorFName, "Mother");

		}

		return HFListMap;

	}

	private static TreeSet<Integer> combineIndexes(TreeSet<Integer> ts, int[] indexesArray) {

		for (int i : indexesArray) {
			ts.add(i);

		}
		return ts;

	}

	private static Map<Integer, List> getAgeGenderMap(String ageGenderLine) {
		
		System.out.println("before"+ageGenderLine);

		Map<LinkedList, LinkedList> ageGenderMap = new LinkedHashMap<LinkedList, LinkedList>();

		ageGenderLine = ageGenderLine.replaceAll("Available", "").replaceAll("Avallable","").replaceAll("[^a-zA-Z0-9]", "")
				.trim();

		ageGenderLine = ageGenderLine.replaceAll("Age", "ZZZ").replaceAll("A9", "age").replaceAll("Ag", "age");

		
		System.out.println("after processing===>"+ageGenderLine);
		
		int[] validAgeIndex = patternMatchingString(ageGenderLine, "ZZZ");
		int[] inValidAgeIndex = patternMatchingString(ageGenderLine, "age");
		TreeSet<Integer> ts = new TreeSet<Integer>();

		ts = combineIndexes(ts, validAgeIndex);
		ts = combineIndexes(ts, inValidAgeIndex);
		List<Integer> ageList = removeUnwantedEntriesFromList(ts);

		System.out.println(ageGenderLine);
		for (int i : ageList)
			System.out.println(i);

		return  getAgeAndGenderValues(ageGenderLine, ageList);

		

	}

	private static Map<Integer, List> getAgeAndGenderValues(String ageAndGenderLine, List<Integer> indexList) {
		
		Map<Integer, List> ageAndGenderMap = new LinkedHashMap<Integer, List>();
		String age=null;
		String Gender=null;
		
		List<String> ageList= new java.util.LinkedList<String>();
		List<String> genderList= new java.util.LinkedList<String>();
		
		
		for (int i = 0; i < 3; i++) {
			int index = indexList.get(i);
			if(ageAndGenderLine.substring(index, index+3).equals("ZZZ")) {
			
				age = ageAndGenderLine.substring(index+3, index+5);
				if(i<2)
				Gender = ageAndGenderLine.substring(index+11,indexList.get(i+1));
				else
				{
					if(ageAndGenderLine.length()>index+11)
					Gender = ageAndGenderLine.substring(index+11,ageAndGenderLine.length());			
					else
						Gender = "invalid";
				}
				
			}
			else {
				age="invalid";
				Gender = "invalid";
			}
			System.out.println(age+"=======>"+Gender);
			ageList.add(age);
			genderList.add(Gender);
		}
		ageAndGenderMap.put(new Integer(0), ageList);
		ageAndGenderMap.put(new Integer(1), genderList);

		return ageAndGenderMap;

	}
	
	
	private static boolean NullOREmptyCheck(String str) {
		
		if (str==null && (str.equals(""))) 
			return true;
			
			else
				return false;
		}
	

}