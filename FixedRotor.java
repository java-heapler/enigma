package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
    }

    // FIXME ?

    @Override
    void advance() {
        throw new EnigmaException("Advancing isn't possible with a fixed rotor");
    }

    @Override
    boolean rotates() {
        return false;
    }
}
