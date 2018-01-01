package com.vise.bluetoothchat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vise.bluetoothchat.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class RecordActivity extends Activity implements OnClickListener {

	private ImageButton start;
	private ImageButton stop;
	private ListView listView;
	// 录音文件播放
	private MediaPlayer myPlayer;
	// 录音
	private MediaRecorder myRecorder;
	// 音频文件保存地址
	private String path;
	private String paths = path;
	private File saveFilePath;
	private TextView tips;
	//所录音的文件
	String[] listFile = null;

	ShowRecorderAdpter showRecord;
	AlertDialog aler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		start = (ImageButton) findViewById(R.id.start);
		stop = (ImageButton) findViewById(R.id.stop);
		listView = (ListView) findViewById(R.id.recordfile);
		tips=(TextView)findViewById(R.id.tips);
		myPlayer = new MediaPlayer();
		myRecorder = new MediaRecorder();
		//从麦克风源进行录音
		myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		// 设置输出格式
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		// 设置编码格式
		myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		showRecord = new ShowRecorderAdpter();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				path = Environment.getExternalStorageDirectory()
						.getCanonicalPath().toString()
						+ "/XIONGRECORDERS";
				File files = new File(path);
				if (!files.exists()) {
					files.mkdir();
				}
				listFile = files.list();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		if (listFile != null) {
			listView.setAdapter(showRecord);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record_main, menu);
		return true;
	}

	class ShowRecorderAdpter extends BaseAdapter {

		@Override
		public int getCount() {
			return listFile.length;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;

		}

		@Override
		public View getView(final int postion, View arg1, ViewGroup arg2) {
			View views = LayoutInflater.from(RecordActivity.this).inflate(
					R.layout.list_show_filerecorder, null);
			TextView filename = (TextView) views
					.findViewById(R.id.show_file_name);
			Button plays = (Button) views.findViewById(R.id.bt_list_play);
			Button stop = (Button) views.findViewById(R.id.bt_list_stop);
            Button delete = (Button) views.findViewById(R.id.bt_list_delete);

			filename.setText(listFile[postion]);
			// 播放录音
			plays.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try {
						myPlayer.reset();
						myPlayer.setDataSource(path + "/" + listFile[postion]);
						if (!myPlayer.isPlaying()) {

							myPlayer.prepare();
							myPlayer.start();
						} else {
							myPlayer.pause();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			// 停止播放
			stop.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (myPlayer.isPlaying()) {
						myPlayer.stop();
					}
				}
			});
            //删除
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
					if (saveFilePath.exists() && saveFilePath != null) {
                    saveFilePath.delete();
                    // 重新读取文件
                    File files = new File(path);
                    listFile = files.list();
                    // 刷新ListView
                    showRecord.notifyDataSetChanged();
                }
                }});
			return views;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			final EditText filename = new EditText(this);
			Builder alerBuidler = new Builder(this);
			alerBuidler
					.setTitle("请输入要保存的文件名")
					.setView(filename)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String text = filename.getText().toString();
									try {
										paths = path
												+ "/"
												+ text
												+ new SimpleDateFormat(
														"yyyyMMddHHmmss").format(System
														.currentTimeMillis())
												+ ".amr";
										saveFilePath = new File(paths);
										myRecorder.setOutputFile(saveFilePath
												.getAbsolutePath());
										saveFilePath.createNewFile();
										myRecorder.prepare();
										// 开始录音
										myRecorder.start();
										tips.setText("正在录音中");
										start.setEnabled(false);
										aler.dismiss();
										// 重新读取文件
										File files = new File(path);
										listFile = files.list();
										// 刷新ListView
										showRecord.notifyDataSetChanged();
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							});
			aler = alerBuidler.create();
			aler.setCanceledOnTouchOutside(false);
			aler.show();
			break;
		case R.id.stop:
			if (saveFilePath.exists() && saveFilePath != null) {
				myRecorder.stop();
				myRecorder.release();
				//判断是否保存，如果不保存则删除
				new Builder(this)
						.setTitle("是否保存该录音")
						.setPositiveButton("确定", null)
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										saveFilePath.delete();
										// 重新读取文件
										File files = new File(path);
										listFile = files.list();
										// 刷新ListView
										showRecord.notifyDataSetChanged();
									}
								}).show();

			}
			tips.setText("录音");
			start.setEnabled(true);
		default:
			break;
            case R.id.recorddelete:
		}

	}

	@Override
	protected void onDestroy() {
		// 释放资源
		if (myPlayer.isPlaying()) {
			myPlayer.stop();
			myPlayer.release();
		}
		myPlayer.release();
		myRecorder.release();
		super.onDestroy();
	}

}
