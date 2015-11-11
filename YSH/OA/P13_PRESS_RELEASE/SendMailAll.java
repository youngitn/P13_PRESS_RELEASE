package YSH.OA.P13_PRESS_RELEASE;

//YSH/OA/P13_PRESS_RELEASE/SendMailAll;
import jcx.jform.bProcFlow;
import jcx.util.*;
import jcx.db.*;

import java.util.Arrays;
import java.util.HashSet;




import com.ysp.service.BaseService;
import com.ysp.service.MailService;


public class SendMailAll extends bProcFlow {

	public boolean action(String value) throws Throwable {
		// 回傳值為 true 表示執行接下來的流程處理
		// 回傳值為 false 表示接下來不執行任何流程處理
		// 傳入值 value 為 "核准"
		talk t = getTalk();
		BaseService service = new BaseService();
		MailService mail = new MailService(service);
		// -----------------於網站維護確認關卡開方正式網址連結欄位
		// 並在按下[核准]後處存進DB--------------------------->
		if (getValue("LIVE_LINK").trim().length() == 0) {
			message("請輸入正式連結網址!");
			return false;
		}
		t.execFromPool("UPDATE PRESS_RELEASE SET PROCESS_STATE = '"
				+ getValue("PROCESS_STATE") + "' , LIVE_LINK = '"
				+ getValue("LIVE_LINK") + "' WHERE PNO = '" + getValue("PNO")
				+ "'");

		// -----------------於網站維護確認關卡開方正式網址連結欄位
		// 並在按下[核准]後處存進DB---------------------------<
		String aString = "";
		String PNO = getValue("PNO").trim();
		String CPNYID = getValue("CPNYID").trim();

		String EMPID;
		String name;
		String title;
		String content = "";
		String HRADDR = "";

		String smtp = (String) get("SYSTEM.POP3");
		if (smtp == null)
			smtp = "www.interinfo.com.tw";
		String sender = (String) get("SYSTEM.SEMAIL");
		if (sender == null)
			sender = "admin@interinfo.com.tw";
		String email = "";
		String sendRS = "";
		// getAllApprovePeople is a local method.
		String[] AllApprovePeople = getAllApprovePeople();
		int isEmailAllSend = 0;

		for (String peopleString : AllApprovePeople) {
			// System.out.println("value=" + it.next().toString());
			content = "";
			aString += peopleString;

			EMPID = getValue("EMPID").trim();
			name = getName(EMPID);
			title = "(" + EMPID + ")" + name + "之新聞稿發佈申請單( " + PNO + " ) 已結案";

			HRADDR = (String) get("SYSTEM.HRADDR");

			String sqlc = "select COCNAME from COMPANY where CPNYID = '"
					+ convert.ToSql(CPNYID) + "'";

			String[][] ret = t.queryFromPool(sqlc);

			content += "主旨:" + title + "<br>";

			content += "收件人:" + peopleString + "-" + getName(peopleString)
					+ "<br>";
			content += "申請人:" + EMPID.trim() + "-" + name.trim() + "<br>";

			content += "公司名稱:" + ret[0][0] + "<br>";

			content += "新聞稿發佈網址連結:<a href=\"" + getValue("LIVE_LINK")+"\">按此連結</a><br>";;

			email = service.getUserInfoBean(peopleString).getEmail();
			String usr[] = { email };

			sendRS = mail.sendMailbccUTF8(usr, title, content, null, "",
					"text/html");
			// if send mail Sending Failed,isEmailAllSend will +1 for check.
			if (!sendRS.trim().equals("")) {
				isEmailAllSend++;
			}

		}
		t.close();
		if (isEmailAllSend != 0) {
			message("EMAIL寄出失敗");
			return false;

		}

		message("EMAIL已寄出通知");

		return true;

	}

	public String getInformation() {
		return "---------------\u6838\u51c6.preProcess()----------------";
	}

	public String[] getAllApprovePeople() {
		String vid[][] = getFlowHistory();
		String ausr[] = new String[vid.length];
		for (int i = 0; i < vid.length; i++) {
			ausr[i] = vid[i][1].trim();
		}
		HashSet<String> set = new HashSet<String>();
		set.addAll(Arrays.asList(ausr));
		String usr[] = (String[]) set.toArray(new String[0]);
		return usr;

	}
}
