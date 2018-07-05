package com.adslinfotech.mobileaccounting.calculator;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import com.adslinfotech.mobileaccounting.R;
import com.adslinfotech.mobileaccounting.app.SimpleAccountingActivity;

public class Calculator extends SimpleAccountingActivity {
  private Button btnBS = null;
  private Button btnC = null;
  private Button btnDecimal = null;
  private Button btnDivide = null;
  private Button btnEight = null;
  private Button btnEquals = null;
  private Button btnFive = null;
  private Button btnFour = null;
  private Button btnMC = null;
  private Button btnMM = null;
  private Button btnMP = null;
  private Button btnMR = null;
  private Button btnMinus = null;
  private Button btnMultiply = null;
  private Button btnNine = null;
  private Button btnOne = null;
  private Button btnPM = null;
  private Button btnPerc = null;
  private Button btnPlus = null;
  private Button btnSeven = null;
  private Button btnSix = null;
  private Button btnSqrRoot = null;
  private Button btnThree = null;
  private Button btnTwo = null;
  private Button btnZero = null;
  private Button expEX = null;
  private Button expSQ = null;
  private boolean hasChanged = false;
  private double memNum = 0.0d;
  private Button nbtn1 = null;
  private Button nbtn2 = null;
  private Button nbtn3 = null;
  private Button nbtn4 = null;
  private Button nbtn5 = null;
  private Button nbtn6 = null;
  private Button nbtn7 = null;
  private double num = 0.0d;
  private int operator = 1;
  private boolean readyToClear = false;
  private EditText txtCalc = null;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView((int) R.layout.calculator);
    initControls();
    initScreenLayout();
    reset();
  }

  private void initScreenLayout() {
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);
    int height = dm.heightPixels;
    int width = dm.widthPixels;
    if (height < 400 || width < 300) {
      this.txtCalc.setTextSize(20.0f);
    }
    if (width < 300) {
      this.btnMC.setTextSize(18.0f);
      this.btnMR.setTextSize(18.0f);
      this.btnMP.setTextSize(18.0f);
      this.btnMM.setTextSize(18.0f);
      this.btnBS.setTextSize(18.0f);
      this.btnDivide.setTextSize(18.0f);
      this.btnPlus.setTextSize(18.0f);
      this.btnMinus.setTextSize(18.0f);
      this.btnMultiply.setTextSize(18.0f);
      this.btnEquals.setTextSize(18.0f);
      this.btnPM.setTextSize(18.0f);
      this.btnPerc.setTextSize(18.0f);
      this.btnC.setTextSize(18.0f);
      this.btnSqrRoot.setTextSize(18.0f);
      this.btnNine.setTextSize(18.0f);
      this.btnEight.setTextSize(18.0f);
      this.btnSeven.setTextSize(18.0f);
      this.btnSix.setTextSize(18.0f);
      this.btnFive.setTextSize(18.0f);
      this.btnFour.setTextSize(18.0f);
      this.btnThree.setTextSize(18.0f);
      this.btnTwo.setTextSize(18.0f);
      this.btnOne.setTextSize(18.0f);
      this.btnZero.setTextSize(18.0f);
      this.btnDecimal.setTextSize(18.0f);
      this.expSQ.setTextSize(18.0f);
      this.expEX.setTextSize(18.0f);
      this.nbtn1.setTextSize(18.0f);
      this.nbtn2.setTextSize(18.0f);
      this.nbtn3.setTextSize(18.0f);
      this.nbtn4.setTextSize(18.0f);
      this.nbtn5.setTextSize(18.0f);
      this.nbtn6.setTextSize(18.0f);
      this.nbtn7.setTextSize(18.0f);
    }
    this.txtCalc.setTextColor(-1);
    this.txtCalc.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    this.txtCalc.setKeyListener(null);
    this.btnZero.setTextColor(-1);
    this.btnOne.setTextColor(-1);
    this.btnTwo.setTextColor(-1);
    this.btnThree.setTextColor(-1);
    this.btnFour.setTextColor(-1);
    this.btnFive.setTextColor(-1);
    this.btnSix.setTextColor(-1);
    this.btnSeven.setTextColor(-1);
    this.btnEight.setTextColor(-1);
    this.btnNine.setTextColor(-1);
    this.btnPM.setTextColor(-1);
    this.btnDecimal.setTextColor(-1);
    this.btnMP.setTextColor(-1);
    this.btnMM.setTextColor(-1);
    this.btnMR.setTextColor(-1);
    this.btnMC.setTextColor(-1);
    this.btnBS.setTextColor(-1);
    this.btnC.setTextColor(-1);
    this.btnPerc.setTextColor(-1);
    this.btnSqrRoot.setTextColor(-1);
    this.btnDivide.setTextColor(-1);
    this.btnPlus.setTextColor(-1);
    this.btnMinus.setTextColor(-1);
    this.btnMultiply.setTextColor(-1);
    this.btnEquals.setTextColor(-1);
    this.expSQ.setTextColor(-1);
    this.expEX.setTextColor(-1);
    this.nbtn1.setTextColor(-1);
    this.nbtn2.setTextColor(-1);
    this.nbtn3.setTextColor(-1);
    this.nbtn4.setTextColor(-1);
    this.nbtn5.setTextColor(-1);
    this.nbtn6.setTextColor(-1);
    this.nbtn7.setTextColor(-1);
  }

  private void initControls() {
    this.txtCalc = (EditText) findViewById(R.id.txtCalc);
    this.btnZero = (Button) findViewById(R.id.btnZero);
    this.btnOne = (Button) findViewById(R.id.btnOne);
    this.btnTwo = (Button) findViewById(R.id.btnTwo);
    this.btnThree = (Button) findViewById(R.id.btnThree);
    this.btnFour = (Button) findViewById(R.id.btnFour);
    this.btnFive = (Button) findViewById(R.id.btnFive);
    this.btnSix = (Button) findViewById(R.id.btnSix);
    this.btnSeven = (Button) findViewById(R.id.btnSeven);
    this.btnEight = (Button) findViewById(R.id.btnEight);
    this.btnNine = (Button) findViewById(R.id.btnNine);
    this.btnPlus = (Button) findViewById(R.id.btnPlus);
    this.btnMinus = (Button) findViewById(R.id.btnMinus);
    this.btnMultiply = (Button) findViewById(R.id.btnMultiply);
    this.btnDivide = (Button) findViewById(R.id.btnDivide);
    this.btnEquals = (Button) findViewById(R.id.btnEquals);
    this.btnC = (Button) findViewById(R.id.btnC);
    this.btnDecimal = (Button) findViewById(R.id.btnDecimal);
    this.btnMC = (Button) findViewById(R.id.btnMC);
    this.btnMR = (Button) findViewById(R.id.btnMR);
    this.btnMM = (Button) findViewById(R.id.btnMM);
    this.btnMP = (Button) findViewById(R.id.btnMP);
    this.btnBS = (Button) findViewById(R.id.btnBS);
    this.btnPerc = (Button) findViewById(R.id.btnPerc);
    this.btnSqrRoot = (Button) findViewById(R.id.btnSqrRoot);
    this.btnPM = (Button) findViewById(R.id.btnPM);
    this.expSQ = (Button) findViewById(R.id.expSQ);
    this.expEX = (Button) findViewById(R.id.expEX);
    this.nbtn1 = (Button) findViewById(R.id.nbtn1);
    this.nbtn2 = (Button) findViewById(R.id.nbtn2);
    this.nbtn3 = (Button) findViewById(R.id.nbtn3);
    this.nbtn4 = (Button) findViewById(R.id.nbtn4);
    this.nbtn5 = (Button) findViewById(R.id.nbtn5);
    this.nbtn6 = (Button) findViewById(R.id.nbtn6);
    this.nbtn7 = (Button) findViewById(R.id.nbtn7);
    this.nbtn1.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(12);
      }
    });
    this.nbtn2.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(7);
      }
    });
    this.nbtn3.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(8);
      }
    });
    this.nbtn4.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(9);
      }
    });
    this.nbtn5.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(10);
      }
    });
    this.nbtn6.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(11);
      }
    });
    this.nbtn7.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(13);
      }
    });
    this.btnZero.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(0);
      }
    });
    this.expSQ.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.setValue(Double.toString(Math.cbrt(Double.parseDouble(Calculator.this.txtCalc.getText().toString()))));
      }
    });
    this.expEX.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(6);
      }
    });
    this.btnOne.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(1);
      }
    });
    this.btnTwo.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(2);
      }
    });
    this.btnThree.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(3);
      }
    });
    this.btnFour.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(4);
      }
    });
    this.btnFive.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(5);
      }
    });
    this.btnSix.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(6);
      }
    });
    this.btnSeven.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(7);
      }
    });
    this.btnEight.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(8);
      }
    });
    this.btnNine.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleNumber(9);
      }
    });
    this.btnPlus.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(1);
      }
    });
    this.btnMinus.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(2);
      }
    });
    this.btnMultiply.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(3);
      }
    });
    this.btnDivide.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(4);
      }
    });
    this.btnEquals.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleEquals(0);
      }
    });
    this.btnC.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.reset();
      }
    });
    this.btnDecimal.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleDecimal();
      }
    });
    this.btnPM.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handlePlusMinus();
      }
    });
    this.btnMC.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.memNum = 0.0d;
      }
    });
    this.btnMR.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.setValue(Double.toString(Calculator.this.memNum));
      }
    });
    this.btnMM.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.memNum = Calculator.this.memNum - Double.parseDouble(Calculator.this.txtCalc.getText().toString());
        Calculator.this.operator = 0;
      }
    });
    this.btnMP.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.memNum = Calculator.this.memNum + Double.parseDouble(Calculator.this.txtCalc.getText().toString());
        Calculator.this.operator = 0;
      }
    });
    this.btnBS.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.handleBackspace();
      }
    });
    this.btnSqrRoot.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.setValue(Double.toString(Math.sqrt(Double.parseDouble(Calculator.this.txtCalc.getText().toString()))));
      }
    });
    this.btnPerc.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Calculator.this.setValue(Double.toString(Calculator.this.num * (0.01d * Double.parseDouble(Calculator.this.txtCalc.getText().toString()))));
      }
    });
    this.txtCalc.setOnKeyListener(new OnKeyListener() {
      public boolean onKey(View v, int i, KeyEvent e) {
        if (e.getAction() == 0) {
          int keyCode = e.getKeyCode();
          Log.d("Onback", "" + keyCode);
          switch (keyCode) {
            case 4:
              Calculator.this.finish();
              break;
            case 7:
              Calculator.this.handleNumber(0);
              break;
            case 8:
              Calculator.this.handleNumber(1);
              break;
            case 9:
              Calculator.this.handleNumber(2);
              break;
            case 10:
              Calculator.this.handleNumber(3);
              break;
            case 11:
              Calculator.this.handleNumber(4);
              break;
            case 12:
              Calculator.this.handleNumber(5);
              break;
            case 13:
              Calculator.this.handleNumber(6);
              break;
            case 14:
              Calculator.this.handleNumber(7);
              break;
            case 15:
              Calculator.this.handleNumber(8);
              break;
            case 16:
              Calculator.this.handleNumber(9);
              break;
            case 20:
              return false;
            case 31:
              Calculator.this.reset();
              break;
            case 43:
              Calculator.this.handleEquals(1);
              break;
            case 56:
              Calculator.this.handleDecimal();
              break;
            case 69:
              Calculator.this.handleEquals(2);
              break;
            case 70:
              Calculator.this.handleEquals(0);
              break;
            case 76:
              Calculator.this.handleEquals(4);
              break;
          }
        }
        return true;
      }
    });
  }

  private void handleEquals(int newOperator) {
    if (this.hasChanged) {
      switch (this.operator) {
        case 1:
          this.num += Double.parseDouble(this.txtCalc.getText().toString());
          break;
        case 2:
          this.num -= Double.parseDouble(this.txtCalc.getText().toString());
          break;
        case 3:
          this.num *= Double.parseDouble(this.txtCalc.getText().toString());
          break;
        case 4:
          this.num /= Double.parseDouble(this.txtCalc.getText().toString());
          break;
        case 5:
          this.num = Math.pow(this.num, 2.0d);
          break;
        case 6:
          this.num = Math.pow(this.num, Double.parseDouble(this.txtCalc.getText().toString()));
          break;
        case 7:
          this.num += Math.sin(Double.parseDouble(this.txtCalc.getText().toString()));
          break;
        case 8:
          this.num += Math.cos(Double.parseDouble(this.txtCalc.getText().toString()));
          break;
        case 9:
          this.num += Math.tan(Double.parseDouble(this.txtCalc.getText().toString()));
          break;
        case 10:
          this.num = Math.log(Double.parseDouble(this.txtCalc.getText().toString()));
          break;
        case 11:
          this.num = Math.exp(Math.log(Double.parseDouble(this.txtCalc.getText().toString())));
          break;
        case 12:
          this.num = 3.141592653589793d;
          break;
        case 13:
          this.num = 2.718281828459045d;
          break;
      }
      String txt = Double.toString(this.num);
      this.txtCalc.setText(txt);
      this.txtCalc.setSelection(txt.length());
      this.readyToClear = true;
      this.hasChanged = false;
    }
    this.operator = newOperator;
  }

  private void handleNumber(int num) {
    if (this.operator == 0) {
      reset();
    }
    String txt = this.txtCalc.getText().toString();
    if (this.readyToClear) {
      txt = "";
      this.readyToClear = false;
    } else if (txt.equals("0")) {
      txt = "";
    }
    txt = txt + Integer.toString(num);
    this.txtCalc.setText(txt);
    this.txtCalc.setKeyListener(null);
    this.txtCalc.setSelection(txt.length());
    this.hasChanged = true;
  }

  private void setValue(String value) {
    if (this.operator == 0) {
      reset();
    }
    if (this.readyToClear) {
      this.readyToClear = false;
    }
    this.txtCalc.setText(value);
    this.txtCalc.setSelection(value.length());
    this.hasChanged = true;
  }

  private void handleDecimal() {
    if (this.operator == 0) {
      reset();
    }
    if (this.readyToClear) {
      this.txtCalc.setText("0.");
      this.txtCalc.setSelection(2);
      this.readyToClear = false;
      this.hasChanged = true;
    } else if (!this.txtCalc.getText().toString().contains(".")) {
      this.txtCalc.append(".");
      this.hasChanged = true;
    }
  }

  private void handleBackspace() {
    if (!this.readyToClear) {
      String txt = this.txtCalc.getText().toString();
      if (txt.length() > 0) {
        txt = txt.substring(0, txt.length() - 1);
        if (txt.equals("")) {
          txt = "0";
        }
        this.txtCalc.setText(txt);
        this.txtCalc.setSelection(txt.length());
      }
    }
  }

  private void handlePlusMinus() {
    if (!this.readyToClear) {
      String txt = this.txtCalc.getText().toString();
      if (!txt.equals("0")) {
        if (txt.charAt(0) == '-') {
          txt = txt.substring(1, txt.length());
        } else {
          txt = "-" + txt;
        }
        this.txtCalc.setText(txt);
        this.txtCalc.setSelection(txt.length());
      }
    }
  }

  private void reset() {
    this.num = 0.0d;
    this.txtCalc.setText("0");
    this.txtCalc.setSelection(1);
    this.operator = 1;
  }

  public void onBackPressed() {
    super.onBackPressed();
    finish();
  }
}
