package com.vise.bluetoothchat.activity;

/**
 * Created by Phoney on 2017/12/2.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vise.bluetoothchat.R;

import java.io.File;

public class FileManageActivity extends AppCompatActivity {
    private Button mButton;
    private EditText mKeyword;
    private TextView mResult;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playfile_list);
        mKeyword=(EditText)findViewById(R.id.filename);
        mButton=(Button)findViewById(R.id.music_selected);
        mResult=(TextView)findViewById(R.id.fileresult);
        mButton.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                String keyword=mKeyword.getText().toString();
                if(keyword.equals(""))
                {
                    mResult.setText("关键字不能为空!!");
                }
            }
        });
    }
    private String searchFile(String keyword)
    {
        String result="";
        File[] files=new File("/").listFiles();
        for(File f:files)
        {
            if(f.getName().indexOf(keyword)>=0)
            {
                result+=f.getPath()+"\n";
            }
        }
        if(result.equals("")) result="找不到文件！！";
        return result;
    }
}
