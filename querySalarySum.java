import hr.common;

import java.io.*;
import java.util.*;

import jcx.util.*;
import jcx.html.*;
import jcx.db.*;
import jcx.jform.hproc;
public class querySalarySum extends hproc{
	public String action(String value)throws Throwable{
		String THRUSER = "";
		THRUSER = "HRUSER";
		String TSALARY = "SALARY";
		String TSALARYD = "SALARYD";
		String TSALRETIRE = "SALRETIRE";
		message("");
		String qSALYM = getValue("SALYM");//薪資年月
		//String qSALYM = "201507";
		String DKIND = getValue("DKIND");//薪資年月
		if(DKIND.trim().equals("2")){
			TSALARY = "SALARY_PLAN";
			TSALARYD = "SALARYD_PLAN";
			TSALRETIRE = "SALRETIRE_PLAN";
		}
		String TKY = getValue("TKY");//薪資年月
		String qDEPT_NO = getValue("DEPT_NO");//部門別
		String qCPNYID = getValue("CPNYID");//公司別
		//String qCPNYID = "YG01";
		String qCOUNT = getValue("COUNT");//發新次數
		//String qCOUNT = "1";
		String DISABLE_ZERO = getValue("DISABLE_ZERO");//只顯示有金額的數目
		String ONLY_SUMMARY = getValue("ONLY_SUMMARY");//顯示最小單位
		String RETIR = getValue("RETIR");//顯示最小單位
		String ORDER1 = getValue("ORDER1");//排序方式
		String PAYTYPE = getValue("PAYTYPE");//排序方式
		String LEVELS =""; 
		try{LEVELS = getValue("LEVELS").trim();}catch(Exception e){}//層級
		String ONLY_PEOPLE =""; 
		try{ONLY_PEOPLE = getValue("ONLY_PEOPLE").trim();}catch(Exception e){}//顯示人數
		String TKIND = "";
		try{TKIND = getValue("TKIND").trim();}catch(Exception e){}//分類方式
		if (qCOUNT.trim().equals("")){
			message("薪資次數不可空白");
			return value;
		}
		String DATELIST = (String)get("SYSTEM.DATELIST");
		boolean b = false;
		if (qSALYM.trim().equals("")) {
			message("薪資年月不可空白");
			return value;
		} else if ( DATELIST.equals("A") ) {
			b = common.checkDate(DATELIST,"ym",qSALYM);
		} else if ( DATELIST.equals("B") ) {
			b = common.checkDate(DATELIST,"ym",qSALYM);
		}
		if(b){
			if(DATELIST.trim().equals("A")){
				qSALYM = convert.roc2ac(qSALYM+"01");
				qSALYM = datetime.getYear(qSALYM).trim()+datetime.getMonth(qSALYM).trim();
			}
		}
		else{
			message("日期輸入格式錯誤，請檢查");
			return value;
		}

		talk t = getTalk();
		String Tsql = "";
		if(!qDEPT_NO.trim().equals("")) Tsql += " and a.DEPT_NO in "+(qDEPT_NO.trim())+" ";
		String user=getUser();
		String CPNYID = qCPNYID.trim();
		String CPY[][] = t.queryFromPool("select CPNYID from HRUSER where EMPID='"+convert.ToSql(user.trim())+"'");

		
		try {
			String ret[][] = t.queryFromPool("select count(*) from HRUSER_" + qSALYM);
			if (check.isNum(ret[0][0].trim())) {
				THRUSER = "HRUSER_" + qSALYM;
			}
		} catch (Exception e) {
			THRUSER = "HRUSER";
		}

		//                               0                  1             2                 3           4            5                6                  7               8           9             10          11                   12         13
		String sql = "select b.DEPT_NO , b.HECNAME,b.EMPID ,c.DEP_NAME,c.DEP_CODE,b.STATE,isnull(c.DEP_TYPE,''),b.WORKTYPE,a.PAYMENT,b.POSSIE,b.GRADE,c.DEP_CODE,c.DEP_NAME,b.CPNYID"
			+ " from "+TSALARY+" a , "+THRUSER+" b ,HRUSER_DEPT_BAS c "
			+ " where a.CPNYID=b.CPNYID and a.EMPID = b.EMPID and b.DEPT_NO=c.DEP_NO"
//			+ " and ("
//			+ " a.PAYMENT!=102143608977930458145"
//			+" or a.TAXPAY!=102143608977930458145"
//			+" or a.DELPRICE!=102143608977930458145"
//			+" or a.NTAXPAY!=102143608977930458145"
//			+" or a.TOTAL!=102143608977930458145"
//			+")"
			+ " and a.EMPID in (select EMPID from "+TSALARYD+" where SALYM= '"+convert.ToSql(qSALYM.trim())+"' and COUNT = '"+convert.ToSql(qCOUNT.trim())+"' and SALAMT!=102143608977930458145)"
			+ Tsql 
			+ " and a.SALYM = '"+convert.ToSql(qSALYM.trim())+"' "
			+ " and a.COUNT = '"+convert.ToSql(qCOUNT.trim())+"' ";
			if(CPNYID.trim().length()!=0)
				sql += " and b.CPNYID = '"+convert.ToSql(CPNYID)+"' ";
			sql+= " and b.PAYYN = 'Y' ";
		if(!PAYTYPE.equals("")) sql+=" and PAYTYPE='"+PAYTYPE+"' ";
		if(!TKY.equals("")) sql+=" and c.SET13='"+TKY+"' ";
		if(ORDER1.equals("B")){
			sql+=" order by c.DEP_CODE,c.DEP_NO,b.EMPID ";
		} else {
			if(CPNYID.equals("3000")){
				sql+=" order by c.DEP_CODE,c.DEP_NO,b.GRADE ";
			} else {
				sql+=" order by c.DEP_CODE,c.DEP_NO,b.GRADE desc ";
			}
		}
		String r1[][] = t.queryFromPool(sql);
		if (r1.length == 0) {
			message("查無資料");
			return value;
		}

		String POSITIONS[][] = t.queryFromPool("select POSSIE,POS_NAME from POSITION");
		Hashtable POS=new Hashtable();
		for(int i=0;i<POSITIONS.length;i++) POS.put(POSITIONS[i][0].trim(),POSITIONS[i][1].trim());

		Hashtable PAYMENT=new Hashtable();
		for(int i=0;i<r1.length;i++){
			String amt=decrypt("hr",r1[i][8]);
//			System.out.println(r1[i][2]+"==>"+amt);
			PAYMENT.put(r1[i][2],amt);
		}
//		System.out.println("LEVELS==>"+LEVELS);
//		if(LEVELS.trim().equals("3") || LEVELS.trim().equals("4")){
		if(!LEVELS.trim().equals("12")){
			int LEV = Integer.parseInt(LEVELS.trim());
			String sqlw = "select DEP_NO,PARENT_NO,isnull(DEP_TYPE,''),DEP_CODE,DEP_NAME,CPNYID from HRUSER_DEPT_BAS where DEP_NO<1000";
//			if(CPNYID.trim().length()!=0)
//				sqlw += " and CPNYID = '"+convert.ToSql(CPNYID)+"' ";
			sqlw += " order by CPNYID,DEP_CODE,DEP_NO";
			String DEP[][] = t.queryFromPool(sqlw);
			Hashtable H1_P = new Hashtable();
			Hashtable H1_T = new Hashtable();
			Hashtable H1_C = new Hashtable();
			Hashtable H1_N = new Hashtable();
			Vector ALLDEP = new Vector();
			for(int i=0;i<DEP.length;i++){
				String SKEYS = "1"+DEP[i][3].trim()+"<PP>1"+DEP[i][0].trim();
				if(!ALLDEP.contains(SKEYS)) ALLDEP.addElement(SKEYS);
				H1_P.put(DEP[i][0].trim(),DEP[i][1].trim());
				if(DEP[i][2].trim().length()==0) DEP[i][2] = "12";
				H1_T.put(DEP[i][0].trim(),DEP[i][2].trim());
				H1_C.put(DEP[i][0].trim(),DEP[i][3].trim());
				H1_N.put(DEP[i][0].trim(),DEP[i][4].trim());
			}
			Hashtable ALLD= new Hashtable();
			for(int i=0;i<r1.length;i++){
//				String EE = "Bef<==>";
//				for(int j=0;j<r1[i].length;j++){
//					if(j==0) EE += r1[i][j].trim();
//					else EE += "<==>"+r1[i][j].trim();
//				}
//				System.out.println(EE);
				String SKEYS = "1"+r1[i][4].trim()+"<PP>1"+r1[i][0].trim();
				int LEVS = 0;
				try{LEVS = Integer.parseInt(r1[i][6].trim());}catch(Exception e){
					LEVS = 12;
				}
//				System.out.println("LEVS==>"+LEVS);
//				System.out.println("LEV==>"+LEV);
				if(LEVS<=LEV){
					Vector DD = (Vector)ALLD.get(SKEYS);
					if(DD==null) DD = new Vector();
					DD.addElement(r1[i]);
					ALLD.put(SKEYS,DD);
					continue;
				}
				String DEP_NO = r1[i][0].trim();
//				System.out.println("DEP_NO==>"+DEP_NO);
				while(true){
					String PAR = "";
					try{PAR = H1_P.get(DEP_NO).toString();}catch(Exception e){}
//					System.out.println("PAR==>"+PAR);
					if(PAR.trim().length()!=0){
						int LE = 0;
						try{LE = Integer.parseInt(H1_T.get(PAR.trim()).toString());}catch(Exception e){}
//						System.out.println("LE==>"+LE);
//						System.out.println("LEV==>"+LEV);
						if(LE<=LEV){
							String CN = "";
							try{CN = H1_N.get(PAR).toString();}catch(Exception e){}
							String DECO = "";
							try{DECO = H1_C.get(PAR).toString();}catch(Exception e){}
							r1[i][0] = PAR.trim();
							r1[i][3] = CN.trim();
							r1[i][4] = DECO.trim();
							r1[i][6] = ""+LE;
							SKEYS = "1"+r1[i][4].trim()+"<PP>1"+r1[i][0].trim();
							Vector DD = (Vector)ALLD.get(SKEYS);
							if(DD==null) DD = new Vector();
							DD.addElement(r1[i]);
							ALLD.put(SKEYS,DD);
//							System.out.println("PAR==>"+PAR);
//							System.out.println("CN==>"+CN);
//							System.out.println("DECO==>"+DECO);
//							System.out.println("LE==>"+LE);
							break;
						}
						else{
							DEP_NO = PAR;
							continue;
						}
					}
					else{
						Vector DD = (Vector)ALLD.get(SKEYS);
						if(DD==null) DD = new Vector();
						DD.addElement(r1[i]);
						ALLD.put(SKEYS,DD);

						break;
					}
				}
			}
			Vector FF = new Vector();
			for(int i=0;i<ALLDEP.size();i++){
				String SKEYS = ALLDEP.elementAt(i).toString();
				Vector DD = (Vector)ALLD.get(SKEYS);
				if(DD!=null){
					for(int j=0;j<DD.size();j++){
						FF.addElement((String [])DD.elementAt(j));
					}
				}
			}
			r1 = (String[][])FF.toArray(new String[0][0]);
		}
//		for(int i=0;i<r1.length;i++){
//			String EE = "Aft<==>";
//			for(int j=0;j<r1[i].length;j++){
//				if(j==0) EE += r1[i][j].trim();
//				else EE += "<==>"+r1[i][j].trim();
//			}
//			System.out.println(EE);
//		}
		String SQLW = "";
		if(CPY[0][0].trim().equals("23826861")){
			SQLW = "select SALNO,SALCNAME,ADDORDEL,SALSCOPE,SALGROUP from SALITEM where (CPNYID='' or CPNYID is null or CPNYID='"+CPNYID+"' ) order by SALGROUP,SALSCOPE,SALNO";
		}
		else{
			if(CPNYID.trim().length()!=0)
				SQLW = "select SALNO,SALCNAME,ADDORDEL,SALSCOPE,SALGROUP from SALITEM where (CPNYID='' or CPNYID is null or CPNYID='"+CPNYID+"' ) order by SALGROUP,SALSCOPE,SALNO";
			else
				SQLW = "select SALNO,SALCNAME,ADDORDEL,SALSCOPE,SALGROUP from SALITEM order by SALGROUP,SALSCOPE,SALNO";
		}
		String base[][] = t.queryFromPool(SQLW);
//		System.out.println("base.length==>"+base.length);
		String header[] = new String[0];
		Vector hv=new Vector();
		int rrt = 0;
		if(ONLY_SUMMARY.trim().equals("Y")){
			hv.addElement(translate("員工編號"));
			hv.addElement(translate("員工姓名"));
			hv.addElement(translate("部門編號"));
			hv.addElement(translate("部門名稱"));
			hv.addElement(translate("職稱"));
			hv.addElement(translate("職等"));
			rrt=4;
		}
		else{
			hv.addElement(translate("部門編號"));
			hv.addElement(translate("部門名稱"));
		}
		int starts = 0;
		int col = 0;
		if(TKIND.trim().length()!=0){
			hv.addElement(translate("類型"));
			starts = 3+rrt;
			if(TKIND.trim().equals("1")){
				col = 2;
			}
		}
		else{
			starts = 2+rrt;
			col = 1;
		}
		if(ONLY_PEOPLE.trim().equals("Y") && !ONLY_SUMMARY.trim().equals("Y")){
			hv.addElement(translate("人數"));
			starts++;
		}
		Hashtable BASE=new Hashtable();
		Hashtable index=new Hashtable();
		Hashtable bg=new Hashtable();
		int count=0;
		String last_group=base[0][4];
		for(int i=0;i<base.length;i++){
			if(!last_group.equals("")){
				if(!last_group.equals(base[i][4])){
					hv.addElement(translate(last_group));
					index.put(last_group,new Integer(count+starts));
					bg.put(new Integer(count+starts),"");
					count++;
				}
			}
			last_group=base[i][4];
			hv.addElement(translate(base[i][1]));
			index.put(base[i][0],new Integer(count+starts));
			BASE.put(base[i][0],base[i]);
			count++;
		}

		if(!last_group.equals("")){
			hv.addElement(translate(last_group));
			index.put(last_group,new Integer(count+starts));
			bg.put(new Integer(count+starts),"");
		}

		hv.addElement(translate("公司提撥退休金"));
		hv.addElement(translate("合計"));

		header=(String[])hv.toArray(new String[0]);
		String TSUM[] = new String[header.length];
		for(int i=0;i<TSUM.length;i++) TSUM[i]="";
		TSUM[0] = translate("總計");TSUM[2] = "";
		if(ONLY_PEOPLE.trim().equals("Y") && ONLY_SUMMARY.trim().equals("Y")) TSUM[1+rrt] = "0";
		else TSUM[1] = "";
		if(ONLY_PEOPLE.trim().equals("Y") && !ONLY_SUMMARY.trim().equals("Y")){
			for(int i=starts-1;i<TSUM.length;i++) TSUM[i]="0";
		}
		else{
			for(int i=starts;i<TSUM.length;i++) TSUM[i]="0";
		}

		String SUM[][] = new String[col][header.length];
		for(int j=0;j<SUM.length;j++){
			for(int i=0;i<SUM[j].length;i++) SUM[j][i]="";
			SUM[j][0] = translate("合計");SUM[j][2] = "";
			if(ONLY_PEOPLE.trim().equals("Y") && ONLY_SUMMARY.trim().equals("Y")) SUM[j][1+rrt] = "0";
			else SUM[j][1] = "";
			if(ONLY_PEOPLE.trim().equals("Y") && !ONLY_SUMMARY.trim().equals("Y")){
				for(int i=starts-1;i<SUM[j].length;i++) SUM[j][i]="0";
			}
			else{
				for(int i=starts;i<SUM[j].length;i++) SUM[j][i]="0";
			}
		}
		String DEP_SUM[][] =new String[col][header.length];
		for(int j=0;j<DEP_SUM.length;j++){
			for(int i=0;i<DEP_SUM[j].length;i++) DEP_SUM[j][i]="";
			DEP_SUM[j][0]=translate("小計");DEP_SUM[j][2] = "";
			if(ONLY_PEOPLE.trim().equals("Y") && ONLY_SUMMARY.trim().equals("Y")) DEP_SUM[j][1+rrt] = "0";
			else DEP_SUM[j][1] = "";
			if(!ONLY_SUMMARY.equals("Y")){
				DEP_SUM[j][0]=r1[0][4].trim();
				DEP_SUM[j][1]=r1[0][3].trim();
			}
			if(ONLY_PEOPLE.trim().equals("Y") && !ONLY_SUMMARY.trim().equals("Y")){
				for(int i=starts-1;i<DEP_SUM[j].length;i++) DEP_SUM[j][i]="0";
			}
			else{
				for(int i=starts;i<DEP_SUM[j].length;i++) DEP_SUM[j][i]="0";
			}
		}
		Hashtable SALRETIRE = new Hashtable();
		if(RETIR.trim().equals("1"))
			SALRETIRE=getSQLHash(t,"select COMPPAY1,EMPID from "+TSALRETIRE+" where SALYM='"+qSALYM+"' order by EMPID",1);
		Hashtable SALARYD=getSQLHash(t,"select SALNO,SALAMT,SALSCOPE,EMPID,CPNYID from "+TSALARYD+" where SALYM='"+qSALYM+"' and COUNT='"+qCOUNT+"' order by EMPID",3);
		String hrkey="hr";
		Vector TAB=new Vector();
		String DEP_NAME="";
		for(int i=0;i<r1.length;i++){
			String EMPID = r1[i][2].trim();
			String HECNAME = r1[i][1].trim();
			String DEPNO = r1[i][0].trim();
			String DEPCODE = r1[i][4].trim();
			String DEPNAME = r1[i][3].trim();
			String STATE = r1[i][5].trim();
			String DEP_TYPE = r1[i][6].trim();
			String WORKSS= r1[i][7].trim();
			String POSSIE= r1[i][9].trim();
			String GRADE= r1[i][10].trim();
			String ODEPCODE= r1[i][11].trim();
			String ODEPNAME= r1[i][12].trim();
			String r1_cpnyid= r1[i][13].trim();
			if(!DEP_NAME.equals(DEPCODE)){
				if(i!=0){
					for(int j=0;j<DEP_SUM.length;j++){
						if(TKIND.trim().equals("1")){
							if(j==0)
								DEP_SUM[j][2+rrt] = translate("直接");
							else if(j==1)
								DEP_SUM[j][2+rrt] = translate("間接");
						}
						TAB.addElement(DEP_SUM[j]);
					}
					DEP_SUM =new String[col][header.length];
					for(int j=0;j<DEP_SUM.length;j++){
						DEP_SUM[j][0]=translate("小計");DEP_SUM[j][2] = "";
						if(ONLY_PEOPLE.trim().equals("Y") && ONLY_SUMMARY.trim().equals("Y")) DEP_SUM[j][1+rrt] = "0";
						else DEP_SUM[j][1] = "";
						if(!ONLY_SUMMARY.equals("Y")){
							DEP_SUM[j][0]=DEPCODE.trim();
							DEP_SUM[j][1]=DEPNAME.trim();
						}
						if(ONLY_PEOPLE.trim().equals("Y") && !ONLY_SUMMARY.trim().equals("Y")){
							for(int z=starts-1;z<DEP_SUM[j].length;z++) DEP_SUM[j][z]="0";
						}
						else{
							for(int z=starts;z<DEP_SUM[j].length;z++) DEP_SUM[j][z]="0";
						}
					}
					String EMPTY[]=new String[header.length];
					if(ONLY_SUMMARY.equals("Y")) TAB.addElement(EMPTY);
				}
				DEP_NAME=DEPCODE;
				String T1[]=new String[header.length];
				T1[0]=DEPCODE;
				T1[1]=DEPNAME;
				if(ONLY_SUMMARY.equals("Y")) TAB.addElement(T1);
			}
			String T[]=new String[header.length];
			if(TKIND.trim().length()!=0){
				T[0] = EMPID;
				T[1] = HECNAME;
				T[2] = ODEPCODE;
				T[3] = ODEPNAME;
				T[4] = (String)POS.get(POSSIE);
				T[5] = GRADE;
				if(TKIND.trim().equals("1")){
					if(WORKSS.trim().equals("B"))
						T[2+rrt] = translate("直接");
					else
						T[2+rrt] = translate("間接");
				}
			}
			else{
				T[0] = EMPID;
				T[1] = HECNAME;
				T[2] = ODEPCODE;
				T[3] = ODEPNAME;
				T[4] = (String)POS.get(POSSIE);
				T[5] = GRADE;
			}
			String[][] salaryd=(String[][])SALARYD.get(EMPID);
			if(salaryd==null) salaryd=new String[0][0];

			String[][] salretire=(String[][])SALRETIRE.get(EMPID);
			if(salretire==null) salretire=new String[0][0];

			String sum="0";
			for(int z=0;z<salaryd.length;z++){
//				String salaryd_cpnyid = salaryd[z][4].trim();
//				if(!r1_cpnyid.trim().equals(salaryd_cpnyid.trim())) continue;
				Integer ind=(Integer)index.get(salaryd[z][0]);
				if(ind==null) continue;
				String amt=decrypt(hrkey,salaryd[z][1]);
				T[ind.intValue()]=amt;
				TSUM[ind.intValue()]=operation.floatAdd(TSUM[ind.intValue()],amt,0);
				if(TKIND.trim().equals("1")){
					if(WORKSS.trim().equals("B")){
						SUM[0][ind.intValue()]=operation.floatAdd(SUM[0][ind.intValue()],amt,0);
						DEP_SUM[0][ind.intValue()]=operation.floatAdd(DEP_SUM[0][ind.intValue()],amt,0);
					}
					else{
						SUM[1][ind.intValue()]=operation.floatAdd(SUM[1][ind.intValue()],amt,0);
						DEP_SUM[1][ind.intValue()]=operation.floatAdd(DEP_SUM[1][ind.intValue()],amt,0);
					}
				}
				else{
					SUM[0][ind.intValue()]=operation.floatAdd(SUM[0][ind.intValue()],amt,0);
					DEP_SUM[0][ind.intValue()]=operation.floatAdd(DEP_SUM[0][ind.intValue()],amt,0);
				}
				String bb[]=(String[])BASE.get(salaryd[z][0]);
				String add1=bb[2];
				String salscope=bb[3];
				add1="A";
				String factor="";
				if(salscope.equals("C") || salscope.equals("D")) add1="D";
				if(add1.equals("D")){
					amt=operation.floatMultiply(amt,"-1",0);
				}

				sum=operation.floatAdd(sum,amt,0);

				String salgroup=bb[4];
				if(!salgroup.equals("")){
					Integer ind_g=(Integer)index.get(salgroup);
					if(ind_g!=null){
						try{
							if(T[ind_g.intValue()]==null) T[ind_g.intValue()]="0";
						}catch(Exception e){
							T[ind_g.intValue()]="0";
						}
						T[ind_g.intValue()]=operation.floatAdd(T[ind_g.intValue()],amt,0);

						TSUM[ind_g.intValue()]=operation.floatAdd(TSUM[ind_g.intValue()],amt,0);
						if(TKIND.trim().equals("1")){
							if(WORKSS.trim().equals("B")){
								SUM[0][ind_g.intValue()]=operation.floatAdd(SUM[0][ind_g.intValue()],amt,0);
								DEP_SUM[0][ind_g.intValue()]=operation.floatAdd(DEP_SUM[0][ind_g.intValue()],amt,0);
							}
							else{
								SUM[1][ind_g.intValue()]=operation.floatAdd(SUM[1][ind_g.intValue()],amt,0);
								DEP_SUM[1][ind_g.intValue()]=operation.floatAdd(DEP_SUM[1][ind_g.intValue()],amt,0);
							}
						}
						else{
							SUM[0][ind_g.intValue()]=operation.floatAdd(SUM[0][ind_g.intValue()],amt,0);
							DEP_SUM[0][ind_g.intValue()]=operation.floatAdd(DEP_SUM[0][ind_g.intValue()],amt,0);
						}
					}
				}
			}

			// 公司提撥退休金
			if(salretire.length!=0) {
				String amt=salretire[0][0];
				T[T.length-2]=amt;
				TSUM[T.length-2]=operation.floatAdd(TSUM[T.length-2],amt,0);
				if(TKIND.trim().equals("1")){
					if(WORKSS.trim().equals("B")){
						SUM[0][T.length-2]=operation.floatAdd(SUM[0][T.length-2],amt,0);
						DEP_SUM[0][T.length-2]=operation.floatAdd(DEP_SUM[0][T.length-2],amt,0);
					}
					else{
						SUM[1][T.length-2]=operation.floatAdd(SUM[1][T.length-2],amt,0);
						DEP_SUM[1][T.length-2]=operation.floatAdd(DEP_SUM[1][T.length-2],amt,0);
					}
				}else{
					SUM[0][T.length-2]=operation.floatAdd(SUM[0][T.length-2],amt,0);
					DEP_SUM[0][T.length-2]=operation.floatAdd(DEP_SUM[0][T.length-2],amt,0);
				}
			}

			String PAY1=(String)PAYMENT.get(EMPID);
//					System.err.println("  "+EMPID +"  PAYMENT="+PAY1+"  sum="+sum);
			if(PAY1!=null){
				if(!PAY1.equals(sum)){
					System.err.println("  "+EMPID +"  PAYMENT="+PAY1+"  sum="+sum);
				}
			}

			T[T.length-1]=sum;
			int de = 1;
			if(TKIND.trim().length()!=0 && ONLY_PEOPLE.trim().equals("Y") && ONLY_SUMMARY.trim().equals("Y")) de=2;
			TSUM[T.length-1]=operation.floatAdd(TSUM[T.length-1],sum,0);
			if(ONLY_PEOPLE.trim().equals("Y")){
				try{
					TSUM[starts-de] = ""+(Integer.parseInt(TSUM[starts-de].trim())+1);
				}catch(Exception e){
					TSUM[starts-de] = "1";
				}
			}
			if(TKIND.trim().equals("1")){
				if(WORKSS.trim().equals("B")){
					if(ONLY_PEOPLE.trim().equals("Y") ){
						try{
							SUM[0][starts-de] = ""+(Integer.parseInt(SUM[0][starts-de].trim())+1);
						}catch(Exception e){
							SUM[0][starts-de] = "1";
						}
						try{
							DEP_SUM[0][starts-de] = ""+(Integer.parseInt(DEP_SUM[0][starts-de].trim())+1);
						}catch(Exception e){
							DEP_SUM[0][starts-de] = "1";
						}
					}
					SUM[0][T.length-1]=operation.floatAdd(SUM[0][T.length-1],sum,0);
					DEP_SUM[0][T.length-1]=operation.floatAdd(DEP_SUM[0][T.length-1],sum,0);
				}
				else{
					if(ONLY_PEOPLE.trim().equals("Y")){
						try{
							SUM[1][starts-de] = ""+(Integer.parseInt(SUM[1][starts-de].trim())+1);
						}catch(Exception e){
							SUM[1][starts-de] = "1";
						}
						try{
							DEP_SUM[1][starts-de] = ""+(Integer.parseInt(DEP_SUM[1][starts-de].trim())+1);
						}catch(Exception e){
							DEP_SUM[1][starts-de] = "1";
						}
					}
					SUM[1][T.length-1]=operation.floatAdd(SUM[1][T.length-1],sum,0);
					DEP_SUM[1][T.length-1]=operation.floatAdd(DEP_SUM[1][T.length-1],sum,0);
				}
			}
			else{
				if(ONLY_PEOPLE.trim().equals("Y")){
					try{
						SUM[0][starts-de] = ""+(Integer.parseInt(SUM[0][starts-de].trim())+1);
					}catch(Exception e){
						SUM[0][starts-de] = "1";
					}
					try{
						DEP_SUM[0][starts-de] = ""+(Integer.parseInt(DEP_SUM[0][starts-de].trim())+1);
					}catch(Exception e){
						DEP_SUM[0][starts-de] = "1";
					}
				}
				SUM[0][T.length-1]=operation.floatAdd(SUM[0][T.length-1],sum,0);
				DEP_SUM[0][T.length-1]=operation.floatAdd(DEP_SUM[0][T.length-1],sum,0);
			}
//System.err.println("----- "+T[0]+"  "+sum+"  "+STATE+"  "+ONLY_SUMMARY);
			if(!sum.equals("0") || !STATE.equals("B")){
				if(ONLY_SUMMARY.equals("Y")) TAB.addElement(T);
			}
			if(i==(r1.length-1)){
				String EMPTY[]=new String[header.length];
				for(int j=0;j<DEP_SUM.length;j++){
					if(TKIND.trim().equals("1")){
						if(j==0)
							DEP_SUM[j][2+rrt] = translate("直接");
						else if(j==1)
							DEP_SUM[j][2+rrt] = translate("間接");
					}
					TAB.addElement(DEP_SUM[j]);
				}
				TAB.addElement(EMPTY);
				for(int j=0;j<SUM.length;j++){
					if(TKIND.trim().equals("1")){
						if(j==0)
							SUM[j][2+rrt] = translate("直接");
						else if(j==1)
							SUM[j][2+rrt] = translate("間接");
					}
					TAB.addElement(SUM[j]);
				}
				if(TKIND.trim().length()!=0){
					TAB.addElement(EMPTY);
					TAB.addElement(TSUM);
				}
			}
		}
		

		if(DISABLE_ZERO.equals("Y")){
			Vector v1=new Vector();
			for(int j=0;j<SUM[0].length;j++){
				for(int i=0;i<SUM.length;i++){
					if(!SUM[i][j].equals("0")){
						v1.addElement(header[j]);
						break;
					}
				}
			}
			header=(String[])v1.toArray(new String[0]);
			for(int z=0;z<TAB.size();z++){
				String m[]=(String[])TAB.elementAt(z);
				Vector v2=new Vector();
				for(int j=0;j<SUM[0].length;j++){
					for(int i=0;i<SUM.length;i++){
						if(!SUM[i][j].equals("0")){
							v2.addElement(m[j]);
							break;
						}
					}
				}
				m=(String[])v2.toArray(new String[0]);
				TAB.setElementAt(m,z);
			}
		}
		setTableHeader("querySalarySum_table",header);
		String[][] mm=(String[][])TAB.toArray(new String[0][0]);
		for(int i=0;i<mm.length;i++){
			for(int j=starts;j<mm[i].length;j++){
				if(mm[i][j]==null) continue;
				if(mm[i][j].equals("")) continue;
				if(mm[i][j].startsWith("-"))
					mm[i][j]="-"+format.format(mm[i][j].substring(1),"999,999,999").trim();
				else
					mm[i][j]=format.format(mm[i][j],"999,999,999").trim();
			}
		}
		setTableData("querySalarySum_table",mm);

		String SALYM1=qSALYM;
		if(DATELIST.equals("A")){
			SALYM1=operation.floatAdd(SALYM1,"-191100",0);
			SALYM1=SALYM1.substring(0,2)+"/"+SALYM1.substring(2);
		} else {
			SALYM1=SALYM1.substring(0,4)+"/"+SALYM1.substring(4);
		}
		setValue("text2",translate("薪資年月：")+SALYM1+"  "+translate("第")+qCOUNT+translate("次"));
		String valueA = "";
		if(DATELIST.trim().equals("A"))
			valueA= translate("製表日期：")+convert.FormatedDate(getToday("yymmdd"),"/");
		else
			valueA = translate("製表日期：")+convert.FormatedDate(getToday("YYYYmmdd"),"/");
		valueA += "   "+translate("製表時間：")+convert.FormatedTime(getTime("hms"),":");
		setValue("text1",valueA);
		return value;
	}
	
	public Hashtable getSQLHash(talk t,String sql,int index) throws Exception{
		Hashtable h=new Hashtable();
		String s[][]=t.queryFromPool(sql);
		if(s.length==0) return h;
		String last_id=s[0][index];
		Vector v=new Vector();
		for(int i=0;i<s.length;i++){
			if(!last_id.equals(s[i][index])){
				String d[][]=(String[][])v.toArray(new String[0][0]);
				h.put(last_id,d);
				last_id=s[i][index];
				v.removeAllElements();
			}
			v.addElement(s[i]);
		}
		String d[][]=(String[][])v.toArray(new String[0][0]);
		h.put(last_id,d);
		return h;
	}
	public String getInformation(){
		return "---------------\u67e5\u8a62\u6309\u9215\u7a0b\u5f0f.preProcess()----------------";
	}
}
