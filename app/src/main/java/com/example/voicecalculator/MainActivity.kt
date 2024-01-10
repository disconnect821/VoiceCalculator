package com.example.voicecalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.voicecalculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SPEECH_REQUEST_CODE = 50
    }
    private var state : Boolean = true
    private var lastNumeric : Boolean = false
    private lateinit var binding : ActivityMainBinding
    private lateinit var speechRecognizer : SpeechRecognizer




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVoice.setOnClickListener {
            startRecording()
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        setNumericOnClickListener()
        setOperatorOnClickListener()
        binding.btnCancel.setOnClickListener {
            binding.resultTextView.text = "0"
        }
        binding.btnEqual.setOnClickListener {
            calculateVoiceInput(binding.resultTextView.text.toString())
        }
    }

    private fun setNumericOnClickListener() {
        val numButtons = listOf(binding.btnOne , binding.btnTwo, binding.btnThree, binding.btnFour, binding.btnFive, binding.btnSix,
            binding.btnSeven , binding.btnEight, binding.btnNine, binding.btnZero)

        for (button in numButtons) {
            button.setOnClickListener {
                appendTextToResult(button.text.toString())
            }
        }
    }

    private fun setOperatorOnClickListener() {
        val opButtons = listOf(binding.btnAdd , binding.btnSub, binding.btnMultiply, binding.btnDivide, binding.btnDecimal)
        for (button in opButtons) {
            button.setOnClickListener {
                appendTextToResult(button.text.toString())
            }
        }
    }

    private fun appendTextToResult(button: String) {
        val currentText = binding.resultTextView.text.toString()
        if(currentText == "0"){
            binding.resultTextView.text = button
        }
        else{
            if(button =="x"){
                binding.resultTextView.text = "$currentText*"
            }
            else{
                binding.resultTextView.text = "$currentText$button"
            }
        }
    }


    private fun startRecording() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT , "Speak to Calculate")
        Log.d("Kahan hai recording" , "idhr bhi nhi hai")
        try{
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }catch (e: Exception){
            Toast.makeText(this, "Speech Recognizer not available " , Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("Kahan hai recording" , "Activity resulti")
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK){
            var spokenText : String = ""
            val results =data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                spokenText = results[0]
            } else {
                Toast.makeText(this, "No speech results", Toast.LENGTH_SHORT).show()
            }
            spokenText = spokenText.replace("divide by" , "/")
            spokenText = spokenText.replace("divided by" , "/")

            spokenText = spokenText.replace("into" , "*")
            spokenText = spokenText.replace("multiply" , "*")
            spokenText = spokenText.replace("multiply by" , "*")

            spokenText = spokenText.replace("x" , "*")


            spokenText = spokenText.replace("add" , "+")
            spokenText = spokenText.replace("plus" , "+")

            spokenText = spokenText.replace("sub" , "-")
            spokenText = spokenText.replace("subtract" , "-")
            spokenText = spokenText.replace("minus" , "-")


            spokenText = spokenText.replace("equals" , "=")
            spokenText = spokenText.replace("equals to " , "=")
            spokenText = spokenText.replace("equal to " , "=")

//            if(spokenText.contains("=")){
//                calculateVoiceInput(spokenText)
//            }
            calculateVoiceInput(spokenText)
        }
    }

    private fun calculateVoiceInput(spokenText: String) {
        val sanitizedInput = spokenText.replace("[^0-9+\\-*/.]".toRegex(), "") // Remove unwanted characters
        try {
            Log.d("Kahan hai recording" , sanitizedInput)
            val expression = ExpressionBuilder(sanitizedInput).build()
            val result  = expression.evaluate()
            updateResultTextView(result.toString())
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing voice input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateResultTextView(result: String) {
        binding.resultTextView.text = result
    }

}