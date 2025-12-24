module gamer_hub
{
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    
    opens gamer_hub to javafx.fxml;
    opens gamer_hub.model to com.fasterxml.jackson.databind;
    opens gamer_hub.service to com.fasterxml.jackson.databind;
    opens gamer_hub.controllers to javafx.fxml;
    
    exports gamer_hub;
}