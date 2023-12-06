package hu.nye.doragongasukidesu.ledcontroller;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import hu.nye.doragongasukidesu.ledcontroller.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;
	private Slider sliderRed, sliderGreen, sliderBlue;
	private TextView textValueRed, textValueGreen, textValueBlue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		sliderRed = binding.sliderRed;
		sliderGreen = binding.sliderGreen;
		sliderBlue = binding.sliderBlue;
		textValueRed = binding.textValueRed;
		textValueGreen = binding.textValueGreen;
		textValueBlue = binding.textValueBlue;

		sliderRed.addOnChangeListener(new SliderChangeListener(sliderRed, textValueRed));
		sliderGreen.addOnChangeListener(new SliderChangeListener(sliderGreen, textValueGreen));
		sliderBlue.addOnChangeListener(new SliderChangeListener(sliderBlue, textValueBlue));
	}

}