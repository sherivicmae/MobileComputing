package com.example.activity_information;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BluetoothTransfer extends AppCompatActivity {

    private Button buttonShow, buttonDiscover, buttonSendFile, btn_receive, btnBack;
    private TextView selectFileTv, selectDeviceTv, connectedDeviceTv;
    private BluetoothAdapter myBluetoothAdapter;
    private ListView listview;
    private Switch switchBT;


    SendReceive sendReceive;
    private Intent btEnablingIntent;
    private int requestCodeForEnable;

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final int REQUEST_FILE_PICKER = 2;

    private ArrayAdapter<String> deviceArrayAdapter;
    private ArrayList<String> deviceList;
    private HashMap<String, BluetoothDevice> discoveredDevicesMap = new HashMap<>();
    private BroadcastReceiver discoveryReceiver;
    private BroadcastReceiver pairingReceiver;

    private String listSelectMode = "PAIRED_SELECT";

    private ProgressBar progressBar4;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;


    private Uri selectedFileUri; // URI for the selected file to send

    @Override
    protected void onStart() {
        super.onStart();
        // Register the Bluetooth state change receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, filter);

        // Check for connected devices when activity starts
        getConnectedDeviceName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unregister the Bluetooth receiver
        unregisterReceiver(bluetoothReceiver);
    }

    private void checkBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // Bluetooth is already permitted, you can check the connected devices
            getConnectedDeviceName();
        }
    }


    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeUIElements() {
        switchBT = findViewById(R.id.switch_BT);
        buttonShow = findViewById(R.id.btn_paired);
        buttonDiscover = findViewById(R.id.btn_discover);
        listview = findViewById(R.id.Lv_devices);
        buttonSendFile = findViewById(R.id.btn_sendFile);
        btn_receive = findViewById(R.id.btn_receive);
        selectFileTv = findViewById(R.id.tv_selectfile);
        //selectDeviceTv = findViewById(R.id.tv_selectdevice);
        connectedDeviceTv = findViewById(R.id.tv_connectedDevice);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupBluetoothAdapter() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;
    }

    private void setupDeviceList() {
        deviceList = new ArrayList<>();
        deviceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        listview.setAdapter(deviceArrayAdapter);
    }

    private void registerBroadcastReceivers() {
        setupDiscoveryReceiver();
        setupPairingReceiver();
    }

    private void setupShowPairedDevicesButton() {
        buttonShow.setOnClickListener(view -> showPairedDevices());
        listSelectMode = "PAIRED_SELECT";
    }

    private void setupDiscoverDevicesButton() {
        buttonDiscover.setOnClickListener(view -> discoverDevices());
        listSelectMode = "DISCOVERED_SELECT";
    }

    private void setupSelectFileButton() {
        buttonSendFile.setOnClickListener(view -> openFilePicker());
    }


    private void showPairedDevices() {
        if (!checkBluetoothPermission(Manifest.permission.BLUETOOTH_CONNECT)) return;

        deviceList.clear();
        deviceList.add("=== Paired Devices ===");

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if (pairedDevices.isEmpty()) {
            deviceList.add("No paired devices found");
        } else {
            for (BluetoothDevice device : pairedDevices) {
                String deviceInfo = device.getName() + "\n" + device.getAddress();
                deviceList.add(deviceInfo);
            }
        }

        deviceArrayAdapter.notifyDataSetChanged();

        // Set up click listener for paired devices
        setUpPairedDeviceClickListener();
    }

    private void discoverDevices() {
        if (!checkBluetoothPermission(Manifest.permission.BLUETOOTH_SCAN)) return;

        deviceList.clear();
        discoveredDevicesMap.clear();
        deviceList.add("=== Available Devices ===");
        deviceArrayAdapter.notifyDataSetChanged();

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
        }
        myBluetoothAdapter.startDiscovery();
        Toast.makeText(this, "Discovering devices...", Toast.LENGTH_SHORT).show();
    }

    private void getConnectedDeviceName() {
        if (myBluetoothAdapter != null && myBluetoothAdapter.isEnabled()) {
            myBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                    if (profile == BluetoothProfile.HEADSET) {
                        List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();
                        if (!connectedDevices.isEmpty()) {
                            BluetoothDevice connectedDevice = connectedDevices.get(0);
                            connectedDeviceTv.setText("Connected to: " + connectedDevice.getName());
                        } else {
                            connectedDeviceTv.setText("Connected to: None");
                        }
                    }
                    myBluetoothAdapter.closeProfileProxy(profile, proxy);
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    connectedDeviceTv.setText("Connected to: None");
                }
            }, BluetoothProfile.HEADSET);
        } else {
            connectedDeviceTv.setText("Bluetooth is disabled");
        }
    }

    // BroadcastReceiver to listen for Bluetooth state changes
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    getConnectedDeviceName(); // Check connected device when Bluetooth is turned on
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    connectedDeviceTv.setText("Bluetooth is disabled");
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                getConnectedDeviceName(); // Update connected device when bond state changes
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getConnectedDeviceName(); // Permission granted, check connected device
            } else {
                connectedDeviceTv.setText("Bluetooth permission denied");
            }
        }
    }


    private boolean checkBluetoothPermission(String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_BLUETOOTH_PERMISSION);
            return false;
        }
        return true;
    }

    private void setupDiscoveryReceiver() {
        discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleDeviceDiscovery(intent);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryReceiver, filter);
    }

    private void handleDeviceDiscovery(Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null && checkBluetoothPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
                String deviceName = device.getName() != null ? device.getName() : "Unknown";
                String deviceAddress = device.getAddress();
                String deviceInfo = deviceName + "\n" + deviceAddress;

                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !discoveredDevicesMap.containsKey(deviceAddress)) {
                    discoveredDevicesMap.put(deviceAddress, device);
                    deviceList.add(deviceInfo);
                    deviceArrayAdapter.notifyDataSetChanged();
                }
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Toast.makeText(BluetoothTransfer.this, "Discovery finished", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPairingReceiver() {
        pairingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handlePairingResult(intent);
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pairingReceiver, filter);
    }

    private void handlePairingResult(Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null) {
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                if (state == BluetoothDevice.BOND_BONDED) {
                    @SuppressLint("MissingPermission") String pairedDeviceName = device.getName();
                    Toast.makeText(BluetoothTransfer.this, "PAIRED SUCCESSFULLY: " + pairedDeviceName, Toast.LENGTH_SHORT).show();
                    removeFromDeviceList(device);
                }
            }
        }
    }

    private void removeFromDeviceList(BluetoothDevice device) {
        String deviceAddress = device.getAddress();
        if (discoveredDevicesMap.containsKey(deviceAddress)) {
            deviceList.removeIf(deviceInfo -> deviceInfo.endsWith(deviceAddress));
            discoveredDevicesMap.remove(deviceAddress);
            deviceArrayAdapter.notifyDataSetChanged();
        }
    }

    private void setUpListViewClickListener() {


        listview.setOnItemClickListener((parent, view, position, id) -> {

            if(Objects.equals(listSelectMode, "PAIRED_SELECT")) {
                String deviceInfo = deviceList.get(position);
                String deviceAddress = deviceInfo.split("\n")[1];

                BluetoothDevice selectedDevice = discoveredDevicesMap.get(deviceAddress);

                // Create new client connection for each transfer
                ClientClass clientClass = new ClientClass(selectedDevice);
                clientClass.start();

                connectedDeviceTv.setText("Status: Connecting");

            }
            else if(Objects.equals(listSelectMode, "DISCOVERED_SELECT")) {
                String deviceInfo = deviceList.get(position);
                String deviceAddress = deviceInfo.split("\n")[1]; // Extract device address'[-

                // Get the BluetoothDevice object from the discovered devices map
                BluetoothDevice device = discoveredDevicesMap.get(deviceAddress);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (device != null && device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    device.createBond(); // Initiate pairing
                    Toast.makeText(BluetoothTransfer.this, "Pairing with " + device.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BluetoothTransfer.this, "Device already paired or not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpPairedDeviceClickListener() {
        listview.setOnItemClickListener((parent, view, position, id) -> {
            if (position > 0) { // Skip the first item (title)
                String deviceInfo = deviceList.get(position);
                String deviceAddress = deviceInfo.split("\n")[1];

                BluetoothDevice device = myBluetoothAdapter.getRemoteDevice(deviceAddress);
                showDeviceOptionsDialog(device);
            }
        });
    }

    public void writeBytesToFile(byte[] data, String fileName) {
        // Define the directory where you want to save the file
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MOBILECOMP_Bluetooth");

        // Ensure the directory exists
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Create the file object with the given filename
        File file = new File(storageDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
            System.out.println("File written successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getFileBytesFromUri(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (InputStream inputStream = contentResolver.openInputStream(uri)) {
            if (inputStream == null) {
                return null;  // Handle case where inputStream couldn't be opened
            }

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (scheme.equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // Try to get the display name from the content provider
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (fileName == null) {
            // If we couldn't get the name from the content provider, try the last path segment
            fileName = uri.getLastPathSegment();
        }

        return fileName != null ? fileName : "unknown_file";
    }

    private void receive() {
        // Create new server connection for each transfer
        ServerClass serverClass = new ServerClass();
        serverClass.start();
    }

    @SuppressLint("MissingPermission")
    private void showDeviceOptionsDialog(BluetoothDevice device) {
        CharSequence[] options = {"Connect", "Unpair"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an action for " + device.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Connect to the device
                        boolean isConnected = connectToDevice(device);
                        if (isConnected) {
                            connectedDeviceTv.setText("Connected to: " + device.getName());
                        } else {
                            connectedDeviceTv.setText("Failed to connect");
                        }
                    } else if (which == 1) {
                        // Unpair the device
                        unpairDevice(device);
                    }
                })
                .show();
    }

    private boolean connectToDevice(BluetoothDevice device) {
        BluetoothDevice selectedDevice = device;

        // Create new client connection for each transfer
        ClientClass clientClass = new ClientClass(selectedDevice);
        clientClass.start();

        connectedDeviceTv.setText("Status: Connecting");
        return true;
    }


    private void unpairDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Get the method from the BluetoothDevice class
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                // Invoke the method on the device
                boolean success = (Boolean) removeBondMethod.invoke(device);
                if (success) {
                    Toast.makeText(this, "Unpaired " + device.getName(), Toast.LENGTH_SHORT).show();
                    removeFromDeviceList(device);
                } else {
                    Toast.makeText(this, "Failed to unpair " + device.getName(), Toast.LENGTH_SHORT).show();
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error while unpairing device", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    private void setupBluetoothSwitch() {
        switchBT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (myBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
                switchBT.setChecked(false);
                return;
            }

            if (isChecked) {
                enableBluetooth();
            } else {
                disableBluetooth();
            }
        });
    }

    private void enableBluetooth() {
        if (!myBluetoothAdapter.isEnabled()) {
            startActivityForResult(btEnablingIntent, requestCodeForEnable);
            Toast.makeText(getApplicationContext(), "Enabling Bluetooth...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableBluetooth() {
        if (myBluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myBluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(), "Disabling Bluetooth...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already disabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Specify the type of files to show (e.g., images, videos, etc.)
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_FILE_PICKER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bluetooth_transfer);
        applyWindowInsets();

        initializeUIElements();
        setupBluetoothAdapter();
        setupDeviceList();
        registerBroadcastReceivers();

        setupShowPairedDevicesButton();
        setupDiscoverDevicesButton();
        setupBluetoothSwitch();
        setupSelectFileButton();


        // Check and request Bluetooth permissions
        checkBluetoothPermissions();

        setUpListViewClickListener();
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted, proceed with Bluetooth tasks
                        Toast.makeText(this, "Bluetooth permission granted", Toast.LENGTH_SHORT).show();
                    } else {
                        // Permission is denied, show a message to the user
                        Toast.makeText(this, "Bluetooth permission is required to connect", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        progressBar4 = findViewById(R.id.progressBar4);

        btn_receive.setOnClickListener(v -> receive());

        if(myBluetoothAdapter.isEnabled()) {
            switchBT.setChecked(true);
        }
        else {
            switchBT.setChecked(false);
        }

        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intentCalcu = new Intent(getApplicationContext(), Home.class);
                intentCalcu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCalcu);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCodeForEnable && resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_FILE_PICKER && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Use AsyncTask to convert and send the file asynchronously
                new ConvertAndSendFileTask(this).execute(fileUri);
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "Unknown";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            fileName = cursor.getString(nameIndex);
            cursor.close();
        }
        return fileName;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case STATE_LISTENING:
                    connectedDeviceTv.setText("Status: Listening");
                    break;
                case STATE_CONNECTING:
                    connectedDeviceTv.setText("Status: Connecting");
                    break;

                case STATE_CONNECTED:
                    connectedDeviceTv.setText("Status: Connected");
                    break;

                case STATE_CONNECTION_FAILED:
                    connectedDeviceTv.setText("Status: Connection Failed");
                    break;

                case STATE_MESSAGE_RECEIVED:
                    FileTransferData transferData = (FileTransferData) message.obj;
                    int fileSize = message.arg1;
                    int progress = (int) (((float) transferData.data.length / fileSize) * 100);

                    if(progress == 100) {
//                        tv_progress.setVisibility(View.GONE);
                        selectFileTv.setText("Transfer Complete - " + transferData.filename);
                        writeBytesToFile(transferData.data, transferData.filename);
                    } else {
//                        tv_progress.setVisibility(View.VISIBLE);
                        selectFileTv.setText(transferData.filename);
                        progressBar4.setProgress(progress);
                    }
//                    tv_progress.setText(progress + "%");

                    break;
            }

            return false;
        }
    });


    public void sendFileToDevice(BluetoothDevice device) {
        if (selectedFileUri != null) {
            // Implement the file transfer logic here.
            // Example: Use a BluetoothSocket to connect and transfer the file.
            // Make sure to handle permissions, errors, and file reading properly.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Toast.makeText(this, "Sending file to " + device.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No file selected to send", Toast.LENGTH_SHORT).show();
        }
    }

    class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice passedDevice) {
            device = passedDevice;
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothTransfer.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    return;
                }
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            if (ActivityCompat.checkSelfPermission(BluetoothTransfer.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN);
                return;
            }

            if(myBluetoothAdapter.isDiscovering()) {
                myBluetoothAdapter.cancelDiscovery();
            }

            try {
                if (ActivityCompat.checkSelfPermission(BluetoothTransfer.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    return;
                }
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                // Create new SendReceive instance for this connection
                sendReceive = new BluetoothTransfer.SendReceive(socket);
                sendReceive.start();
            }
            catch(IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothTransfer.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                serverSocket=myBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private long totalExpectedBytes = -1;
        private int totalBytesReceived = 0;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;

            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            try {
                tmpInputStream = bluetoothSocket.getInputStream();
                tmpOutputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            inputStream = tmpInputStream;
            outputStream = tmpOutputStream;
        }

        public void setTotalExpectedBytes(long totalExpectedBytes) {
            this.totalExpectedBytes = totalExpectedBytes;
        }

        public void run() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytes;

            try {
                // First read the filename length (4 bytes)
                byte[] filenameLengthBuffer = new byte[4];
                bytes = inputStream.read(filenameLengthBuffer);
                if (bytes != 4) throw new IOException("Failed to read filename length");
                int filenameLength = ByteBuffer.wrap(filenameLengthBuffer).getInt();

                // Then read the filename
                byte[] filenameBuffer = new byte[filenameLength];
                bytes = inputStream.read(filenameBuffer);
                if (bytes != filenameLength) throw new IOException("Failed to read filename");
                String filename = new String(filenameBuffer, StandardCharsets.UTF_8);

                // Read file size as before
                byte[] sizeBuffer = new byte[8];
                bytes = inputStream.read(sizeBuffer);
                if (bytes == 8) {
                    totalExpectedBytes = ByteBuffer.wrap(sizeBuffer).getLong();
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, (int) totalExpectedBytes, -1,
                            new BluetoothTransfer.FileTransferData(filename, (int) totalExpectedBytes, new byte[0])).sendToTarget();
                } else {
                    throw new IOException("Failed to read file size.");
                }

                // Rest of file reading remains the same
                while (true) {
                    bytes = inputStream.read(buffer);
                    if (bytes == -1) break;
                    byteArrayOutputStream.write(buffer, 0, bytes);

                    byte[] fileBytes = byteArrayOutputStream.toByteArray();
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, (int) totalExpectedBytes, -1,
                            new BluetoothTransfer.FileTransferData(filename, (int) totalExpectedBytes, fileBytes)).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private class ConvertAndSendFileTask extends AsyncTask<Uri, BluetoothTransfer.FileProgress, Boolean> {
        private final Context context;
        private String fileName;

        public ConvertAndSendFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar4.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(Uri... uris) {
            Uri fileUri = uris[0];
            byte[] fileBytes = getFileBytesFromUri(context, fileUri);
            fileName = getFileNameFromUri(context, fileUri);

            if (fileBytes != null && sendReceive != null) {
                try {
                    // Send filename length first
                    byte[] filenameBytes = fileName.getBytes(StandardCharsets.UTF_8);
                    byte[] filenameLengthBytes = ByteBuffer.allocate(4).putInt(filenameBytes.length).array();
                    sendReceive.write(filenameLengthBytes);

                    // Send filename
                    sendReceive.write(filenameBytes);

                    // Send file size
                    byte[] sizeBytes = ByteBuffer.allocate(8).putLong(fileBytes.length).array();
                    sendReceive.write(sizeBytes);

                    // Send file in chunks
                    int totalBytes = fileBytes.length;
                    int bytesSent = 0;
                    int chunkSize = 1024;

                    while (bytesSent < totalBytes) {
                        int remainingBytes = totalBytes - bytesSent;
                        int currentChunkSize = Math.min(chunkSize, remainingBytes);

                        byte[] chunk = new byte[currentChunkSize];
                        System.arraycopy(fileBytes, bytesSent, chunk, 0, currentChunkSize);

                        sendReceive.write(chunk);
                        bytesSent += currentChunkSize;

                        // Calculate progress and publish
                        int progress = (int) ((bytesSent / (float) totalBytes) * 100);
                        publishProgress(new BluetoothTransfer.FileProgress(fileName, progress));
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(BluetoothTransfer.FileProgress... values) {
            if (values.length > 0) {
                BluetoothTransfer.FileProgress progress = values[0];
                selectFileTv.setText(progress.fileName);
                progressBar4.setProgress(progress.progress);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                selectFileTv.setText("Transfer Complete - " + fileName);
                Toast.makeText(context, "File sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                selectDeviceTv.setText("Transfer Failed");
                Toast.makeText(context, "Failed to send file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper class to hold progress information
    private static class FileProgress {
        String fileName;
        int progress;

        FileProgress(String fileName, int progress) {
            this.fileName = fileName;
            this.progress = progress;
        }
    }

    // Add this class to hold both filename and data
    private static class FileTransferData {
        String filename;
        int totalExpectedBytes;
        byte[] data;

        FileTransferData(String filename, int totalExpectedBytes, byte[] data) {
            this.filename = filename;
            this.totalExpectedBytes = totalExpectedBytes;
            this.data = data;
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoveryReceiver);
        unregisterReceiver(pairingReceiver);
    }
}
