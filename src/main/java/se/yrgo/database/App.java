package se.yrgo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This is a really simple example of parsing a command line and
 * connecting to a ms sql server instance with jdbc.
 * 
 * It is intended as a basic illustration only and is not coded very nicely
 * by design. The students should make sure their code is very nicely
 * coded, not me.
 * 
 */
public class App {
    // This is not really a good way to handle passwords
    private static final String USER = "sa";
    private static final String PASSWORD = "someP4ssword";
    private static final String DB = "test_db";

    public static void main(String[] args) {
        try {
            CommandLine cmd = parseOptions(args);
            if (cmd.hasOption("a")) {
                System.out.println("Option a");
            }
            
            if (cmd.hasOption("c")) {
                System.out.println("Option b");
            }

            if (cmd.hasOption("c")) {
                System.out.println("Option c with arg: " + cmd.getOptionValue("c"));
            }
        }
        catch (ParseException ex) {
            printHelp();
            System.exit(-1);
        }

        connectAndRun();
    }

    private static void connectAndRun() {
        final String connectionUrl = String.format("jdbc:sqlserver://localhost:1433;database=%s;" +
        "encrypt=false;trustServerCertificate=false;loginTimeout=30;", DB);

        try (Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD)) {
            randomInsert(connection);
            performAQuery(connection);
        }
        catch( SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption(new Option("a", "alogflag", false, "Turn on option a."));
        options.addOption(new Option("b", "bigflag", false, "Turn on option b."));
        options.addOption(new Option("c", "collossalfal", true, "Turn on option c with argument."));
        return options;
    }

    private static CommandLine parseOptions(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(getOptions(), args);
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("inlamning3", getOptions());
    }

    private static void randomInsert(Connection connection) {
        final String insertSql = "INSERT INTO example_table (val) VALUES (?)";

        int random = ThreadLocalRandom.current().nextInt(10000);

        try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (int i = 0; i < 30; ++i) {
                statement.setString(1, Integer.toString(i*random, 16));
                statement.executeUpdate();
            }
        }
        catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static void performAQuery(Connection connection) {
        // We should always use prepared statements if we have
        // any variable data in the statement
        try (Statement statement = connection.createStatement()) {
            // Create and execute a SELECT SQL statement.
            final String selectSql = "SELECT TOP(10) id, val FROM example_table ORDER BY val DESC";
            
            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                // Print results from select statement
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String val = resultSet.getString("val");
                    System.out.printf("id=%d val=%s%n", id, val);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}
