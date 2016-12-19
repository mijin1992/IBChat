package com.brasco.simwechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brasco.simwechat.app.Constant;

public class InputActivity extends IBActivity {

    TextView m_txtValue = null;
    Button m_btnSave = null;

    int m_Type = 0;
    String m_Value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Intent intent = getIntent();
        m_Type = intent.getIntExtra(Constant.REQ_INPUT_TYPE, 0);
        m_Value = intent.getStringExtra(Constant.REQ_INPUT_STRING);

        m_txtValue = (TextView) findViewById(R.id.txt_value);
        m_btnSave = (Button) findViewById(R.id.button_save);
        m_btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strValue = m_txtValue.getText().toString();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constant.REQ_INPUT_TYPE, m_Type);
                returnIntent.putExtra(Constant.REQ_INPUT_STRING, strValue);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        m_txtValue.setText(m_Value);
        if (m_Type == 0)
            ActionBar("Edit Name");
        else
            ActionBar("What's Up");
    }
}
