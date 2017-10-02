
import java.util.Scanner;

public class SieveOfEratosthenes 
{
	public static void main(String[] args) 
	{
		System.out.println("Please enter int >= 2 :");
		Scanner scanner = new Scanner(System.in);
		if (scanner.hasNextInt()) 
		{
			int number = scanner.nextInt();
			if (number < 2)
			{
				System.out.println("INVALID ANSWER!");
				System.exit(0);
			}
			int[] SequenceArray = new int[number - 1];
			createSequence( SequenceArray);
			System.out.println();
			
			sieve(SequenceArray, number);
			System.out.println("Primes:" + nonCrossedOutSubseqToString(SequenceArray));
			scanner.close();
		} 
		else 
		{
			System.out.println("INVALID INPUT!");
			System.exit(0);
		}
	}

	public static int[] createSequence(int[] SequenceArray)
	{
		int value = 2;
		for (int counter = 0; counter < SequenceArray.length; counter++) 
		{
			SequenceArray[counter] = value;
			if (counter < SequenceArray.length -1){
			System.out.print(SequenceArray[counter] +  ",");
			}
			else {
				System.out.print(SequenceArray[counter]);
			}
			
			value++;
		}
		return SequenceArray;
	}

	public static int[] crossOutHigherMultiples(int[] SequenceArray, int value1, int number) 
	{
		int position = value1 - 2;
		while (position + value1 < SequenceArray.length) 
		{
			position = position + value1;
			if (SequenceArray[position] != 0) 
			{
				SequenceArray[position] = 0;
			}
		}
		if (value1 <= Math.sqrt(number)) 
		{
			System.out.println(sequenceToString(SequenceArray));
		}
		return SequenceArray;
	}

	public static int[] sieve(int[] SequenceArray, int number)

	{
		for (int count = 0; count < SequenceArray.length; count++) 
		{
			int value1 = SequenceArray[count];
			if (value1 != 0) {
				SequenceArray = crossOutHigherMultiples(SequenceArray, value1, number);
			}
		}
		return SequenceArray;
	}

	public static String sequenceToString(int[] SequenceArray)
	{
		String characters = "";
		
		for (int counter = 0; counter < SequenceArray.length; counter++) 
		{
			int value = counter + 2;
			if (SequenceArray[counter] == 0)
			{
				characters += "[" + value + "]";
			} else {
				characters += value;
			}
			if (counter < SequenceArray.length - 1)
			{
				characters += ",";
			}
		}
		return characters;
	}

	public static String nonCrossedOutSubseqToString(int[] SequenceArray)
	{
		String primes = " ";
		for (int count = 0; count < SequenceArray.length; count++) 
		{
			if ((count > 0) && (SequenceArray[count] != 0))
			{
				primes += ",";
			}
			if (SequenceArray[count] != 0) 
			{
				primes += SequenceArray[count];
			}
		}
		return primes;
	}
}
