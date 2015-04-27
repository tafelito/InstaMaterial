package io.github.froger.instamaterial;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    private OnCommentsItemClickListener onCommentsItemClickListener;

    public CommentsAdapter(Context context) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.btn_fab_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;
        String url = "https://markets.jpmorgan.com/research/ImageServlet?parameterType=ANALYST&id=";
        String id = "";
        switch (position % 3) {
            case 0:
                holder.tvComment.setText("Bruce Kasman");
                id="U018173";
                break;
            case 1:
                holder.tvComment.setText("Peter Acciavatti");
                id="U031797";
                break;
            case 2:
                holder.tvComment.setText("David Hensley");
                id="U421084";
                break;
        }

        holder.llAnalyst.setOnClickListener(this);
        holder.llAnalyst.setTag(position);

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttpDownloader(context) {
            @Override
            protected HttpURLConnection openConnection(Uri uri)
                    throws IOException {

                CookieHandler.setDefault(new CookieManager(null,
                        CookiePolicy.ACCEPT_ALL));

                HttpURLConnection connection = super.openConnection(uri);
                connection.setRequestProperty("Cookie",
                        "pajpm1=oU0OAFQLyTdLAQAAAAAAAAAAAABUC8k3SwEAAGQA5HJDE9Eg0gaNUemeBexoRtfLiq8=;secure;ua=accepted;uaU=accepted;ua=accepted;uaU=accepted");

                return connection;
            }
        });
        builder.listener(new Picasso.Listener() {

            @Override
            public void onImageLoadFailed(Picasso arg0, Uri arg1, Exception arg2) {
                Log.e("Comments", "Error Loading image with Piccaso " + arg1.toString(), arg2);
            }
        });

        /*builder.build()
                .load(url + id)
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(new RoundedTransformation())
                .into(holder.ivUserAvatar);*/
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public void setOnCommentsItemClickListener(OnCommentsItemClickListener onCommentsItemClickListener) {
        this.onCommentsItemClickListener = onCommentsItemClickListener;
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.llAnalyst) {
            if (onCommentsItemClickListener != null) {
                onCommentsItemClickListener.onAnalystClick(view, (Integer) view.getTag());
            }
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.llAnalyst)
        View llAnalyst;
        @InjectView(R.id.ivUserAvatar)
        ImageView ivUserAvatar;
        @InjectView(R.id.tvComment)
        TextView tvComment;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public interface OnCommentsItemClickListener {
        public void onAnalystClick(View v, int position);

    }
}
