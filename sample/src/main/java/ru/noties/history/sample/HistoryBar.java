package ru.noties.history.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import ru.noties.history.Entry;
import ru.noties.history.History;
import ru.noties.history.HistoryChangedObserver;

public class HistoryBar extends FrameLayout {

    interface PopToListener {
        void popTo(@NonNull Entry<ScreenKey> entry);
    }

    private RecyclerView recyclerView;
    private Adapter adapter;

    public HistoryBar(Context context) {
        super(context);
        init(context, null);
    }

    public HistoryBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        inflate(context, R.layout.history_bar, this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        adapter = new Adapter(context);
        recyclerView.setAdapter(adapter);
    }

    public void setHistory(@NonNull History<ScreenKey> history) {
        adapter.setPopToListener(history::popTo);
        history.observe(new HistoryChangedObserver<ScreenKey>() {
            @Override
            public void onHistoryChanged() {
                adapter.setItems(history.entries());
                recyclerView.post(() -> recyclerView.scrollToPosition(adapter.getItemCount() - 1));
            }
        });
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private final LayoutInflater inflater;
        private List<Entry<ScreenKey>> entries;
        private PopToListener popToListener;

        Adapter(@NonNull Context context) {
            inflater = LayoutInflater.from(context);
            setHasStableIds(true);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(inflater.inflate(R.layout.history_bar_item, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {

            final Entry<ScreenKey> entry = entries.get(position);

            final int value;
            final int color;
            {
                final ContentState state = entry.state();
                value = state.value();
                color = state.color();
            }

            final TextView text = holder.text;

            text.setText(text.getResources().getString(R.string.content_pattern, value));
            text.setBackgroundColor(color);

            final View.OnClickListener clickListener;
            if (popToListener == null) {
                clickListener = null;
            } else {
                clickListener = v -> popToListener.popTo(entry);
            }
            holder.itemView.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return entries != null
                    ? entries.size()
                    : 0;
        }

        @Override
        public long getItemId(int position) {
            return entries.get(position).hashCode();
        }

        void setItems(@Nullable List<Entry<ScreenKey>> entries) {
            this.entries = entries;
            notifyDataSetChanged();
        }

        void setPopToListener(@NonNull PopToListener listener) {
            this.popToListener = listener;
            notifyDataSetChanged();
        }

        static class Holder extends RecyclerView.ViewHolder {

            final TextView text;

            Holder(@NonNull View itemView) {
                super(itemView);

                this.text = itemView.findViewById(R.id.text_view);
            }
        }
    }
}
