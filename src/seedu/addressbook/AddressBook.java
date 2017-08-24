package seedu.addressbook;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

/**
 * This class is used to maintain a list of person data which are saved
 * in a text file.
 **/
public class AddressBook {


    /* We use a String array to store details of a single person.
     * The constants given below are the indexes for the different data elements of a person
     * used by the internal String[] storage format.
     * For example, a person's name is stored as the 0th element in the array.
     */
    private static final int PERSON_DATA_INDEX_NAME = 0;
    private static final int PERSON_DATA_INDEX_PHONE = 1;
    private static final int PERSON_DATA_INDEX_EMAIL = 2;

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final ArrayList<String[]> ALL_PERSONS = new ArrayList<>();
    private static ArrayList<String[]> latestPersonListingView = ALL_PERSONS; // initial view is of all
    private static String storageFilePath;


    public static void main(String[] args) {
        for (String m : new String[]{"===================================================",
                "===================================================",
                "AddessBook Level 1 - Version 1.0", "Welcome to your Address Book!",
                "==================================================="}) {
            System.out.println("|| " + m);
        }
        if (args.length >= 2) {
            for (String m2 : new String[]{"Too many parameters! Correct program argument format:"
                    + (System.lineSeparator() + "|| ") + "\tjava AddressBook"
                    + (System.lineSeparator() + "|| ") + "\tjava AddressBook [custom storage file path]"}) {
                System.out.println("|| " + m2);
            }
            for (String m : new String[]{"Exiting Address Book... Good bye!",
                    "===================================================",
                    "==================================================="}) {
                System.out.println("|| " + m);
            }
            System.exit(0);
        }

        if (args.length == 1) {

            boolean isValidFilePath = false;
            if (args[0] != null) {


                Path filePathToValidate;
                try {
                    filePathToValidate = Paths.get(args[0]);
                Path parentDirectory = filePathToValidate.getParent();
                isValidFilePath = (parentDirectory == null || Files.isDirectory(parentDirectory)) && filePathToValidate.getFileName().toString().lastIndexOf('.') > 0
                        && (!Files.exists(filePathToValidate) || Files.isRegularFile(filePathToValidate));
                } catch (InvalidPathException ipe) {

                }
            }
            if (!isValidFilePath) {
                for (String m : new String[]{String.format("The given file name [%1$s] is not a valid file name!", args[0])}) {
                    System.out.println("|| " + m);
                }
                for (String m : new String[]{"Exiting Address Book... Good bye!",
                        "===================================================",
                        "==================================================="}) {
                    System.out.println("|| " + m);
                }
                System.exit(0);
            }

            storageFilePath = args[0];
            final File storageFile = new File(args[0]);
            if (!storageFile.exists()) {

                for (String m1 : new String[]{String.format("Storage file missing: %1$s", args[0])}) {
                    System.out.println("|| " + m1);
                }

                try {
                    storageFile.createNewFile();
                    for (String m : new String[]{String.format("Created new empty storage file: %1$s", args[0])}) {
                        System.out.println("|| " + m);
                    }
                } catch (IOException ioe) {
                    for (String m : new String[]{String.format("Error: unable to create file: %1$s", args[0])}) {
                        System.out.println("|| " + m);
                    }
                    for (String m : new String[]{"Exiting Address Book... Good bye!",
                            "===================================================",
                            "==================================================="}) {
                        System.out.println("|| " + m);
                    }
                    System.exit(0);
                }
            }
        }

        if(args.length == 0) {
            for (String m : new String[]{"Using default storage file : " + "addressbook.txt"}) {
                System.out.println("|| " + m);
            }
            storageFilePath = "addressbook.txt";
            final File storageFile = new File(storageFilePath);
            if (!storageFile.exists()) {
                for (String m1 : new String[]{String.format("Storage file missing: %1$s", storageFilePath)}) {
                    System.out.println("|| " + m1);
                }

                try {
                    storageFile.createNewFile();
                    for (String m : new String[]{String.format("Created new empty storage file: %1$s", storageFilePath)}) {
                        System.out.println("|| " + m);
                    }
                } catch (IOException ioe) {
                    for (String m : new String[]{String.format("Error: unable to create file: %1$s", storageFilePath)}) {
                        System.out.println("|| " + m);
                    }
                    for (String m : new String[]{"Exiting Address Book... Good bye!",
                            "===================================================",
                            "==================================================="}) {
                        System.out.println("|| " + m);
                    }
                    System.exit(0);
                }
            }
        }
        ArrayList<String> lines = null;
        try {
            lines = new ArrayList<>(Files.readAllLines(Paths.get(storageFilePath)));
        } catch (FileNotFoundException fnfe) {
            for (String m11 : new String[]{String.format("Storage file missing: %1$s", storageFilePath)}) {
                System.out.println("|| " + m11);
            }
            for (String m2 : new String[]{"Exiting Address Book... Good bye!",
                    "===================================================",
                    "==================================================="}) {
                System.out.println("|| " + m2);
            }
            System.exit(0);
        } catch (IOException ioe) {
            for (String m11 : new String[]{String.format("Unexpected error: unable to read from file: %1$s", storageFilePath)}) {
                System.out.println("|| " + m11);
            }
            for (String m2 : new String[]{"Exiting Address Book... Good bye!",
                    "===================================================",
                    "==================================================="}) {
                System.out.println("|| " + m2);
            }
            System.exit(0);
        }
        final ArrayList<String[]> decodedPersons = new ArrayList<>();
        Optional<ArrayList<String[]>> returnValue = Optional.empty();;
        boolean isDecodingSuccess = true;
        for (String encodedPerson : lines) {
            Optional<String[]> decodedPersons1 = Optional.empty();
            boolean isPersonDataPresent = true;
            // check that we can extract the parts of a person from the encoded string
            final String matchAnyPersonDataPrefix = "p/" + '|' + "e/";
            final String[] splitArgs = encodedPerson.trim().split(matchAnyPersonDataPrefix);
            if (!(splitArgs.length == 3 // 3 arguments
                    && !splitArgs[0].isEmpty() // non-empty arguments
                    && !splitArgs[1].isEmpty()
                    && !splitArgs[2].isEmpty())) {
                isPersonDataPresent = false;
            }
            if(isPersonDataPresent) {
                String result;
                final int indexOfPhonePrefix = encodedPerson.indexOf("p/");
                final int indexOfEmailPrefix = encodedPerson.indexOf("e/");

                // email is last arg, target is from prefix to end of string
                if (indexOfEmailPrefix > indexOfPhonePrefix) {
                    result = encodedPerson.substring(indexOfEmailPrefix, encodedPerson.length()).trim().replace("e/", "");

                    // email is middle arg, target is from own prefix to next prefix
                } else {
                    result = encodedPerson.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim().replace("e/", "");
                }
                String result1;
                final int indexOfPhonePrefix1 = encodedPerson.indexOf("p/");
                final int indexOfEmailPrefix1 = encodedPerson.indexOf("e/");

                // phone is last arg, target is from prefix to end of string
                if (indexOfPhonePrefix1 > indexOfEmailPrefix1) {
                    result1 = encodedPerson.substring(indexOfPhonePrefix1, encodedPerson.length()).trim().replace("p/", "");

                    // phone is middle arg, target is from own prefix to next prefix
                } else {
                    result1 = encodedPerson.substring(indexOfPhonePrefix1, indexOfEmailPrefix1).trim().replace("p/", "");
                }
                final int indexOfPhonePrefix2 = encodedPerson.indexOf("p/");
                final int indexOfEmailPrefix2 = encodedPerson.indexOf("e/");
                // name is leading substring up to first data prefix symbol
                int indexOfFirstPrefix = Math.min(indexOfEmailPrefix2, indexOfPhonePrefix2);
                final String[] person = new String[3];
                person[PERSON_DATA_INDEX_NAME] = encodedPerson.substring(0, indexOfFirstPrefix).trim();
                person[PERSON_DATA_INDEX_PHONE] = result1;
                person[PERSON_DATA_INDEX_EMAIL] = result;
                final String[] decodedPerson1 = person;
                // check that the constructed person is valid
                //TODO: implement a more permissive validation
                //TODO: implement a more permissive validation
                //TODO: implement a more permissive validation
                decodedPersons1 =  decodedPerson1[PERSON_DATA_INDEX_NAME].matches("(\\w|\\s)+")
                        && decodedPerson1[PERSON_DATA_INDEX_PHONE].matches("\\d+")
                        && decodedPerson1[PERSON_DATA_INDEX_EMAIL].matches("\\S+@\\S+\\.\\S+") ? Optional.of(decodedPerson1) : Optional.empty();
            }
            final Optional<String[]> decodedPerson = decodedPersons1;
            if (!decodedPerson.isPresent()) {
                isDecodingSuccess = false;
                break;
            }
            decodedPersons.add(decodedPerson.get());
        }
        if(isDecodingSuccess) {
            returnValue = Optional.of(decodedPersons);
        }
        final Optional<ArrayList<String[]>> successfullyDecoded = returnValue;
        if (!successfullyDecoded.isPresent()) {
            for (String m2 : new String[]{"Storage file has invalid content"}) {
                System.out.println("|| " + m2);
            }
            for (String m2 : new String[]{"Exiting Address Book... Good bye!",
                    "===================================================",
                    "==================================================="}) {
                System.out.println("|| " + m2);
            }
            System.exit(0);
        }
        ALL_PERSONS.clear();
        ALL_PERSONS.addAll(successfullyDecoded.get());
        while (true) {
            System.out.print("|| " + "Enter command: ");
            String inputLine = SCANNER.nextLine();
            // silently consume all blank and comment lines
            while (inputLine.trim().isEmpty() || inputLine.trim().charAt(0) == '#') {
                inputLine = SCANNER.nextLine();
            }
            String userCommand = inputLine;
            for (String m1 : new String[]{"[Command entered:" + userCommand + "]"}) {
                System.out.println("|| " + m1);
            }
            String feedback = executeCommand(userCommand);
            for (String m : new String[]{feedback, "==================================================="}) {
                System.out.println("|| " + m);
            }
        }
    }


    /**
     * Executes the command as specified by the {@code userInputString}
     *
     * @param userInputString  raw input from user
     * @return  feedback about how the command was executed
     */
    private static String executeCommand(String userInputString) {
        final String[] split =  userInputString.trim().split("\\s+", 2);
        final String[] commandTypeAndParams = split.length == 2 ? split : new String[]{split[0], ""};
        final String commandType = commandTypeAndParams[0];
        final String commandArgs = commandTypeAndParams[1];
        switch (commandType) {
        case "add":
            // try decoding a person from the raw args
            Optional<String[]> decodedPersons = Optional.empty();
            boolean isPersonDataPresent = true;
            // check that we can extract the parts of a person from the encoded string
            final String matchAnyPersonDataPrefix = "p/" + '|' + "e/";
            final String[] splitArgs = commandArgs.trim().split(matchAnyPersonDataPrefix);
            if (!(splitArgs.length == 3 // 3 arguments
                    && !splitArgs[0].isEmpty() // non-empty arguments
                    && !splitArgs[1].isEmpty()
                    && !splitArgs[2].isEmpty())) {
                isPersonDataPresent = false;
            }
            if(isPersonDataPresent) {
                String result11;
                final int indexOfPhonePrefix = commandArgs.indexOf("p/");
                final int indexOfEmailPrefix = commandArgs.indexOf("e/");

                // email is last arg, target is from prefix to end of string
                if (indexOfEmailPrefix > indexOfPhonePrefix) {
                    result11 = commandArgs.substring(indexOfEmailPrefix, commandArgs.length()).trim().replace("e/", "");

                    // email is middle arg, target is from own prefix to next prefix
                } else {
                    result11 = commandArgs.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim().replace("e/", "");
                }
                String result1;
                final int indexOfPhonePrefix1 = commandArgs.indexOf("p/");
                final int indexOfEmailPrefix1 = commandArgs.indexOf("e/");

                // phone is last arg, target is from prefix to end of string
                if (indexOfPhonePrefix1 > indexOfEmailPrefix1) {
                    result1 = commandArgs.substring(indexOfPhonePrefix1, commandArgs.length()).trim().replace("p/", "");

                    // phone is middle arg, target is from own prefix to next prefix
                } else {
                    result1 = commandArgs.substring(indexOfPhonePrefix1, indexOfEmailPrefix1).trim().replace("p/", "");
                }
                final int indexOfPhonePrefix2 = commandArgs.indexOf("p/");
                final int indexOfEmailPrefix2 = commandArgs.indexOf("e/");
                // name is leading substring up to first data prefix symbol
                int indexOfFirstPrefix = Math.min(indexOfEmailPrefix2, indexOfPhonePrefix2);
                final String[] person3 = new String[3];
                person3[PERSON_DATA_INDEX_NAME] = commandArgs.substring(0, indexOfFirstPrefix).trim();
                person3[PERSON_DATA_INDEX_PHONE] = result1;
                person3[PERSON_DATA_INDEX_EMAIL] = result11;
                final String[] decodedPerson = person3;
                // check that the constructed person is valid
                //TODO: implement a more permissive validation
                //TODO: implement a more permissive validation
                //TODO: implement a more permissive validation
                decodedPersons =  decodedPerson[PERSON_DATA_INDEX_NAME].matches("(\\w|\\s)+")
                        && decodedPerson[PERSON_DATA_INDEX_PHONE].matches("\\d+")
                        && decodedPerson[PERSON_DATA_INDEX_EMAIL].matches("\\S+@\\S+\\.\\S+") ? Optional.of(decodedPerson) : Optional.empty();
            }
            final Optional<String[]> decodeResult = decodedPersons;

            // checks if args are valid (decode result will not be present if the person is invalid)
            if (!decodeResult.isPresent()) {
                return String.format("Invalid command format: %1$s " + (System.lineSeparator() + "|| ") + "%2$s", "add", String.format("%1$s: %2$s", "add", "Adds a person to the address book.") + (System.lineSeparator() + "|| ")
                            + String.format("\tParameters: %1$s", "NAME "
                                                                              + "p/" + "PHONE_NUMBER "
                                                                              + "e/" + "EMAIL") + (System.lineSeparator() + "|| ")
                            + String.format("\tExample: %1$s", "add" + " John Doe p/98765432 e/johnd@gmail.com") + (System.lineSeparator() + "|| "));
            }

            // add the person as specified
            final String[] personToAdd = decodeResult.get();
            ALL_PERSONS.add(personToAdd);
            final ArrayList<String> encoded1 = new ArrayList<>();
            for (String[] person1 : ALL_PERSONS) {
                encoded1.add(String.format("%1$s " // name
                                                                        + "p/" + "%2$s " // phone
                                                                        + "e/" + "%3$s",
                        person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]));
            }
            final ArrayList<String> linesToWrite1 = encoded1;
            try {
                Files.write(Paths.get(storageFilePath), linesToWrite1);
            } catch (IOException ioe1) {
                for (String m1 : new String[]{String.format("Unexpected error: unable to write to file: %1$s", storageFilePath)}) {
                    System.out.println("|| " + m1);
                }
                for (String m1 : new String[]{"Exiting Address Book... Good bye!",
                        "===================================================",
                        "==================================================="}) {
                    System.out.println("|| " + m1);
                }
                System.exit(0);
            }
            return String.format("New person added: %1$s, Phone: %2$s, Email: %3$s",
                    personToAdd[PERSON_DATA_INDEX_NAME], personToAdd[PERSON_DATA_INDEX_PHONE], personToAdd[PERSON_DATA_INDEX_EMAIL]);
            case "find":
                final Set<String> keywords = new HashSet<>(new ArrayList<>(Arrays.asList(commandArgs.trim().trim().split("\\s+"))));
                final ArrayList<String[]> matchedPersons = new ArrayList<>();
                for (String[] person2 : ALL_PERSONS) {
                    final Set<String> wordsInName = new HashSet<>(new ArrayList<>(Arrays.asList(person2[PERSON_DATA_INDEX_NAME].trim().split("\\s+"))));
                    if (!Collections.disjoint(wordsInName, keywords)) {
                        matchedPersons.add(person2);
                    }
                }
                final ArrayList<String[]> personsFound = matchedPersons;
                final StringBuilder messageAccumulator1 = new StringBuilder();
                for (int i1 = 0; i1 < personsFound.size(); i1++) {
                    final String[] person1 = personsFound.get(i1);
                    final int displayIndex1 = i1 + 1;
                    messageAccumulator1.append('\t')
                                      .append(String.format("%1$d. ", displayIndex1) + String.format("%1$s  Phone Number: %2$s  Email: %3$s",
                                              person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]))
                                      .append(System.lineSeparator() + "|| ");
                }
                String listAsString1 = messageAccumulator1.toString();
                for (String m1 : new String[]{listAsString1}) {
                    System.out.println("|| " + m1);
                }
                // clone to insulate from future changes to arg list
                latestPersonListingView = new ArrayList<>(personsFound);
                return String.format("%1$d persons found!", personsFound.size());
            case "list":
            ArrayList<String[]> toBeDisplayed = ALL_PERSONS;
            final StringBuilder messageAccumulator = new StringBuilder();
            for (int i = 0; i < toBeDisplayed.size(); i++) {
                final String[] person = toBeDisplayed.get(i);
                final int displayIndex = i + 1;
                messageAccumulator.append('\t')
                                  .append(String.format("%1$d. ", displayIndex) + String.format("%1$s  Phone Number: %2$s  Email: %3$s",
                                          person[PERSON_DATA_INDEX_NAME], person[PERSON_DATA_INDEX_PHONE], person[PERSON_DATA_INDEX_EMAIL]))
                                  .append(System.lineSeparator() + "|| ");
            }
            String listAsString = messageAccumulator.toString();
            for (String m : new String[]{listAsString}) {
                System.out.println("|| " + m);
            }
            // clone to insulate from future changes to arg list
            latestPersonListingView = new ArrayList<>(toBeDisplayed);
            return String.format("%1$d persons found!", toBeDisplayed.size());
            case "delete":
                boolean result;
                try {
                    final int extractedIndex = Integer.parseInt(commandArgs.trim()); // use standard libraries to parse
                    result = extractedIndex >= 1;
                } catch (NumberFormatException nfe) {
                    result = false;
                }
                if (!result) {
                    return String.format("Invalid command format: %1$s " + (System.lineSeparator() + "|| ") + "%2$s", "delete", String.format("%1$s: %2$s", "delete", "Deletes a person identified by the index number used in "
                                                                    + "the last find/list call.") + (System.lineSeparator() + "|| ")
                                + String.format("\tParameters: %1$s", "INDEX") + (System.lineSeparator() + "|| ")
                                + String.format("\tExample: %1$s", "delete" + " 1") + (System.lineSeparator() + "|| "));
                }
                final int targetVisibleIndex = Integer.parseInt(commandArgs.trim());
                if (!(targetVisibleIndex >= 1 && targetVisibleIndex < latestPersonListingView.size() + 1)) {
                    return "The person index provided is invalid";
                }
                final String[] targetInModel = latestPersonListingView.get(targetVisibleIndex - 1);
                final boolean changed = ALL_PERSONS.remove(targetInModel);
                if (changed) {
                    final ArrayList<String> encoded2 = new ArrayList<>();
                    for (String[] person1 : ALL_PERSONS) {
                        encoded2.add(String.format("%1$s " // name
                                                                                + "p/" + "%2$s " // phone
                                                                                + "e/" + "%3$s",
                                person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]));
                    }
                    final ArrayList<String> linesToWrite2 = encoded2;
                    try {
                        Files.write(Paths.get(storageFilePath), linesToWrite2);
                    } catch (IOException ioe1) {
                        for (String m1 : new String[]{String.format("Unexpected error: unable to write to file: %1$s", storageFilePath)}) {
                            System.out.println("|| " + m1);
                        }
                        for (String m1 : new String[]{"Exiting Address Book... Good bye!",
                                "===================================================",
                                "==================================================="}) {
                            System.out.println("|| " + m1);
                        }
                        System.exit(0);
                    }
                }
                return changed ? String.format("Deleted Person: %1$s", String.format("%1$s  Phone Number: %2$s  Email: %3$s",
                        targetInModel[PERSON_DATA_INDEX_NAME], targetInModel[PERSON_DATA_INDEX_PHONE], targetInModel[PERSON_DATA_INDEX_EMAIL])) // success
                                                                  : "Person could not be found in address book"; // not found
            case "clear":
            ALL_PERSONS.clear();
            final ArrayList<String> encoded = new ArrayList<>();
            for (String[] person : ALL_PERSONS) {
                encoded.add(String.format("%1$s " // name
                                                                        + "p/" + "%2$s " // phone
                                                                        + "e/" + "%3$s",
                        person[PERSON_DATA_INDEX_NAME], person[PERSON_DATA_INDEX_PHONE], person[PERSON_DATA_INDEX_EMAIL]));
            }
            final ArrayList<String> linesToWrite = encoded;
            try {
                Files.write(Paths.get(storageFilePath), linesToWrite);
            } catch (IOException ioe) {
                for (String m : new String[]{String.format("Unexpected error: unable to write to file: %1$s", storageFilePath)}) {
                    System.out.println("|| " + m);
                }
                for (String m : new String[]{"Exiting Address Book... Good bye!",
                        "===================================================",
                        "==================================================="}) {
                    System.out.println("|| " + m);
                }
                System.exit(0);
            }
            return "Address book has been cleared!";
            case "help":
            return (String.format("%1$s: %2$s", "add", "Adds a person to the address book.") + (System.lineSeparator() + "|| ")
                    + String.format("\tParameters: %1$s", "NAME "
                                                                      + "p/" + "PHONE_NUMBER "
                                                                      + "e/" + "EMAIL") + (System.lineSeparator() + "|| ")
                    + String.format("\tExample: %1$s", "add" + " John Doe p/98765432 e/johnd@gmail.com") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "find", "Finds all persons whose names contain any of the specified "
                                                        + "keywords (case-sensitive) and displays them as a list with index numbers.") + (System.lineSeparator() + "|| ")
                    + String.format("\tParameters: %1$s", "KEYWORD [MORE_KEYWORDS]") + (System.lineSeparator() + "|| ")
                    + String.format("\tExample: %1$s", "find" + " alice bob charlie") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "list", "Displays all persons as a list with index numbers.") + (System.lineSeparator() + "|| ")
                    + String.format("\tExample: %1$s", "list") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "delete", "Deletes a person identified by the index number used in "
                                                                    + "the last find/list call.") + (System.lineSeparator() + "|| ")
                    + String.format("\tParameters: %1$s", "INDEX") + (System.lineSeparator() + "|| ")
                    + String.format("\tExample: %1$s", "delete" + " 1") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "clear", "Clears address book permanently.") + (System.lineSeparator() + "|| ")
                    + String.format("\tExample: %1$s", "clear") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "exit", "Exits the program.")
                    + String.format("\tExample: %1$s", "exit")) + (System.lineSeparator() + "|| ")
                    + (String.format("%1$s: %2$s", "help", "Shows program usage instructions.")
                    + String.format("\tExample: %1$s", "help"));
            case "exit":
                for (String m : new String[]{"Exiting Address Book... Good bye!",
                        "===================================================",
                        "==================================================="}) {
                    System.out.println("|| " + m);
                }
                System.exit(0);
            default:
            return String.format("Invalid command format: %1$s " + (System.lineSeparator() + "|| ") + "%2$s", commandType, (String.format("%1$s: %2$s", "add", "Adds a person to the address book.") + (System.lineSeparator() + "|| ")
                        + String.format("\tParameters: %1$s", "NAME "
                                                                          + "p/" + "PHONE_NUMBER "
                                                                          + "e/" + "EMAIL") + (System.lineSeparator() + "|| ")
                        + String.format("\tExample: %1$s", "add" + " John Doe p/98765432 e/johnd@gmail.com") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "find", "Finds all persons whose names contain any of the specified "
                                                            + "keywords (case-sensitive) and displays them as a list with index numbers.") + (System.lineSeparator() + "|| ")
                        + String.format("\tParameters: %1$s", "KEYWORD [MORE_KEYWORDS]") + (System.lineSeparator() + "|| ")
                        + String.format("\tExample: %1$s", "find" + " alice bob charlie") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "list", "Displays all persons as a list with index numbers.") + (System.lineSeparator() + "|| ")
                        + String.format("\tExample: %1$s", "list") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "delete", "Deletes a person identified by the index number used in "
                                                                        + "the last find/list call.") + (System.lineSeparator() + "|| ")
                        + String.format("\tParameters: %1$s", "INDEX") + (System.lineSeparator() + "|| ")
                        + String.format("\tExample: %1$s", "delete" + " 1") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "clear", "Clears address book permanently.") + (System.lineSeparator() + "|| ")
                        + String.format("\tExample: %1$s", "clear") + (System.lineSeparator() + "|| ")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "exit", "Exits the program.")
                        + String.format("\tExample: %1$s", "exit")) + (System.lineSeparator() + "|| ")
                        + (String.format("%1$s: %2$s", "help", "Shows program usage instructions.")
                        + String.format("\tExample: %1$s", "help")));
        }
    }


}