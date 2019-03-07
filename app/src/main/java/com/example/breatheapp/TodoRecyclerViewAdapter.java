package com.example.breatheapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.breatheapp.TodoFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Task> mValues;
    private final OnListFragmentInteractionListener mListener;

    public TodoRecyclerViewAdapter(ArrayList<Task> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTaskView.setText(mValues.get(position).getName());
        holder.mDateView.setText(mValues.get(position).getDate());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTaskView;
        public final TextView mDateView;
        public Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTaskView = (TextView) view.findViewById(R.id.task);
            mDateView = (TextView) view.findViewById(R.id.time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTaskView.getText() + "'";
        }
    }
}
