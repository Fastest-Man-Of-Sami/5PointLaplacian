module com.example.fivepointlaplacian {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.fivepointlaplacian to javafx.fxml;
    exports com.example.fivepointlaplacian;
}