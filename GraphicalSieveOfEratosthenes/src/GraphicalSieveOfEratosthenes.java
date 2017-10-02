import java.awt.Color;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.Font;
public class GraphicalSieveOfEratosthenes {
	public static final double MARGIN = 0.7;
	public static final Color[] Colors = {StdDraw.GRAY,StdDraw.BLUE, StdDraw.CYAN, StdDraw.GREEN, StdDraw.MAGENTA, StdDraw.ORANGE,
			StdDraw.PINK, StdDraw.RED, StdDraw.YELLOW };
	public static int numberOfPrimes = -1;
	public static int changeColours = 0;
	
	public static void main(String[] args)
	{
		System.out.println("Please enter int >= 2 :");
		Scanner scanner = new Scanner(System.in);
		if (scanner.hasNextInt()) {
			int number = scanner.nextInt();
			int scale = 10;
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(0.85, 0.95, "Primes");
			double matrix = Math.ceil(Math.sqrt(number));
			double xWidth = MARGIN / matrix;
			double square = xWidth / 10;
			square = (xWidth - square) / 2;
			double matrix2 = matrix;
			while (matrix2 > 20) {
				matrix2 = matrix2 - 3;
				scale = scale - 1;
				if (scale <= 0) {
					scale = 1;
				}	
			}
			Font font = new Font("Arial", Font.ITALIC, scale);
			StdDraw.setFont(font);
			if (number < 2) {
				System.out.println("INVALID ANSWER!");
				System.exit(0);
			}
			int[] SequenceArray = new int[number - 1];
			createSequence(SequenceArray);
			if (SequenceArray != null) {
				displayNumbers2ToN(number, matrix, xWidth, square);
				sieve(SequenceArray, number, matrix, xWidth, square, scale);
			}
			scanner.close();
		} else {
			System.out.println("INVALID INPUT!");
			System.exit(0);
		}
	}

	public static int[] createSequence(int[] SequenceArray) {
		int value = 2;
		for (int counter = 0; counter < SequenceArray.length; counter++) {
			SequenceArray[counter] = value;
			if (counter < SequenceArray.length - 1) {
				System.out.print(SequenceArray[counter] + ",");
			} else {
				System.out.print(SequenceArray[counter]);
			}
			value++;
		}
		return SequenceArray;
	}

	public static int[] crossOutHigherMultiples(int[] SequenceArray, int value1, int number, double matrix,
			double xWidth, double square, int scale) {
		int position = value1 - 2;
		while (position + value1 < SequenceArray.length) {
			position = position + value1;
			if (SequenceArray[position] >= 0) {
				SequenceArray[position] = 0;
			}
		}
		if (value1 <= Math.sqrt(number)) {
			displayComposite(number, matrix, SequenceArray, xWidth, square, Colors, scale);
			System.out.println(Arrays.toString(SequenceArray));
		}
		return SequenceArray;
	}

	public static int[] sieve(int[] SequenceArray, int number, double matrix, double xWidth, double square, int scale) {
		for (int count = 0; count < SequenceArray.length; count++) {
			int value1 = SequenceArray[count];
			if (value1 >= 0) {
				numberOfPrimes++;
				SequenceArray = crossOutHigherMultiples(SequenceArray, value1, number, matrix, xWidth, square, scale);
				displayPrimes(SequenceArray, number, scale, value1);
			}
		}
		System.out.println(numberOfPrimes);
		return SequenceArray;
	}

	public static void displayNumber(double matrix, double xWidth, double square, int i) {
		double y = 1 - (xWidth / 2);
		double x = xWidth / 2;
		double count = i;
		int insert = i;
		while (count > matrix) {
			count = count - matrix;
			y = y - xWidth;
		}
		while (count > 1) {
			count--;
			x += xWidth;
		}
		if (count < 2) {
		}
		Color display = Colors[0];
		StdDraw.setPenColor(display);
		StdDraw.filledSquare(x, y, square);
		StdDraw.setPenColor(StdDraw.BLACK);
		String value = Integer.toString(insert);
		StdDraw.text(x, y, value);
	}

	public static void displayComposite(int number, double matrix, int[] SequenceArray, double xWidth, double square,Color[] Colors, int scale) {

		double heigth = 0;
		double width = 0;
		boolean prime = false;
		Color c1 = Colors[changeColours];
		changeColours++;
		if (changeColours >= Colors.length) {
			changeColours = 1;
		}
		for (int count = 0; count < SequenceArray.length; count++) {
			int value = SequenceArray[count];
			if (value > 0) {
				if (prime == false) {
					SequenceArray[count] = -2;
					prime = true;
				}
			}
			if (value == 0) {
				double number1 = count + 2;
				int number2 = (int) number1;
				SequenceArray[count] = -1;
				width = Math.ceil(number1 / matrix) - 1;
				if (number1 <= matrix) {
					heigth = number1 - 1;
				} else {
					while (number1 >= matrix + 1) {
						number1 = number1 - matrix;
						heigth = number1;
					}
					heigth = heigth - 1;
				}
				StdDraw.setPenColor(c1);
				heigth = (xWidth / 2) + (heigth * xWidth);
				width = 1 - (xWidth / 2) - (width * xWidth);
				StdDraw.filledSquare(heigth, width, square);
				String value2 = Integer.toString(number2);
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.text(heigth, width, value2);
				StdDraw.setPenColor(c1);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void displayNumbers2ToN(int number,double matrix,double xWidth, double square) {
		for (int i = 2; i <= number; i++) {
			displayNumber(matrix, xWidth, square, i);
		}
		changeColours++;
	}

	public static void displayPrimes(int[] SequenceArray, int number, int scale, int value1) {

		double xPos = 0.75;
		double yPos = .9;
		int counter7 = 1;
		double yPos2;
		StdDraw.setPenColor(StdDraw.BLACK);
		if (value1 <= number) {
			int value = value1;
			int counter9 = numberOfPrimes;
			while (counter9 >= 3) {
				counter9 = counter9 - 3;
				counter7++;
			}
			double xPos2 = xPos + (counter9 * 0.1);
			String value2 = Integer.toString(value);
			yPos = (0.9 / (20) * counter7) / 3;
			yPos2 = 0.9 - yPos;
			StdDraw.text(xPos2, yPos2, value2);
			counter9++;
			if (counter9 >= 3) {
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
}			