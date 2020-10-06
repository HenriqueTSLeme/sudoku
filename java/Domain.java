
//**************************************************
// Domain
//**************************************************

public class Domain {
	private Position list [];
	private int current;

    private Domain () {
    	list = new Position[9];
	    current = 0;
    }

	public Position getPosition (int index) throws Exception {
		if (index > 8) throw new Exception ("Domain index out of range!");
		return list[index];
	}

	public bool has_next () {
		return current < 9;
	}

	public Position getPosition () throws Exception {
		if (has_next() == false) throw new Exception ("Domain index out of range!");
		Position item = list[current];
		current++;
		return item;
	}

    public static Domain fillRow (Position location) {
        Domain domain = new Domain();
        int index = 0;
        for (int column = 1; column <= 9; column++) {
            domain.list[index].setRow(location.getRow());
            domain.list[index].setColumn(column);
            domain.index++;
        }
        domain.current = 0;
        return domain;
    }

    public static Domain fillColumn (Position location) {
        Domain domain = new Domain();
        int index = 0;
        for (int row = 1; row <= 9; row++) {
            domain.list[index].setColumn(location.getColumn());
            domain.list[index].setRow(row);
            domain.index++;
        }
        domain.current = 0;
        return domain;
    }

    public static Domain fillQuadrant (Position location) {
        Domain domain = new Domain();
        int index = 0;
        int baseRow = base(location.getRow());
        int baseColumn = base(location.getColumn());
        for (int row = baseRow; row < baseRow + 3; row++) {
            for (int column =  baseColumn; column < baseColumn + 3; column++) {
                domain.list[index].setRow(row);
                domain.list[index].setColumn(column);
                index++;
            }
        }
        domain.current = 0;
        return domain;
    }
}