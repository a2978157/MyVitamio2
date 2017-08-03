package retrofit.mifeng.us.myvitamio;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView vv;
    private CustomMediaController mCustomMediaController;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        //调用Vitamio.initialize(this)方法可对其进行初始化操作，该方法有一个返回值表示初始化是否成功，当初始化成功后我们再来进行进一步的操作。
        if (Vitamio.initialize(this)){
            vv = (VideoView) findViewById(R.id.vv);
            //setVideoURI方法给VideoView设置一个网络播放地址
            vv.setVideoURI(Uri.parse("http://baobab.wdjcdn.com/145076769089714.mp4"));
            //MediaController是一个播放控制器（这个东西不是必须设置的，看需求）
            MediaController mediaController = new MediaController(this);
            vv.setMediaController(mediaController);

            //自定义视频控制器
            mCustomMediaController = new CustomMediaController(this, vv, this);
            mCustomMediaController.setVideoName("白火锅 x 红火锅");//视频标题
            mCustomMediaController.show(5000);
            vv.setMediaController(mCustomMediaController);

            spUtils = new SPUtils();//SharedPreferences工具类
            //获取上一次保存的进度
            long progress = spUtils.getShared(this, "progress");//此处使用SharedPreferences保存当前播放进度，SPUtils是我自己封装的工具类
            vv.seekTo(progress);//设置视频的进度

            //调用videoView的start方法就可以播放视频了（注意添加网络访问权限）
            vv.start();

            //显示缓冲百分比的TextView
            final TextView buffer = (TextView) findViewById(R.id.buffer_percent);
            //显示下载网速的TextView
            final TextView net_speed = (TextView) findViewById(R.id.net_speed);

            //这个方法表示监听缓冲百分比，里边的percent参数就表示当前缓冲百分比。
            vv.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    buffer.setText("已缓冲：" + percent + "%");
                }
            });

            //这个监听器我们可以用来监听缓冲的整个过程，what参数表示缓冲的时机，
            // extra参数表示当前的下载网速，根据what参数我们可以判断出当前是开始缓冲还是缓冲结束还是正在缓冲，
            // 开始缓冲的时候，我们将左上角的两个控件显示出来，同时让播放器暂停播放，
            // 缓冲结束时将左上角两个控件隐藏起来，同时播放器开始播放，正在缓冲的时候我们就来显示当前的下载网速。
            vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        //开始缓冲
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            buffer.setVisibility(View.VISIBLE);
                            net_speed.setVisibility(View.VISIBLE);
                            mp.pause();
                            break;
                        //缓冲结束
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            buffer.setVisibility(View.GONE);
                            net_speed.setVisibility(View.GONE);
                            mp.start();
                            break;
                        //正在缓冲
                        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                            net_speed.setText("当前网速:" + extra + "kb/s");
                            break;
                    }
                    return true;
                }
            });
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //屏幕切换时，设置全屏
        if (vv != null){
            vv.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
        }
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onPause() {
        super.onPause();
        //保存进度
        spUtils.saveShared("progress",vv.getCurrentPosition(),this);
    }

}
