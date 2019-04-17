package com.example.breatheapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TodoFragment extends Fragment implements TodoRecyclerViewAdapter.OnItemLongClickListener {

    public static final int REQUEST_ADD_TASK = 1;
    public static final int REQUEST_EDIT_TASK = 2;
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private TodoRecyclerViewAdapter mAdapter;
    private String date;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TodoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TodoFragment newInstance(int columnCount) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        date = sdf.format(Calendar.getInstance().getTime());
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        // set up recyclerview
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // show tasks according to date
        Query query = FirebaseFirestore.getInstance()
                .collection("tasks")
                .whereEqualTo("date", date).orderBy("time");
        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();

        mAdapter = new TodoRecyclerViewAdapter(options, mListener, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        // swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        // set up floating action button
        FloatingActionButton myFab = view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // go to Add Task activity
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                intent.putExtra("requestCode", REQUEST_ADD_TASK);
                startActivityForResult(intent, REQUEST_ADD_TASK);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // parent view
        int viewId;
        if (getActivity() instanceof CalendarActivity)
            viewId = R.id.coordinatorLayout;
        else
            viewId = R.id.fragmentFrame;

        if ((requestCode == REQUEST_ADD_TASK || requestCode == CalendarActivity.REQUEST_ADD_TASK)
                && resultCode == Activity.RESULT_OK) {
            Snackbar.make(getActivity().findViewById(viewId), "Task added", Snackbar.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_EDIT_TASK && resultCode == Activity.RESULT_OK) {
            Snackbar.make(getActivity().findViewById(viewId), "Task updated", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongItemClicked(int position) {
        String taskId = mAdapter.getSnapshots().getSnapshot(position).getId();
        Intent intent = new Intent(getActivity(), AddTaskActivity.class);
        intent.putExtra("id", taskId);
        intent.putExtra("requestCode", REQUEST_EDIT_TASK);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
        return true;
    }

    // set date from calendar activity
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Task task);
    }
}
