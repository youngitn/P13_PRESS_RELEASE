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
		// 當表單進入流程狀態簽核主管一時,會執行本段程式
		// 可用以寄發Email通知或是自動再處理自定Transaction
		service = new BaseService(this);
		mail = new MailService(service);
		//log.debug("value=" + value);
		talk t = getTalk();
		String[][] r1 = null;
		String sql = "";
		String state = value.trim();

		// 組成所需的 SQL
		boolean isFinal = false;
		if (state.equals("待處理")) {
			String EMPID = getValue("EMPID").trim();
			sql = "select EMPID , PASS , EMAIL from HRUSER where EMPID = '" + convert.ToSql(EMPID) + "' ";
		} else if (state.equals("主管簽核") || state.equals("職務代理人")) {
			String EMPID = "";
			if (getEngagedPeople().size() != 0)
				EMPID = getEngagedPeople().elementAt(0).toString().trim();
			sql = "select EMPID , PASS , EMAIL from HRUSER where EMPID = '" + convert.ToSql(EMPID) + "' ";
		} else if (state.startsWith("簽核主管")) {
			String SIGN = "";
			if (state.endsWith("一")) {
				SIGN = getValue("SIGN11").trim();
				if (getValue("SIGN12").trim().length() <= 0 && getValue("SIGN13").trim().length() <= 0
						&& getValue("SIGN14").trim().length() <= 0 && getValue("SIGN15").trim().length() <= 0
						&& getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("二")) {
				SIGN = getValue("SIGN12").trim();
				if (getValue("SIGN13").trim().length() <= 0 && getValue("SIGN14").trim().length() <= 0
						&& getValue("SIGN15").trim().length() <= 0 && getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("三")) {
				SIGN = getValue("SIGN13").trim();
				if (getValue("SIGN14").trim().length() <= 0 && getValue("SIGN15").trim().length() <= 0
						&& getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("四")) {
				SIGN = getValue("SIGN14").trim();
				if (getValue("SIGN15").trim().length() <= 0 && getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("五")) {
				SIGN = getValue("SIGN15").trim();
				if (getValue("SIGN16").trim().length() <= 0) {
					isFinal = true;
				}
			} else if (state.endsWith("六")) {
				SIGN = getValue("SIGN16").trim();
				isFinal = true;
			}
			sql = "select EMPID , PASS , EMAIL from HRUSER "
					+ "where EMPID in (select ID from HRUSER_DEPT where DEP_NO = " + SIGN + ") ";
		} else if ("END".equals(state)) {
			//isFinal = true;
		}

		// 執行 SQL
		if (sql.length() > 0)
			r1 = t.queryFromPool(sql);
		else
			r1 = new String[0][0];

		// 取得發送 mail 所需的資訊
		String[][] r2 = null;

		// 取得系統連結
		String HRADDR = "", fun_id = "";
		sql = "select HRADDR from HRSYS ";
		r2 = t.queryFromPool(sql);
		if (r2.length != 0 && r2[0][0] != null && r2[0][0].trim().length() != 0) {
			if (r2[0][0].trim().toUpperCase().startsWith("HTTP"))
				HRADDR = r2[0][0].trim() + fun_id;
			else
				HRADDR = "http://" + r2[0][0].trim() + fun_id;
		}

		// 判斷是否為 事業處主管 (1105) , 業務處主管 (1106)
		boolean run = false;
		String EMPID = getValue("EMPID").trim();
		sql = "select count(*) from HRUSER_DEPT " + "where DEP_NO in ('1105','1106') " + "and ID = '"
				+ convert.ToSql(EMPID) + "' ";
		r2 = t.queryFromPool(sql);
		if (r2.length != 0 && r2[0][0] != null && r2[0][0].trim().length() != 0 && !r2[0][0].trim().equals("0"))
			run = true;

		// 取得內容所需資料
		String PNO = getValue("PNO").trim();
		sql = "select a.PNO , a.EMPID , b.HECNAME , c.DEP_NAME , a.SDATE , a.EDATE , a.AMTDAY , a.TRAPLACE "
				+ "from TRAVEL a , HRUSER b , HRUSER_DEPT_BAS c " + "where a.EMPID = b.EMPID "
				+ "and b.DEPT_NO = c.DEP_NO " + "and a.PNO = '" + convert.ToSql(PNO) + "' ";
		r2 = t.queryFromPool(sql);

		// 設定主旨及內容
		String subject = "", content = "", fun_name = "員工之出差申請單";
		if (r2.length != 0 && r2[0][1] != null)
			subject += "(" + r2[0][1].trim() + ") ";
		if (r2.length != 0 && r2[0][2] != null)
			subject += r2[0][2].trim() + " ";
		subject += fun_name + "，請進入系統簽核" + HRADDR.trim();

		// 發送 mail
		for (int i = 0; i < r1.length; i++) {
			content = "";
			if (r2.length != 0 && r2[0][0] != null)
				content += "出差單號：" + r2[0][0].trim() + " \r\n";
			if (r2.length != 0 && r2[0][1] != null)
				content += "工號：" + r2[0][1].trim() + " \r\n";
			if (r2.length != 0 && r2[0][2] != null)
				content += "姓名：" + r2[0][2].trim() + " \r\n";
			if (r2.length != 0 && r2[0][3] != null)
				content += "單位：" + r2[0][3].trim() + " \r\n";
			if (r2.length != 0 && r2[0][4] != null)
				content += "起始日期：" + r2[0][4].trim() + " \r\n";
			if (r2.length != 0 && r2[0][5] != null)
				content += "結束日期：" + r2[0][5].trim() + " \r\n";
			if (r2.length != 0 && r2[0][6] != null)
				content += "總計天數：" + r2[0][6].trim() + " \r\n";
			content += "地點：" + getData("TRAPLACE") + " \r\n";
			content += "事由：" + getData("DESC1") + " \r\n";

			if (run) {
				content = "";
				if (r2.length != 0 && r2[0][0] != null)
					content += "出差單號：" + r2[0][0].trim() + " \r\n";
				if (r2.length != 0 && r2[0][1] != null)
					content += "工號：" + r2[0][1].trim() + " \r\n";
				if (r2.length != 0 && r2[0][2] != null)
					content += "姓名：" + r2[0][2].trim() + " \r\n";
				if (r2.length != 0 && r2[0][7] != null)
					content += "出差地點：" + r2[0][7].trim() + " \r\n";
				if (r2.length != 0 && r2[0][4] != null)
					content += "起始日期：" + r2[0][4].trim() + " \r\n";
				if (r2.length != 0 && r2[0][5] != null)
					content += "結束日期：" + r2[0][5].trim() + " \r\n";
				content += "地點：" + getData("TRAPLACE") + " \r\n";
				content += "事由：" + getData("DESC1") + " \r\n";
			}
			content += "系統網址：";
			if (HRADDR.length() != 0)
				content += "( " + HRADDR;
			// if (r1[i][0].trim().length() != 0 && r1[i][1].trim().length() != 0) content +=
			// "&pwd={"+r1[i][1].trim()+"}"+"&uid="+r1[i][0].trim();
			if (HRADDR.length() != 0)
				content += " )";
			content += "簽核";
			
			mail.sendMailbccUTF8(new String[] { r1[i][2].trim() }, subject, content, null, "", "text/plain");

		}
		//log.debug("isFinal=" + isFinal);
		if (isFinal) {
			String empid = r2[0][1].trim();
			String name = r2[0][2].trim();
			String SDATE = convert.FormatedDate(getValue("SDATE").trim(), "/");
			subject = empid + "　" + name + "　" + SDATE + "申請出差　已經主管核准。";
			content = "";
			if (r2.length != 0 && r2[0][0] != null)
				content += "出差單號：" + r2[0][0].trim() + " \r\n";
			if (r2.length != 0 && r2[0][1] != null)
				content += "工號：" + r2[0][1].trim() + " \r\n";
			if (r2.length != 0 && r2[0][2] != null)
				content += "姓名：" + r2[0][2].trim() + " \r\n";
			if (r2.length != 0 && r2[0][3] != null)
				content += "單位：" + r2[0][3].trim() + " \r\n";
			if (r2.length != 0 && r2[0][4] != null)
				content += "起始日期：" + r2[0][4].trim() + " \r\n";
			if (r2.length != 0 && r2[0][5] != null)
				content += "結束日期：" + r2[0][5].trim() + " \r\n";
			if (r2.length != 0 && r2[0][6] != null)
				content += "總計天數：" + r2[0][6].trim() + " \r\n";
			content += "地點：" + getData("TRAPLACE") + " \r\n";
			content += "事由：" + getData("DESC1") + " \r\n";
			// 2011.11.09 add 監管人員////////////////////////////////////////////////
			String notifier = "";
			// PLACE.PLACE IN ('F') ->49425
			String rrr[][] = t.queryFromPool("SELECT PLACE from HRUSER WHERE EMPID = '" + empid
					+ "' and CPNYID ='YT01' ");
			if (rrr.length > 0) {
				if (rrr[0][0].equals("F")) {
					notifier = "49425"; // 謝慧玲
				} else if (rrr[0][0].equals("E") || rrr[0][0].equals("V") || rrr[0][0].equals("W")) {
					notifier = "12629"; // 吳秀莉
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
