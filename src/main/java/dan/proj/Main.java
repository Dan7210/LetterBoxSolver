//Made by Daniel Marquez
//Brute force solver for WSJ LetterBox game
//Known issues: Dictionary isn't the same one WSJ uses, but generally works.
//Final revision date: 1/24/2024

package dan.proj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {
    private final static List<String> dictWords = new ArrayList<>(); //Word Dictionary
    private final static Map<Character, Integer> charMap = new HashMap<>(); //Map letters to sides

    private final static List<String> words = new ArrayList<>(); //Valid words
    private final static List<String> checkedWords = new ArrayList<>(); //Already-checked words
    private final static List<String> newWords = new ArrayList<>(); //New Words found per each iteration loop

    public static void main(String[] args) {
        System.out.println("Welcome to the WSJ LetterBox Solver.");
        Scanner scanner = new Scanner(System.in);

        //Load in Dictionary
        try {
            // Construct the file path for 'dict.txt' in the same directory
            String dictFilePath = System.getProperty("user.dir") +  "\\res\\dict.txt";

            BufferedReader reader = new BufferedReader(new FileReader(dictFilePath));
            String line;
            while((line=reader.readLine()) != null) {
                dictWords.add(line);
            }
        } catch(Exception e) {
            System.out.println("Issue with Buffered Reader.");
            System.exit(1);
        }

        //Load in all four letter "sides"
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
                List<String> wordsToRemove = new ArrayList<>();
                for(String word:words) {
                    if(!dictWords.contains(word) || word.length() < 3 || uniqueLetterCounter(word) < 3) {
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

        //Make sentence starting with the first word and then expand on it until valid.
        List<String> sentence = new ArrayList<>();
        sentence.add(findBestWord()); //Find most letter-diverse word to start with
        while(uniqueLetterCounter(sentence)<12) {
            boolean wordFound = false;
            for(String word:words) {
                //If the sentence does not already contain a word and the start/end letters match
                if(!sentence.contains(word) && word.charAt(0)==sentence.get(sentence.size()-1).charAt(sentence.get(sentence.size()-1).length()-1)) {
                    List<String> tempSentence = new ArrayList<>(sentence); //Copy elements without memory reference
                    tempSentence.add(word);
                    if(uniqueLetterCounter(tempSentence) > uniqueLetterCounter(sentence)) {
                        sentence.clear();
                        sentence.addAll(tempSentence);
                        wordFound = true;
                    }
                }
            }
            //If no valid word can be found
            if(!wordFound) {
                //If the sentence is not at its root word, remove the last word and try again.
                if(sentence.size() > 1) {
                    String lastWord = sentence.get(sentence.size()-1);
                    System.out.println("Removing word: " + lastWord);
                    words.remove(lastWord);
                    sentence.remove(lastWord);
                }
                //Exit if no valid combinations are found.
                else if(words.isEmpty()) {
                    System.out.println("No valid solution found.");
                    System.exit(1);
                }
                //Restart if no valid combination is found.
                else {

                    System.out.println("Changing first word.");
                    words.remove(sentence.get(0));
                    sentence.clear();
                    sentence.add(findBestWord());
                }
            }
        }

        //Print out the solution
        System.out.print("\nThe best sentence to win the game is: ");
        for(String word:sentence) {
            System.out.print(word + " ");
        }
        System.out.println(".");
    }

    //Searches for valid words, letters must not be from the same side (charMap)
    private static void bruteSearch(String word) {
        for(Character letter:charMap.keySet()) {
            if(!Objects.equals(charMap.get(letter), charMap.get(word.charAt(word.length() - 1)))) {
                String newWord = word+letter;
                if(checkStartsWith(word)) {
                    newWords.add(newWord);
                }
            }
        }
    }

    //Just finds the word out of the given list that has the most unique letters.
    private static String findBestWord() {
        int bestSize = -1;
        String bestWord = "";
        for (String word : words) {
            if (uniqueLetterCounter(word) > bestSize) {
                bestSize = uniqueLetterCounter(word);
                bestWord = word;
            }
        }
        return bestWord;
    }

    //Counts the amount of unique letters in a sentence
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

    //Counts the amount of unique letters in a word
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

    //Checks if a dictionary word starts with the given word.
    private static boolean checkStartsWith(String word) {
        for(String dWord:dictWords) {
            if(dWord.startsWith(word)) {
                return true;
            }
        }
        return false;
    }
}