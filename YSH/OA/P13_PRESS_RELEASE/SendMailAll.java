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
		// �^�ǭȬ� true ��ܰ��汵�U�Ӫ��y�{�B�z
		// �^�ǭȬ� false ��ܱ��U�Ӥ��������y�{�B�z
		// �ǤJ�� value �� "�֭�"
		talk t = getTalk();
		BaseService service = new BaseService();
		MailService mail = new MailService(service);

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
			title = "(" + EMPID + ")" + name + "���s�D�Z�o�G�ӽг�( " + PNO + " ) �w����";

			HRADDR = (String) get("SYSTEM.HRADDR");

			String sqlc = "select COCNAME from COMPANY where CPNYID = '"
					+ convert.ToSql(CPNYID) + "'";

			String[][] ret = t.queryFromPool(sqlc);

			content += "�D��:" + title + "\r\n";

			content += "����H:" + peopleString + "-" + getName(peopleString)
					+ "\r\n";
			content += "�ӽФH:" + EMPID.trim() + "-" + name.trim() + "\r\n";

			content += "���q�W��:" + ret[0][0] + "\r\n";

			email = service.getUserInfoBean(peopleString).getEmail();
			String usr[] = { email };

			sendRS = mail.sendMailbccUTF8(usr, title, content, null, "",
					"text/plain");
			// if send mail Sending Failed,isEmailAllSend will +1 for check.
			if (!sendRS.trim().equals("")) {
				isEmailAllSend++;
			}

		}

		if (isEmailAllSend != 0) {
			message("EMAIL�H�X����");
			return false;

		}

		message("EMAIL�w�H�X�q��");
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
