import java.util.Scanner;
import javax.swing.JOptionPane;

public class ClimbingClubRecord {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		JOptionPane.showMessageDialog(null, "THE CURRENT AMOUNT OF HARNESSES IS: ");

		int input = JOptionPane.showConfirmDialog(null, "DO YOU WANT TO ADD A HARNESS?");
		if (input == JOptionPane.YES_OPTION) {
			String make = JOptionPane.showInputDialog("ENTER MAKE OF THE HARNESS TO ADD:");
			String model = JOptionPane.showInputDialog("ENTER MODEL NUMBER OF THE HARNESS TO ADD:");
		}

		int input2 = JOptionPane.showConfirmDialog(null, "DO YOU WANT TO REMOVE A HARNESS?");
		if (input2 == JOptionPane.YES_OPTION) {
			String make = JOptionPane.showInputDialog("ENTER MAKE OF THE HARNESS TO REMOVE:");
			String model = JOptionPane.showInputDialog("ENTER MODEL NUMBER OF THE HARNESS TO REMOVE:");
		}

		int input3 = JOptionPane.showConfirmDialog(null, "HAS A HARNESS BEEN SAFTEY CHECKED?");
		if (input3 == JOptionPane.YES_OPTION) {
			String make = JOptionPane.showInputDialog("ENTER MAKE OF THE HARNESS TO UPDATE SAFTEY CHECK:");
			String model = JOptionPane.showInputDialog("ENTER MODEL NUMBER OF THE HARNESS TO UPDATE SAFTEY CHECK:");
			String instructor = JOptionPane.showInputDialog("ENTER INSTRUCTOR WHO CARRIED OUT THE CHECK:");
		}

		int input4 = JOptionPane.showConfirmDialog(null, "DOES A MEMBER NEED A HARNESS?");
		if (input4 == JOptionPane.YES_OPTION) {
			String make = JOptionPane.showInputDialog("ENTER MAKE OF THE HARNESS TO LOAN:");
			String model = JOptionPane.showInputDialog("ENTER MODEL NUMBER OF THE HARNESS TO LOAN:");
			String member = JOptionPane.showInputDialog("ENTER THE MEMBER WHO NEEDS THE HARNESS:");
		}

		int input5 = JOptionPane.showConfirmDialog(null, "DOES A MEMBER NEED A HARNESS?");
		if (input5 == JOptionPane.YES_OPTION) {
			String make = JOptionPane.showInputDialog("ENTER MAKE OF THE HARNESS TO RETURN:");
			String model = JOptionPane.showInputDialog("ENTER MODEL NUMBER OF THE HARNESS TO RETURN:");
			String member = JOptionPane.showInputDialog("ENTER THE MEMBER WHO RETURNED THE HARNESS:");
		}

	}

}
