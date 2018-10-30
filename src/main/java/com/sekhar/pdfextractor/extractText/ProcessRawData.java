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

	public List<String> allLines = new ArrayList<String>();

	public List<Voter> removeHeaderAndTrailerFromRawFile(String rawFilePath) {

		List<Voter> voterBeanSet = new ArrayList<Voter>();
		String pageNumber=null;
		try {
			this.allLines = Files.readAllLines(Paths.get(rawFilePath), StandardCharsets.ISO_8859_1);

			System.out.println("Handling Image " + rawFilePath);
			System.out.println("before  processing list size " + this.allLines.size());

			for (int i = 0; i < this.allLines.size() - 1; i++) {

				if (this.allLines.get(i).trim().equals("")) {
					this.allLines.remove(i);
				}

			}
			System.out.println("after removing empty lines from file " + this.allLines.size());

			// System.out.println("allLines.remove(allLines.size() - 2)" +
			// allLines.get(allLines.size() - 2));
			
			pageNumber = allLines.get(allLines.size() - 2);
			pageNumber = pageNumber.substring(pageNumber.lastIndexOf("Page"));
			
			allLines.remove(allLines.size() - 2);

			// System.out.println("allLines.remove(1);" + allLines.get(1));
			allLines.remove(1);

			// System.out.println("allLines.remove(0);" + allLines.get(0));
			allLines.remove(0);

			// System.out.println("final list size " + allLines.size());

			// System.out.println("The last line is " + allLines.get(allLines.size() - 1));

			if ((allLines.size() - 1) % 5 != 0) {

				System.out.println(" adjusting the invalid lines...let me try");

				this.adjustInputLines();
			}

			for (int i = 0; i < allLines.size() - 1; i += 5) {

				System.out.println("Address Input Data is " + allLines.get(i + 3));

				List<Voter> voterSet = cleanseRawData(allLines.get(i), allLines.get(i + 1), allLines.get(i + 2),
						allLines.get(i + 3), allLines.get(i + 4),pageNumber);

				System.out.println(i + //
						"===============================================================================");
				System.out.println(voterSet);
				System.out.println("===============================================================================");

				voterBeanSet.addAll(voterSet);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		// return voterBeanSet;
		return voterBeanSet;
	}

	public static List<Voter> cleanseRawData(String voterIDLine, String nameLine, String husbandAndFatherLine,
			String houseNumberLine, String ageAndGenderLine,String pageNumber) {

		Voter firstVoter = new Voter();
		Voter secondvoter = new Voter();
		Voter thirdVoter = new Voter();
		
		firstVoter.setPageNumber(pageNumber);
		secondvoter.setPageNumber(pageNumber);
		thirdVoter.setPageNumber(pageNumber);

		Map<Integer, Voter> voterersMap = new LinkedHashMap<Integer, Voter>();

		voterersMap.put(0, firstVoter);
		voterersMap.put(1, secondvoter);
		voterersMap.put(2, thirdVoter);

		// Setting VoterIDs
		List<String> voterList = getVoterIDList(voterIDLine);
		for (int i = 0; i < voterList.size(); i++) {

			Voter v = voterersMap.get(i);
			v.setVoterID(voterList.get(i));
			voterersMap.put(i, v);

		}

		// Setting Names

		try {
			List<String> List = getNamesListMap(nameLine);
			;

			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setUserName(List.get(i));
				voterersMap.put(i, v);

			}

			List.clear();
		} catch (java.lang.IndexOutOfBoundsException e) {
			System.out.println("getNamesListMap--->Handling Exeption");
		}

		// House Number
		try {
			// Map<String, String> houseNumberListMap1 =
			// getHouseNumbersMap(houseNumberLine);

			List<String> List = getHouseNumbersMap(houseNumberLine);

			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setHouseNumber(List.get(i));
				voterersMap.put(i, v);

			}

			List.clear();

		} catch (java.lang.IndexOutOfBoundsException e) {
			System.out.println("getHouseNumbersMap--> Handling Exeption");

			System.out.println(e.toString());
		}

		// Setting Age
		try {
			List<String> List = new java.util.LinkedList<>();
			Map<Integer, List> ageGenderMap = getAgeGenderMap(ageAndGenderLine);
			List.addAll(ageGenderMap.get(0));
			// firstVoter.setAge(List.get(0));
			// secondvoter.setAge(List.get(1));
			// thirdVoter.setAge(List.get(2));

			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setAge(List.get(i));
				voterersMap.put(i, v);

			}

			List.clear();
			List.addAll(ageGenderMap.get(1));

			// Setting Gender
			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setGender(List.get(i));
				voterersMap.put(i, v);

			}
			List.clear();

		} catch (java.lang.IndexOutOfBoundsException e) {
			System.out.println("getAgeGenderMap-->Handling Exeption");
		}

		try {
			Map<String, String> husbandAndFatherRelationshipMap = getHusbandAndFatherListMap(husbandAndFatherLine);
			List<String> List = new java.util.LinkedList<>();
			// setting dependent Names
			List.addAll(husbandAndFatherRelationshipMap.keySet());
			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setDependentName(List.get(i));
				voterersMap.put(i, v);

			}

			List.clear();

			// setting dependent type relationships
			List.addAll(husbandAndFatherRelationshipMap.values());
			for (int i = 0; i < List.size(); i++) {

				Voter v = voterersMap.get(i);
				v.setDependentType(List.get(i));
				voterersMap.put(i, v);

			}
			List.clear();

		} catch (java.lang.IndexOutOfBoundsException e) {
			System.out.println("getAgeGenderMap--->Handling Exeption");
		}

		List<Voter> list = new ArrayList<Voter>();
		
		
		for (int i = 0; i < voterersMap.size(); i++) {

			if( !(voterersMap.get(i).getAge().equalsIgnoreCase("Invalid")) && !(voterersMap.get(i).getGender().equalsIgnoreCase("Invalid"))) {
				list.add(voterersMap.get(i));
			}
			
		}
		
		
		return list;

	}

	// Set<String>
	public static List<String> getVoterIDList(String voterIDLine) {

		//// System.out.println("VoterIDLine");

		List<String> voterList = new ArrayList<String>();

		voterIDLine = voterIDLine.replaceAll("OOt ", "001").replaceAll("‘9", "").trim();
		StringTokenizer st = new StringTokenizer(voterIDLine, " ");

		while (st.hasMoreTokens()) {
			String voiterID = st.nextToken();
			if (voiterID.length() > 6) {
				voterList.add(voiterID);

			}
		}

		if (voterList.size() == 3) {
			return voterList;
		} else {
			int diff = 3 - voterList.size();
			if (diff == 1) {
				voterList.add("");

			} else if (diff == 2) {
				voterList.add("");
				voterList.add("");

			} else {
				voterList.add("");
				voterList.add("");
				voterList.add("");

			}
			return voterList;

		}

	}

	public static List<String> getNamesListMap(String namesLine) {

		// Map<String, String> namesListMap = new LinkedHashMap<String, String>();
		namesLine = namesLine.replaceAll(":", "").replaceAll("Photo is", "").trim();
		List<Integer> indexList = getNamesIndex(namesLine);
		;
		List<String> nameList = null;

		if (indexList.size() == 3) {
			nameList = getNamesAsList(namesLine, indexList);
		} else {
			nameList = getNamesAsList(namesLine, indexList);
			while (nameList.size() == 3) {
				nameList.add("Invalid");
			}

		}

		return nameList;

	}

	public static List<String> getNamesAsList(String namesList, List<Integer> indexList) {

		String name = null;
		List<String> nameList = new ArrayList<String>();

		for (int i = 0; i < indexList.size(); i++) {
			int index = indexList.get(i);

			if (i < indexList.size() - 1) {
				name = namesList.substring(index + 4, indexList.get(i + 1)).trim();
			} else {
				name = namesList.substring(index + 4).trim();
			}

			nameList.add(name);

		}

		if (nameList.size() == 3) {
			return nameList;
		} else {
			while (nameList.size() == 3) {
				nameList.add("Invalid");
			}

			return nameList;

		}

	}

	public static Map<String, String> getHusbandAndFatherListMap(String husbandAndFatherLine) {

		Map<String, String> namesListMap = new LinkedHashMap<String, String>();
		// System.out.println("getHusbandAndFatherListMap45--->" +
		// husbandAndFatherLine.trim());
		husbandAndFatherLine = husbandAndFatherLine.replaceAll("'s", "").replaceAll("Husband Name", "111")
				.replaceAll("Father Name", "222").replaceAll("Mother Name", "333").replaceAll("Husband", "444")
				.replaceAll("Father", "555").replaceAll("Mother", "666").replaceAll(":", "").trim();
		// System.out.println("Processing--->" + husbandAndFatherLine);

		List<Integer> indexList = sortFatherHusbandIndexs(husbandAndFatherLine);

		if (indexList.size() == 3) {
			namesListMap = get3ValidVotersHusbandFatherMap(husbandAndFatherLine, indexList);
		} else {
			namesListMap = get3NotValidVotersHusbandFatherMap(husbandAndFatherLine, indexList);
		}

		return namesListMap;

	}

	public static List<String> getHouseNumbersMap(String houseNumberLine) {

		// List<String> houseNumberList = new ArrayList<String>();
		// System.out.println("getHouseNumbersMap--->" + houseNumberLine.trim());
		houseNumberLine = houseNumberLine.replaceAll("House Number", "XXX").replaceAll("Ho", "YYY")
				.replaceAll("H°", "YYY").replaceAll(" :", "").trim();
		// System.out.println("Processing--->" + houseNumberLine);

		List<Integer> indexList = sortHouseNumberIndex(houseNumberLine);

		return getHouseNumbersMap(houseNumberLine, indexList);

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
			// System.out.println("No results found");

		}

		return startIndex;

	}

	private static List<Integer> sortHouseNumberIndex(String husbandAndFatherLine) {

		int[] validHouseIndex = patternMatchingString(husbandAndFatherLine, "XXX");
		int[] inValidHouseIndex = patternMatchingString(husbandAndFatherLine, "YYY");

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

	private static List<Integer> getNamesIndex(String namesLine) {

		int[] nameStartIndex = patternMatchingString(namesLine, "Name");

		TreeSet<Integer> ts = new TreeSet<Integer>();
		ts = combineIndexes(ts, nameStartIndex);
		return removeUnwantedEntriesFromList(ts);

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

		// System.out.println("Processing from
		// get3ValidVotersHusbandFatherMap====================================>>");
		// System.out.println(husbandAndFatherLine + "========" + indexList);

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

				if (!HFListMap.containsKey(HorFName)) {
					HFListMap.put(HorFName, "Husband");
				} else {
					HorFName = HorFName + " ";
					HFListMap.put(HorFName, "Husband");
				}

			} else if (HorF.equals("222")) {
				if (!HFListMap.containsKey(HorFName)) {
					HFListMap.put(HorFName, "Father");
				} else {
					HorFName = HorFName + " ";
					HFListMap.put(HorFName, "Father");
				}

			} else if (HorF.equals("333")) {
				if (!HFListMap.containsKey(HorFName)) {
					HFListMap.put(HorFName, "Mother");
				} else {
					HorFName = HorFName + " ";
					HFListMap.put(HorFName, "Mother");
				}

			} else {
				HFListMap.put(HorFName, "Invalid");
			}

		}

		return HFListMap;

	}

	private static List<String> getHouseNumbersMap(String houseNumberLine, List<Integer> indexList) {

		List<String> houseList = new ArrayList<String>();

		for (int i = 0; i < indexList.size(); i++) {
			int index = indexList.get(i);
			String validHouse = houseNumberLine.substring(index, index + 3);
			String houseNumber = null;
			if (i < indexList.size() - 1) {
				houseNumber = houseNumberLine.substring(index + 3, indexList.get(i + 1)).trim();
			} else {
				houseNumber = houseNumberLine.substring(index + 3).trim();
			}

			if (validHouse.equals("XXX")) {

				houseNumber=houseNumber.replaceAll("Photo is not", "").replaceAll("Photo is","").trim();
				// System.out.println(houseNumber);
				houseList.add(houseNumber);

			} else if (validHouse.equals("YYY")) {
				houseList.add("Invalid");

			} else {
				houseList.add("Invalid");
			}

		}

		return houseList;

	}

	private static Map<String, String> get3NotValidVotersHusbandFatherMap(String husbandAndFatherLine,
			List<Integer> indexList) {

		Map<String, String> HFListMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < indexList.size(); i++) {
			int index = indexList.get(i);
			String HorF = husbandAndFatherLine.substring(index, index + 3);
			String HorFName = null;
			if (i < indexList.size()-1) {
				HorFName = husbandAndFatherLine.substring(index + 4, indexList.get(i + 1));
			} else {
				HorFName = husbandAndFatherLine.substring(index + 4);
			}

			HorFName = HorFName.replaceAll("[-+.^:,?']", "").trim();

			if (HorF.equals("111")) {

				HFListMap.put(HorFName, "Husband");

			} else if (HorF.equals("222")) {
				HFListMap.put(HorFName, "Father");

			} else
				HFListMap.put(HorFName, "Mother");

		}

		return HFListMap;

	}

	private static TreeSet<Integer> combineIndexes(TreeSet<Integer> ts, int[] indexesArray) {

		for (int i : indexesArray) {
			ts.add(i);

		}
		return ts;

	}

	public static Map<Integer, List> getAgeGenderMap(String ageGenderLine) {

		// System.out.println("before" + ageGenderLine);

		Map<LinkedList, LinkedList> ageGenderMap = new LinkedHashMap<LinkedList, LinkedList>();

		ageGenderLine = ageGenderLine.replaceAll("Available", "").replaceAll("Avallable", "")
				.replaceAll("[^a-zA-Z0-9]", "").trim();

		ageGenderLine = ageGenderLine.replaceAll("Age", "ZZZ").replaceAll("A9", "###").replaceAll("Ag", "###");

		ageGenderLine = ageGenderLine.replaceAll("FEMALE", "XX").replaceAll("MALE", "YY").replaceAll("I", "").trim();
		// System.out.println("after processing===>" + ageGenderLine);

		int[] validAgeIndex = patternMatchingString(ageGenderLine, "ZZZ");
		int[] inValidAgeIndex = patternMatchingString(ageGenderLine, "###");
		TreeSet<Integer> ts = new TreeSet<Integer>();

		ts = combineIndexes(ts, validAgeIndex);
		ts = combineIndexes(ts, inValidAgeIndex);
		List<Integer> ageList = removeUnwantedEntriesFromList(ts);

		// System.out.println(ageGenderLine);
		// for (int i : ageList)
		// System.out.println(i);

		return getAgeAndGenderValues(ageGenderLine, ageList);

	}

	private static Map<Integer, List> getAgeAndGenderValues(String ageAndGenderLine, List<Integer> indexList) {

		Map<Integer, List> ageAndGenderMap = new LinkedHashMap<Integer, List>();
		String age = null;
		String Gender = null;

		List<String> ageList = new java.util.LinkedList<String>();
		List<String> genderList = new java.util.LinkedList<String>();
		// Need to make a fix for this issueif()
		if (indexList.size() == 3) {
			for (int i = 0; i < 3; i++) {
				int index = indexList.get(i);
				if (ageAndGenderLine.substring(index, index + 3).equals("ZZZ")) {

					age = ageAndGenderLine.substring(index + 3, index + 5);

					if (i < 2)
						Gender = (ageAndGenderLine.substring(index + 11, indexList.get(i + 1))).equals("XX") ? "FEMALE"
								: "MALE";
					else {
						if (ageAndGenderLine.length() > index + 11)
							Gender = (ageAndGenderLine.substring(index + 11, ageAndGenderLine.length())).equals("XX")
									? "FEMALE"
									: "MALE";
						else
							Gender = "invalid";
					}

				} else {
					age = "invalid";
					Gender = "invalid";
				}
				// System.out.println(age + "=======>" + Gender);
				ageList.add(age);
				genderList.add(Gender);
			}
		} else {
			if (indexList.size() == 1 && indexList.get(0) == 0) {
				if (ageAndGenderLine.substring(0, 3).equals("ZZZ")) {
					age = ageAndGenderLine.substring(3, 5);
					Gender = (ageAndGenderLine.substring(11, 13).trim().equals("XX")) ? "FEMALE" : "MALE";

					ageList.add(age);
					genderList.add(Gender);
					ageList.add("Invalid");
					genderList.add("Invalid");
					ageList.add("Invalid");
					genderList.add("Invalid");

				} else {
					return get3InvalidAgeGenderMAP();
				}

			} else if (indexList.size() == 1 && !(indexList.get(0) == 0)) {

				int index = indexList.get(0);
				if (index < 26) {

					if (ageAndGenderLine.substring(index, index + 3).equals("ZZZ")) {
						age = ageAndGenderLine.substring(index + 3, index + 5);
						Gender = (ageAndGenderLine.substring(index + 11, index + 13).trim().equals("XX")) ? "FEMALE"
								: "MALE";

						ageList.add("Invalid");
						genderList.add("Invalid");
						ageList.add(age);
						genderList.add(Gender);
						ageList.add("Invalid");
						genderList.add("Invalid");

					} else {
						return get3InvalidAgeGenderMAP();
					}

				} else {
					if (ageAndGenderLine.substring(index, index + 3).equals("ZZZ")) {
						age = ageAndGenderLine.substring(index + 3, index + 5);
						Gender = (ageAndGenderLine.substring(index + 11, index + 13).trim().equals("XX")) ? "FEMALE"
								: "MALE";

						ageList.add("Invalid");
						genderList.add("Invalid");
						ageList.add("Invalid");
						genderList.add("Invalid");
						ageList.add(age);
						genderList.add(Gender);

					} else {
						return get3InvalidAgeGenderMAP();
					}

				}

			}

			else {

				int frstIndex = indexList.get(0);
				int secondIndex = indexList.get(1);
				if (frstIndex == 0 && secondIndex == 13) {

					if (ageAndGenderLine.substring(frstIndex, frstIndex + 3).equals("ZZZ")) {
						age = ageAndGenderLine.substring(frstIndex + 3, frstIndex + 5);
						Gender = (ageAndGenderLine.substring(frstIndex + 11, frstIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(0, age);
						genderList.add(0, Gender);

					}

					else {

						ageList.add(0, "Invalid");
						genderList.add(0, "Invalid");
					}

					if (ageAndGenderLine.substring(secondIndex, secondIndex + 3).equals("ZZZ")) {

						age = ageAndGenderLine.substring(secondIndex + 3, secondIndex + 5);
						Gender = (ageAndGenderLine.substring(secondIndex + 11, secondIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(1, age);
						genderList.add(1, Gender);

					} else {

						ageList.add(1, "Invalid");
						genderList.add(1, "Invalid");
					}

					ageList.add(2, "Invalid");
					genderList.add(2, "Invalid");

				} else if (frstIndex > 0 && secondIndex > 13) {

					ageList.add(0, "Invalid");
					genderList.add(0, "Invalid");

					if (ageAndGenderLine.substring(frstIndex, frstIndex + 3).equals("ZZZ")) {
						age = ageAndGenderLine.substring(frstIndex + 3, frstIndex + 5);
						Gender = (ageAndGenderLine.substring(frstIndex + 11, frstIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(1, age);
						genderList.add(1, Gender);

					}

					else {

						ageList.add(1, "Invalid");
						genderList.add(1, "Invalid");
					}

					if (ageAndGenderLine.substring(secondIndex, secondIndex + 3).equals("ZZZ")) {

						age = ageAndGenderLine.substring(secondIndex + 3, secondIndex + 5);
						Gender = (ageAndGenderLine.substring(secondIndex + 11, secondIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(2, age);
						genderList.add(2, Gender);

					} else {

						ageList.add(2, "Invalid");
						genderList.add(2, "Invalid");
					}

				} else if (frstIndex == 0 && secondIndex < 26) {

					if (ageAndGenderLine.substring(frstIndex, frstIndex + 3).equals("ZZZ")) {
						age = ageAndGenderLine.substring(frstIndex + 3, frstIndex + 5);
						Gender = (ageAndGenderLine.substring(frstIndex + 11, frstIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(0, age);
						genderList.add(0, Gender);

					}

					else {

						ageList.add(0, "Invalid");
						genderList.add(0, "Invalid");
					}

					ageList.add(1, "Invalid");
					genderList.add(1, "Invalid");

					if (ageAndGenderLine.substring(secondIndex, secondIndex + 3).equals("ZZZ")) {

						age = ageAndGenderLine.substring(secondIndex + 3, secondIndex + 5);
						Gender = (ageAndGenderLine.substring(secondIndex + 11, secondIndex + 13).trim().equals("XX"))
								? "FEMALE"
								: "MALE";

						ageList.add(2, age);
						genderList.add(2, Gender);

					} else {

						ageList.add(2, "Invalid");
						genderList.add(2, "Invalid");
					}

				} else {

					// System.out.println("Dont know what to do ????");

				}

			}

		}

		ageAndGenderMap.put(new Integer(0), ageList);

		ageAndGenderMap.put(new Integer(1), genderList);

		return ageAndGenderMap;

	}

	public static Map<Integer, List> get3InvalidAgeGenderMAP() {

		Map<Integer, List> ageAndGenderMap = new LinkedHashMap<Integer, List>();
		List<String> ageList = new java.util.LinkedList<String>();
		List<String> genderList = new java.util.LinkedList<String>();
		ageList.add("Invalid");
		genderList.add("Invalid");
		ageList.add("Invalid");
		genderList.add("Invalid");
		ageList.add("Invalid");
		genderList.add("Invalid");

		ageAndGenderMap.put(new Integer(0), ageList);
		ageAndGenderMap.put(new Integer(1), genderList);

		return ageAndGenderMap;

	}

	private static boolean NullOREmptyCheck(String str) {

		if (str == null && (str.equals("")))
			return true;

		else
			return false;
	}

	public static List get3InvalidObject() {

		List<String> inValidList = new java.util.LinkedList<String>();

		inValidList.add("Invalid");
		inValidList.add("Invalid");
		inValidList.add("Invalid");

		return inValidList;

	}

	public void adjustInputLines() {

		for (int i = 5; i < this.allLines.size() - 1; i += 5) {

			String keyword1 = "Age";
			String keyword2 = "Ag";

			String ageLine = this.allLines.get(i).replaceAll(":", " ");

			Boolean found = Arrays.asList(ageLine.split(" ")).contains(keyword1);
			if (found) {
				System.out.println("Keyword matched the string:: Age ");
				System.out.println("===============================================================================");
				System.out.println("Extra Age Line" + this.allLines.get(i));
				System.out.println("Age Line" + this.allLines.get(i - 1));
				System.out.println("House NumberLine" + this.allLines.get(i - 2));
				System.out.println("Father//Mother//Husband Line" + this.allLines.get(i - 3));
				System.out.println("Name Line" + this.allLines.get(i - 4));
				System.out.println("Voter ID Line" + this.allLines.get(i - 5));
				System.out.println("===============================================================================");

				// blockModification(this.allLines.get(i - 5), this.allLines.get(i - 4),
				// this.allLines.get(i - 3),
				// this.allLines.get(i - 2), this.allLines.get(i - 1), this.allLines.get(i));

				for (int j = 0; j < 6; j++) {

					System.out.println("deleted starting here  Age");
					this.allLines.remove(i - j);
				}

				i = i - 5;

			}

			Boolean found1 = Arrays.asList(ageLine.split(" ")).contains(keyword2);
			if (found1) {
				System.out.println("Keyword matched the string:: Ag");
				System.out.println("===============================================================================");
				System.out.println("Extra Age Line" + this.allLines.get(i));
				System.out.println("Age Line" + this.allLines.get(i - 1));
				System.out.println("House NumberLine" + this.allLines.get(i - 2));
				System.out.println("Father//Mother//Husband Line" + this.allLines.get(i - 3));
				System.out.println("Name Line" + this.allLines.get(i - 4));
				System.out.println("Name Line" + this.allLines.get(i - 5));
				System.out.println("===============================================================================");

				blockModification(this.allLines.get(i - 5), this.allLines.get(i - 4), this.allLines.get(i - 3),
						this.allLines.get(i - 2), this.allLines.get(i - 1), this.allLines.get(i));

				for (int j = 0; j < 6; j++) {

					System.out.println("deleted starting here  Ag");
					this.allLines.remove(i - j);
				}

				i = i - 5;

			}

		}

	}

	public void blockModification(String voterIdLine, String nameLine, String DependencyLine, String houseLine,
			String ageGenderLine, String extraAgwLine) {
		Voter v1 = new Voter();
		Voter v2 = new Voter();
		Voter v3 = new Voter();

		String[] arrayDependentName = DependencyLine.split("Husband's Name");
		List<String> list2 = new ArrayList<String>(Arrays.asList(arrayDependentName));
		list2.removeAll(Arrays.asList(""));
		arrayDependentName = list2.toArray(new String[0]);
		v1.setDependentName(arrayDependentName[0].replace(":", "").trim());
		v2.setDependentName(arrayDependentName[1].replace(":", "").trim());
		v3.setDependentName(arrayDependentName[2].replace(":", "").trim());

		String[] arrayHouse = houseLine.split("House");
		List<String> list3 = new ArrayList<String>(Arrays.asList(arrayHouse));
		list3.removeAll(Arrays.asList(""));
		arrayHouse = list3.toArray(new String[0]);

		if (arrayHouse[0].contains("Number")) {
			v1.setHouseNumber(arrayHouse[0].replace(":", "").replace("Number", "").trim());
		} else {
			v1.setDependentName(v1.getDependentName().concat(" " + arrayHouse[0].replace("Photo is", "").trim()));
		}

		if (arrayHouse[1].contains("Number")) {
			v2.setHouseNumber(arrayHouse[1].replace(":", "").replace("Number", "").trim());
		} else {
			v2.setDependentName(v2.getDependentName().concat(" " + arrayHouse[0].replace("Photo is", "").trim()));
		}

		if (arrayHouse[2].contains("Number")) {
			v3.setHouseNumber(arrayHouse[0].replace(":", "").replace("Number", "").trim());
		} else {
			v3.setDependentName(v2.getDependentName().concat(" " + arrayHouse[0].replace("Photo is", "").trim()));
		}

		// Age ,Gender String d
		String[] arrayAge = ageGenderLine.split("Age");
		List<String> list4 = new ArrayList<String>(Arrays.asList(arrayAge));
		list4.removeAll(Arrays.asList(""));
		arrayAge = list4.toArray(new String[0]);
		if (arrayAge[0].contains("House")) {
			v1.setHouseNumber(arrayAge[0].replace("House Number", "").replace(":", "").replace("Photo is", "")
					.replace("Available", "").trim());
		} else {
			String string = arrayAge[0].replaceAll("[\\s]+[0-9{1}][\\s]+", "").replace("Available", "").trim();
			Pattern p = Pattern.compile("[0-9]{2}");
			Matcher m = p.matcher(string);
			if (m.find()) {
				System.out.println(m.group(0));
				v1.setAge(m.group(0));
			}
			if (string.contains("Gender")) {
				String gender = string.replace(":", "").replaceAll("[0-9]{2}", "").trim();
				v1.setGender(gender.substring(gender.indexOf("Gender") + 6, gender.length()).trim());
				System.out.println(v1.getGender());
			}
		}

		if (arrayAge[1].contains("House")) {
			v2.setHouseNumber(arrayAge[0].replace("House Number", "").replace(":", "").replace("Available", "").trim());
		} else {
			String string = arrayAge[1].replaceAll("[\\s]+[0-9{1}][\\s]+", "").replace("Available", "").trim();
			Pattern p = Pattern.compile("[0-9]{2}");
			Matcher m = p.matcher(string);
			if (m.find()) {
				System.out.println(m.group(0));
				v2.setAge(m.group(0));
			}
			if (string.contains("Gender")) {
				String gender = string.replace(":", "").replaceAll("[0-9]{2}", "").trim();
				v2.setGender(gender.substring(gender.indexOf("Gender") + 6, gender.length()).trim());
				System.out.println(v2.getGender());
			}
		}

		if (arrayAge[2].contains("House")) {
			v3.setHouseNumber(arrayAge[0].replace("House Number", "").replace(":", "").replace("Available", "").trim());
		} else {
			String string = arrayAge[2].replaceAll("[\\s]+[0-9{1}][\\s]+", "").replace("Available", "").trim();
			Pattern p = Pattern.compile("[0-9]{2}");
			Matcher m = p.matcher(string);
			if (m.find()) {
				System.out.println(m.group(0));
				v3.setAge(m.group(0));
			}
			if (string.contains("Gender")) {
				String gender = string.replace(":", "").replaceAll("[0-9]{2}", "").trim();
				v3.setGender(gender.substring(gender.indexOf("Gender") + 6, gender.length()).trim());
				System.out.println(v3.getGender());
			}
		}

		// String e
		Pattern p = Pattern.compile("[0-9]{2}");
		Matcher m = p.matcher(extraAgwLine);
		if (m.find()) {
			System.out.println(m.group(0));
			v1.setAge(m.group(0));
		}
		v1.setGender(extraAgwLine.substring(extraAgwLine.indexOf("Gender") + 6, extraAgwLine.length()).replace(":", "")
				.trim());

		List<Voter> finalList = new ArrayList<Voter>();

		finalList.add(v1);
		finalList.add(v2);
		finalList.add(v3);

		System.out.println("=================888888888=================================================");
		System.out.println(finalList);
		System.out.println("=================888888888=================================================");

	}

}
