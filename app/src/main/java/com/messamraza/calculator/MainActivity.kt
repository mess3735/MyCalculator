package com.messamraza.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var workingsTextView: TextView

    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result_tv)
        workingsTextView = findViewById(R.id.workings_tv)
    }

    fun onDigitClick(view: View) {
        if (stateError) {
            workingsTextView.text = (view as Button).text
            stateError = false
        } else {
            workingsTextView.append((view as Button).text)
        }
        lastNumeric = true
        onEqual()
    }

    fun onOperatorClick(view: View) {
        val button = view as Button
        val symbol = when (button.text) {
            "×" -> "*"
            "÷" -> "/"
            else -> button.text.toString()
        }

        val currentText = workingsTextView.text.toString()

        if (symbol == "-" && (currentText.isEmpty() || currentText.last() in "+*/")) {
            //Negative number at start or after an operator
            workingsTextView.append(symbol)
            lastNumeric = false
            lastDot = false
        } else if (lastNumeric && !stateError) {
            //Normal operator usage after a number
            workingsTextView.append(symbol)
            lastNumeric = false
            lastDot = false
        }
    }

    fun onClearClick(view: View) {
        workingsTextView.text = ""
        resultTextView.text = "0"
        stateError = false
        lastNumeric = false
        lastDot = false
    }

    fun onBackClick(view: View) {
        val text = workingsTextView.text.toString()
        if (text.isNotEmpty()) {
            workingsTextView.text = text.dropLast(1)
        }

        try {
            val lastChar = text.last()
            if (lastChar.isDigit()) {
                onEqual()
            }
        } catch (e: Exception) {
            resultTextView.text = "0"
            stateError = false
            lastNumeric = false
            lastDot = false
        }
    }

    fun onEqualClick(view: View) {
        onEqual()
        workingsTextView.text = resultTextView.text.toString()
    }

    fun onDecimalClick(view: View) {
        if (lastNumeric && !stateError && !lastDot) {
            workingsTextView.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    private fun onEqual() {
        if (lastNumeric && !stateError) {
            val expressionText = workingsTextView.text.toString()
                .replace("×", "*")
                .replace("÷", "/")
            try {
                val expression = ExpressionBuilder(expressionText).build()
                val result = expression.evaluate()
                resultTextView.text = result.toString()
            } catch (ex: Exception) {
                resultTextView.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }
}
