package com.manzolik.gmanzoli.mytrains;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.manzolik.gmanzoli.mytrains.data.TrainReminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrainReminderListAdapter extends RecyclerView.Adapter<TrainReminderListAdapter.TrainReminderItemHolder>
    implements Filterable {

    private List<TrainReminder> reminderList;
    private List<TrainReminder> originalReminderList;

    private OnDeleteListener onDeleteListener;

    public TrainReminderListAdapter(List<TrainReminder> reminderList, OnDeleteListener onDeleteListener) {
        this.originalReminderList = reminderList;
        this.reminderList = new ArrayList<>();
        this.reminderList.addAll(originalReminderList);
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    public TrainReminderItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_adapter_row_train_reminder, parent, false);

        return new TrainReminderItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TrainReminderItemHolder holder, final int position) {
        final TrainReminder reminder = reminderList.get(position);

        holder.trainDescription.setText(String.format("%d - %s", reminder.getTrain().getCode(), reminder.getTrain().getDepartureStation().getName()));

        holder.trainTarget.setText(reminder.getTargetStaion().getName());

        SimpleDateFormat format = new SimpleDateFormat("H:mm", Locale.getDefault());
        holder.trainTime.setText(String.format("Dalle %s alle %s", format.format(reminder.getStartTime().getTime()), format.format(reminder.getEndTime().getTime())));

        holder.trainDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Non posso usare la variabile position che ricevo come paramentro
                * perché se ho già eliminato degli elementi la posizione effettiva della view
                * all'interno della RecycleView può essere diversa, portando ad un animazione inconsistente */
                onDeleteListener.onDelete(TrainReminderListAdapter.this, reminder, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }


    @Override
    public Filter getFilter() {
        return new TrainReminderFilter(this, originalReminderList);
    }

    public void deleteItemAtPosition(int adapterPosition) {
        TrainReminder reminder = reminderList.get(adapterPosition);
        originalReminderList.remove(reminder);  // Rimuove l'elemento dalla lista completa
        reminderList.remove(adapterPosition); // Rimuove l'elemento dal datasource della lista visualizzata
        notifyItemRemoved(adapterPosition);
    }

    public interface OnDeleteListener{
        void onDelete(TrainReminderListAdapter adapter, TrainReminder reminder, int position);
    }

    public static class TrainReminderItemHolder extends RecyclerView.ViewHolder{
        protected final TextView trainDescription;
        protected final TextView trainTarget;
        protected final TextView trainTime;
        protected final ImageButton trainDelete;


        public TrainReminderItemHolder(View v) {
            super(v);
            this.trainDescription = (TextView) v.findViewById(R.id.train_reminder_adapter_train_description);
            this.trainTarget = (TextView) v.findViewById(R.id.train_reminder_adapter_train_target);
            this.trainTime = (TextView) v.findViewById(R.id.train_reminder_adapter_train_time);
            this.trainDelete = (ImageButton) v.findViewById(R.id.train_reminder_adapter_train_delete);
        }
    }

    private static class TrainReminderFilter extends Filter {

        private final TrainReminderListAdapter adapter;
        private final List<TrainReminder> originalList;
        private final List<TrainReminder> filteredList;

        public TrainReminderFilter(TrainReminderListAdapter adapter, List<TrainReminder> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence query) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (query.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = query.toString().toLowerCase().trim();

                for (final TrainReminder reminder : originalList) {
                    // reminer.toString = codiceTreno - stazioneDiPartenzaDelTreno
                    if (reminder.toString().contains(filterPattern)) {
                        filteredList.add(reminder);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.reminderList.clear();
            adapter.reminderList.addAll((ArrayList<TrainReminder>) filterResults.values);
            adapter.notifyDataSetChanged();
        }

    }
}
