/**
 * @Author:		Shiyao Qi
 * @Date:		2013.11.25
 * @Function:	List the files under SDcard, 
 * 				list the filepath according to the keyword,
 * 				get the md5 and sha1 values of the specified file.
 * @Email:		qishiyao2008@126.com
 */

package com.twlkyao.findfile;

import java.io.File;
import java.util.ArrayList;

import com.twlkyao.findfile.utils.FileOperation;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
//	private TextView textviewFilename; // The filename textview
	private EditText keyword; // The filename edittext
	private Button btnSearch; // The search button
	private String strKeyword; // The filename string
	private File SDcard = Environment.getExternalStorageDirectory(); // Get the external storage directory
	
//	private TextView textviewResult; // The result textview
	private ListView fileListView; // The listview to stotre the file information
	private ArrayList<File> filelist; // Used to store the filename
	private TextView search_result_label; // The search_result_label
	
	private FileListAdapter fileListAdapter; // The self-defined Adapter
	private FileOperation fileOperation; // The self-defined class
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViews(); // Find the views
		initData(Environment.getExternalStorageDirectory());
		setListeners(); // Set the listeners
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Find the views by id
	 */
	public void findViews() {
		keyword = (EditText) this.findViewById(R.id.keyword); // The filename textview
		btnSearch = (Button) this.findViewById(R.id.button_search); // The search button
		search_result_label= (TextView) this.findViewById(R.id.search_result_label); // The search result label
		fileListView = (ListView) this.findViewById(R.id.file_listview); // The listview to store file information
	}
	
	/**
	 * Set the search button listener
	 */
	public void setListeners() {
		
		// Set the btnSearch listener
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				search_result_label.setVisibility(View.VISIBLE); // Set the search_result_label visible
				
				strKeyword = keyword.getText().toString();
				if(strKeyword != null && !strKeyword.trim().equals("")) { // Judge if the edittext is null or empty
					
					fileOperation = new FileOperation();
					filelist = fileOperation.findFileByName(strKeyword, SDcard.toString());
					
					if(filelist.isEmpty()) { // The search result is null
						Toast.makeText(getApplicationContext(),
							getString(R.string.result_null),
								Toast.LENGTH_SHORT).show();
					} else { // Add the search result into the listview
						fileListAdapter = new FileListAdapter(getApplicationContext(), filelist,
								SDcard.equals(Environment.getExternalStorageDirectory()));
						fileListView.setAdapter(fileListAdapter);
					} 
				} else { // The input keyword is null
					Toast.makeText(getApplicationContext(),
						getString(R.string.keyword_null),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		// Set the fileListView listener
		fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				File file = (File) fileListAdapter.getItem(position);
				Log.d("OnClick", "postition:" + position + "id:" + id);
				if(!file.canRead()) { // If the file can't read, alert
					new AlertDialog.Builder(getApplicationContext())
						.setTitle(R.string.file_cannot_read_alert_title)
							.setPositiveButton(android.R.string.ok, new OnClickListener() {
							@Override
							public void onClick (DialogInterface dialog, int which) {
								
							}
					}).show();
				} else if(file.isDirectory()) { // If the clicked item is a directory, get into it
					/**
					filelist = findFileByName(strKeyword, file.toString());
					fileListAdapter = new FileListAdapter(getApplicationContext(), filelist,
							file.equals(Environment.getExternalStorageDirectory()));  
					fileListView.setAdapter(fileListAdapter);
					*/
					initData(file); // Use the function to update the list
					
				} else { // If the clicked item is a file, get the file information, such as md5 or sha1
					fileOperation = new FileOperation(); // Construct a new FileOperation instance
					String md5 = fileOperation.fileToMD5(file.getPath()); // Get the md5 value for use
					String sha1 = fileOperation.fileToSHA1(file.getPath()); // Get the sha1 value for use
					Log.d("Digest", "md5:" + md5 + "\nsha1:" + sha1);
				}
			}
		});
	}
	/**
	 * Update the fileListView
	 * @param folder The new folder path to display
	 */
	private void initData(File folder) {
        boolean isSDcard = folder.equals(SDcard); // Judge is the folder is the SDcard
        ArrayList<File> files = new ArrayList<File>();   
        if (!isSDcard) { // If the current folder is not the SDcard
        	files.add(Environment.getExternalStorageDirectory()); // Back to the SDcard
            files.add(folder.getParentFile()); // Back to parent
        }
        File[] filterFiles = folder.listFiles(); // Get the file list under the specified folder
        if (null != filterFiles && filterFiles.length > 0) {
            for (File file : filterFiles) { // Add the files to the ArrayList
                files.add(file);
            }
        }
        fileListAdapter= new FileListAdapter(getApplicationContext(), files, isSDcard); // Update the fileListAdapter
        fileListView.setAdapter(fileListAdapter); // Update the fileListView's adapter
    }
}
