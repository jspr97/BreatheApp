package com.example.breatheapp;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.breatheapp.TodoFragment.OnListFragmentInteractionListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class TodoRecyclerViewAdapter extends FirestoreRecyclerAdapter<Task,TodoRecyclerViewAdapter.ViewHolder> {

    public interface OnItemLongClickListener {
        boolean onLongItemClicked(int position);
    }

    private static final int TASK_SOLO = 1;
    private static final int TASK_SHARED = 2;

    private final OnListFragmentInteractionListener mListener;
    private OnItemLongClickListener mLongClickListener;

    public TodoRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Task> options,
                                   OnListFragmentInteractionListener listener,
                                   OnItemLongClickListener longClickListener) {
        super(options);
        mListener = listener;
        mLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate different views for solo and shared tasks
        if (viewType == TASK_SOLO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == TASK_SHARED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item_shared, parent, false);
            return new ViewHolderShared(view);
        } else {
            throw new RuntimeException("Task viewholder error");
        }
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @Override
    public int getItemViewType(int position) {
        Task task = getSnapshots().getSnapshot(position).toObject(Task.class);

        // check whether task is shared
        if (task.getUsers().size() ==  1)
            return TASK_SOLO;
        else if (task.getUsers().size() > 1)
            return TASK_SHARED;
        else return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Task model) {
        // set task name
        holder.mTaskView.setText(model.getName());

        // set date and email for shared tasks
        if (holder.getItemViewType() == TASK_SHARED) {
            ((ViewHolderShared) holder).mDateView.setText(model.getDate());
            ((ViewHolderShared) holder).mEmailView.setText(model.getUsers().get(0));
        }
        // if time not specified, hide textview
        if (model.getTime() == null)
            holder.mTimeView.setVisibility(View.GONE);
        else {
            // display 12hr time to user
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                Date date = sdf.parse(model.getTime());
                sdf.applyPattern("hh:mm a");
                holder.mTimeView.setText(sdf.format(date));
                holder.mTimeView.setVisibility(View.VISIBLE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // set checkbox
        holder.mCheckBox.setChecked(model.getDone());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                    Log.i("TAG1", Boolean.toString(model.getDone()));
                    // toggle whether task is done
                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                    Boolean done = !documentSnapshot.getBoolean("done");
                    documentSnapshot.getReference().update("done", done);
                    Log.i("TAG2", Boolean.toString(model.getDone()));
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // edit task details
                mLongClickListener.onLongItemClicked(position);
                return true;
            }
        });
    }

    // view for solo task
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CheckBox mCheckBox;
        public TextView mTaskView;
        public TextView mTimeView;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCheckBox = view.findViewById(R.id.checkBox);
            mTaskView = view.findViewById(R.id.task);
            mTimeView = view.findViewById(R.id.time);

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        mTaskView.setPaintFlags(mTaskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    else
                        mTaskView.setPaintFlags(mTaskView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTaskView.getText() + "'";
        }
    }

    // view for shared task
    public class ViewHolderShared extends ViewHolder {
        public TextView mDateView, mEmailView;

        public ViewHolderShared(View view) {
            super(view);
            mDateView = view.findViewById(R.id.date);
            mEmailView = view.findViewById(R.id.email);
        }
    }
}
