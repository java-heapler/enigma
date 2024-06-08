package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Joseph Heupler
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        // FIXME
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        // FIXME
        _setting = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        // FIXME
        int new_int = alphabet().toInt(cposn);
        set(new_int);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        // FIXME
        int a = permutation().permute(p + setting());
        int permuted = permutation().wrap(a); // performs permutation on P (x away from pos 0)
        int result = permutation().wrap(permuted - setting()); // makes sure permutation wraps around (accounting for setting)
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        // FIXME
        int inverted = permutation().wrap(permutation().invert(e + setting())); // performs permutation on E (x away from pos 0)
        int result = permutation().wrap(inverted - setting()); // makes sure inverse permutation wraps around (accounting for setting)
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        // FIXME
        int wrapped_setting = permutation().wrap(setting()); // wraps setting according to size of permutation's alphabet
        if (notches().indexOf(alphabet().toChar(wrapped_setting)) != -1) {
            return true;
        }
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;
    private int _setting;

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
}
