package com.example.aggarwals.nsithangout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by AGGARWAL'S on 9/6/2015.
 */
public class ChoiceDisplayAdapter extends BaseAdapter {

    public static final String INTENT_LATI = "Lati";
    public static final String INTENT_LONG = "Long";
    public static final String INTENT_PLACE_NAME = "place_name";
    public static final String INTENT_PLACE_ID = "place_id";
    public static final String INTENT_COLOR = "color";
    private Context mContext;
    private ArrayList<ChoiceDisplayObject> displayObjects;
    private LruCache<String,Bitmap> mMemoryCache;
    private ChoiceDisplay choiceDisplayactivity;

    public ChoiceDisplayAdapter(Context context,ArrayList<ChoiceDisplayObject> objects,ChoiceDisplay choiceDisplayactivity){
        mContext = context;
        displayObjects = objects;
        this.choiceDisplayactivity = choiceDisplayactivity;
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void clearcachememory(){
        if (mMemoryCache !=null)
            mMemoryCache.evictAll();
    }

    @Override
    public int getCount() {
        return displayObjects.size();
    }

    @Override
    public Object getItem(int i) {
        return displayObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();

        if (convertview == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(R.layout.choice_display_layout_activity,null);
            holder.placephoto = (ImageView)convertview.findViewById(R.id.place_photo);
            holder.placeicon = (ImageView)convertview.findViewById(R.id.place_icon);
            holder.placename = (TextView)convertview.findViewById(R.id.place_name);
            holder.vicinity = (TextView)convertview.findViewById(R.id.place_vicinity);
            holder.rating = (TextView)convertview.findViewById(R.id.place_rating);
            holder.placeopen = (TextView)convertview.findViewById(R.id.place_open);
            holder.placelocate = (Button)convertview.findViewById(R.id.place_locate);
            convertview.setTag(holder);
        }else
            holder = (ViewHolder)convertview.getTag();

        holder.placename.setText(displayObjects.get(i).getName());
        holder.placename.setTextColor(mContext.getResources().getColor(displayObjects.get(i).getColor()));
        holder.placename.setId(i);

        holder.vicinity.setText(displayObjects.get(i).getVicinity());
        holder.vicinity.setId(i);

        holder.rating.setText(String.valueOf(displayObjects.get(i).getRating()));
        holder.rating.setId(i);

        String key  = displayObjects.get(i).getPhtotref();
        if ( key != null){
            Bitmap bitmap = getBitmapFromMemCache(key);
            if (bitmap != null)
                holder.placephoto.setImageBitmap(bitmap);
            else
                new placephotoloader(displayObjects.get(i).getPhtotref(),holder.placephoto).execute();
        }else
        holder.placephoto.setImageResource(R.drawable.imagenotavailable);
        holder.placephoto.setId(i);
        if (displayObjects.get(i).getopennowv())
            holder.placeopen.setText("Open Now");
        else
            holder.placeopen.setText("Closed Now");
        holder.placeopen.setId(i);

        final int position = i;
        holder.placelocate.setId(i);
        holder.placelocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PlaceMapActivity.class);
                intent.putExtra(INTENT_LATI,displayObjects.get(position).getLatti());
                intent.putExtra(INTENT_LONG,displayObjects.get(position).getLongi());
                intent.putExtra(INTENT_PLACE_NAME,displayObjects.get(position).getName());
                intent.putExtra(INTENT_PLACE_ID,displayObjects.get(position).getPlace_id());
                intent.putExtra(INTENT_COLOR,displayObjects.get(position).getColor());
                choiceDisplayactivity.startActivity(intent);
            }
        });

        AnimationSet set = new AnimationSet(true);
        TranslateAnimation slide = new TranslateAnimation(-200,0,-200,0);
        slide.setInterpolator(new DecelerateInterpolator(5.0f));
        slide.setDuration(300);
        Animation fade = new AlphaAnimation(0,1.0f);
        fade.setInterpolator(new DecelerateInterpolator(5.0f));
        fade.setDuration(300);
        set.addAnimation(slide);
        set.addAnimation(fade);
        convertview.startAnimation(set);
        return convertview;
    }

    class ViewHolder {
        ImageView placephoto;
        TextView placename;
        TextView vicinity;
        ImageView placeicon;
        TextView rating;
        TextView placeopen;
        Button placelocate;

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public class placephotoloader extends AsyncTask<Void,Void,Void>{

        private String photoref;
        private ImageView photoplace;
        private Bitmap bmp;
        private String url;

        placephotoloader(String photoref,ImageView photoplace){
            this.photoref = photoref;
            this.photoplace = photoplace;
            bmp = null;
            url ="https://maps.googleapis.com/maps/api/place/photo?maxheight=145&photoreference="+this.photoref+"&key=AIzaSyBAuY7uwzJkS1d1Cp8WLYphhs4UuAZ7ZL4";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InputStream inputStream =(InputStream) new URL(url).getContent();
                bmp = BitmapFactory.decodeStream(inputStream);
            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (bmp != null) {
                addBitmapToMemoryCache(photoref, bmp);
                photoplace.setImageBitmap(bmp);
            }

        }
    }
}
