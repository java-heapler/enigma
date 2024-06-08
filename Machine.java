package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Joseph Heupler
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        // FIXME
        _numRotors = numRotors;
        _allRotors = allRotors;
        _pawls = pawls;
        _machineRotors = new Rotor[0];
        if (_numRotors <= 1) {
            String errStr = "Enigma must have NUMROTORS > 1, "
                    + _numRotors + " passed in.";
            throw new EnigmaException(errStr);
        } else if (_pawls < 0) {
            String errStr = "Enigma must have PAWLS >= 0, "
                    + _pawls + " passed in";
            throw new EnigmaException(errStr);
        } else if (_numRotors <= _pawls) {
            throw new EnigmaException("Enigma "
                    + "must have NUMROTORS > PAWLS");
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        // FIXME
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        // FIXME
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        // FIXME
        return _machineRotors[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotorsInput) {
        // FIXME
        if (rotorsInput.length > numRotors()) {
            throw new EnigmaException("Too low numRotors inputted, "
                    + "need " + rotorsInput.length + " numPawls");
        }

        Rotor[] tmpRotors = new Rotor[numRotors() + 1];
        int tmpRotorsI = 0;
        for (int i = 0; i < rotorsInput.length; i += 1) {
            for (Rotor r : _allRotors) {
                if (rotorsInput[i].equals(r.name())) {
                    tmpRotors[tmpRotorsI] = r;
                    tmpRotorsI += 1;
                    break;
                }
            }
        }

        _machineRotors = new Rotor[tmpRotorsI];
        System.arraycopy(tmpRotors, 0, _machineRotors, 0, tmpRotorsI);
        for (int i = 0; i < _machineRotors.length; i += 1) {
            for (int j = 0; j < _machineRotors.length; j += 1) {
                if (i != j && _machineRotors[i].name().equals(_machineRotors[j].name())) {
                    throw new EnigmaException(
                            "Duplicate Rotors not allowed, "
                                    + "duplicate found: " + _machineRotors[i].name());
                }
            }
        }

        if (!_machineRotors[0].reflecting()) {
            throw new EnigmaException("Last Rotor "
                    + "(" + _machineRotors[0].toString() + ") "
                    + "is not reflector but it should be a reflector.");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        // FIXME
        if (numRotors() == setting.length()+1) {
            int rotorIndex = 0;
            while (rotorIndex < _machineRotors.length-1) {
                char currChar = setting.charAt(rotorIndex);
                _machineRotors[rotorIndex+1].set(currChar);
                rotorIndex++;
            }
        } else {
            throw new EnigmaException("length of numRotors must always be one greater than the # of characters in setting");
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        // FIXME
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        // FIXME
        _plugboard = plugboard;
    }

    void configurePlugboard(String settings) {
        Pattern cycles_pattern = Pattern.compile("(\\(\\w+\\))+"); // DEBUG??

        // get cycles from settings string
        // "(CA) (CB)" -> Permutation Constructor (as cycles argument) (which is in Permutation.java)
        Matcher m = cycles_pattern.matcher(settings);
        String plugboardCycles = null;
        while (m.find()) {
            plugboardCycles += m.group(0); // I need this : "(ASDASD) (ASDASD)"
        }


        // only add plugboard if there are cycles to add
        if (plugboardCycles != null && plugboardCycles != "") {
            Alphabet alphabet = alphabet();

            Permutation configuredPlugboard = new Permutation(plugboardCycles, alphabet);// FEED CYCLES INTO HERE

            setPlugboard(configuredPlugboard);
        }
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        // do not permute with plugboard if plugboard does not exist
        if (plugboard() != null) {
            c = plugboard().permute(c);
        }
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        // do not permute with plugboard if plugboard does not exist
        if (plugboard() != null) {
            c = plugboard().permute(c);
        }
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    private void handleAdvance(Rotor[] _machineRotors, int i) {
        Rotor previousRotor = _machineRotors[i-1];
        Rotor nextRotor = _machineRotors[i + 1];
        Rotor currentRotor = _machineRotors[i];
        // case for advancing right rotor
        if (currentRotor.rotates() && nextRotor.atNotch()) {
            currentRotor.advance();
        }
        // case for advancing left rotor
        else if (previousRotor.rotates() && currentRotor.atNotch() && currentRotor.rotates()) {
            currentRotor.advance();
        }
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        // FIXME
        for (int i = 1; i < _machineRotors.length - 1; i += 1) {
            handleAdvance(_machineRotors, i); //updates index so we do this
        }
        // always advance the fast rotor
        int lastIndex = _machineRotors.length - 1;
        if (_machineRotors[lastIndex].rotates()) {
            _machineRotors[lastIndex].advance();
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        // FIXME
        int i = _machineRotors.length - 1;
        while (i >= 0) {
            c = _machineRotors[i].convertForward(c);
            i--;
        }

        i = 1;
        while (i < _machineRotors.length) {
            Rotor currentRotor = _machineRotors[i];
            // reflector does NOT convert backwards
            if (!currentRotor.reflecting()) {
                c = currentRotor.convertBackward(c);
            }
            i++;
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        // FIXME
        String msgNoSpace = msg.replaceAll(" ", "");
        char[] msgArr = msgNoSpace.toCharArray();

        // CHECK: all chars in msg are in alphabet
        for (char ch : msgArr) {
            if (!_alphabet.contains(ch)) {
                throw EnigmaException.error(
                        "Char not in alphabet.");
            }
        }

        StringBuilder out = new StringBuilder();
        int i = 0;
        for (char ch : msgArr) {
            i = convert(_alphabet.toInt(ch));
            out.append(_alphabet.toChar(i));
        }

        String result = out.toString();
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    // FIXME: ADDITIONAL FIELDS HERE, IF NEEDED.
    Rotor[] getMachineRotors() {
        return _machineRotors;
    }
    private Permutation _plugboard;
    private int _numRotors;
    private int _pawls;
    private Collection<Rotor> _allRotors;
    private Rotor[] _machineRotors;
    private Iterator<Rotor> _iterRotors;

}
