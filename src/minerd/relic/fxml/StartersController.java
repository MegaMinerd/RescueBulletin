package minerd.relic.fxml;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import minerd.relic.data.Starters;
import minerd.relic.data.Text;
import minerd.relic.file.Rom;
import minerd.relic.file.RomFile;

public class StartersController {
	public TableView<PlayerOption> playerTable;
	public TableView<PartnerOption> partnerTable;
	public TableColumn<PlayerOption, Integer> playerId, playerPokemon;
	public TableColumn<PlayerOption, String> playerNature, playerGender;
	public TableColumn<PartnerOption, Integer> partnerId, partnerPokemon;
	public Button apply;

	public void load(Starters starters) {
		System.out.println(playerPokemon.isEditable());
		ObservableList<PlayerOption> playerList = FXCollections.observableArrayList();
		for(int i = 0; i<26; i++)
			playerList.add(new PlayerOption(i, Text.getText("Natures", i/2), i%2==0 ? "Male" : "Female", starters.getPlayer(i)));

		ObservableList<PartnerOption> partnerList = FXCollections.observableArrayList();
		for(int i = 0; i<10; i++)
			partnerList.add(new PartnerOption(i, starters.getPartner(i)));

		playerId.setCellValueFactory(new PropertyValueFactory<PlayerOption, Integer>("id"));
		playerPokemon.setCellValueFactory(new PropertyValueFactory<PlayerOption, Integer>("species"));
		playerPokemon.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		playerNature.setCellValueFactory(new PropertyValueFactory<PlayerOption, String>("nature"));
		playerGender.setCellValueFactory(new PropertyValueFactory<PlayerOption, String>("gender"));
		partnerId.setCellValueFactory(new PropertyValueFactory<PartnerOption, Integer>("id"));
		partnerPokemon.setCellValueFactory(new PropertyValueFactory<PartnerOption, Integer>("species"));
		partnerPokemon.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		playerTable.setItems(playerList);
		partnerTable.setItems(partnerList);
	}

	public void applyChanges() {
		RomFile rom;
		try{
			rom = Rom.getAll();
		
			Starters starters = new Starters(0);
			int[] players = new int[26];
			for(int i = 0; i<26; i++)
				players[i] = (int)playerTable.getItems().get(i).getSpecies();
			starters.setPlayers(players);
		
			int[] partners = new int[10];
			for(int i = 0; i<10; i++)
				partners[i] = (int)partnerTable.getItems().get(i).getSpecies();
			starters.setPartners(partners);
		
			starters.save(rom);
			Rom.saveAll(rom);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public class PlayerOption {
		private int id, species;
		private String nature, gender;

		public PlayerOption(int idIn, String natureIn, String genderIn, int speciesIn) {
			this.id = idIn;
			this.nature = natureIn;
			this.gender = genderIn;
			this.species = speciesIn;
		}

		public int getId() {
			return id;
		}

		public int getSpecies() {
			return species;
		}

		public String getNature() {
			return nature;
		}

		public String getGender() {
			return gender;
		}
	}

	public class PartnerOption {
		private int id, species;

		public PartnerOption(int idIn, int speciesIn) {
			this.id = idIn;
			this.species = speciesIn;
		}

		public int getId() {
			return id;
		}

		public int getSpecies() {
			return species;
		}
	}
}
