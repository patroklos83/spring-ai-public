package com.patroclos.ai.service;

import java.util.*;

public class Tokenizer {

	private List<String> inputs;

	private HashSet<String> stopWords;

	private int skipGramWindow;

	public Tokenizer(List<String> ins, HashSet<String> words) {
		this.inputs = ins;
		stopWords = words;
	}

	public Tokenizer(List<String> ins, HashSet<String> words,int skipGramWindow ) {
		this.inputs = ins;
		stopWords = words;
		this.skipGramWindow=skipGramWindow;
	}

	private HashSet<String> uniqueWords = new HashSet<>();

	private List<String[]> removeOutStopWords() {
		List<String[]> list = new ArrayList<>();

		for (int i = 0; i < inputs.size(); i++) {
			String temp = inputs.get(i).toLowerCase();
			String[] words = temp.split(" ");
			int space =0;
			for (int j = 0; j < words.length; j++) {
				if (stopWords.contains(words[j])) {
					words[j] = "";
					space = space+1;
				} else {
					uniqueWords.add(words[j]);
				}
			}

			String[] finalWord = new String[words.length-space];
			int index =0;
			for(int k =0;k<words.length;k++)
			{
				if(words[k] == "")
				{
					continue;
				}
				finalWord[index]=words[k] ;
				index=index+1;
			}
			list.add(finalWord);
		}

		return list;
	}

	private List<String[]> createBigrams(List<String[]> words) {
		List<String[]> list = new ArrayList<>();

		for (int i = 0; i < words.size(); i++) {
			String[] array = words.get(i);

			for (int j = 0; j < array.length; j++) {
				if (array[j] == null) {
					continue;
				}

				for (int k = 0; k < array.length; k++) {
					if (k == j || array[k] == null) {
						continue;
					} else {
						String[] bigrams = new String[2];

						bigrams[0] = array[j];
						bigrams[1] = array[k];
						list.add(bigrams);
					}
				}
			}
		}

		return list;
	}

	private List<String[]> createSkipGramsTrainingDataSet(List<String[]> words, int window) {
		List<String[]> list = new ArrayList<>();

		for (int i = 0; i < words.size(); i++) {
			String[] array = words.get(i);

			for (int j = 0; j < array.length; j++) {

				if(array[j]==null)
				{
					continue;
				}
				int tWindow = window;
				int k = j - 1;
				int p = j + 1;
				while (tWindow > 0) {
					if (k >= 0) {
						String[] grams = new String[2];
						grams[0] = array[j];
						grams[1] = array[k];
						k = k - 1;
						list.add(grams);

					}
					if (p < array.length) {
						String[] grams = new String[2];
						grams[0] = array[j];
						grams[1] = array[p];
						p = p + 1;
						list.add(grams);
					}
					tWindow = tWindow - 1;
				}
			}

		}

		return list;
	}

	public List<Integer[][]> generateBigramsEncoding() {
		List<Integer[][]> doubles = new ArrayList<>();
		List<String[]> temp = removeOutStopWords();
		List<String[]> grams = createBigrams(temp);

		HashMap<String, Integer[]> hashMap = oneHotEncoder();

		System.out.println(hashMap);

		for (int i = 0; i < grams.size(); i++) {
			String[] str = grams.get(i);
			Integer[][] array = new Integer[2][uniqueWords.size()];

			array[0] = hashMap.get(str[0]);
		}
		return doubles;
	}

	public List<Integer[][]> generateSkipGramsTrainingDataSet() {
		List<Integer[][]> doubles = new ArrayList<>();
		List<String[]> temp = removeOutStopWords();
		List<String[]> grams = createSkipGramsTrainingDataSet(temp,skipGramWindow);

		HashMap<String, Integer[]> hashMap = oneHotEncoder();

		System.out.println(hashMap);

		for (int i = 0; i < grams.size(); i++) {
			String[] str = grams.get(i);
			Integer[][] array = new Integer[2][uniqueWords.size()];

			array[0] = hashMap.get(str[0]);
			array[1] = hashMap.get(str[1]);
			doubles.add(array);
		}
		return doubles;
	}

	public HashMap<Integer, List<Integer[]>>  generateSkipGramsTrainingData() {
		List<String[]> temp = removeOutStopWords();
		HashMap<Integer, List<Integer[]>> dataSet = new HashMap<>();
		List<String[]> grams = createSkipGramsTrainingDataSet(temp,skipGramWindow);
		List<Integer[]> inputs = new ArrayList<>();
		List<Integer[]> outputs = new ArrayList<>();
		HashMap<String, Integer[]> hashMap = oneHotEncoder();
		dataSet.put(1, inputs);
		dataSet.put(2, outputs);
		System.out.println(hashMap);

		for (int i = 0; i < grams.size(); i++) {
			String[] str = grams.get(i);
			Integer[] tempInput = hashMap.get(str[0]);
			Integer[] tempOutput = hashMap.get(str[0]);
			inputs.add(tempInput);
			outputs.add(tempOutput);
		}
		return dataSet;
	}

	public HashMap<String, Integer[]> oneHotEncoder() {
		int i = 0;
		HashMap<String, Integer[]> encoder = new HashMap<>();
		for (String key : uniqueWords) {
			Integer[] values = new Integer[uniqueWords.size()];
			values[i] = 1;
			encoder.put(key, values);
			i = i+1;

		}
		return encoder;
	}

//	public static void main(String[] args) {
//		String str = "Sherkhan is king of the Jungle";
//		String str1 = "Mowgli is a brave boy";
//		String str2 = "Bagira is strong";
//		String str3 = "Ballu is very helping";
//		String str4 = "Lali is very kind";
//
//
//		List<String> words = new ArrayList<>();
//
//		words.add(str);
//		words.add(str1);
//		words.add(str2);
//		words.add(str3);
//		words.add(str4);
//
//
//		HashSet<String> set = new HashSet<>();
//
//		set.add("the");
//		set.add("is");
//		set.add("a");
//		set.add("of");
//		set.add("very");
//
//		Tokenizer encoder = new Tokenizer(words, set,2);
//		List<Integer[][]> trainingSet = encoder.generateSkipGramsTrainingDataSet();
//
//	}

}