package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<music> musicList=new ArrayList<>();
        musicList.addAll(getmusics());
        Recycler recycler=new Recycler(musicList);
        RecyclerView recyclerView=findViewById(R.id.rec_list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycler);
    }
    public List<music> getmusics() {
        Cursor cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<music> mp3Infos = new ArrayList<music>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
          music mp3Info = new music();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));//音乐id
            Log.i("tiaoshi","id="+id);
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));            //音乐标题
            Log.i("tiaoshi","title="+title);
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));            //艺术家
            Log.i("tiaoshi","artist="+artist);
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));          //时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));              //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));          //是否为音乐
            if (isMusic != 0) {     //只把音乐添加到集合当中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Infos.add(mp3Info);
            }
        }
        return mp3Infos;
    }

    class Recycler extends RecyclerView.Adapter<Recycler.ViewHolder>{
        private List<music> musicList;
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView number,music_title,music_Artist;
            public  ViewHolder(View view){
                super(view);
                number=(TextView)findViewById(R.id.number);
                music_Artist=(TextView)findViewById(R.id.music_Artist);
                music_title=(TextView)findViewById(R.id.music_title);
            }
        }
        public Recycler(List<music> musics){
            musicList=musics;
        }
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_part,parent,false);
            final ViewHolder holder=new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i=0;

                }
            });
            return  holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            music music=musicList.get(position);
            holder.music_title.setText(music.getTitle());
            holder.music_Artist.setText(music.getArtist());
            holder.number.setText(position);
        }

        @Override
        public int getItemCount() {
            return musicList.size();
        }

    }
}
