module com.example.monopoly_li {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    
    opens com.example.monopoly_li to javafx.fxml;
    exports com.example.monopoly_li;
    exports com.example.monopoly_li.Square;
    opens com.example.monopoly_li.Square to javafx.fxml;
}