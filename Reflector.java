package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        // FIXME
        if (perm.derangement() == false) {
            throw new EnigmaException("Reflector class requires deranged cycles");
        }
    }

    // FIXME?

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw new EnigmaException("Reflector has only a single position");
        }
    }

    @Override
    boolean reflecting() {
        return true;
    }

    int convertBackward(int e) {
        throw new EnigmaException("Reflector does NOT convert backwards!");
    }
}
