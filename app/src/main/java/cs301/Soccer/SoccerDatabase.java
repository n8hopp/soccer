package cs301.Soccer;

import android.util.JsonWriter;
import android.util.Log;
import cs301.Soccer.soccerPlayer.SoccerPlayer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Soccer player database -- presently, all dummied up
 *
 * @author *** put your name here ***
 * @version *** put date of completion here ***
 *
 */
public class SoccerDatabase implements SoccerDB {

    // dummied up variable; you will need to change this
    private final Hashtable<String, SoccerPlayer> database = new Hashtable<String, SoccerPlayer>();

    /**
     * add a player
     *
     * @see SoccerDB#addPlayer(String, String, int, String)
     */
    @Override
    public boolean addPlayer(String firstName, String lastName,
                             int uniformNumber, String teamName)
    {
        if(getPlayer(firstName, lastName) == null)
        {
            String fullName = firstName + "&" + lastName;
            SoccerPlayer newPlayer = new SoccerPlayer(firstName, lastName, uniformNumber, teamName);
            database.put(fullName, newPlayer);
            return true;
        }
        return false;
    }

    /**
     * remove a player
     *
     * @see SoccerDB#removePlayer(String, String)
     */
    @Override
    public boolean removePlayer(String firstName, String lastName)
    {
        if(getPlayer(firstName, lastName) == null) return false;
        String fullName = firstName + "&" + lastName;
        database.remove(fullName);
        return true;
    }

    /**
     * look up a player
     *
     * @see SoccerDB#getPlayer(String, String)
     */
    @Override
    public SoccerPlayer getPlayer(String firstName, String lastName) {
        String fullName = firstName + "&" + lastName;
        SoccerPlayer foundPlayer;
        if((foundPlayer = database.get(fullName)) != null)
        {
            return foundPlayer;
        }
        else return null;
    }

    /**
     * increment a player's goals
     *
     * @see SoccerDB#bumpGoals(String, String)
     */
    @Override
    public boolean bumpGoals(String firstName, String lastName)
    {
        SoccerPlayer foundPlayer = getPlayer(firstName, lastName);
        if (foundPlayer != null)
        {
            foundPlayer.bumpGoals();
            return true;
        }
        return false;
    }

    /**
     * increment a player's yellow cards
     *
     * @see SoccerDB#bumpYellowCards(String, String)
     */
    @Override
    public boolean bumpYellowCards(String firstName, String lastName)
    {
        SoccerPlayer foundPlayer = getPlayer(firstName, lastName);
        if (foundPlayer != null)
        {
            foundPlayer.bumpYellowCards();
            return true;
        }
        return false;
    }

    /**
     * increment a player's red cards
     *
     * @see SoccerDB#bumpRedCards(String, String)
     */
    @Override
    public boolean bumpRedCards(String firstName, String lastName)
    {
        SoccerPlayer foundPlayer = getPlayer(firstName, lastName);
        if (foundPlayer != null)
        {
            foundPlayer.bumpRedCards();
            return true;
        }
        return false;
    }

    /**
     * tells the number of players on a given team
     *
     * @see SoccerDB#numPlayers(String)
     */
    @Override
    // report number of players on a given team (or all players, if null)
    public int numPlayers(String teamName)
    {
        int numPlayers = 0;

        Enumeration<String> e = database.keys();

        while(e.hasMoreElements())
        {
            String key = e.nextElement();
            SoccerPlayer currentPlayer = database.get(key);
            if(teamName == null)
            {
                numPlayers++;
            }
            else
            {
                if (currentPlayer.getTeamName().equals(teamName))
                {
                    numPlayers++;
                }
            }
        }
        return numPlayers;
    }

    /**
     * gives the nth player on a the given team
     *
     * @see SoccerDB#playerIndex(int, String)
     */
    // get the nTH player
    @Override
    public SoccerPlayer playerIndex(int idx, String teamName)
    {
        int numPlayers = 0;

        Enumeration<String> e = database.keys();
        while(e.hasMoreElements())
        {
            String key = e.nextElement();
            SoccerPlayer currentPlayer = database.get(key);
            if(teamName != null)
            {
                String currentTeam = currentPlayer.getTeamName();
                int i = 0;
                int j = 0;
                if(currentTeam.equals(teamName))
                {
                    if (numPlayers == idx)
                    {
                        return currentPlayer;
                    }
                    numPlayers++;
                }

            }
            else {
                if (numPlayers == idx) {
                    return currentPlayer;
                }
                numPlayers++;
            }

        }
        return null;
    }

    /**
     * reads database data from a file
     *
     * @see SoccerDB#readData(java.io.File)
     */
    // read data from file
    @Override
    public boolean readData(File file) {
        if (file.exists()) {
            Scanner fileScanner = null;
            try {
                fileScanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String nameReg = "\\[Name: (.*?) (.*?)]" +
                                 "\\[Team: (.*?)]" +
                                 "\\[Number: (.*?)]" +
                                 "\\[Goals: (.*?)]" +
                                 "\\[YellowCards: (.*?)]" +
                                 "\\[RedCards: (.*?)]";
                Pattern p = Pattern.compile(nameReg);
                Matcher m = p.matcher(line);
                if(m.find())
                {
                    String firstName    = m.group(1);
                    String lastName     = m.group(2);
                    String team         = m.group(3);
                    int uniform      = Integer.parseInt(m.group(4));
                    int goals        = Integer.parseInt(m.group(5));
                    int yellowCards  = Integer.parseInt(m.group(6));
                    int redCards     = Integer.parseInt(m.group(7));

                    SoccerPlayer foundPlayer = getPlayer(firstName, lastName);
                    if(foundPlayer != null)
                    {
                        removePlayer(firstName, lastName); // Delete player if already exists to wipe existing data
                    }
                    else addPlayer(firstName, lastName, uniform, team);

                    for(int i = 0; i < goals; i++) bumpGoals(firstName,lastName);
                    for(int i = 0; i < yellowCards; i++) bumpYellowCards(firstName,lastName);
                    for(int i = 0; i < redCards; i++) bumpRedCards(firstName,lastName);
                }

            }
            return true;

        }
        else return false;
    }

    /**
     * write database data to a file
     *
     * @see SoccerDB#writeData(java.io.File)
     */
    // write data to file
    @Override
    public boolean writeData(File file) {
        try {
            if(file.createNewFile())
            {
                PrintWriter pw = new PrintWriter(file);

                Set<String> keys = database.keySet();
                for(String key : keys) {
                    SoccerPlayer currentPlayer = database.get(key);
                    String playerData = "[Name: "
                            + currentPlayer.getFirstName()
                            + " "
                            + currentPlayer.getLastName()
                            + "][Team: " + currentPlayer.getTeamName() + "]"
                            + "[Number: " + currentPlayer.getUniform() + "]"
                            + "[Goals: " + currentPlayer.getGoals() + "]"
                            + "[YellowCards: " + currentPlayer.getYellowCards() + "]"
                            + "[RedCards: " + currentPlayer.getRedCards() + "]";
                    pw.println(logString(playerData));
                }
                pw.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * helper method that logcat-logs a string, and then returns the string.
     * @param s the string to log
     * @return the string s, unchanged
     */
    private String logString(String s) {
        Log.i("write string", s);
        return s;
    }

    /**
     * returns the list of team names in the database
     *
     * @see cs301.Soccer.SoccerDB#getTeams()
     */
    // return list of teams
    @Override
    public HashSet<String> getTeams() {

        HashSet<String> returnHash = new HashSet<>();
        Set<String> keys = database.keySet();

        for(String key : keys) {
            SoccerPlayer currentPlayer = database.get(key);
            returnHash.add(currentPlayer.getTeamName());
        }

        return returnHash;
    }

    /**
     * Helper method to empty the database and the list of teams in the spinner;
     * this is faster than restarting the app
     */
    public boolean clear() {
        if(database != null) {
            database.clear();
            return true;
        }
        return false;
    }
}
