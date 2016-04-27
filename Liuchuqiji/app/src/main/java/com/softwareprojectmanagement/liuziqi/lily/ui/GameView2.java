package com.softwareprojectmanagement.liuziqi.lily.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import adapter.ChessGridAdapter;
import core.Config;

/**
 * Created by Lily on 16/3/14.
 * 实现一个简易的棋盘界面,点击落子,无算法无AI
 * 应该能适配不同屏幕
 * 目前棋盘为15*15,更改棋盘大小只需修改BOARDSIZE值
 * 人机GameView
 */
public class GameView2 extends AppCompatActivity {
    private int KONGNUM = Config.KONGNUM;
    private int BLACKNUM = Config.BLACKNUM;
    private int WHITENUM = Config.WHITENUM;
    private int BOARDSIZE = Config.BOARDSIZE;
    private int screen_width;//屏幕宽度

    private int arr_board[][] = new int[BOARDSIZE][BOARDSIZE];

    private GridView gv_gameView;
    private ChessGridAdapter chessGridAdapter;

    private int firststep=0;//是否是第一步
    private int chessSum=0;//下了第几个棋，每方走两个棋子
    private int dir[][]={{1,0},{1,1},{0,1},{1,-1}};
    private int countClick=0;
    private int playColor=BLACKNUM;
    private boolean isGameover=false;
    private int nowMoveX[]=new int[2];
    private int nowMoveY[]=new int[2];

    private Chronometer whiteTimer;
    private Chronometer blackTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_board);
        initView();
        myGame();
    }
    void initView() {
        gv_gameView = (GridView) findViewById(R.id.gridview);
        gv_gameView.setNumColumns(BOARDSIZE);

        WindowManager wm = this.getWindowManager();
        screen_width = wm.getDefaultDisplay().getWidth();

        //为GridView设置适配器
        chessGridAdapter = new ChessGridAdapter(this,screen_width,arr_board);//lily
        gv_gameView.setAdapter(chessGridAdapter);

        //因为gridview每个小格子的长宽必须是整数,所以设置重新设置一下棋盘的大小
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) gv_gameView.getLayoutParams();
        linearParams.height = (screen_width / BOARDSIZE) * BOARDSIZE;
        linearParams.width = linearParams.height;
        gv_gameView.setLayoutParams(linearParams);

        //注册监听事件
        gv_gameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//落子
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (isGameover)
                    return;

                int nowX, nowY;
                nowX = position / BOARDSIZE;
                nowY = position % BOARDSIZE;
                if (arr_board[nowX][nowY] == KONGNUM) {
                    countClick++;
                    chessSum++;

                    //根据当前玩家颜色来落对应的子
                    if (playColor == BLACKNUM) {
                        arr_board[nowX][nowY] = BLACKNUM;
                        nowMoveX[chessSum - 1] = nowX;
                        nowMoveY[chessSum - 1] = nowY;
                    } else if (playColor == WHITENUM) {
                        arr_board[nowX][nowY] = WHITENUM;
                        nowMoveX[chessSum - 1] = nowX;
                        nowMoveY[chessSum - 1] = nowY;
                    }
                    Toast.makeText(GameView2.this, "第" + countClick + "子,落子位置:" + position, Toast.LENGTH_SHORT).show();

                    //变色
                    if (firststep == 0 && playColor == BLACKNUM && chessSum == 1) {
                        firststep = 1;
                        changeTimer(playColor);
                        playColor ^= 3;
                        chessSum = 0;

                    } else if (chessSum == 2) {
                        chessSum = 0;
                        changeTimer(playColor);
                        playColor ^= 3;
                    }

                    chessGridAdapter.notifyDataSetChanged();//更新数据,刷新
                    //判断胜负
                    if (checkWin(nowX, nowY)) {
                        if (arr_board[nowX][nowY] == WHITENUM) {
                            Toast.makeText(GameView2.this, "游戏结束！白方获胜！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GameView2.this, "游戏结束！黑方获胜！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });



    }

    void myGame(){
        for(int i = 0 ; i < BOARDSIZE ; i ++ ){
            for(int j = 0; j < BOARDSIZE ; j++){
                arr_board[i][j] = KONGNUM;
            }
        }
//        arr_board[3][4] = BLACKNUM;
//        arr_board[1][2] = WHITENUM;
        whiteTimer=(Chronometer)this.findViewById(R.id.whiteTimer);
        blackTimer=(Chronometer)this.findViewById(R.id.blackTimer);
        blackTimer.start();
    }

    private boolean checkWin(int posx,int posy)
    {
        int color=arr_board[posx][posy];
        int connectSum;
        int nextx,nexty;
        for(int i=0;i<4;i++)
        {
            connectSum=1;
            nextx=posx+dir[i][0];
            nexty=posy+dir[i][1];
            while(inBoard(nextx,nexty) && arr_board[nextx][nexty]==color)
            {
                connectSum++;
                nextx+=dir[i][0];
                nexty+=dir[i][1];
            }
            nextx=posx-dir[i][0];
            nexty=posy-dir[i][1];
            while(inBoard(nextx,nexty) && arr_board[nextx][nexty]==color)
            {
                connectSum++;
                nextx-=dir[i][0];
                nexty-=dir[i][1];
            }
            if(connectSum>=6)
            {
                isGameover=true;
                return true;
            }
        }
        return false;
    }

    //检查未出界
    private boolean inBoard(int x,int y)
    {
        if(x>=0 && x<BOARDSIZE && y>=0 && y<BOARDSIZE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //交换计时
    private void changeTimer(int color)
    {
        if(color==WHITENUM)
        {
            whiteTimer.stop();
            String time[]= blackTimer.getText().toString().split(":");
            int temp=Integer.parseInt(time[0])*60;
            temp+=Integer.parseInt(time[1]);
            blackTimer.setBase(SystemClock.elapsedRealtime() -temp*1000);
            blackTimer.start();;
        }
        else
        {
            blackTimer.stop();
            String time[]= whiteTimer.getText().toString().split(":");
            int temp=Integer.parseInt(time[0])*60;
            temp+=Integer.parseInt(time[1]);
            whiteTimer.setBase(SystemClock.elapsedRealtime() - temp * 1000);
            whiteTimer.start();
        }
    }
}