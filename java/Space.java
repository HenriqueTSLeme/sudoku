public class Space {
    private boolean sample [];

    public Space () {
        sample = new boolean[9];
        for (int item = 0; item < 9; item++) {
            sample[item] = true;
        }
    }
    
    public int count_chances () {
        int result = 0;
        for (int item = 0; item < 9; item++) {
            if (sample[item] == true) result++;
        }
        return result;
    }
    
    public boolean discard (int index) throws Exception {
        index --;
        if (index >= 9) throw new Exception("Index out of range!");
        if (sample [index]) {
            sample [index] = false;
            return true;
        }
        return false;
    }
    
    public boolean mark (int index) throws Exception {
        index --;
        if (index >= 9) throw new Exception("Index out of range!");
        if (sample [index] == false) {
            sample [index] = true;
            return true;
        }
        return false;
    }
    
    public void choose (int index) throws Exception {
        index --;
        if (index >= 9) throw new Exception("Index out of range!");
        for (int item = 0; item < 9; item++) {
            if (item != index) sample[item] = false;
        }
    }
    
    public boolean is_possible (int index) throws Exception {
        index --;
        if (index >= 9) throw new Exception("Index out of range!");
        return sample [index];
    }
    
    public int read () {
        int result = 0;
        for (int item = 0; item < 9; item++) {
            if (sample[item] == true) {
                if (result > 0) return 0;
                result = item + 1;
            }
        }
        return result;
    }
    
	/** Copy possibilities to another cell
	@param location (Position) cell position
	@return (unsigned int) quantidade de possibilidades */
    public void copyTo (Space pcell) throws Exception {
        for (int chance = 1; chance <=9; chance++ ) {
            if (is_possible(chance)) {
                pcell.mark(chance);
            } else {
                pcell.discard(chance);
            }
        }
    }
}