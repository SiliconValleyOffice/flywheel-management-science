/* @(#)StrategicCommitment.java
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

package com.flywheelms.library.fmm.node.impl.commitment;

import com.flywheelms.library.fmm.node.NodeId;
import com.flywheelms.library.fmm.node.impl.governable.ProjectAsset;
import com.flywheelms.library.fmm.node.impl.governable.StrategicAsset;
import com.flywheelms.library.fmm.node.impl.governable.StrategicMilestone;
import com.flywheelms.library.fmm.node.interfaces.horizontal.FmmNode;
import com.flywheelms.library.fms.activity.FmsActivity;

import java.util.Date;

public class StrategicCommitment extends FmmCommitmentNodeImpl implements Comparable<StrategicCommitment> {
	
	private StrategicMilestone strategicMilestone;
	private StrategicAsset strategicAsset;

    public StrategicCommitment(String anExistingNodeIdString, String aStrategicMilestoneId, String aProjectAssetId) {
        super(NodeId.hydrate(
                StrategicCommitment.class,
                anExistingNodeIdString),
                aStrategicMilestoneId,
                aProjectAssetId );
    }

	public StrategicCommitment(StrategicMilestone aStrategicMilestone, StrategicAsset aStrategicAsset) {
		super(StrategicCommitment.class, aStrategicMilestone.getNodeIdString(), aStrategicAsset.getNodeIdString());
		setStrategicMilestone(aStrategicMilestone);
		setStrategicAsset(aStrategicAsset);
	}

	public String getStrategicMilestoneNodeId() {
		return this.parentNodeIdString;
	}
	
	public StrategicMilestone getStrategicMilestone() {
		if(this.strategicMilestone == null && this.parentNodeIdString != null) {
			this.strategicMilestone =
					FmsActivity.getActiveDatabaseMediator().retrieveStrategicMilestone(this.parentNodeIdString);
		}
		return this.strategicMilestone;
	}

	public void setStrategicMilestoneNodeId(String aStrategicMilestoneNodeId) {
		this.parentNodeIdString = aStrategicMilestoneNodeId;
		this.strategicMilestone = null;
	}

	public void setStrategicMilestone(StrategicMilestone aStrategicMilestone) {
		this.strategicMilestone = aStrategicMilestone;
		this.parentNodeIdString = aStrategicMilestone.getNodeIdString();
	}

	public String getProjectAssetNodeIdString() {
		return this.childNodeIdString;
	}
	
	public StrategicAsset getStrategicAsset() {
		if(this.strategicAsset == null) {
			this.strategicAsset =
					FmsActivity.getActiveDatabaseMediator().retrieveStrategicAsset(this.childNodeIdString);
		}
		return this.strategicAsset;
	}

	public void setProjectAssetNodeId(String aProjectAssetNodeId) {
		this.childNodeIdString = aProjectAssetNodeId;
		this.strategicAsset = null;
	}

	public void setStrategicAsset(StrategicAsset aStrategicAsset) {
		this.strategicAsset = aStrategicAsset;
		this.childNodeIdString = aStrategicAsset.getNodeIdString();
	}

	@Override
	public int compareTo(StrategicCommitment anOtherStrategicCommitment) {
		return (getSequence() < anOtherStrategicCommitment.getSequence() ? -1 :
			(getSequence() == anOtherStrategicCommitment.getSequence() ? 0 : 1));
	}

	@Override
	public String getLinkedByNodeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLinkedDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends FmmNode> getParentClass() {
		return StrategicMilestone.class;
	}

	@Override
	public Class<? extends FmmNode> getChildClass() {
		return ProjectAsset.class;
	}
}
