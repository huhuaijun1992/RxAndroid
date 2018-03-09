package com.example.rxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRxAndroid();
    }

    public void  init(){
        button1=(Button)findViewById(R.id.first_button);
        button2=(Button)findViewById(R.id.two_button);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

    }
    public void showRxAndroid(){
        //创建一个被观察者（发布者）
        Observable observable=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1001);
                subscriber.onNext(1002);
                subscriber.onNext(1003);
                subscriber.onCompleted();
            }

        });
        //创建一个观察者
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.i("hhj", "onCompleted.. ");
            }

            @Override
            public void onError(Throwable e) {
                Log.i("hhj", "subscriber onError.. "+e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("hhj", "onNext.. integer:" + integer);
            }
        };
        observable.subscribe(subscriber);
    }
     public void creatObservable(){
     Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if(!subscriber.isUnsubscribed()){
                    for(int i= 1;i<=10;i++){
                        subscriber.onNext(i);
                    }
                    subscriber.onCompleted();
                }

            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.i("hhj", "onNext:=================="+integer);
            }
        });
     }
    /**
     * 过滤某些条件
     */
    public static void filter(){
        Observable observable = Observable.just(1,2,3,4,5,6,7,8);
        observable.filter(new Func1<Integer,Boolean>() {
            @Override public Boolean call(Integer integer) {
                return integer<5;
            }
        }).observeOn(Schedulers.io()).subscribe(new Subscriber<Integer>() {
            @Override public void onCompleted() {
                Log.i("adu","onCompleted");
            }
            @Override public void onError(Throwable e) {
                Log.i("adu","onError");
            }
            @Override public void onNext(Integer integer) {
                Log.i("adu","inNext==》》"+integer);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_button:
                showRxAndroid();
                break;
            case R.id.two_button:
                creatObservable();
                break;
            case R.id.third_button:
                filter();
                break;

        }
    }
}
