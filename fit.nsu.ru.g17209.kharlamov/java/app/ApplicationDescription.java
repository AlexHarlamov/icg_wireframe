package app;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class ApplicationDescription provides all the necessary elements to organize simple description
 */
public class ApplicationDescription {

    /**
     * Path to the description file
     */
    private String descriptionFilePath = "src/files/about.txt";

    /**
     * Description text
     */
    private String description;

    /**
     * Menu item for the menu bar
     */
    private JMenuItem menuItem;

    /**
     * Frame for the popup window
     */
    private JFrame parentFrame = new JFrame();

    public ApplicationDescription(){

        try{
            description = new String (Files.readAllBytes(Paths.get(descriptionFilePath)));
        }catch (IOException e){
            description = "Description file not available";
        }

        menuItem = new JMenuItem("About app");
        menuItem.addActionListener(e -> JOptionPane.showMessageDialog(parentFrame, description));
    }

    public JMenuItem getMenuItem() {
        return menuItem;
    }
}
