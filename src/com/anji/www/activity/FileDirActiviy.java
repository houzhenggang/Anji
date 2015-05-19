package com.anji.www.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.anji.www.R;
import com.anji.www.filebrowser.FileActivityHelper;
import com.anji.www.filebrowser.FileAdapter;
import com.anji.www.filebrowser.FileInfo;
import com.anji.www.filebrowser.FileUtil;
import com.anji.www.filebrowser.PasteFile;
import com.anji.www.util.LogUtil;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.KeyEvent;

public class FileDirActiviy extends ListActivity implements OnClickListener
{
	private TextView _filePath;
	private Button bt_back;
	private TextView tv_title;
	private List<FileInfo> _files = new ArrayList<FileInfo>();
	// private String _rootPath = FileUtil.getSDPath();
	private String _rootPath;
	private String _currentPath;
	private final String TAG = "Main";
	private final int MENU_RENAME = Menu.FIRST;
	private final int MENU_COPY = Menu.FIRST + 3;
	private final int MENU_MOVE = Menu.FIRST + 4;
	private final int MENU_DELETE = Menu.FIRST + 5;
	private final int MENU_INFO = Menu.FIRST + 6;
	private BaseAdapter adapter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_dir);

		_filePath = (TextView) findViewById(R.id.file_path);
		bt_back = (Button) findViewById(R.id.bt_back);
		bt_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(getString(R.string.browse));
		_rootPath = Environment.getExternalStorageDirectory().toString()
				+ "/anji";
		_currentPath = _rootPath;
		// �󶨳����¼�
		// getListView().setOnItemLongClickListener(_onItemLongClickListener);

		// ע�������Ĳ˵�
		registerForContextMenu(getListView());

		// ������
		adapter = new FileAdapter(this, _files);
		setListAdapter(adapter);

		// ��ȡ��ǰĿ¼���ļ��б�
		viewFiles(_currentPath);
	}

	/** �����Ĳ˵� **/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try
		{
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		}
		catch (ClassCastException e)
		{
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		FileInfo f = _files.get(info.position);
		menu.setHeaderTitle(f.Name);
		menu.add(0, MENU_RENAME, 1, getString(R.string.file_rename));
		menu.add(0, MENU_COPY, 2, getString(R.string.file_copy));
		menu.add(0, MENU_MOVE, 3, getString(R.string.file_move));
		menu.add(0, MENU_DELETE, 4, getString(R.string.file_delete));
		menu.add(0, MENU_INFO, 5, getString(R.string.file_info));
	}

	/** �����Ĳ˵��¼����� **/
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		FileInfo fileInfo = _files.get(info.position);
		File f = new File(fileInfo.Path);
		switch (item.getItemId())
		{
		case MENU_RENAME:
			FileActivityHelper.renameFile(FileDirActiviy.this, f,
					renameFileHandler);
			return true;
		case MENU_COPY:
			pasteFile(f.getPath(), "COPY");
			return true;
		case MENU_MOVE:
			pasteFile(f.getPath(), "MOVE");
			return true;
		case MENU_DELETE:
			FileUtil.deleteFile(f);
			viewFiles(_currentPath);
			return true;
		case MENU_INFO:
			FileActivityHelper.viewFileInfo(FileDirActiviy.this, f);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/** �б�����¼����� **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		FileInfo f = _files.get(position);

		if (f.IsDirectory)
		{
			viewFiles(f.Path);
		}
		else
		{
			openFile(f.Path);
		}
	}

	/** �ض��巵�ؼ��¼� **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// ����back����
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			File f = new File(_currentPath);
			String parentPath = f.getParent();
			LogUtil.LogI(TAG, " KEYCODE_BACK _currentPath=" + _currentPath);
			LogUtil.LogI(TAG, " KEYCODE_BACK parentPath=" + parentPath);

			if (_currentPath.equals(_rootPath))
			{
				super.onBackPressed();
			}
			else
			{
				viewFiles(parentPath);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** ��ȡ��PasteFile���ݹ�����·�� **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (Activity.RESULT_OK == resultCode)
		{
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH"))
			{
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
	}

	// /** �����˵� **/
	// public boolean onCreateOptionsMenu(Menu menu)
	// {
	// // MenuInflater inflater = this.getMenuInflater();
	// // inflater.inflate(R.menu.main_menu, menu);
	// return true;
	// }

	/** �˵��¼� **/
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.mainmenu_home:
			viewFiles(_rootPath);
			break;
		case R.id.mainmenu_refresh:
			viewFiles(_currentPath);
			break;
		case R.id.mainmenu_createdir:
			FileActivityHelper.createDir(FileDirActiviy.this, _currentPath,
					createDirHandler);
			break;
		case R.id.mainmenu_exit:
			exit();
			break;
		default:
			break;
		}
		return true;
	}

	/** ��ȡ��Ŀ¼�������ļ� **/
	private void viewFiles(String filePath)
	{
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(
				FileDirActiviy.this, filePath);
		if (tmp != null)
		{
			// �������
			_files.clear();
			_files.addAll(tmp);
			tmp.clear();

			// ���õ�ǰĿ¼
			_currentPath = filePath;
			_filePath.setText(filePath);

			// this.onContentChanged();
			adapter.notifyDataSetChanged();
		}
	}

	/** �����¼����� **/
	/**
	 * private OnItemLongClickListener _onItemLongClickListener = new
	 * OnItemLongClickListener() {
	 * 
	 * @Override public boolean onItemLongClick(AdapterView<?> parent, View
	 *           view, int position, long id) { Log.e(TAG, "position:" +
	 *           position); return true; } };
	 **/

	/** ���ļ� **/
	private void openFile(String path)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/** �������ص�ί�� **/
	private final Handler renameFileHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 0) viewFiles(_currentPath);
		}
	};

	/** �����ļ��лص�ί�� **/
	private final Handler createDirHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 0) viewFiles(_currentPath);
		}
	};

	/** ճ���ļ� **/
	private void pasteFile(String path, String action)
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("CURRENTPASTEFILEPATH", path);
		bundle.putString("ACTION", action);
		intent.putExtras(bundle);
		intent.setClass(FileDirActiviy.this, PasteFile.class);
		// ��һ��Activity���ȴ����
		startActivityForResult(intent, 0);
	}

	/** �˳����� **/
	private void exit()
	{

		new AlertDialog.Builder(FileDirActiviy.this)
				.setMessage(R.string.confirm_exit).setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						FileDirActiviy.this.finish();
						// android.os.Process.killProcess(android.os.Process
						// .myPid());
						// System.exit(0);
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				}).show();
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		switch (id)
		{
		case R.id.bt_back:
			super.onBackPressed();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
			break;

		default:
			break;
		}
	}
}
