package YSH.OA.P13_PRESS_RELEASE;

import jcx.jform.hproc;
import jcx.db.*;

public class Init extends hproc {
	public String action(String value) throws Throwable {
		// �i�۩wHTML�����U��쪺�w�]�ȻP���s���ʧ@
		// �ǤJ�� value
		
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
						+ "\">����U��</a><br>";
			}
			if (RF1.trim().length() != 0) {
				downloadString += "<a href=\"" + getDownloadURL(RF1.trim())
						+ "\">�s�D�Z���ɤU��</a><br>";
			}
			if (RF2.trim().length() != 0) {
				downloadString += "<a href=\"" + getDownloadURL(RF2.trim())
						+ "\">�s�D�Z����2�U��</a><br>";
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
