package dan.proj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    private static List<String> dictWords = new ArrayList<>(); //Dictionary
    private static Map<Character, Integer> charMap = new HashMap<>();

    private static List<String> words = new ArrayList<>(); //Valid words
    private static List<String> checkedWords = new ArrayList<>(); //Already-checked words
    private static List<String> newWords = new ArrayList<>(); //New Words found - Used to prevent concur. modification error

    private static List<List<String>> sentences = new ArrayList<>();
    private static List<List<String>> checkedSentences = new ArrayList<>();
    private static List<List<String>> newSentences = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the WSJ LetterBox Solver.");
        Scanner scanner = new Scanner(System.in);

        //Load in Dictionary
        try {
            String dictFilePath = "C:\\Users\\merky\\IdeaProjects\\LetterBoxSolver\\src\\main\\java\\dan\\proj\\dict.txt"; //Convert to local resource later
            BufferedReader reader = new BufferedReader(new FileReader(dictFilePath));
            String line;
            while((line=reader.readLine()) != null) {
                dictWords.add(line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println(words.size());

        //Load in all four combinations.
        for(int i=0;i<4;i++) {
            System.out.println("Enter 3-letters for side "+(i+1)+": ");
            String input = scanner.next();

            //Input validation
            if(input.length() != 3 || !input.matches("[a-zA-Z]+")) {
                System.out.println("Invalid combination. Enter 3 letters.");
                i--;
            }else {
                for(int j=0; j<3;j++) {
                    charMap.put(input.charAt(j), i); //Map a letter to its side value
                    words.add(String.valueOf(input.charAt(j))); //Create a word with the starting letter
                }
            }
        }
        scanner.close(); //Resource closing

        //Find all valid words
        while(true) {
            for(String word:words) {
                if(!checkedWords.contains(word)) {
                    bruteSearch(word);
                    checkedWords.add(word);
                }
            }
            if(newWords.isEmpty()) {
                System.out.println(words.size() + " valid words were found for this setup.");
                break;
            }
            //Prevents concurrent modification exception
            for(String newWord : newWords) {
                words.add(newWord);
            }
            newWords.clear();
        }
        //Find all valid sentences
        while(true) {
            //Create sentences
            for(String word:words) {
                List<String> newSentence = new ArrayList<>();
                newSentence.add(word);
                sentences.add(newSentence);
            }
            //Make all possible sentences
            for(List<String> sentence:sentences) {
                if(!checkedSentences.contains(sentence) && sentence.size() < 4) {
                    checkedSentences.add(sentence);
                    makeVerbose(sentence);
                }
            }
            //Terminate if sentences are done generating
            if(newSentences.isEmpty()) {
                System.out.println(sentences.size() + " valid sentences were found using generated words.");
                break;
            }
            for(List<String> sentence:newSentences) {
                sentences.add(sentence);
            }
            newSentences.clear();
        }
        //Find best sentence
        List<String> bestSentence = null;
        int bestLength = Integer.MAX_VALUE;
        for(List<String> sentence:sentences) {
            if(uniqueLetterCounter(sentence)==12 && letterCounter(sentence)<bestLength) {
                bestSentence = sentence;
                bestLength = letterCounter(sentence);
            }
        }
        if(bestSentence == null) {
            System.out.println("The game is unwinnable. RIP.");
        }
        System.out.print("\nThe best sentence to win the game is: ");
        for(String word:bestSentence) {
            System.out.print(word + " ");
        }
        System.out.println(".");
    }

    private static int letterCounter(List<String> sentence) {
        int length = 0;
        for(String word:sentence) {
            length += word.length();
        }
        return length;
    }

    private static int uniqueLetterCounter(List<String> sentence) {
        List<Character> uniqueLetters = new ArrayList<>();
        for(String word:sentence) {
            for(int i = 0; i < word.length(); i++) {
                Character letter = word.charAt(i);
                if(!uniqueLetters.contains(letter)) {
                    uniqueLetters.add(letter);
                }
            }
        }
        return uniqueLetters.size();
    }

    private static void makeVerbose(List<String> sentence) {
        for(String word:words) {
            if(!sentence.contains(word)) {
                List<String> newSentence = sentence;
                newSentence.add(word);
                newSentences.add(newSentence);
            }
        }
    }

    private static void bruteSearch(String word) {
        for(Character letter:charMap.keySet()) {
            if(charMap.get(letter) != charMap.get(word.charAt(word.length()-1))) {
                String newWord = word+letter;
                if(checkValid(word)) {
                    newWords.add(newWord);
                }
            }
        }
    }

    private static boolean checkValid(String word) {
        for(String dWord:dictWords) {
            if(dWord.startsWith(word)) {
                return true;
            }
        }
        return false;
    }
}