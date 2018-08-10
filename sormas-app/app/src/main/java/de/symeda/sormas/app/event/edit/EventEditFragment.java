package de.symeda.sormas.app.event.edit;

import android.view.View;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventType;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class EventEditFragment extends BaseEditFragment<FragmentEventEditLayoutBinding, Event, Event> {

    private Event record;

    // Enum lists

    private List<Item> diseaseList;
    private List<Item> typeOfPlaceList;

    public static EventEditFragment newInstance(Event activityRootData) {
        return newInstance(EventEditFragment.class, null, activityRootData);
    }

    private void setUpControlListeners(final FragmentEventEditLayoutBinding contentBinding) {
        contentBinding.eventEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddressPopup(contentBinding);
            }
        });
    }

    private void openAddressPopup(final FragmentEventEditLayoutBinding contentBinding) {
        final Location location = record.getEventLocation();
        final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), location);
        locationDialog.show(null);

        locationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
            @Override
            public void onOkClick(View v, Object item, View viewRoot) {
                contentBinding.eventEventLocation.setValue(location);
                locationDialog.dismiss();
            }
        });
    }


    // Overrides

    @Override
    protected String getSubHeadingTitle() {
        return getResources().getString(R.string.caption_event_information);
    }

    @Override
    public Event getPrimaryData() {
        return record;
    }

    @Override
    protected void prepareFragmentData() {
        record = getActivityRootData();

        diseaseList = DataUtils.getEnumItems(Disease.class, true);
        typeOfPlaceList = DataUtils.getEnumItems(TypeOfPlace.class, true);
    }

    @Override
    public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        setUpControlListeners(contentBinding);

        if (isLiveValidationDisabled()) {
            disableLiveValidation(true);
        }

        contentBinding.setData(record);
        contentBinding.setEventTypeClass(EventType.class);
    }

    @Override
    public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
        // Initialize ControlSpinnerFields
        contentBinding.eventDisease.initializeSpinner(diseaseList);
        contentBinding.eventTypeOfPlace.initializeSpinner(typeOfPlaceList);

        // Initialize ControlDateFields
        contentBinding.eventEventDate.initializeDateField(getFragmentManager());
    }

    @Override
    public int getEditLayout() {
        return R.layout.fragment_event_edit_layout;
    }

}