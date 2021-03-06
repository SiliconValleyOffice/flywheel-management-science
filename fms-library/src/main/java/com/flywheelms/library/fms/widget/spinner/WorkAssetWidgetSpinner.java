/* @(#)ProjectAssetWidgetSpinner.java
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

package com.flywheelms.library.fms.widget.spinner;

import android.content.Context;
import android.util.AttributeSet;

import com.flywheelms.gcongui.gcg.interfaces.GcgGuiable;
import com.flywheelms.library.fmm.node.impl.enumerator.FmmNodeDefinition;
import com.flywheelms.library.fmm.node.impl.governable.Project;
import com.flywheelms.library.fmm.node.impl.governable.ProjectAsset;
import com.flywheelms.library.fmm.node.impl.governable.StrategicMilestone;
import com.flywheelms.library.fmm.node.impl.governable.WorkAsset;
import com.flywheelms.library.fmm.node.impl.governable.WorkPackage;
import com.flywheelms.library.fms.activity.FmsActivity;
import com.flywheelms.library.fms.widget.FmmHeadlineNodeWidgetSpinner;

import java.util.ArrayList;

// TODO - Need a WorkAssetWidgetSpinner, which includes StrategicAssets and ProjectAssets
public class WorkAssetWidgetSpinner extends FmmHeadlineNodeWidgetSpinner {

	private Project project; // primary parent
	private StrategicMilestone strategicMilestone; // secondary parent
	private WorkPackage workPackageException;  // TODO - primary child that should be ignored
	private WorkAsset workAssetException;  // a peer that should be ignored

	public WorkAssetWidgetSpinner(Context aContext) {
		super(aContext);
	}

    public WorkAssetWidgetSpinner(Context aContext, AttributeSet anAttributeSet) {
        super(aContext, anAttributeSet);
    }

	@Override
	protected String getLabelText() {
		return FmmNodeDefinition.WORK_ASSET.getLabelText();
	}
	
	@Override
	protected ArrayList<GcgGuiable> getPrimaryParentGuiableList() {
		ArrayList<GcgGuiable> theGuiableList;
		if(this.project == null) {
			theGuiableList = new ArrayList<GcgGuiable>(); 
		} else {	
			theGuiableList = new ArrayList<GcgGuiable>(
					FmsActivity.getActiveDatabaseMediator().retrieveWorkAssetList(this.project) );
		}
		return theGuiableList;
	}
	
	@Override
	protected ArrayList<GcgGuiable> getPrimaryParentPrimaryChildMoveTargetGuiableList() {
		ArrayList<GcgGuiable> theGuiableList;
		if(this.project == null) {
			theGuiableList = new ArrayList<GcgGuiable>(); 
		} else {	
			theGuiableList = new ArrayList<GcgGuiable>(FmsActivity.getActiveDatabaseMediator().retrieveWorkAssetListForProject(
                    this.project.getNodeIdString(), this.workAssetException.getNodeIdString()));
		}
		return theGuiableList;
	}

    @Override
    protected ArrayList<? extends GcgGuiable> getOrphanNodesPrimaryParentGuiableList() {
        ArrayList<GcgGuiable> theGuiableList =
                new ArrayList<GcgGuiable>(FmsActivity.getActiveDatabaseMediator().listWorkAssetOrphansFromProject());
        return theGuiableList;
    }

    @Override
    protected ArrayList<? extends GcgGuiable> getOrphanNodesSecondaryParentGuiableList() {
        ArrayList<GcgGuiable> theGuiableList =
                new ArrayList<GcgGuiable>(FmsActivity.getActiveDatabaseMediator().listWorkAssetOrphansFromStrategicMilestone());
        return theGuiableList;
    }

    protected ArrayList<? extends GcgGuiable> getDefaultGuiableList() {
        return new ArrayList<GcgGuiable>(FmsActivity.getActiveDatabaseMediator().retrieveProjectAssetList());
    }
	
	public void updateSpinnerData(Project aProject) {
		this.project = aProject;
		super.updateSpinnerData();
	}

	public void updateSpinnerData(StrategicMilestone aStrategicMilestone) {
		this.strategicMilestone = aStrategicMilestone;
		super.updateSpinnerData();
	}

	public void updateSpinnerData(WorkPackage aWorkPackageException) {
		this.workPackageException = aWorkPackageException;
		super.updateSpinnerData();
	}

	public void updateSpinnerData(Project aProject, WorkPackage aWorkPackageException) {
		this.project = aProject;
		this.workPackageException = aWorkPackageException;
		super.updateSpinnerData();
	}

	public void updateSpinnerData(Project aProject, WorkAsset aWorkAssetException) {
		this.project = aProject;
		this.workAssetException = aWorkAssetException;
		super.updateSpinnerData();
	}

	public void updateSpinnerData(StrategicMilestone aStrategicMilestone, ProjectAsset aProjectAssetException) {
		this.strategicMilestone = aStrategicMilestone;
		this.workAssetException = aProjectAssetException;
		super.updateSpinnerData();
	}
	
}
