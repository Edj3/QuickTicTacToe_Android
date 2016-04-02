package com.mannmade.quicktictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class QuickTicTacToeMain extends AppCompatActivity {
    public static int size = 0;
    public static int currentPlayer = 1;
    public static TextView playerTurn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_main);
        //actionbar setup
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setLogo(R.mipmap.ic_quick_tictactoe);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_quick_tictactoe);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        final TableLayout board = (TableLayout) findViewById(R.id.board);
        playerTurn = (TextView) findViewById(R.id.player_turn);
        //ensure currentPlayer resets to 1
        currentPlayer = 1;

        AlertDialog.Builder startGameAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
        final EditText size_prompt = (EditText) this.getLayoutInflater().inflate(R.layout.size_prompt, null);
        startGameAlert.setView(size_prompt);
        startGameAlert.setTitle(R.string.choose_board);
        startGameAlert.setMessage(R.string.choose_board_message);
        startGameAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = size_prompt.getText().toString();
                //handle blank entry
                if(userInput.equals("")){
                    size = 0;
                }else{
                    size = Integer.valueOf(size_prompt.getText().toString());
                }

                //If user enters negative number or 0 or blank, default to 3 (minimum best game)
                if(size <= 0){
                    size = 3;
                }

                //If user enters number greater than 8, default to 8 (maximum best game)
                if(size > 8){
                    size = 8;
                }
                setUpBoard(board);
                if(playerTurn != null){
                    playerTurn.setText(getResources().getString(R.string.p1_go));
                }
                dialog.cancel();
            }
        });
        startGameAlert.show();
    }

    private void setUpBoard(TableLayout board){
        if(board != null){
            for(int i = 0; i < size; i++){
                board.addView(this.getLayoutInflater().inflate(R.layout.table_row, null));
                TableRow t = (TableRow) board.getChildAt(i);
                setUpBoardRow(t, board);
            }

        }
    }

    private void setUpBoardRow(TableRow t, final TableLayout board){
        for(int j = 0; j < size; j++){
            LinearLayout v = (LinearLayout) this.getLayoutInflater().inflate(R.layout.blank_view, null);
            t.addView(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.blank_view){
                       handlePlayerTurn(v, board);
                    }
                }
            });
        }
    }

    private void handlePlayerTurn(View v, TableLayout board){
        if(currentPlayer == 1){ //Player 1 is selecting (Set up player 2's next turn)
            ViewGroup parent = (ViewGroup) v.getParent();
            int index = parent.indexOfChild(v);
            parent.removeView(v);
            v = getLayoutInflater().inflate(R.layout.x_view, parent, false);
            parent.addView(v, index);
            currentPlayer = 2;
            if(playerTurn != null){
                playerTurn.setText(getResources().getString(R.string.p2_go));
            }
            determineWin(board);
        }else{ //player 2 is selecting (set up player 1s next turn)
            ViewGroup parent = (ViewGroup) v.getParent();
            int index = parent.indexOfChild(v);
            parent.removeView(v);
            v = getLayoutInflater().inflate(R.layout.o_view, parent, false);
            parent.addView(v, index);
            currentPlayer = 1;
            if(playerTurn != null){
                playerTurn.setText(getResources().getString(R.string.p1_go));
            }
            determineWin(board);
        }
    }

    private void determineWin(TableLayout board){
        //only check for draw while all other checks are false
        if(!checkRowWin(board) && !checkColumnWin(board) && !checkDiagonalRightToLeftWin(board) && !checkDiagonalLeftToRightWin(board)){
            checkForDraw(board);
        }
    }

    private boolean checkRowWin(TableLayout board){
        boolean result = false;
        for(int i = 0; i < size; i++){
            TableRow t = (TableRow) board.getChildAt(i);
            boolean xWon = true;
            boolean oWon = true;

            for(int j = 0; j < size; j++){
                if(t.getChildAt(j).getId() != R.id.x_view){
                    xWon = false;
                }
                if(t.getChildAt(j).getId() != R.id.o_view){
                    oWon = false;
                }
            }

            AlertDialog.Builder winAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
            winAlert.setMessage(R.string.thanks);
            winAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                    startActivity(getIntent());
                }
            });

            if(xWon){
                winAlert.setTitle(R.string.p1_victory);
                winAlert.show();
                result = (xWon || oWon);
                break;  //break after winning to stop loop from continuing
            }

            if(oWon){
                winAlert.setTitle(R.string.p2_victory);
                winAlert.show();
                result = (xWon || oWon);
                break;  //break after winning to stop loop from continuing
            }
        }
        return result;
    }

    private boolean checkColumnWin(TableLayout board){
        boolean result = false;
        for(int i = 0; i < size; i++){

            boolean xWon = true;
            boolean oWon = true;

            for(int j = 0; j < size; j++){
                TableRow t = (TableRow) board.getChildAt(j);
                if(t.getChildAt(i).getId() != R.id.x_view){
                    xWon = false;
                }
                if(t.getChildAt(i).getId() != R.id.o_view){
                    oWon = false;
                }
            }

            AlertDialog.Builder winAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
            winAlert.setMessage(R.string.thanks);
            winAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                    startActivity(getIntent()); //restart for next game
                }
            });

            if(xWon){
                winAlert.setTitle(R.string.p1_victory);
                winAlert.show();
                result = (xWon || oWon);
                break;  //break after winning to stop loop from continuing
            }

            if (oWon) {
                winAlert.setTitle(R.string.p2_victory);
                winAlert.show();
                result = (xWon || oWon);
                break; //break after winning to stop loop from continuing
            }
        }
        return result;
    }

    private boolean checkDiagonalLeftToRightWin(TableLayout board){
        boolean xWon = true;
        boolean oWon = true;

        for(int i = 0; i < size; i++){
            TableRow t = (TableRow) board.getChildAt(i);
            if(t.getChildAt(i).getId() != R.id.x_view){
                xWon = false;
            }
            if(t.getChildAt(i).getId() != R.id.o_view){
                oWon = false;
            }
        }

        AlertDialog.Builder winAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
        winAlert.setMessage(R.string.thanks);
        winAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                startActivity(getIntent()); //restart for next game
            }
        });

        if(xWon){
            winAlert.setTitle(R.string.p1_victory);
            winAlert.show();
        }

        if(oWon){
            winAlert.setTitle(R.string.p2_victory);
            winAlert.show();
        }

        return (xWon || oWon);
    }

    private boolean checkDiagonalRightToLeftWin(TableLayout board){
        boolean xWon = true;
        boolean oWon = true;

        for(int i = 0; i < size; i++){ //use last element (size - 1) and decrease by i
            TableRow t = (TableRow) board.getChildAt(i);
            if(t.getChildAt(size - 1 - i).getId() != R.id.x_view){
                xWon = false;
            }
            if(t.getChildAt(size - 1 - i).getId() != R.id.o_view){
                oWon = false;
            }
        }

        AlertDialog.Builder winAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
        winAlert.setMessage(R.string.thanks);
        winAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                startActivity(getIntent());
            }
        });

        if(xWon){
            winAlert.setTitle(R.string.p1_victory);
            winAlert.show();
        }

        if(oWon){
            winAlert.setTitle(R.string.p2_victory);
            winAlert.show();
        }

        return (xWon || oWon);
    }

    private void checkForDraw(TableLayout board){
        boolean draw = true;
        for(int i = 0; i < size; i++){
            TableRow t = (TableRow) board.getChildAt(i);
            for(int j = 0; j < size; j++){
                //if a blank view exists, then the game is not over
                if(t.getChildAt(j).getId() == R.id.blank_view){
                    draw = false;
                }
            }
        }

        AlertDialog.Builder drawAlert = new AlertDialog.Builder(QuickTicTacToeMain.this);
        drawAlert.setMessage(R.string.thanks);
        drawAlert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                startActivity(getIntent());
            }
        });

        if(draw){
            drawAlert.setTitle(R.string.draw);
            drawAlert.show();
        }
    }
}
