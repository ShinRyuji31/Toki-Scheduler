package com.toki.ui.util;

import javafx.scene.Scene;
import java.io.File;
import java.io.InputStream;
import java.nio.file.*;

public class CssManager {

    private static final String[] GLOBAL_CSS = {
            "css/_global.css"
    };

    private static final String RESOURCE_BASE_PATH = "src/main/resources/"; 

    public static void apply(Scene scene, String... extraCss) {
        reload(scene, extraCss);
        enableHotReload(scene, extraCss);
    }

    private static void reload(Scene scene, String... extraCss) {
        scene.getStylesheets().clear();

        try {
            System.out.println("--- Attempting CSS Reload ---");
            
            for (String css : GLOBAL_CSS) {
                loadCssFile(scene, css);
            }

            for (String css : extraCss) {
                loadCssFile(scene, css);
            }

            System.out.println("CSS Reloaded successfully!");

        } catch (Exception e) {
            System.err.println("CSS Reload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void enableHotReload(Scene scene, String... extraCss) {
        scene.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode().toString().equals("R")) {
                reload(scene, extraCss);
            }
        });
    }

    private static void loadCssFile(Scene scene, String cssPath) throws Exception {
        
        Path sourcePath = Paths.get(RESOURCE_BASE_PATH, cssPath);
        File cssFile = sourcePath.toFile();

        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
            System.out.println("Loaded (Source): " + cssPath);
        } else {
            String classPath = "/" + cssPath.replace(File.separator, "/");
            InputStream in = CssManager.class.getResourceAsStream(classPath);
            
            if (in == null) {
                System.err.println("Cannot find CSS resource (Source or Class Path): " + cssPath);
                return;
            }

            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "javafx_css_reload");
            Files.createDirectories(tempDir);
            
            Path tempCssFile = tempDir.resolve(
                    cssPath.replace(File.separator, "_") + "_" + System.nanoTime() + ".css"
            );

            Files.copy(in, tempCssFile, StandardCopyOption.REPLACE_EXISTING);
            scene.getStylesheets().add(tempCssFile.toUri().toString());
            System.out.println("Loaded (Class Path/Temp): " + cssPath);
        }
    }
}