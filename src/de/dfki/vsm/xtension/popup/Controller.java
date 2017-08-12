package de.dfki.vsm.xtension.popup;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by EmpaT on 02.08.2017.
 */
public class Controller implements Initializable
{
    //Es wird aus Gui-Klassen initialisiert
    protected static JFrame sFrame;
    @FXML
    Label popUpText;

    String text;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

            try
            {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream("popUpText.txt") ,
                                "UTF-8"));
                while((text = br.readLine()) != null)
                    popUpText.setText(popUpText.getText() + " " + text);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

    }

    // listeners for updates
    protected static ArrayList<PopUpViewListener> sListeners = new ArrayList<>();

    public void addListener(PopUpViewListener listener)
    {
        if (!sListeners.contains(listener))
        {
            sListeners.add(listener);
        }
    }


    public void setFrame(JFrame frame)
    {
        sFrame = frame;
    }
}
