package icerapi.testaar;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android_serialport_api.SerialPort;
import icerapi.icerapi.ICERAPI;


public class MainActivity extends AppCompatActivity {
    //Api mApi;
    ICERAPI mICERAPI;
    Button mICERAPIButton;
    Button mCopyXMLButton;
    //SetPara
    Button mSetPara;
    Dialog SetPara;
    //SetPara
    String mApplicationPath;
    String mOutputString = "";
    String mFinalOutputString = "";
    EditText mMoneyEdit;
    Spinner mTransactionTypeSpinner;
    String[] mTransactionTypeString = {
            "216000:六筆查詢", "296000:票卡查詢", "390061:社福卡展期", "390861:自動加值開啟", "606100:扣款",
            "620061:扣款退貨", "801061:加值", "816100:取消交易", "825799:自動加值",
            "881799:SignOnQuery", "881999:SignOn", "882999:悠遊卡配對", "900099:悠遊卡結帳"};
    String mMoney;
    //default 交易明細
    String mTransactionType = "216000";
    TextView mOutput;
    final String TAG = "TestAAR";
    final int timeout = 10;
    //thread
    private Handler mUI_Handler = new Handler();
    private Handler mThreadHandler;
    private HandlerThread mThread;
    String[] sICERParName = {"LogFlag", "DLLVersion", "TCPIPTimeOut", "LogCnt", "ICERINI_Name", "ComPort", "ComPort2", "ECC_IP", "ECC_Port", "ICER_IP", "ICER_Port", "CMAS_IP", "CMAS_Port", "TMLocationID", "TMID", "TMAgentNumber", "LocationID", "NewLocationID", "SPID", "NewSPID", "Slot", "BaudRate", "OpenCom", "MustSettleDate", "ReaderMode", "BatchFlag", "OnlineFlag", "ICERDataFlag", "MessageHeader", "DLLMode", "AutoLoadMode", "MaxALAmt", "Dev_Info", "TCPIP_SSL", "CMASAdviceVerify", "AutoSignOnPercnet", "AutoLoadFunction", "VerificationCode", "ReSendReaderAVR", "XMLHeaderFlag", "FolderCreatFlag", "BLCName", "CMASMode", "POS_ID", "PacketLenFlag", "CRT_FileName", "Key_FileName", "ICERFlowDebug"};
    String[] sICERParValue = {"1", "2", "30", "30", "ICERINI.xml", "5", "/dev/ttyXRM0", "172.16.11.20", "8902", "172.25.17.95", "8303", "211.78.134.165", "7000", "0000100002", "01", "0001", "0", "0", "0", "0", "11", "115200", "4", "10", "3", "1", "1", "1", "99909020", "0", "0", "1000", "1122334455", "1", "0", "0", "0", "0", "0", "1", "1", "BLC03328A_140422.BIG", "0", "0", "0", "", "", "0"};
    String[] defaultParValue = {"1", "2", "30", "30", "ICERINI.xml", "5", "/dev/ttyXRM0", "172.16.11.20", "8902", "172.25.17.95", "8303", "211.78.134.165", "7000", "0000100002", "01", "0001", "0", "0", "0", "0", "11 ", "115200", "4", "10", "3", "1", "1", "1", "99909020", "0", "0", "1000", "1122334455", "1", "0", "0", "0", "0", "0", "1", "1", "BLC03328A_140422.BIG", "0", "0", "0", "", "", "0"};

    String[] sICERReqName = {"T1100", "T1101", "T4108", "T4830", "T5501", "T5591", "T5592", "T5593"};
    String[] sICERReqValue = {"", "", "0", "0", "00000001", "", "", "1"};
    String[] defaultICERReqValue = {"", "", "0", "0", "00000001", "", "", "1"};
    //CheckBox
    CheckBox mCheckBox;
    EditText mT1100;
    Boolean mCheck = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //
    String t3700, t1300, t4100;
    //    //for 21600 900099
//    EditText mT21600IndexEdit, mT900099CountEdit, mT900099TotalEdit;
//    String mT21600Index, mT900099Count, mT900099Total;
    private GoogleApiClient client;

    //Loop Test
    ToggleButton mLoopToggleButton;
    Boolean mLoop = false;
    String mLoopResult;
    int mCount = 0;
    int mPass = 0;
    int mFail = 0;
    int mSleep = 0;

    //
    Button mClean, mSetReq, mLoadPara;
    final String ICERINI_Path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICERINI/";
    String mLoadFile = "";

    //thread
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApplicationPath = MainActivity.this.getFilesDir().toString();
        Log.d(TAG, "mApplicationPath: " + mApplicationPath);

        mICERAPIButton = (Button) findViewById(R.id.ICERAPI_exe);
        mCopyXMLButton = (Button) findViewById(R.id.copyXML);
        mLoopToggleButton = (ToggleButton) findViewById(R.id.loop);
        //SetPara
        mSetPara = (Button) findViewById(R.id.setPara);
        //SetPara
        mOutput = (TextView) findViewById(R.id.output);
        mTransactionTypeSpinner = (Spinner) findViewById(R.id.transactionType);
        mMoneyEdit = (EditText) findViewById(R.id.amount);
        mMoney = mMoneyEdit.getText().toString();
        //
        mClean = (Button) findViewById(R.id.clean);
        mSetReq = (Button) findViewById(R.id.setReq);
        mLoadPara = (Button) findViewById(R.id.loadPara);
        //create ICERINI folder
        createFolder(ICERINI_Path);
        //create /data/data/[packageName]/files/BlcFile for 黑名單
        createFolder(mApplicationPath + "/BlcFile");
        //create data/data/[packageName]/files/ICERData
        createFolder(mApplicationPath + "/ICERData");
        createFolder(mApplicationPath + "/ICERData/BlcFile");

        mClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //openport();
                mOutput.setText("");
            }
        });

        mSetReq.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View view = inflater.inflate((R.layout.dialog2), null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("交易參數修改(ICERAPI.REQ)");
                builder.setView(view);
                //T1100 check box
                mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
                mT1100 = (EditText) view.findViewById(R.id.T1100);
                mCheckBox.setChecked(mCheck);
                if (mCheck)
                    mT1100.setEnabled(false);
                mCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                        if (isChecked) {
                            mT1100.setEnabled(false);
                            mT1100.setText("");
                            mCheck = isChecked;
                        } else {
                            //CheckBox狀態 : 未勾選，隱藏TextView
                            mT1100.setEnabled(true);
                            mCheck = isChecked;
                        }
                    }
                });

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vdGetParaDataFromDialog(view, sICERReqName, sICERReqValue, 0);
                        vdSetParaDataFromDialog(view, sICERReqName, sICERReqValue, 0);
                    }
                });
                builder.setNeutralButton("預設", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < sICERReqName.length; i++)
                            sICERReqValue[i] = defaultICERReqValue[i];
                        vdSetParaDataFromDialog(view, sICERReqName, sICERReqName, 0);
                    }
                });
                builder.setNegativeButton("取消", null);
                vdSetParaDataFromDialog(view, sICERReqName, sICERReqValue, 0);
                builder.show();
            }
        });
        mLoadPara.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View view = inflater.inflate((R.layout.dialog3), null);
                //final View view2 = inflater.inflate(R.layout.dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("載入參數(ICERINI.xml)");
                builder.setView(view);
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mLoadFile.endsWith("xml")) {
                            restoreFile(mLoadFile);
//                            getValueByXml();
//                            vdSetParaDataFromDialog(view2, sICERParName, sICERParValue, 4);
                        }
                    }
                });
                builder.setNeutralButton("刪除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(ICERINI_Path, mLoadFile);
                        if (file.exists()) {
                            file.delete();
                            Toast.makeText(MainActivity.this, "已刪除" + mLoadFile, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                //spinner
                Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                File folder = new File(ICERINI_Path + "/");
                File[] listOfFiles = folder.listFiles();
                final ArrayList<String> fileName = new ArrayList<>();
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        fileName.add(file.getName());

                    }
                }
                mLoadFile = "";
                ArrayAdapter<String> fileList = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        fileName);
                spinner.setAdapter(fileList);
                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(MainActivity.this, fileName.get(position), Toast.LENGTH_LONG).show();
                        mLoadFile = fileName.get(position);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mLoadFile = "";
                    }
                });
                builder.show();
            }
        });

//        mT21600IndexEdit = (EditText) findViewById(R.id.index);
//        mT900099CountEdit = (EditText) findViewById(R.id.count);
//        mT900099TotalEdit = (EditText) findViewById(R.id.total);
//
//        mT21600Index = mT21600IndexEdit.getText().toString();
//        mT900099Count = mT900099CountEdit.getText().toString();
//        mT900099Total = mT900099TotalEdit.getText().toString();

        ArrayAdapter<String> TransactionType = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, mTransactionTypeString);
        mTransactionTypeSpinner.setAdapter(TransactionType);
        //
        //loadPara();
        mTransactionTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTransactionType = mTransactionTypeString[position].substring(0, 6);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTransactionType = mTransactionTypeString[0].substring(0, 6);
            }
        });
        //thread
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        //thread
        mICERAPIButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoop = false;
                //初始化
                mFinalOutputString = "";
                mOutputString = "";
                mOutput.setText("");
                //Toast.makeText(MainActivity.this,"執行ICERAPI---timeout: "+timeout+"秒",Toast.LENGTH_LONG).show();
                //交易金額
                mMoney = mMoneyEdit.getText().toString();
//                mT21600Index = mT21600IndexEdit.getText().toString();
//                mT900099Count = mT900099CountEdit.getText().toString();
//                mT900099Total = mT900099TotalEdit.getText().toString();
                Log.d(TAG, "Transaction = " + mTransactionType + "   " + "Money = " + mMoney);
                //thread
                // createXML();
                FileOutputStream out = null;
                String tempString = TxnPar.createRequsetFile(mTransactionType, mMoney, sICERReqValue, t4100, t3700, t1300);
                Log.d(TAG, "REQ_FILE: " + tempString);
                try {

                    if (checkICERDataFlag())
                        out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ");
                    else
                        out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ");
                    out.write(tempString.toString().getBytes());
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //create REQ.ok
                try {
                    if (checkICERDataFlag())
                        out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ.OK");
                    else
                        out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ.OK");
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mThreadHandler.post(ICERAPI_exe);
                //thread
            }
        });

        //copy file to data/data/[packageName]/files
        mCopyXMLButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //call ICER_API
                inFileProcess("ICERINI.xml");
                //inFileProcess("ICERAPI.REQ");
                inFileProcess("ICERSSL.CRT");
                Log.d(TAG, "infileProcess----");
                for (int i = 0; i < sICERParValue.length; i++) {
                    if (sICERParValue[i].endsWith("BIG")) {
                        Log.d(TAG, "infileProcess-" + sICERParValue[i]);
                        inFileProcess(sICERParValue[i]);
                    }
                }
                //inFileProcess("BLC02200A_161207.BIG");
                //TestFile
                //inFileProcess("BLC.BIG");
                deleteExistFile(mApplicationPath + "/ICERData/ICERAPI_CMAS.rev");
                deleteExistFile(mApplicationPath + "/ICERData/ICERAPI_CMAS.adv");
                deleteExistFile(mApplicationPath + "/ICERAPI_CMAS.rev");
                deleteExistFile(mApplicationPath + "/ICERAPI_CMAS.adv");
//                for (int i = 0; i < sICERParName.length; i++)
//                    sICERParValue[i] = defaultParValue[i];
//                savePara();

            }
        });
        //set ICERINI.xml
        mSetPara.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View view = inflater.inflate((R.layout.dialog), null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("參數修改(ICERINI.xml)");
                builder.setView(view);
                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Create Parameter");
                        //Log.d(TAG, "" + ((EditText) view.findViewById(R.id.ComPort)).getText().toString());
                        vdGetParaDataFromDialog(view, sICERParName, sICERParValue, 4);
                        createParameter();
                        vdSetParaDataFromDialog(view, sICERParName, sICERParValue, 4);
                        //savePara();
                        //backup file to ICERINI/fileName
                        backupParameter(sICERParValue[4]);
                    }
                });
                builder.setNeutralButton("預設", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < sICERParName.length; i++)
                            sICERParValue[i] = defaultParValue[i];
                        createParameter();
                        //savePara();
                    }
                });
                builder.setNegativeButton("取消", null);
                getValueByXml();
                vdSetParaDataFromDialog(view, sICERParName, sICERParValue, 4);
                builder.show();
            }
        });
        //Loop Test Button
        mLoopToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        mSleep = 0;
                        mCount = 0;
                        mPass = 0;
                        mFail = 0;
                        mLoop = true;
                        //初始化
                        mFinalOutputString = "";
                        mOutputString = "";
                        mOutput.setText("");
                        mMoney = mMoneyEdit.getText().toString();
//                        mT21600Index = mT21600IndexEdit.getText().toString();
//                        mT900099Count = mT900099CountEdit.getText().toString();
//                        mT900099Total = mT900099TotalEdit.getText().toString();
                        //thread
                        //createXML();
                        FileOutputStream out = null;
                        String tempString = TxnPar.createRequsetFile(mTransactionType, mMoney, sICERReqValue, t4100, t3700, t1300);
                        Log.d(TAG, "REQ_FILE: " + tempString);
                        try {
                            if (checkICERDataFlag())
                                out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ");
                            else
                                out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ");
                            out.write(tempString.toString().getBytes());
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //create REQ.ok
                        try {
                            if (checkICERDataFlag())
                                out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ.OK");
                            else
                                out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ.OK");
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mThreadHandler.post(ICERAPI_exe);
                    } catch (Exception e) {

                    }
                    //thread
                } else {
                    mLoop = false;
                    if (mThread != null) {
                        mThread.interrupt();
                        mThread = null;
                    }

                }
            }
        });
        //SetPara
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void createFolder(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                File tDataPath = new File(path);
                //識別指定的目錄是否存在，false則建立；
                if (tDataPath.exists() == false) {
                    tDataPath.mkdir();
                }
            } else {
                requestPermission();
            }
        } else {
            File tDataPath = new File(path);
            //識別指定的目錄是否存在，false則建立；
            if (tDataPath.exists() == false) {
                tDataPath.mkdir();
            }
        }
    }

    private void getValueByXml() {
        InputStream in = null;
        byte[] buffer = new byte[1024];
        int read;
        String value = null;
        String input = "";
        File file = new File(mApplicationPath, "ICERINI.xml");
        if (file.exists()) {
            try {
                in = new FileInputStream(file);
                while ((read = in.read(buffer)) != -1) {
                    input += new String(buffer);
                }
                in.close();
                //Toast.makeText(MainActivity.this,input,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 4; i < sICERParName.length; i++) {
                value = findTag(input, sICERParName[i]);
                if (value != null)
                    sICERParValue[i] = value;
                else
                    sICERParValue[i] = "";
                value = null;
            }
        }
//        }else{
//            Toast.makeText(MainActivity.this,"請先執行預設參數按鈕",Toast.LENGTH_LONG).show();
//        }
    }

    private void restoreFile(String inputFile) {
        InputStream in = null;
        OutputStream out = null;
        byte[] buffer = new byte[1024];
        int read;

        File inFile = new File(ICERINI_Path, inputFile);
        File outFile = new File(mApplicationPath, "ICERINI.xml");
        try {
            in = new FileInputStream(inFile);

            out = new FileOutputStream(outFile);

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backupParameter(String outputFile) {
        InputStream in = null;
        OutputStream out = null;
        byte[] buffer = new byte[1024];
        int read;
        if (outputFile.endsWith(".xml")) {
            File inFile = new File(mApplicationPath, "ICERINI.xml");
            File outFile = new File(ICERINI_Path, outputFile);
            try {
                in = new FileInputStream(inFile);

                out = new FileOutputStream(outFile);

                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPara() {
        SharedPreferences settings = getSharedPreferences("ICERINI", MODE_PRIVATE);
        for (int i = 0; i < sICERParValue.length; i++)
            sICERParValue[i] = settings.getString(sICERParName[i], defaultParValue[i]);
    }

    private void savePara() {
        SharedPreferences settings = getSharedPreferences("ICERINI", MODE_PRIVATE);
        SharedPreferences.Editor prefEdit = settings.edit();
        for (int i = 0; i < sICERParName.length; i++) {
            prefEdit.putString(sICERParName[i], sICERParValue[i]);
        }
        prefEdit.commit();
    }

    //run ICERAPI
    private Runnable ICERAPI_exe = new Runnable() {

        public synchronized void run() {
            int inTime;
            //delete ICERAPI.RES.OK   ICERAPI.RES
            deleteExistFile(mApplicationPath + "/ICERData/ICERAPI.RES.OK");
            deleteExistFile(mApplicationPath + "/ICERData/ICERAPI.RES");
            deleteExistFile(mApplicationPath + "/ICERAPI.RES.OK");
            deleteExistFile(mApplicationPath + "/ICERAPI.RES");
            //call ICER_API
            //Log.d(TAG, "Environment.getExternalStorageDirectory().getAbsolutePath()=" + Environment.getExternalStorageDirectory().getAbsolutePath());
            mICERAPI = new ICERAPI(MainActivity.this.getFilesDir().toString(), Environment.getExternalStorageDirectory().getAbsolutePath(), MainActivity.this);

            //showMessage ICERAPI.RES
            inTime = 0;
            while (true) {
                File file;
                if (checkICERDataFlag())
                    file = new File(mApplicationPath + "/ICERData/ICERAPI.RES.OK");
                else
                    file = new File(mApplicationPath + "/ICERAPI.RES.OK");
                if (file.exists()) {
                    String[] result = new String[6];
                    if (checkICERDataFlag())
                        mOutputString = showFileOnToast(mApplicationPath + "/ICERData/ICERAPI.RES", false);
                    else
                        mOutputString = showFileOnToast(mApplicationPath + "/ICERAPI.RES", false);
                    //get t3700 t1300 t4100
                    if (findTag(mOutputString, "T3700") != null)
                        t3700 = findTag(mOutputString, "T3700");
                    if (findTag(mOutputString, "T1300") != null)
                        t1300 = findTag(mOutputString, "T1300");
                    if (findTag(mOutputString, "T4100") != null)
                        t4100 = findTag(mOutputString, "T4100");
                    //get t3700 t1300 t4100
                    for (int i = 0; i <= 6; i++) {
                        if (mOutputString.contains("T390" + i)) {
                            result[i] = findTag(mOutputString, "T390" + i);
                            mFinalOutputString += "T390" + i + " =  " + result[i] + "\n";
                        }
                    }
                    Log.d(TAG, "output = " + mFinalOutputString);
                    mLoopResult = result[1];

                    break;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inTime += 50;

                if (inTime >= timeout * 1000)
                    break;
            }
            mUI_Handler.post(setUI);
            if (!mLoopResult.equals("0")) {
                mLoop = false;
            }

            if (mLoop) {
                FileOutputStream out = null;
                String tempString = TxnPar.createRequsetFile(mTransactionType, mMoney, sICERReqValue, t4100, t3700, t1300);
                Log.d(TAG, "REQ_FILE: " + tempString);
                try {
                    if (checkICERDataFlag())
                        out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ");
                    else
                        out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ");
                    out.write(tempString.toString().getBytes());
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mThreadHandler.postDelayed(ICERAPI_exe, 2 * 1000);
            }
        }
    };

    private String findTag(String mOutputString, String tag) {
        if (mOutputString.contains(tag)) {
            int start = mOutputString.indexOf(tag);
            int end = mOutputString.indexOf("/" + tag);
            String tempString = mOutputString.substring(start, end);
            start = tempString.indexOf(">");
            end = tempString.indexOf("<");
//            Log.d(TAG, "outputTagResult:" + "S/T:"+start+"/"+end);
//            Log.d(TAG, "outputTagResult:" + tempString.substring(start + 1, end));
            if (end - start > 1)
                return tempString.substring(start + 1, end);
            else
                return null;
        }
        return null;
    }

    //for LoopTest output
    private Runnable setUI = new Runnable() {

        public void run() {
            if (!mLoop) {
                if (mFinalOutputString.length() > 0)
                    Toast.makeText(MainActivity.this, mFinalOutputString, Toast.LENGTH_SHORT).show();
                mOutput.setText(mOutputString);
            } else {
                mCount++;
                if (mLoopResult.equals("0"))
                    mPass++;
                else
                    mFail++;

                mOutputString = "Count: " + mCount + " Pass: " + mPass + " Fail: " + mFail + " Sleep: " + mSleep;
                //Toast.makeText(MainActivity.this, mOutputString, Toast.LENGTH_SHORT).show();
                mOutput.setText(mOutputString);
            }

        }

    };

    public void inFileProcess(String bFileName) {
        //讀取檔案
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = null;
        byte[] buffer = new byte[1024];
        int read;

        try {
            in = assetManager.open(bFileName);
            File outFile;
            if (bFileName.endsWith("BIG")) {
                if(checkICERDataFlag())
                    outFile = new File(mApplicationPath + "/ICERData/BlcFile", bFileName);
                else
                    outFile = new File(mApplicationPath + "/BlcFile", bFileName);
            } else {
                outFile = new File(mApplicationPath, bFileName);
            }
            out = new FileOutputStream(outFile);

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

        } catch (IOException e) {
            Log.e("tag", "Failed to copy asset file: " + bFileName, e);
        }
    }

    public String showFileOnToast(String bFileName, Boolean showToast) {
        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        try {
            //開啟 getFilesDir() 目錄底下名稱為 test.txt 檔案
            in = new FileInputStream(bFileName);
            //讀取該檔案的內容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line + "\n");
            }
        } catch (Exception e) {
            ;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                ;
            }
        }
        if (showToast)
            Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
        return data.toString();
    }

    public void deleteExistFile(String bFileName) {
        File file = new File(bFileName);
        Log.d(TAG, "deleteFile: " + bFileName);
        if (file.exists())
            file.delete();
    }

    public String sBuildXMLTagValue(int inTableCnt, String sTag, String sVlaue) {
        String sStr = "";
        int i;

        for (i = 0; i < inTableCnt; i++)
            sStr += "\t";

        sStr += "<" + sTag + ">" + sVlaue + "</" + sTag + ">" + "\n";
        //Log.e("sBuildXMLTagValue", "sStr: " + sStr);
        return sStr;
    }

    public void createXML() {
        FileOutputStream out = null;
        String tempString = "";

        try {
            out = openFileOutput("ICERAPI.REQ", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //header
        tempString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n";
        tempString += "<TransXML>\n";
        tempString += "\t<TRANS>\n";

        //T0100 //Message Type
        tempString += sBuildXMLTagValue(2, "T0100", "0800");
        /*tempString += "\t\t<T0100>";
                tempString += "0800";
                tempString += "</T0100>\n";*/

        //T0300 //交易類別
        tempString += sBuildXMLTagValue(2, "T0300", mTransactionType);
        /*tempString += "\t\t<T0300>";
        tempString += mTransactionType;
        tempString += "</T0300>\n";*/

        if (mTransactionType.equals("606100") || mTransactionType.equals("801061")) {
            //T0400 //金額
            tempString += sBuildXMLTagValue(2, "T0400", mMoney + "00");
            /*tempString += "\t\t<T0400>";
            tempString += mMoney + "00";
            tempString += "</T0400>\n";*/
        }
        //T1100 T1101 T1200 時間
        SimpleDateFormat sdFormat;
        Date date;
        String strDate = "";
        sdFormat = new SimpleDateFormat("HHmmss");
        date = new Date();
        strDate = sdFormat.format(date);
        for (int i = 1; i <= 3; i++) {
            if (i == 1)
                tempString += sBuildXMLTagValue(2, "T1100", strDate);
            else if (i == 2)
                tempString += sBuildXMLTagValue(2, "T1101", strDate);
            else
                tempString += sBuildXMLTagValue(2, "T1200", strDate);
            /*tempString += "\t\t<T1" + i + "00>";
            tempString += strDate;
            tempString += "</T1" + i + "00>\n";*/
        }

        //T1300 日期
        strDate = "";
        sdFormat = new SimpleDateFormat("yyyyMMdd");
        strDate = sdFormat.format(date);
        tempString += sBuildXMLTagValue(2, "T1300", strDate);
        /*tempString += "\t\t<T1300>";
        tempString += strDate;
        tempString += "</T1300>\n";*/

        //if(mTransactionType != "881799" && mTransactionType != "881899" && mTransactionType != "881999")
        {
            //T4108
            tempString += sBuildXMLTagValue(2, "T4108", "0");
            /*tempString += "\t\t<T4108>";
            tempString += "0";
            tempString += "</T4108>\n";*/
        }

        //T4830
        tempString += sBuildXMLTagValue(2, "T4830", "0");
        /*tempString += "\t\t<T4830>";
        tempString += "0";
        tempString += "</T4830>\n";*/

        //T5501
        tempString += sBuildXMLTagValue(2, "T5501", "000001");
        /*tempString += "\t\t<T5501>";
        tempString += "000001";
        tempString += "</T5501>\n";*/

        //T5503//TM Location ID
        tempString += sBuildXMLTagValue(2, "T5503", "0000100001");
        /*tempString += "\t\t<T5503>";
        tempString += "0000100001";
        tempString += "</T5503>\n";*/

        Log.d(TAG, "REQ_FILE: " + tempString);
        //將資料寫入檔案中
        try {
            out.write(tempString.toString().getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //showFileOnToast("ICERAPI.REQ",true);
    }

    //ICERINI
    public void vdGetParaDataFromDialog(View vDialogView, String[] name, String[] value, int start) {
        int i, inResID;

        for (i = start; i < name.length; i++) {
            inResID = getResources().getIdentifier(name[i], "id", "icerapi.testaar");
            value[i] = ((EditText) vDialogView.findViewById(inResID)).getText().toString();
        }
    }

    public void vdSetParaDataFromDialog(View vDialogView, String[] name, String[] value, int start) {
        int i, inResID;
        EditText temp;

        for (i = start; i < name.length; i++) {
            inResID = getResources().getIdentifier(name[i], "id", "icerapi.testaar");
            temp = ((EditText) vDialogView.findViewById(inResID));
            temp.setText("" + value[i]);
        }
    }

    public void createParameter() {
        FileOutputStream out = null;
        String tempString = "";
        int i;
        deleteExistFile("ICERINI.xml");
        try {
            out = openFileOutput("ICERINI.xml", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //header
        tempString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n";
        tempString += "<TransXML>\n";
        tempString += "\t<TRANS>\n";

        for (i = 0; i < sICERParName.length; i++) {
            tempString += sBuildXMLTagValue(2, sICERParName[i], sICERParValue[i]);
        }
        //Log.d("createParameter:",tempString);

        tempString += "\t</TRANS>\n";
        tempString += "</TransXML>\n";
        Log.d("INI", "Data:" + tempString);
        //將資料寫入檔案中
        try {
            out.write(tempString.toString().getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //showFileOnToast("ICERINI.xml",true);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://icerapi.testaar/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://icerapi.testaar/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    //Write external permission
    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do your work
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public Boolean checkICERDataFlag() {
        for (int i = 0; i < sICERParName.length; i++) {
            if (sICERParName[i].equals("ICERDataFlag"))
                if (sICERParValue[i].equals("1"))
                    return true;
                else
                    return false;
        }
        return false;
    }

    //Open Serial port
    private SerialPort serialPort;
    public static final String TTY_USB5 = "/dev/ttyXRM1";
    private String deviceName = TTY_USB5;
    private static final int BAUD_RATE = 115200;

    public void openport() {
        Toast.makeText(this, "start-open-port", Toast.LENGTH_SHORT).show();
        try {
            if (serialPort != null) {
                serialPort.close();
                serialPort = null;
            }
            serialPort = new SerialPort(new File(deviceName), BAUD_RATE, 0);
            Toast.makeText(this, "OPEN PORT", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Seelp start --");
            Thread.sleep(5000);
            Log.d(TAG, "Seelp end --");
        } catch (IOException e) {
            Toast.makeText(this, "NOT OPEN PORT", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "can't open serial port");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (serialPort != null) {
                serialPort.close();
                Log.d(TAG, "Close serial port");
            }
        }
    }
}

