module com.example.monopoly_li {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.monopoly_li to javafx.fxml;
    exports com.example.monopoly_li;
}