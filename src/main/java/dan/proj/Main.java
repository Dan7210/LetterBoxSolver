package dan.proj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    //Variables
    private final static List<String> dictWords = new ArrayList<>(); //Dictionary
    private final static Map<Character, Integer> charMap = new HashMap<>();

    private final static List<String> words = new ArrayList<>(); //Valid words
    private final static List<String> checkedWords = new ArrayList<>(); //Already-checked words
    private final static List<String> newWords = new ArrayList<>(); //New Words found - Used to prevent concur. modification error

    private final static List<List<String>> sentences = new ArrayList<>();
    private final static List<List<String>> newSentences = new ArrayList<>();
    private final static List<List<String>> foundSentences = new ArrayList<>();

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
            System.out.println("Issue with Buffered Reader.");
            System.exit(1);
        }

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
                List<String> wordsToRemove = new ArrayList<String>();
                for(String word:words) {
                    if(!dictWords.contains(word) || word.length() < 5 || uniqueLetterCounter(word) < 3) {
                        wordsToRemove.add(word);
                    }
                }
                for(String word:wordsToRemove) {
                    words.remove(word);
                }
                System.out.println(words.size() + " valid words remain after pruning.");
                break;
            }
            //Prevents concurrent modification exception
            words.addAll(newWords);
            newWords.clear();
        }

        //Find most letter-diverse word to start with
        int bestSize = -1;
        String bestWord = "";
        for (String word : words) {
            if (uniqueLetterCounter(word) > bestSize) {
                bestSize = uniqueLetterCounter(word);
                bestWord = word;
            }
        }
        //Make first sentence with best word
        List<String> newSentence = new ArrayList<>();
        newSentence.add(bestWord);
        sentences.add(newSentence);
        newSentences.add(newSentence);

        boolean run = true;
        //Find all valid sentences



        while(run) {
            //Make all possible sentences
            for(List<String> sentence:newSentences) {
                System.out.println(sentence.size());
                makeVerbose(sentence);
            }
            newSentences.clear();
            newSentences.addAll(foundSentences);
            sentences.addAll(foundSentences);
            foundSentences.clear();
            //Terminate if sentences are done generating
            for(List<String> sentence:newSentences) {
                if (newSentences.isEmpty() || uniqueLetterCounter(sentence)==12) {
                    System.out.println(sentences.size() + " valid sentences were found using generated words.");
                    run = false;
                    break;
                }
            }
        }

        //Find and print best sentence
        List<String> bestSentence = null;
        int bestLength = 999;
        for(List<String> sentence:sentences) {
            if(uniqueLetterCounter(sentence)==12 && letterCounter(sentence)<bestLength) {
                bestSentence = sentence;
                bestLength = letterCounter(sentence);
            }
        }
        if(bestSentence == null) {
            System.out.println("The game is unwinnable. RIP.");
            System.exit(1);
        }
        System.out.print("\nThe best sentence to win the game is: ");
        for(String word:bestSentence) {
            System.out.print(word + " ");
        }
        System.out.println(".");
    }

    private static void makeVerbose(List<String> sentence) {
        for(String word:words) {
            //If sentence does not already contain this word and the sentence ends with the same letter the word starts with
            if(!sentence.contains(word) && word.charAt(0)==sentence.get(sentence.size()-1).charAt(sentence.get(sentence.size()-1).length()-1)) {
                List<String> newSentence = sentence;
                newSentence.add(word);
                foundSentences.add(newSentence);
            }
        }
    }

    private static void bruteSearch(String word) {
        for(Character letter:charMap.keySet()) {
            if(charMap.get(letter) != charMap.get(word.charAt(word.length()-1))) {
                String newWord = word+letter;
                if(checkStartsWIth(word)) {
                    newWords.add(newWord);
                }
            }
        }
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

    private static int uniqueLetterCounter(String word) {
        List<Character> uniqueLetters = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            Character letter = word.charAt(i);
            if (!uniqueLetters.contains(letter)) {
                uniqueLetters.add(letter);
            }
        }
        return uniqueLetters.size();
    }

    private static boolean checkStartsWIth(String word) {
        for(String dWord:dictWords) {
            if(dWord.startsWith(word)) {
                return true;
            }
        }
        return false;
    }
}