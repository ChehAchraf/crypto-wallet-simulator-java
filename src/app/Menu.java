package app;

import java.util.Scanner;

public class Menu {

    private Scanner scanner;
    private boolean isActive;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.isActive = true;
    }

    public void startApplication() {
        while (isActive) {
            System.out.println("================================");
            System.out.println("     crypto wallet simulator    ");
            System.out.println("================================");
            handleUserChoice();
        }
    }

    private void handleUserChoice() {
        System.out.print("Enter your choice : ");
        String choice = scanner.nextLine();
    }

}
