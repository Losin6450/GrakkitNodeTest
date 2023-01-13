package me.losin6450.core;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.engine.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Script manager.
 */
public class ScriptManager {


    private static List<Task> tasks;

    /**
     * The Pool.
     */
    public static IJavetEnginePool<NodeRuntime> pool;
    /**
     * The Main engine.
     */
    public static IJavetEngine<NodeRuntime> mainEngine;
    private static ServerAdapter adapter;
    /**
     * The constant main.
     */
    public static Script main;
    /**
     * The constant defaultMain.
     */
    public static final String defaultMain = "index.js";

    /**
     * On load.
     *
     * @throws JavetException the javet exception
     */
    public static void onLoad() throws JavetException {
        DriverManager.getDrivers();
        pool = new JavetEnginePool<>(
                new JavetEngineConfig()
                        .setAllowEval(true)
                        .setGlobalName("globalThis")
                        .setJSRuntimeType(JSRuntimeType.Node)
                        .setGCBeforeEngineClose(true)
                        .setAutoSendGCNotification(true));
        mainEngine = pool.getEngine();
        tasks = new ArrayList<>();

    }

    /**
     * Sets adapter.
     *
     * @param adapter the adapter
     */
    public static void setAdapter(ServerAdapter adapter) {
        if(ScriptManager.adapter != null) return;
        ScriptManager.adapter = adapter;
    }

    /**
     * Locate url.
     *
     * @param clazz the clazz
     * @return the url
     */
    public static URL locate (Class<?> clazz) {
        try {
            URL resource = clazz.getProtectionDomain().getCodeSource().getLocation();
            if (resource instanceof URL) return resource;
        } catch (Throwable error) {
            // do nothing
        }
        URL resource = clazz.getResource(clazz.getSimpleName() + ".class");
        if (resource instanceof URL) {
            String link = resource.toString();
            String suffix = clazz.getCanonicalName().replace('.', '/') + ".class";
            if (link.endsWith(suffix)) {
                String path = link.substring(0, link.length() - suffix.length());
                if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);
                try {
                    return new URL(path);
                } catch (Throwable error) {
                    // do nothing
                }
            }
        }
        return null;
    }

    /**
     * Open.
     *
     * @param root the root
     * @throws IOException the io exception
     */
    public static void open(String root) throws IOException {
        Paths.get(root).toFile().mkdirs();
        Config[] configs = {
                new Config(Config.ConfigType.JSON, root, ".grakkitrc", false),
                new Config(Config.ConfigType.YAML, root, "config.yml", false),
                new Config(Config.ConfigType.JSON, root, "grakkit.json", false),
                new Config(Config.ConfigType.JSON, root, "package.json", true)
        };
        String main = defaultMain;
        Config conf = null;
        for(Config config : configs){
            String m = config.getMain();
            if (m != null){
                main = m;
                conf = config;
                break;
            }
        }
        try {
            File file = new File(root, main);
            if(!file.exists()) file.createNewFile();
            ScriptManager.main = new Script(file, "main", root, null, conf);
            ScriptManager.main.execute();
        } catch (Throwable error){
            error.printStackTrace();
        }
    }

    /**
     * Close.
     *
     * @throws JavetException the javet exception
     */
    public static void close() throws JavetException {
        main.destroy();
    }

    /**
     * Patch.
     *
     * @param loader the loader
     */
    public static void patch(Loader loader){
        loader.addURL(ScriptManager.locate(ScriptManager.class));
        Thread.currentThread().setContextClassLoader(loader);
    }

    /**
     * Gets adapter.
     *
     * @return the adapter
     */
    public static ServerAdapter getAdapter() {
        return adapter;
    }


    /**
     * Tick.
     */
    public static void tick(){
        tasks.forEach(task -> {
            task.current++;
            if(task.current >= task.getClock()){
                task.current = 0;
                task.getRunnable().accept(task);
                if(!task.isInfinite()){
                    tasks.remove(task);
                }
            }
        });
    }

    /**
     * Add task boolean.
     *
     * @param task the task
     * @return the boolean
     */
    public static boolean addTask(Task task){
        return tasks.add(task);
    }

    /**
     * Remove task boolean.
     *
     * @param task the task
     * @return the boolean
     */
    public static boolean removeTask(Task task){
        return tasks.remove(task);
    }
}