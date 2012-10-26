package com.mibe.editcsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.mibe.draglistview.DragListActivity;

public abstract class EditCsvActivity extends DragListActivity {

	// 差分チェック用に保存したデータ配列
	public ArrayList<String> list_save = new ArrayList<String>();

	// 展開するCSVファイルのフルパス
	public String filePath = "";

	/**
	 * アクティビティ作成時
	 * CSVファイルの関連付けを行う
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){

		// 何かのパスが与えられている場合
		if(Intent.ACTION_VIEW.equals(getIntent().getAction())){

			// file:///path を file: と /path に分割する
			String fname[] = String.valueOf(getIntent().getData()).split("//");

			// 適正に分割できたらファイルパスを保存する
			if(fname.length == 2)filePath = fname[1];
		} else {

			// パスが与えられていない場合

			SharedPreferences sp = getPreferences(MODE_PRIVATE);

			filePath = sp.getString("sp_filePath", "");
		}

		// 親クラスの処理
		super.onCreate(savedInstanceState);
	}

	/**
	 * 配列の設定
	 */
	@Override
	public void setArrayList() {

		// 配列の初期化
		initArrayList();

		// ファイル展開処理
		if(!openCsvFile()){
			
			// ファイルパスが設定されなかったとき（終了させる？）
			
			
			list_view.add("v1");
			list_data.add("d1");
			list_save.add("s1");
		}
	}

	// ファイル展開処理
	public boolean openCsvFile(){

		// ファイルパスがまだ設定されていない場合
		if(filePath.equals("")){

			// TODO ファイル選択をここに（新規作成もここ）

			// ファイルを選んでいない場合の処理
			return false;
		}

		// ファイルの有無を検証
		if(! new File(filePath).isFile()){
			Toast.makeText(this, "hoge_f", Toast.LENGTH_SHORT).show();
			return false;
		}

		// ウィンドウタイトルを変更
		setTitle(new File(filePath).getName());

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "MS932"));

			//TODO レコード毎に処理する（単純な行分けではだめ）

			String line;
			while((line = br.readLine()) != null){

				// 表示用のテキスト
				list_view.add(getViewText(line));

				// レコード本体（編集後と編集前）
				list_data.add(line);
				list_save.add(line);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return true;
	}

	// 1つのレコードから表示するテキストを抽出する
	public String getViewText(String line){
		return "view_".concat(line);
	}



	/**
	 * 配列の内容を一時的に保存する
	 */
	@Override
	public void saveArrayList(Bundle bundle){
		
		// 変更前のデータ配列を保存する
		saveStringArray(bundle, "DLA_list_save", list_save);
		
		// 親クラスの処理を行う
		super.saveArrayList(bundle);
	}
	
	/**
	 * 配列の内容を復元する
	 */
	@Override
	public void loadArrayList(Bundle bundle){
		
		// 変更前のデータ配列を復元する
		list_save = loadStringArray(bundle, "DLA_list_save");
		
		// 親クラスの処理を行う
		super.loadArrayList(bundle);
	}

	// 自作の終了処理
	public void myFinish(){
		super.finish();
	}

	/**
	 * アクティビティ破棄時
	 * 保存のチェックを行う
	 * 開いていたファイルパスを保存する
	 */
	@Override
	public void finish(){

		// データに変更があるとき
		if(!list_data.equals(list_save)){
			// 保存を確認するダイアログを表示する
			showSaveDialog();
			
			// 終了処理を中止
			return;
		}

		// ファイルパスが存在する時
		if(!filePath.equals("")){

			// 最後に開いていたパスとして保存する
			SharedPreferences sp = getPreferences(MODE_PRIVATE);
			sp.edit().putString("sp_filePath", filePath).commit();
		}

		// 親クラスの処理
		super.finish();
	}

	/**
	 * 保存を確認するダイアログを表示する
	 */
	public void showSaveDialog(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("データに変更があります");
		builder.setMessage("選択してください");

		builder.setPositiveButton("保存して\n終了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveCsv(); // 上書き保存する
				myFinish(); // 終了する
			}
		});

		builder.setNeutralButton("保存せずに\n終了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				myFinish(); // 終了する
			}
		});

		builder.setNegativeButton("キャンセル",null);

		builder.show();
	}

	/**
	 * CSVファイルを上書き保存する
	 * 各レコードの長さを検証して，一番長い場所を探す
	 */
	public void saveCsv(){
		Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
	}

	/**
	 * アイテムが選択された時の処理
	 */
	@Override
	public boolean onItemClicked(int position) {
		Toast.makeText(this, "data: ".concat(list_data.get(position)), Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "save: ".concat(list_save.get(position)), Toast.LENGTH_SHORT).show();
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

	/**
	 * 再読み込みが押された時の処理
	 */
}
