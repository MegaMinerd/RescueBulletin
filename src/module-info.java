module RescueRelic {
    requires javafx.fxml;
	requires javafx.controls;
	opens application to javafx.graphics, javafx.fxml;
    exports minerd.relic;
    exports minerd.relic.item;
    exports minerd.relic.move;
    exports minerd.relic.pokemon;
}
