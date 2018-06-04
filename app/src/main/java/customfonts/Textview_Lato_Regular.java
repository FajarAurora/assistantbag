package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by foram on 20/2/17.
 */

public class Textview_Lato_Regular extends android.support.v7.widget.AppCompatTextView {

    public Textview_Lato_Regular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Textview_Lato_Regular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Textview_Lato_Regular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Regular.ttf");
            setTypeface(tf);
        }
    }

}
