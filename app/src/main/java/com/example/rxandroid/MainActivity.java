package com.example.rxandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button1,button2,button3,button4,button5;
    private ImageView imageView;
    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void  init(){
        button1=(Button)findViewById(R.id.first_button);
        button2=(Button)findViewById(R.id.two_button);
        button3=(Button)findViewById(R.id.third_button);
        button4=(Button)findViewById(R.id.four_button);
        button5=(Button)findViewById(R.id.fiveth_button) ;
        imageView=(ImageView)findViewById(R.id.local_image) ;

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        client=new OkHttpClient();

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
    //加载一张本地图片
    public void showLocalImage(){
        final String imagPath="file:///android_asset/icon1.jpg";
        Observable observable = Observable.just(imagPath);
          observable.map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String s) {
                Log.i("hhj", "call: ==="+s);
                return BitmapFactory.decodeResource(getResources(),R.mipmap.icon1);
            }
        }).subscribeOn(Schedulers.newThread()) //开启一个新的线程
                .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Subscriber<Bitmap>() {
                     @Override
                     public void onCompleted() {
                         imageView.setVisibility(View.VISIBLE);
                         Log.i("hhj", "onCompleted: 图片加载完成");
                     }

                     @Override
                     public void onError(Throwable e) {

                     }

                     @Override
                     public void onNext(Bitmap bitmap) {
                         imageView.setImageBitmap(bitmap);

                     }
                 });
    }

    public void showOnlineImage(final String httpImagePath){
        Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(final Subscriber<? super byte[]> subscriber) {
                Request request=new Request.Builder().url(httpImagePath).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(new Throwable("错误"));

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            subscriber.onNext(response.body().bytes());

                        }else {
                            subscriber.onError(new Throwable("错误"));
                        }

                    }
                });

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT);

                    }

                    @Override
                    public void onNext(byte[] bytes) {
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
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
            case R.id.four_button:
                showLocalImage();
                break;
            case R.id.fiveth_button:
                showOnlineImage("http://pic.58pic.com/58pic/15/91/28/76E58PICWqY_1024.jpg");
                break;

        }
    }
}
