package leonproject.com.dailyselfie;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by fudan on 3/19/15.
 */
public class SelfieAdapter extends BaseAdapter {

    private Context context;
    private Cursor cursor;
    private RelativeLayout layout;

    public SelfieAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (RelativeLayout) inflater.inflate(R.layout.list_item, null);

        TextView display_time_text_view = (TextView) layout.findViewById(R.id.list_item_dateTextView);
        ImageView image_view = (ImageView) layout.findViewById(R.id.list_item_image_preview);

        cursor.moveToPosition(position);

        display_time_text_view.setText(cursor.getString(cursor.getColumnIndex(SelfieDB.TIME)));
        String url = cursor.getString(cursor.getColumnIndex(SelfieDB.PHOTOPATH));
        if(!url.equals("null"))
            image_view.setImageBitmap(cropBitMap(decodeFile(new File(url),200)));


        return layout;
    }


    private Bitmap decodeFile(File f, int requiredSize) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = requiredSize;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.i("DailyInfo","FileNotFound!");
            return null;
        }

    }

    public Bitmap cropBitMap(Bitmap bmp) {
        if (bmp != null) {
            Bitmap dstBmp;
            if (bmp.getWidth() >= bmp.getHeight()) {

                dstBmp = Bitmap.createBitmap(
                        bmp,
                        bmp.getWidth() / 2 - bmp.getHeight() / 2,
                        0,
                        bmp.getHeight(),
                        bmp.getHeight()
                );

            } else {

                dstBmp = Bitmap.createBitmap(
                        bmp,
                        0,
                        bmp.getHeight() / 2 - bmp.getWidth() / 2,
                        bmp.getWidth(),
                        bmp.getWidth()
                );
            }

            return dstBmp;

        }

        return null;
    }

}
