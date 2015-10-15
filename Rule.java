import java.util.Vector;

import com.ysp.service.BaseService;

import com.ysp1.conf.DeptConfig;
import com.ysp1.conf.FlowConfig;
import com.ysp1.oa.P004.CommonValue;
import com.yungshingroup.hr.Org;
import com.yungshingroup.utils.Cache;

import jcx.jform.bBase;
import jcx.jform.bRule;


public class Rule extends bRule implements CommonValue{
	public Vector getIDs(String value) throws Throwable {
		Vector theIDs = new Vector();
		String theNowState = getState();
		String theEMPID = getData(Field_REQ_EMP_ID).trim();//REQ_EMP_ID 申請人ID
		String theParnr = null;
		BaseService mBaseService = new BaseService(this);
		Org org = Cache.org(bBase.cache);
		//如果簽核狀態為-待處理
		if(FlowConfig.FLOW_POINT_PENDING.equals(theNowState)){
			theIDs.add(theEMPID);
		}
		//簽核狀態-課主管
		else if(FlowConfig.FLOW_POINT_LESSION_CHIEF.equals(theNowState)){
			//theParnr = mBaseService.getBossBySignLevel(theEMPID, DeptConfig.DEP_TYPE_11);
			theParnr = org.getDeptTypeManager_OK(theEMPID, String.valueOf(DeptConfig.DEP_TYPE_11));
			theIDs.add(theParnr);
		}
		//簽核狀態-處主管
		else if(FlowConfig.FLOW_POINT_DIVISION_CHIEF.equals(theNowState)){
			//theParnr = mBaseService.getBossBySignLevel(theEMPID, DeptConfig.DEP_TYPE_10);
			theParnr = org.getDeptTypeManager_OK(theEMPID, String.valueOf(DeptConfig.DEP_TYPE_10));
			theIDs.add(theParnr);
		}
		//簽核狀態-會計經辦
		else if(FlowConfig.FLOW_POINT_ACCOUNT_ASSISTANT.equals(theNowState)){
			theParnr = getData(Field_ASSISTANT).trim();
			theIDs.add(theParnr);
		}
		
		return theIDs;
	}
}
