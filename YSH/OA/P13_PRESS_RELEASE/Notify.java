package YSH.OA.P13_PRESS_RELEASE;
import jcx.jform.bNotify;

import java.util.*;

import jcx.util.*;
import jcx.db.*; 

import com.ysp.service.BaseService;
import com.ysp.service.MailService;

public class Notify extends bNotify{
	BaseService service;
	public void actionPerformed(String value)throws Throwable{
		
		service = new BaseService(this);
		//get sign people 123
		Vector<?> vid = getEngagedPeople();
		if(vid.size()==0) return;
		String PNO = getValue("PNO").trim();		
		String CPNYID = getValue("CPNYID").trim();
		String EMPID = getValue("EMPID").trim();
		getValue("DEPT_NO").trim();
		
		String name = getName(EMPID); 
		talk t = getTalk();
		String sqlc = "select COCNAME from COMPANY where CPNYID = '"+convert.ToSql(CPNYID)+"'";
		
		String[][] ret = t.queryFromPool(sqlc);  
		
		BaseService bs = new BaseService(this);
		bs.getUserInfoBean(EMPID);
		MailService mail = new MailService(bs);

		Vector<String> V2 = new Vector<String>();
		for(int i=0;i<vid.size();i++){
			String sql = "select EMAIL from HRUSER where EMPID = '"+convert.ToSql(vid.elementAt(i).toString())+"' ";
			String r1[][] = t.queryFromPool(sql);
			if(r1.length==0) continue;
			V2.addElement(r1[0][0].trim());
			vid.elementAt(i).toString();
		}
		if(V2.size()==0) return;
		//String smtp = "10.1.1.60,25,ehr,ehr123";
		//String sender = "ehr@ysp.local";
		
		String smtp = (String)get("SYSTEM.POP3");
		if(smtp==null) smtp="www.interinfo.com.tw";
		String sender=(String)get("SYSTEM.SEMAIL");
		if(sender==null) sender="admin@interinfo.com.tw";
		
		String HRADDR = (String)get("SYSTEM.HRADDR");
		
		String sqlcommString = "SELECT F_INP_INFO FROM PRESS_RELEASE_FLOWC where PNO =  '"+convert.ToSql(PNO)+"' ";
		String backString[][] = t.queryFromPool(sqlcommString);
		String backTitleString = "";
		String backMemoSring = "";
		if (backString[0][0].contains("退簽")){
			backTitleString = "已退簽";
			backMemoSring = "簽核意見:"+getMemo()+"\r\n";
		}
		String title = "("+EMPID+")"+name+"之新聞稿發佈申請單( "+PNO+" ) "+backTitleString;
		String content = "";
		content += "主旨:"+ title+"\r\n";
		
		//在收件人文字中 排除admin
		if  (vid.indexOf("admin") >= 0){
			vid.remove("admin");
		}
		content += "收件人:"+((String)vid.elementAt(0)).trim()+"-"+getName((String)vid.elementAt(0))+"\r\n";
		content += "申請人:"+EMPID.trim()+"-"+name.trim()+"\r\n";
		
		content += "公司名稱:"+ret[0][0]+"\r\n"; 

		content += "簽核網址:"+HRADDR.trim()+"\r\n";
		content += backMemoSring;
		String usr[] = ((String[]) V2.toArray(new String[0]));
		
		String sendRS = mail.sendMailbccUTF8(usr, title, content, null, "", "text/plain");

		if (sendRS.trim().equals("")){
			message("EMAIL已寄出通知");
		}else{
			message("EMAIL寄出失敗");
		}

		return;
	}
	public String getInformation(){ 
		return "---------------\u8655\u4e3b\u7ba1.Notify()----------------";
	}
} 