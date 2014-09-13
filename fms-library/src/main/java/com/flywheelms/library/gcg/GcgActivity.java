/* @(#)GcgActivity.java
 ** 
 ** Copyright (C) 2012 by Steven D. Stamps
 **
 **             Trademarks & Copyrights
 ** Flywheel Management Science(TM), Flywheel Management Model(TM),
 ** Flywheel Story Editor(TM) and FlywheelMS(TM) are exclusive trademarks
 ** of Steven D. Stamps and may only be used freely for the purpose of
 ** identifying the unforked version of this software.  Subsequent forks
 ** may not use these trademarks.  All other rights are reserved.
 **
 ** DecKanGL (Decorated Kanban Glyph Language) and TribKn (Tribal Knowledge)
 ** are also exclusive trademarks of Steven D. Stamps.  These may be used
 ** freely within the unforked FlywheelMS application and documentation.
 ** All other rights are reserved.
 **
 ** gConGUI (game Controller Graphical User Interface) is an exclusive
 ** trademark of Steven D. Stamps.  This trademark may be used freely
 ** within the unforked FlywheelMS application and documentation.
 ** All other rights are reserved.
 **
 ** Trademark information is available at
 ** <http://www.flywheelms.com/trademarks>
 **
 ** Flywheel Management Science(TM) is a copyrighted body of management
 ** metaphors, governance processes, and leadership techniques that is
 ** owned by Steven D. Stamps.  These copyrighted materials may be freely
 ** used, without alteration, by the community (users and developers)
 ** surrounding this GPL3-licensed software.  Additional copyright
 ** information is available at <http://www.flywheelms.org/copyrights>
 **
 **              GPL3 Software License
 ** This program is free software: you can use it, redistribute it and/or
 ** modify it under the terms of the GNU General Public License, version 3,
 ** as published by the Free Software Foundation. This program is distributed
 ** in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 ** even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 ** PURPOSE.  See the GNU General Public License for more details. You should
 ** have received a copy of the GNU General Public License, in a file named
 ** COPYING, along with this program.  If you cannot find your copy, see
 ** <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.flywheelms.library.gcg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flywheelms.library.R;
import com.flywheelms.library.fdk.FdkHostSupport;
import com.flywheelms.library.fdk.enumerator.FdkKeyboardState;
import com.flywheelms.library.fdk.interfaces.FdkHost;
import com.flywheelms.library.fdk.widget.FdkKeyboard;
import com.flywheelms.library.fmm.enumerator.FmmNodeTransactionType;
import com.flywheelms.library.fmm.node.NodeId;
import com.flywheelms.library.fmm.node.impl.enumerator.FmmNodeDefinition;
import com.flywheelms.library.fmm.repository.FmmAccessScope;
import com.flywheelms.library.fmm.transaction.FmmDataRefreshNotice;
import com.flywheelms.library.fms.context.FmsNavigationTarget;
import com.flywheelms.library.fms.dialog.FmsDialog;
import com.flywheelms.library.fms.dialog.FmsRevertDataOkCancelDialog;
import com.flywheelms.library.fms.dialog.FmsSaveChangesDialog;
import com.flywheelms.library.fms.helper.FmsActivityHelper;
import com.flywheelms.library.gcg.context.GcgActivityBreadcrumb;
import com.flywheelms.library.gcg.context.GcgApplicationContext;
import com.flywheelms.library.gcg.context.GcgApplicationContextHeader;
import com.flywheelms.library.gcg.context.GcgFrameBreadcrumb;
import com.flywheelms.library.gcg.enumerator.GcgDoItNowMenuItemState;
import com.flywheelms.library.gcg.interfaces.GcgDoItNowClient;
import com.flywheelms.library.gcg.interfaces.GcgFrame;
import com.flywheelms.library.gcg.interfaces.GcgGuiable;
import com.flywheelms.library.gcg.interfaces.GcgPerspective;
import com.flywheelms.library.gcg.listeners.GcgDoItNowListener;
import com.flywheelms.library.gcg.menu.GcgFrameSpinner;
import com.flywheelms.library.gcg.treeview.GcgTreeViewAdapter;
import com.flywheelms.library.gcg.view.GcgActivityStatusIndicator;
import com.flywheelms.library.gcg.view.GcgDoItNowView;
import com.flywheelms.library.gcg.viewflipper.GcgPerspectiveFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public abstract class GcgActivity extends Activity implements FdkHost, GcgDoItNowClient {

	protected boolean isMainGcgApplicationActivity = false;
	protected TextView activityCurtain;
	protected Stack<FmsDialog> modalFmsDialogStack = new Stack<FmsDialog>();
	protected boolean mustSelectDataSource;
	protected boolean dataRefreshAll = false;
	protected boolean parentDataRefreshAll = false;
	protected ArrayList<FmmDataRefreshNotice> dataRefreshList;
	protected ArrayList<FmmDataRefreshNotice> parentDataRefreshList;
	private Bundle savedInstanceState;
	private String activityLabel;
	protected GcgFrameSpinner frameSpinner;
	protected boolean setDisplayHome = true;
	private String activityNodeIdString;
	private String helpContextUrlString = "";
	public FdkHostSupport fdkHostSupport;
	private MenuItem publishPdfMenuItem;
	protected MenuItem activityStatusIndicatorMenuItem;
	protected GcgActivityStatusIndicator activityStatusIndicator;
	protected LinearLayout activityLayout;
	private GcgApplicationContext gcgApplicationContext;
	private GcgApplicationContextHeader gcgApplicationContextHeader;
	private ArrayList<GcgGuiable> perspectiveContext;
	private MenuItem doItNowMenuItem;
	private GcgDoItNowView doItNowView;
	private OnLongClickListener saveDataLongClickListener;
	private GcgDoItNowMenuItemState doItNowMenuItemState = GcgDoItNowMenuItemState.DISABLED;
	private GcgDoItNowListener doItNowListener;
	protected boolean dataHasBeenModified = false;
	protected boolean discardDataChanges = false;
	private String activeViewGroupName = null;
	private GcgTreeViewAdapter activeTreeViewAdapter;
	protected Hashtable<String, FmmNodeTransactionType> modifiedFmmNodeIdList = new Hashtable<String, FmmNodeTransactionType>();
	protected Hashtable<String, FmmNodeTransactionType> queuedChildModifiedFmmNodeIdTable;
	protected float lastXposition;  // for detecting page swipes

	public GcgActivity(String anInitialHelpContextUrlString) {
		super();
		this.helpContextUrlString = anInitialHelpContextUrlString;
		this.activityNodeIdString = new NodeId("FLA").getNodeIdString();
	}
	
	@Override
	public void onPostResume() {
		super.onPostResume();
		if(this.dataRefreshAll) {
			refreshDataDisplay();
			this.dataRefreshAll = false;
		} else if(this.dataRefreshList != null) {
			refreshDataDisplay();
		}
		this.dataRefreshList = null;
		resetSoftKeyboard();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		resetSoftKeyboard();
	}

	private void resetSoftKeyboard() {
		if(this.fdkHostSupport != null) {
			this.fdkHostSupport.resetSoftKeyboard(getWindowToken());
		}
	}

	// override to implement granular, application-specific updates
//	private void processDataRefreshList() {
//		buildContentViewForDataSource();
//	}

	protected void processExtras() {
		try {
			if(getIntent().getExtras() != null) {
				if(getIntent().hasExtra(FmsActivityHelper.bundle_key__FMS_CONTEXT)) {
					setGcgApplicationContext(new GcgApplicationContext(new JSONObject(getIntent().getExtras().getString(FmsActivityHelper.bundle_key__FMS_CONTEXT))));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Context getContext() {
		return this;
	}

	public GcgApplicationContext getGcgApplicationContext() {
		return this.gcgApplicationContext;
	}

	public void setGcgApplicationContext(GcgApplicationContext anApplicationContext) {
		this.gcgApplicationContext = anApplicationContext;
	}

	public void updateGcgApplicationContext() {
		return;
	}
	
	public void resetApplicationContext() {
		this.gcgApplicationContextHeader.resetApplicationContext();
	}

	protected void initializeGcgApplicationContextHeader() {
		setGcgApplicationContextHeader( (GcgApplicationContextHeader) findViewById(R.id.gcg__application_context__header));
		getGcgApplicationContextHeader().initialize(this);
	}

	private void setGcgApplicationContextHeader(GcgApplicationContextHeader anApplicationContextHeader) {
		this.gcgApplicationContextHeader = anApplicationContextHeader;
	}

	protected void initializeGcgApplicationContext() {
		setGcgApplicationContext(new GcgApplicationContext(R.drawable.gcg__unspecified_glyph, "Unknown Data Source"));
	}
	
	protected ArrayList<GcgGuiable> getPerspectiveContextGuiableList() {
		return new ArrayList<GcgGuiable>();
	}

	protected void removeNavigationButton() {
		Button theButton = (Button) getGcgApplicationContextHeader().findViewById(R.id.gcg__application_context__navigation_button);
		theButton.setVisibility(View.GONE);
	}

	protected GcgApplicationContextHeader getGcgApplicationContextHeader() {
		return this.gcgApplicationContextHeader;
	}

	@Override
	public void onSaveInstanceState(Bundle theBundle) {
		super.onSaveInstanceState(theBundle);
		if(getGcgApplicationContext() != null) {
			theBundle.putString(FmsActivityHelper.bundle_key__FMS_CONTEXT, getGcgApplicationContext().getSerialized());
		}
	}
	
	@Override
	public void onRestoreInstanceState(Bundle aSavedInstanceState) {
		super.onRestoreInstanceState(aSavedInstanceState);
	}

	@Override
	public void onWindowFocusChanged(boolean bHasFocus) {
		if(bHasFocus && ! this.modalFmsDialogStack.empty()) {
			this.modalFmsDialogStack.peek().restartDialog();
		}
	}

	@Override
	protected void onCreate(Bundle aSavedInstanceState) {
		super.onCreate(aSavedInstanceState);
		this.savedInstanceState = aSavedInstanceState;
		this.fdkHostSupport = new FdkHostSupport(this);
		buildContentViewForDataSource();
	}
	
	@Override
	public void onBackPressed() {
	  finish();
	}
	
	protected View getRootView() {
		return findViewById(android.R.id.content);
	}

	protected void selectDataSource() {
		return;
	}

	protected void buildContentViewForDataSource() {
		setContentView(R.layout.gcg__library_activity);
		this.activityLayout = (LinearLayout) findViewById(R.id.fms_library_activity__layout_root);
		this.activityLayout.setBackgroundResource(getRootLayoutBackgroundResourceId());
		layoutInflation();
	}
	
	protected void enableActivityCurtain(boolean aBoolean) {
		if(this.activityCurtain == null) {
			return;
		}
		if(aBoolean) {
			this.activityCurtain.setVisibility(View.VISIBLE);
			this.activityCurtain.bringToFront();
		} else {
			this.activityCurtain.setVisibility(View.GONE);
		}
	}

	protected boolean activityCurtainEnabled() {
		return this.activityCurtain == null ? false : this.activityCurtain.getVisibility() == View.VISIBLE;
	}

	public void layoutInflation() {
		getLayoutInflater().inflate(getContentViewResourceId(), this.activityLayout, true);
		initializeActivityCurtain();
		getActionBar().setDisplayHomeAsUpEnabled(this.setDisplayHome);
		if(this.savedInstanceState != null) {
			processSavedInstanceState(this.savedInstanceState);
		} else {
			processExtras();
		}
		initializeGui();
	}
	
	public void dataSourceSelected(@SuppressWarnings("unused") Object aDataSourceObject) {
		setMustSelectDataSource(false);
		buildContentViewForDataSource();
		initializeGcgApplicationContext();
		initializeGcgApplicationContextHeader();
	}
	
	protected void disableDisplayHome() {
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}
	
	protected void enableDisplayHome() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	protected int getRootLayoutBackgroundResourceId() {
		return R.color.portobello;
	}

	protected void initializeGui() {
		initializeActivityCurtain();
	}

	private void initializeActivityCurtain() {
		this.activityCurtain = (TextView) findViewById(R.id.gcg__activity_curtain);
	}

	protected void processSavedInstanceState(@SuppressWarnings("unused") Bundle aSavedInstanceState) { return; }

	protected abstract int getContentViewResourceId();

	protected int getActivityRootViewResourceId() {
		return 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.fdkHostSupport.destroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu aMenu) {
		MenuInflater aMenuInflater = getMenuInflater();
		aMenuInflater.inflate(R.menu.fms_activity__default_options_menu, aMenu);
		this.fdkHostSupport.initOptionsMenu(aMenu);
		this.doItNowView = new GcgDoItNowView(this);
		this.saveDataLongClickListener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View aView) {
				GcgActivity.this.requestRevertAllModifiedData();
				GcgActivity.this.doItNowView.drawSaveDataBitmap();
				return true;
			}
		};
		this.doItNowMenuItem = aMenu.findItem(R.id.action__do_it_now);
		this.doItNowMenuItem.setActionView(this.doItNowView);
		this.activityStatusIndicatorMenuItem = aMenu.findItem(R.id.activity_status_indicator);
		this.activityStatusIndicatorMenuItem.setActionView(getActivityStatusIndicator());
		this.publishPdfMenuItem = aMenu.findItem(R.id.action__publish_pdf);
		if(! isPdfPublisher()) {
			aMenu.removeItem(R.id.action__publish_pdf);
		}
		resetActionBarSave();
		return true;
	}
	
	protected GcgActivityStatusIndicator getActivityStatusIndicator() {
		if(this.activityStatusIndicator == null) {
			this.activityStatusIndicator = new GcgActivityStatusIndicator(this);
		}
		return this.activityStatusIndicator;
	}
	
	protected void setDoItNowState(GcgDoItNowMenuItemState aDoItNowState) {
		this.doItNowMenuItemState = aDoItNowState;
		switch (aDoItNowState) {
			case DISABLED:
				this.doItNowView.clear();
				this.doItNowView.setOnLongClickListener(null);
				break;
			case DO_IT_NOW:
				this.doItNowView.drawDoItNowBitmap();
				this.doItNowView.setOnLongClickListener(null);
				break;
			case SAVE:
				this.doItNowView.drawSaveDataBitmap();
				this.doItNowView.setOnLongClickListener(this.saveDataLongClickListener);
				break;
			default:
		}
	}
	
	protected GcgDoItNowMenuItemState getGcgDoItNowMenuItemState() {
		return this.doItNowMenuItemState;
	}
	
	protected void setDoItNowListener(GcgDoItNowListener aDoItNowListener) {
		this.doItNowListener = aDoItNowListener;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem aMenuItem) {
		int itemId = aMenuItem.getItemId();  // cannot use switch statement because R data members are from a library
		if (itemId == android.R.id.home) {
			this.finish();
		} else if (itemId == R.id.action__help) {
			Intent theIntent = new Intent(Intent.ACTION_VIEW, 
					Uri.parse(getHelpContextUrlString()));
			startActivity(theIntent);
		} else if (itemId == FdkHostSupport.menu_item__FDK) {
			switch(this.fdkHostSupport.getKeyboardState()) {
			case AVAILABLE:
				this.fdkHostSupport.setKeyboardState(FdkKeyboardState.ACTIVE);
				this.fdkHostSupport.setMenuItemIcon();
				this.fdkHostSupport.activateFdkKeyboard(true);
				break;
			case ACTIVE:
				this.fdkHostSupport.setKeyboardState(FdkKeyboardState.AVAILABLE);
				this.fdkHostSupport.setMenuItemIcon();
				this.fdkHostSupport.activateFdkKeyboard(false);
				break;
			case DISABLED:
				break;
			default:
			}
		} else if (itemId == R.id.action__do_it_now) {
			clickDoItNowMenuItem();
		} else if (itemId == R.id.action__publish_pdf) {
			launchPdfPublishingWizard();
		}
		return true;
	} 

	@Override
	public void clickDoItNowMenuItem() {
		switch (this.doItNowMenuItemState) {
			case DO_IT_NOW:
				startGreenActivityStatusAnimation();
            	setDoItNowState(GcgDoItNowMenuItemState.DISABLED);
				this.doItNowListener.doItNow();
				break;
			case SAVE:
				startMagentaActivityStatusAnimation();
            	setDoItNowState(GcgDoItNowMenuItemState.DISABLED);
				saveAllDataModifications();
				stopActivityStatusAnimation();
				break;
			case DISABLED:
				break;
			default:
		}
	}
	
	public void startGreenActivityStatusAnimation() {  // Do It Now processing
		getActivityStatusIndicator().startGreenAnimation();
	}
	
	public void startMagentaActivityStatusAnimation() {  // saving data
		getActivityStatusIndicator().startMagentaAnimation();
	}
	
	public void startOrangeActivityStatusAnimation() {  // retrieving data
		getActivityStatusIndicator().startOrangeAnimation();
	}
	
	public void startBlueActivityStatusAnimation() {  // starting an activity
		getActivityStatusIndicator().startBlueAnimation(false);
	}

	public void startBlueActivityStatusAnimation(boolean aBoolean) {
		getActivityStatusIndicator().startBlueAnimation(aBoolean);
	}
	
	public void startYellowActivityStatusAnimation() {  // auto-updates from database mediator
		getActivityStatusIndicator().startYellowAnimation();
	}
	
	public void stopActivityStatusAnimation() {
		if(this.activityStatusIndicator != null) {
			getActivityStatusIndicator().stopAnimation();
		}
	}

	protected void launchPdfPublishingWizard() {
		return;
	}

	protected void saveAllDataModifications() {
		resetDataHasBeenModified();
	}
	
	public void setMustSelectDataSource(boolean aBoolean) {
		this.mustSelectDataSource = aBoolean;
	}
	
	public boolean mustSelectDataSource() {
		return this.mustSelectDataSource;
	}
	
	public void requestRevertAllModifiedData() {
		this.modalFmsDialogStack.push(new FmsRevertDataOkCancelDialog(this)); 
		this.modalFmsDialogStack.peek().processDialog();
	}
	
	public void revertAllDataModifications() {  return;  }

	protected boolean isPdfPublisher() {
		return false;
	}
	
	protected File generatePdfFile() {
		return null;
	}
	
	protected boolean isDataModified() {
		return this.dataHasBeenModified;
	}

	public void onDataStateChange() {
		resetActionBarSave();
	}
	
	private void resetDataHasBeenModified() {
		this.dataHasBeenModified = false;
		resetActionBarSave();
	}
	
	public void refreshDataDisplay() {
/*
		startOrangeActivityStatusAnimation();
		// do what you gotta do
		stopActivityStatusAnimation();
 */
	}

	public void resetActionBarSave() {
		if(this.doItNowMenuItem != null) {
			if(this.dataHasBeenModified) {
				setDoItNowState(GcgDoItNowMenuItemState.SAVE);
			} else {
				setDoItNowState(GcgDoItNowMenuItemState.DISABLED);
			}
		}
		if(this.publishPdfMenuItem != null) {  // Hack Alert - for onRestoreInstanceState()
			this.publishPdfMenuItem.setEnabled(! this.dataHasBeenModified);
		}
	}

	public void setHelpContextUrlString(String aHelpContextUrlString) {
		this.helpContextUrlString = aHelpContextUrlString;
	}

	public String getHelpContextUrlString() {
		return this.helpContextUrlString;
	}
	
	public String getActivityNodeIdString() {
		return this.activityNodeIdString;
	}

	public void enableMultiShiftControls(@SuppressWarnings("unused") boolean aBoolean) { return; }

	public void enableDoItNowButton(GcgDoItNowListener aDoItNowListener) {
		this.doItNowListener = aDoItNowListener;
		setDoItNowState(GcgDoItNowMenuItemState.DO_IT_NOW);
	}

	public void disableDoItNowButton() {
		setDoItNowState(GcgDoItNowMenuItemState.DISABLED);
	}
	
	public GcgActivityBreadcrumb getGcgActivityBreadcrumb() {
		GcgActivityBreadcrumb theActivityBreadcrumb = new GcgActivityBreadcrumb(
				getBreadcrumbDrawableResourceId(),
				getBreadcrumbHeadline(),
				getActivityNodeIdString(),
				getBreadcrumbTargetNodeIdString() );
		theActivityBreadcrumb.setFrameBreadcrumb(getFrameBreadcrumb());
		return theActivityBreadcrumb;
	}

	protected GcgFrameBreadcrumb getFrameBreadcrumb() {
		if(getGcgFrame() == null) {
			return null;
		}
		GcgFrameBreadcrumb theFrameBreadcrumb = new GcgFrameBreadcrumb(R.drawable.gcg__frame, getGcgFrameName());
		if(getGcgPerspective() != null) {
			theFrameBreadcrumb.setPerspective(getGcgPerspective());
			theFrameBreadcrumb.setPerspectiveContext(this.perspectiveContext);
		}
		return theFrameBreadcrumb;
	}

	protected int getBreadcrumbDrawableResourceId() {
		return getDisplayedFmmNodeDefinition().getLabelDrawableResourceId();
	}

	protected abstract String getBreadcrumbHeadline();

	protected abstract FmmNodeDefinition getDisplayedFmmNodeDefinition();

	protected abstract String getBreadcrumbTargetNodeIdString();


	@Override
	protected void onActivityResult(int aRequestCode, int aResultCode, Intent anIntent) {
		if(anIntent == null) {
			return;
		}
		this.queuedChildModifiedFmmNodeIdTable = FmsActivityHelper.getModifiedNodeHashTable(anIntent);
		if(aResultCode != FmsNavigationTarget.request_code__NAVIGATE) {
			this.queuedChildModifiedFmmNodeIdTable.putAll(FmsActivityHelper.getModifiedNodeHashTable(anIntent));
			updateDataRefreshInfo(anIntent);
			super.onActivityResult(aRequestCode, aResultCode, anIntent);
		} else {
			updateNodeModificationListForContextNavigation(anIntent);
			processFmsApplicationContextNavigationIntent(anIntent);
		}
	}

	private void updateDataRefreshInfo(Intent anIntent) {
		if(anIntent.getExtras().containsKey(FmsActivityHelper.bundle_key__DATA_REFRESH__ALL)) {
			this.dataRefreshAll = true;
		} else if (anIntent.getExtras().containsKey(FmsActivityHelper.bundle_key__DATA_REFRESH__NOTICE_LIST)) {
			this.dataRefreshList = new ArrayList<FmmDataRefreshNotice>();
		}
	}

	private void updateNodeModificationListForContextNavigation(Intent anIntent) {
		Intent theIntent = anIntent;
		if(anIntent == null) {
			if(this.modifiedFmmNodeIdList.size() < 1) {
				return;
			}
			theIntent = new Intent();
		}
		Hashtable<String, FmmNodeTransactionType> theModifiedNodeIdTable = FmsActivityHelper.getModifiedNodeHashTable(theIntent);
		if(theModifiedNodeIdTable.size() > 0) {
			this.modifiedFmmNodeIdList.putAll(theModifiedNodeIdTable);
			theIntent.putExtra(FmsActivityHelper.bundle_key__MODIFIED_FMM_NODE__MAP, FmsActivityHelper.getSerializedModifiedNodeTable(this.modifiedFmmNodeIdList));
		}
	}

	protected void processFmsApplicationContextNavigationIntent(Intent anIntent) {
		String theString = anIntent.getStringExtra(FmsNavigationTarget.bundle_key__FMS_NAVIGATION_TARGET);
		FmsNavigationTarget theNavigationTarget = null;
		try {
			theNavigationTarget = new FmsNavigationTarget(new JSONObject(theString));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fmsApplicationContextNavigation(theNavigationTarget);
	}
	
	public void fmsApplicationContextNavigation(FmsNavigationTarget anFmsApplicationContextNavigationTarget) {
		if(this.isMainGcgApplicationActivity) {
			navigateToCurrentFramePerspective(anFmsApplicationContextNavigationTarget);
			return;
		}
		if(anFmsApplicationContextNavigationTarget.getFmsApplicationContextBreadcrumb() == null || ! this.activityNodeIdString.equals(
				anFmsApplicationContextNavigationTarget.getFmsApplicationContextBreadcrumb().getActivityIdString()) ) {
			// create data intent
			Intent theIntent = new Intent();   
			theIntent.putExtra(FmsNavigationTarget.bundle_key__FMS_NAVIGATION_TARGET, anFmsApplicationContextNavigationTarget.getJsonObject().toString());
			finish(FmsNavigationTarget.request_code__NAVIGATE, theIntent);
		} 	// else stop on this activity
	}

	private void navigateToCurrentFramePerspective(FmsNavigationTarget anFmsApplicationContextNavigationTarget) {
		if(getGcgFrame() == null || getGcgPerspective() == null) {
			return;
		}
		if(getGcgPerspective() != anFmsApplicationContextNavigationTarget.getGcgPerspective()) {
//			setGcgPerspective(anFmsApplicationContextNavigationTarget.getGcgPerspective());
			activityNavigation(getGcgFrame(), anFmsApplicationContextNavigationTarget.getGcgPerspective());
		}
	}

	public void activityNavigation(GcgFrame aFrame, GcgPerspective aPerspective) {
		setGcgFrame(aFrame);
		setGcgPerspective(aPerspective);
	}

	@Override
	public void finish() {
		finishOk();
	}

	public void finish(int anActivityResultCode) {
		finish(anActivityResultCode, null);
	}

	public void finish(Intent anIntent) {
		finish(Activity.RESULT_OK, anIntent);
	}
	
	public void finishOk() {
		finish(Activity.RESULT_OK, null);
	}
	
	public void finish(int anActivityResultCode, Intent anIntent) {
		if(protectDataChanges(FmsSaveChangesDialog.next_action__FINISH, getActiveViewGroupName())) {
			return;
		}
		Intent theIntent = anIntent;
		if(theIntent == null) {
			theIntent = new Intent();
		}
		if(this.modifiedFmmNodeIdList.size() > 0) {
			theIntent.putExtra(FmsActivityHelper.bundle_key__MODIFIED_FMM_NODE__MAP, FmsActivityHelper.getSerializedModifiedNodeTable(this.modifiedFmmNodeIdList));
		}
		if(this.parentDataRefreshAll) {
			theIntent.putExtra(FmsActivityHelper.bundle_key__DATA_REFRESH__ALL, "");
		}
		if(this.parentDataRefreshList != null) {
			theIntent.putExtra(FmsActivityHelper.bundle_key__DATA_REFRESH__NOTICE_LIST, getSerializedParentDataRefreshNoticeList());
		}
		if(anIntent == null && theIntent.getExtras() == null) {
			setResult(anActivityResultCode);
		} else {
			setResult(anActivityResultCode, theIntent);
		}
		super.finish();
	}

	private String getSerializedParentDataRefreshNoticeList() {
		String theString = "";
		for(@SuppressWarnings("unused") FmmDataRefreshNotice theDataRefreshNotice : this.parentDataRefreshList) {
			// TODO - unfinished
		}
		return theString;
	}

	public boolean protectDataChanges(int aNextAction, String aTargetDetail) {
		if(this.discardDataChanges) {
			return false;
		}
		if(isDataModified() && ! automaticallySaveDataModifications()) {
			startDialog(new FmsSaveChangesDialog(this, aTargetDetail, aNextAction));
			return true;
		}
		if(isDataModified()) {
			saveAllDataModifications();
		}
		return false;
	}

	private static boolean automaticallySaveDataModifications() {
		return GcgApplication.getInstance().automaticallySaveDataModifications();
	}

	public void saveDataChangesDialogResults(int aButtonChoice, boolean bAutoSaveNextTime, int aNextAction) {
		setAutomaticallySaveDataModifications(bAutoSaveNextTime);
		switch (aButtonChoice) {
			case FmsSaveChangesDialog.button_choice__DISCARD:
				this.discardDataChanges = true;
				resetDataHasBeenModified();
				initiateNextAction(aNextAction);
				break;
			case FmsSaveChangesDialog.button_choice__SAVE:
				saveAllDataModifications();
				initiateNextAction(aNextAction);
				break;
			case FmsSaveChangesDialog.button_choice__CANCEL_NAVIGATION:
				break;
			default:
		}
	}
	
	protected void initiateNextAction(int aNextAction) {
		switch(aNextAction) {
			case FmsSaveChangesDialog.next_action__FINISH:
				finish();
				return;
			default:
		}
	}

	private static void setAutomaticallySaveDataModifications(boolean aBoolean) {
		GcgApplication.getInstance().setAutomaticallySaveDataModifications(aBoolean);
	}

	public String getActiveViewGroupName() {
		return this.activeViewGroupName;
	}
	
	protected void setActiveViewGroupName(String aName) {
		this.activeViewGroupName = aName;
	}
	
	@SuppressWarnings("null")
	public String getActivityLabel() {
		if(this.activityLabel == null) {
			PackageManager thePackageManager = getPackageManager();
			ActivityInfo theActivityInfo = null;
			try {
				theActivityInfo = thePackageManager.getActivityInfo(getComponentName(), 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.activityLabel = theActivityInfo.loadLabel(thePackageManager).toString();
		}
		return this.activityLabel;
	}

	public Hashtable<String, FmmNodeTransactionType> getModifiedFmmNodeIdTable() {
		return this.modifiedFmmNodeIdList;
	}

	public void setModifiedFmmNodeIdTable(Hashtable<String, FmmNodeTransactionType> aLocallyModifiedFmmNodeIdTable) {
		this.modifiedFmmNodeIdList = aLocallyModifiedFmmNodeIdTable;
	}

	public Hashtable<String, FmmNodeTransactionType> getQueuedChildModifiedFmmNodeIdTable() {
		return this.queuedChildModifiedFmmNodeIdTable;
	}

	public void setQueuedChildModifiedFmmNodeIdTable(Hashtable<String, FmmNodeTransactionType> aChildModifiedFmmNodeIdTable) {
		this.queuedChildModifiedFmmNodeIdTable = aChildModifiedFmmNodeIdTable;
	}

	public GcgFrame getGcgFrame() {
		return this.frameSpinner == null ? null : this.frameSpinner.getFrame();
	}

	public String getGcgFrameName() {
		return this.getGcgFrame() == null ? "" : this.getGcgFrame().getName();
	}
	
	public void setGcgFrame(GcgFrame aGcgFrame) {
		this.frameSpinner.setFrame(aGcgFrame);
	}

	protected GcgPerspective getGcgPerspective() {
		return this.frameSpinner == null ? null : this.frameSpinner.getPerspective();
	}

	public String getGcgPerspectiveName() {
		return getGcgPerspective() == null ? "" : getGcgPerspective().getName();
	}

	protected void setGcgPerspective(GcgPerspective aGcgPerspective) {
		this.frameSpinner.setPerspective(aGcgPerspective);
	}
	
	public void startCreateFmmConfigurationWizard(FmmAccessScope anAccessScope) {
		FmsActivityHelper.startCreateFmmWizard(this, anAccessScope);
	}

	public String getOrganizationName() {
		return "";
	}

	public boolean isApplicationRootActivity() {
		return false;
	}
	
	protected GcgPerspectiveFlipper getGcgPerspectiveFlipper() {
		return null;
	}

	public GcgApplicationContext getChildGcgApplicationContext() {
		if(getGcgApplicationContext() == null) {
			return null;
		}
		GcgApplicationContext theChildContext = new GcgApplicationContext(getGcgApplicationContext().getJsonObject());
		theChildContext.getActivityBreadcrumbList().add(getGcgActivityBreadcrumb());
		return theChildContext;
	}

	public void updatePerspectiveContext(ArrayList<GcgGuiable> aContextList) {
		this.perspectiveContext = aContextList;
	}

	protected void saveGuiState() {
		if(this.frameSpinner != null) {
			this.frameSpinner.saveGuiState();
		}
	}

	protected void restoreGuiState() {
		if(this.frameSpinner != null) {
			this.frameSpinner.restoreGuiState();
		}
	}

	public void startDialog(FmsDialog anFmsDialog) {
		startDialog(anFmsDialog, activityCurtainEnabled());
	}

	public void startDialog(FmsDialog anFmsDialog, boolean bActivityCurtain) {
		enableActivityCurtain(bActivityCurtain);
		this.modalFmsDialogStack.push(anFmsDialog);
		this.modalFmsDialogStack.peek().processDialog();
	}

	public void replaceDialog(FmsDialog anFmsDialog) {
		this.modalFmsDialogStack.peek().dismiss();
		this.modalFmsDialogStack.pop();
		this.modalFmsDialogStack.push(anFmsDialog);
		this.modalFmsDialogStack.peek().processDialog();
	}

	public void stopDialog() {
		stopDialog(false);
	}

	public void stopDialog(boolean bRefreshPreviousDialogOrActivity) {
		this.modalFmsDialogStack.peek().dismiss();
		this.modalFmsDialogStack.pop();
		if(this.modalFmsDialogStack.size() == 0) {
			if(this.mustSelectDataSource) {
				finish();
			} else if(bRefreshPreviousDialogOrActivity) {
				buildContentViewForDataSource();
			}
			enableActivityCurtain(false);
		} else if(bRefreshPreviousDialogOrActivity) {
			refreshDialog();
		}
	}
	
	public void refreshDialog() {
		this.modalFmsDialogStack.peek().refreshDialog();
	}

	@Override
	public boolean fdkSpeechRecognitionSupported() {
		return this.fdkHostSupport.fdkSpeechRecognitionSupported();
	}

	@Override
	public FdkKeyboard getFdkKeyboard() {
		return this.fdkHostSupport.getFdkKeyboard();
	}

	@Override
	public void startDictation() {
		this.fdkHostSupport.startDictation();
	}

	@Override
	public void stopDictation() {
		this.fdkHostSupport.stopDictation();
	}

	@Override
	public void cancelDictation() {
		this.fdkHostSupport.cancelDictation();
	}
	
	@Override
	public void setFdkKeyboard(FdkKeyboard anFdkKeyboard) {
		this.fdkHostSupport.setFdkKeyboard(anFdkKeyboard);
	}

	@Override
	public void activateFdkKeyboard(boolean bActivate) {
		this.fdkHostSupport.activateFdkKeyboard(bActivate);
	}

	public FrameLayout getGcgThumbpadLeft() {
		this.fdkHostSupport.initGcgThumbpadLeft(getWindow());
		return this.fdkHostSupport.getGcgThumbpadLeft();
	}

	public ViewGroup getFdkKeypadPeerViewLeft() {
		this.fdkHostSupport.initFdkKeypadPeerViewLeft(getWindow());
		return this.fdkHostSupport.getFdkKeypadPeerViewLeft();
	}
	
	@Override
	public void toggleSoftKeyboardActive() {
		if(this.fdkHostSupport != null) {
			this.fdkHostSupport.toggleSoftKeyboardActive();
		}
	}
	
	public IBinder getWindowToken() {
		ViewGroup theContentView = (ViewGroup) this.findViewById(android.R.id.content);
		return theContentView.getWindowToken();
	}

	public ArrayList<FmmDataRefreshNotice> getDataRefreshList() {
		return this.dataRefreshList;
	}

	public void setDataRefreshList(ArrayList<FmmDataRefreshNotice> aDataRefreshList) {
		this.dataRefreshList = aDataRefreshList;
	}

	public boolean isDataRefreshAll() {
		return this.dataRefreshAll;
	}

	public void setDataRefreshAll(boolean dataRefreshAll) {
		this.dataRefreshAll = dataRefreshAll;
	}

	public ArrayList<FmmDataRefreshNotice> getParentDataRefreshList() {
		return this.parentDataRefreshList;
	}

	public void seParenttDataRefreshList(ArrayList<FmmDataRefreshNotice> aDataRefreshList) {
		this.parentDataRefreshList = aDataRefreshList;
	}

	public boolean isParentDataRefreshAll() {
		return this.parentDataRefreshAll;
	}

	public void setParentDataRefreshAll(boolean dataRefreshAll) {
		this.parentDataRefreshAll = dataRefreshAll;
	}
	
	public void updateParentDataRefreshList(FmmDataRefreshNotice aDataRefreshNotice) {
		if(this.parentDataRefreshList == null) {
			this.parentDataRefreshList = new ArrayList<FmmDataRefreshNotice>();
		}
		this.parentDataRefreshList.add(aDataRefreshNotice);
	}
	
	public GcgTreeViewAdapter getActiveGcgTreeViewAdapter() {
		return this.activeTreeViewAdapter;
	}
	
	public void onWidgetDataChangeListener(@SuppressWarnings("unused") int aResourceId) { return; }
	
}