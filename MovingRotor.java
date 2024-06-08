package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Joseph Heupler
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        // FIXME
        _notches = notches;
    }

    // FIXME?

    @Override
    void advance() {
        // FIXME
        this.set(this.setting()+1);
    }

    @Override
    String notches() {
        // FIXME
        return _notches;
    }

    @Override
    boolean rotates() {
        // FIXME
        return true;
    }

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    private String _notches;
}
