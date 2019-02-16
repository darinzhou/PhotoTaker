package com.easysoftware.phototaker.view.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easysoftware.phototaker.R;
import com.easysoftware.phototaker.model.database.Photo;
import com.easysoftware.phototaker.util.Utils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.PhotoViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Photo photo);
    }

    private Context mContext;
    private CompositeDisposable mCompositeDisposable;
    private List<Photo> mPhotos;
    private OnItemClickListener mOnItemClickListener;

    public PhotoRecyclerViewAdapter(Context context, CompositeDisposable compositeDisposable,
                                    OnItemClickListener onItemClickListener) {
        mContext = context;
        mCompositeDisposable = compositeDisposable;
        mOnItemClickListener = onItemClickListener;
    }

    public void update(List<Photo> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_photo, viewGroup, false);
        return new PhotoViewHolder(itemView, mCompositeDisposable);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int i) {
        if (mPhotos != null && mPhotos.size() > 0) {
            Photo photo = mPhotos.get(i);
            photoViewHolder.bind(photo, mOnItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (mPhotos == null) {
            return 0;
        }
        return mPhotos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private CompositeDisposable mCompositeDisposable;
        private ImageView mImageView;
        private TextView mTextView;
        private View mItemView;

        public PhotoViewHolder(@NonNull View itemView, CompositeDisposable compositeDisposable) {
            super(itemView);
            mCompositeDisposable = compositeDisposable;
            mItemView = itemView;
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.textView);
        }

        public void bind(final Photo photo, OnItemClickListener onItemClickListener) {
            mCompositeDisposable.add(Utils.decodeBitmap(photo.getUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Bitmap>() {
                        @Override
                        public void onNext(Bitmap bitmap) {
                            mImageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    })
            );

            mTextView.setText(photo.getName());

            if (onItemClickListener != null) {
                mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(photo);
                    }
                });
            }

        }
    }
}
