package enigma;

import static enigma.EnigmaException.*;
import java.util.regex.*;
import java.util.ArrayList; // import the ArrayList class

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Joseph Heupler
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        // FIXME
        _cycles = cycles;
        _alphabet = alphabet;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        // FIXME
        _cycles = _cycles + " (" + cycle + ')';
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        // FIXME
        return _alphabet.size();
    }

    /** Return the parsed cycles as an array from cycles. */
    ArrayList<String> getCyclesArr() {
        Pattern cyclesPattern = Pattern.compile("\\(+([a-zA-Z]+)\\)+"); // cycles to be separated by closed parenthesis
        ArrayList<String> cycles_arr = new ArrayList<String>();

        Matcher m = cyclesPattern.matcher(this._cycles);

        while (m.find()) {
            cycles_arr.add(m.group(1));
        }
        return cycles_arr;
    }



    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        // FIXME
        p = wrap(p);
        if (p >= size()) {
            throw new EnigmaException("index is out of bounds in alphabet"); // check to see if specific exception needed
        }

        ArrayList<String> cycles_arr = getCyclesArr();

        char target = alphabet()._chars.charAt(p);

        char char_at_index = alphabet().toChar(p); // finds char in alphabet at given index

        for (int i = 0; i < cycles_arr.size(); i++) {
            String current = cycles_arr.get(i);

            int targetInCycleIndex = current.indexOf(target);

            if (targetInCycleIndex < 0) {
                // if target does not exist in this cycle, proceed to next
                continue;
            }

            int t = (targetInCycleIndex + 1) % (current.length()); // provides wraparound value for cycle

            char shiftedChar = current.charAt(t);

            return _alphabet.toInt(shiftedChar); // map the letter to the first cycle char
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        // FIXME
        c = wrap(c);
        if (c >= size()) {
            throw new EnigmaException("index is out of bounds in alphabet"); // check to see if specific exception needed
        }

        ArrayList<String> cycles_arr = getCyclesArr();

        char target = alphabet()._chars.charAt(c);

        for (int i = 0; i < cycles_arr.size(); i++) {
            String current = cycles_arr.get(i);

            int targetInCycleIndex = current.indexOf(target);

            if (targetInCycleIndex < 0) {
                // if target does not exist in this cycle, proceed to next
                continue;
            }

            int t;
            if (targetInCycleIndex-1 < 0) {
                t = current.length() - 1;
            } else {
                t = (targetInCycleIndex-1) % (current.length());
            }

            char shiftedChar = current.charAt(t);

            return _alphabet.toInt(shiftedChar); // map the letter to the first cycle char
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        // FIXME
        if (!alphabet().contains(p)) {
            throw new EnigmaException("P was not found in the alphabet"); // check to see if specific exception needed
        }

        ArrayList<String> cycles_arr = getCyclesArr();

        char target = p;

        for (int i = 0; i < cycles_arr.size(); i++) {
            String current = cycles_arr.get(i);

            int targetInCycleIndex = current.indexOf(target);

            if (targetInCycleIndex < 0) {
                // if target does not exist in this cycle, proceed to next
                continue;
            }

            int t = (targetInCycleIndex-1) % (current.length());

            char shiftedChar = current.charAt(t);

            return shiftedChar; // map the letter to the first cycle char
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        // FIXME
        if (!alphabet().contains(c)) {
            throw new EnigmaException("C was not found in the alphabet"); // check to see if specific exception needed
        }

        ArrayList<String> cycles_arr = getCyclesArr();

        char target = c;

        for (int i = 0; i < cycles_arr.size(); i++) {
            String current = cycles_arr.get(i);

            int targetInCycleIndex = current.indexOf(target);

            if (targetInCycleIndex < 0) {
                // if target does not exist in this cycle, proceed to next
                continue;
            }

            int t;
            if (targetInCycleIndex == 0) {
                t = current.length() - 1; // loop back to start
            } else {
                t = (targetInCycleIndex-1) % (current.length());
            }

            char shiftedChar = current.charAt(t);

            return shiftedChar; // map the letter to the first cycle char
        }

        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        Pattern cycles_pattern = Pattern.compile("\\(+[a-zA-Z]+\\)+"); // cycles to be separated by closed parenthesis
        ArrayList<String> cycles_arr = new ArrayList<String>();
        Matcher m = cycles_pattern.matcher(this._cycles);
        while (m.find()) {
            cycles_arr.add(m.group());
        }
        // FIXME
        int i = 0;
        while (i < cycles_arr.size()) {
            if (cycles_arr.get(i).length() == 1) {
                return false;
            }
            i++;
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    private String _cycles;
    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
}
