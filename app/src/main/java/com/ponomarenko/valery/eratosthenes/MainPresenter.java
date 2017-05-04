package com.ponomarenko.valery.eratosthenes;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Valery Ponomarenko
 * Date: 5/4/2017
 * LinkedIn: https://www.linkedin.com/in/ponomarenkovalery
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<IMainView> {

    private static final int MAX_TEXT_LENGTH = 200000;

    private Disposable subscription;
    private StringBuilder sbPrimes;
    private Long sumPrimes;

    public void calculatePrimesUntil(long number) {
        getViewState().clear();
        getViewState().showLoading(true);

        unsubscribe();

        sbPrimes = new StringBuilder();
        sumPrimes = 0L;

        subscription = createObservable(number)
                .subscribeOn(Schedulers.computation())
                .doOnNext(integer -> {
                    //ограничение на вывод строки (если убрать, то приложение зависает на выводе списка цифр)
                    if(sbPrimes.length() < MAX_TEXT_LENGTH) {
                        sbPrimes.append(String.format(Locale.getDefault(), "%d ", integer));
                    }
                    sumPrimes += integer;
                })
                .sample(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(primeNumber -> {
                            updateUI(sumPrimes.toString(), sbPrimes.toString());
                        },
                        throwable -> {
                            getViewState().showLoading(false);
                            getViewState().showError(throwable.getMessage());
                        },
                        () -> {
                            getViewState().showLoading(false);
                            updateUI(sumPrimes.toString(), sbPrimes.toString());
                        });
    }

    public void unsubscribe() {
        if(subscription != null) {
            subscription.dispose();
        }
    }

    private void updateUI(String sum, String primes) {
        getViewState().showPrimes(primes);
        getViewState().showSum(sum);
    }

    private Observable<Long> createObservable(long maxNumber) {
        return Observable
                .create(emitter -> {
                    try {
                        segmented(maxNumber, emitter);
                    } catch (Throwable ex) {
                        emitter.onError(ex);
                    }
                    emitter.onComplete();
                });
    }

    private void segmented(long maxNumber, ObservableEmitter<Long> emitter){
        int limit = Math.round((float)Math.sqrt(maxNumber));
        Integer[] primes = simple(limit, emitter);

        long low = limit;
        long high = 2 * limit;

        while (low < maxNumber) {
            int[] primesInSegment = new int[limit];
            Arrays.fill(primesInSegment, 1);

            for (Integer prime : primes) {
                int lim = (int)Math.floor(low / prime) * prime;
                if (lim < low) {
                    lim += prime;
                }

                for (int j = lim; j < high; j += prime) {
                    primesInSegment[(int)(j - low)] = 0;
                }
            }

            for(long i = low; i < high; i++) {
                if(primesInSegment[(int)(i - low)] == 1) {
                    emitter.onNext(i);
                }
            }

            low += limit;
            high += limit;
            if(high > maxNumber) high = maxNumber;
        }
    }

    private Integer[] simple(int maxNumber, ObservableEmitter<Long> emitter) {
        int[] s = new int[maxNumber];
        List<Integer> primes = new ArrayList<>();
        Arrays.fill(s, 1);

        //2 - первое простое число
        int index = 2;

        while (index * index < maxNumber) {
            if (s[index] == 1) {
                emitter.onNext((long)index);
                primes.add(index);

                for (int j = index * index; j < maxNumber; j += index) {
                    s[j] = 0;
                }
            }
            index++;
        }

        while (index < s.length) {
            if (s[index] == 1) {
                emitter.onNext((long)index);
                primes.add(index);
            }
            index++;
        }

        return primes.toArray(new Integer[primes.size()]);
    }
}
