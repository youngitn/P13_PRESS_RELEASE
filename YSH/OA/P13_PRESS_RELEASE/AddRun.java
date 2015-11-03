package YSH.OA.P13_PRESS_RELEASE;

import java.sql.SQLException;
import java.util.ArrayList;

import jcx.db.talk;
import jcx.jform.hproc;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;

public class AddRun extends hproc {
	public String action(String value) throws Throwable {

		synchronized (this) {
			doAdd();
		}
		return value;
	}

	/**
	 * 新增申請表單
	 * 
	 * @throws Throwable
	 */
	private void doAdd() throws Throwable {
		if (!getValue("PNO").trim().isEmpty()) {
			message("該表單已存在,請重起新單！");
		} else if (getValue("REASON").trim().length() == 0) {
			message("請輸入原因！");
		} else {
			String pno = getPNO(getToday("YYYYmmdd"), "PRESS_RELEASE");

			setValue("PNO", pno);
			// String UPLOADS = getValue("ATTACHMENT").trim();
			ArrayList<String> UPLOADS = new ArrayList<String>();
			ArrayList<String> UPLOADS_rel = new ArrayList<String>();
			UPLOADS.add("ATTACHMENT");
			UPLOADS.add("PRESS_RELEASE_FILE");
			UPLOADS.add("PRESS_RELEASE_FILE2");
			String UPLOAD_SQL = "";
			String UPLOAD_FIELD_SQL = "";
			String c = ",";
			for (String UPLOAD : UPLOADS) {
				if (getValue(UPLOAD).trim().length() != 0) {
					UPLOADS_rel.add(UPLOAD);
				}
			}
			for (String UPLOAD : UPLOADS_rel) {

				File F1 = getUploadFile(UPLOAD);
				// if (UPLOADS.size() == UPLOADS.indexOf(UPLOAD)) {
				// c = "";
				// }
				if (F1 != null) {
					UPLOAD_FIELD_SQL += c + UPLOAD;
					UPLOAD_SQL += c + " '" + F1 + "' ";

				}

			}

			talk t = getTalk();
			String sql = "Insert into PRESS_RELEASE (PNO,CPNYID,DATE,EMPID,DEPT_NO,REASON"
					+ UPLOAD_FIELD_SQL
					+ ") VALUES ('"
					+ getValue("PNO")
					+ "','"
					+ getValue("CPNYID")
					+ "','"
					+ getValue("DATE")
					+ "','"
					+ getValue("EMPID")
					+ "','"
					+ getValue("DEPT_NO")
					+ "','" + getValue("REASON") + "'" + UPLOAD_SQL + ")";
			String now = getNow();
			String MUSER = getUser();
			String sc1 = "insert into PRESS_RELEASE_FLOWC (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"
					+ pno + "','待處理','" + MUSER + "','" + now + "','待處理')";
			String sc2 = "insert into PRESS_RELEASE_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"
					+ pno + "','待處理','" + MUSER + "','" + now + "','待處理')";
			// String
			// sc3="insert into E_SALARY_SIGN_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"+pno+"','單位主管','"+MUSER+"','"+now+"','')";

			t.execFromPool(sql);
			t.execFromPool(sc1);
			t.execFromPool(sc2);
			// t.execFromPool(sc3);

			t.close();
			setEditable("SEND", false);
			message("資料庫異動完成!");
		}
	}

	private String getPNO(String inToday, String inTableName)
			throws SQLException, Exception {
		int idx = 1;
		talk t = getTalk();
		String pno = inToday;
		String sql = "select max(pno) from " + inTableName
				+ " where pno like '" + inToday + "%'";

		String theMaxPNO[][];
		theMaxPNO = t.queryFromPool(sql);
		if (theMaxPNO[0][0] != null && !theMaxPNO[0][0].trim().equals("")) {
			pno = inToday
					+ StringUtils.leftPad(
							NumberUtils.toInt(theMaxPNO[0][0].substring(inToday
									.length())) + idx + "", 3, "0");
		} else {
			pno = inToday + StringUtils.leftPad(idx + "", 3, "0");
		}
		t.close();
		System.gc();
		return pno;
	}
}
