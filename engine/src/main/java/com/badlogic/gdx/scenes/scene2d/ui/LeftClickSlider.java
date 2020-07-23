package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

/**
 * This {@link Slider} only changes it value when it is <i>left</i> clicked on.
 */
// TODO replace with https://github.com/libgdx/libgdx/pull/6101
public class LeftClickSlider extends ProgressBar {
	int button = Buttons.LEFT; // -1 as default
	int draggingPointer = -1;
	boolean mouseOver;
	private Interpolation visualInterpolationInverse = Interpolation.linear;
	private float[] snapValues;
	private float threshold;

	public LeftClickSlider(float min, float max, float stepSize,
			boolean vertical, Skin skin) {
		this(min, max, stepSize, vertical,
				skin.get("default-" + (vertical ? "vertical" : "horizontal"),
						SliderStyle.class));
	}

	public LeftClickSlider(float min, float max, float stepSize,
			boolean vertical, Skin skin, String styleName) {
		this(min, max, stepSize, vertical,
				skin.get(styleName, SliderStyle.class));
	}

	/**
	 * Creates a new slider. If horizontal, its width is determined by the
	 * prefWidth parameter, its height is determined by the maximum of the
	 * height of either the slider {@link NinePatch} or slider handle
	 * {@link TextureRegion}. The min and max values determine the range the
	 * values of this slider can take on, the stepSize parameter specifies the
	 * distance between individual values. E.g. min could be 4, max could be 10
	 * and stepSize could be 0.2, giving you a total of 30 values, 4.0 4.2, 4.4
	 * and so on.
	 * 
	 * @param min
	 *            the minimum value
	 * @param max
	 *            the maximum value
	 * @param stepSize
	 *            the step size between values
	 * @param style
	 *            the {@link SliderStyle}
	 */
	public LeftClickSlider(float min, float max, float stepSize,
			boolean vertical, SliderStyle style) {
		super(min, max, stepSize, vertical, style);

		addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				if (disabled)
					return false;
				if (LeftClickSlider.this.button != -1
						&& LeftClickSlider.this.button != button)
					return false;
				if (draggingPointer != -1)
					return false;
				draggingPointer = pointer;
				calculatePositionAndValue(x, y);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer,
					int button) {
				if (pointer != draggingPointer)
					return;
				draggingPointer = -1;
				// The position is invalid when focus is cancelled
				if (event.isTouchFocusCancel()
						|| !calculatePositionAndValue(x, y)) {
					// Fire an event on touchUp even if the value didn't change,
					// so listeners can see when a drag ends via isDragging.
					ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
					fire(changeEvent);
					Pools.free(changeEvent);
				}
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				calculatePositionAndValue(x, y);
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer,
					Actor fromActor) {
				if (pointer == -1)
					mouseOver = true;
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer,
					Actor toActor) {
				if (pointer == -1)
					mouseOver = false;
			}
		});
	}

	public void setStyle(SliderStyle style) {
		if (style == null)
			throw new NullPointerException("style cannot be null");
		if (!(style instanceof SliderStyle))
			throw new IllegalArgumentException("style must be a SliderStyle.");
		super.setStyle(style);
	}

	/**
	 * Returns the slider's style. Modifying the returned style may not have an
	 * effect until {@link #setStyle(SliderStyle)} is called.
	 */
	public SliderStyle getStyle() {
		return (SliderStyle) super.getStyle();
	}

	protected Drawable getKnobDrawable() {
		SliderStyle style = getStyle();
		return (disabled && style.disabledKnob != null) ? style.disabledKnob
				: (isDragging() && style.knobDown != null) ? style.knobDown
						: ((mouseOver && style.knobOver != null)
								? style.knobOver
								: style.knob);
	}

	boolean calculatePositionAndValue(float x, float y) {
		final SliderStyle style = getStyle();
		final Drawable knob = getKnobDrawable();
		final Drawable bg = (disabled && style.disabledBackground != null)
				? style.disabledBackground
				: style.background;

		float value;
		float oldPosition = position;

		final float min = getMinValue();
		final float max = getMaxValue();

		if (vertical) {
			float height = getHeight() - bg.getTopHeight()
					- bg.getBottomHeight();
			float knobHeight = knob == null ? 0 : knob.getMinHeight();
			position = y - bg.getBottomHeight() - knobHeight * 0.5f;
			value = min + (max - min) * visualInterpolationInverse
					.apply(position / (height - knobHeight));
			position = Math.max(Math.min(0, bg.getBottomHeight()), position);
			position = Math.min(height - knobHeight, position);
		} else {
			float width = getWidth() - bg.getLeftWidth() - bg.getRightWidth();
			float knobWidth = knob == null ? 0 : knob.getMinWidth();
			position = x - bg.getLeftWidth() - knobWidth * 0.5f;
			value = min + (max - min) * visualInterpolationInverse
					.apply(position / (width - knobWidth));
			position = Math.max(Math.min(0, bg.getLeftWidth()), position);
			position = Math.min(width - knobWidth, position);
		}

		float oldValue = value;
		if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
				&& !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT))
			value = snap(value);
		boolean valueSet = setValue(value);
		if (value == oldValue)
			position = oldPosition;
		return valueSet;
	}

	/** Returns a snapped value. */
	protected float snap(float value) {
		if (snapValues == null || snapValues.length == 0)
			return value;
		float bestDiff = -1, bestValue = 0;
		for (int i = 0; i < snapValues.length; i++) {
			float snapValue = snapValues[i];
			float diff = Math.abs(value - snapValue);
			if (diff <= threshold) {
				if (bestDiff == -1 || diff < bestDiff) {
					bestDiff = diff;
					bestValue = snapValue;
				}
			}
		}
		return bestDiff == -1 ? value : bestValue;
	}

	/**
	 * Will make this progress bar snap to the specified values, if the knob is
	 * within the threshold.
	 * 
	 * @param values
	 *            May be null.
	 */
	public void setSnapToValues(float[] values, float threshold) {
		this.snapValues = values;
		this.threshold = threshold;
	}

	/** Returns true if the slider is being dragged. */
	public boolean isDragging() {
		return draggingPointer != -1;
	}

	/**
	 * Sets the inverse interpolation to use for display. This should perform
	 * the inverse of the {@link #setVisualInterpolation(Interpolation) visual
	 * interpolation}.
	 */
	public void setVisualInterpolationInverse(Interpolation interpolation) {
		this.visualInterpolationInverse = interpolation;
	}

}