/* @(#)FmmAccessScopeWidgetSpinner.java
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
import com.flywheelms.gcongui.gcg.widget.GcgWidgetSpinner;
import com.flywheelms.library.fmm.repository.FmmAccessScope;

import java.util.ArrayList;

public class FmmAccessScopeWidgetSpinner extends GcgWidgetSpinner {

	public FmmAccessScopeWidgetSpinner(Context aContext, AttributeSet anAttributeSet) {
		super(aContext, anAttributeSet);
	}

	@Override
	protected ArrayList<GcgGuiable> updateGuiableList() {
		return FmmAccessScope.getGuiableList();
	}

	@Override
	protected String getLabelText() {
		return this.labelTextString == null ? FmmAccessScope.SHARED.getLabelText() : this.labelTextString;
	}
	
	public FmmAccessScope getFmmAccessScope() {
		return (FmmAccessScope) getSelectedItem();
	}

}
