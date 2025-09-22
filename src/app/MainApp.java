package app;

public class MainApp {

    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.startApplication();
        } catch (Exception e) {
            System.out.println("Error while launchin app : " + e.getMessage());
        }
    }
    
}
