package com.example.breatheapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView textTaskCount, textInfo;
    private ProgressBar progressBar, loading;
    private ConstraintLayout visualizer;
    private Group visualizerGroup;
    private CardView cardView;
    private int taskCount = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textTaskCount = view.findViewById(R.id.taskCount);
        textInfo = view.findViewById(R.id.textInfo);
        progressBar = view.findViewById(R.id.progressBar);
        visualizer = view.findViewById(R.id.visualizer);
        loading = view.findViewById(R.id.loadingBar);
        visualizerGroup = view.findViewById(R.id.group);
        cardView = view.findViewById(R.id.cardView);

        loading.setVisibility(View.VISIBLE);
        visualizerGroup.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);

        // get task count
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String date = sdf.format(Calendar.getInstance().getTime());
        FirebaseFirestore.getInstance().collection("tasks")
                .whereEqualTo("date", date).whereEqualTo("done", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            taskCount++;
                        }
                        loading.setVisibility(View.INVISIBLE);
                        visualizerGroup.setVisibility(View.VISIBLE);
                        visualize();
                    }
                });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void visualize() {
        // no animation
        if (taskCount == 0 && progressBar.getProgress() == 0)
            showInfo();
        else {
            // task count animation
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, taskCount);
            valueAnimator.setDuration(1500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textTaskCount.setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();

            // gauge animation
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress",
                    progressBar.getProgress(), (taskCount > 10 ? 500 : taskCount * 50));
            animator.setDuration(1500);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    showInfo();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
            animator.start();
        }
    }

    private void showInfo() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.5f, 0.2f);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) progressBar.getLayoutParams();
                params.verticalBias = (float)valueAnimator.getAnimatedValue();
                progressBar.setLayoutParams(params);
            }
        });
        valueAnimator.start();

        if (taskCount == 0) {
            textInfo.setText("No tasks today! Go enjoy!");
        } else if (taskCount <= 4) {
            textInfo.setText("Only few tasks today");
        } else if (taskCount <= 8) {
            textInfo.setText("A lot of tasks today");
        } else {
            textInfo.setText("Too many tasks today!");
        }
        cardView.setVisibility(View.VISIBLE);
        cardView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
