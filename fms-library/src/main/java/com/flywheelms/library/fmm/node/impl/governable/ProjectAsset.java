/* @(#)ProjectAsset.java
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

package com.flywheelms.library.fmm.node.impl.governable;

import android.content.Intent;

import com.flywheelms.library.deckangl.enumerator.DecKanGlDecoratorCanvasLocation;
import com.flywheelms.library.deckangl.interfaces.DecKanGlDecorator;
import com.flywheelms.library.fmm.FmmDatabaseMediator;
import com.flywheelms.library.fmm.context.FmmPerspective;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorChildFractals;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorCompletion;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorFacilitationIssue;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorFacilitator;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorFlywheelCommitment;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorGovernance;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorParentFractals;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorSequence;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorStory;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorStrategicCommitment;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorWorkTaskBudget;
import com.flywheelms.library.fmm.deckangl.FmsDecoratorWorkTeam;
import com.flywheelms.library.fmm.meta_data.CompletableNodeMetaData;
import com.flywheelms.library.fmm.meta_data.ProjectAssetMetaData;
import com.flywheelms.library.fmm.meta_data.SequencedLinkNodeMetaData;
import com.flywheelms.library.fmm.node.FmmHeadlineNodeShallow;
import com.flywheelms.library.fmm.node.NodeId;
import com.flywheelms.library.fmm.node.impl.commitment.StrategicCommitment;
import com.flywheelms.library.fmm.node.impl.completable.FmmCompletableNodeImpl;
import com.flywheelms.library.fmm.node.impl.enumerator.FmmNodeDefinition;
import com.flywheelms.library.fmm.node.impl.headline.FmmHeadlineNodeImpl;
import com.flywheelms.library.fmm.node.interfaces.FmmSequencedNode;
import com.flywheelms.library.fmm.transaction.FmmNodeGlyphType;
import com.flywheelms.library.fms.helper.FmsActivityHelper;
import com.flywheelms.library.gcg.activity.GcgActivity;
import com.flywheelms.library.util.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ProjectAsset extends FmmCompletableNodeImpl implements Comparable<ProjectAsset> {
	
	private static final long serialVersionUID = -3645381005646011918L;
	private String projectNodeIdString;
	private Project project;
	private String flywheelTeamNodeIdString;
	private FlywheelTeam flywheelTeam;
	private ArrayList<WorkPackage> workPackageList;
	
	public ProjectAsset() {
		super(new NodeId(FmmNodeDefinition.PROJECT_ASSET.getNodeTypeCode()));
	}
	
	public ProjectAsset(JSONObject aJsonObject) {
		super(ProjectAsset.class, aJsonObject);
		try {
			setProjectNodeIdString(aJsonObject.getString(ProjectAssetMetaData.column_PROJECT_ID));
			setSequence(aJsonObject.getInt(CompletableNodeMetaData.column_SEQUENCE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ProjectAsset(NodeId aNodeId) {
		super(aNodeId);
	}
	
	public ProjectAsset(String aExistingNodeIdString) {
		super(NodeId.hydrate(
				ProjectAsset.class,
				aExistingNodeIdString ));
		// TODO Auto-generated constructor stub
	}

	public String getFlywheelTeamNodeIdString() {
		return this.flywheelTeamNodeIdString;
	}

	public void setFlywheelTeamNodeIdString(String aFlywheelTeamNodeIdString) {
		this.flywheelTeamNodeIdString = aFlywheelTeamNodeIdString;
		this.flywheelTeam = null;
	}

	public FlywheelTeam getFlywheelTeam() {
		if(this.flywheelTeamNodeIdString == null || this.flywheelTeamNodeIdString.equals("")) {
			return null;
		}
		if(this.flywheelTeam == null) {
			this.flywheelTeam = FmmDatabaseMediator.getActiveMediator().getFlywheelTeam(this.flywheelTeamNodeIdString);
		}
		return this.flywheelTeam;
	}

	public void setFlywheelTeam(FlywheelTeam aFlywheelTeam) {
		this.flywheelTeam = aFlywheelTeam;
		this.flywheelTeamNodeIdString = this.flywheelTeam.getNodeIdString();
	}

	public String getStrategicMilestoneNodeId() {
		return getStrategicCommitment() == null ? null :
			getStrategicCommitment().getStrategicMilestoneNodeId();
	}

	public StrategicCommitment getStrategicCommitment() {
		return FmmDatabaseMediator.getActiveMediator().getStrategicCommitmentForProjectAsset(getNodeIdString());
	}

	////////////////////////////////////////////////

	public Project getProject() {
		if(this.project == null) {
			this.project = FmmDatabaseMediator.getActiveMediator().getProject(getNodeIdString());
		}
		return this.project;
	}

	public void setProject(Project aProject) {
		this.project = aProject;
		if(this.project == null) {
			this.projectNodeIdString = "";
		} else {
			this.projectNodeIdString = this.project.getNodeIdString();
		}
	}
	
	public String getProjectNodeIdString() {
		return this.projectNodeIdString;
	}
	
	public void setProjectNodeIdString(String aNodeIdString) {
		this.projectNodeIdString = aNodeIdString;
		if(this.project == null || this.projectNodeIdString == null || this.projectNodeIdString.equals("") || this.project.getNodeIdString().equals(this.projectNodeIdString)) {
			return;
		}
		this.project = null;
	}

	@Override
	protected void initializeNodeCompletionSummaryMap() {
		super.initializeNodeCompletionSummaryMap();
		NodeCompletionSummary theNodeCompletionSummary = new NodeCompletionSummary();
		theNodeCompletionSummary.setSummaryDrawableResourceId(
                FmmNodeDefinition.WORK_PACKAGE.getUndecoratedGlyphResourceId(FmmNodeGlyphType.GREEN));
		updateNodeCompletionSummary(FmmPerspective.STRATEGIC_PLANNING, theNodeCompletionSummary);
		this.nodeCompletionSummaryMap.put(FmmPerspective.STRATEGIC_PLANNING, theNodeCompletionSummary);
		this.nodeCompletionSummaryMap.put(FmmPerspective.WORK_BREAKDOWN, theNodeCompletionSummary);
	}

	@Override
	public void updateNodeCompletionSummary(FmmPerspective anFmmPerspective, NodeCompletionSummary aNodeSummary) {
		switch(anFmmPerspective) {
            case STRATEGIC_PLANNING:
            case WORK_BREAKDOWN:
                Collection<WorkPackage> theWorkPackageCollection = getWorkPackageCollection();
                if(theWorkPackageCollection.size() > 0) {
                    aNodeSummary.setShowNodeSummary(true);
                    aNodeSummary.setSummaryPrefix("( " + countGreenWorkPackages() + " ");
                    aNodeSummary.setSummarySuffix(" of " + theWorkPackageCollection.size() + " )");
                } else {
                    aNodeSummary.setShowNodeSummary(false);
                }
                break;
            default:
                break;
		}
	}

	@Override
	public int compareTo(ProjectAsset anOtherProjectAsset) {
		return (getSequence() < anOtherProjectAsset.getSequence() ? -1 :
			(getSequence() == anOtherProjectAsset.getSequence() ? 0 : 1));
	}
	
	public static final String SERIALIZATION_FORMAT_VERSION = "0.1";
	
	@Override
	public JSONObject getJsonObject() {
		JSONObject theJsonObject = super.getJsonObject();
		try {
			theJsonObject.put(JsonHelper.key__SERIALIZATION_FORMAT_VERSION, SERIALIZATION_FORMAT_VERSION);
			theJsonObject.put(ProjectAssetMetaData.column_PROJECT_ID, getProjectNodeIdString());
			theJsonObject.put(SequencedLinkNodeMetaData.column_SEQUENCE, getSequence());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return theJsonObject;
	}
	
	//////////////////

	public JSONArray getWorkPackageJsonArray() {
		JSONArray theJsonArray = new JSONArray();
		for(WorkPackage theWorkPackage : getWorkPackageList()) {
			theJsonArray.put(theWorkPackage.getNodeIdString());
		}
		return theJsonArray;
	}

	public void setWorkPackageList(JSONArray aJsonArray) {
		this.workPackageList = new ArrayList<WorkPackage>();
		for(int i=0; i < aJsonArray.length(); ++i) {
			try {
				this.workPackageList.add(FmmDatabaseMediator.getActiveMediator().getWorkPackage(
						aJsonArray.getString(i) ));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Collection<WorkPackage> getWorkPackageCollection() {
		return getWorkPackageList();
	}

	public ArrayList<WorkPackage> getWorkPackageList() {
		if(this.workPackageList == null) {
			this.workPackageList = FmmDatabaseMediator.getActiveMediator().listWorkPackageForProjectAsset(getNodeIdString());
		}
		return this.workPackageList;
	}

	private int countGreenWorkPackages() {
		int theGreenCount = 0;
		for(WorkPackage theWorkPackage : getWorkPackageCollection()) {
			if(theWorkPackage.isGreen()) {
				++theGreenCount;
			}
		}
		return theGreenCount;
	}
	
	/////////////////////////////////////////////////
	//////  TEMPORARY for TESTING  //////////////////////////
	@Override
	public HashMap<DecKanGlDecoratorCanvasLocation, DecKanGlDecorator> getUpdatedDecKanGlDecoratorMap() {
		HashMap<DecKanGlDecoratorCanvasLocation, DecKanGlDecorator> theDecKanGlDecoratorMap =
				new HashMap<DecKanGlDecoratorCanvasLocation, DecKanGlDecorator>();
		theDecKanGlDecoratorMap.put(
				FmsDecoratorGovernance.PROPOSED_GOVERNANCE.getDecoratorCanvasLocation(),
				FmsDecoratorGovernance.PROPOSED_GOVERNANCE );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorFacilitationIssue.NO_FACILITATION_ISSUE.getDecoratorCanvasLocation(),
				FmsDecoratorFacilitationIssue.NO_FACILITATION_ISSUE );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorFacilitator.SUGGESTED_FACILITATOR.getDecoratorCanvasLocation(),
				FmsDecoratorFacilitator.SUGGESTED_FACILITATOR );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorParentFractals.ONE_CONFIRMED_PARENT_FRACTAL.getDecoratorCanvasLocation(),
				FmsDecoratorParentFractals.ONE_CONFIRMED_PARENT_FRACTAL );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorChildFractals.SUGGESTED_CHILD_FRACTALS.getDecoratorCanvasLocation(),
				FmsDecoratorChildFractals.SUGGESTED_CHILD_FRACTALS );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorStrategicCommitment.PROPOSED_STRATEGIC_COMMITMENT.getDecoratorCanvasLocation(),
				FmsDecoratorStrategicCommitment.PROPOSED_STRATEGIC_COMMITMENT );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorFlywheelCommitment.SUGGESTED_FLYWHEEL_COMMITMENT.getDecoratorCanvasLocation(),
				FmsDecoratorFlywheelCommitment.SUGGESTED_FLYWHEEL_COMMITMENT );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorWorkTaskBudget.SUGGESTED_TASK_BUDGET.getDecoratorCanvasLocation(),
				FmsDecoratorWorkTaskBudget.SUGGESTED_TASK_BUDGET );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorWorkTeam.SUGGESTED_TEAM.getDecoratorCanvasLocation(),
				FmsDecoratorWorkTeam.SUGGESTED_TEAM );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorStory.STORY_SWAG.getDecoratorCanvasLocation(),
				FmsDecoratorStory.STORY_SWAG );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorSequence.SEQUENCE_SWAG.getDecoratorCanvasLocation(),
				FmsDecoratorSequence.SEQUENCE_SWAG );
		theDecKanGlDecoratorMap.put(
				FmsDecoratorCompletion.COMPLETION_NOT_SCHEDULED.getDecoratorCanvasLocation(),
				FmsDecoratorCompletion.COMPLETION_NOT_SCHEDULED );
		return theDecKanGlDecoratorMap;
	}
	
	public static void startNodeEditorActivity(GcgActivity anActivity, String aNodeListParentNodeId, ArrayList<FmmHeadlineNodeShallow> aHeadlineNodeShallowList, String anInitialNodeIdToDisplay) {
		FmmHeadlineNodeImpl.startNodeEditorActivity(
				anActivity,
				aNodeListParentNodeId,
				aHeadlineNodeShallowList,
				anInitialNodeIdToDisplay,
				FmmNodeDefinition.PROJECT_ASSET );
	}
	
	public static void startNodePickerActivity(GcgActivity anActivity, ArrayList<String> aNodeIdExclusionList, String aWhereClause, String aListActionLabel) {
		FmsActivityHelper.startHeadlineNodePickerActivity(anActivity, FmmNodeDefinition.PROJECT_ASSET, aNodeIdExclusionList, aWhereClause, aListActionLabel);
	}
	
	public static ProjectAsset getFmmConfiguration(Intent anIntent) {
		return FmmDatabaseMediator.getActiveMediator().getProjectAsset(NodeId.getNodeIdString(anIntent));
	}

	@Override
	public int getSequence(FmmNodeDefinition anFmmNodeDefinition) {
		FmmSequencedNode theSequencedNode = anFmmNodeDefinition == FmmNodeDefinition.STRATEGIC_MILESTONE ?
				FmmDatabaseMediator.getActiveMediator().getStrategicCommitmentForProjectAsset(getNodeIdString()) :
				this;  // within Project
		return theSequencedNode.getSequence();
	}

	public boolean hasMoveTargetWorkPackages(WorkPackage aWorkPackageException) {
		return FmmDatabaseMediator.getActiveMediator().getMoveTargetWorkPackageCount(this, aWorkPackageException) > 0;
	}

	public boolean isWorkPackageMoveTarget() {
		return true;
	}

}
