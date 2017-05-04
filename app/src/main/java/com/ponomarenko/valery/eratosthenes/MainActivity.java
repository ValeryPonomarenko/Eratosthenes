package com.ponomarenko.valery.eratosthenes;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MvpAppCompatActivity implements IMainView {

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.text_primes)
    TextView textPrimes;
    @BindView(R.id.text_sum)
    TextView textSum;
    @BindView(R.id.view_progress)
    View viewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void showLoading(boolean isLoading) {
        viewProgress.setVisibility(isLoading? View.VISIBLE: View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPrimes(String numbers) {
        if(numbers.length() != textPrimes.length()) {
            textPrimes.setText(numbers);
        }
    }

    @Override
    public void showSum(String sum) {
        textSum.setText(sum);
    }

    @Override
    public void clear() {
        textSum.setText("");
        textPrimes.setText("");
    }

    @OnClick(R.id.btn_search)
    void onSearchClick() {
        if(textNumber.getText().length() > 0) {
            presenter.calculatePrimesUntil(Long.valueOf(textNumber.getText().toString()));
        }
    }
}
