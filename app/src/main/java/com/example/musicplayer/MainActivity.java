package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.media.MediaPlayer;


import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView toplay,up,next,xun,c;
    private MediaPlayer mediaPlayer;
    private SeekBar jindutiao;
    private TextView current, length,mingzi;
    private List<music> list=new ArrayList<>();
    int p,howxun=0;
    private ObjectAnimator animator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mingzi=findViewById(R.id.mingzi);
        c=findViewById(R.id.c);
        toplay = findViewById(R.id.toplay);
        up=findViewById(R.id.up);
        next=findViewById(R.id.next);
        jindutiao = findViewById(R.id.listen_jindutiao_sb);
        current = findViewById(R.id.listen_current_tv);
        length = findViewById(R.id.listen_length_tv);
        mediaPlayer = new MediaPlayer();
        xun=findViewById(R.id.xun);

        List<music> musicList = new ArrayList<>();
        musicList.addAll(getmusics());
        list=musicList;
        Recycler recycler = new Recycler(musicList);
        RecyclerView recyclerView = findViewById(R.id.rec_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycler);
        //检查当前权限（若没有该权限，值为-1；若有该权限，值为0）
        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("PERMISION_CODE", hasReadExternalStoragePermission + "***");
        if (hasReadExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            //若没有授权，会弹出一个对话框（这个对话框是系统的，开发者不能自己定制），用户选择是否授权应用使用系统权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }



        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(howxun==0)           //单曲
                {
                    music music = list.get(p);
                    mingzi.setText("歌曲："+music.getTitle());
                    jindutiao.setMax((int) music.getDuration());
                    length.setText("" + formatTime(music.getDuration()));
                    play(music.getUrl());
                }
                else if(howxun==1){         //顺序播放
                    p=p+1;
                    music music = list.get(p);
                    mingzi.setText("歌曲："+music.getTitle());
                    jindutiao.setMax((int) music.getDuration());
                    Log.i("tiaoshi", "Max=" + jindutiao.getMax());
                    play(music.getUrl());
                    length.setText("" + formatTime(music.getDuration()));

                }
                else if(howxun==2){         //随机
                    Random rand = new Random();
                    p=rand.nextInt(list.size());
                    Log.i("tiaoshi","p="+p);
                    music music = list.get(p);
                    mingzi.setText("歌曲："+music.getTitle());
                    jindutiao.setMax((int) music.getDuration());
                    Log.i("tiaoshi", "Max=" + jindutiao.getMax());
                    play(music.getUrl());
                    length.setText("" + formatTime(music.getDuration()));
                }
            }
        });
        toplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    toplay.setImageResource(R.drawable.toplay);
                } else {
                    mediaPlayer.start();
                    animator.start();
                    toplay.setImageResource(R.drawable.stop);
                }
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p=p-1;
                if(p<0)
                    p=list.size()-1;
                Log.i("tiaoshi","p="+p);
                Log.i("tiaoshi","size"+list.size());
                music music = list.get(p);
                mingzi.setText("歌曲："+music.getTitle());
                jindutiao.setMax((int) music.getDuration());
                Log.i("tiaoshi", "Max=" + jindutiao.getMax());
                play(music.getUrl());
                length.setText("" + formatTime(music.getDuration()));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                p=p+1;
                if(p==list.size())
                    p=0;
                music music = list.get(p);
                mingzi.setText("歌曲："+music.getTitle());
                jindutiao.setMax((int) music.getDuration());
                Log.i("tiaoshi", "Max=" + jindutiao.getMax());
                play(music.getUrl());
                length.setText("" + formatTime(music.getDuration()));
            }
        });
        jindutiao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        xun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(howxun==0){
                    howxun=1;
                    xun.setImageResource(R.drawable.xun_list);
                }
                else if(howxun==1){
                    howxun=2;
                    xun.setImageResource(R.drawable.xun_suiji);
                }
                else {
                    howxun=0;
                    xun.setImageResource(R.drawable.xun_danqu);
                }
            }
        });
    }

    public void play(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            toplay.setImageResource(R.drawable.stop);
            new Thread(new SeekBarThread()).start();
            animator = ObjectAnimator.ofFloat(c, "rotation", 0f, 360.0f);
            animator.setDuration(10000);
            animator.setInterpolator(new LinearInterpolator());//匀速
            animator.setRepeatCount(-1);//设置动画重复次数（-1代表一直转）
            animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
            animator.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<music> getmusics() {
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        Log.i("tiaoshi", cursor.toString());
        List<music> mp3Infos = new ArrayList<>();
        Log.i("tiaoshi", "int=" + cursor.getCount() + "");
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            music mp3Info = new music();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            Log.i("tiaoshi", "id=" + id);
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));            //音乐标题
            Log.i("tiaoshi", "title=" + title);
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));            //艺术家
            Log.i("tiaoshi", "artist=" + artist);
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));          //时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));              //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
            mp3Info.setId(id);
            mp3Info.setTitle(title);
            mp3Info.setArtist(artist);
            mp3Info.setDuration(duration);
            mp3Info.setSize(size);
            mp3Info.setUrl(url);
            mp3Infos.add(mp3Info);
            Log.i("tiaoshi", "music_title" + mp3Infos.get(i).getTitle());

        }
        return mp3Infos;
    }

    class Recycler extends RecyclerView.Adapter<Recycler.ViewHolder> {
        private List<music> musicList;

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView number, music_title, music_Artist;

            public ViewHolder(View view) {
                super(view);
                number = (TextView) view.findViewById(R.id.number);
                music_Artist = (TextView) view.findViewById(R.id.music_Artist);
                music_title = (TextView) view.findViewById(R.id.music_title);
            }
        }

        public Recycler(List<music> musics) {
            musicList = musics;
        }

        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_part, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    p=holder.getAdapterPosition();
                    music music = musicList.get(holder.getAdapterPosition());
                    mingzi.setText("歌曲："+music.getTitle());
                    jindutiao.setMax((int) music.getDuration());
                    Log.i("tiaoshi", "Max=" + jindutiao.getMax());
                    play(musicList.get(holder.getAdapterPosition()).getUrl());
                    length.setText("" + formatTime(music.getDuration()));

                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("是否删除歌曲");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.remove(holder.getAdapterPosition());//选择行的位置
                            musicList=list;
                            notifyDataSetChanged();

                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create();
                    builder.show();
                    return false;



                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            music mus = musicList.get(position);
            Log.i("tiaoshi", "size" + musicList.size());
            holder.music_Artist.setText(mus.getArtist());
            holder.music_title.setText(mus.getTitle());
            holder.number.setText(position + "");
        }

        @Override
        public int getItemCount() {
            return musicList.size();
        }

    }

    public String formatTime(long time) {
        Log.i("tiaoshi", "time=" + time);
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (mediaPlayer != null && !mediaPlayer.isPlaying() == false) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            jindutiao.setProgress(msg.what);
            current.setText(formatTime(msg.what));

        }
    };

}
