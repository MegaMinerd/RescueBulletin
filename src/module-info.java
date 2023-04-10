module RescueRelic {
    requires javafx.fxml;
	requires transitive javafx.controls;
	requires javafx.base;
	requires java.desktop;
	requires transitive javafx.swing;
	opens application to javafx.graphics, javafx.fxml;
    exports minerd.relic;
    exports minerd.relic.data;
    exports minerd.relic.fxml;
    exports minerd.relic.area;
    exports minerd.relic.move;
    exports minerd.relic.tree;
    exports minerd.relic.dungeon;
}
