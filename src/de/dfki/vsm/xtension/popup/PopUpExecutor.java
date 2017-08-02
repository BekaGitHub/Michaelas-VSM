package de.dfki.vsm.xtension.popup;

import de.dfki.vsm.model.config.ConfigFeature;
import de.dfki.vsm.model.project.AgentConfig;
import de.dfki.vsm.model.project.PluginConfig;
import de.dfki.vsm.model.scenescript.ActionFeature;
import de.dfki.vsm.runtime.activity.AbstractActivity;
import de.dfki.vsm.runtime.activity.SpeechActivity;
import de.dfki.vsm.runtime.activity.executor.ActivityExecutor;
import de.dfki.vsm.runtime.activity.scheduler.ActivityWorker;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.util.log.LOGConsoleLogger;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by EmpaT on 02.08.2017.
 */
public class PopUpExecutor extends ActivityExecutor implements PopUpViewListener
{
    // The GUI
    static PopUpGui gui = null;

    ActivityWorker mActivityWorker = null;

    public static  final HashSet<ActivityWorker> mActivityWorkers = new HashSet<>();
    // Configuration values
    public static HashMap<String, String> mConfigValues = new HashMap<>();
    private static PopUpExecutor popUpExecutor;

    // The singelton logger instance
    private final LOGConsoleLogger mLogger = LOGConsoleLogger.getInstance();

    public PopUpExecutor(PluginConfig config, RunTimeProject project)
    {
        super(config, project);
        if(popUpExecutor == null)
        {
            popUpExecutor = this;
        }
    }

    @Override
    public String marker(long id)
    {
        return "$(" + id + ")";
    }

    @Override
    public void execute(AbstractActivity activity)
    {
        // Agent config
        final String actor = activity.getActor();
        AgentConfig agent = mProject.getAgentConfig(actor);

        if (activity instanceof SpeechActivity)
        {
            SpeechActivity sa = (SpeechActivity) activity;
            String text = sa.getTextOnly("$(").trim();
            LinkedList<String> timemarks = sa.getTimeMarks("$(");

            // If text is empty - assume activity has empty text but has marker activities registered
            if (text.isEmpty())
            {
                for (String tm : timemarks)
                {
                    mProject.getRunTimePlayer().getActivityScheduler().handle(tm);
                }
            }
        }
        else
        {
            final String name = activity.getName();

            final LinkedList<ActionFeature> features = activity.getFeatures();

            if (name.equalsIgnoreCase("show"))
            {
                for (ActionFeature af : activity.getFeatures())
                {
                    switchGui(af);
                }

                // wait until we got feedback
                mLogger.message("ActivityWorker for SpikeView waiting ....");
                synchronized (mActivityWorkers)
                {
                    mActivityWorker = (ActivityWorker) Thread.currentThread();
                    mActivityWorkers.add(mActivityWorker);

                    while (mActivityWorkers.contains(mActivityWorker))
                    {
                        try
                        {
                            mActivityWorkers.wait();
                        }
                        catch (InterruptedException exc)
                        {
                            mLogger.failure(exc.toString());
                        }
                    }

                }
                mLogger.message("ActivityWorker for Webview done ....");
            }
        }
    }

    public void switchGui(ActionFeature af)
    {
        if(af.getKey().equalsIgnoreCase("on"))
        {
            String val = af.getVal().replace("'", "");

            switch(val)
            {
                case "POPUP":
                    gui = new PopUpGui();
                    break;
            }

            showGui();
        }

    }

    private void showGui()
    {
        gui.init(getInstance(), mConfigValues);
        Platform.runLater(() -> gui.show(true));
    }

    public static PopUpExecutor getInstance()
    {
        return popUpExecutor;
    }

    @Override
    public void updateOnPopUpView(HashMap<String, Integer> values)
    {
        synchronized (mActivityWorkers)
        {
            if(mActivityWorkers.contains(mActivityWorker))
            {
                mActivityWorkers.remove(mActivityWorker);
            }

            mActivityWorkers.notifyAll();
            mLogger.message("mActivityWorkers(" + mActivityWorkers.size() + ") notified");
        }
    }

    @Override
    public void launch()
    {
        for (ConfigFeature cf : mConfig.getEntryList())
        {
            mConfigValues.put(cf.getKey(), cf.getValue());
        }
    }

    @Override
    public void unload()
    {
        mLogger.message("GUI destroyed");
        if(gui != null) {
            Platform.runLater(() ->
            {
                gui.hide();
                gui = null;
            } );
        }
    }
}
