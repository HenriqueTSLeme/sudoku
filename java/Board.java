
/**
 * Game board with every possibility to each cell
 */
class Board {
    /** The board is a set of 9 x 9 cells. cells are counted in rows and
     * columns starting at 1*/
    private Space cell [];

    /** constructor */
    public Board () {
        cell = new Space[81];
        for (int offset = 0; offset < 81; offset++ ) {
            cell [offset] = new Space();
        }
    }

    /**
     * Quadrant base. Quadrante is a 3 x 3 cells.
     * @param value (int) horizontal or vertical position
     * @return (int) quadrant base (horizontal or vertical)
     */
    public int base (int value) {
        return value - ((value - 1) % 3);
    }
    
	/** Copy possibilities to another cell
	@param location (Position) cell position
	@param pcell (Space) cell position
	@return (int) quantidade de possibilidades */
    public void copyTo (Position location, Space pcell) throws Exception {
        cell [location.position()].copyTo(pcell);
    }
    
 	/** counts how many possibilities there are for a cell
	@param location (Position) cell position
	@return (int) quantidade de possibilidades */
    public int count_chances (Position location) {
        return cell [location.position()].count_chances ();
    }
    
 	/** rules out a possibility
	@param location (Position) cell position
	@param index (int) index of the discarded possibility
	@result (bool) indica se estava ativo e se foi realmente liberado */
    public boolean discard (Position location, int index) throws Exception {
        // restricts the possibilities of this cell
        if (cell [location.position()].discard (index)) {
            // calls rule 2 which checks whether the possibilities have reached 1
            regra2_restricao (location);
            // indicates that it has released a possibility
            return true;
        }
        return false;
    }
    
	/** choose or reduces the possibilities to 1
	@param location (Position) cell position
	@param index (int) index of the elected possibility */
    public void choose (Position location, int index) throws Exception {
        // limit a cell
        cell [location.position()].choose (index);
        // calls rule 1 that rule out this possibility from the rest of the domain
        regra1_exclusao (location, index);
    }
    
	/** checks the status of each possibility
	@param location (Position) cell position
	@param index (int)index of the possibility
	@reurn (bool) state of this possibility */
    public boolean is_possible (Position location, int index) throws Exception {
        // gets the status of a cell possibility
        return cell [location.position()].is_possible (index);
    }
    
	/** obtains the unique possibility (only if it is unique)
	@param location (Position) cell position
	@return (int) single or zero possibility if there is more than one */
    public int read (Position location) {
        // obtains the unique possibility for a cell or returns zero (multiple
        // possibilities)
        return cell [location.position()].read ();
    }
    
	/** Rule 1 EXCLUSION: for a settled house, discard the value of this house
     * from ll the possibilities of the rest of the domains of this house.
	@param location (Position) cell position
	@param index (int) elected possibility */
    public void regra1_exclusao (Position location, int index) throws Exception {
        // exclude from lines
        for (int row = 1; row <=9; row++) if (row != location.getRow()) discard (new Position(row, location.getColumn()), index);
        // exclude from columns
        for (int column = 1; column <=9; column++) if (column != location.getColumn()) discard (new Position(location.getRow(), column), index);
        // exclude from quadrants
        int baseRow = base (location.getRow());
        int baseColumn = base (location.getColumn());
        for (int row = 0; row < 3; row++) {
            int curRow = baseRow + row;
            for (int column = 0; column < 3; column++) {
                int curColumn = baseColumn + column;
                if (curColumn != location.getColumn() || curRow != location.getRow()) {
                    discard (new Position(curRow, curColumn), index);
                }
            }
        }
    }
    
	/** Rule 2 RESTRICTION: if the possibilities of a cell come to one option,
     * this only possibility is the value resolved for this house.
	@param location (Position) cell position
	@param index (int) elected possibility */
    public void regra2_restricao (Position location) throws Exception {
        // this cell should have only one possibility
        if (count_chances (location) == 1) {
            // obtains the unique possibility
            int index = read (location);
            // calls rule 1 that rule out this possibility from the rest of the domain
            regra1_exclusao (location, index);
        }
    }
    
	/** Rule 3 SINGLE ISOLATION: if one of the possibilities of a cell is the
	only possibility within a domain it becomes the final value for this cell.
	@param location (Position) cell position
	@result (bool) indicates whether or not it has done some isolation */
    public boolean rule3_isolation_single (Position location) throws Exception {
        // checks if the number of possibilities is greater than 1
        if (count_chances (location) > 1) {
    
            // goes through all the possibilities of a cell
            for (int index = 1; index <=9; index++) {
                if (is_possible (location, index)) {
    
                    // looks for this possibility in the vertical domain (location.getRow ())
                    boolean dominio = true;
                    for (int row = 1; row <=9; row++) {
                        if (row != location.getRow()) {
                            if (is_possible (new Position(row, location.getColumn()), index)) {
                                dominio = false;
                                break;
                            }
                        }
                    }
                    // looks for this possibility in the horizontal domain (column)
                    if (dominio == false) {
                        dominio = true;
                        for (int column = 1; column <=9; column++) {
                            if (column != location.getColumn()) {
                                if (is_possible (new Position(location.getRow(), column), index)) {
                                    dominio = false;
                                    break;
                                }
                            }
                        }
                    }
                    // looks for this possibility in the sub-matrix domain
                    if (dominio == false) {
                        dominio = true;
                        int baseRow = base (location.getRow());
                        int baseColumn = base (location.getColumn());
                        for (int row = 0; row < 3; row++) {
                            int curRow = baseRow + row;
                            for (int column = 0; column < 3; column++) {
                                int curColumn = baseColumn + column;
                                if (curColumn != location.getColumn() || curRow != location.getRow()) {
                                    if (is_possible (new Position(curRow, curColumn), index)) {
                                        dominio = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // this is the only possibility
                    if (dominio) {
                        System.out.print ("ISOLAMENTO linha "  + location.getRow() + " coluna " + location.getColumn() + "\n");
                        choose (location, index);
                        return true;
                    }
                }
            }
        }
        return false;
    }

	/** Rule 4 CONFINEMENT ORTO: within a quadrant, if a possible value is
     * restricted to a single row or column, remove this possibility from this
     * row or column in other quadrants.
	@param location (Position) cell position
	@result (bool) indicates whether or not any projection was made */
    public boolean rule4_confinement_ortogonal (Position location) throws Exception {
        boolean result = false;
    
        // checks if the number of possibilities is greater than 1
        if (count_chances (location) > 1) {
    
            // goes through all the possibilities of a piece
            for (int index = 1; index <=9; index++) {
                if (is_possible (location, index)) {
    
                    // looks for this possibility in the horizontal sub-matrix domain
                    boolean dominio = true;
                    int baseRow = base (location.getRow());
                    int baseColumn = base (location.getColumn());
                    for (int row = 0; row < 3 && (dominio); row++) {
                        int curRow = baseRow + row;
                        if (curRow != location.getRow()) {
                            for (int column = 0; column < 3 && (dominio); column++) {
                                int curColumn = baseColumn + column;
                                if (is_possible (new Position(curRow, curColumn), index)) {
                                    dominio = false;
                                }
                            }
                        }
                    }
    
                    // delete these possibilities in this location.getRow () in other quadrants
                    if (dominio) {
                        for (int column = 1; column <= 9; column++) {
                            int matrizcoluna = base (column);
                            if (matrizcoluna != baseColumn) {
                                if (discard (new Position(location.getRow(), column), index)) {
                                    result = true;
                                }
                            }
                        }
                    }
    
                    // looks for this possibility in the vertical sub-matrix domain
                    dominio = true;
                    for (int row = 0; row < 3 && (dominio); row++) {
                        int curRow = baseRow + row;
                        for (int column = 0; column < 3 && (dominio); column++) {
                            int curColumn = baseColumn + column;
                            if (curColumn != location.getColumn()) {
                                if (is_possible (new Position(curRow, curColumn), index)) {
                                    dominio = false;
                                }
                            }
                        }
                    }
    
                    // delete these possibilities from this column in other submatrices
                    if (dominio) {
                        for (int row = 1; row <= 9; row++) {
                            int matrizlinha = base (row);
                            if (matrizlinha != baseRow) {
                                if (discard (new Position(row, location.getColumn()), index)) {
                                    result = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (result) {
            System.out.print ("CONFINAMENTO ORTO linha " + location.getRow() + " coluna " + location.getColumn() + "\n");
        }
        return result;
    }
    
	/** Rule 4 CONFINEMENT QUAD: within a row or column, if a possible value is
     * restricted to a quadrant, remove this possibility from other rows or
     * columns in this quadrants.
	@param location (Position) cell position
	@result (bool) indicates whether or not any projection was made */
    public boolean rule4_confinement_quadrant (Position location) throws Exception {
        boolean result = false;
        if (count_chances(location) > 1) {
            for (int index = 1; index <= 9; index++) {
                if (is_possible(location, index)) {

                    // check row
                    boolean valid = true;
                    for (int row = 1; row <= 9; row++) {
                        Position other = new Position (row, location.getColumn());
                        if (location.quadrant(other) == false) {
                            if (is_possible(other, index)) {
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (valid) {
                        int col_base = base(location.getColumn());
                        int row_base = base(location.getRow());
                        for (int column = col_base; column <= (col_base + 2); column++) {
                            if (column != location.getColumn()) {
                                for (int row = row_base; row <= (row_base + 2); row++) {
                                    Position other = new Position (row, column);
                                    if (discard(other, index)) {
                                        result = true;
                                    }
                                }
                            }
                        }
                    }

                    // check column
                    valid = true;
                    for (int column = 1; column <= 9; column++) {
                        Position other = new Position (location.getRow(), column);
                        if (location.quadrant(other) == false) {
                            if (is_possible(other, index)) {
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (valid) {
                        int col_base = base(location.getColumn());
                        int row_base = base(location.getRow());
                        for (int column = col_base; column <= (col_base + 2); column++) {
                            for (int row = row_base; row <= (row_base + 2); row++) {
                                if (row != location.getRow()) {
                                    Position other = new Position (row, column);
                                    if (discard(other, index)) {
                                        result = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (result) {
            System.out.print ("CONFINAMENTO QUAD linha " + location.getRow() + " coluna " + location.getColumn() + "\n");
        }
        return result;
    }

    /**
     * if possibility is valid on a cell, clear this possibilitity in another cell
     */
    public boolean clear (Position location, Position location_to) throws Exception {
        boolean result = false;
        if (location.getRow() != location_to.getRow() || location.getColumn() != location_to.getColumn()) {
            for (int index = 1; index <= 9; index++) {
                if (is_possible (location, index)) {
                    Position other = new Position(location_to.getRow(), location_to.getColumn());
                    if (is_possible (other, index)) {
                        discard(other, index);
                        result = true;
                    }
                }
            }
        }
        return result;
    }
    
    public boolean same_outcomes (Position location, Position location_to) throws Exception {
        if (location.getRow() != location_to.getRow() || location.getColumn() != location_to.getColumn()) {
            for (int index = 1; index <= 9; index++) {
                if (is_possible (location, index)) {
                    if (is_possible (new Position(location_to.getRow(), location_to.getColumn()), index) == false) {
                        return false;
                    }
                } else {
                    if (is_possible (new Position(location_to.getRow(), location_to.getColumn()), index)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean regra5_dois_pares (Position location) throws Exception {
        boolean result = false;
    
        // verifica se quantidade de possibilidades é maior que 1
        int chance = count_chances (location);
        if (chance == 2) {
    
            // passa por todas as possibilidades horizontais
            for (int linha_to = 1; linha_to <= 9; linha_to++) {
                if (location.getRow() != linha_to) {
                    int chance_to = count_chances (new Position(linha_to, location.getColumn()));
                    if (chance == chance_to) {
                        if (same_outcomes(location, new Position(linha_to, location.getColumn()))) {
                            boolean one = false;
                            for (int linha_x = 1; linha_x <= 9; linha_x++) {
                                if (location.getRow() != linha_x && linha_to != linha_x) {
                                    if (clear (location, new Position(linha_x, location.getColumn()))) {
                                        one = true;
                                    }
                                }
                            }
                            if (one) {
                                result = true;
                            }
                            break;
                        }
                    }
                }
            }
    
            // passa por todas as possibilidades verticais
            for (int coluna_to = 1; coluna_to <= 9; coluna_to++) {
                if (location.getColumn() != coluna_to) {
                    int chance_to = count_chances (new Position(location.getRow(), coluna_to));
                    if (chance == chance_to) {
                        if (same_outcomes(location, new Position(location.getRow(), coluna_to))) {
                            boolean one = false;
                            for (int coluna_x = 1; coluna_x <= 9; coluna_x++) {
                                if (location.getColumn() != coluna_x && coluna_to != coluna_x) {
                                    if (clear (location, new Position(location.getRow(), coluna_x))) {
                                        one = true;
                                    }
                                }
                            }
                            if (one) {
                                result = true;
                            }
                            break;
                        }
                    }
                }
            }
    
            // passa por todas as possibilidades do quadrante
            int col_base = base(location.getColumn());
            int lin_base = base(location.getRow());
            for (int coluna_to = col_base; coluna_to <= (col_base + 2); coluna_to++) {
                for (int linha_to = lin_base; linha_to <= (lin_base + 2); linha_to++) {
                    if (location.getColumn() != coluna_to || location.getRow() != linha_to) {
                        int chance_to = count_chances (new Position(linha_to, coluna_to));
                        if (chance == chance_to) {
                            if (same_outcomes(location, new Position(linha_to, coluna_to))) {
                                boolean one = false;
                                for (int coluna_x = col_base; coluna_x <= (col_base + 2); coluna_x++) {
                                    for (int linha_x = lin_base; linha_x <= (lin_base + 2); linha_x++) {
                                        if ((location.getColumn() != coluna_x || location.getRow() != linha_x) && (coluna_to != coluna_x || linha_to != linha_x)) {
                                            if (clear (location, new Position(linha_x, coluna_x))) {
                                                one = true;
                                            }
                                        }
                                    }
                                }
                                if (one) {
                                    result = true;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (result) {
            System.out.print ("SUFICIENCIA linha " + location.getRow() + " coluna " + location.getColumn() + "\n");
        }
        return result;
    }

    public void show () throws Exception {
        for (int row = 1; row <=9; row++) {
            if (row > 1)
                if ((row-1)%3 == 0) {
                    System.out.print ("==========|===========|===========#===========|===========|===========#===========|===========|===========\n");
                } else {
                    System.out.print ("----------|-----------|-----------#-----------|-----------|-----------#-----------|-----------|-----------\n");
                }
            for (int column = 1; column <=9; column++) {
                if (column > 1) if ((column-1)%3 == 0) System.out.print (" # "); else System.out.print (" | ");
                int c = 0;
                for (int k = 1; k<=9; k++) {
                    if (is_possible (new Position(row, column), k) == false) {
                        c++;
                    }
                }
                c = c / 2;
                for (int k = 0; k<c; k++) {
                    System.out.print (" ");
                }
                for (int k = 1; k<=9; k++) {
                    if (is_possible (new Position(row, column), k)) {
                        System.out.print (k);
                        c++;
                    }
                }
                for (; c < 9; c++) {
                    System.out.print (" ");
                }
            }
            System.out.print ("\n");
        }
    }
}