module RescueRelic {
    requires javafx.fxml;
	requires javafx.controls;
	requires javafx.base;
	opens application to javafx.graphics, javafx.fxml;
    exports minerd.relic;
    exports minerd.relic.item;
    exports minerd.relic.move;
    exports minerd.relic.pokemon;
    exports minerd.relic.dungeon;
}
