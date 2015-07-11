package nu.bernhard.wakemewhenigetthere;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.SeekBar;


/**
 * DiscreteSeekBar is which SeekBar for discrete values
 *
 * The values to be available can be set with setDiscreteValues()
 * setValue can be used to set the selected value of the SeekBar
 * getValue returns the selected discrete value
 */
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

    /**
     * Set the discrete values available to the SeekBar
     *
     * Values of the array should be in strict increasing order.
     *
     * @param values array of integer values available to the SeekBar
     */
    public void setDiscreteValues(Integer[] values) {
        this.values = values;
        this.setMax(values.length - 1);
    }

    /**
     * Get the current selected discrete value
     *
     * @return the current selected discrete value
     */
    public int getValue() {
        return values[getProgress()];
    }

    /**
     * Set the selected discrete value of the DiscreteSeekBar
     *
     * If the value is not part of the discrete values
     * available to the DiscreteSeekBar (set with
     * setDiscreteValues()) the next closest value will
     * be selected
     *
     * @param value
     */
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
}
