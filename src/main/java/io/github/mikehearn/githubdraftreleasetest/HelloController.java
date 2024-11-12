package io.github.mikehearn.githubdraftreleasetest;

import dev.hydraulic.conveyor.control.SoftwareUpdateController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Platform;

public class HelloController {
    @FXML
    private Label localVersionLabel;
    @FXML
    private Label remoteVersionLabel;
    @FXML
    private Label updateStatusLabel;
    @FXML
    private Button updateButton;

    private SoftwareUpdateController updateController;
    private SoftwareUpdateController.Version localVersion;
    private SoftwareUpdateController.Version remoteVersion;

    public void setUpdateController(SoftwareUpdateController controller) {
        this.updateController = controller;
        updateVersionInfo();
    }

    private void updateVersionInfo() {
        if (updateController != null) {
            localVersion = updateController.getCurrentVersion();
            localVersionLabel.setText("Local Version: " + (localVersion != null ? localVersion.getVersion() : "Unknown"));

            new Thread(() -> {
                try {
                    remoteVersion = updateController.getCurrentVersionFromRepository();
                    Platform.runLater(() -> {
                        remoteVersionLabel.setText("Remote Version: " + remoteVersion.getVersion());
                        updateUpdateStatus();
                    });
                } catch (SoftwareUpdateController.UpdateCheckException e) {
                    Platform.runLater(() -> {
                        remoteVersionLabel.setText("Remote Version: Error checking");
                        updateUpdateStatus();
                    });
                }
            }).start();
        } else {
            localVersionLabel.setText("Local Version: Unknown (Update controller not available)");
            remoteVersionLabel.setText("Remote Version: Unknown (Update controller not available)");
            updateUpdateStatus();
        }
    }

    private void updateUpdateStatus() {
        if (updateController == null) {
            updateButton.setDisable(true);
            updateStatusLabel.setText("Update checking not available.");
            return;
        }

        SoftwareUpdateController.Availability availability = updateController.canTriggerUpdateCheckUI();
        boolean updateAvailable = (localVersion != null && remoteVersion != null && remoteVersion.compareTo(localVersion) > 0);

        if (availability == SoftwareUpdateController.Availability.AVAILABLE) {
            updateButton.setDisable(false);
            if (updateAvailable) {
                updateStatusLabel.setText("Update available. Click the button to update.");
            } else {
                updateStatusLabel.setText("No update available. Click the button to check again.");
            }
        } else {
            updateButton.setDisable(true);
            switch (availability) {
                case UNIMPLEMENTED:
                    updateStatusLabel.setText("Updates not implemented on this platform.");
                    break;
                case UNSUPPORTED_PACKAGE_TYPE:
                    updateStatusLabel.setText("Updates not supported for this package type.");
                    break;
                case NON_GUI_APP:
                    updateStatusLabel.setText("Updates not available for non-GUI apps.");
                    break;
                default:
                    updateStatusLabel.setText("Updates not available.");
                    break;
            }
        }
    }

    @FXML
    protected void onUpdateButtonClick() {
        if (updateController != null && updateController.canTriggerUpdateCheckUI() == SoftwareUpdateController.Availability.AVAILABLE) {
            updateController.triggerUpdateCheckUI();
            updateUpdateStatus();
        }
    }
}
