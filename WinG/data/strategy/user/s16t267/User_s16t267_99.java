package prog;

import sys.game.GameBoard;
import sys.game.GameHand;
import sys.game.GamePlayer;
import sys.game.GameState;
import sys.struct.GogoHand;
import sys.user.GogoCompSub;

public class User_s16t267_99 extends GogoCompSub {

//====================================================================
//  コンストラクタ
//====================================================================

    public User_s16t267_99(int id, GamePlayer player) {
        //----------------------------------------------------------------
        // WinG-CSで実行するときに使うコンストラクタです。変更しないでください。
        //----------------------------------------------------------------
        super(player);
        name = Integer.toString(id);
    }

    public User_s16t267_99(GamePlayer player) {
        super(player);
        name = "s16t267";    // プログラマが入力
    }

//--------------------------------------------------------------------
//  コンピュータの着手
//--------------------------------------------------------------------

    public synchronized GameHand calc_hand(GameState state, GameHand hand) {
        theState = state;
        theBoard = state.board;
        lastHand = hand;

        //--  置石チェック
        init_values(theState, theBoard);

        //--  評価値の計算
        calc_values(theState, theBoard);
        // 先手後手、取石数、手数(序盤・中盤・終盤)で評価関数を変える

        //--  着手の決定
        return deside_hand();

    } 

//----------------------------------------------------------------
//  置石チェック
//----------------------------------------------------------------

    public void init_values(GameState prev, GameBoard board) {
        this.size = board.SX;
        values = new int[size][size];

        for ( int i = 0; i < size; i++ ) {
            for ( int j = 0; j < size; j++ ) {
                if ( board.get_cell(i, j) != board.SPACE ) {    // すでに埋まっているマス
                    values[i][j] = -2;
                } else {
                    if ( values[i][j] == -2 ) { values[i][j] = 0; }
                }
            }
        }
    }

//----------------------------------------------------------------
//  評価値の計算
//----------------------------------------------------------------

    public void calc_values(GameState prev, GameBoard board) {
        int [][] cell = board.get_cell_all();   // 盤面情報
        int mycolor;                            // 自分の石の色
        mycolor = role;                         // プレイヤーの色　-1or1？

        //--  各マスの評価値
        for ( int i = 0; i < size; i++ ) {
            for ( int j = 0; j < size; j++ ) {
                // 埋まっているマスはスルー
                if ( values[i][j] == -2 ) { continue; }
                //--  適当な評価の例
                // 相手の五連を崩す → 1000;
                if ( check_run(cell, mycolor*-1, i, j, 5) ) {
                    values[i][j] = 1000;
                    continue;
                }
                // 勝利(五取) → 950;
                if ( check_rem(cell, mycolor*-1, i, j) ) {
                    values[i][j] = 950;
                    continue;
                }
                // 勝利(五連) → 900;
                if ( check_run(cell, mycolor, i, j, 5) ) {
                    values[i][j] = 900;
                    continue;
                }
                // 敗北阻止(五取) → 850;
                if ( check_rem(cell, mycolor, i, j) )  {
                    values[i][j] = 850;
                    continue;
                }
                // 敗北阻止(五連) → 800;
                if ( check_run(cell, mycolor*-1, i, j, 5) ) {
                    values[i][j] = 800;
                    continue;
                }
                // 相手の四連を止める → 700;
                if ( check_run(cell, mycolor*-1, i, j, 4) ) {
                    values[i][j] = 700;
                    continue;
                }
                // 自分の四連を作る → 600;
                if ( check_run(cell, mycolor, i, j, 4) ) {
                    values[i][j] = 600;
                    continue;
                }
                // 相手の三連を防ぐ → 500;
                if ( check_run(cell, mycolor*-1, i, j, 3) ) {
                    values[i][j] = 500;
                    continue;
                }
                // 自分の三連を作る → 400;
                if ( check_run(cell, mycolor, i, j, 3) ) { values[i][j] = 400; }
                // 三々の禁じ手は打たない → -1
                // 相手の石を取る → 300;
                if ( check_rem(cell, mycolor, i, j) ) { values[i][j] = 300; }
                // 自分の石を守る → 200;
                if ( check_rem(cell, mycolor*-1, i, j) ) { values[i][j] = 200; }
                // ランダム
                if ( values[i][j] == 0) {
                    int aaa = (int) Math.round(Math.random() * 15);
                    //System.out.printf("%d ", aaa);
                    if ( values[i][j] < aaa) { values[i][j] = aaa; }
                }
                // if ( values[i][j] < 16 ) {
                //     near_random(cell, values, mycolor*1, i, j);
                // }
                
                // 四々や四三の判定
                // 飛び三や飛び四の判定
                // 三をどちらで止めるか
            }
        }
        //output(values);
    }

//----------------------------------------------------------------
//  任意乱数生成
//----------------------------------------------------------------

    void near_random(int[][] board, int[][] values, int color, int x, int y) {
        if ( x-1 < 0 || y-1 < 0 || x+1 >= size || y+1 >= size ) {
           
        } else {
            if ( board[x-1][y] == color && board[x+1][y] == color ) {
                values[x][y] = 100;
            } else if ( board[x][y-1] == color && board[x][y+1] == color ) {
                values[x][y] = 100;
            } else if ( board[x-1][y-1] == color && board[x+1][y+1] == color ) {
                values[x][y] = 100;
            } else if ( board[x-1][y+1] == color && board[x+1][y-1] == color ) {
                values[x][y] = 100;
            } 
        }
        
    } 

//----------------------------------------------------------------
//  連の全周チェック
//----------------------------------------------------------------

    boolean check_run(int[][] board, int color, int i, int j, int len) {
        for ( int dx = -1; dx <= 1; dx++ ) {
            for ( int dy = -1; dy <= 1; dy++ ) {
                if ( dx == 0 && dy == 0 ) { continue; }
                if ( check_run_dir(board, color, i, j, dx, dy, len) ) { return true; }
            }
        }
        return false;
    }

//----------------------------------------------------------------
//  連の方向チェック(止連・端連・長連も含む、飛びは無視)
//----------------------------------------------------------------

    boolean check_run_dir(int[][] board, int color, int i, int j, int dx, int dy, int len) {
        for ( int k = 1; k < len; k++ ) {
            int x = i+k*dx;
            int y = j+k*dy;
            if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }   // 盤面外
            if ( board[i+k*dx][j+k*dy] != color ) { return false; }             // プレイヤーの色と一致してない
        }
        return true;
    }

//----------------------------------------------------------------
//  取の全周チェック(ダブルの判定は無し)
//----------------------------------------------------------------

    boolean check_rem(int [][] board, int color, int i, int j) {
        for ( int dx = -1; dx <= 1; dx++ ) {
            for ( int dy = -1; dy <= 1; dy++ ) {
                if ( dx == 0 && dy == 0 ) { continue; }
                if ( check_rem_dir(board, color, i, j, dx, dy) ) { return true; }
            }
        }
        return false;
    }

//----------------------------------------------------------------
//  取の方向チェック
//----------------------------------------------------------------

    boolean check_rem_dir(int[][] board, int color, int i, int j, int dx, int dy) {
        int len = 3;
        for ( int k = 1; k <= len; k++ ) {
            int x = i+k*dx;
            int y = j+k*dy;
            if ( x < 0 || y < 0 || x >= size || y >= size ) { return false; }   // 盤面外
            if ( board[i+k*dx][j+k*dy] != color ) { return false; }             // プレイヤーの色と一致してない
            if ( k == len-1 ) { color *= -1; }
        }
        return true;
    }
//----------------------------------------------------------------
//  着手の決定
//----------------------------------------------------------------

    public GameHand deside_hand() {
        GogoHand hand = new GogoHand();
        hand.set_hand(0, 0);  // 左上をデフォルトのマスとする
        int value = -1;       // 評価値のデフォルト
        
        //--  評価値が最大となるマス
        for (int i = 0; i < size; i++ ) {
            for (int j = 0; j < size; j++ ) {
                if ( value < values[i][j]) {
                    hand.set_hand(i, j);
                    value = values[i][j];
                }
            }
        }
        return hand;
    }

//----------------------------------------------------------------
//  評価値のデバッグ出力
//----------------------------------------------------------------

    void output(int[][] values) {
        for (int i = 0; i < size; i++ ) {
            for (int j = 0; j < size; j++ ) {
                System.out.printf("%3d ", values[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
