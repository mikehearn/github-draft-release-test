package io.github.mikehearn.githubdraftreleasetest;

import dev.hydraulic.conveyor.control.SoftwareUpdateController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        SoftwareUpdateController updateController = SoftwareUpdateController.getInstance();
        HelloController controller = fxmlLoader.getController();
        controller.setUpdateController(updateController);
        String versionString = "Unknown";
        if (updateController != null && updateController.getCurrentVersion() != null) {
            versionString = updateController.getCurrentVersion().getVersion();
        }
        stage.setTitle("Hello from version " + versionString);
        stage.setScene(scene);
        loadIconsForStage(stage);
        stage.show();
    }

    private static void loadIconsForStage(Stage stage) throws IOException {
        // This is required for Windows and Linux. On macOS there's no distinction between window
        // icons and app icons, so we don't bundle the icon PNGs separately and thus the loop here
        // doesn't do anything.
        var appDir = System.getProperty("app.dir");
        if (appDir == null)
            return;
        var iconsDir = Paths.get(appDir);
        try (var dirEntries = Files.newDirectoryStream(iconsDir, "icon-*.png")) {
            for (Path iconFile : dirEntries) {
                try (var icon = Files.newInputStream(iconFile)) {
                    stage.getIcons().add(new Image(icon));
                }
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
