package com.example.farrukh.elgamal;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by Farrukh on 14.05.2017.
 */

class CalculateTask extends AsyncTask<String, Integer, String> {

    private ProgressDialog progressDialog;
    private Context ctx;
    private BigInteger k, p, g, y;
    private String messageToProcess;
    asyncListener listener = null;
    private String key;
    private boolean isEncryption = false;
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZабвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ 1234567890.,!@#$%^&*()_+~-/*'|:;[]{}=`\"";


    CalculateTask(Context context, String messageToProcess, BigInteger k, BigInteger p, BigInteger g, BigInteger y) {
        this.messageToProcess = messageToProcess;
        this.k = k;
        this.p = p;
        this.g = g;
        this.y = y;
        ctx = context;
        isEncryption = true;
    }

    CalculateTask(Context context, String key) {
        this.key = key;
        ctx = context;
        isEncryption = false;
    }

    @Override
    protected String doInBackground(String... params) {
        if (isEncryption) return encrypt(params[0], k, p, g, y);
        else return decrypt(params[0], key);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Calculating...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        listener.onFinish(result);
        progressDialog.dismiss();
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


    private String encrypt(String message, BigInteger k, BigInteger p, BigInteger g, BigInteger y) {
        BigInteger num, r, e;
        String result = "";
        int i;
        for (i = 0; i < message.length(); i++) {
            num = BigInteger.valueOf(getCharIndex(message.charAt(i)));
            r = g.pow(k.intValue()).mod(p);
            e = (y.pow(k.intValue()).multiply(num)).mod(p);
            result += String.valueOf(r).length() + String.valueOf(r) + String.valueOf(e).length() + String.valueOf(e);
        }
        return result;
    }

    private String decrypt(String message, String key) {
        Log.d("LOG", "1231 = " + message);
        String result = "";
        BigInteger r, e, p_val, x_val;
        ArrayList<BigInteger> messageList;
        ArrayList<BigInteger> keyList;
        int cursorIndex = 0;
        BigInteger decCharIndex;
        messageList = decryptHelper(message);
        keyList = decryptHelper(key);
        p_val = keyList.get(0);
        x_val = keyList.get(1);
        while (cursorIndex < messageList.size()) {
            r = messageList.get(cursorIndex++);
            e = messageList.get(cursorIndex++);
            decCharIndex = r.pow(p_val.intValue() - 1 - x_val.intValue()).multiply(e).mod(p_val);
            result += alphabet.charAt(decCharIndex.intValue());
        }
        return result;
    }

    private ArrayList<BigInteger> decryptHelper(String message) {
        ArrayList<BigInteger> keyList = new ArrayList<>();
        int cursorIndex, numLength;
        String number;
        cursorIndex = 0;

        while (cursorIndex < message.length()) {
            numLength = Character.getNumericValue(message.charAt(cursorIndex));
            number = message.substring(cursorIndex + 1, cursorIndex + numLength + 1);
            cursorIndex = cursorIndex + numLength + 1;
            keyList.add(BigInteger.valueOf(Integer.parseInt(number)));
        }
        return keyList;

    }


    interface asyncListener {
        void onFinish(String result);
    }
}
