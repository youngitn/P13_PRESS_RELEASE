

import java.util.Vector;

import jcx.db.talk;
import jcx.jform.bNotify;
import jcx.util.convert;

import com.ysp.bean.UserInfoViewBean;
import com.ysp.service.BaseService;
import com.ysp.service.MailService;

public class FlowMail extends bNotify {
	BaseService service;

	public void actionPerformed(String value) throws Throwable {
		service = new BaseService(this);
		Vector vid = getEngagedPeople();
		if (vid.size() == 0)
			return;
		String state = getState();
		String empid = getValue("EMPID").trim();
		String DEPT_NAME = getValue("DEPT_NAME").trim();
		String M_DATE = getValue("M_DATE").trim();
		String CHNNOTE = getValue("CHNNOTE").trim();
		String name = getName(empid);
		talk t = getTalk();
		String THING = getValue("THING").trim();
		
		BaseService bs = new BaseService(this);
		UserInfoViewBean user = bs.getUserInfoBean(empid);
		MailService mail = new MailService(bs);
		
		if (THING.trim().length() != 0) {
			
			String[][] THG = t.queryFromPool("select ITEMNAME from SERVICES where ITEM='" + convert.ToSql(THING.trim())
					+ "' and CPNYID = '" + user.getCpnyid() + "'");
			if (THG.length != 0)
				THING = THG[0][0].trim();
		}
		Vector V2 = new Vector();
		for (int i = 0; i < vid.size(); i++) {
			String sql = "select EMAIL from HRUSER where EMPID = '" + convert.ToSql(vid.elementAt(i).toString()) + "' ";
			String r1[][] = t.queryFromPool(sql);
			if (r1.length == 0)
				continue;
			V2.addElement(r1[0][0].trim());
		}
		// V2.addElement("pigpom@interinfo.com.tw");
		if (V2.size() == 0)
			return;
		String smtp = (String) get("SYSTEM.POP3");
		if (smtp == null)
			smtp = "www.interinfo.com.tw";
		String sender = (String) get("SYSTEM.SEMAIL");
		if (sender == null)
			sender = "admin@interinfo.com.tw";
		String HRADDR = (String) get("SYSTEM.HRADDR");
		// System.out.println("smtp==>"+smtp);
		// System.out.println("sender==>"+sender);
		String title = "�D���G(" + empid + ")" + name + "���u�ӽЦ�F�䴩��( " + THING + " )�A�жi�J�t��ñ�� " + HRADDR.trim();
		String content = "";
		content += "�u���G" + empid.trim() + "\r\n";
		content += "�m�W�G" + name.trim() + "\r\n";
		content += "���G" + DEPT_NAME.trim() + "\r\n";
		content += "�ݨD����G" + M_DATE.trim() + "\r\n";
		content += "�ݨD�����W�١G" + THING.trim() + "\r\n";
		content += "�ݨD�y�z�G" + CHNNOTE + "\r\n";
		content += "�t�κ��}�G" + HRADDR.trim() + "\r\n";
		String usr[] = (String[]) V2.toArray(new String[0]);
		String sendRS = mail.sendMailbccUTF8(usr, title, content, null, "", "text/plain");
		if (sendRS.trim().equals("")) {
			message("EMAIL�w�H�X�q��");
		} else {
			message("EMAIL�H�X����");
		}
		return;
	}

	public String getInformation() {
		return "---------------\u627f\u8fa6\u8005\u56de\u8986.Notify()----------------";
	}
}
