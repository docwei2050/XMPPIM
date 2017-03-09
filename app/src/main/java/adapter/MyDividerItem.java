package adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

/**
 * Created by tobo on 17/3/7.
 */

public class MyDividerItem extends DividerItemDecoration {

    public MyDividerItem(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }
}
