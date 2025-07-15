package de.omagh.feature_diary.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.omagh.core_data.model.Task;
import de.omagh.feature_diary.R;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    private static final DiffUtil.ItemCallback<Task> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.isCompleted() == newItem.isCompleted()
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getDueDate() == newItem.getDueDate();
        }
    };
    private final OnTaskCheckedListener listener;

    public TaskAdapter(OnTaskCheckedListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    interface OnTaskCheckedListener {
        void onTaskChecked(Task task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView dueText;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.taskCheckBox);
            dueText = itemView.findViewById(R.id.taskDueDate);
        }

        void bind(Task task) {
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setText(task.getDescription());
            checkBox.setChecked(task.isCompleted());
            String date = new SimpleDateFormat("MMM dd", Locale.getDefault())
                    .format(new Date(task.getDueDate()));
            dueText.setText(date);
            checkBox.setOnCheckedChangeListener((b, checked) -> {
                if (checked) listener.onTaskChecked(task);
            });
        }
    }
}
