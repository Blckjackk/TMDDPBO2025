import javax.swing.*;
import java.util.ArrayList;

public class HelloWorld extends JFrame{

    public static void main(String[] args) {
        HelloWorld window = new HelloWorld();

        window.setSize(400, 300);
        window.setContentPane(window.mainPanel);
        window.setVisible(true);
    }


    private JPanel mainPanel;
    private JTable catTable;
    private ArrayList<Cat> listCat;
}
