module RescueRelic {
	requires java.desktop;
	requires javafx.base;
    requires javafx.fxml;
	requires transitive javafx.controls;
	requires transitive javafx.swing;
	opens application to javafx.graphics, javafx.fxml;
    exports minerd.relic;
    exports minerd.relic.data;
    exports minerd.relic.data.dungeon;
    exports minerd.relic.fxml;
    exports minerd.relic.tree;
}
