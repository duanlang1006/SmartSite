package com.isoftstone.smartsite.model.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.model.main.view.RoundMenuView;
import com.isoftstone.smartsite.utils.ToastUtils;
import com.uniview.airimos.Player;
import com.uniview.airimos.listener.OnStartLiveListener;
import com.uniview.airimos.listener.OnStopLiveListener;
import com.uniview.airimos.manager.ServiceManager;
import com.uniview.airimos.parameter.StartLiveParam;
import com.uniview.airimos.thread.RecvStreamThread;

/**
 * Created by gone on 2017/10/17.
 * modifed by zhangyinfu on 2017/10/19
 */

public class VideoPlayActivity extends Activity{
    private static final String TAG = "zyf_VideoPlayActivity";

    private SurfaceView mSurfaceView;
    private Player mPlayer;
    private Context mContext;
    private RecvStreamThread mRecvStreamThread = null;
    private RoundMenuView mRoundMenuView;
    private static  final int GRAY_9999 = Color.GREEN;
    private static  final int GRAY_F2F2 = Color.BLUE;

    private int mSurfaceViewWidth;
    private int mSurfaceViewHeight;
    //static {
    //    System.loadLibrary("imosplayer");
    //}

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_videoplay);
        mContext = this;
        //SurfaceView用于渲染
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);

        //监听SurfaceView的变化
        mSurfaceView.getHolder().addCallback(new surfaceCallback());

        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setZOrderMediaOverlay(true);
        //初始化一个Player对象
        mPlayer = new Player();
        mPlayer.AVInitialize(mSurfaceView.getHolder());

        /*获取Intent中的Bundle对象*/
        if(bundle == null) {
            bundle = this.getIntent().getExtras();
        }

        /*获取Bundle中的数据，注意类型和key*/
        String resCode = bundle.getString("ResCode");
        Log.i(TAG,"--------------resCode-------" + resCode);
        startLive(resCode);

        //初始化摇杆控件
        mRoundMenuView = (RoundMenuView)findViewById(R.id.round_menu_view);
        initRoundMenuView();

        //获取设备屏幕大小信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mSurfaceViewWidth = dm.widthPixels;
        mSurfaceViewHeight = dm.heightPixels;

    }

    /**
     * 启动实况
     *
     * @param cameraCode 摄像机编码
     */
    public void startLive(String cameraCode) {
        try {
            //启动实况的结果监听
            OnStartLiveListener listener = new OnStartLiveListener() {
                @Override
                public void onStartLiveResult(long errorCode, String errorDesc, String playSession) {
                    if (errorCode == 0){

                        //先停掉别的接收流线程
                        if (mRecvStreamThread != null) {
                            mPlayer.AVStopPlay();
                            mRecvStreamThread.interrupt();
                            mRecvStreamThread = null;
                        }

                        //将播放会话设给Player
                        mPlayer.setPlaySession(playSession);
                        //启动播放解码
                        mPlayer.AVStartPlay();
                        //修改監控界面大小為當前屏幕大小
                        mPlayer.changeDisplaySize(mSurfaceViewWidth, mSurfaceViewHeight);

                        //收流线程启动
                        mRecvStreamThread = new RecvStreamThread(mPlayer, playSession);
                        mRecvStreamThread.start();
                    }else{
                        Toast.makeText(VideoPlayActivity.this,errorDesc,Toast.LENGTH_SHORT).show();
                    }

                }

            };

            //设置启动实况的参数
            StartLiveParam p = new StartLiveParam();
            p.setCameraCode(cameraCode);
            p.setUseSecondStream(true); //使用辅流
            p.setBitrate(32 * 8);   //64KB的码率
            p.setFramerate(12);     //25帧率
            p.setResolution(2);     //4CIF分辨率

            //启动实况
            ServiceManager.startLive(p, listener);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopLive() {
        //停止实况，第二个参数是null表示不接收结果
        ServiceManager.stopLive(mPlayer.getPlaySession(), new OnStopLiveListener() {
            @Override
            public void onStopLiveResult(long errorCode, String errorMsg) {
                if (errorCode == 0){
                    if (mRecvStreamThread != null){
                        mRecvStreamThread.interrupt();
                        mRecvStreamThread = null;
                    }

                    //停止Player播放解码
                    mPlayer.AVStopPlay();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        //销毁Player
        if (null != mPlayer) {
            mPlayer.AVFinalize();
            mPlayer = null;
        }

        super.onDestroy();
    }

    public void initRoundMenuView() {
        RoundMenuView.RoundMenu roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = GRAY_9999;//Integer.parseInt(toHexEncoding(Color.GRAY));
        roundMenu.strokeColor = GRAY_F2F2;//Integer.parseInt(toHexEncoding(Color.GRAY));//ColorUtils.getColor(mContext, R.color.gray_9999);
        roundMenu.icon= drawableToBitmap(mContext,R.drawable.star);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("点击了1");
            }
        };
        mRoundMenuView.addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = GRAY_9999;//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.strokeColor = GRAY_F2F2;//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.icon= drawableToBitmap(mContext,R.drawable.star);//ImageUtils.drawable2Bitmap(getActivity(),R.drawable.ic_right);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("点击了2");
            }
        };
        mRoundMenuView.addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = GRAY_9999;//Integer.parseInt(toHexEncoding(R.color.gray_9999));//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.strokeColor = GRAY_F2F2;//Integer.parseInt(toHexEncoding(R.color.gray_9999));//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.icon= drawableToBitmap(mContext,R.drawable.star);//ImageUtils.drawable2Bitmap(getActivity(),R.drawable.ic_right);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("点击了3");
            }
        };
        mRoundMenuView.addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = GRAY_9999;//Integer.parseInt(toHexEncoding(R.color.gray_9999));//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.strokeColor = GRAY_F2F2;//Integer.parseInt(toHexEncoding(R.color.gray_9999));//ColorUtils.getColor(getActivity(), R.color.gray_9999);
        roundMenu.icon= drawableToBitmap(mContext,R.drawable.star);//ImageUtils.drawable2Bitmap(getActivity(),R.drawable.ic_right);
        roundMenu.onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShort("点击了4");
            }
        };
        mRoundMenuView.addRoundMenu(roundMenu);

        /**mRoundMenuView.setCoreMenu(ColorUtils.getColor(getActivity(), R.color.gray_f2f2),
                ColorUtils.getColor(getActivity(), R.color.gray_9999), ColorUtils.getColor(getActivity(), R.color.gray_9999)
                , 1, 0.43,ImageUtils.drawable2Bitmap(getActivity(),R.drawable.ic_ok), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showToast(getActivity(),"点击了中心圆圈");
                    }
                });*/

        mRoundMenuView.setCoreMenu(GRAY_F2F2, GRAY_9999, GRAY_F2F2
                , 1, 0.43, drawableToBitmap(mContext, R.drawable.ic_stop)
                , new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.showShort("点击了中心圆圈");
                    }
        });
    }

    public static String toHexEncoding(int color) {
        String R, G, B;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        //判断获取到的R,G,B值的长度 如果长度等于1 给R,G,B值的前边添0
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        sb.append("0x");
        sb.append(R);
        sb.append(G);
        sb.append(B);
        return sb.toString();
    }

    public static Bitmap drawableToBitmap(Context context, int resId) {
        Drawable drawable = context.getDrawable(resId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    class surfaceCallback implements SurfaceHolder.Callback {

        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "===== surfaceCreated =====");
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "===== surfaceChanged =====");
            if (mPlayer != null) {
                mPlayer.changeDisplaySize(width, height);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
            Log.d(TAG, "===== surfaceDestroyed =====");
        }
    }
}
