package minerd.relic.lists;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import minerd.relic.RomManipulator;
import minerd.relic.tree.DataTreeItem;

public class StartersDataTreeNode extends DataTreeItem {
	int playerPointer, partnerPointer;

	public StartersDataTreeNode(int player, int partner) {
		super("Starters");
		playerPointer = player;
		partnerPointer = partner;
	}
	
	@Override
	public Node select(){
		AnchorPane starterDataPane = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("starters.fxml"));
			starterDataPane = loader.load();
			StartersController controller = loader.getController();
			

			RomManipulator.seek(playerPointer);
			int[] players = new int[26];
			for(int i=0; i<26; i++)
				players[i] = RomManipulator.readUnsignedShort();
			
			RomManipulator.seek(partnerPointer);
			int[] partners = new int[10];
			for(int i=0; i<10; i++)
				partners[i] = RomManipulator.readUnsignedShort();
			
			Starters starters = new Starters(players, partners);
			
			controller.load(starters);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return starterDataPane;
	}

}
