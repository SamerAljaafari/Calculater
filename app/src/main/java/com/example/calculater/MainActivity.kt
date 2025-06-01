package com.example.calculater

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.math.*

/**
 * MainActivity class for a calculator application.
 * Supports basic arithmetic operations and scientific functions.
 * @author Samer Aljaafari
 */

class MainActivity : AppCompatActivity() {

    // UI components for input and result display
    private lateinit var editText: EditText
    private lateinit var resultText: TextView

    // Stack to hold numbers for operations
    private val stack = Stack<Float>()
    // Flag to determine if the device is a tablet
    private var isTablet = false

    /**
     * Called when the activity is starting.
     * Initializes the activity, sets up the UI, and configures button listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable the action bar for the options menu
        setSupportActionBar(findViewById(R.id.toolbar))

        editText = findViewById(R.id.main)
        resultText = findViewById(R.id.resultText)

        // Check if we're on a tablet by looking for scientific function buttons
        isTablet = findViewById<Button?>(R.id.sin) != null

        val clearButton: Button = findViewById(R.id.clear_text)
        val clearAllButton: Button = findViewById(R.id.clear_all)
        val addButton: Button = findViewById(R.id.add)
        val subtractButton: Button = findViewById(R.id.sub)
        val multiplyButton: Button = findViewById(R.id.mul)
        val divideButton: Button = findViewById(R.id.div)
        val equalButton: Button = findViewById(R.id.submit)

        val numberButtons = listOf(
            R.id.num1 to "1", R.id.num2 to "2", R.id.num3 to "3",
            R.id.num4 to "4", R.id.num5 to "5", R.id.num6 to "6",
            R.id.num7 to "7", R.id.num8 to "8", R.id.num9 to "9",
            R.id.zero to "0", R.id.dot to "."
        )

        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                editText.append(value)
            }
        }

        // Set click listener for the equal button to push numbers onto the stack
        equalButton.setOnClickListener {
            val input = editText.text.toString().trim()
            if (input.isNotEmpty()) {
                try {
                    val number = input.toFloat()
                    stack.push(number)
                    editText.text.clear()
                    updateResultText()
                } catch (e: NumberFormatException) {
                    resultText.text = "Invalid number"
                }
            } else {
                resultText.text = "Enter a number first"
            }
        }

        addButton.setOnClickListener { performOperation("+") }
        subtractButton.setOnClickListener { performOperation("-") }
        multiplyButton.setOnClickListener { performOperation("*") }
        divideButton.setOnClickListener { performOperation("/") }

        clearButton.setOnClickListener {
            editText.text.clear()
        }

        clearAllButton.setOnClickListener {
            stack.clear()
            editText.text.clear()
            resultText.text = "0"
        }

        // Set up scientific function buttons for tablet layout
        if (isTablet) {
            findViewById<Button>(R.id.sin).setOnClickListener { performScientificOperation("sin") }
            findViewById<Button>(R.id.cos).setOnClickListener { performScientificOperation("cos") }
            findViewById<Button>(R.id.tan).setOnClickListener { performScientificOperation("tan") }
            findViewById<Button>(R.id.sqrt).setOnClickListener { performScientificOperation("sqrt") }
        }
    }

    /**
     * Initialize the contents of the Activity's options menu.
     * Inflates the scientific menu for non-tablet devices.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Only inflate the menu for phones (non-tablet devices)
        if (!isTablet) {
            menuInflater.inflate(R.menu.scientific_menu, menu)
        }
        return true
    }

    /**
     * Handles item selection from the options menu.
     * Performs scientific operations based on the selected menu item.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sin -> performScientificOperation("sin")
            R.id.menu_cos -> performScientificOperation("cos")
            R.id.menu_tan -> performScientificOperation("tan")
            R.id.menu_sqrt -> performScientificOperation("sqrt")
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    /**
     * Performs arithmetic operations on the top two numbers in the stack.
     * @param operator The arithmetic operator (+, -, *, /)
     */
    private fun performOperation(operator: String) {
        if (stack.size < 2) {
            resultText.text = "Need 2 numbers"
            return
        }

        val b = stack.pop()
        val a = stack.pop()

        val result = when (operator) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0f) a / b else {
                resultText.text = "Divide by zero"
                stack.push(a)
                stack.push(b)
                return
            }
            else -> {
                resultText.text = "Unknown op"
                stack.push(a)
                stack.push(b)
                return
            }
        }

        stack.push(result)
        updateResultText()
    }

    /**
     * Performs scientific operations on the top number in the stack.
     * @param operation The scientific operation (sin, cos, tan, sqrt)
     */
    private fun performScientificOperation(operation: String) {
        if (stack.isEmpty()) {
            resultText.text = "Need a number"
            return
        }

        val a = stack.pop()
        val result = when (operation) {
            "sin" -> sin(Math.toRadians(a.toDouble())).toFloat()
            "cos" -> cos(Math.toRadians(a.toDouble())).toFloat()
            "tan" -> tan(Math.toRadians(a.toDouble())).toFloat()
            "sqrt" -> if (a >= 0) sqrt(a.toDouble()).toFloat() else {
                resultText.text = "Invalid input for sqrt"
                stack.push(a)
                return
            }
            else -> {
                resultText.text = "Unknown operation"
                stack.push(a)
                return
            }
        }

        stack.push(result)
        updateResultText()
    }

    /**
     * Updates the result text view with the current stack contents.
     */
    private fun updateResultText() {
        resultText.text = "Stack: ${stack.joinToString(", ")}"
    }
}
