package com.folioreader.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.folioreader.Config;
import com.folioreader.R;
import com.folioreader.model.HighlightImpl;
import com.folioreader.ui.view.UnderlinedTextView;
import com.folioreader.util.AppUtil;
import com.folioreader.util.UiUtil;

import java.util.List;

/**
 * @author gautam chibde on 16/6/17.
 */

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.HighlightHolder> {
    private List<HighlightImpl> highlights;
    private HighLightAdapterCallback callback;
    private Context context;
    private Config config;

    public HighlightAdapter(Context context, List<HighlightImpl> highlights, HighLightAdapterCallback callback, Config config) {
        this.context = context;
        this.highlights = highlights;
        this.callback = callback;
        this.config = config;
    }

    @Override
    public HighlightHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HighlightHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_highlight, parent, false));
    }

    @Override
    public void onBindViewHolder(final HighlightHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.container.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.container.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    }
                });
            }
        }, 10);

        holder.content.setText(Html.fromHtml(getItem(position).getContent()));
        UiUtil.setBackColorToTextView(holder.content,
                getItem(position).getType());
        holder.date.setText(AppUtil.formatDate(getItem(position).getDate()));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemClick(getItem(position));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.deleteHighlight(getItem(position).getId());
                highlights.remove(position);
                notifyDataSetChanged();

            }
        });
        holder.editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.editNote(getItem(position), position);
            }
        });
        if (getItem(position).getNote() != null) {
            if (getItem(position).getNote().isEmpty()) {
                holder.note.setVisibility(View.GONE);
                holder.noteIcon.setVisibility(View.GONE); // Hide noteIcon if note is empty
            } else {
                holder.note.setVisibility(View.VISIBLE);
                holder.note.setText(getItem(position).getNote());
                holder.noteIcon.setVisibility(View.VISIBLE); // Show noteIcon if note is not empty
            }
        } else {
            holder.note.setVisibility(View.GONE);
            holder.noteIcon.setVisibility(View.GONE); // Hide noteIcon if note is null
        }
//        holder.container.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final int height = holder.container.getHeight();
//                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ViewGroup.LayoutParams params =
//                                holder.swipeLinearLayout.getLayoutParams();
//                        params.height = height;
//                        holder.swipeLinearLayout.setLayoutParams(params);
//                    }
//                });
//            }
//        }, 30);
        if (config.isNightMode()) {
            holder.container.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.black));
            holder.note.setTextColor(ContextCompat.getColor(context,
                    R.color.white));
            holder.date.setTextColor(ContextCompat.getColor(context,
                    R.color.gray_text));
            holder.content.setTextColor(ContextCompat.getColor(context,
                    R.color.white));
        } else {
            holder.container.setBackgroundColor(ContextCompat.getColor(context,
                    R.color.white));
            holder.note.setTextColor(ContextCompat.getColor(context,
                    R.color.day_underline_icon_fill_color_top));
            holder.date.setTextColor(ContextCompat.getColor(context,
                    R.color.gray_text));
            holder.content.setTextColor(ContextCompat.getColor(context,
                    R.color.day_underline_icon_fill_color_top));
        }
    }

    private HighlightImpl getItem(int position) {
        return highlights.get(position);
    }

    @Override
    public int getItemCount() {
        return highlights.size();
    }

    public void editNote(String note, int position) {
        highlights.get(position).setNote(note);
        notifyDataSetChanged();
    }

    static class HighlightHolder extends RecyclerView.ViewHolder {
        private UnderlinedTextView content;
        private ImageView delete,  noteIcon;
        private LinearLayout  editNote;
        private TextView date;
        private RelativeLayout container;
        private TextView note;

        HighlightHolder(View itemView) {
            super(itemView);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            content = (UnderlinedTextView) itemView.findViewById(R.id.utv_highlight_content);
            delete = (ImageView) itemView.findViewById(R.id.iv_delete);
            editNote = (LinearLayout) itemView.findViewById(R.id.iv_edit_note_layout);
            date = (TextView) itemView.findViewById(R.id.tv_highlight_date);
            note = (TextView) itemView.findViewById(R.id.tv_note);
            noteIcon = (ImageView) itemView.findViewById(R.id.tv_note_icon);
        }
    }

    public interface HighLightAdapterCallback {
        void onItemClick(HighlightImpl highlightImpl);

        void deleteHighlight(int id);

        void editNote(HighlightImpl highlightImpl, int position);
    }
}
