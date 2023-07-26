/*
*  ProfileActivityGoalbtnButtonSheet
*  TMHeart
*
*  Created by Devanshu Shukla.
*  Copyright © 2018 Hackveda. All rights reserved.
*/

package hackveda.devanshu.tmheart.dialog;

import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import hackveda.devanshu.tmheart.R;


public class ProfileActivityGoalbtnButtonSheet extends BottomSheetDialogFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		return inflater.inflate(R.layout.profile_activity_goalbtn_button_sheet, container, false);
	}
}
