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

	public static void removeHeaderAndTrailerFromRawFile(String rawFilePath) {

		try {
			List<String> allLines = Files.readAllLines(Paths.get(rawFilePath), StandardCharsets.ISO_8859_1);

			allLines.remove(allLines.size() - 2);
			allLines.remove(1);
			allLines.remove(0);

			// List<String> voterList = getVoterIDList(allLines.get(0));
			System.out.println("Voter ID" + allLines.get(0));

			// System.out.println("cleaned Voter ID" + voterList);

			System.out.println("==================");
			System.out.println("Name-->" + allLines.get(1));
			// Map<String, String> namesListMap = getNamesListMap(allLines.get(1));
			// System.out.println("cleaned Names List " + namesListMap.keySet());

			/*
			 * System.out.println("==================");
			 * System.out.println("Husband//Father Name" + allLines.get(2));
			 * 
			 * Map<String, String> namesListMap =
			 * getHusbandAndFatherListMap(allLines.get(2));
			 * System.out.println("cleaned Names List " +
			 * namesListMap.keySet()+"--->"+namesListMap.values()); Map<String, String>
			 * namesListMap1 = getHusbandAndFatherListMap(allLines.get(7));
			 * System.out.println("cleaned Names List " +
			 * namesListMap1.keySet()+"-->"+namesListMap1.values());
			 * 
			 * 
			 * Map<String, String> namesListMap2 =
			 * getHusbandAndFatherListMap(allLines.get(12));
			 * 
			 * System.out.println("cleaned Names List " +
			 * namesListMap2.keySet()+namesListMap2.values());
			 * 
			 */

			System.out.println("House No" + allLines.get(3));
			Map<String, String> houseNumberMap = getHouseNumbersMap(allLines.get(3));
			System.out.println("House No" + houseNumberMap.keySet() + "====" + houseNumberMap.values());

			System.out.println("Age and Gender " + allLines.get(4));
			
			Map<LinkedList,LinkedList> ageGenderMap = getAgeGenderMap(allLines.get(4));
			
			Set age = ageGenderMap.keySet();
			List gender = (List) ageGenderMap.values();
			System.out.println("age is "+age);
			System.out.println("gender is "+gender);
			
			/*
			 ** * *  * System.out.println("Voter ID" + allLines.get(5));
			 * System.out.println("Voter ID" + allLines.get(10));
			 * System.out.println("Voter ID" + allLines.get(15));
			 * System.out.println("Voter ID" + allLines.get(20));
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Set<Voter> cleanseRawData(String voterIDLine, String nameLine, String husbandAndFatherLine,
			String houseNumberLine, String ageAndGenderLine) {

		Set<Voter> VoterSet = new HashSet<Voter>();

		Voter firstVoter = new Voter();
		Voter secondvoter = new Voter();
		Voter thirdVoter = new Voter();

		List<String> voterList = getVoterIDList(voterIDLine);
		firstVoter.setVoterID(voterList.get(0));
		secondvoter.setVoterID(voterList.get(1));
		thirdVoter.setVoterID(voterList.get(2));

		return VoterSet;

	}

	// Set<String>
	public static List<String> getVoterIDList(String voterIDLine) {

		System.out.println("VoterIDLine");

		List<String> voterList = new ArrayList<String>();

		StringTokenizer st = new StringTokenizer(voterIDLine, " ");

		while (st.hasMoreTokens()) {
			String voiterID = st.nextToken();
			if (voiterID.length() > 8) {
				voterList.add(voiterID);

			}
		}

		return voterList;

	}

	public static Map<String, String> getNamesListMap(String namesLine) {

		Map<String, String> namesListMap = new LinkedHashMap<String, String>();

		System.out.println("getNamesList--->" + namesLine.trim());

		Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(namesLine);
		boolean b = m.find();
		System.out.println("there is one delted Voter" + b);

		namesLine = namesLine.replaceAll("Name", "").trim();

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
				.replaceAll("Father Name", "222").replaceAll("Husband", "444").replaceAll("Father", "555")
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

		List<Integer> indexList = sortHouseNUmberIndex(houseNumberLine);

		System.out.println("Processing=======>" + indexList);

		houseNumberListMap = getHouseNUmbersMap(houseNumberLine, indexList);

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

	private static List<Integer> sortHouseNUmberIndex(String husbandAndFatherLine) {

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
		int[] husbandInValidStartIndex = patternMatchingString(husbandAndFatherLine, "44");
		int[] fatherInValidStartIndex = patternMatchingString(husbandAndFatherLine, "55");

		TreeSet<Integer> ts = new TreeSet<Integer>();

		ts = combineIndexes(ts, husbandValidStartIndex);
		ts = combineIndexes(ts, fatherValidStartIndex);
		ts = combineIndexes(ts, husbandInValidStartIndex);
		ts = combineIndexes(ts, fatherInValidStartIndex);

		return removeUnwantedEntriesFromList(ts);
	}

	private static Map<String, String> get3ValidVotersHusbandFatherMap(String husbandAndFatherLine,
			List<Integer> indexList) {

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

				HFListMap.put(HorFName, "H");

			} else if (HorF.equals("222")) {
				HFListMap.put(HorFName, "F");

			} else if (HorF.equals("444")) {
				HFListMap.put(HorFName, "NH");

			} else {
				HFListMap.put(HorFName, "NF");
			}

		}

		return HFListMap;

	}

	private static Map<String, String> getHouseNUmbersMap(String houseNumberLine, List<Integer> indexList) {

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

				HFListMap.put(HorFName, "H");

			} else {
				HFListMap.put(HorFName, "F");

			}

		}

		return HFListMap;

	}

	private static TreeSet<Integer> combineIndexes(TreeSet<Integer> ts, int[] indexesArray) {

		for (int i : indexesArray) {
			ts.add(i);

		}
		return ts;

	}
	
	
	
	
	
	
	private static Map<LinkedList,LinkedList> getAgeGenderMap(String ageGenderLine){
		
		Map<LinkedList,LinkedList> ageGenderMap = new LinkedHashMap<LinkedList,LinkedList>();
		
		ageGenderLine = ageGenderLine.replaceAll("Available","");
		
		LinkedList age = new LinkedList();
		LinkedList gender = new LinkedList();
		ageGenderMap.put(age, gender);
		
		
		System.out.println(ageGenderLine);
		return ageGenderMap;
		
		
		
		
	}

}
