package YSH.OA.P13_PRESS_RELEASE;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import jcx.db.talk;
import jcx.jform.hproc;
import jcx.util.convert;
import hr.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.ysp.service.BaseService;
import com.ysp.service.MailService;


public class AddRun extends hproc {
	

	public String action(String value) throws Throwable {

		synchronized (this) {
			doAdd();
		}
		return value;
	}

	/**
	 * �s�W�ӽЪ��
	 * 
	 * @throws Throwable
	 */
	private void doAdd() throws Throwable {
		if (!getValue("PNO").trim().isEmpty()) {
			message("�Ӫ��w�s�b,�Э��_�s��I");
		} else if (getValue("REASON").trim().length() == 0) {
			message("�п�J��]�I");
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

			// String boss_lv1[][] =
			// t.queryFromPool("SELECT DEP_CHIEF FROM USER_INOFFICE_INFO_VIEW WHERE EMPID = '"+MUSER+"'");
			// String GeneralManager[][] =
			// t.queryFromPool("SELECT DEP_CHIEF FROM HRUSER_DEPT_BAS WHERE DEP_NO = '423'");
			String SIGN_LV = "���ݥD��";
			// if (boss_lv1[0][0].equals(GeneralManager[0][0])){
			// SIGN_LV = "�`�g�z";
			// }

			String sc1 = "insert into PRESS_RELEASE_FLOWC (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"
					+ pno
					+ "','"
					+ SIGN_LV
					+ "','"
					+ MUSER
					+ "','"
					+ now
					+ "','" + SIGN_LV + "')";
			String sc2 = "insert into PRESS_RELEASE_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"
					+ pno + "','�ݳB�z','" + MUSER + "','" + now + "','�ݳB�z')";
			String sc3 = "insert into PRESS_RELEASE_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"
					+ pno + "','���ݥD��','" + MUSER + "','" + now + "','')";
			// String
			// sc3="insert into E_SALARY_SIGN_FLOWC_HIS (PNO,F_INP_STAT,F_INP_ID,F_INP_TIME,F_INP_INFO) values ('"+pno+"','���D��','"+MUSER+"','"+now+"','')";

			t.execFromPool(sql);
			t.execFromPool(sc1);
			t.execFromPool(sc2);
			t.execFromPool(sc3);
			sendMail(t, getValue("EMPID"));
			t.close();
			setEditable("SEND", false);

			// message("��Ʈw���ʧ���!");
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

	public void sendMail(talk t, String empid) throws Throwable {
		
		String sqlc = "SELECT HRADDR FROM HRSYS";
		String[][] HRADDR = t.queryFromPool(sqlc);
		
		String reEmpId = getBOS(t, empid);
		String[] usr = { getEmail(reEmpId) };
		String title1 = "";
		String name = getName(empid);
		String title = "�D���G(" + empid + ")" + name + "���u���s�D�Z�o�G�ӽг�A�жi�J�t��ñ��"
				+ title1.trim();
		String content = "�жi�J eHR �t��ñ��( <a href=\"" + HRADDR[0][0].trim() + "\">�����s��</a>)<br>";
		content += "=========���e�K�n=========<br>";
		content += "�渹:" +  getValue("PNO") + "<br>";
		content += "�ӽФ��:" +  getValue("DATE") + "<br>";
		BaseService service = new BaseService();
		MailService mail = new MailService(service);
		String sendRS = mail.sendMailbccUTF8(usr, title, content, null, "","text/html");
		message("�w�q��ñ�֪�!");
		

	}

	public String getBOS(talk t, String EMPID) throws Throwable {
		int level = 3;
		String MASTER[][] = t.queryFromPool(
				"select MASTERID from HRUSER where EMPID='"
						+ convert.ToSql(EMPID.trim()) + "'", 30);
		if (MASTER.length != 0) {
			if (!MASTER[0][0].trim().equals("")) {
				return MASTER[0][0].trim();
			}
		}
		Vector v = null;
		v = common.getBosses(t, EMPID.trim(), new Vector(), level);
		for (int i = 0; i < v.size(); i++) {
			String id1 = v.elementAt(i).toString().trim();
			if (id1.trim().equals(""))
				continue;
			if (id1.trim().equals(EMPID.trim()))
				continue;
			return id1.trim();
		}
		/*
		 * String BOSS=common.getBoss(EMPID.trim(),1);
		 * if(BOSS.trim().length()!=0) return BOSS.trim();
		 */
		return "admin";
	}

}
