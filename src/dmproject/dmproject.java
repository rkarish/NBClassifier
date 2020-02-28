package dmproject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DMProject {

	static HashMap<String, Double> spam = new HashMap<>();
	static HashMap<String, Double> notSpam = new HashMap<>();
	static ArrayList<String> vocab = new ArrayList<>();

	public static void main(String[] args) throws Exception {

		String token;
		Scanner s;
		File dir = new File("train");
		
		for (File file : dir.listFiles()) {
			s = new Scanner(file);
			if (file.toString().startsWith("train/spm")) {
				while (s.hasNext()) {
					token = s.next();
					if (!spam.containsKey(token)) {
						spam.put(token, 1.0);
						if (!vocab.contains(token)) {
							vocab.add(token);
						}
					} else {
						spam.put(token, spam.get(token) + 1.0);
					}
				}
			} else {
				while (s.hasNext()) {
					token = s.next();
					if (!notSpam.containsKey(token)) {
						notSpam.put(token, 1.0);
						if (!vocab.contains(token)) {
							vocab.add(token);
						}
					} else {
						notSpam.put(token, notSpam.get(token) + 1.0);
					}
				}
			}
			s.close();
		}

		System.out.println("Vocab size: " + vocab.size());
		System.out.println("Spam size: " + spam.size());
		System.out.println("Not spam size: " + notSpam.size());

		for (String key : spam.keySet()) {
			spam.put(key, Math.log((spam.get(key) + 0.00001) / (spam.size() + vocab.size())));
		}

		for (String key : notSpam.keySet()) {
			notSpam.put(key, Math.log((notSpam.get(key) + 0.00001) / (notSpam.size() + vocab.size())));
		}
		
		double count = 0;
		double numFiles = 1;
		double spamProb;
		double notSpamProb;
		dir = new File("test");

		for (File file : dir.listFiles()) {
			s = new Scanner(file);
			spamProb = 0.0;
			notSpamProb = 0.0;
			numFiles++;

			while (s.hasNext()) {
				token = s.next();
				if (spam.containsKey(token)) {
					spamProb += spam.get(token);
				} else {
					spamProb += Math.log(0.00001 / (spam.size() + vocab.size()));
				}
				
				if (notSpam.containsKey(token)) {
					notSpamProb += notSpam.get(token);
				} else {
					notSpamProb += Math.log(0.00001 / (notSpam.size() + vocab.size()));
				}
			}
			
			System.out.println(file.toString());
			System.out.println("Spam prob: " + spamProb);
			System.out.println("Not spam prob: " + notSpamProb);

			if (file.toString().startsWith("test/spm")) {
				if (spamProb > notSpamProb) {
					System.out.println("Correct");
					count++;
				} else {
					System.out.println("Not Correct");
				}
			} else {
				if (notSpamProb > spamProb) {
					System.out.println("Correct");
					count++;
				} else {
					System.out.println("Not Correct");
				}
			}
			s.close();
		}
		
		System.out.println(count);
		System.out.println(numFiles);
		System.out.println("NB classifcation accuracy: %" + (count / numFiles) * 100);
	}
}
