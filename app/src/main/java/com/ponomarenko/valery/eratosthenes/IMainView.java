package com.ponomarenko.valery.eratosthenes;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Author: Valery Ponomarenko
 * Date: 5/4/2017
 * LinkedIn: https://www.linkedin.com/in/ponomarenkovalery
 */

@StateStrategyType(SkipStrategy.class)
public interface IMainView extends MvpView {
    void showLoading(boolean isLoading);
    void showError(String message);
    void showPrimes(String numbers);
    void showSum(String sum);
    void clear();
}
