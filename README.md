# 物联网智能衣柜设计-Android软件部分源代码

使用安卓手机，基于 Kotlin 语言和Jetpack Composer搭建用户界面，实现智能衣柜的软件部分。

## 架构

## 未使用的API

```
val tempResult: DailyWeatherResponse? = CaiyunWeatherApi.retrofitService.getDailyWeather(
                    WEATHER_API_TOKEN,
                    latlng.value!!
                )
```

```
 <!--天气（绿色页面）的布局-->
                <androidx.cardview.widget.CardView
                    android:id="@+id/cv_weather"
                    android:layout_width="match_parent"
                    android:layout_height="150dp"
                    android:layout_marginLeft="@dimen/space5"
                    android:layout_marginRight="@dimen/space5"
                    app:cardBackgroundColor="#4CAF50"
                    app:cardCornerRadius="24dp"
                    app:cardElevation="@dimen/space10"
                    app:layout_constraintLeft_toLeftOf="parent"
                    app:layout_constraintRight_toRightOf="parent"
                    app:layout_constraintTop_toTopOf="parent">


                    <androidx.constraintlayout.widget.ConstraintLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        tools:context=".ui.console.ConsoleFragment">


                        <TextView
                            android:id="@+id/tv_humidity_hint"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginStart="24dp"
                            android:layout_marginTop="@dimen/h3"
                            android:padding="4dp"
                            android:text="@{viewmodel.weatherCondition}"
                            android:textAlignment="center"
                            android:textColor="#FFFFFF"
                            android:textSize="16sp"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />


                        <TextView
                            android:id="@+id/textView7"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:fontFamily="sans-serif-medium"
                            android:padding="4dp"
                            android:text="@{Integer.toString(viewmodel.pmIndex)}"
                            android:textAlignment="center"
                            android:textColor="#FFFFFF"
                            android:textSize="36sp"
                            app:layout_constraintBaseline_toBottomOf="parent"
                            app:layout_constraintEnd_toEndOf="@+id/tv_humidity_hint"
                            app:layout_constraintHorizontal_bias="0.488"
                            app:layout_constraintStart_toStartOf="@+id/tv_humidity_hint"
                            app:layout_constraintTop_toBottomOf="@+id/tv_humidity_hint" />

                        <TextView
                            android:id="@+id/textView5"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:fontFamily="sans-serif-smallcaps"
                            android:padding="4dp"
                            android:text="@{Integer.toString(viewmodel.weatherForecastTemperature)}"
                            android:textAlignment="center"
                            android:textColor="#FFFFFF"
                            android:textSize="32sp"
                            app:layout_constraintEnd_toEndOf="@+id/textView4"
                            app:layout_constraintTop_toBottomOf="@+id/textView4" />

                        <TextView
                            android:id="@+id/textView4"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="16dp"
                            android:layout_marginEnd="24dp"
                            android:padding="4dp"
                            android:text="重庆市 渝北区"
                            android:textAlignment="center"
                            android:textColor="#FFFFFF"
                            android:textSize="16sp"
                            app:layout_constraintEnd_toEndOf="parent"
                            app:layout_constraintTop_toTopOf="parent" />

                        <TextView
                            android:id="@+id/textView6"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginStart="16dp"
                            android:layout_marginTop="8dp"
                            android:layout_marginBottom="16dp"
                            android:fontFamily="serif"
                            android:text="@{viewmodel.clothingSuggestion}"
                            android:textAlignment="center"
                            android:textColor="#FFFFFF"
                            android:textSize="12sp"
                            app:layout_constraintBottom_toBottomOf="parent"
                            app:layout_constraintStart_toStartOf="parent"
                            app:layout_constraintTop_toBottomOf="@+id/textView5" />

                    </androidx.constraintlayout.widget.ConstraintLayout>
                </androidx.cardview.widget.CardView>
```

