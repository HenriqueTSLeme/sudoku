public class Sudoku {
    public static void main (String[] param) throws Exception {
        Board x = new Board();

        // O MAIS DIFICIL DO MUNDO
        /*x.choose (new Position(1, 1), 8);
        x.choose (new Position(2, 3), 3);
        x.choose (new Position(2, 4), 6);
        x.choose (new Position(3, 2), 7);
        x.choose (new Position(3, 5), 9);
        x.choose (new Position(3, 7), 2);
        x.choose (new Position(4, 2), 5);
        x.choose (new Position(4, 6), 7);
        x.choose (new Position(5, 5), 4);
        x.choose (new Position(5, 6), 5);
        x.choose (new Position(5, 7), 7);
        x.choose (new Position(6, 4), 1);
        x.choose (new Position(6, 8), 3);
        x.choose (new Position(7, 3), 1);
        x.choose (new Position(7, 8), 6);
        x.choose (new Position(7, 9), 8);
        x.choose (new Position(8, 3), 8);
        x.choose (new Position(8, 4), 5);
        x.choose (new Position(8, 8), 1);
        x.choose (new Position(9, 2), 9);
        x.choose (new Position(9, 7), 4);*/

        // O MAIS DIFICIL DO MUNDO
        x.choose (new Position(1, 7), 1);
        x.choose (new Position(1, 8), 3);
        x.choose (new Position(2, 1), 7);
        x.choose (new Position(2, 8), 9);
        x.choose (new Position(3, 1), 2);
        x.choose (new Position(3, 3), 9);
        x.choose (new Position(3, 4), 5);
        x.choose (new Position(3, 9), 4);
        x.choose (new Position(4, 1), 9);
        x.choose (new Position(4, 2), 2);
        x.choose (new Position(4, 5), 4);
        x.choose (new Position(5, 6), 6);
        x.choose (new Position(5, 7), 3);
        x.choose (new Position(5, 8), 4);
        x.choose (new Position(6, 2), 7);
        x.choose (new Position(6, 6), 1);
        x.choose (new Position(7, 6), 4);
        x.choose (new Position(7, 7), 6);
        x.choose (new Position(8, 1), 3);
        x.choose (new Position(9, 1), 5);
        x.choose (new Position(9, 3), 8);
        x.choose (new Position(9, 4), 9);
        x.choose (new Position(9, 9), 7);

        System.out.println("START\n");
        x.show();

        // verifica todas as regras até que tenha falhado 2 vezes
        int teste = 3;
        while (teste > 0) {
            for (int row = 1; row <=9; row++) {
                for (int column = 1; column <=9; column++) {
                    if (x.rule3_isolation_single (new Position(row, column))) {
                        teste = 8;
                    } else if (x.regra4_projecao (new Position(row, column))) {
                        teste = 8;
                    } else if (x.regra5_dois_pares (new Position(row, column))) {
                        teste = 8;
                    }
                }
            }
            if (teste > 0) teste--;
        }

        System.out.println("START\n");
        x.show();
    }
}