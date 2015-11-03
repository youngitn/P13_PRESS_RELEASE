package YSH.OA.P13_PRESS_RELEASE;

import jcx.jform.hproc;
import jcx.db.*;

public class Init extends hproc {
	public String action(String value) throws Throwable {
		// 可自定HTML版本各欄位的預設值與按鈕的動作
		// 傳入值 value
		
		if (POSITION == 5) {

			setVisible("SEND", false);
			setVisible("QUERYPAGE", false);
			setVisible("ATTACHMENT",false);
			setVisible("PRESS_RELEASE_FILE",false);
			setVisible("PRESS_RELEASE_FILE2",false);
			talk t = getTalk();
			String EMPID = getValue("EMPID");
			String sql = "select HECNAME,DEPT_NO,DEP_NAME,CPNYID from USER_INFO_VIEW where EMPID = '"
					+ EMPID.trim() + "'";
			String[][] ret = t.queryFromPool(sql);
			String HECNAME = ret[0][0];
			String DEPT_NO = ret[0][1];
			String DEP_NAME = ret[0][2];
			String CPNYID = ret[0][3];
			setValue("EMPID_NAME", HECNAME);
			setValue("DEPT_NO_NAME", DEP_NAME);
			setValue("EMPID", EMPID);
			setValue("DEPT_NO", DEPT_NO);
			setValue("CPNYID", CPNYID);
			setValue("DATE", getToday("YYYYmmdd"));

			String FF = getValue("ATTACHMENT").trim();
			String RF1 = getValue("PRESS_RELEASE_FILE").trim();
			String RF2 = getValue("PRESS_RELEASE_FILE2").trim();
			String downloadString = "";
			if (FF.trim().length() != 0) {
				downloadString += "<a href=\"" + getDownloadURL(FF.trim())
						+ "\">附件下載</a><br>";
			}
			if (RF1.trim().length() != 0) {
				downloadString += "<a href=\"" + getDownloadURL(RF1.trim())
						+ "\">新聞稿夾檔下載</a><br>";
			}
			if (RF2.trim().length() != 0) {
				downloadString += "<a href=\"" + getDownloadURL(RF2.trim())
						+ "\">新聞稿夾檔2下載</a><br>";
			}
			setValue("DOWLOAD", downloadString);

		} else {
			
			
			/*
			 * talk t = getTalk(); String EMPID = getUser(); String sql =
			 * "select HECNAME,DEPT_NO,DEP_NAME,CPNYID from USER_INFO_VIEW where EMPID = '"
			 * +EMPID.trim()+"'"; String[][] ret = t.queryFromPool(sql); String
			 * HECNAME = ret[0][0]; String DEPT_NO = ret[0][1]; String DEP_NAME
			 * = ret[0][2]; String CPNYID = ret[0][3];
			 * setValue("EMPID_NAME",HECNAME);
			 * setValue("DEPT_NO_NAME",DEP_NAME); setValue("EMPID",EMPID);
			 * setValue("DEPT_NO",DEPT_NO); setValue("CPNYID",CPNYID);
			 * setValue("DATE",getToday("YYYYmmdd"));
			 */
		}
		
		return value;
	}

	public String getInformation() {
		return "---------------init().html_action()----------------";
	}

}
