package com.example.activity_information;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;


import com.google.android.material.button.MaterialButton;

public class Calculator extends AppCompatActivity implements View.OnClickListener{


    TextView resultTv, solutionTv;
    MaterialButton buttonAC, buttonDel, buttonPercent, buttonDot;
    MaterialButton buttonDivide, buttonMultiply, buttonSubtract, buttonAdd, buttonEquals;
    MaterialButton button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        assignId(buttonAC, R.id.button_ac);
        assignId(buttonDel, R.id.button_del);
        assignId(buttonPercent, R.id.button_percent);
        assignId(buttonDot, R.id.button_period);
        assignId(buttonDivide, R.id.button_divide);
        assignId(buttonMultiply, R.id.button_multiply);
        assignId(buttonSubtract, R.id.button_subtract);
        assignId(buttonAdd, R.id.button_add);
        assignId(buttonEquals, R.id.button_equal);
        assignId(button0, R.id.button_0);
        assignId(button1, R.id.button_1);
        assignId(button2, R.id.button_2);
        assignId(button3, R.id.button_3);
        assignId(button4, R.id.button_4);
        assignId(button5, R.id.button_5);
        assignId(button6, R.id.button_6);
        assignId(button7, R.id.button_7);
        assignId(button8, R.id.button_8);
        assignId(button9, R.id.button_9);
    }

    void assignId(MaterialButton btn, int id){
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MaterialButton button = (MaterialButton) view;
        String buttonText = button.getText().toString();
        String dataToCalculate= solutionTv.getText().toString();

            if(buttonText.equals("AC")){
                solutionTv.setText("");
                resultTv.setText("0");
                return;
            }

        if (buttonText.equals("=")) {
            // Evaluate the expression when equals is pressed
            String expression = dataToCalculate.replace("x", "*"); // Replace 'x' with '*'
            String finalResult = getResult(expression); // Evaluate the modified expression
            solutionTv.setText(finalResult); // Display the result
            resultTv.setText(finalResult); // Show the final result in resultTv
            return;
        }

        if(buttonText.equals("Del")){
            if (dataToCalculate.length() > 1) {
                // Remove the last character
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
            } else {
                // If the string is empty or has one character, set it to "0"
                dataToCalculate = "0";
            }
        } else if (buttonText.equals("%")) {
            // Calculate percentage
            if (!dataToCalculate.isEmpty()) {
                double value = Double.parseDouble(dataToCalculate);
                double percentage = value / 100; // Compute the percentage
                dataToCalculate = String.valueOf(percentage);
            }
        } else {
            // Prevent adding to "0" when the input starts with "0"
            if (dataToCalculate.equals("0")) {
                dataToCalculate = buttonText;
            } else {
                dataToCalculate += buttonText;
            }
        }

        solutionTv.setText(dataToCalculate);

        //String expression = dataToCalculate.replace("x", "*");
        String finalResult = getResult(dataToCalculate);

        if(!finalResult.equals("error")){
            resultTv.setText(finalResult);
        }
    }

    String getResult(String data){
        try{
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable,data,"Javascript", 1,null).toString();
            if(finalResult.endsWith(".0")){
                finalResult = finalResult.replace(".0", "");
            }
            return finalResult;
        } catch (Exception e) {
            return "error";
        }

    }

}