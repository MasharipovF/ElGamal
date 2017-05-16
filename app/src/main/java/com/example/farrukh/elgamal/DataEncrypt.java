package com.example.farrukh.elgamal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringDef;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataEncrypt extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static final int MESSAGE_SELECT_CODE = 121;
    private static final int KEY_SELECT_CODE = 122;
    public static String messagePath;
    public static String keyPath;
    public static String messageFromFile, keyFromFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        getPermision();

    }

    private static final int MY_PERMISSIONS_READ_WRITE = 38;

    private void getPermision() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_WRITE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri fileUri;
        switch (requestCode) {
            case MESSAGE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    messagePath = FileOperations.getPath(this, data.getData());
                    fileUri = Uri.parse(messagePath);
                    if (!fileUri.getLastPathSegment().substring(fileUri.getLastPathSegment().lastIndexOf(".")).equals(".txt")) {
                        Toast.makeText(getApplicationContext(), "Incorrect filetype! Please choose only .msg extension", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File messageFile = new File(messagePath);
                    messageFromFile = FileOperations.readFromFile(messageFile);
                    mSectionsPagerAdapter.onEvent(new setterEvent(messageFromFile), 1, (PlaceholderFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem()));
                    //Toast.makeText(getApplicationContext(), messagePath, Toast.LENGTH_SHORT).show();
                }
                break;
            case KEY_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    keyPath = FileOperations.getPath(this, data.getData());
                    fileUri = Uri.parse(keyPath);
                    if (!fileUri.getLastPathSegment().substring(fileUri.getLastPathSegment().lastIndexOf(".")).equals(".key")) {
                        Toast.makeText(getApplicationContext(), "Incorrect filetype! Please choose only .key extension", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    File keyFile = new File(keyPath);
                    keyFromFile = FileOperations.readFromFile(keyFile);
                    mSectionsPagerAdapter.onEvent(new setterEvent(keyFromFile), 2, (PlaceholderFragment) mSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem()));
                    //Toast.makeText(getApplicationContext(), keyPath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class setterEvent {
        private String text;

        setterEvent(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ 1234567890.,!@#$%^&*()_+~-/*+'|:;`\"";
        private static final int MESSAGE_SELECT_CODE = 121;
        private static final int KEY_SELECT_CODE = 122;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String KEY_EXTENSION = "key";
        private static final String MESSAGE_EXTENSION = "txt";
        public static final int number_range = 10000;
        public boolean isKeyGenerated = false;
        public boolean isEncryption = false;


        public ImageButton msg_file_chooser;
        public ImageButton key_file_chooser;
        public EditText msg_edit;
        public TextView p_key;
        public TextView g_key;
        public TextView k_key;
        public TextView y_key;
        public TextView x_key;
        public TextView result_text;
        public FloatingActionButton fab;

        public PlaceholderFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:  // encryption
                    rootView = inflater.inflate(R.layout.fragment_encrypt, container, false);
                    fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
                    msg_file_chooser = (ImageButton) rootView.findViewById(R.id.chooseMsgBtn);

                    msg_edit = (EditText) rootView.findViewById(R.id.message);
                    msg_edit.setHint(getResources().getString(R.string.hint_encrypt));

                    p_key = (TextView) rootView.findViewById(R.id.p_number);
                    g_key = (TextView) rootView.findViewById(R.id.g_number);
                    k_key = (TextView) rootView.findViewById(R.id.k_number);
                    y_key = (TextView) rootView.findViewById(R.id.y_number);
                    result_text = (TextView) rootView.findViewById(R.id.resultMsg);

                    msg_file_chooser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser(MESSAGE_SELECT_CODE);
                        }
                    });

                    break;
                case 2: // decryption
                    rootView = inflater.inflate(R.layout.fragment_decrypt, container, false);
                    msg_file_chooser = (ImageButton) rootView.findViewById(R.id.chooseMsgBtn);
                    key_file_chooser = (ImageButton) rootView.findViewById(R.id.chooseKeyBtn);
                    fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

                    msg_edit = (EditText) rootView.findViewById(R.id.message);

                    p_key = (TextView) rootView.findViewById(R.id.p_number);
                    g_key = (TextView) rootView.findViewById(R.id.g_number);
                    x_key = (TextView) rootView.findViewById(R.id.x_number);
                    result_text = (TextView) rootView.findViewById(R.id.resultMsg);

                    msg_edit.setHint(getResources().getString(R.string.hint_decrypt));

                    msg_file_chooser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser(MESSAGE_SELECT_CODE);
                        }
                    });

                    key_file_chooser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser(KEY_SELECT_CODE);

                        }
                    });

                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_encrypt, container, false);
                    break;

            }

            /*fab.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    msg_edit.setText("");
                    p_key.setText("");
                    g_key.setText("");
                    x_key.setText("");
                    y_key.setText("");
                    k_key.setText("");
                    result_text.setText("");
                    Toast.makeText(getActivity().getApplicationContext(), "Data cleared", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final BigInteger p, g, k, y, x;
                    final String[] encrypted_message_infile = {""};
                    final String[] decrypted_message = new String[1];
                    final String[] key_infile = {""};


                    final String messageToEncryptOrDecrpyt;
                    messageToEncryptOrDecrpyt = msg_edit.getText().toString();
                    switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                        case 1: // encryption
                            if (TextUtils.isEmpty(msg_edit.getText().toString())) {
                                msg_edit.setError("Message is empty!!!");
                                return;
                            }

                            // key generation
                            p = BigInteger.valueOf(getPrime(getMaxIndex(messageToEncryptOrDecrpyt), number_range));
                            g = BigInteger.valueOf(getKey(p, 1)).mod(p);
                            k = BigInteger.valueOf(getKey(p, 3));
                            x = BigInteger.valueOf(getKey(p, 1));
                            y = g.pow(x.intValue()).mod(p);

                            // encryption
                            CalculateTask task = new CalculateTask(getActivity(), messageToEncryptOrDecrpyt, k, p, g, y);
                            task.execute(messageToEncryptOrDecrpyt);
                            task.listener = new CalculateTask.asyncListener() {
                                @Override
                                public void onFinish(String result) {
                                    encrypted_message_infile[0] = result;
                                    key_infile[0] += String.valueOf(p).length() + String.valueOf(p) + String.valueOf(x).length() + String.valueOf(x) + String.valueOf(g).length() + String.valueOf(g) + String.valueOf(k).length() + String.valueOf(k) + String.valueOf(y).length() + String.valueOf(y);

                                    // filling in views
                                    result_text.setText("Encrypted message: \n" + encrypted_message_infile[0]);
                                    p_key.setText(p.toString());
                                    g_key.setText(g.toString());
                                    k_key.setText(k.toString());
                                    y_key.setText(y.toString());

                                    // writing to file
                                    saveFile(getActivity(), encrypted_message_infile[0], key_infile[0]);
                                }
                            };

                            break;
                        case 2: // decryption

                            if (TextUtils.isEmpty(msg_edit.getText().toString())) {
                                msg_edit.setError("Choose file for decryption!!!");
                                return;
                            }

                            CalculateTask decryptTask = new CalculateTask(getActivity(), keyFromFile);
                            decryptTask.execute(messageFromFile);
                            decryptTask.listener = new CalculateTask.asyncListener() {
                                @Override
                                public void onFinish(String result) {
                                    decrypted_message[0] = result;
                                    saveFile(getActivity(), decrypted_message[0], null);
                                    result_text.setText("Decrypted message: \n" + decrypted_message[0]);
                                }
                            };
                            break;
                        default:
                            break;
                    }
                }
            });
            return rootView;
        }
        //////////////////////////// TODO CLASS END METHODS BEGIN

        public void saveFile(Context context, final String msgToSave, final String keyToSave) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.save_dialog, null);
            dialogBuilder.setView(dialogView);

            final EditText msgFileName = (EditText) dialogView.findViewById(R.id.msgFileName);
            final Button cancelBtn = (Button) dialogView.findViewById(R.id.cancelBtn);
            final Button saveBtn = (Button) dialogView.findViewById(R.id.saveBtn);

            final AlertDialog alertDialog = dialogBuilder.create();
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOperations operation = new FileOperations();
                    if (msgToSave != null)
                        operation.writeToFile(msgToSave, msgFileName.getText().toString(), MESSAGE_EXTENSION, getActivity().getApplicationContext());
                    if (keyToSave != null)
                        operation.writeToFile(keyToSave, msgFileName.getText().toString(), KEY_EXTENSION, getActivity().getApplicationContext());
                    alertDialog.dismiss();
                    if ((operation.isSuccesfullyWritten == 2 && getArguments().getInt(ARG_SECTION_NUMBER) == 1) || (operation.isSuccesfullyWritten == 1 && getArguments().getInt(ARG_SECTION_NUMBER) == 2))
                        Toast.makeText(getActivity().getApplicationContext(), "Files succesfully saved in " + Environment.getExternalStorageDirectory() + "/elgamal ", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity().getApplicationContext(), "Error in saving files!!", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.setMessage("Save files under names...");
            alertDialog.setCancelable(true);
            alertDialog.show();
        }

        public ArrayList<BigInteger> decryptHelper(String message) {
            ArrayList<BigInteger> keyList = new ArrayList<>();
            int cursorIndex, numLength;
            String number;
            cursorIndex = 0;

            while (cursorIndex < message.length()) {
                numLength = Character.getNumericValue(message.charAt(cursorIndex));
                number = message.substring(cursorIndex + 1, cursorIndex + numLength + 1);
                cursorIndex = cursorIndex + numLength + 1;
                Log.d("LOG", "number = " + number);
                keyList.add(BigInteger.valueOf(Integer.parseInt(number)));
            }
            return keyList;

        }

        private int getCharIndex(char c) {
            int position = -1;
            for (int i = 0; i < alphabet.length(); i++) {
                if (alphabet.charAt(i) == c) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        private int getPrime(int min, int max) {
            int num = 0;
            Log.d("elgamal", "min = " + Integer.toString(min));
            Random random = new Random();
            boolean flag = false;
            while (!flag) {
                num = random.nextInt(max - min + 1) + min;
                if (num < 2) continue;

                if (num == 2) break;

                if (num % 2 == 0) continue;

                boolean somecheck = false;

                for (int i = 3; i * i <= num; i += 2) {
                    if (num % i == 0) {
                        somecheck = true;
                    }
                }

                flag = !somecheck;
            }
            return num;
        }

        public int getKey(BigInteger p, int p_minus) {
            int num = 0;
            Random random = new Random();
            boolean flag = false;
            while (!flag) {
                num = random.nextInt(number_range);
                if (num > 1 && num < p.intValue() - p_minus) flag = true;
            }
            return num;
        }

        public int getMaxIndex(String message) {
            int maxx = -1;

            for (int i = 0; i < message.length(); i++) {
                Log.d("elgamal", "index = " + getCharIndex(message.charAt(i)) + "  symbol = '" + message.charAt(i) + "'");
                if (getCharIndex(message.charAt(i)) >= maxx) maxx = getCharIndex(message.charAt(i));
            }
            return maxx;
        }

        public void showFileChooser(int code) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                switch (code) {
                    case MESSAGE_SELECT_CODE:
                        getActivity().startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Upload"),
                                MESSAGE_SELECT_CODE);
                        break;
                    case KEY_SELECT_CODE:
                        getActivity().startActivityForResult(
                                Intent.createChooser(intent, "Select a File to Upload"),
                                KEY_SELECT_CODE);
                        break;
                }

            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(getActivity().getApplicationContext().getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            }

        }


        public void onEvent(final setterEvent event, int type, PlaceholderFragment fragment) {

            // your implementation
            switch (type) {
                case 1:
                    fragment.msg_edit.setText(event.getText());
                    break;
                case 2:
                    ArrayList<BigInteger> keys = decryptHelper(event.getText());
                    p_key.setText(keys.get(0).toString());
                    g_key.setText(keys.get(2).toString());
                    x_key.setText(keys.get(1).toString());
                    break;
                default:
                    break;
            }
            //Toast.makeText(getActivity(), event.getText(), Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        PlaceholderFragment fragment;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            fragment = PlaceholderFragment.newInstance(position + 1);
            Log.d("elgamal", "chngd");
            return fragment;

        }

        private void onEvent(setterEvent event, int type, PlaceholderFragment fragment) {
            fragment.onEvent(event, type, fragment);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ENCRYPTION";
                case 1:
                    return "DECRYPTION";
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
