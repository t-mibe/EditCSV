package com.mibe.editcsv;

import android.view.MenuItem;
import android.widget.Toast;

import com.mibe.draglistview.DragListActivity;

public abstract class EditCsvActivity extends DragListActivity {

	@Override
	public void setArrayList() {
		// 配列の初期化
		initArrayList();

		list_view.add("v1");
		list_data.add("d1");
	}

	@Override
	public boolean onItemClicked(int position) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
	
	/**
	 *  メニューが選択された時の処理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean ret = true;

		int id = item.getItemId();

		if(id == R.id.menu_refresh){
			Toast.makeText(this, getString(R.string.menu_refresh), Toast.LENGTH_SHORT).show();
			ret = true;
		} else if(id == R.id.menu_save){
			Toast.makeText(this, getString(R.string.menu_save), Toast.LENGTH_SHORT).show();
			ret = true;
		} else if(id == R.id.menu_add){
			Toast.makeText(this, getString(R.string.menu_add), Toast.LENGTH_SHORT).show();
			ret = true;
		} else if(id == R.id.menu_settings){
			Toast.makeText(this, getString(R.string.menu_settings), Toast.LENGTH_SHORT).show();
			ret = true;
		} else {
			ret = super.onOptionsItemSelected(item);
		}

		return ret;
	}
}
