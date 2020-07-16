package iisg.amsterdam.wp4_links;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.github.liblevenshtein.transducer.Candidate;

public class CandidateCertificate {

	public String certificateID = "";
	public int numberNames = -1;
	public int numberMatchedNames = 0;
	public int levenshteinTotal = -1;
	public int levenshteinLastName = -1;
	public int maximumMatchedLevenshtein = 0;
	public String individualsInCertificate = "";
	public HashMap<String, ArrayList<Candidate>> levenshteinPerName;



	public CandidateCertificate(String certificate_ID, String number_names, String sourceFullName, String individualsInCert, Candidate candidate, String separator) {
		certificateID = certificate_ID;
		numberNames = Integer.valueOf(number_names);
		individualsInCertificate  = individualsInCert;
		levenshteinLastName = getLevenshteinLastName(sourceFullName, candidate, separator);
		levenshteinTotal = levenshteinLastName;
		ArrayList<Candidate> candidates = new ArrayList<Candidate>();
		candidates.add(candidate);
		levenshteinPerName = new HashMap<>();
		levenshteinPerName.put(sourceFullName, candidates);

	}

	//	public void addMatchedName(Person sourcePerson, Candidate candidate) {
	//		// if last names are equal then add the levensthein distances (i.e. next line)		
	//		numberMatchedNames++;
	//		String[] candidatefullName = candidate.term().split(sourcePerson.names_separator);
	//		if(sourcePerson.getLastName().equals(candidatefullName[1])) {
	//			levenshteinTotal = levenshteinTotal + candidate.distance();
	//			if(candidate.distance() > maximumMatchedLevenshtein) {
	//				maximumMatchedLevenshtein = candidate.distance();
	//			}
	//		}
	//	}

	//	public void addMatchedName(Person sourcePerson, String sourceFullName, Candidate candidate) {
	//
	//		if(!levenshteinPerName.containsKey(sourceFullName)) {
	//			if(!consideredNames.contains(candidate.term())) {
	//				levenshteinPerName.put(sourceFullName, candidate);
	//				consideredNames.add(candidate.term());
	//			}
	//		} else {
	//			Candidate storedCand = levenshteinPerName.get(sourceFullName);
	//			if(candidate.distance() < storedCand.distance()) {
	//				consideredNames.remove(storedCand.term());
	//				consideredNames.add(candidate.term());
	//				levenshteinPerName.put(sourceFullName, candidate);
	//			}
	//		}	
	//	}

	public void addMatchedName(Person sourcePerson, String sourceFullName, Candidate candidate) {
		if(levenshteinPerName.containsKey(sourceFullName)) {
			levenshteinPerName.get(sourceFullName).add(candidate);
		} else {
			ArrayList<Candidate> candidates = new ArrayList<Candidate>();
			candidates.add(candidate);
			levenshteinPerName.put(sourceFullName, candidates);
		}
	}


	public HashMap<String, Candidate> organiseMetadata() {

		HashMap<String, Candidate> result = new HashMap<String, Candidate>();
		Set<String> matchedCandidates = new HashSet<String>();

		TreeMap<Integer, Set<String>> metadata = new TreeMap<Integer, Set<String>>();
		for (Entry<String, ArrayList<Candidate>> entry: levenshteinPerName.entrySet()) {
			int s = entry.getValue().size();
			if(metadata.containsKey(s)) {
				metadata.get(s).add(entry.getKey());
			} else {
				Set<String> test = new HashSet<String>();
				test.add(entry.getKey());
				metadata.put(s, test);
			}
		}
		for (Entry<Integer, Set<String>> metaEntry: metadata.entrySet()) {
			for (String cand: metaEntry.getValue()) {
				ArrayList<Candidate> finalCandidates = levenshteinPerName.get(cand);
				Candidate finalCandidate = new Candidate("", 6);
				for (Candidate possibleCandidate: finalCandidates) {
					if(!matchedCandidates.contains(possibleCandidate.term())) {
						if(possibleCandidate.distance() < finalCandidate.distance()) {
							finalCandidate = possibleCandidate;
						}
					}
				}
				if(finalCandidate.distance() < 6) {
					matchedCandidates.add(finalCandidate.term());
					result.put(cand, finalCandidate);
					levenshteinTotal = levenshteinTotal + finalCandidate.distance() - levenshteinLastName;
					if(maximumMatchedLevenshtein < finalCandidate.distance()) {
						maximumMatchedLevenshtein = finalCandidate.distance();
					}
				}
			}
		}
		return result;
	}


	public Integer getLevenshteinLastName(String sourceFullName, Candidate cand, String separator) {
		String[] sourceFullNameSplit = sourceFullName.split(separator);
		String[] candFullNameSplit = cand.term().split(separator);
		if(sourceFullNameSplit[1].equals(candFullNameSplit[1])) {
			return 0;
		} else {
			if(sourceFullNameSplit[0].equals(candFullNameSplit[0])) {
				return cand.distance();
			} else {
				return 1;
			}
		}
	}



}