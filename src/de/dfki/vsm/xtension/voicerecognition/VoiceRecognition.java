/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.vsm.xtension.voicerecognition;

import de.dfki.stickman3D.Stickman3D;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.xtension.stickman.StickmanExecutor;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author EmpaT
 */
public class VoiceRecognition extends Thread {

    RunTimeProject mProject;
    URL url;
    ConfigurationManager cm;
    Recognizer recognizer;
    Microphone microphone;

    boolean stopVoiceRecognition = false;

    public VoiceRecognition(RunTimeProject project) {
        this.mProject = project;
        try {
            System.out.println("Recording loaded...");
//            this.url = getClass().getClassLoader().getResource("res/voicegrammer/voice.config.xml");
            this.url = new File("voicegrammer/voice.config.xml").toURI().toURL();
                    //getClass().getClassLoader().getResource("res/voicegrammer/voice.config.xml");
            this.cm = new ConfigurationManager(url);
            this.recognizer = (Recognizer) cm.lookup("recognizer");
            this.microphone = (Microphone) cm.lookup("microphone");
            recognizer.allocate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (microphone.startRecording()) {
            System.out.println("Recording started. Start speaking");
        }
        /////////////////////////
        while (!stopVoiceRecognition) {

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();
                System.out.println(resultText);
                String[] splitResultText = resultText.split(" ");
                String name = splitResultText[0];

                if (resultText.contains("next"))
                {

                    System.out.println("You said: " + name);
                    mProject.setVariable("action", name);
                }
            }
        }
    }

    private void switchBackground(String background) {
        HBox stickmanBox = null;
        for (Map.Entry<String, Stickman3D> e : StickmanExecutor.stickmanContainer.entrySet()) {
            String stageID = e.getValue().getStageController().getStageIdentifier();
            try {
                stickmanBox = e.getValue().getStageController().getStickmanStage()
                        .getStickmanBox(stageID);
            } catch (Exception ex) {
                Logger.getLogger(VoiceRecognition.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (background.equalsIgnoreCase("zero")) {
            stickmanBox.setStyle("-fx-background-color: white");
        } else {
            String pathTobackground = getClass().getClassLoader().getResource("res/img/background/" + background + ".jpg").toExternalForm();
            stickmanBox.setStyle("-fx-background-image: url('" + pathTobackground + "'); "
                    + "-fx-background-position: center center; " + "-fx-background-repeat: stretch;");
        }
    }

}
