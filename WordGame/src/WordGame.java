import java.util.Arrays;
import java.util.Scanner;

public class WordGame {

	public static void main(String[] args) {
		boolean empty = false;
		In dictionary = new In("words.txt");
		String[] words = readDictionary(dictionary);
		while (!empty){
		
		System.out.println("Enter a comma separated list of words (or an empty list to quit):");
		Scanner scanner = new Scanner(System.in);
		String user = new String(scanner.nextLine());
		if(user.isEmpty()){
			empty = true;
			scanner.close();
		}
		else
		{
		isWordChain( words,readWordList(user));
		}
		}	
		System.out.println("GOODBYE");
		System.exit(0);
	}

	public static String[] readDictionary(In dictionary) {
		String all = dictionary.readAll();
		String[] words = all.split("\r\n");
		return words;
	}

	public static String[] readWordList(String user) {
		String[] array = user.split(", ");
		return array;
	}

	public static boolean isUniqueList(String[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int k = 0; k < array.length; k++) {
				if (array[i].equals(array[k]) && (i != k)) {
					return false;

				}
			}
		}
		return true;
	}

	public static boolean isEnglishWord(String[] array, String[] words) 
	{
		boolean isEnglish = true;
		int position = 0;
		for (int i = 0; i < array.length; i++) {
			String value = array[i];
			position = Arrays.binarySearch(words, value);
			if (position < 0) {
				isEnglish = false;
			}
		}
		return isEnglish;
	}

	public static boolean isDifferentByOne(String[] array) {

		boolean different = false;
		for (int j = 0; j < array.length - 1; j++) {
			int count = 0;
			String string = array[j];
			int nextPos = j + 1;
			String string2 = array[nextPos];
			if (string.length() != string2.length()) {
				different = true;
			}
			for (int i = 0; i < string.length(); i++) {
				if (string.charAt(i) != string2.charAt(i)) {
					count++;
				}
			}

			if ((count > 1) || (count == 0)) {
				different = true;
			}
		}
		return different;
	}

	public static void isWordChain( String [] words,String[] array) {
		if ((isUniqueList(array) == true) && (isEnglishWord(array, words) == true)
				&& (isDifferentByOne(array) == false)) {
			System.out.println("Valid chain of words from Lewis Carroll's word-links game.");
		} else {
			System.out.println("INVALID INPUT");
		}
	}
}

