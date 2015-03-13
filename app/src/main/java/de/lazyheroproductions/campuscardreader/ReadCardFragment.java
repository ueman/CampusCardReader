package de.lazyheroproductions.campuscardreader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Use the {@link ReadCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadCardFragment extends Fragment {

    private View rootView;
    private TextView creditTextView;
    private TextView transactionTextView;
    private static final String FORMAT_STRING = "%.2f\u20AC"; // two numbers after the comma and a €-sign
    private OnAddNewDataListener mCallback;
    private boolean isAttached = false;

    public static ReadCardFragment newInstance() {
        return new ReadCardFragment();
    }

    public ReadCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_read_card, container, false);
        creditTextView = (TextView) rootView.findViewById(R.id.credit);
        transactionTextView = (TextView) rootView.findViewById(R.id.last_transaction);
        rootView.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onAddNewData();
                v.setEnabled(false);
//                v.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // makes sure that the container activity has implemented
        // the callback-interface. If not, it throws an exception
        try {
            mCallback = (OnAddNewDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    public boolean isAttached(){
        return isAttached;
    }

    public void updateData(double credit, double lastTransaction){
        creditTextView.setText(getResources().getText(R.string.credit) + " " + format(credit));
        transactionTextView.setText(getResources().getText(R.string.last_transaction) + " " + format(lastTransaction));
        rootView.findViewById(R.id.put_card_to_device_textview).setVisibility(View.GONE);
    }

    private String format(double d){
        return String.format(FORMAT_STRING, d);
    }

    public interface OnAddNewDataListener {
        public void onAddNewData();
    }

}
