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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class TodoRecyclerViewAdapter extends FirestoreRecyclerAdapter<Task,TodoRecyclerViewAdapter.ViewHolder> {

    //private final ArrayList<Task> mValues;
    private final OnListFragmentInteractionListener mListener;

    public TodoRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Task> options, OnListFragmentInteractionListener listener) {
        super(options);
        //mValues = items;
        mListener = listener;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Task model) {
        //holder.mItem = mValues.get(position);
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
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
                }
            }
        });
        if (model.getName() == null)
            Log.d("NAME: ", "notfound");
        Log.d("DATE: ", model.getDate());
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
