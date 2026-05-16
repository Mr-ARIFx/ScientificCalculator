package com.example.scientificcalculator;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView tvResult;
    private TextView tvExpression;
    private TextView tvDegRadMode;

    private final StringBuilder currentExpression = new StringBuilder();
    private boolean isResultDisplayed = false;
    private boolean isDegreeMode      = true;

    private final DecimalFormat df = new DecimalFormat("#.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        attachListeners();
        updateDisplay("0", "");
    }

    private void bindViews() {
        tvResult     = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);
        tvDegRadMode = findViewById(R.id.tvDegRadMode);
    }

    private void attachListeners() {
        int[] ids = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDecimal,
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide,
                R.id.btnEquals, R.id.btnDelete, R.id.btnPercent,
                R.id.btnOpenParen, R.id.btnCloseParen,
                R.id.btnSin,  R.id.btnCos,  R.id.btnTan,
                R.id.btnAsin, R.id.btnAcos, R.id.btnAtan,
                R.id.btnLog,  R.id.btnLn,   R.id.btnSqrt,
                R.id.btnSquare, R.id.btnPow, R.id.btnReciprocal,
                R.id.btnFactorial, R.id.btnPi, R.id.btnE,
                R.id.btnDegRad
        };
        for (int id : ids) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        animatePress(v);

        int id = v.getId();

        if      (id == R.id.btn0)      appendDigit("0");
        else if (id == R.id.btn1)      appendDigit("1");
        else if (id == R.id.btn2)      appendDigit("2");
        else if (id == R.id.btn3)      appendDigit("3");
        else if (id == R.id.btn4)      appendDigit("4");
        else if (id == R.id.btn5)      appendDigit("5");
        else if (id == R.id.btn6)      appendDigit("6");
        else if (id == R.id.btn7)      appendDigit("7");
        else if (id == R.id.btn8)      appendDigit("8");
        else if (id == R.id.btn9)      appendDigit("9");
        else if (id == R.id.btnDecimal)    appendDecimal();
        else if (id == R.id.btnAdd)        appendOperator("+");
        else if (id == R.id.btnSubtract)   appendOperator("−");
        else if (id == R.id.btnMultiply)   appendOperator("×");
        else if (id == R.id.btnDivide)     appendOperator("÷");
        else if (id == R.id.btnEquals)     evaluateExpression();
        else if (id == R.id.btnDelete)     deleteLastChar();
        else if (id == R.id.btnPercent)    applyPercent();
        else if (id == R.id.btnOpenParen)  appendRaw("(");
        else if (id == R.id.btnCloseParen) appendRaw(")");
        else if (id == R.id.btnSin)        applyTrig("sin");
        else if (id == R.id.btnCos)        applyTrig("cos");
        else if (id == R.id.btnTan)        applyTrig("tan");
        else if (id == R.id.btnAsin)       applyTrig("asin");
        else if (id == R.id.btnAcos)       applyTrig("acos");
        else if (id == R.id.btnAtan)       applyTrig("atan");
        else if (id == R.id.btnLog)        applyFunction("log");
        else if (id == R.id.btnLn)         applyFunction("ln");
        else if (id == R.id.btnSqrt)       applyFunction("sqrt");
        else if (id == R.id.btnSquare)     applySquare();
        else if (id == R.id.btnPow)        startPower();
        else if (id == R.id.btnReciprocal) applyReciprocal();
        else if (id == R.id.btnFactorial)  applyFactorial();
        else if (id == R.id.btnPi)         appendConstant(Math.PI, "π");
        else if (id == R.id.btnE)          appendConstant(Math.E,  "e");
        else if (id == R.id.btnDegRad)     toggleDegRad();
    }

    private void appendDigit(String d) {
        if (isResultDisplayed) { currentExpression.setLength(0); isResultDisplayed = false; }
        currentExpression.append(d);
        refreshDisplay();
    }

    private void appendDecimal() {
        if (isResultDisplayed) { currentExpression.setLength(0); currentExpression.append("0"); isResultDisplayed = false; }
        String e = currentExpression.toString();
        int last = Math.max(Math.max(e.lastIndexOf('+'), e.lastIndexOf('−')),
                Math.max(e.lastIndexOf('×'), e.lastIndexOf('÷')));
        String seg = e.substring(last + 1);
        if (!seg.contains(".")) { if (seg.isEmpty()) currentExpression.append("0"); currentExpression.append("."); }
        refreshDisplay();
    }

    private void appendOperator(String op) {
        if (isResultDisplayed) isResultDisplayed = false;
        String e = currentExpression.toString();
        if (e.isEmpty()) { if (op.equals("−")) currentExpression.append("−"); return; }
        char last = e.charAt(e.length() - 1);
        if (last == '+' || last == '−' || last == '×' || last == '÷')
            currentExpression.setCharAt(e.length() - 1, op.charAt(0));
        else
            currentExpression.append(op);
        refreshDisplay();
    }

    private void appendRaw(String token) {
        if (isResultDisplayed) isResultDisplayed = false;
        currentExpression.append(token);
        refreshDisplay();
    }

    private void appendConstant(double val, String sym) {
        if (isResultDisplayed) { currentExpression.setLength(0); isResultDisplayed = false; }
        currentExpression.append(sym);
        tvExpression.setText(currentExpression.toString());
        tvResult.setText(formatNum(val));
        adjustSize(formatNum(val));
    }

    private void deleteLastChar() {
        if (isResultDisplayed) { clearAll(); return; }
        if (currentExpression.length() == 0) return;
        String e = currentExpression.toString();
        if      (e.endsWith("asin") || e.endsWith("acos") || e.endsWith("atan"))
            currentExpression.delete(currentExpression.length() - 4, currentExpression.length());
        else if (e.endsWith("sin") || e.endsWith("cos") || e.endsWith("tan")
                || e.endsWith("log") || e.endsWith("sqrt"))
            currentExpression.delete(currentExpression.length() - 3, currentExpression.length());
        else if (e.endsWith("ln"))
            currentExpression.delete(currentExpression.length() - 2, currentExpression.length());
        else
            currentExpression.deleteCharAt(currentExpression.length() - 1);

        if (currentExpression.length() == 0) updateDisplay("0", "");
        else refreshDisplay();
    }

    private void clearAll() {
        currentExpression.setLength(0);
        isResultDisplayed = false;
        updateDisplay("0", "");
    }

    private void applyTrig(String fn) {
        double v = getCurrentValue(); if (Double.isNaN(v)) { showError(); return; }
        double rad = isDegreeMode ? Math.toRadians(v) : v;
        double res; String label;
        switch (fn) {
            case "sin":  res = cleanTrig(Math.sin(rad)); label = "sin(" + formatNum(v) + (isDegreeMode?"°":"") + ")"; break;
            case "cos":  res = cleanTrig(Math.cos(rad)); label = "cos(" + formatNum(v) + (isDegreeMode?"°":"") + ")"; break;
            case "tan":
                if (isDegreeMode && Math.abs(v % 180) == 90) { updateDisplay("Undefined", "tan(" + formatNum(v) + "°)"); return; }
                res = cleanTrig(Math.tan(rad)); label = "tan(" + formatNum(v) + (isDegreeMode?"°":"") + ")"; break;
            case "asin": if (v<-1||v>1){updateDisplay("Domain Error","asin("+formatNum(v)+")");return;} res=Math.asin(v); if(isDegreeMode)res=Math.toDegrees(res); label="asin("+formatNum(v)+")"; break;
            case "acos": if (v<-1||v>1){updateDisplay("Domain Error","acos("+formatNum(v)+")");return;} res=Math.acos(v); if(isDegreeMode)res=Math.toDegrees(res); label="acos("+formatNum(v)+")"; break;
            case "atan": res=Math.atan(v); if(isDegreeMode)res=Math.toDegrees(res); label="atan("+formatNum(v)+")"; break;
            default: return;
        }
        displayResult(res, label);
    }

    private double cleanTrig(double v) {
        if (Math.abs(v) < 1e-10) return 0;
        if (Math.abs(v - 1) < 1e-10) return 1;
        if (Math.abs(v + 1) < 1e-10) return -1;
        return v;
    }

    private void applyFunction(String fn) {
        double v = getCurrentValue(); if (Double.isNaN(v)) { showError(); return; }
        double res; String label;
        switch (fn) {
            case "log":  if(v<=0){updateDisplay("Domain Error","log("+formatNum(v)+")");return;} res=Math.log10(v); label="log("+formatNum(v)+")"; break;
            case "ln":   if(v<=0){updateDisplay("Domain Error","ln("+formatNum(v)+")");return;}  res=Math.log(v);   label="ln("+formatNum(v)+")";  break;
            case "sqrt": if(v<0) {updateDisplay("Domain Error","√("+formatNum(v)+")");return;}   res=Math.sqrt(v);  label="√("+formatNum(v)+")";   break;
            default: return;
        }
        displayResult(res, label);
    }

    private void applySquare()     { double v=getCurrentValue(); if(!Double.isNaN(v)) displayResult(v*v,"("+formatNum(v)+")²"); else showError(); }
    private void applyReciprocal() { double v=getCurrentValue(); if(Double.isNaN(v)){showError();return;} if(v==0){updateDisplay("Undefined","1/0");return;} displayResult(1.0/v,"1/("+formatNum(v)+")"); }
    private void applyFactorial()  {
        double v=getCurrentValue(); if(Double.isNaN(v)){showError();return;}
        if(v<0||v!=Math.floor(v)||v>20){updateDisplay("Domain Error",formatNum(v)+"!");return;}
        displayResult(factorial((int)v), formatNum(v)+"!");
    }
    private double factorial(int n) { double r=1; for(int i=2;i<=n;i++) r*=i; return r; }

    private void startPower() {
        double base=getCurrentValue(); if(Double.isNaN(base)){showError();return;}
        currentExpression.setLength(0);
        currentExpression.append(formatNum(base)).append("^");
        tvExpression.setText(formatNum(base)+" ^"); tvResult.setText("?");
        isResultDisplayed=false;
    }

    private void applyPercent() { double v=getCurrentValue(); if(!Double.isNaN(v)) displayResult(v/100.0, formatNum(v)+"%"); }

    private void evaluateExpression() {
        String raw = currentExpression.toString().trim();
        if (raw.isEmpty()) return;
        raw = raw.replace("π", String.valueOf(Math.PI)).replace("e", String.valueOf(Math.E));
        try {
            double result = eval(raw);
            if (Double.isNaN(result))          updateDisplay("Error",     currentExpression.toString());
            else if (Double.isInfinite(result)) updateDisplay("Undefined", currentExpression.toString());
            else displayResult(result, currentExpression.toString() + " =");
        } catch (ArithmeticException ae) { updateDisplay("Undefined", currentExpression.toString());
        } catch (Exception ex)           { updateDisplay("Error",     currentExpression.toString()); }
    }

    private double eval(String expr) throws Exception {
        expr = expr.replace("−","-").replace("×","*").replace("÷","/");
        return evalRPN(shunt(tokenize(expr)));
    }

    private List<String> tokenize(String expr) {
        List<String> t = new ArrayList<>(); int i=0;
        while (i<expr.length()) {
            char c=expr.charAt(i);
            if (Character.isWhitespace(c)){i++;continue;}
            if (c=='-' && (i==0||isOC(expr.charAt(i-1))||expr.charAt(i-1)=='(')) {
                StringBuilder n=new StringBuilder("-"); i++;
                while(i<expr.length()&&(Character.isDigit(expr.charAt(i))||expr.charAt(i)=='.')) n.append(expr.charAt(i++));
                t.add(n.toString());
            } else if (Character.isDigit(c)||c=='.') {
                StringBuilder n=new StringBuilder();
                while(i<expr.length()&&(Character.isDigit(expr.charAt(i))||expr.charAt(i)=='.')) n.append(expr.charAt(i++));
                t.add(n.toString());
            } else { t.add(String.valueOf(c)); i++; }
        }
        return t;
    }
    private boolean isOC(char c){return c=='+'||c=='-'||c=='*'||c=='/'||c=='^';}

    private List<String> shunt(List<String> tokens) throws Exception {
        List<String> out=new ArrayList<>(); Stack<String> ops=new Stack<>();
        for (String tk:tokens) {
            if (isN(tk)) out.add(tk);
            else if (tk.equals("(")) ops.push(tk);
            else if (tk.equals(")")) {
                while(!ops.isEmpty()&&!ops.peek().equals("(")) out.add(ops.pop());
                if(ops.isEmpty()) throw new Exception("Paren"); ops.pop();
            } else if (isOP(tk)) {
                while(!ops.isEmpty()&&isOP(ops.peek())&&((isL(tk)&&pr(tk)<=pr(ops.peek()))||(!isL(tk)&&pr(tk)<pr(ops.peek())))) out.add(ops.pop());
                ops.push(tk);
            }
        }
        while(!ops.isEmpty()){String op=ops.pop(); if(op.equals("("))throw new Exception("Paren"); out.add(op);}
        return out;
    }

    private double evalRPN(List<String> rpn) throws Exception {
        Stack<Double> s=new Stack<>();
        for (String tk:rpn) {
            if (isN(tk)) s.push(Double.parseDouble(tk));
            else {
                if(s.size()<2) throw new Exception("Invalid");
                double b=s.pop(), a=s.pop();
                switch(tk){
                    case "+": s.push(a+b); break; case "-": s.push(a-b); break;
                    case "*": s.push(a*b); break;
                    case "/": if(b==0) throw new ArithmeticException("Div"); s.push(a/b); break;
                    case "^": s.push(Math.pow(a,b)); break;
                }
            }
        }
        if(s.size()!=1) throw new Exception("Invalid"); return s.pop();
    }

    private boolean isN(String t){try{Double.parseDouble(t);return true;}catch(Exception e){return false;}}
    private boolean isOP(String t){return t.equals("+")||t.equals("-")||t.equals("*")||t.equals("/")||t.equals("^");}
    private int pr(String o){switch(o){case"+":case"-":return 1;case"*":case"/":return 2;case"^":return 3;default:return 0;}}
    private boolean isL(String o){return !o.equals("^");}


    private void toggleDegRad() {
        isDegreeMode = !isDegreeMode;
        String mode = isDegreeMode ? "DEG" : "RAD";
        tvDegRadMode.setText(mode);
        Button b = findViewById(R.id.btnDegRad);
        if (b != null) b.setText(mode);
    }


    private void updateDisplay(String result, String expression) {
        tvResult.setText(result);
        tvExpression.setText(expression);
        adjustSize(result);
    }

    private void displayResult(double val, String label) {
        String f = formatNum(val);
        currentExpression.setLength(0);
        currentExpression.append(f);
        isResultDisplayed = true;
        updateDisplay(f, label);
    }

    private void refreshDisplay() {
        String e = currentExpression.toString();
        tvExpression.setText("");
        tvResult.setText(e);
        adjustSize(e);
    }

    private void adjustSize(String text) {
        int n = text.length();
        if      (n > 14) tvResult.setTextSize(24);
        else if (n > 10) tvResult.setTextSize(32);
        else if (n > 7)  tvResult.setTextSize(40);
        else             tvResult.setTextSize(48);
    }

    private void showError() { updateDisplay("Error",""); currentExpression.setLength(0); isResultDisplayed=true; }

    private String formatNum(double v) {
        if (Double.isNaN(v))       return "Error";
        if (Double.isInfinite(v))  return v>0 ? "∞" : "-∞";
        if (v==Math.floor(v)&&Math.abs(v)<1e15) return String.valueOf((long)v);
        return df.format(v);
    }

    private double getCurrentValue() {
        try { return Double.parseDouble(tvResult.getText().toString()); }
        catch (NumberFormatException e) {
            try { return eval(currentExpression.toString().replace("π",String.valueOf(Math.PI)).replace("e",String.valueOf(Math.E))); }
            catch (Exception ex) { return Double.NaN; }
        }
    }

    private void animatePress(View v) {
        ObjectAnimator sx=ObjectAnimator.ofFloat(v,"scaleX",1f,0.93f,1f);
        ObjectAnimator sy=ObjectAnimator.ofFloat(v,"scaleY",1f,0.93f,1f);
        sx.setDuration(100); sy.setDuration(100); sx.start(); sy.start();
    }
}