package seedu.addressbook;

/*
 * NOTE : =============================================================
 * This class is written in a procedural fashion (i.e. not Object-Oriented)
 * Yes, it is possible to write non-OO code using an OO language.
 * ====================================================================
 */

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

/*
 * NOTE : =============================================================
 * This class header comment below is brief because details of how to
 * use this class are documented elsewhere.
 * ====================================================================
 */

/**
 * This class is used to maintain a list of person data which are saved
 * in a text file.
 **/
public class AddressBook {

    /**
     * Default file path used if the user doesn't provide the file name.
     */
    private static final String DEFAULT_STORAGE_FILEPATH = "addressbook.txt";

    /**
     * Version info of the program.
     */
    private static final String VERSION = "AddessBook Level 1 - Version 1.0";

    /**
     * A decorative prefix added to the beginning of lines printed by AddressBook
     */
    private static final String LINE_PREFIX = "|| ";

    /**
     * A platform independent line separator.
     */
    private static final String LS = System.lineSeparator() + LINE_PREFIX;

    /*
     * NOTE : ==================================================================
     * These messages shown to the user are defined in one place for convenient
     * editing and proof reading. Such messages are considered part of the UI
     * and may be subjected to review by UI experts or technical writers. Note
     * that Some of the strings below include '%1$s' etc to mark the locations
     * at which java String.format(...) method can insert values.
     * =========================================================================
     */
    private static final String MESSAGE_ADDED = "New person added: %1$s, Phone: %2$s, Email: %3$s";
    private static final String MESSAGE_ADDRESSBOOK_CLEARED = "Address book has been cleared!";
    private static final String MESSAGE_COMMAND_HELP = "%1$s: %2$s";
    private static final String MESSAGE_COMMAND_HELP_PARAMETERS = "\tParameters: %1$s";
    private static final String MESSAGE_COMMAND_HELP_EXAMPLE = "\tExample: %1$s";
    private static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    private static final String MESSAGE_DISPLAY_PERSON_DATA = "%1$s  Phone Number: %2$s  Email: %3$s";
    private static final String MESSAGE_DISPLAY_LIST_ELEMENT_INDEX = "%1$d. ";
    private static final String MESSAGE_GOODBYE = "Exiting Address Book... Good bye!";
    private static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format: %1$s " + LS + "%2$s";
    private static final String MESSAGE_INVALID_FILE = "The given file name [%1$s] is not a valid file name!";
    private static final String MESSAGE_INVALID_PROGRAM_ARGS = "Too many parameters! Correct program argument format:"
                                                            + LS + "\tjava AddressBook"
                                                            + LS + "\tjava AddressBook [custom storage file path]";
    private static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    private static final String MESSAGE_INVALID_STORAGE_FILE_CONTENT = "Storage file has invalid content";
    private static final String MESSAGE_PERSON_NOT_IN_ADDRESSBOOK = "Person could not be found in address book";
    private static final String MESSAGE_ERROR_CREATING_STORAGE_FILE = "Error: unable to create file: %1$s";
    private static final String MESSAGE_ERROR_MISSING_STORAGE_FILE = "Storage file missing: %1$s";
    private static final String MESSAGE_ERROR_READING_FROM_FILE = "Unexpected error: unable to read from file: %1$s";
    private static final String MESSAGE_ERROR_WRITING_TO_FILE = "Unexpected error: unable to write to file: %1$s";
    private static final String MESSAGE_PERSONS_FOUND_OVERVIEW = "%1$d persons found!";
    private static final String MESSAGE_STORAGE_FILE_CREATED = "Created new empty storage file: %1$s";
    private static final String MESSAGE_WELCOME = "Welcome to your Address Book!";
    private static final String MESSAGE_USING_DEFAULT_FILE = "Using default storage file : " + DEFAULT_STORAGE_FILEPATH;

    // These are the prefix strings to define the data type of a command parameter
    private static final String PERSON_DATA_PREFIX_PHONE = "p/";
    private static final String PERSON_DATA_PREFIX_EMAIL = "e/";

    private static final String PERSON_STRING_REPRESENTATION = "%1$s " // name
                                                            + PERSON_DATA_PREFIX_PHONE + "%2$s " // phone
                                                            + PERSON_DATA_PREFIX_EMAIL + "%3$s"; // email
    private static final String COMMAND_ADD_WORD = "add";
    private static final String COMMAND_ADD_DESC = "Adds a person to the address book.";
    private static final String COMMAND_ADD_PARAMETERS = "NAME "
                                                      + PERSON_DATA_PREFIX_PHONE + "PHONE_NUMBER "
                                                      + PERSON_DATA_PREFIX_EMAIL + "EMAIL";
    private static final String COMMAND_ADD_EXAMPLE = COMMAND_ADD_WORD + " John Doe p/98765432 e/johnd@gmail.com";

    private static final String COMMAND_FIND_WORD = "find";
    private static final String COMMAND_FIND_DESC = "Finds all persons whose names contain any of the specified "
                                        + "keywords (case-sensitive) and displays them as a list with index numbers.";
    private static final String COMMAND_FIND_PARAMETERS = "KEYWORD [MORE_KEYWORDS]";
    private static final String COMMAND_FIND_EXAMPLE = COMMAND_FIND_WORD + " alice bob charlie";

    private static final String COMMAND_LIST_WORD = "list";
    private static final String COMMAND_LIST_DESC = "Displays all persons as a list with index numbers.";
    private static final String COMMAND_LIST_EXAMPLE = COMMAND_LIST_WORD;

    private static final String COMMAND_DELETE_WORD = "delete";
    private static final String COMMAND_DELETE_DESC = "Deletes a person identified by the index number used in "
                                                    + "the last find/list call.";
    private static final String COMMAND_DELETE_PARAMETER = "INDEX";
    private static final String COMMAND_DELETE_EXAMPLE = COMMAND_DELETE_WORD + " 1";

    private static final String COMMAND_CLEAR_WORD = "clear";
    private static final String COMMAND_CLEAR_DESC = "Clears address book permanently.";
    private static final String COMMAND_CLEAR_EXAMPLE = COMMAND_CLEAR_WORD;

    private static final String COMMAND_HELP_WORD = "help";
    private static final String COMMAND_HELP_DESC = "Shows program usage instructions.";
    private static final String COMMAND_HELP_EXAMPLE = COMMAND_HELP_WORD;

    private static final String COMMAND_EXIT_WORD = "exit";
    private static final String COMMAND_EXIT_DESC = "Exits the program.";
    private static final String COMMAND_EXIT_EXAMPLE = COMMAND_EXIT_WORD;

    private static final String DIVIDER = "===================================================";


    /* We use a String array to store details of a single person.
     * The constants given below are the indexes for the different data elements of a person
     * used by the internal String[] storage format.
     * For example, a person's name is stored as the 0th element in the array.
     */
    private static final int PERSON_DATA_INDEX_NAME = 0;
    private static final int PERSON_DATA_INDEX_PHONE = 1;
    private static final int PERSON_DATA_INDEX_EMAIL = 2;

    /**
     * The number of data elements for a single person.
     */
    private static final int PERSON_DATA_COUNT = 3;

    /**
     * Offset required to convert between 1-indexing and 0-indexing.COMMAND_
     */
    private static final int DISPLAYED_INDEX_OFFSET = 1;

    /**
     * If the first non-whitespace character in a user's input line is this, that line will be ignored.
     */
    private static final char INPUT_COMMENT_MARKER = '#';

    /*
     * This variable is declared for the whole class (instead of declaring it
     * inside the readUserCommand() method to facilitate automated testing using
     * the I/O redirection technique. If not, only the first line of the input
     * text file will be processed.
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /*
     * NOTE : =============================================================================================
     * Note that the type of the variable below can also be declared as List<String[]>, as follows:
     *    private static final List<String[]> ALL_PERSONS = new ArrayList<>()
     * That is because List is an interface implemented by the ArrayList class.
     * In this code we use ArrayList instead because we wanted to to stay away from advanced concepts
     * such as interface inheritance.
     * ====================================================================================================
     */

    /**
     * List of all persons in the address book.
     */
    private static final ArrayList<String[]> ALL_PERSONS = new ArrayList<>();

    /**
     * Stores the most recent list of persons shown to the user as a result of a user command.
     * This is a subset of the full list. Deleting persons in the pull list does not delete
     * those persons from this list.
     */
    private static ArrayList<String[]> latestPersonListingView = ALL_PERSONS; // initial view is of all


    /**
     * The path to the file used for storing person data.
     */
    private static String storageFilePath;

    /*
     * NOTE : =============================================================
     * Notice how this method solves the whole problem at a very high level.
     * We can understand the high-level logic of the program by reading this
     * method alone.
     * If the reader wants a deeper understanding of the solution, she can go
     * to the next level of abstraction by reading the methods that are
     * referenced by the high-level method below.
     * ====================================================================
     */

    public static void main(String[] args) {
        for (String m : new String[]{DIVIDER, DIVIDER, VERSION, MESSAGE_WELCOME, DIVIDER}) {
            System.out.println(LINE_PREFIX + m);
        }
        if (args.length >= 2) {
            for (String m2 : new String[]{MESSAGE_INVALID_PROGRAM_ARGS}) {
                System.out.println(LINE_PREFIX + m2);
            }
            for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m);
            }
            System.exit(0);
        }

        if (args.length == 1) {
            setupGivenFileForStorage(args[0]);
        }

        if(args.length == 0) {
            setupDefaultFileForStorage();
        }
        ArrayList<String> lines = null;
        try {
            lines = new ArrayList<>(Files.readAllLines(Paths.get(storageFilePath)));
        } catch (FileNotFoundException fnfe) {
            for (String m11 : new String[]{String.format(MESSAGE_ERROR_MISSING_STORAGE_FILE, storageFilePath)}) {
                System.out.println(LINE_PREFIX + m11);
            }
            for (String m2 : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m2);
            }
            System.exit(0);
        } catch (IOException ioe) {
            for (String m11 : new String[]{String.format(MESSAGE_ERROR_READING_FROM_FILE, storageFilePath)}) {
                System.out.println(LINE_PREFIX + m11);
            }
            for (String m2 : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m2);
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
            final String matchAnyPersonDataPrefix = PERSON_DATA_PREFIX_PHONE + '|' + PERSON_DATA_PREFIX_EMAIL;
            final String[] splitArgs = encodedPerson.trim().split(matchAnyPersonDataPrefix);
            if (!(splitArgs.length == 3 // 3 arguments
                    && !splitArgs[0].isEmpty() // non-empty arguments
                    && !splitArgs[1].isEmpty()
                    && !splitArgs[2].isEmpty())) {
                isPersonDataPresent = false;
            }
            if(isPersonDataPresent) {
                String result;
                final int indexOfPhonePrefix = encodedPerson.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix = encodedPerson.indexOf(PERSON_DATA_PREFIX_EMAIL);

                // email is last arg, target is from prefix to end of string
                if (indexOfEmailPrefix > indexOfPhonePrefix) {
                    result = encodedPerson.substring(indexOfEmailPrefix, encodedPerson.length()).trim().replace(PERSON_DATA_PREFIX_EMAIL, "");

                    // email is middle arg, target is from own prefix to next prefix
                } else {
                    result = encodedPerson.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim().replace(PERSON_DATA_PREFIX_EMAIL, "");
                }
                String result1;
                final int indexOfPhonePrefix1 = encodedPerson.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix1 = encodedPerson.indexOf(PERSON_DATA_PREFIX_EMAIL);

                // phone is last arg, target is from prefix to end of string
                if (indexOfPhonePrefix1 > indexOfEmailPrefix1) {
                    result1 = encodedPerson.substring(indexOfPhonePrefix1, encodedPerson.length()).trim().replace(PERSON_DATA_PREFIX_PHONE, "");

                    // phone is middle arg, target is from own prefix to next prefix
                } else {
                    result1 = encodedPerson.substring(indexOfPhonePrefix1, indexOfEmailPrefix1).trim().replace(PERSON_DATA_PREFIX_PHONE, "");
                }
                final int indexOfPhonePrefix2 = encodedPerson.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix2 = encodedPerson.indexOf(PERSON_DATA_PREFIX_EMAIL);
                // name is leading substring up to first data prefix symbol
                int indexOfFirstPrefix = Math.min(indexOfEmailPrefix2, indexOfPhonePrefix2);
                final String[] person = new String[PERSON_DATA_COUNT];
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
            for (String m2 : new String[]{MESSAGE_INVALID_STORAGE_FILE_CONTENT}) {
                System.out.println(LINE_PREFIX + m2);
            }
            for (String m2 : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m2);
            }
            System.exit(0);
        }
        ALL_PERSONS.clear();
        ALL_PERSONS.addAll(successfullyDecoded.get());
        while (true) {
            System.out.print(LINE_PREFIX + "Enter command: ");
            String inputLine = SCANNER.nextLine();
            // silently consume all blank and comment lines
            while (inputLine.trim().isEmpty() || inputLine.trim().charAt(0) == INPUT_COMMENT_MARKER) {
                inputLine = SCANNER.nextLine();
            }
            String userCommand = inputLine;
            for (String m1 : new String[]{"[Command entered:" + userCommand + "]"}) {
                System.out.println(LINE_PREFIX + m1);
            }
            String feedback = executeCommand(userCommand);
            for (String m : new String[]{feedback, DIVIDER}) {
                System.out.println(LINE_PREFIX + m);
            }
        }
    }



    /**
     * Sets up the storage file based on the supplied file path.
     * Creates the file if it is missing.
     * Exits if the file name is not acceptable.
     */
    private static void setupGivenFileForStorage(String filePath) {

        if (!isValidFilePath(filePath)) {
            for (String m : new String[]{String.format(MESSAGE_INVALID_FILE, filePath)}) {
                System.out.println(LINE_PREFIX + m);
            }
            for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m);
            }
            System.exit(0);
        }

        storageFilePath = filePath;
        final File storageFile = new File(filePath);
        if (storageFile.exists()) {
            return;
        }

        for (String m1 : new String[]{String.format(MESSAGE_ERROR_MISSING_STORAGE_FILE, filePath)}) {
            System.out.println(LINE_PREFIX + m1);
        }

        try {
            storageFile.createNewFile();
            for (String m : new String[]{String.format(MESSAGE_STORAGE_FILE_CREATED, filePath)}) {
                System.out.println(LINE_PREFIX + m);
            }
        } catch (IOException ioe) {
            for (String m : new String[]{String.format(MESSAGE_ERROR_CREATING_STORAGE_FILE, filePath)}) {
                System.out.println(LINE_PREFIX + m);
            }
            for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m);
            }
            System.exit(0);
        }
    }

    /**
     * Sets up the storage based on the default file.
     * Creates file if missing.
     * Exits program if the file cannot be created.
     */
    private static void setupDefaultFileForStorage() {
        for (String m : new String[]{MESSAGE_USING_DEFAULT_FILE}) {
            System.out.println(LINE_PREFIX + m);
        }
        storageFilePath = DEFAULT_STORAGE_FILEPATH;
        final File storageFile = new File(storageFilePath);
        if (storageFile.exists()) {
            return;
        }

        for (String m1 : new String[]{String.format(MESSAGE_ERROR_MISSING_STORAGE_FILE, storageFilePath)}) {
            System.out.println(LINE_PREFIX + m1);
        }

        try {
            storageFile.createNewFile();
            for (String m : new String[]{String.format(MESSAGE_STORAGE_FILE_CREATED, storageFilePath)}) {
                System.out.println(LINE_PREFIX + m);
            }
        } catch (IOException ioe) {
            for (String m : new String[]{String.format(MESSAGE_ERROR_CREATING_STORAGE_FILE, storageFilePath)}) {
                System.out.println(LINE_PREFIX + m);
            }
            for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                System.out.println(LINE_PREFIX + m);
            }
            System.exit(0);
        }
    }

    /**
     * Returns true if the given file path is valid.
     * A file path is valid if it has a valid parent directory as determined by {@link #hasValidParentDirectory}
     * and a valid file name as determined by {@link #hasValidFileName}.
     */
    private static boolean isValidFilePath(String filePath) {
        if (filePath == null) {
            return false;
        }
        Path filePathToValidate;
        try {
            filePathToValidate = Paths.get(filePath);
        } catch (InvalidPathException ipe) {
            return false;
        }
        Path parentDirectory = filePathToValidate.getParent();
        return (parentDirectory == null || Files.isDirectory(parentDirectory)) && filePathToValidate.getFileName().toString().lastIndexOf('.') > 0
                && (!Files.exists(filePathToValidate) || Files.isRegularFile(filePathToValidate));
    }


    /*
     * ===========================================
     *           COMMAND LOGIC
     * ===========================================
     */

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
        case COMMAND_ADD_WORD:
            // try decoding a person from the raw args
            Optional<String[]> decodedPersons = Optional.empty();
            boolean isPersonDataPresent = true;
            // check that we can extract the parts of a person from the encoded string
            final String matchAnyPersonDataPrefix = PERSON_DATA_PREFIX_PHONE + '|' + PERSON_DATA_PREFIX_EMAIL;
            final String[] splitArgs = commandArgs.trim().split(matchAnyPersonDataPrefix);
            if (!(splitArgs.length == 3 // 3 arguments
                    && !splitArgs[0].isEmpty() // non-empty arguments
                    && !splitArgs[1].isEmpty()
                    && !splitArgs[2].isEmpty())) {
                isPersonDataPresent = false;
            }
            if(isPersonDataPresent) {
                String result11;
                final int indexOfPhonePrefix = commandArgs.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix = commandArgs.indexOf(PERSON_DATA_PREFIX_EMAIL);

                // email is last arg, target is from prefix to end of string
                if (indexOfEmailPrefix > indexOfPhonePrefix) {
                    result11 = commandArgs.substring(indexOfEmailPrefix, commandArgs.length()).trim().replace(PERSON_DATA_PREFIX_EMAIL, "");

                    // email is middle arg, target is from own prefix to next prefix
                } else {
                    result11 = commandArgs.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim().replace(PERSON_DATA_PREFIX_EMAIL, "");
                }
                String result1;
                final int indexOfPhonePrefix1 = commandArgs.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix1 = commandArgs.indexOf(PERSON_DATA_PREFIX_EMAIL);

                // phone is last arg, target is from prefix to end of string
                if (indexOfPhonePrefix1 > indexOfEmailPrefix1) {
                    result1 = commandArgs.substring(indexOfPhonePrefix1, commandArgs.length()).trim().replace(PERSON_DATA_PREFIX_PHONE, "");

                    // phone is middle arg, target is from own prefix to next prefix
                } else {
                    result1 = commandArgs.substring(indexOfPhonePrefix1, indexOfEmailPrefix1).trim().replace(PERSON_DATA_PREFIX_PHONE, "");
                }
                final int indexOfPhonePrefix2 = commandArgs.indexOf(PERSON_DATA_PREFIX_PHONE);
                final int indexOfEmailPrefix2 = commandArgs.indexOf(PERSON_DATA_PREFIX_EMAIL);
                // name is leading substring up to first data prefix symbol
                int indexOfFirstPrefix = Math.min(indexOfEmailPrefix2, indexOfPhonePrefix2);
                final String[] person3 = new String[PERSON_DATA_COUNT];
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
                return String.format(MESSAGE_INVALID_COMMAND_FORMAT, COMMAND_ADD_WORD, String.format(MESSAGE_COMMAND_HELP, COMMAND_ADD_WORD, COMMAND_ADD_DESC) + LS
                            + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_ADD_PARAMETERS) + LS
                            + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_ADD_EXAMPLE) + LS);
            }

            // add the person as specified
            final String[] personToAdd = decodeResult.get();
            ALL_PERSONS.add(personToAdd);
            final ArrayList<String> encoded1 = new ArrayList<>();
            for (String[] person1 : ALL_PERSONS) {
                encoded1.add(String.format(PERSON_STRING_REPRESENTATION,
                        person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]));
            }
            final ArrayList<String> linesToWrite1 = encoded1;
            try {
                Files.write(Paths.get(storageFilePath), linesToWrite1);
            } catch (IOException ioe1) {
                for (String m1 : new String[]{String.format(MESSAGE_ERROR_WRITING_TO_FILE, storageFilePath)}) {
                    System.out.println(LINE_PREFIX + m1);
                }
                for (String m1 : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                    System.out.println(LINE_PREFIX + m1);
                }
                System.exit(0);
            }
            return String.format(MESSAGE_ADDED,
                    personToAdd[PERSON_DATA_INDEX_NAME], personToAdd[PERSON_DATA_INDEX_PHONE], personToAdd[PERSON_DATA_INDEX_EMAIL]);
            case COMMAND_FIND_WORD:
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
                    final int displayIndex1 = i1 + DISPLAYED_INDEX_OFFSET;
                    messageAccumulator1.append('\t')
                                      .append(String.format(MESSAGE_DISPLAY_LIST_ELEMENT_INDEX, displayIndex1) + String.format(MESSAGE_DISPLAY_PERSON_DATA,
                                              person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]))
                                      .append(LS);
                }
                String listAsString1 = messageAccumulator1.toString();
                for (String m1 : new String[]{listAsString1}) {
                    System.out.println(LINE_PREFIX + m1);
                }
                // clone to insulate from future changes to arg list
                latestPersonListingView = new ArrayList<>(personsFound);
                return String.format(MESSAGE_PERSONS_FOUND_OVERVIEW, personsFound.size());
            case COMMAND_LIST_WORD:
            ArrayList<String[]> toBeDisplayed = ALL_PERSONS;
            final StringBuilder messageAccumulator = new StringBuilder();
            for (int i = 0; i < toBeDisplayed.size(); i++) {
                final String[] person = toBeDisplayed.get(i);
                final int displayIndex = i + DISPLAYED_INDEX_OFFSET;
                messageAccumulator.append('\t')
                                  .append(String.format(MESSAGE_DISPLAY_LIST_ELEMENT_INDEX, displayIndex) + String.format(MESSAGE_DISPLAY_PERSON_DATA,
                                          person[PERSON_DATA_INDEX_NAME], person[PERSON_DATA_INDEX_PHONE], person[PERSON_DATA_INDEX_EMAIL]))
                                  .append(LS);
            }
            String listAsString = messageAccumulator.toString();
            for (String m : new String[]{listAsString}) {
                System.out.println(LINE_PREFIX + m);
            }
            // clone to insulate from future changes to arg list
            latestPersonListingView = new ArrayList<>(toBeDisplayed);
            return String.format(MESSAGE_PERSONS_FOUND_OVERVIEW, toBeDisplayed.size());
            case COMMAND_DELETE_WORD:
                boolean result;
                try {
                    final int extractedIndex = Integer.parseInt(commandArgs.trim()); // use standard libraries to parse
                    result = extractedIndex >= DISPLAYED_INDEX_OFFSET;
                } catch (NumberFormatException nfe) {
                    result = false;
                }
                if (!result) {
                    return String.format(MESSAGE_INVALID_COMMAND_FORMAT, COMMAND_DELETE_WORD, String.format(MESSAGE_COMMAND_HELP, COMMAND_DELETE_WORD, COMMAND_DELETE_DESC) + LS
                                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_DELETE_PARAMETER) + LS
                                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_DELETE_EXAMPLE) + LS);
                }
                final int targetVisibleIndex = Integer.parseInt(commandArgs.trim());
                if (!(targetVisibleIndex >= DISPLAYED_INDEX_OFFSET && targetVisibleIndex < latestPersonListingView.size() + DISPLAYED_INDEX_OFFSET)) {
                    return MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
                }
                final String[] targetInModel = latestPersonListingView.get(targetVisibleIndex - DISPLAYED_INDEX_OFFSET);
                final boolean changed = ALL_PERSONS.remove(targetInModel);
                if (changed) {
                    final ArrayList<String> encoded2 = new ArrayList<>();
                    for (String[] person1 : ALL_PERSONS) {
                        encoded2.add(String.format(PERSON_STRING_REPRESENTATION,
                                person1[PERSON_DATA_INDEX_NAME], person1[PERSON_DATA_INDEX_PHONE], person1[PERSON_DATA_INDEX_EMAIL]));
                    }
                    final ArrayList<String> linesToWrite2 = encoded2;
                    try {
                        Files.write(Paths.get(storageFilePath), linesToWrite2);
                    } catch (IOException ioe1) {
                        for (String m1 : new String[]{String.format(MESSAGE_ERROR_WRITING_TO_FILE, storageFilePath)}) {
                            System.out.println(LINE_PREFIX + m1);
                        }
                        for (String m1 : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                            System.out.println(LINE_PREFIX + m1);
                        }
                        System.exit(0);
                    }
                }
                return changed ? String.format(MESSAGE_DELETE_PERSON_SUCCESS, String.format(MESSAGE_DISPLAY_PERSON_DATA,
                        targetInModel[PERSON_DATA_INDEX_NAME], targetInModel[PERSON_DATA_INDEX_PHONE], targetInModel[PERSON_DATA_INDEX_EMAIL])) // success
                                                                  : MESSAGE_PERSON_NOT_IN_ADDRESSBOOK; // not found
            case COMMAND_CLEAR_WORD:
            ALL_PERSONS.clear();
            final ArrayList<String> encoded = new ArrayList<>();
            for (String[] person : ALL_PERSONS) {
                encoded.add(String.format(PERSON_STRING_REPRESENTATION,
                        person[PERSON_DATA_INDEX_NAME], person[PERSON_DATA_INDEX_PHONE], person[PERSON_DATA_INDEX_EMAIL]));
            }
            final ArrayList<String> linesToWrite = encoded;
            try {
                Files.write(Paths.get(storageFilePath), linesToWrite);
            } catch (IOException ioe) {
                for (String m : new String[]{String.format(MESSAGE_ERROR_WRITING_TO_FILE, storageFilePath)}) {
                    System.out.println(LINE_PREFIX + m);
                }
                for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                    System.out.println(LINE_PREFIX + m);
                }
                System.exit(0);
            }
            return MESSAGE_ADDRESSBOOK_CLEARED;
            case COMMAND_HELP_WORD:
            return (String.format(MESSAGE_COMMAND_HELP, COMMAND_ADD_WORD, COMMAND_ADD_DESC) + LS
                    + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_ADD_PARAMETERS) + LS
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_ADD_EXAMPLE) + LS) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_FIND_WORD, COMMAND_FIND_DESC) + LS
                    + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_FIND_PARAMETERS) + LS
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_FIND_EXAMPLE) + LS) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_LIST_WORD, COMMAND_LIST_DESC) + LS
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_LIST_EXAMPLE) + LS) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_DELETE_WORD, COMMAND_DELETE_DESC) + LS
                    + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_DELETE_PARAMETER) + LS
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_DELETE_EXAMPLE) + LS) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_CLEAR_WORD, COMMAND_CLEAR_DESC) + LS
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_CLEAR_EXAMPLE) + LS) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_EXIT_WORD, COMMAND_EXIT_DESC)
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_EXIT_EXAMPLE)) + LS
                    + (String.format(MESSAGE_COMMAND_HELP, COMMAND_HELP_WORD, COMMAND_HELP_DESC)
                    + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_HELP_EXAMPLE));
            case COMMAND_EXIT_WORD:
                for (String m : new String[]{MESSAGE_GOODBYE, DIVIDER, DIVIDER}) {
                    System.out.println(LINE_PREFIX + m);
                }
                System.exit(0);
            default:
            return String.format(MESSAGE_INVALID_COMMAND_FORMAT, commandType, (String.format(MESSAGE_COMMAND_HELP, COMMAND_ADD_WORD, COMMAND_ADD_DESC) + LS
                        + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_ADD_PARAMETERS) + LS
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_ADD_EXAMPLE) + LS) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_FIND_WORD, COMMAND_FIND_DESC) + LS
                        + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_FIND_PARAMETERS) + LS
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_FIND_EXAMPLE) + LS) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_LIST_WORD, COMMAND_LIST_DESC) + LS
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_LIST_EXAMPLE) + LS) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_DELETE_WORD, COMMAND_DELETE_DESC) + LS
                        + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_DELETE_PARAMETER) + LS
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_DELETE_EXAMPLE) + LS) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_CLEAR_WORD, COMMAND_CLEAR_DESC) + LS
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_CLEAR_EXAMPLE) + LS) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_EXIT_WORD, COMMAND_EXIT_DESC)
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_EXIT_EXAMPLE)) + LS
                        + (String.format(MESSAGE_COMMAND_HELP, COMMAND_HELP_WORD, COMMAND_HELP_DESC)
                        + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_HELP_EXAMPLE)));
        }
    }


}