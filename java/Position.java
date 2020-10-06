
//**************************************************
// Position
//**************************************************

class Position {
	private int row;
	private int column;

	/** empty constructor */
	public Position () throws Exception {
		setRow(1);
		setColumn(1);
	}

	/** constructor
	@param vrow (int) row index from 1 to 9
	@param vcolumn (int) column index from 1 to 9
	*/
	public Position (int vrow, int vcolumn) throws Exception {
		setRow(vrow);
		setColumn(vcolumn);
	}

	/** set line
	@param value (int) row index from 1 to 9
	*/
	public void setRow (int value) throws Exception {
		if (value < 1 || value > 9) throw new Exception("Line out of range!");
		row = value;
	}

	/** set column
	@param value (int) column index from 1 to 9
	*/
	public void setColumn (int value) throws Exception {
		if (value < 1 || value > 9) throw new Exception("Line out of range!");
		column = value;
	}

	/** get line
	@return (int) row index from 1 to 9
	*/
	public int getRow () {
		return row;
	}

	/** get column
	@return (int) column index from 1 to 9
	*/
	public int getColumn () {
		return column;
	}

	/** compare
	@param vrow (int) row index from 1 to 9
	@param vcolumn (int) column index from 1 to 9
	@return (bool) true if equal
	*/
	public boolean equal (int vrow, int vcolumn) {
		return (row == vrow && column == vcolumn);
	}

	/** compare
	@param location (Position) location
	@return (bool) true if equal
	*/
	public boolean equal (Position location) {
		return (row == location.getRow() && column == location.getColumn());
	}

	/** converts row x column to linear index
	@return (int) linear position
	*/
	public int position () {
		return (((row - 1) * 9) + column) -1;
	}
}
