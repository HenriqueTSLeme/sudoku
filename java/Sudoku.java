public class Sudoku {
    public static void main (String[] param) throws Exception {
        Board board = new Board();

        // O MAIS DIFICIL DO MUNDO
        /*board.choose (new Position(1, 1), 8);
        board.choose (new Position(2, 3), 3);
        board.choose (new Position(2, 4), 6);
        board.choose (new Position(3, 2), 7);
        board.choose (new Position(3, 5), 9);
        board.choose (new Position(3, 7), 2);
        board.choose (new Position(4, 2), 5);
        board.choose (new Position(4, 6), 7);
        board.choose (new Position(5, 5), 4);
        board.choose (new Position(5, 6), 5);
        board.choose (new Position(5, 7), 7);
        board.choose (new Position(6, 4), 1);
        board.choose (new Position(6, 8), 3);
        board.choose (new Position(7, 3), 1);
        board.choose (new Position(7, 8), 6);
        board.choose (new Position(7, 9), 8);
        board.choose (new Position(8, 3), 8);
        board.choose (new Position(8, 4), 5);
        board.choose (new Position(8, 8), 1);
        board.choose (new Position(9, 2), 9);
        board.choose (new Position(9, 7), 4);*/

        // O MAIS DIFICIL DO MUNDO
        board.choose (new Position(1, 7), 1);
        board.choose (new Position(1, 8), 3);
        board.choose (new Position(2, 1), 7);
        board.choose (new Position(2, 8), 9);
        board.choose (new Position(3, 1), 2);
        board.choose (new Position(3, 3), 9);
        board.choose (new Position(3, 4), 5);
        board.choose (new Position(3, 9), 4);
        board.choose (new Position(4, 1), 9);
        board.choose (new Position(4, 2), 2);
        board.choose (new Position(4, 5), 4);
        board.choose (new Position(5, 6), 6);
        board.choose (new Position(5, 7), 3);
        board.choose (new Position(5, 8), 4);
        board.choose (new Position(6, 2), 7);
        board.choose (new Position(6, 6), 1);
        board.choose (new Position(7, 6), 4);
        board.choose (new Position(7, 7), 6);
        board.choose (new Position(8, 1), 3);
        board.choose (new Position(9, 1), 5);
        board.choose (new Position(9, 3), 8);
        board.choose (new Position(9, 4), 9);
        board.choose (new Position(9, 9), 7);

        System.out.println("START\n");
        board.show();

        // verifica todas as regras até que tenha falhado 2 vezes
        int teste = 3;
        while (teste > 0) {
            for (int row = 1; row <=9; row++) {
                for (int column = 1; column <=9; column++) {
                    Position other = new Position(row, column);
                    if (board.rule3_isolation_single (other)) {
                        teste = 3;
                    } else if (board.rule4_confinement_ortogonal (other)) {
                        teste = 3;
                    } else if (board.rule4_confinement_quadrant (other)) {
                        teste = 3;
                    } else if (board.regra5_dois_pares (other)) {
                        teste = 3;
                    }
                }
            }
            if (teste > 0) teste--;
        }

        System.out.println("END\n");
        board.show();
    }
}