package cq.siddharth_bhat.photo_editor_clearquote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class PhotoEditorView extends RelativeLayout {

    private static final String TAG = "PhotoEditorView";

    private FilterImageView mImgSource;
    private DrawingView mDrawingView;
    private ImageFilterView mImageFilterView;
    private boolean clipSourceImage;
    private static final int imgSrcId = 1, shapeSrcId = 2, glFilterId = 3;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        mImgSource = new FilterImageView(getContext());
        RelativeLayout.LayoutParams sourceParam = setupImageSource(attrs);

        mImgSource.setOnImageChangedListener(new FilterImageView.OnImageChangedListener() {
            @Override
            public void onBitmapLoaded(@Nullable Bitmap sourceBitmap) {
                mImageFilterView.setFilterEffect(PhotoFilter.NONE);
                mImageFilterView.setSourceBitmap(sourceBitmap);
                Log.d(TAG, "onBitmapLoaded() called with: sourceBitmap = [" + sourceBitmap + "]");
            }
        });

        mImageFilterView = new ImageFilterView(getContext());
        RelativeLayout.LayoutParams filterParam = setupFilterView();

        mDrawingView = new DrawingView(getContext());
        RelativeLayout.LayoutParams brushParam = setupDrawingView();

        addView(mImgSource, sourceParam);

        addView(mImageFilterView, filterParam);

        addView(mDrawingView, brushParam);
    }


    @SuppressLint("Recycle")
    private RelativeLayout.LayoutParams setupImageSource(@Nullable AttributeSet attrs) {
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);

        RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }

        int widthParam = ViewGroup.LayoutParams.MATCH_PARENT;
        if (clipSourceImage) {
            widthParam = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                widthParam, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        return params;
    }


    private RelativeLayout.LayoutParams setupDrawingView() {
        mDrawingView.setVisibility(GONE);
        mDrawingView.setId(shapeSrcId);

        // Align drawing view to the size of image view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_LEFT, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_RIGHT, imgSrcId);
        return params;
    }


    private RelativeLayout.LayoutParams setupFilterView() {
        mImageFilterView.setVisibility(GONE);
        mImageFilterView.setId(glFilterId);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        return params;
    }

    public ImageView getSource() {
        return mImgSource;
    }

    DrawingView getDrawingView() {
        return mDrawingView;
    }


    void saveFilter(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (mImageFilterView.getVisibility() == VISIBLE) {
            mImageFilterView.saveBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(final Bitmap saveBitmap) {
                    Log.e(TAG, "saveFilter: " + saveBitmap);
                    mImgSource.setImageBitmap(saveBitmap);
                    mImageFilterView.setVisibility(GONE);
                    onSaveBitmap.onBitmapReady(saveBitmap);
                }

                @Override
                public void onFailure(Exception e) {
                    onSaveBitmap.onFailure(e);
                }
            });
        } else {
            onSaveBitmap.onBitmapReady(mImgSource.getBitmap());
        }
    }

    void setFilterEffect(PhotoFilter filterType) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(filterType);
    }

    void setFilterEffect(CustomEffect customEffect) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(customEffect);
    }

    void setClipSourceImage(boolean clip) {
        clipSourceImage = clip;
        RelativeLayout.LayoutParams param = setupImageSource(null);
        mImgSource.setLayoutParams(param);
    }
}
