import jcx.jform.bNotify;

import java.util.*;

import com.ysp.service.BaseService;
import com.ysp.service.MailService;


import jcx.util.*;

import jcx.db.*;

public class Mail2 extends bNotify {

	BaseService service = null;
	MailService mail = null;

	//Log log = LogUtil.getLog(this.getClass());

	public void actionPerformed(String value) throws Throwable {
		// ����i�J�y�{���Añ�֥D�ޤ@��,�|���楻�q�{��
		// �i�ΥH�H�oEmail�q���άO�۰ʦA�B�z�۩wTransaction
		service = new BaseService(this);
		mail = new MailService(service);
		//log.debug("value=" + value);
		talk t = getTalk();
		String[][] r1 = null;
		String sql = "";
		String state = value.trim();

		// �զ��һݪ� SQL
		boolean isFinal = false;
		if (state.equals("�ݳB�z")) {
			String EMPID = getValue("EMPID").trim();
			sql = "select EMPID , PASS , EMAIL from HRUSER where EMPID = '" + convert.ToSql(EMPID) + "' ";
		} else if (state.equals("�D��ñ��") || state.equals("¾�ȥN�z�H")) {
			String EMPID = "";
			if (getEngagedPeople().size() != 0)
				EMPID = getEngagedPeople().elementAt(0).toString().trim();
			sql = "select EMPID , PASS , EMAIL from HRUSER where EMPID = '" + convert.ToSql(EMPID) + "' ";
		} else if (state.startsWith("ñ�֥D��")) {
			String SIGN = "";
			if (state.endsWith("�@")) {
				SIGN = getValue("SIGN11").trim();
				if (getValue("SIGN12").trim().length() <= 0 && getValue("SIGN13").trim().length() <= 0
						&& getValue("SIGN14").trim().length() <= 0 && getValue("SIGN15").trim().length() <= 0
						&& getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("�G")) {
				SIGN = getValue("SIGN12").trim();
				if (getValue("SIGN13").trim().length() <= 0 && getValue("SIGN14").trim().length() <= 0
						&& getValue("SIGN15").trim().length() <= 0 && getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("�T")) {
				SIGN = getValue("SIGN13").trim();
				if (getValue("SIGN14").trim().length() <= 0 && getValue("SIGN15").trim().length() <= 0
						&& getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("�|")) {
				SIGN = getValue("SIGN14").trim();
				if (getValue("SIGN15").trim().length() <= 0 && getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("��")) {
				SIGN = getValue("SIGN15").trim();
				if (getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("��")) {
				SIGN = getValue("SIGN16").trim();
				isFinal = true;
			}
			sql = "select EMPID , PASS , EMAIL from HRUSER "
					+ "where EMPID in (select ID from HRUSER_DEPT where DEP_NO = " + SIGN + ") ";
		} else if ("END".equals(state)) {
			//isFinal = true;
		}

		// ���� SQL
		if (sql.length() > 0)
			r1 = t.queryFromPool(sql);
		else
			r1 = new String[0][0];

		// ���o�o�e mail �һݪ���T
		String[][] r2 = null;

		// ���o�t�γs��
		String HRADDR = "", fun_id = "";
		sql = "select HRADDR from HRSYS ";
		r2 = t.queryFromPool(sql);
		if (r2.length != 0 && r2[0][0] != null && r2[0][0].trim().length() != 0) {
			if (r2[0][0].trim().toUpperCase().startsWith("HTTP"))
				HRADDR = r2[0][0].trim() + fun_id;
			else
				HRADDR = "http://" + r2[0][0].trim() + fun_id;
		}

		// �P�_�O�_�� �Ʒ~�B�D�� (1105) , �~�ȳB�D�� (1106)
		boolean run = false;
		String EMPID = getValue("EMPID").trim();
		sql = "select count(*) from HRUSER_DEPT " + "where DEP_NO in ('1105','1106') " + "and ID = '"
				+ convert.ToSql(EMPID) + "' ";
		r2 = t.queryFromPool(sql);
		if (r2.length != 0 && r2[0][0] != null && r2[0][0].trim().length() != 0 && !r2[0][0].trim().equals("0"))
			run = true;

		// ���o���e�һݸ��
		String PNO = getValue("PNO").trim();
		sql = "select a.PNO , a.EMPID , b.HECNAME , c.DEP_NAME , a.SDATE , a.EDATE , a.AMTDAY , a.TRAPLACE "
				+ "from TRAVEL a , HRUSER b , HRUSER_DEPT_BAS c " + "where a.EMPID = b.EMPID "
				+ "and b.DEPT_NO = c.DEP_NO " + "and a.PNO = '" + convert.ToSql(PNO) + "' ";
		r2 = t.queryFromPool(sql);

		// �]�w�D���Τ��e
		String subject = "", content = "", fun_name = "���u���X�t�ӽг�";
		if (r2.length != 0 && r2[0][1] != null)
			subject += "(" + r2[0][1].trim() + ") ";
		if (r2.length != 0 && r2[0][2] != null)
			subject += r2[0][2].trim() + " ";
		subject += fun_name + "�A�жi�J�t��ñ��" + HRADDR.trim();

		// �o�e mail
		for (int i = 0; i < r1.length; i++) {
			content = "";
			if (r2.length != 0 && r2[0][0] != null)
				content += "�X�t�渹�G" + r2[0][0].trim() + " \r\n";
			if (r2.length != 0 && r2[0][1] != null)
				content += "�u���G" + r2[0][1].trim() + " \r\n";
			if (r2.length != 0 && r2[0][2] != null)
				content += "�m�W�G" + r2[0][2].trim() + " \r\n";
			if (r2.length != 0 && r2[0][3] != null)
				content += "���G" + r2[0][3].trim() + " \r\n";
			if (r2.length != 0 && r2[0][4] != null)
				content += "�_�l����G" + r2[0][4].trim() + " \r\n";
			if (r2.length != 0 && r2[0][5] != null)
				content += "��������G" + r2[0][5].trim() + " \r\n";
			if (r2.length != 0 && r2[0][6] != null)
				content += "�`�p�ѼơG" + r2[0][6].trim() + " \r\n";
			content += "�a�I�G" + getData("TRAPLACE") + " \r\n";
			content += "�ƥѡG" + getData("DESC1") + " \r\n";

			if (run) {
				content = "";
				if (r2.length != 0 && r2[0][0] != null)
					content += "�X�t�渹�G" + r2[0][0].trim() + " \r\n";
				if (r2.length != 0 && r2[0][1] != null)
					content += "�u���G" + r2[0][1].trim() + " \r\n";
				if (r2.length != 0 && r2[0][2] != null)
					content += "�m�W�G" + r2[0][2].trim() + " \r\n";
				if (r2.length != 0 && r2[0][7] != null)
					content += "�X�t�a�I�G" + r2[0][7].trim() + " \r\n";
				if (r2.length != 0 && r2[0][4] != null)
					content += "�_�l����G" + r2[0][4].trim() + " \r\n";
				if (r2.length != 0 && r2[0][5] != null)
					content += "��������G" + r2[0][5].trim() + " \r\n";
				content += "�a�I�G" + getData("TRAPLACE") + " \r\n";
				content += "�ƥѡG" + getData("DESC1") + " \r\n";
			}
			content += "�t�κ��}�G";
			if (HRADDR.length() != 0)
				content += "( " + HRADDR;
			// if (r1[i][0].trim().length() != 0 && r1[i][1].trim().length() != 0) content +=
			// "&pwd={"+r1[i][1].trim()+"}"+"&uid="+r1[i][0].trim();
			if (HRADDR.length() != 0)
				content += " )";
			content += "ñ��";
			
			mail.sendMailbccUTF8(new String[] { r1[i][2].trim() }, subject, content, null, "", "text/plain");

		}
		//log.debug("isFinal=" + isFinal);
		if (isFinal) {
			String empid = r2[0][1].trim();
			String name = r2[0][2].trim();
			String SDATE = convert.FormatedDate(getValue("SDATE").trim(), "/");
			subject = empid + "�@" + name + "�@" + SDATE + "�ӽХX�t�@�w�g�D�ޮ֭�C";
			content = "";
			if (r2.length != 0 && r2[0][0] != null)
				content += "�X�t�渹�G" + r2[0][0].trim() + " \r\n";
			if (r2.length != 0 && r2[0][1] != null)
				content += "�u���G" + r2[0][1].trim() + " \r\n";
			if (r2.length != 0 && r2[0][2] != null)
				content += "�m�W�G" + r2[0][2].trim() + " \r\n";
			if (r2.length != 0 && r2[0][3] != null)
				content += "���G" + r2[0][3].trim() + " \r\n";
			if (r2.length != 0 && r2[0][4] != null)
				content += "�_�l����G" + r2[0][4].trim() + " \r\n";
			if (r2.length != 0 && r2[0][5] != null)
				content += "��������G" + r2[0][5].trim() + " \r\n";
			if (r2.length != 0 && r2[0][6] != null)
				content += "�`�p�ѼơG" + r2[0][6].trim() + " \r\n";
			content += "�a�I�G" + getData("TRAPLACE") + " \r\n";
			content += "�ƥѡG" + getData("DESC1") + " \r\n";
			// 2011.11.09 add �ʺޤH��////////////////////////////////////////////////
			String notifier = "";
			// PLACE.PLACE IN ('F') ->49425
			String rrr[][] = t.queryFromPool("SELECT PLACE from HRUSER WHERE EMPID = '" + empid
					+ "' and CPNYID ='YT01' ");
			if (rrr.length > 0) {
				if (rrr[0][0].equals("F")) {
					notifier = "49425"; // �¼z��
				} else if (rrr[0][0].equals("E") || rrr[0][0].equals("V") || rrr[0][0].equals("W")) {
					notifier = "12629"; // �d�q��
				}
			}
			// //////////////////////////////////////////////

			Vector vc = new Vector();
			vc.addElement(getEmail(empid));
			if (notifier.length() > 0) {
				vc.addElement(getEmail(notifier));
			}
			String[] em2 = (String[]) vc.toArray(new String[0]);

			// sendMailbcc(host , sender , new String [] {getEmail(empid)} , subject , content , null , "" ,
			// "text/plain");
			String sendRS = mail.sendMailbccUTF8(em2, subject, content, null, "", "text/plain");

		}
		return;
	}

	public String getInformation() {
		return "---------------\u7c3d\u6838\u4e3b\u7ba1\u4e00.Notify()----------------";
	}
}
