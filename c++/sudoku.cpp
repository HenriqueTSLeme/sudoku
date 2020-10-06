// Projeto: sudoku
// Descrição: mecanismo de resolução do jogo sudoku
// Henrique Leme, 09/12/2005

#include <iostream>

using namespace std;

unsigned int base (unsigned int value) {
	return value - ((value - 1) % 3);
}

//**************************************************
// Space
//**************************************************

/** sample spece is an array of samples */

class Space {

	/** array of samples */
	bool sample [9];
public:

	/** constructor */
	Space (void);

	/** conta quantas possibilidades existem para uma unidade
	@return (unsigned int) quantidade de possibilidades */
	unsigned int count_chances (void);

	/** set possibility
	@param index (unsigned int) indice da possibilidade descartada
	@result (bool) indica se estava inativo e se foi realmente marcado */
	bool mark (unsigned int index);

	/** descarta uma possibilidade
	@param index (unsigned int) indice da possibilidade descartada
	@result (bool) indica se estava ativo e se foi realmente liberado */
	bool discard (unsigned int index);

	/** elege (fixa) uma possibilidade para uma unidade
	@param index (unsigned int) indice da possibilidade eleita */
	void choose (unsigned int index);

	/** obtém o estado de cada possibilidade para uma unidade
	@param index (unsigned int) indice de possibilidades
	@reurn (bool) estado desta possibilidade */
	bool is_possible (unsigned int index);

	/** obtém a possibilidade única (apenas se for única) para uma unidade
	@return (unsigned int) possibilidade única ou zero se houver mais de uma */
	unsigned int read (void);
};

Space::Space (void) {
	for (unsigned int item = 0; item < 9; item++) {
		sample[item] = true;
	}
}

unsigned int Space::count_chances (void) {
	unsigned int result = 0;
	for (unsigned int item = 0; item < 9; item++) {
		if (sample[item] == true) result++;
	}
	return result;
}

bool Space::discard (unsigned int index) {
	index --;
	if (index >= 9) throw "Index out of range!";
	if (sample [index]) {
		sample [index] = false;
		return true;
	}
	return false;
}

bool Space::mark (unsigned int index) {
	index --;
	if (index >= 9) throw "Index out of range!";
	if (sample [index] == false) {
		sample [index] = true;
		return true;
	}
	return false;
}

void Space::choose (unsigned int index) {
	index --;
	if (index >= 9) throw "Index out of range!";
	for (unsigned int item = 0; item < 9; item++) {
		if (item != index) sample[item] = false;
	}
}

bool Space::is_possible (unsigned int index) {
	index --;
	if (index >= 9) throw "Index out of range!";
	return sample [index];
}

unsigned int Space::read (void) {
	unsigned int result = 0;
	for (unsigned int item = 0; item < 9; item++) {
		if (sample[item] == true) {
			if (result) return 0;
			result = item + 1;
		}
	}
	return result;
}

//**************************************************
// Position
//**************************************************

class Position {
	unsigned int row;
	unsigned int column;

public:
	/** constructor
	@param vrow (unsigned int) row index from 1 to 9
	@param vcolumn (unsigned int) column index from 1 to 9
	*/
	Position () {
		setRow(1);
		setColumn(1);
	}

	/** constructor
	@param vrow (unsigned int) row index from 1 to 9
	@param vcolumn (unsigned int) column index from 1 to 9
	*/
	Position (unsigned int vrow, unsigned int vcolumn) {
		setRow(vrow);
		setColumn(vcolumn);
	}

	/** set line
	@param value (unsigned int) row index from 1 to 9
	*/
	void setRow (unsigned int value) {
		if (value < 1 || value > 9) throw "Line out of range!";
		row = value;
	}

	/** set column
	@param value (unsigned int) column index from 1 to 9
	*/
	void setColumn (unsigned int value) {
		if (value < 1 || value > 9) throw "Line out of range!";
		column = value;
	}

	/** get line
	@return (unsigned int) row index from 1 to 9
	*/
	unsigned int getRow (void) {
		return row;
	}

	/** get column
	@return (unsigned int) column index from 1 to 9
	*/
	unsigned int getColumn (void) {
		return column;
	}

	/** compare
	@param vrow (unsigned int) row index from 1 to 9
	@param vcolumn (unsigned int) column index from 1 to 9
	@return (bool) true if equal
	*/
	bool equal (unsigned int vrow, unsigned int vcolumn) {
		return (row == vrow && column == vcolumn);
	}

	/** compare
	@param location (Position) location
	@return (bool) true if equal
	*/
	bool equal (Position location) {
		return (row == location.getRow() && column == location.getColumn());
	}

	/** converts row x column to linear index
	@return (unsigned int) linear position
	*/
	unsigned int position () {
		return (((row - 1) * 9) + column) -1;
	}
};

//**************************************************
// Domain
//**************************************************

class Domain {
	Position list [9];
	unsigned int current;
public:
	Domain & fillRow (Position location);
	Domain & fillColumn (Position location);
	Domain & fillQuadrant (Position location);

	Position * getPosition (unsigned int index) {
		if (index > 8) throw "Domain index out of range!";
		return & list[index];
	}
	bool has_next () {
		return current < 9;
	}
	Position * getPosition () {
		if (has_next() == false) throw "Domain index out of range!";
		Position * item = & list[current];
		current++;
		return item;
	}
};

Domain & Domain::fillRow (Position location) {
	unsigned int index = 0;
	for (unsigned int column = 1; column <= 9; column++) {
		list[index].setRow(location.getRow());
		list[index].setColumn(column);
		index++;
	}
	current = 0;
	return * this;
}

Domain & Domain::fillColumn (Position location) {
	unsigned int index = 0;
	for (unsigned int row = 1; row <= 9; row++) {
		list[index].setColumn(location.getColumn());
		list[index].setRow(row);
		index++;
	}
	current = 0;
	return * this;
}

Domain & Domain::fillQuadrant (Position location) {
	unsigned int index = 0;
	unsigned int baseRow = base(location.getRow());
	unsigned int baseColumn = base(location.getColumn());
	for (unsigned int row = baseRow; row < baseRow + 3; row++) {
		for (unsigned int column =  baseColumn; column < baseColumn + 3; column++) {
			list[index].setRow(row);
			list[index].setColumn(column);
			index++;
		}
	}
	current = 0;
	return * this;
}

//**************************************************
// Board
//**************************************************

/** The board is a set of 9 x 9 cells, where each cell has a set of possibilities */

class Board {
private:
	/** The board is a set of 9 x 9 cells */
	Space cell [81];
public:

	/** conta quantas possibilidades existem para uma peca
	@param location (Position) cell position
	@return (unsigned int) quantidade de possibilidades */
	unsigned int count_chances (Position location);

	/** descarta uma possibilidade
	@param location (Position) cell position
	@param index (unsigned int) indice da possibilidade descartada
	@result (bool) indica se estava ativo e se foi realmente liberado */
	bool discard (Position location, unsigned int index);

	/** elege (fixa) uma possibilidade
	@param location (Position) cell position
	@param index (unsigned int) indice da possibilidade eleita */
	void choose (Position location, unsigned int index);

	/** obtém o estado de cada possibilidade
	@param location (Position) cell position
	@param index (unsigned int) indice de possibilidades
	@reurn (bool) estado desta possibilidade */
	bool is_possible (Position location, unsigned int index);

	/** obtém a possibilidade única (apenas se for única)
	@param location (Position) cell position
	@return (unsigned int) possibilidade única ou zero se houver mais de uma */
	unsigned int read (Position location);

	/** Regra 1 EXCLUSAO: para uma casa resolvida, descartar o valor desta casa de
	todas as possibilidades do resto dos domínios desta casa.
	@param location (Position) cell position
	@param index (unsigned int) possibilidade eleita */
	void regra1_exclusao (Position location, unsigned int index);

	/** Regra 2 RESTRICAO: se as possibilidades de uma casa chegarem a uma opção,
	esta única possibilidade é o valor resolvido para esta casa.
	@param location (Position) cell position
	@param index (unsigned int) possibilidade eleita */
	void regra2_restricao (Position location);

	/** Rule 3 SINGLE ISOLATION: if one of the possibilities of a cell is the
	only possibility within a domain it becomes the final value for this cell.
	@param location (Position) cell position
	@result (bool) indica se fez ou não algum isolamento */
	bool rule3_isolation_single (Position location);

	/** Rule 3 PAIR ISOLATION: if there is only 2 cells with 2 possible values
	and this possibilities do not exist in the rest of the domain this 2 values
	becomes the only possibilities for this 2 cells.
	@param location (Position) cell position
	@result (bool) indica se fez ou não algum isolamento */
	bool rule3_isolation_pair (Position location);
	bool rule3_isolation_pair (Position location, Domain domain);

	bool brute_force (Position location);
	bool brute_force (Position location, Domain domain);

	/** Rule 3 TRIPLE ISOLATION: if there is only 3 cells with 3 possible values
	and this possibilities do not exist in the rest of the domain this 3 values
	becomes the only possibilities for this 3 cells.
	@param location (Position) cell position
	@result (bool) indica se fez ou não algum isolamento */
	bool rule3_isolation_triple (Position location);

	/** Regra 4 PROJECAO: dentro de um quadrante, se um valor possível for restrito
	a uma única linha, remover esta possibilidade desta linha em outros quadrantes.
	Dentro de um quadrante, se um valor possível for restrito a uma única coluna,
	remover esta possibilidade desta coluna em outros quadrantes.
	@param location (Position) cell position
	@result (bool) indica se fez ou não alguma projeção */
	bool regra4_projecao (Position location);


	bool clear (Position location, Position location_to);

	bool same_outcomes (Position location, Position location_to);

	bool regra5_dois_pares (Position location);

	bool regra6_pair_isolation (Position location);

	void show (void);

	void copyTo (Position & location, Space & pcell);

};

void Board::copyTo (Position & location, Space & pcell) {
	for (unsigned int chance = 1; chance <=9; chance++ ) {
		if (is_possible(location, chance)) {
			pcell.mark(chance);
		} else {
			pcell.discard(chance);
		}
	}
}

bool rule3_isolation_pair (Board & board, Position & location, Domain & domain, unsigned int chance) {
	bool result = false;
	unsigned int count = 0;
	Position * other;
	while (domain.has_next()) {
		Position * current = domain.getPosition();
		if (board.is_possible(*current, chance)) {
			count++;
			other = current;
		}
	}
	return result;
}

bool Board::rule3_isolation_pair (Position location, Domain domain) {
	bool result = false;
	for (unsigned int chance = 1; chance <=9; chance++ ) {
		if (is_possible(location, chance)) {
			unsigned int count = 0;
			Position * other;
			while (domain.has_next()) {
				Position * current = domain.getPosition();
				if (is_possible(*current, chance)) {
					count++;
					other = current;
				}
			}
		}
	}
	return result;
}

bool Board::rule3_isolation_pair (Position location) {
	bool result = false;
	if (count_chances(location) == 2) {
		Domain domain;
		if (rule3_isolation_pair(location, domain.fillRow(location))) result = true;
		if (rule3_isolation_pair(location, domain.fillColumn(location))) result = true;
		if (rule3_isolation_pair(location, domain.fillQuadrant(location))) result = true;
	}
	return result;
}

bool Board::brute_force (Position location, Domain domain) {
	bool result = false;

	Space cell[8];
	unsigned int offset = 0;
	for (unsigned int index = 0; index < 9; index++ ) {
		Position * current = domain.getPosition();
		if (location.equal (*current)) offset = index;
		copyTo(*current, cell[index]);
	}

	for (unsigned int chance = 1; chance <=9; chance++ ) {
		if (is_possible(location, chance)) {
			unsigned int count = 0;
			Position * other;
			while (domain.has_next()) {
				Position * current = domain.getPosition();
				if (is_possible(*current, chance)) {
					count++;
					other = current;
				}
			}
		}
	}
	return result;
}


bool Board::brute_force (Position location) {
	bool result = false;
	if (count_chances(location) == 2) {
		Domain domain;
		if (brute_force(location, domain.fillRow(location))) result = true;
		if (brute_force(location, domain.fillColumn(location))) result = true;
		if (brute_force(location, domain.fillQuadrant(location))) result = true;
	}
	return result;
}

unsigned int Board::count_chances (Position location) {
	return cell [location.position()].count_chances ();
}

bool Board::discard (Position location, unsigned int index) {
	// restringe as possibilidades desta peça
	if (cell [location.position()].discard (index)) {
		// chama a regra 2 que verifica se as possibilidades chegaram a 1
		regra2_restricao (location);
		// indica que liberou uma possibilidade
		return true;
	}
	return false;
}

void Board::choose (Position location, unsigned int index) {
	// limita uma peca
	cell [location.position()].choose (index);
	// chama a regra 1 que descartar esta possibilidade do resto do domínio
	regra1_exclusao (location, index);
}

bool Board::is_possible (Position location, unsigned int index) {
	// obtém o estado de uma possibilidade de uma peça
	return cell [location.position()].is_possible (index);
}

unsigned int Board::read (Position location) {
	// obtém a possibilidade única para uma peça ou devolve zero (possiblidades múltiplas)
	return cell [location.position()].read ();
}

void Board::regra1_exclusao (Position location, unsigned int index) {
	// exclui das linhas
	for (unsigned int row = 1; row <=9; row++) if (row != location.getRow()) discard (Position(row, location.getColumn()), index);
	// exclui das colunas
	for (unsigned int column = 1; column <=9; column++) if (column != location.getColumn()) discard (Position(location.getRow(), column), index);
	// exclui das sub-matrizes
	unsigned int baseRow = base (location.getRow());
	unsigned int baseColumn = base (location.getColumn());
	for (unsigned int row = 0; row < 3; row++) {
		unsigned int curRow = baseRow + row;
		for (unsigned int column = 0; column < 3; column++) {
			unsigned int curColumn = baseColumn + column;
			if (curColumn != location.getColumn() or curRow != location.getRow()) {
				discard (Position(curRow, curColumn), index);
			}
		}
	}
}

void Board::regra2_restricao (Position location) {
	// esta peça deve ter apenas uma possibilidade
	if (count_chances (location) == 1) {
		// obtém a possibilidade única
		unsigned int index = read (location);
		// chama a regra 1 que descartar esta possibilidade do resto do domínio
		regra1_exclusao (location, index);
	}
}

bool Board::rule3_isolation_single (Position location) {
	// verifica se quantidade de possibilidades é maior que 1
	if (count_chances (location) > 1) {

		// passa por todas as possibilidades de uma peça
		for (unsigned int index = 1; index <=9; index++) {
			if (is_possible (location, index)) {

				// procura esta possibilidade no domínio vertical (location.getRow())
				bool dominio = true;
				for (unsigned int row = 1; row <=9; row++) {
					if (row != location.getRow()) {
						if (is_possible (Position(row, location.getColumn()), index)) {
							dominio = false;
							break;
						}
					}
				}
				// procura esta possibilidade no domínio horizontal (coluna)
				if (dominio == false) {
					dominio = true;
					for (unsigned int column = 1; column <=9; column++) {
						if (column != location.getColumn()) {
							if (is_possible (Position(location.getRow(), column), index)) {
								dominio = false;
								break;
							}
						}
					}
				}
				// procura esta possibilidade no domínio sub-matriz
				if (dominio == false) {
					dominio = true;
					unsigned int baseRow = base (location.getRow());
					unsigned int baseColumn = base (location.getColumn());
					for (unsigned int row = 0; row < 3; row++) {
						unsigned int curRow = baseRow + row;
						for (unsigned int column = 0; column < 3; column++) {
							unsigned int curColumn = baseColumn + column;
							if (curColumn != location.getColumn() or curRow != location.getRow()) {
								if (is_possible (Position(curRow, curColumn), index)) {
									dominio = false;
									break;
								}
							}
						}
					}
				}
				// esta é a única possiblidade
				if (dominio) {
					cout << "ISOLAMENTO linha " << location.getRow() << " coluna " << location.getColumn() << "\n";
					choose (location, index);
					return true;
				}
			}
		}
	}
	return false;
}

bool Board::regra4_projecao (Position location) {
	bool result = false;

	// verifica se quantidade de possibilidades é maior que 1
	if (count_chances (location) > 1) {

		// passa por todas as possibilidades de uma peça
		for (unsigned int index = 1; index <=9; index++) {
			if (is_possible (location, index)) {

				// procura esta possibilidade no domínio sub-matriz horizontal
				bool dominio = true;
				unsigned int baseRow = base (location.getRow());
				unsigned int baseColumn = base (location.getColumn());
				for (unsigned int row = 0; row < 3 && (dominio); row++) {
					unsigned int curRow = baseRow + row;
					if (curRow != location.getRow()) {
						for (unsigned int column = 0; column < 3 && (dominio); column++) {
							unsigned int curColumn = baseColumn + column;
							if (is_possible (Position(curRow, curColumn), index)) {
								dominio = false;
							}
						}
					}
				}

				// apaga estas possibilidades nesta location.getRow() em outras submatrizes
				if (dominio) {
					for (unsigned int column = 1; column <= 9; column++) {
						unsigned int matrizcoluna = base (column);
						if (matrizcoluna != baseColumn) {
							if (discard (Position(location.getRow(), column), index)) {
								result = true;
							}
						}
					}
				}

				// procura esta possibilidade no domínio sub-matriz vertical
				dominio = true;
				for (unsigned int row = 0; row < 3 && (dominio); row++) {
					unsigned int curRow = baseRow + row;
					for (unsigned int column = 0; column < 3 && (dominio); column++) {
						unsigned int curColumn = baseColumn + column;
						if (curColumn != location.getColumn()) {
							if (is_possible (Position(curRow, curColumn), index)) {
								dominio = false;
							}
						}
					}
				}

				// apaga estas possibilidades desta coluna em outras submatrizes
				if (dominio) {
					for (unsigned int row = 1; row <= 9; row++) {
						unsigned int matrizlinha = base (row);
						if (matrizlinha != baseRow) {
							if (discard (Position(row, location.getColumn()), index)) {
								result = true;
							}
						}
					}
				}
			}
		}
	}
	if (result) {
		cout << "PROJECAO linha " << location.getRow() << " coluna " << location.getColumn() << "\n";
	}
	return result;
}

bool Board::clear (Position location, Position location_to) {
	bool result = false;
	if (location.getRow() != location_to.getRow() || location.getColumn() != location_to.getColumn()) {
		for (unsigned int index = 1; index <= 9; index++) {
			if (is_possible (location, index)) {
				if (is_possible (Position(location_to.getRow(), location_to.getColumn()), index)) {
					discard(Position(location_to.getRow(), location_to.getColumn()), index);
					result = true;
				}
			}
		}
	}
	return result;
}

bool Board::same_outcomes (Position location, Position location_to) {
	if (location.getRow() != location_to.getRow() || location.getColumn() != location_to.getColumn()) {
		for (unsigned int index = 1; index <= 9; index++) {
			if (is_possible (location, index)) {
				if (is_possible (Position(location_to.getRow(), location_to.getColumn()), index) == false) {
					return false;
				}
			} else {
				if (is_possible (Position(location_to.getRow(), location_to.getColumn()), index)) {
					return false;
				}
			}
		}
		return true;
	}
	return false;
}

bool Board::regra5_dois_pares (Position location) {
	bool result = false;

	// verifica se quantidade de possibilidades é maior que 1
	int chance = count_chances (location);
	if (chance == 2) {

		// passa por todas as possibilidades horizontais
		for (unsigned int linha_to = 1; linha_to <= 9; linha_to++) {
			if (location.getRow() != linha_to) {
				int chance_to = count_chances (Position(linha_to, location.getColumn()));
				if (chance == chance_to) {
					if (same_outcomes(location, Position(linha_to, location.getColumn()))) {
						bool one = false;
						for (unsigned int linha_x = 1; linha_x <= 9; linha_x++) {
							if (location.getRow() != linha_x && linha_to != linha_x) {
								if (clear (location, Position(linha_x, location.getColumn()))) {
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
		for (unsigned int coluna_to = 1; coluna_to <= 9; coluna_to++) {
			if (location.getColumn() != coluna_to) {
				int chance_to = count_chances (Position(location.getRow(), coluna_to));
				if (chance == chance_to) {
					if (same_outcomes(location, Position(location.getRow(), coluna_to))) {
						bool one = false;
						for (unsigned int coluna_x = 1; coluna_x <= 9; coluna_x++) {
							if (location.getColumn() != coluna_x && coluna_to != coluna_x) {
								if (clear (location, Position(location.getRow(), coluna_x))) {
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
		unsigned int col_base = base(location.getColumn());
		unsigned int lin_base = base(location.getRow());
		for (unsigned int coluna_to = col_base; coluna_to <= (col_base + 2); coluna_to++) {
			for (unsigned int linha_to = lin_base; linha_to <= (lin_base + 2); linha_to++) {
				if (location.getColumn() != coluna_to || location.getRow() != linha_to) {
					int chance_to = count_chances (Position(linha_to, coluna_to));
					if (chance == chance_to) {
						if (same_outcomes(location, Position(linha_to, coluna_to))) {
							bool one = false;
							for (unsigned int coluna_x = col_base; coluna_x <= (col_base + 2); coluna_x++) {
								for (unsigned int linha_x = lin_base; linha_x <= (lin_base + 2); linha_x++) {
									if ((location.getColumn() != coluna_x || location.getRow() != linha_x) && (coluna_to != coluna_x || linha_to != linha_x)) {
										if (clear (location, Position(linha_x, coluna_x))) {
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
		cout << "SUFICIENCIA linha " << location.getRow() << " coluna " << location.getColumn() << "\n";
	}
	return result;
}

void Board::show (void) {
	for (unsigned int row = 1; row <=9; row++) {
		if (row > 1)
			if ((row-1)%3 == 0) {
				cout << "==========|===========|===========#===========|===========|===========#===========|===========|===========\n";
			} else {
				cout << "----------|-----------|-----------#-----------|-----------|-----------#-----------|-----------|-----------\n";
			}
		for (unsigned int column = 1; column <=9; column++) {
			if (column > 1) if ((column-1)%3 == 0) cout << " # "; else cout << " | ";
			unsigned int c = 0;
			for (unsigned int k = 1; k<=9; k++) {
				if (is_possible (Position(row, column), k) == false) {
					c++;
				}
			}
			c = c / 2;
			for (unsigned int k = 0; k<c; k++) {
				cout << " ";
			}
			for (unsigned int k = 1; k<=9; k++) {
				if (is_possible (Position(row, column), k)) {
					cout << k;
					c++;
				}
			}
			for (; c < 9; c++) {
				cout << " ";
			}
		}
		cout << "\n";
	}
}

int main( void ) {
	Board x;

	/* x.choose (1, 6, 5);
	x.choose (1, 8, 7);
	x.choose (2, 4, 3);
	x.choose (2, 8, 6);
	x.choose (3, 1, 7);
	x.choose (3, 2, 6);
	x.choose (3, 5, 2);
	x.choose (3, 6, 9);
	x.choose (4, 1, 8);
	x.choose (4, 5, 9);
	x.choose (4, 8, 4);
	x.choose (6, 2, 5);
	x.choose (6, 3, 6);
	x.choose (6, 6, 3);
	x.choose (6, 7, 8);
	x.choose (7, 1, 4);
	x.choose (7, 5, 5);
	x.choose (7, 6, 8);
	x.choose (7, 7, 9);
	x.choose (8, 1, 9);
	x.choose (8, 9, 7);
	x.choose (9, 3, 3);
	x.choose (9, 7, 2); */

	// MUITO DIFICIL
	/* x.choose (1, 1, 9);
	x.choose (1, 9, 7);
	x.choose (2, 3, 4);
	x.choose (2, 8, 1);
	x.choose (3, 4, 7);
	x.choose (3, 8, 9);
	x.choose (3, 9, 2);
	x.choose (4, 1, 7);
	x.choose (4, 2, 5);
	x.choose (4, 6, 8);
	x.choose (5, 1, 2);
	x.choose (6, 4, 1);
	x.choose (6, 5, 7);
	x.choose (6, 8, 2);
	x.choose (7, 1, 5);
	x.choose (7, 5, 2);
	x.choose (8, 1, 6);
	x.choose (8, 8, 7);
	x.choose (9, 6, 4);
	x.choose (9, 5, 6);
	x.choose (9, 8, 3);
	x.choose (9, 9, 1); */

	// O MAIS DIFICIL DO MUNDO
	x.choose (Position(1, 1), 8);
	x.choose (Position(2, 3), 3);
	x.choose (Position(2, 4), 6);
	x.choose (Position(3, 2), 7);
	x.choose (Position(3, 5), 9);
	x.choose (Position(3, 7), 2);
	x.choose (Position(4, 2), 5);
	x.choose (Position(4, 6), 7);
	x.choose (Position(5, 5), 4);
	x.choose (Position(5, 6), 5);
	x.choose (Position(5, 7), 7);
	x.choose (Position(6, 4), 1);
	x.choose (Position(6, 8), 3);
	x.choose (Position(7, 3), 1);
	x.choose (Position(7, 8), 6);
	x.choose (Position(7, 9), 8);
	x.choose (Position(8, 3), 8);
	x.choose (Position(8, 4), 5);
	x.choose (Position(8, 8), 1);
	x.choose (Position(9, 2), 9);
	x.choose (Position(9, 7), 4);

	// cout << "START\n";
	// x.show();

	// verifica todas as regras até que tenha falhado 2 vezes
	int teste = 3;
	while (teste) {
		for (unsigned int row = 1; row <=9; row++) {
			for (unsigned int column = 1; column <=9; column++) {
				if (x.rule3_isolation_single (Position(row, column))) {
					teste = 8;
				} else if (x.regra4_projecao (Position(row, column))) {
					teste = 8;
				} else if (x.regra5_dois_pares (Position(row, column))) {
				 	teste = 8;
				}
			}
		}
		if (teste) teste--;
	}

	cout << "OUT\n";
	x.show();
	return 0;
}
