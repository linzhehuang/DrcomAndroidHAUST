package haust.drcomandroidhaust.drcom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import haust.drcomandroidhaust.drcom.drcom.DrcomService;
import haust.drcomandroidhaust.drcom.drcom.HostInfo;
import haust.drcomandroidhaust.drcom.drcom.WifiUtils;

import haust.drcomandroidhaust.R;

//适配其他学校：
// 1. 修改 WifiUtils.currentIsSchoolWifi 的判断学校wifi功能
// 2. 修改 DrcomConfig
public class MainActivity extends Activity {
    private String mAccount;
    private String mPassword;
    private EditText edAccount;
    private EditText edPassword;
    private Button btnLogin;
    private String spFileName = "store";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edAccount = (EditText)this.findViewById(R.id.editText1);
        edPassword = (EditText)this.findViewById(R.id.editText2);
        btnLogin = (Button)this.findViewById(R.id.button1);

        SharedPreferences sp = getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        edAccount.setText(sp.getString("account",""));
        edPassword.setText(sp.getString("password",""));

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){
                mAccount = edAccount.getText().toString();
                mPassword = edPassword.getText().toString();

                //检查wifi是否打开且是否为学校wifi
                WifiUtils wifiUtils = new WifiUtils(MainActivity.this);
                if(!wifiUtils.isWifiOpened()){
                    Toast.makeText(MainActivity.this, "wifi is closed", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isSchoolWifi = WifiUtils.currentIsSchoolWifi(wifiUtils);
                if(!isSchoolWifi){
                    Toast.makeText(MainActivity.this, "not a school wifi", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mac = wifiUtils.getMacAddress();
                HostInfo info = new HostInfo(mAccount,mPassword, mac);
                Intent startIntent = new Intent(MainActivity.this, DrcomService.class);
                startIntent.putExtra("info",info);
                startService(startIntent);

                SharedPreferences.Editor editor = getSharedPreferences(spFileName, Context.MODE_PRIVATE).edit();
                editor.putString("account", mAccount);
                editor.putString("password",mPassword);
                editor.apply();
            }
        });

    }


}

