package enigma;

import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Joseph Heupler
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main).
     * @param args Command line arguments.
     */
    Main(List<String> args) {
        // get config file from args
        _config = getInput(args.get(0));

        // get input file from args
        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        // get output file from args
        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME.
     * @param name file name
     * @return Scanner for file named NAME
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     *  Return a PrintStream writing to the file named NAME.
     * @param name file name
     * @return PrintStream for file named NAME
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }



    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        // FIXME
        Machine enigmaMachine = readConfig();
        if (!_input.hasNext()) {
            throw EnigmaException.error(
                    "Wrong format for input!");
        }
        String str = "\\*.+";
        // STR REGEX SHOULD MATCH THIS LINE: `* B Beta III IV I AXLE (YF) (ZH)`.
        while (_input.hasNextLine()) {
            String currInputLine = _input.nextLine();

            boolean isMachineSet = false; // for debugging purposes
            if (currInputLine.matches(str)) {
                String settingInput = currInputLine; // DEBUG?
                settingInput = settingInput.trim();
                setUp(enigmaMachine, settingInput);
                isMachineSet = true;
                continue; // process next line
            }
            if (!currInputLine.matches(str)) {
//                String inputWithoutAsterisk = currInputLine.replace(" ");
                String encryptedString = enigmaMachine.convert(currInputLine);
                printMessageLine(encryptedString); // write encryption to output
                isMachineSet = false;
            }
        }
    }

    /**
     * Validates config file for enigma machine.
     */
    private void configureMachine() {
        _alphabet = new Alphabet();
        if (_config.hasNext("[^*()\\s]+")) { // changed regex
            String alphabetInput = _config.next();
            _alphabet = new Alphabet(alphabetInput.trim());
        } else {
            throw EnigmaException.error("No alphabet provided or alphabet has punctuation.");
        }

        int countNumConfig = 0;
        if (_config.hasNextInt()) {
            _numRotors = _config.nextInt();
            countNumConfig += 1;
        } else {
            throw EnigmaException.error("No int provided for numRotors");
        }
        if (_config.hasNextInt()) {
            _numPawls = _config.nextInt();
            countNumConfig += 1;
        } else {
            throw EnigmaException.error("No int provided for pawls");
        }
        if (_config.hasNextInt()) {
            _config.nextInt();
            countNumConfig += 1;
        }
        // check if we have illegal number of configurations supplied
        if (countNumConfig > 2) {
            throw EnigmaException.error("More configuration numbers supplied");
        }
        while (_config.hasNext()) {
            if (_config.hasNext("[^a-zA-Z]+\\s+")) {
                break;
            }
            Rotor configuredRotor = readRotor();
            _allRotors.add(configuredRotor);
        }
        _configuredMachine = new Machine(_alphabet,
                _numRotors, _numPawls, _allRotors);
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            // FIXME
            configureMachine();
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
        return _configuredMachine;
    }

    private Rotor assignPermutations(Permutation currentPermutation) {
        if (_notches.charAt(0) == 'M') {
            return new MovingRotor(_currRotorName,
                    currentPermutation, _notches.substring(1));
        }
        else if (_notches.charAt(0) == 'N') {
            return new FixedRotor(_currRotorName, currentPermutation);
        }
        else if (_notches.charAt(0) == 'R') {
            return new Reflector(_currRotorName, currentPermutation);
        } else {
            throw EnigmaException.error("Notches is invalid");
        }
    }

    private Rotor configureRotor() {
        Rotor configuredRotor;
        if (_config.hasNext("([a-zA-Z]+[0-9]*)")) {
            _currRotorName = _config.next();
        } else {
            throw EnigmaException.error("Wrong " +
                    "configuration for Rotor name |" + _config.next() + " |");
        }
        if (_config.hasNext("([a-zA-Z]+[0-9]*)")) {
            String s = _config.next();
//            String pattern = "[MRN]([a-zA-Z\\d]+)";
//            Matcher m = Pattern.compile(pattern).matcher(s);
//            String notches = null;
//            if (m.find()) {
//               notches = m.group(1);
//            }
            _notches = s;
        }
        _cyclesAggregator = new StringBuilder();
        while (_config.hasNext("((\\(\\w+\\))+\\n*)+")) { // change regex
            String current = _config.next();
            String illegalCharacterPattern = "[^\\(\\)\\*]+";
            if (current.matches(illegalCharacterPattern)) {
                throw EnigmaException.error("Wrong: cycles cannot include punctuation chars.");
            }
            String currentCycleInput = current;
            _cyclesAggregator.append(currentCycleInput);
        }
        _cycles = _cyclesAggregator.toString();
        _cycles = _cycles.trim();
        // only add permutation if cycles is not null
        if (_cycles != "" && _cycles != null) {
            Permutation currentPermutation = new Permutation(_cycles, _alphabet);
            configuredRotor = assignPermutations(currentPermutation);
            return configuredRotor;
        }
        throw EnigmaException.error("Something went wrong");
    }

    /**
     * Configure a rotor based on config file.
     * @return returns
     */
    private Rotor readRotor() {
        try {
            // FIXME
            Rotor configuredRotor = configureRotor();
            return configuredRotor;
        } catch (NoSuchElementException excp) {
            throw error("Wrong rotor configuration format.");
        }
    }

    private ArrayList<String> getRotorsInput(String[] parsedSettings) {
        ArrayList<String> rotorsInput = new ArrayList<String>();
        // get rotors from input that are also in machine's rotors
        for (int i = 0; i < parsedSettings.length; i++) {
            for (int j = 0; j < _allRotors.size();  j++) {
                Rotor currentRotor = _allRotors.get(j);
                String currentSetting = parsedSettings[i];
                if (currentRotor.name().equals(currentSetting)) {
                    rotorsInput.add(currentSetting);
                    break;
                }
            }
        }
        return rotorsInput;
    }

    private String[] getParsedSettings(String settings) {
        // split inputted settings by whitespace
        String[] parsedSettings = settings.split(" ");
        if (settings.charAt(0) != '*') {
            throw error("Setting must start with *");
        }
        return parsedSettings;
    }

    private String getRotorSetting(String[] parsedSettings, ArrayList<String> rotorsInput) {
        String rotorSetting = null;
        // iterate backwards from plugboard settings until encounter first string without parentheses
        for (int i = parsedSettings.length - 1; i >=0; i--) {
            if (!parsedSettings[i].contains("(")) {
                // must be the setting for configurations without a plugboard
                rotorSetting = parsedSettings[i];
                break;
            }
        }

        // set rotors by setting corresponding to "AXLE"
        // check if number of rotorsInput is equal to length of rotorSetting
        if (rotorsInput.size() - 1 != rotorSetting.length()) {  // DEBUG?
            throw EnigmaException.error(
                    "Number of inputted rotors does not equal"
                    + "the number of starting positions for the rotors (rotorSetting");
        }
        return rotorSetting;
    }

    /** Set Machine M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        // FIXME
        String[] parsedSettings = getParsedSettings(settings);

        // get rotors that are in machine and in inputted settings
        ArrayList<String> rotorsInput = getRotorsInput(parsedSettings);

        // check setting for format
        // checkSetting(M, addedRotors, addedPresets);

        M.insertRotors(rotorsInput.toArray(new String[rotorsInput.size()]));

        String rotorSetting = getRotorSetting(parsedSettings, rotorsInput);

        M.setRotors(rotorSetting); // DEBUG?

        // finally configure plugboard
        M.configurePlugboard(settings); // JOE

    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        // FIXME
        // system.out or some other function?
        StringBuilder result = new StringBuilder();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            if (i != 0 && i % 5 == 0) { // reached 5 characters
                result.append(out.toString());
                result.append(" ");
                out = new StringBuilder();
            }
            out.append(msg.charAt(i));
        }
        String remaining = out.toString();
        if (remaining != "") {
            result.append(remaining);
        }
        _output.println(result.toString().trim());
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    private ArrayList<Rotor> _allRotors = new ArrayList<>();

    private String _cycles;

    private StringBuilder _cyclesAggregator;

    private String _currRotorName;

    private String _notches;

    private int _numRotors;

    private int _numPawls;

    /** Configured machine preset by readConfig — the Enigma. */
    private Machine _configuredMachine;

}