package com.example.monopoly_li;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

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
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Error In gameExists",
                    "Error Getting Game From Database",
                    "That game does not exist or a different error occurred."
            );
            e.printStackTrace();
        }
        return false;
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
                    result.getInt(4),
                    getOwnedProperties(gameID, result.getInt(1))
                ));
            
            Player[] players = new Player[data.size()];
            for (int i = 0; i < data.size(); i++) {
                Player current = data.get(i);
                for(Property property : current.getOwned())
                    property.setOwner(current);
                players[i] = current;
            }
            
            return players;
        } catch (Exception e) {
            Utils.errorAlert(
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
                        new int[]{result.getInt("house0"),
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
            Utils.errorAlert(
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
                if(!result.getString("color").equals("special")) {
                    data[i] = new Property(
                            result.getString("property_name"),
                            result.getInt("property_id"),
                            result.getInt("price"),
                            new int[]{result.getInt("house0"),
                                    result.getInt("house1"),
                                    result.getInt("house2"),
                                    result.getInt("house3"),
                                    result.getInt("house4"),
                                    result.getInt("hotel")
                            },
                            result.getString("color"),
                            getStage(gameID, result.getInt("property_id"))
                    );
                } else {
                    // add action property (not implemented yet)
                }
                i++;
            }
            
            return data;
        } catch (Exception e) {
            Utils.errorAlert(
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
            
            if(result.next())
                return result.getInt(1);
            else
                Utils.errorAlert(
                        Alert.AlertType.ERROR,
                        "Error Getting Stage",
                        "No Property",
                        "That property does not exist, no stage found."
                );
        } catch (Exception e) {
            Utils.errorAlert(
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
            Utils.errorAlert(
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
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Connection Error",
                    "Error Connecting To Pharmacy Database",
                    "Database could not be connected to, please try again."
            );
            e.printStackTrace();
        }
        return null;
    }
}