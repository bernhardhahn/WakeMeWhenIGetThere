package nu.bernhard.wakemewhenigetthere;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;


public class DiscreteSeekBar extends SeekBar {
    private Integer[] values = {100, 250, 500, 1000, 2500, 5000, 10000, 25000};

    public DiscreteSeekBar(Context context) {
        super(context);
        initValues();
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValues();
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValues();
    }

    private void initValues() {
        setDiscreteValues(values);
        setAccentColor();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setAccentColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Integer color = getResources().getColor(R.color.accent);
            getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setDiscreteValues(Integer[] values) {
        this.values = values;
        this.setMax(values.length - 1);

    }

    public int getValue() {
        return values[getProgress()];
    }

    public void setValue(int value) {
        int closeness = Integer.MAX_VALUE;
        int closestIndex = 0;
        for (int i = 0; i < values.length; ++i) {
            if (Math.abs(values[i] - value) <= closeness) {
                closeness = Math.abs(values[i] - value);
                closestIndex = i;
            }
        }
        setProgress(closestIndex);
    }
}
