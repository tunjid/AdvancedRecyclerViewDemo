package com.tunjid.advancedrecyclerviewdemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.tunjid.advancedrecyclerviewdemo.ExternalComponents.ViewUtils;

import java.util.List;

/**
 * An adapter with drag and drop implementation for some modelObjects
 */
public class SimpleDraggableSortableAdapter extends RecyclerView.Adapter<SimpleDraggableSortableAdapter.ViewHolder>
        implements DraggableItemAdapter<SimpleDraggableSortableAdapter.ViewHolder>,
        SwipeableItemAdapter<SimpleDraggableSortableAdapter.ViewHolder> {

    private static final String TAG = SimpleDraggableSortableAdapter.class.getSimpleName();

    private static final int MODEL_OBJECT = 1;

    private List<ModelObject> modelObjects;

    private AdapterListener adapterListener;

    public SimpleDraggableSortableAdapter(List<ModelObject> modelObjects) {

        this.modelObjects = modelObjects;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        final LayoutInflater inflater = LayoutInflater.from(context);

        View itemView;

        // The row for your viewholder defined by it's position
        // You can have as many different view types as you'd like, I have 1 in this case
        itemView = inflater.inflate(R.layout.fragment_main_row, viewGroup, false);

        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final ModelObject modelObject = modelObjects.get(position);

        // set text
        modelObject.setPosition(position + 1);
        viewHolder.titleTextView.setText(modelObject.getTitle());
        viewHolder.positionTextView.setText("" + (position + 1));

        viewHolder.viewHolderListener = new ViewHolder.ViewHolderListener() {
            @Override
            public void onSelectionClicked() {
                adapterListener.onModelObjectClicked(modelObject);
            }
        };

        // set background resource (target view ID: container)
        final int dragState = viewHolder.getDragStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            }
            else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            }
            else {
                bgResId = R.drawable.bg_swipe_item_neutral;
            }

            viewHolder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties, i.e how much to swipe beofre anything is triggered
        viewHolder.setSwipeItemSlideAmount(0);
    }

    @Override
    public int getItemViewType(int position) {
        return MODEL_OBJECT;
    }

    @Override
    public int getItemCount() {
        return modelObjects.size();
    }

    @Override
    public long getItemId(int position) {
        return modelObjects.get(position).hashCode();
    }

    /**
     * Used by the interface in the {@link DraggableItemAdapter} to specify what to do
     * after a move has been completed
     *
     * @param fromPosition The original position of the {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * @param toPosition   The new position of the {@link android.support.v7.widget.RecyclerView.ViewHolder}
     */
    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.i(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        // Don't do anything if you didn't really move at all
        if ((fromPosition == toPosition) || toPosition == 0) {
            return;
        }

        // Implement logic of changing positionTextView order

        ModelObject item1 = modelObjects.get(fromPosition);
        ModelObject item2 = modelObjects.get(toPosition);

        int place1 = item1.getPosition();
        int place2 = item2.getPosition();

        item1.setPosition(place2); // Swap places
        item2.setPosition(place1); // Swap places

        modelObjects.set(fromPosition, item2); // Swap places
        modelObjects.set(toPosition, item1); // Swap places

        notifyItemMoved(fromPosition, toPosition);

        adapterListener.onModelObjectMoved();
    }

    /**
     * Submits the parameters defined below to the {@link ViewUtils} to determine if a drag can be started
     *
     * @param holder The {@link android.support.v7.widget.RecyclerView.ViewHolder} being dragged
     * @param x      x position of drag
     * @param y      y position of drag
     * @return whether or not the drag can be started
     */
    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int x, int y) {

        if (holder.viewType != MODEL_OBJECT) {
            return false;
        }
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    /**
     * Used when you have ranges that should not be draggable, say a header in your RecyclerView
     *
     * @param holder The {@link android.support.v7.widget.RecyclerView.ViewHolder} being dragged
     * @return the ItemDraggableRange object, or null if all are draggable
     */
    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder) {
        // This shows how to create a draggable range object

        /*final int start = 1;
        final int end = modelObjects.size();
        return new ItemDraggableRange(start, end);*/
        return null;
    }

    // Swipe methods

    /**
     * Used to specify the swipe reaction tyoe. Can be one of:
     * <p> REACTION_CAN_SWIPE_BOTH: {@value RecyclerViewSwipeManager#REACTION_CAN_SWIPE_BOTH}</p>
     * <p> REACTION_CAN_SWIPE_RIGHT: {@value RecyclerViewSwipeManager#REACTION_CAN_SWIPE_RIGHT}</p>
     * <p> REACTION_CAN_SWIPE_LEFT: {@value RecyclerViewSwipeManager#REACTION_CAN_SWIPE_LEFT}</p>
     * <p> REACTION_CAN_NOT_SWIPE_BOTH: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_BOTH}</p>
     * <p> REACTION_CAN_NOT_SWIPE_RIGHT: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_RIGHT}</p>
     * <p> REACTION_CAN_NOT_SWIPE_LEFT: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_LEFT}</p>
     * <p> REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_BOTH_WITH_RUBBER_BAND_EFFECT}</p>
     * <p> REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT}</p>
     * <p> REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT: {@value RecyclerViewSwipeManager#REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT}</p>
     *
     * @param holder The {@link android.support.v7.widget.RecyclerView.ViewHolder} being swiped
     * @param x      x position of swipe
     * @param y      y position of swipe
     * @return The reaction type.
     */
    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int x, int y) {

        if (onCheckCanStartDrag(holder, x, y)) {
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
        }

        else {
            return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_BOTH;
        }
    }

    /**
     * The background to show when swiping. This is usually specified by a drawable in xml
     *
     * @param holder The {@link android.support.v7.widget.RecyclerView.ViewHolder} being swiped
     * @param type   {@link RecyclerViewSwipeManager} background type to shpw
     */
    @Override
    public void onSetSwipeBackground(ViewHolder holder, int type) {
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_move_left;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_delete_right;
                break;
        }

        if (holder.viewType == MODEL_OBJECT) { // Used if you have different ViewTypes to check for
            holder.itemView.setBackgroundResource(bgRes);
        }
    }

    /**
     * Used to specify what happens after a swipe
     *
     * @param holder The {@link android.support.v7.widget.RecyclerView.ViewHolder} being swiped
     * @param result The result of the swipe from the {@link RecyclerViewSwipeManager} class.
     * @return an integer from the {@link RecyclerViewSwipeManager} defining what to do
     * <p>This will be acted on in
     * {@link SwipeableItemAdapter#onPerformAfterSwipeReaction(RecyclerView.ViewHolder, int, int)}</p>
     */
    @Override
    public int onSwipeItem(ViewHolder holder, int result) {
        Log.d(TAG, "onSwipeItem(result = " + result + ")");

        switch (result) {
            // swipe right --- remove
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
            // swipe left --- Do whatever you define listener specifies.
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    /**
     * After a swipe has been completed, this performs an action depending on the swipe performed.
     *
     * @param holder   The {@link android.support.v7.widget.RecyclerView.ViewHolder} being swiped
     * @param result   The result of the swipe from the {@link RecyclerViewSwipeManager} class.
     * @param reaction The reaction from {@link SwipeableItemAdapter#onSwipeItem(RecyclerView.ViewHolder, int)}
     */
    @Override
    public void onPerformAfterSwipeReaction(ViewHolder holder, int result, int reaction) {
        Log.d(TAG, "onPerformAfterSwipeReaction(result = " + result + ", reaction = " + reaction + ")");

        final int position = holder.getPosition();

        if (holder.viewType == MODEL_OBJECT) {

            switch (reaction) {

                // If swiped right, delete the item
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM:

                    modelObjects.remove(position);

                    for (int i = 0; i < modelObjects.size(); i++) { // Reset placement
                        modelObjects.get(i).setPosition(i + 1);
                    }

                    notifyItemRemoved(position);

                    if (adapterListener != null) {
                        adapterListener.onModelObjectRemoved(position);
                    }
                    break;

                // If left, reverse the title String
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION:

                    ModelObject modelObject = modelObjects.get(position);

                    String orginalTitle = modelObject.getTitle();
                    String reversedTitle = new StringBuilder(orginalTitle).reverse().toString();

                    modelObject.setTitle(reversedTitle);

                    if (adapterListener != null) {
                        adapterListener.onModelObjectSwiped(modelObject); // Subtract Loading View.
                    }
                    break;
            }
        }
    }

    public void addItemsToAdapter(List<ModelObject> modelsToAdd) {
        modelObjects.addAll(modelsToAdd);

        for (int i = 0; i < modelObjects.size(); i++) {
            modelObjects.get(i).setPosition(i + 1);
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends AbstractDraggableSwipeableItemViewHolder
            implements View.OnClickListener {

        public int viewType;

        public ViewGroup mContainer;
        public View mDragHandle;
        public TextView titleTextView;
        public TextView positionTextView;
        public ViewHolderListener viewHolderListener;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            mContainer = (ViewGroup) itemView.findViewById(R.id.container);
            mDragHandle = itemView.findViewById(R.id.drag_handle);
            titleTextView = (TextView) itemView.findViewById(R.id.title);
            positionTextView = (TextView) itemView.findViewById(R.id.position);
            mContainer.setOnClickListener(this);
        }

        /**
         * Gets the View to animate for swiping
         *
         * @return The container view to animate for swiping
         */
        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.container:
                    viewHolderListener.onSelectionClicked();
                    break;
            }
        }

        public interface ViewHolderListener {
            void onSelectionClicked();
        }
    }

    /**
     * Set the adapterListener to be notified when an item has been clicked.
     */
    public void setAdapterListener(AdapterListener listener) {
        this.adapterListener = listener;
    }

    public interface AdapterListener {
        void onModelObjectRemoved(int position);

        void onModelObjectMoved();

        void onModelObjectSwiped(ModelObject modelObject);

        void onModelObjectClicked(ModelObject modelObject);
    }
}

