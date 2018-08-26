package org.peterkwan.udacity.mysupermarket.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ITEM;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.QUANTITY;

public class InputQuantityDialogFragment extends DialogFragment {

    private static final int DIALOG_FRAGMENT = 1;
    private static final String LOG_TAG = InputQuantityDialogFragment.class.getSimpleName();

    private Item item;

    @BindView(R.id.item_name_view)
    TextView itemNameView;

    @BindView(R.id.item_quantity_view)
    EditText itemQuantityView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_item_quantity, null);

        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ITEM))
            item = bundle.getParcelable(ITEM);

        itemNameView.setText(item.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String quantityString = itemQuantityView.getText().toString();

                        try {
                            int quantity = Integer.parseInt(quantityString);
                            if (quantity > 0) {
                                Intent intent = new Intent();
                                intent.putExtra(ITEM, item);
                                intent.putExtra(QUANTITY, quantity);
                                getTargetFragment().onActivityResult(DIALOG_FRAGMENT, RESULT_OK, intent);
                            }
                            else
                                Toast.makeText(getContext(), R.string.quantity_validation_error, Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Log.e(LOG_TAG, "Not a number");
                            Toast.makeText(getContext(), R.string.quantity_validation_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

}
