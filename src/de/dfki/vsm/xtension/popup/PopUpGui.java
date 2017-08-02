package de.dfki.vsm.xtension.popup;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by EmpaT on 02.08.2017.
 */
public class PopUpGui
{
    protected Controller controller;
    protected Parent root;
    protected final Double mScaleFactor = 1.5d;
    protected final JFXPanel mJFXPanel = new JFXPanel();
    protected JFrame mFrame;
    protected PopUpExecutor popUpExecutor;

    // Configurable Values
    protected HashMap<String, String> mConfigValues = new HashMap<>();
    protected HashMap<Integer, Integer> responseMap = new HashMap<>();

    public void show(boolean visible)
    {
        try
        {
            mFrame.repaint();
            mFrame.setVisible(visible);
            mFrame.toFront();
            centreWindow(mFrame);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void hide()
    {
        mFrame.setVisible(false);
    }

    public void init(PopUpExecutor executor, HashMap<String, String> config)
    {

        popUpExecutor = executor;
        mConfigValues = config;

        mFrame = new JFrame("FRAME");
        mFrame.add(mJFXPanel);
        // Set Not Rezizable
        mFrame.setResizable(false);
        // Set Always On Top
        mFrame.setAlwaysOnTop(false);
        // Set Undecorated
//        mFrame.setUndecorated(true);

        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(() -> initFX());
    }

    protected void initFX()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/res/de.dfki.vsm.xtension.popup/" + "popup.fxml"));
        controller = new Controller();
        fxmlLoader.setController(controller);
        controller.addListener(popUpExecutor);

        try
        {
            root =  fxmlLoader.load();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        // get root
        Group group = new Group(root);

        // set root background
        root.setStyle("-fx-background-color: #00000000;");

        // place centered
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(group);

        // set general background, note alpha value must > 0 to ensure modal feature
        rootPane.setStyle("-fx-background-color: #0000ff50;");

        Scene scene = new Scene(rootPane);

        mJFXPanel.setScene(scene);

        double x = Screen.getPrimary().getVisualBounds().getWidth();
        double y = Screen.getPrimary().getVisualBounds().getHeight();
        mFrame.setSize(900, 600);

        controller.setFrame(mFrame);
    }

    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
}
