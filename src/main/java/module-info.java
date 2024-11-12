module io.github.mikehearn.githubdraftreleasetest {
    requires javafx.controls;
    requires javafx.fxml;
    requires dev.hydraulic.conveyor.control;

    opens io.github.mikehearn.githubdraftreleasetest to javafx.fxml;
    exports io.github.mikehearn.githubdraftreleasetest;
}
