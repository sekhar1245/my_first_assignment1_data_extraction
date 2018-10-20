package com.sekhar.pdfextractor.extractText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.sekhar.pdfextractor.beans.Voter;

public class ProcessRawData {

	public static void removeHeaderAndTrailerFromRawFile(String rawFilePath) {

		try {
			List<String> allLines = Files.readAllLines(Paths.get(rawFilePath), StandardCharsets.ISO_8859_1);

			allLines.remove(allLines.size() - 2);
			allLines.remove(1);
			allLines.remove(0);

			//List<String> voterList = getVoterIDList(allLines.get(0));
			System.out.println("Voter ID" + allLines.get(0));

			//System.out.println("cleaned Voter ID" + voterList);

			System.out.println("==================");
			System.out.println("Name-->" + allLines.get(1));
			//Map<String, String> namesListMap = getNamesListMap(allLines.get(1));
			//System.out.println("cleaned Names List " + namesListMap.keySet());

			System.out.println("==================");
			System.out.println("Husband//Father Name" + allLines.get(2));
			
			Map<String, String> namesListMap = getHusbandAndFatherListMap(allLines.get(2));
			System.out.println("cleaned Names List " + namesListMap.keySet());
			Map<String, String> namesListMap1 = getHusbandAndFatherListMap(allLines.get(7));
			System.out.println("cleaned Names List " + namesListMap1.keySet());

			/*
			 ** * System.out.println("House No" + allLines.get(3));
			 * System.out.println("Age and Gender " + allLines.get(4));
			 * System.out.println("Voter ID" + allLines.get(5));
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
		System.out.println("getHusbandAndFatherListMap--->" + husbandAndFatherLine.trim());		
		husbandAndFatherLine = husbandAndFatherLine.replaceAll("'s", "").replaceAll("Husband Name", "111").replaceAll("Father Name", "222").trim();
		
		System.out.println(husbandAndFatherLine);

		

		return namesListMap;

	}

}
