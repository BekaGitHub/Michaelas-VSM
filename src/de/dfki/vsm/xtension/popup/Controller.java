package de.dfki.vsm.xtension.popup;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by EmpaT on 02.08.2017.
 */
public class Controller
{
    //Es wird aus Gui-Klassen initialisiert
    protected static JFrame sFrame;

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
