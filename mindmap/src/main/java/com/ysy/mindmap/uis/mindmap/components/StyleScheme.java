package com.ysy.mindmap.uis.mindmap.components;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Hashtable;
import java.util.Map;

class StyleScheme {

    // Default Values
    public static final Integer ATTRIBUTE_DEFAULT_PADDING = 5;
    public static final Integer ATTRIBUTE_DEFAULT_EXTERNAL_PADDING = 10;

    //Attribute Keys
    //Padding Keys
    public static final String ATTRIBUTE_PADDING = "padding";
    public static final String ATTRIBUTE_PADDING_LEFT = "padding.left";
    public static final String ATTRIBUTE_PADDING_RIGHT = "padding.right";
    public static final String ATTRIBUTE_PADDING_TOP = "padding.top";
    public static final String ATTRIBUTE_PADDING_BOTTOM = "padding.bottom";
    public static final String ATTRIBUTE_EXTERNAL_PADDING_HEIGHT = "externalpadding.height";
    public static final String ATTRIBUTE_EXTERNAL_PADDING_WIDTH = "externalpadding.width";
    public static final String ATTRIBUTE_EXTERNAL_PADDING = "externalpadding";
    public static final Integer ATTRIBUTE_DEFAULT_FORE_COLOR = Color.BLUE;
    public static final Integer ATTRIBUTE_DEFAULT_BACKGROUND_COLOR = Color.GRAY;
    public static final Integer ATTRIBUTE_DEFAULT_BACKGROUND_HIGHLIGHTED_COLOR = Color.MAGENTA;

    //Color Keys
    public static final String ATTRIBUTE_FORE_COLOR = "color.fore";
    public static final String ATTRIBUTE_FORE_COLOR_BORDER = "color.fore.border";
    public static final String ATTRIBUTE_FORE_COLOR_TEXT = "color.fore.text";
    public static final String ATTRIBUTE_BACKGROUND_COLOR = "color.back";
    public static final String ATTRIBUTE_BACKGROUND_COLOR_HIGHLIGHTED = "color.back.highlighted";

    private Paint borderPaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint backgroundHighlightedPaint;
    private Paint pathPaint;
    private Map<String, Object> displayAttributes;

    private int colorAlpha;

    public StyleScheme() {
        displayAttributes = new Hashtable<>();
        initDefaultValues();
        setColorAlpha(255);
    }

    private void initDefaultValues() {
        setRenderAttribute(ATTRIBUTE_FORE_COLOR, ATTRIBUTE_DEFAULT_FORE_COLOR);
        setRenderAttribute(ATTRIBUTE_BACKGROUND_COLOR, ATTRIBUTE_DEFAULT_BACKGROUND_COLOR);
        setRenderAttribute(ATTRIBUTE_BACKGROUND_COLOR_HIGHLIGHTED, ATTRIBUTE_DEFAULT_BACKGROUND_HIGHLIGHTED_COLOR);
        setRenderAttribute(ATTRIBUTE_EXTERNAL_PADDING, ATTRIBUTE_DEFAULT_EXTERNAL_PADDING);
        setRenderAttribute(ATTRIBUTE_PADDING, ATTRIBUTE_DEFAULT_PADDING);
    }

    protected Paint getBorderPaint() {
        if (borderPaint == null) {
            int borderColor = getRenderAttribute(ATTRIBUTE_FORE_COLOR_BORDER);
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setColor(borderColor);
            borderPaint.setAlpha(colorAlpha);
        }
        return borderPaint;
    }

    protected Paint getTextPaint() {
        if (textPaint == null) {
            textPaint = new Paint();
            int textColor = getRenderAttribute(ATTRIBUTE_FORE_COLOR_TEXT);
            textPaint.setColor(textColor);
            textPaint.setAlpha(colorAlpha);
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setAntiAlias(true);
        }
        return textPaint;
    }

    protected Paint getBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint();
            int color = getRenderAttribute(ATTRIBUTE_BACKGROUND_COLOR);
            backgroundPaint.setColor(color);
            backgroundPaint.setAlpha(colorAlpha);
            backgroundPaint.setStyle(Paint.Style.FILL);
        }
        return backgroundPaint;
    }

    protected Paint getBackgroundHighlightedPaint() {
        if (backgroundHighlightedPaint == null) {
            backgroundHighlightedPaint = new Paint();
            int color = getRenderAttribute(ATTRIBUTE_BACKGROUND_COLOR_HIGHLIGHTED);
            backgroundHighlightedPaint.setColor(color);
            backgroundHighlightedPaint.setAlpha(colorAlpha);

            backgroundHighlightedPaint.setStyle(Paint.Style.FILL);
        }
        return backgroundHighlightedPaint;
    }

    protected Paint getPathPaint() {
        if (pathPaint == null) {
            pathPaint = new Paint();
            int color = getRenderAttribute(ATTRIBUTE_FORE_COLOR_BORDER);
            pathPaint.setColor(color);
            pathPaint.setAlpha(colorAlpha);
            pathPaint.setStyle(Paint.Style.STROKE);
            pathPaint.setAntiAlias(true);
        }
        return pathPaint;
    }

    public <T> T getRenderAttribute(String key) {
        while (key != null && key.length() > 0) {
            if (displayAttributes.containsKey(key)) {
                return (T) displayAttributes.get(key);
            }
            key = key.lastIndexOf(".") > 0
                    ? key.substring(0, key.lastIndexOf("."))
                    : null;
        }
        return null;
    }

    public void setRenderAttribute(String key, Object value) {
        displayAttributes.put(key, value);
    }

    public int getColorAlpha() {
        return colorAlpha;
    }

    /**
     * Sets the alpha values of all colors in the style scheme.
     *
     * @param colorAlpha The alpha value, must be in the bounds [0, 255].
     */
    public void setColorAlpha(int colorAlpha) {
        this.colorAlpha = colorAlpha;
        clearCachedPaints();
    }

    private void clearCachedPaints() {
        backgroundHighlightedPaint = null;
        backgroundPaint = null;
        borderPaint = null;
        pathPaint = null;
        textPaint = null;
    }
}
