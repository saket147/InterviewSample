package com.example.bigstepsampleapp.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.example.bigstepsampleapp.R

class CircleImageView : AppCompatImageView {
    private val mDrawableRect: RectF? = RectF()
    private val mBorderRect: RectF? = RectF()
    private val mShaderMatrix: Matrix? = Matrix()
    private val mBitmapPaint: Paint? = Paint()
    private val mBorderPaint: Paint? = Paint()
    private val mCircleBackgroundPaint: Paint? = Paint()
    private var mBorderColor = DEFAULT_BORDER_COLOR
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mCircleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f
    private var mColorFilter: ColorFilter? = null
    private var mReady = false
    private var mSetupPending = false
    private var mBorderOverlay = false
    private var mDisableCircularTransformation = false

    constructor(context: Context) : super(context) {
        init()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(context, attrs, defStyle) {
        val a = context?.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0)
        mBorderWidth = a?.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH)!!
        mBorderColor = a?.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR)
        mBorderOverlay = a?.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY)
        mCircleBackgroundColor = a?.getColor(R.styleable.CircleImageView_civ_circle_background_color, DEFAULT_CIRCLE_BACKGROUND_COLOR)
        a?.recycle()
        init()
    }

    private fun init() {
        super.setScaleType(SCALE_TYPE)
        mReady = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = OutlineProvider()
        }
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType? {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType?) {
        require(scaleType == SCALE_TYPE) { String.format("ScaleType %s not supported.", scaleType) }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    override fun onDraw(canvas: Canvas?) {
        if (mDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        if (mBitmap == null) {
            return
        }
        if (mCircleBackgroundColor != Color.TRANSPARENT) {
            mDrawableRect?.centerX()?.let { mCircleBackgroundPaint?.let { it1 -> canvas?.drawCircle(it, mDrawableRect?.centerY(), mDrawableRadius, it1) } }
        }
        mDrawableRect?.centerX()?.let { mBitmapPaint?.let { it1 -> canvas?.drawCircle(it, mDrawableRect?.centerY(), mDrawableRadius, it1) } }
        if (mBorderWidth > 0) {
            mBorderRect?.centerX()?.let { mBorderPaint?.let { it1 -> canvas?.drawCircle(it, mBorderRect?.centerY(), mBorderRadius, it1) } }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(@ColorInt borderColor: Int) {
        if (borderColor == mBorderColor) {
            return
        }
        mBorderColor = borderColor
        mBorderPaint?.color = mBorderColor
        invalidate()
    }

    fun getCircleBackgroundColor(): Int {
        return mCircleBackgroundColor
    }

    fun setCircleBackgroundColor(@ColorInt circleBackgroundColor: Int) {
        if (circleBackgroundColor == mCircleBackgroundColor) {
            return
        }
        mCircleBackgroundColor = circleBackgroundColor
        mCircleBackgroundPaint?.color = circleBackgroundColor
        invalidate()
    }

    fun setCircleBackgroundColorResource(@ColorRes circleBackgroundRes: Int) {
        setCircleBackgroundColor(context.resources.getColor(circleBackgroundRes))
    }

    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        if (borderWidth == mBorderWidth) {
            return
        }
        mBorderWidth = borderWidth
        setup()
    }

    fun isBorderOverlay(): Boolean {
        return mBorderOverlay
    }

    fun setBorderOverlay(borderOverlay: Boolean) {
        if (borderOverlay == mBorderOverlay) {
            return
        }
        mBorderOverlay = borderOverlay
        setup()
    }

    fun isDisableCircularTransformation(): Boolean {
        return mDisableCircularTransformation
    }

    fun setDisableCircularTransformation(disableCircularTransformation: Boolean) {
        if (mDisableCircularTransformation == disableCircularTransformation) {
            return
        }
        mDisableCircularTransformation = disableCircularTransformation
        initializeBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        if (cf === mColorFilter) {
            return
        }
        mColorFilter = cf
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }

    private fun applyColorFilter() {
        mBitmapPaint?.colorFilter = mColorFilter
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            (drawable as BitmapDrawable?)?.bitmap
        } else try {
            val bitmap: Bitmap? = if (drawable is ColorDrawable) {
                BITMAP_CONFIG?.let { Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, it) }
            } else {
                BITMAP_CONFIG?.let { Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, it) }
            }
            val canvas = bitmap?.let { Canvas(it) }
            canvas?.width?.let { drawable.setBounds(0, 0, it, canvas?.height) }
            if (canvas != null) {
                drawable.draw(canvas)
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = if (mDisableCircularTransformation) {
            null
        } else {
            getBitmapFromDrawable(drawable)
        }
        setup()
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (width == 0 && height == 0) {
            return
        }
        if (mBitmap == null) {
            invalidate()
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint?.isAntiAlias = true
        mBitmapPaint?.shader = mBitmapShader
        mBorderPaint?.style = Paint.Style.STROKE
        mBorderPaint?.isAntiAlias = true
        mBorderPaint?.color = mBorderColor
        mBorderPaint?.strokeWidth = mBorderWidth.toFloat()
        mCircleBackgroundPaint?.style = Paint.Style.FILL
        mCircleBackgroundPaint?.isAntiAlias = true
        mCircleBackgroundPaint?.color = mCircleBackgroundColor
        mBitmapHeight = mBitmap?.height!!
        mBitmapWidth = mBitmap?.width!!
        calculateBounds()?.let { mBorderRect?.set(it) }
        mBorderRadius = Math.min(((mBorderRect?.height()?.minus(mBorderWidth))?.div(2.0f)!!), (((mBorderRect?.width()?.minus(mBorderWidth))?.div(2.0f)!!)))
        mDrawableRect?.set(mBorderRect)
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect?.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f)
        }
        mDrawableRadius = mDrawableRect?.height()?.div(2.0f)?.let { Math.min(it, mDrawableRect?.width()?.div(2.0f)) }!!
        applyColorFilter()
        updateShaderMatrix()
        invalidate()
    }

    private fun calculateBounds(): RectF? {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = Math.min(availableWidth, availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f
        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix?.set(null)
        if (mBitmapWidth * mDrawableRect?.height()!! > mDrawableRect?.width()!! * mBitmapHeight) {
            scale = mDrawableRect?.height()?.div(mBitmapHeight).toFloat()
            dx = (mDrawableRect?.width()?.minus(mBitmapWidth * scale))!! * 0.5f
        } else {
            scale = mDrawableRect?.width() / mBitmapWidth
            dy = (mDrawableRect?.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix?.setScale(scale, scale)
        mShaderMatrix?.postTranslate((dx + 0.5f) + mDrawableRect.left, (dy + 0.5f) + mDrawableRect.top)
        mBitmapShader?.setLocalMatrix(mShaderMatrix)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.getX()?.let { inTouchableArea(it, event?.getY()) }!! && super.onTouchEvent(event)
    }

    private fun inTouchableArea(x: Float, y: Float): Boolean {
        return Math.pow(x - mBorderRect?.centerX()?.toDouble()!!, 2.0) + Math.pow(y - mBorderRect?.centerY()?.toDouble(), 2.0) <= Math.pow(mBorderRadius.toDouble(), 2.0)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private inner class OutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            val bounds = Rect()
            mBorderRect?.roundOut(bounds)
            outline?.setRoundRect(bounds, bounds.width() / 2.0f)
        }
    }

    companion object {
        private val SCALE_TYPE: ScaleType? = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG: Bitmap.Config? = Bitmap.Config.ARGB_8888
        private const val COLORDRAWABLE_DIMENSION = 2
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_CIRCLE_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_OVERLAY = false
    }
}