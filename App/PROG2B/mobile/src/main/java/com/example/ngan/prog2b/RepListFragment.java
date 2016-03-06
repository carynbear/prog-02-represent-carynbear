package com.example.ngan.prog2b;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngan on 3/2/16.
 */
public class RepListFragment extends ListFragment {
    ListActionListener mCallback;

    // Container Activity must implement this interface
    public interface ListActionListener {
        public void onWebSelected(RepListItem i);
        public void onEmailSelected(RepListItem i);
        public void onDetailSelected(RepListItem i);
    }


    private List<RepListItem> mItems;        // RepListView items list


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mCallback = (ListActionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement ListActionListener");
            }
        } else throw new IllegalArgumentException(context.toString()
                + " is not an Activity; RepListFragment.onAttach()");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        mItems = activity.passDataToListFragment();
        // initialize and set the list adapter
        setListAdapter(new RepListAdapter(getActivity(), mItems, this)); //ToDo:this
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.replist_fragment_layout, container, false);
        ListView ls = (ListView) view.findViewById(android.R.id.list);
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new MyGestureListener(ls));
        ls.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                } else {
                    return false;
                }

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        //ToDo:decide whether or not to remove dividers
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        RepListItem item = mItems.get(position);
        //ToDo: Launch the representative view
        // do something
        mCallback.onDetailSelected(item);

        Toast.makeText(getActivity(), item.name, Toast.LENGTH_SHORT).show();
    }

    public void bemailPressed(int position) {
        //ToDo: Send email
        RepListItem item = mItems.get(position);
        mCallback.onEmailSelected(item);
        Toast.makeText(getActivity(), item.email, Toast.LENGTH_SHORT).show();
    }

    public void bwebPressed(int position) {
        //ToDo: launch website
        RepListItem item = mItems.get(position);
        mCallback.onWebSelected(item);
        Toast.makeText(getActivity(), item.web, Toast.LENGTH_SHORT).show();
    }


    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private ListView list;
        private List<Integer> viewPos;

        public MyGestureListener(ListView list) {
            this.list = list;
            this.viewPos = new ArrayList<Integer>();
            for (int i = 0; i<mItems.size(); i++) {
                viewPos.add(0);
            }
        }

        // CONDITIONS ARE TYPICALLY VELOCITY OR DISTANCE
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.d("---onFling---", e1.toString() + e2.toString() + "");

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (showButton(e1)) {
                        return true;
                    }
                // left to right swipe
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (hideButton(e1)) {
                        return true;
                    }
                }
            } catch (Exception e) {
            // nothing
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private boolean showButton(MotionEvent e1) {
            int pos = list.pointToPosition((int) e1.getX(), (int) e1.getY());
            if (viewPos.get(pos) == 0) {
                viewPos.set(pos,1);
                return showButtons(pos, 0);
            } else return false;
        }

        private boolean hideButton(MotionEvent e1) {
            int pos = list.pointToPosition((int) e1.getX(), (int) e1.getY());
            if (viewPos.get(pos) == 1) {
                viewPos.set(pos, 0);
                return showButtons(pos, 1);
            } else return false;
        }

        private boolean showButtons(int pos, int dir) {
            View child = list.getChildAt(pos);
            if (child != null) {
                ViewSwitcher viewSwitcher = (ViewSwitcher) child
                        .findViewById(R.id.list_switcher);
                if (dir == 0) {
                    viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right));
                    viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_left));
                }
                if (dir == 1) {
                    viewSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left));
                    viewSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right));
                }
                viewSwitcher.showNext();
                return true;
            }
            return false;
        }

    }
}

