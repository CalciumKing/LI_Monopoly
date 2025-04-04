package com.example.monopoly_li;

import com.example.monopoly_li.Square.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
    Name: Landen Ingerslev
    Assignment: Java Monopoly Project
    Description: SQL utilities manager, all that happens here is
    importing or exporting data from the SQL database.
*/

public class SQLUtils {
    public static boolean gameExists(int gameID, String password) {
        try (Connection connection = connectDB()) {
            if (connection == null) return false;
            
            String sql = "select * from game where game_id = ? and password = ?;";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameID);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            
            return result.next();
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In gameExists",
                    "Error Getting Game From Database",
                    "That game does not exist or a different error occurred."
            );
            e.printStackTrace();
        }
        return false;
    }
    
    // region Creation Methods
    public static int createNewGame(String password, int numPlayers) {
        try(Connection connection = connectDB()) {
            if(connection == null) return -1;
            
            String sql = "insert into game (password, turn) values (?, 0);";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, password);
            statement.executeUpdate();
            
            createPlayers(numPlayers);
            
            return getNewestGameID();
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In createNewGame",
                    "Error Creating Game",
                    "There was an error creating a game."
            );
            e.printStackTrace();
        }
        return -1;
    }
    
    private static void createPlayers(int numPlayers) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            for (int i = 0; i < numPlayers; i++) {
                String sql = "insert into players (game_id, balance, position, inJail) values (?, 1500, 0, 0);";
                
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, getNewestGameID());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In createPlayers",
                    "Error Creating Players",
                    "An error occurred when creating new players for the newest created game."
            );
            e.printStackTrace();
        }
    }
    // endregion
    
    // region Get Data Methods
    private static int getNewestGameID() {
        try(Connection connection = connectDB()) {
            if(connection == null) return -1;
            
            String sql = "select max(game_id) from game;";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            
            if(result.next())
                return result.getInt(1);
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error In getNewestGameID",
                    "Error Returning new GameID",
                    "There was an error retrieving the newly created game id."
            );
            e.printStackTrace();
        }
        return -1;
    }
    
    public static Player[] getPlayers (int gameID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from players where game_id = ?;";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, gameID);
            ObservableList<Player> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();
            
            while(result.next())
                data.add(new Player(
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getBoolean(5),
                    result.getInt(4),
                    getOwnedProperties(gameID, result.getInt(1))
                ));
            
            Player[] players = new Player[data.size()];
            for (int i = 0; i < data.size(); i++) {
                players[i] = data.get(i);
                for(Property property : players[i].getOwned())
                    property.setOwner(players[i]);
            }
            
            return players;
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error Getting Players",
                    "Error in getPlayers",
                    "There was an error running the SQL information to get players from the database."
            );
            e.printStackTrace();
        }
        return null;
    }
    
    private static ArrayList<Property> getOwnedProperties(int gameID, int playerID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from properties where property_id in (" +
                    "select property_id from owned where game_id = ? and player_id = ?);";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, gameID);
            prepared.setInt(2, playerID);
            
            ObservableList<Property> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();
            
            while(result.next()) {
                data.add(new Property(
                        result.getString("property_name"),
                        result.getInt("property_id"),
                        result.getInt("price"),
                        new int[]{
                                result.getInt("house0"),
                                result.getInt("house1"),
                                result.getInt("house2"),
                                result.getInt("house3"),
                                result.getInt("house4"),
                                result.getInt("hotel")
                        },
                        result.getString("color"),
                        getStage(gameID, result.getInt("property_id"))
                ));
            }
            
            return new ArrayList<>(data);
        } catch (Exception e) {
            Utils.normalAlert(
                    Alert.AlertType.ERROR,
                    "Error Getting Players",
                    "Error in getOwnedProperties",
                    "There was an error running the SQL information to get owned properties from the database."
            );
            e.printStackTrace();
        }
        return null;
    }
    
    public static Cell[] getAllProperties(int gameID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from properties";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            ResultSet result = prepared.executeQuery();
            Cell[] data = new Cell[40];
            
            int i = 0;
            while(result.next()) {
                String name = result.getString("property_name"),
                        color = result.getString("color");
                int id = result.getInt("property_id"),
                        price = result.getInt("price");
                
                if(!color.equals("SPECIAL") && !color.equals("UTILITY")) { // purchasable property
                    data[i] = new Property(
                        name,
                        id,
                        price,
                        new int[] {
                                result.getInt("house0"),
                                result.getInt("house1"),
                                result.getInt("house2"),
                                result.getInt("house3"),
                                result.getInt("house4"),
                                result.getInt("hotel")
                        },
                        color,
                        getStage(gameID, id)
                    );
                } else if(color.equals("UTILITY")) { // non-purchasable utility
                    data[i] = new Property(
                        name,
                        id,
                        price,
                        new int[] { // eventually change values to dice roll multipliers
                            48,
                            120,
                            9999999,
                            9999999,
                            9999999,
                            9999999
                        },
                        color,
                        getStage(gameID, id)
                    );
                } else { // non-purchasable action and non-action spaces
                    data[i] = switch(name) {
                        case "Go" -> new Cell(
                            name,
                            Type.GO,
                            id
                        );
                        case "Jail" -> new Cell(
                            name,
                            Type.JAIL,
                            id
                        );
                        case "Free Parking" -> new Cell(
                            name,
                            Type.FREE_PARKING,
                            id
                        );
                        case "Go To Jail" -> new Action(
                            name,
                            "Go directly to Jail. Do not pass GO, do not collect $200.",
                            Type.GO_TO_JAIL,
                            id,
                            player -> {
                                player.setInJail(true);
                                player.goToJail();
                            }
                        );
                        case "Chance 1", "Chance 2", "Chance 3" -> {
                            Card card = new Card(false);
                            yield new Action(
                                    name,
                                    card.getCard().getDescription(),
                                    Type.CHANCE,
                                    id,
                                    player -> card.getCard().execute(player)
                            );
                        }
                        case "Community Chest 1", "Community Chest 2", "Community Chest 3" -> {
                            Card card = new Card(true);
                            yield new Action(
                                    name,
                                    card.getCard().getDescription(),
                                    Type.CHEST,
                                    id,
                                    player -> card.getCard().execute(player)
                            );
                        }
                        case "Income Tax", "Luxury Tax" -> new Action(
                                    name,
                                    "Pay Tax",
                                    Type.TAX,
                                    id,
                                    player -> player.payTax(.1)
                            );
                        default -> { // shouldn't ever run, all other values in use
                            System.out.println(id + ", " + name);
                            yield null;
                        }
                    };
                }
                i++;
            }
            
            return data;
        } catch (Exception e) {
            Utils.normalAlert(
                Alert.AlertType.ERROR,
                "Error Getting All Properties",
                "Error in getAllProperties",
                "There was an error running the SQL information to get all properties from the database."
            );
            e.printStackTrace();
        }
        return null;
    }
    
    public static int getStage (int gameID, int propertyID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return -1;
            
            String sql = "select stage from owned where game_id = ? and property_id = ? limit 1;";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, gameID);
            prepared.setInt(2, propertyID);
            ResultSet result = prepared.executeQuery();
            
            if(result.next()) return result.getInt(1);
            else return 0;
        } catch (Exception e) {
            Utils.normalAlert(
                Alert.AlertType.ERROR,
                "Error Getting Stage",
                "Error in getStage",
                "There was an error running the SQL information to get the stage from the database."
            );
            e.printStackTrace();
        }
        return -1;
    }
    
    public static int getTurn(int gameID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return -1;
            
            String sql = "select turn from game where game_id = ?;";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, gameID);
            ResultSet result = prepared.executeQuery();
            
            if(result.next())
                return result.getInt(1);
        } catch (Exception e) {
            Utils.normalAlert(
                Alert.AlertType.ERROR,
                "Error Getting Turn",
                "Error In getTurn",
                "There was an error running the SQL information to get the turn from the database."
            );
            e.printStackTrace();
        }
        return -1;
    }
    
    private static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/monopoly", "root", "password");
        } catch (Exception e) {
            Utils.normalAlert(
                Alert.AlertType.ERROR,
                "Connection Error",
                "Error Connecting To Pharmacy Database",
                "Database could not be connected to, please try again."
            );
            e.printStackTrace();
        }
        return null;
    }
    // endregion
}