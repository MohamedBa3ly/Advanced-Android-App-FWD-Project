package com.example.advancedapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var txtWidth = 0f
    private var txtSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var circleOffset = txtSize / 2
    private lateinit var btnTitle: String
    private var progressWidth = 0f
    private var progressCircle = 0f
    private var valueAnimator = ValueAnimator()

    //Color button for base btn , during loading and color for circle :
    private var btnColor = ContextCompat.getColor(context,R.color.colorPrimary)
    private var loadingColor = ContextCompat.getColor(context,R.color.colorPrimaryDark)
    private var circleColor = ContextCompat.getColor(context,R.color.colorAccent)

    //i will make a Button view in (content_main) with this class and i will use buttonState in MainActivity :
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new){

            ButtonState.Clicked -> {
                btnTitle = "Clicked"
                invalidate()
            }

            ButtonState.Loading -> {
                btnTitle = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f,widthSize.toFloat())
                valueAnimator.duration = 4500
                valueAnimator.addUpdateListener {
                    progressWidth = it.animatedValue as Float
                    progressCircle = (widthSize.toFloat() / 365) * progressWidth
                    invalidate()
                }

                valueAnimator.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                        progressWidth = 0f
                        if (buttonState == ButtonState.Loading){
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()
            }

            ButtonState.Completed -> {
                valueAnimator.cancel()
                progressWidth = 0f
                progressCircle = 0f
                btnTitle = resources.getString(R.string.button_download)
                invalidate()
            }
        }
    }


    init {
        btnTitle = "Download"
        context.withStyledAttributes(attrs,R.styleable.LoadingButton){
            btnColor = getColor(R.styleable.LoadingButton_btnColor,0)
            loadingColor = getColor(R.styleable.LoadingButton_btnLoadingColor,0)
            circleColor = getColor(R.styleable.LoadingButton_loadingCircleColor,0)
        }
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    //Fun to draw canvas i will make fun for backgroundColor, ProgressBackground,title and CircleProgress and i will call here to Draw :)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        colorOfBackground(canvas)
        backgroundProgress(canvas)
        title(canvas)
        circleProgress(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    //Fun to use in onDraw for back ground color:
    private fun colorOfBackground(canvas: Canvas?){
        paint.color = btnColor
        canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
    }

    //Fun to use in onDraw for title of text :
    private fun title(canvas: Canvas?){
        paint.color = Color.WHITE
        txtWidth = paint.measureText(btnTitle)
        canvas?.drawText(btnTitle,widthSize/2 - txtWidth/2,heightSize/2 - (paint.descent() + paint.ascent())/2,paint)
    }

    //Fun to use in onDraw for progress in rect :
    private fun backgroundProgress(canvas: Canvas?){
        paint.color = loadingColor
        canvas?.drawRect(0f,0f,progressWidth,heightSize.toFloat(),paint)
    }

    //Fun to use in onDraw for progress of circle next to text in rect : ( i will use drawArc for circle )
    private fun circleProgress(canvas: Canvas?){
        canvas?.save()
        canvas?.translate(widthSize/2+txtWidth/2+circleOffset,heightSize/2 - txtSize/2)
        paint.color = circleColor
        canvas?.drawArc(RectF(0f,0f,txtSize,txtSize),0f,progressCircle * 0.365f,true,paint)
        canvas?.restore()
    }

}