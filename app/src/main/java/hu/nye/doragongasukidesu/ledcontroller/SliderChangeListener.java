package hu.nye.doragongasukidesu.ledcontroller;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

class SliderChangeListener implements Slider.OnChangeListener {
	/**
	 * A slider, amire a listener vonatkozik
	 */
	private final Slider slider;
	/**
	 * A TextView, aminek a szövege ettől a slider-től függ
	 * Azaz amin a százalékos érték jelenik meg
	 */
	private final TextView textView;
	/**
	 * A slider maximum értéke, feltételezzük, hogy a minimum 0
	 */
	private final float max;
	/**
	 * A %-jel egy statikus változóban van, hogy ne jöjjön létre több objektum belőle
	 */
	private static final String PERCENTAGE_SYMBOL = "%";

	public SliderChangeListener(Slider slider, TextView textView) {
		this.slider = slider;
		this.textView = textView;
		max = slider.getValueTo();
	}

	@Override
	public void onValueChange(@NonNull Slider slider, float v, boolean b) {
		textView.setText((int) (1 / max * v * 100) + PERCENTAGE_SYMBOL);
	}
}