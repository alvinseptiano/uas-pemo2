import UI.MainUI;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        MainUI mainUI;
        try {
            mainUI = new MainUI();
            mainUI.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
