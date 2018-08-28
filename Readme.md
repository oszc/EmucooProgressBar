#效果
![加载失败](https://github.com/oszc/EmucooProgressBar/blob/master/gifs/pb_no_shadow.gif?raw=true)
![加载失败](https://github.com/oszc/EmucooProgressBar/blob/master/gifs/pb_with_shader.gif?raw=true)

#圆角ProgressBar使用方法：
```
<com.emucoo.emucooprogressbar.EmucooProgressView
            android:id="@+id/progress"
            android:layout_width="251dp"
            android:layout_height="wrap_content"
            app:emu_pv_max="100"
            app:emu_pv_need_tag="true"
            app:emu_pv_need_shadow="true"
            app:emu_pv_need_text="true"
            app:emu_pv_progress="2"
            app:emu_pv_keep_gradient="true"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            android:layout_marginTop="56dp"
            android:layout_marginStart="56dp"/>

```

#集成到项目：
```
implementation 'com.Emucoo:emucoo_progress_bar:1.0.5'
```
