package com.example.breatheapp;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
        public boolean onLongItemClicked(int position);
    }

    private final OnListFragmentInteractionListener mListener;
    private OnItemLongClickListener mLongClickListener;

    public TodoRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Task> options,
                                   OnListFragmentInteractionListener listener,
                                   OnItemLongClickListener longClickListener) {
        super(options);
        //mValues = items;
        mListener = listener;
        mLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Task model) {
        // set task name
        holder.mTaskView.setText(model.getName());

        // if time not specified, hide label
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

                    // toggle whether task is done
                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
                    final Boolean done = !documentSnapshot.getBoolean("done");
                    documentSnapshot.getReference().update("done", done);
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
}
