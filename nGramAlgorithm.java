import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class nGramAlgorithm {
    private MyHashTable unigram, bigrams, trigrams;
    private Map<String, Integer> hashmap;
    private Map<String, Integer> uniMap, triMap;
    private double prevProb;
    

    public nGramAlgorithm() throws IOException {
        unigram = new MyHashTable(50000);
        bigrams = new MyHashTable(60000);
        trigrams = new MyHashTable(70000);
        prevProb = 0;
        
    }
    public void extract(List<String> text) {

        for(String word : text) {
            unigram.insert(word.trim());
        }

        for(int i = 0; i < text.size() - 1; i++) {
            String bigram = text.get(i) + " " + text.get(i+1);
            bigrams.insert(bigram.trim());
        }
        for(int i = 0; i<text.size()-2; i++)
        {
            String trigram = text.get(i) + " " + text.get(i + 1) + " " + text.get(i + 2);
            trigrams.insert(trigram.trim());
        }
    }

    public List<String> display(String[] words) throws IOException {

        String[] startingWords = words;
        int n = words.length;
        return generateLikelyWordSequence(startingWords, n);
        
    }

    public MyHashTable getUnigrams() {
        return unigram;
    }

    public double calculateUnigramProbability(String word) {
        int totalCount = getTotalCount(unigram);
        Integer wordCount = uniMap.get(word);
        return wordCount != null ? (double) wordCount / totalCount : 0.0;
    }

    public double calculateBigramProbability(String word1, String word2) {
        String bigram = word1 + " " + word2;
        Integer uniCount = uniMap.get(word1);
        if(uniCount == null) {
            uniCount = 1;
        }
        Integer bigramCount = hashmap.get(bigram);
        return bigramCount != null ? (double) bigramCount / uniCount : 0.0;
    }

    public double calculateTrigramProbability(String word1, String word2, String word3) {
        String trigram = word1 + " " + word2 + " " + word3;
        Integer bigramCount = bigrams.getWordsMap().get(word1 + " " + word2);
    
        if (bigramCount != null && bigramCount > 0) {
            Integer trigramCount = trigrams.getWordsMap().get(trigram);
            return trigramCount != null ? (double) trigramCount / bigramCount : 0.0;
        } else {
            return 0.0;
        }
    }

    private int getTotalCount(MyHashTable table) {
        hashmap = table.getWordsMap();
        int totalCount = 0;
        for (int count : hashmap.values()) {
            totalCount += count;
        }
        return totalCount;
    }

    //------------------------------------------------------------- Generating the Likely word sequence --------------------------------------------------------//

    public List<String> generateLikelyWordSequence(String[] startingWords, int n) throws IOException {
        List<String> wordSequence = new ArrayList<>();
        if (startingWords != null) {
            Collections.addAll(wordSequence, startingWords);
        }
    
        uniMap = unigram.getWordsMap();
        hashmap = bigrams.getWordsMap();
        triMap = trigrams.getWordsMap();
        Random random = new Random();
        double minVal = 0.0000000;
        double maxVal = 5.00000;
        int attempt = 0;
    
        while (wordSequence.size() < 20) {
            String lastWord = wordSequence.get(wordSequence.size() - 1);
            System.out.println(wordSequence);
            int sequenceSize = wordSequence.size();
            if (n == 1) {
                double rand = random.nextDouble() * (maxVal - minVal) + minVal;
                String nextWord = selectNextUnigram(rand);
                if (nextWord == null) {
                    break;
                }
                wordSequence.add(nextWord);
                attempt = 0;
            } else if (n == 2) {
                double rand = random.nextDouble() * (maxVal - minVal) + minVal;
                System.out.println(rand);
                String nextWord = selectNextBigram(lastWord, rand);
                if (nextWord != null) {
                    wordSequence.add(nextWord);
                    attempt = 0;
                }
                else{
                    attempt++;
                }
            } else if (n == 3) {
                String lastTwoWords = wordSequence.get(sequenceSize - 2) + " " + wordSequence.get(sequenceSize - 1);
                double rand = random.nextDouble() * (maxVal - minVal) + minVal;
                String nextWord = selectNextTrigram(lastTwoWords, rand, triMap);
                if (nextWord != null) {
                    wordSequence.add(nextWord);
                    attempt = 0;
                }
                else{
                    attempt++;
                }
            } 
            else 
            {
                break;
            }
    
            if (attempt>100) {
                break;
            }
        }
        return wordSequence;
    }
    
//-------------------------------------------------------- Finding next most likely word sequence ---------------------------------------------------------------//

    private String selectNextUnigram(double rand) {
        hashmap = unigram.getWordsMap();
        for (String word : hashmap.keySet()) {
            double val = calculateUnigramProbability(word)*1000;
            if (rand < val) {
                return word;
            }
        }
        return null;
    }

    private String selectNextBigram(String word1, double rand) throws IOException {
        double val;
        uniMap = unigram.getWordsMap();
        for (String word2 : uniMap.keySet()) {
            String bigram = word1 + " " + word2;
            val = calculateBigramProbability(word1, word2)*1000;
            
            if (val > prevProb) {
                prevProb = val;
                hashmap.remove(bigram);
                return word2;
            }
        }
        prevProb = rand;
        return null;
    }

    private String selectNextTrigram(String lastTwoWords, double rand, Map<String, Integer> triMap) {
        String[] lastWords = lastTwoWords.split("\\s+");
        double val;
        int maxAttempts = 100;
        int attempt = 0;
    
        while (attempt < maxAttempts) {
            for (String word : uniMap.keySet()) {
                String trigram = lastTwoWords + " " + word;
                val = calculateTrigramProbability(lastWords[0], lastWords[1], word) * 1000;
    
                if (val > prevProb && triMap.containsKey(trigram)) {
                    prevProb = val;
                    triMap.remove(trigram);
                    return word;
                }
            }
            for (String word : uniMap.keySet()) {
                String trigram = word + " " + lastWords[0] + " " + lastWords[1];
                val = calculateTrigramProbability(word, lastWords[0], lastWords[1]) * 1000;
    
                if (val > prevProb && triMap.containsKey(trigram)) {
                    prevProb = val;
                    triMap.remove(trigram);
                    return word;
                }
            }

            attempt++;
        }
    
        prevProb = rand;
        return null;
    }
    
//--------------------------------------------------------- Getting Probabilities for Bigrams and Trigrams -----------------------------------------------------------------------------//

    public Map<String, Double> getProbabilities() {
        Map<String, Double> probabilities = new HashMap<>();
        hashmap = bigrams.getWordsMap();
        uniMap = unigram.getWordsMap();
        for (String bigram : hashmap.keySet()) {
            String[] words = bigram.split("\\s+");
            double p = calculateBigramProbability(words[0], words[1]);
            probabilities.put(bigram, p);
        }
        return probabilities;
    }

    public Map<String, Double> getTrigramProbabilities() {
        Map<String, Double> probabilities = new HashMap<>();
        triMap = trigrams.getWordsMap();
        uniMap = unigram.getWordsMap();
        for (String bigram : triMap.keySet()) {
            String[] words = bigram.split("\\s+");
            double p = calculateTrigramProbability(words[0], words[1],words[2]);
            probabilities.put(bigram, p);
            System.out.println(bigram+"     "+p);
        }
        return probabilities;
    }
}
