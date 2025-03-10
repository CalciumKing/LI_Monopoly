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
            String sql = "select * from game where gameID = ? and password = ?;";
            
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, gameID);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            
            return result.next();
        } catch (Exception e) {
            Utils.errorAlert(
                    Alert.AlertType.ERROR,
                    "Error In getMedicine",
                    "Error Getting Medicine From Database",
                    "There was an error getting medicine from the database, please try again."
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
                    getProperties(gameID, result.getInt(1))
                ));
            
            Player[] players = new Player[data.size()];
            for (int i = 0; i < data.size(); i++)
                players[i] = data.get(i);
            
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
    
    private static ArrayList<Property> getProperties(int gameID, int playerID) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from properties where property_id in (" +
                    "select property_id from owned where game_id = ? and player_id = ?);";
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setInt(1, gameID);
            prepared.setInt(2, playerID);
            
            ObservableList<Property> data = FXCollections.observableArrayList();
            ResultSet result = prepared.executeQuery();
            
            while(result.next())
                data.add(new Property(
                        result.getString(2),
                        result.getInt(1),
                        result.getInt(3),
                        result.getInt(10),
                        result.getInt(5),
                        result.getInt(6),
                        result.getInt(7),
                        result.getInt(8),
                        result.getInt(9),
                        result.getString(4),
                        //stage
                ));
            
            return (ArrayList<Property>) data;
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
    
    private static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/pharmacy", "root", "password");
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