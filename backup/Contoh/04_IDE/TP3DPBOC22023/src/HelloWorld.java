import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class HelloWorld extends JFrame{

    public static void main(String[] args) {
        HelloWorld window = new HelloWorld();

        window.setSize(400,300);
        window.setContentPane(window.mainPanel);
        window.setVisible(true);
    }

    private JPanel mainPanel;
    private JTable catTable;

    private ArrayList<Cat> listCat;

    public HelloWorld() {
        listCat = new ArrayList<>();

        listCat.add(new Cat("yelly", "Kampung"));
        listCat.add(new Cat("Blacky", "Kampung"));
        listCat.add(new Cat("Whity", "Kampung"));

        catTable.setModel(SetTable());
    }

    public final DefaultTableModel SetTable(){
        Object[] column = {"No.", "Nama", "Ras"};
        DefaultTableModel temp = new DefaultTableModel(null, column);

        for (int i = 0; i < listCat.size(); i++){
            Object[] row = new Object[3];
            row[0] = i;
            row[1] = listCat.get(i).getName();
            row[2] = listCat.get(i).getRas();

            temp.addRow(row);
        }

        return temp;
    }
}
