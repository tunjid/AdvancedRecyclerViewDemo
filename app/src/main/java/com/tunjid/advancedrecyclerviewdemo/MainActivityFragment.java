package com.tunjid.advancedrecyclerviewdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.tunjid.advancedrecyclerviewdemo.ExternalComponents.CustomRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private CustomRecyclerView recyclerView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initializeViewComponents(rootView);
        return rootView;
    }

    private void initializeViewComponents(View rootView) {

        ArrayList<ModelObject> modelObjects = new ArrayList<>();

        for (int i = 0; i < states_A_M.size(); i++) {
            modelObjects.add(new ModelObject(i, states_A_M.get(i)));
        }

        // Create Layout Manager for RecyclerView
        LinearLayoutManager recylerViewLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recylerViewLayoutManager.scrollToPosition(0);

        // Find RecyclerView
        recyclerView = (CustomRecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(recylerViewLayoutManager);

        // Touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        RecyclerViewTouchActionGuardManager recyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        recyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        recyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        RecyclerViewDragDropManager recyclerViewDragDropManager = new RecyclerViewDragDropManager();

        // swipe manager
        RecyclerViewSwipeManager recyclerViewSwipeManager = new RecyclerViewSwipeManager();

        // Create adapter
        SimpleDraggableSortableAdapter simpleDraggableSortableAdapter = new SimpleDraggableSortableAdapter(modelObjects);

        simpleDraggableSortableAdapter.setAdapterListener(new SimpleDraggableSortableAdapter.AdapterListener() {
            @Override
            public void onModelObjectRemoved(int position) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onModelObjectRemoved(position);
                }
            }

            @Override
            public void onModelObjectMoved() {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onModelObjectMoved();
                }
            }

            @Override
            public void onModelObjectSwiped(ModelObject modelObject) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onModelObjectSwiped(modelObject);
                }
            }

            @Override
            public void onModelObjectClicked(ModelObject modelObject) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onModelObjectClicked(modelObject);
                }
            }
        });

        // wrap for dragging
        RecyclerView.Adapter wrappedAdapter
                = recyclerViewDragDropManager.createWrappedAdapter(simpleDraggableSortableAdapter);

        // wrap again for swiping
        wrappedAdapter = recyclerViewSwipeManager.createWrappedAdapter(wrappedAdapter);

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Set set all of these to the RecyclerView
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(wrappedAdapter);  // requires *wrapped* adapter
        recyclerView.setItemAnimator(animator);

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        recyclerViewTouchActionGuardManager.attachRecyclerView(recyclerView);
        recyclerViewSwipeManager.attachRecyclerView(recyclerView);
        recyclerViewDragDropManager.attachRecyclerView(recyclerView);
        addItemsToAdapter();
    }

    private void addItemsToAdapter() {

        new Thread(new Runnable() {
            public void run() {

                final List<ModelObject> modelObjectsToAdd = new ArrayList<>();

                try {
                    Thread.sleep(5000); // Sleep to mimic API call

                    for (int i = 0; i < states_A_M.size(); i++) {
                        modelObjectsToAdd.add(new ModelObject(i, states_N_Y.get(i)));
                    }
                }
                catch (Exception e) {
                    Log.d(TAG, "Faile to add items");
                }
                recyclerView.post(new Runnable() {
                    public void run() {
                        unwrapAdapter().addItemsToAdapter(modelObjectsToAdd);
                    }
                });
            }
        }).start();
    }

    /**
     * Used when you need to update your adapter when something changes, say an API call or something
     *
     * @return the adapter bound to the {@link RecyclerView before any wrapping}
     */
    private SimpleDraggableSortableAdapter unwrapAdapter() {
        // Unwrap once for swipeable
        RecyclerView.Adapter unWrappedAdapter = ((BaseWrapperAdapter) recyclerView.getAdapter()).getWrappedAdapter();

        // Unwrap again for draggable
        unWrappedAdapter = ((BaseWrapperAdapter) unWrappedAdapter).getWrappedAdapter();

        // return adapter
        return ((SimpleDraggableSortableAdapter) unWrappedAdapter);
    }

    public static final List<String> states_A_M = new ArrayList<>();

    static {
        states_A_M.add("Alabama, AL");
        states_A_M.add("Alaska, AK");
        states_A_M.add("Alberta, AB");
        states_A_M.add("American Samoa, AS");
        states_A_M.add("Arizona, AZ");
        states_A_M.add("Arkansas, AR");
        states_A_M.add("Armed Forces (AE), AE");
        states_A_M.add("Armed Forces Americas, AA");
        states_A_M.add("Armed Forces Pacific, AP");
        states_A_M.add("British Columbia, BC");
        states_A_M.add("California, CA");
        states_A_M.add("Colorado, CO");
        states_A_M.add("Connecticut, CT");
        states_A_M.add("Delaware, DE");
        states_A_M.add("District Of Columbia, DC");
        states_A_M.add("Florida, FL");
        states_A_M.add("Georgia, GA");
        states_A_M.add("Guam, GU");
        states_A_M.add("Hawaii, HI");
        states_A_M.add("Idaho, ID");
        states_A_M.add("Illinois, IL");
        states_A_M.add("Indiana, IN");
        states_A_M.add("Iowa, IA");
        states_A_M.add("Kansas, KS");
        states_A_M.add("Kentucky, KY");
        states_A_M.add("Louisiana, LA");
        states_A_M.add("Maine, ME");
        states_A_M.add("Manitoba, MB");
        states_A_M.add("Maryland, MD");
        states_A_M.add("Massachusetts, MA");
        states_A_M.add("Michigan, MI");
        states_A_M.add("Minnesota, MN");
        states_A_M.add("Mississippi, MS");
        states_A_M.add("Missouri, MO");
        states_A_M.add("Montana, MT");
    }

    public static final List<String> states_N_Y = new ArrayList<>();

    static {
        states_N_Y.add("Nebraska, NE");
        states_N_Y.add("Nevada, NV");
        states_N_Y.add("New Brunswick, NB");
        states_N_Y.add("New Hampshire, NH");
        states_N_Y.add("New Jersey, NJ");
        states_N_Y.add("New Mexico, NM");
        states_N_Y.add("New York, NY");
        states_N_Y.add("Newfoundland, NF");
        states_N_Y.add("North Carolina, NC");
        states_N_Y.add("North Dakota, ND");
        states_N_Y.add("Northwest Territories, NT");
        states_N_Y.add("Nova Scotia, NS");
        states_N_Y.add("Nunavut, NU");
        states_N_Y.add("Ohio, OH");
        states_N_Y.add("Oklahoma, OK");
        states_N_Y.add("Ontario, ON");
        states_N_Y.add("Oregon, OR");
        states_N_Y.add("Pennsylvania, PA");
        states_N_Y.add("Prince Edward Island, PE");
        states_N_Y.add("Puerto Rico, PR");
        states_N_Y.add("Quebec, PQ");
        states_N_Y.add("Rhode Island, RI");
        states_N_Y.add("Saskatchewan, SK");
        states_N_Y.add("South Carolina, SC");
        states_N_Y.add("South Dakota, SD");
        states_N_Y.add("Tennessee, TN");
        states_N_Y.add("Texas, TX");
        states_N_Y.add("Utah, UT");
        states_N_Y.add("Vermont, VT");
        states_N_Y.add("Virgin Islands, VI");
        states_N_Y.add("Virginia, VA");
        states_N_Y.add("Washington, WA");
        states_N_Y.add("West Virginia, WV");
        states_N_Y.add("Wisconsin, WI");
        states_N_Y.add("Wyoming, WY");
        states_N_Y.add("Yukon Territory, YT");
    }

}
