package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Joseph Heupler
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        // FIXME
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        // FIXME
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        // FIXME
        if (_chars.indexOf(ch) != -1) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        // FIXME
        if (index >= this.size() && index < 0) {
            throw new EnigmaException("Index is out of bounds in alphabet"); // check to see if specific exception needed
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        // FIXME
        for (int i = 0; i < this.size(); i++) {
            if (_chars.charAt(i) == ch) {
                return i;
            }
        }
        throw new EnigmaException("Character was not found in the alphabet");
    }
    public String _chars;
}