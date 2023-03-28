module RescueRelic {
    requires javafx.fxml;
	requires transitive javafx.controls;
	requires javafx.base;
	requires java.desktop;
	requires transitive javafx.swing;
	opens application to javafx.graphics, javafx.fxml;
    exports minerd.relic;
    exports minerd.relic.area;
    exports minerd.relic.item;
    exports minerd.relic.move;
    exports minerd.relic.tree;
    exports minerd.relic.lists;
    exports minerd.relic.dungeon;
    exports minerd.relic.pokemon;
}
