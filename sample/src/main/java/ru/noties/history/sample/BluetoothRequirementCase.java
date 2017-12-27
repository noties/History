package ru.noties.history.sample;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import ru.noties.debug.Debug;
import ru.noties.requirements.BuildUtils;
import ru.noties.requirements.MutableBool;
import ru.noties.requirements.RequestCode;
import ru.noties.requirements.RequirementCase;

public class BluetoothRequirementCase extends RequirementCase {

    private static final int REQUEST_CODE = RequestCode.createRequestCode(BluetoothRequirementCase.class);

    @Override
    public boolean meetsRequirement() {
        final BluetoothAdapter adapter;
        if (BuildUtils.isAtLeast(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            final BluetoothManager manager = (BluetoothManager) activity().getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = manager != null
                    ? manager.getAdapter()
                    : null;
        } else {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }
        return adapter != null && adapter.isEnabled();
    }

    @Override
    public void startResolution() {

        final MutableBool bool = new MutableBool();

        new AlertDialog.Builder(activity())
                .setTitle("Bluetooth")
                .setMessage("Man, Bluetooth is off, come on now, turn it on, will ya?")
                .setPositiveButton("OK", (dialog, which) -> {
                    Debug.i("OK");
                    bool.setValue(true);
                    final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_CODE);
                })
                .setNegativeButton("Cancel", null)
                .setOnDismissListener(dialog -> {
                    Debug.i("dismiss, bool: %s", bool.value());
                    if (!bool.value()) {
                        deliverResult(false);
                    }
                })
                .show();
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.i("requestCode: %s, resultCode: %s, data: %s", requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode) {
            deliverResult(meetsRequirement());
            return true;
        }
        return super.onActivityResult(requestCode, resultCode, data);
    }
}
