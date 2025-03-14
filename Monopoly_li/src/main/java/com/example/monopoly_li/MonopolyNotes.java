/* be able to click on cells for cell information dynamically
package com.example.monopoly_li;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import org.w3c.dom.events.MouseEvent;
public class MonopolyNotes {
    private void attachClickEvent() {
        EventHandler<MouseEvent> click = e -> {
            if(e.getButton() == MouseButton.PRIMARY) {

            } else if(e.getButton() == MouseButton.SECONDARY) {

            }
        };
        cell.getCell().addEventFilter(MouseEvent.MOUSE_CLICKED, click);
    }
}
*/