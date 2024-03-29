package minerd.relic.fxml;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import minerd.relic.data.FriendArea;

public class AreaController {
    FriendArea area;
	public Label areaName;
	public TextField population, price;
	public ChoiceBox<String> condition;
	public Button apply;

	public void load(FriendArea area) {
	    this.area = area;
		areaName.setText(area.getName());
		population.setText(area.getPopulation()+"");
		condition.getItems().addAll("Buy (Story)", "Buy (Post-game)", "Wonder mail event", "Lengendary quest");
		condition.getSelectionModel().select(area.getCondition());
		price.setText(area.getPrice()+"");
	}
    
    public void applyChanges(){
        area.setName(areaName.getText());
        area.setPopulation(Integer.parseInt(population.getText()));
        area.setCondition(condition.getSelectionModel().getSelectedIndex());
        area.setPrice(Integer.parseInt(price.getText()));
    }
}