import jcx.jform.hproc;

import java.io.*;
import java.util.*;

import jcx.util.*;
import jcx.html.*;
import jcx.db.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

public class SalaryCostAnalysis extends hproc{
	public String action(String value)throws Throwable{
		talk t = getTalk();
		String CNP = (String)get("iCPNYID");
		String hrkey = "hr";
		String DATELIST = (String)get("SYSTEM.DATELIST");
		String CPNYID = getValue("name1");
		String YYMM = getValue("name2");
		String SYYMM = getValue("name2");
		//  CPNYID = "YT01";
		//  YYMM = "201103";
		//	SYYMM = "201103";
		String ID = getUser();
		if(CNP.trim().length()==4 && CNP.trim().startsWith("YT")){
			String AAA[][] = t.queryFromPool("select count(*) from HRUSER_DEPT where DEP_NO=1061 and ID='"+convert.ToSql(ID.trim())+"'");
			if(AAA[0][0].trim().equals("0")){
				CPNYID = CNP.trim();
			}
		}
		if(CPNYID.trim().length()==0){
			message("公司別不可空白");
			setValue("name1","");
			setValue("name2","");
			setTableData("table2",new String [0][0]);
			return value;
		}
		if(YYMM.trim().length()==0){
			message("薪資年月不可空白");
			setValue("name1","");
			setValue("name2","");
			setTableData("table2",new String [0][0]);
			return value;
		}
		if(DATELIST.trim().equals("A")){
			if(!check.isRocDay(YYMM.trim()+"01")){
				message("薪資年月輸入錯誤");
				setValue("name1","");
				setValue("name2","");
				setTableData("table2",new String [0][0]);
				return value;
			}
			YYMM = ""+(Integer.parseInt(YYMM.trim())+191100);
		}
		else{
			if(!check.isACDay(YYMM.trim()+"01")){
				message("薪資年月輸入錯誤");
				setValue("name1","");
				setValue("name2","");
				setTableData("table2",new String [0][0]);
				return value;
			}
		}
		String DEP_KIND[][] = t.queryFromPool("select DEPTKIND,DEPTKN from DEPTKIND order by DEPTKIND");
		String salSQL = "select SALNO,SALCNAME,TAXYN,SALSCOPE from SALITEM where isnull(METHOD,'0')!='4' order by SALSCOPE,TAXYN desc,SALNO";							
		String SALNO[][] = t.queryFromPool(salSQL);
													
		
		Hashtable POSTS = new Hashtable();
		Vector v1=new Vector();
		v1.addElement("薪資項目");
		int cc=1;
		for(int i=0;i<DEP_KIND.length;i++){
			v1.addElement(DEP_KIND[i][1].trim()+"-間接");
			POSTS.put(DEP_KIND[i][0].trim()+"<PP>A",""+cc);
			cc++;
			v1.addElement(DEP_KIND[i][1].trim()+"-直接");
			POSTS.put(DEP_KIND[i][0].trim()+"<PP>B",""+cc);
			cc++;
		}
		v1.addElement("小計");
		Hashtable RETY = new Hashtable();
		String TOTR[] = new String [(DEP_KIND.length*2)+2];
		String TOTRW[] = new String [(DEP_KIND.length*2)+2];
		String TOTRA[] = new String [(DEP_KIND.length*2)+2];
		String TOTRB[] = new String [(DEP_KIND.length*2)+2];
		String TOTRC[] = new String [(DEP_KIND.length*2)+2];
		String TOTRD[] = new String [(DEP_KIND.length*2)+2];
		String TOTRE[] = new String [(DEP_KIND.length*2)+2];
		String TOTRAC[] = new String [(DEP_KIND.length*2)+2];
		String TOTRBE[] = new String [(DEP_KIND.length*2)+2];
		String TOTRRE[] = new String [(DEP_KIND.length*2)+2];
		String TOTRCO[] = new String [(DEP_KIND.length*2)+2];
		String TOTRCOD04[] = new String [(DEP_KIND.length*2)+2];

		String PTOTRA[] = new String [(DEP_KIND.length*2)+2];
		String PTOTRB[] = new String [(DEP_KIND.length*2)+2];
		String PTOTRC[] = new String [(DEP_KIND.length*2)+2];
		String PTOTRD[] = new String [(DEP_KIND.length*2)+2];
		for(int j=1;j<PTOTRA.length;j++) PTOTRA[j] = "0";
		for(int j=1;j<PTOTRB.length;j++) PTOTRB[j] = "0";
		for(int j=1;j<PTOTRC.length;j++) PTOTRC[j] = "0";
		for(int j=1;j<PTOTRD.length;j++) PTOTRD[j] = "0";

		for(int j=1;j<TOTR.length;j++) TOTR[j] = "0";
		for(int j=1;j<TOTRW.length;j++) TOTRW[j] = "0";
		for(int j=1;j<TOTRA.length;j++) TOTRA[j] = "0";
		for(int j=1;j<TOTRB.length;j++) TOTRB[j] = "0";
		for(int j=1;j<TOTRC.length;j++) TOTRC[j] = "0";
		for(int j=1;j<TOTRD.length;j++) TOTRD[j] = "0";
		for(int j=1;j<TOTRE.length;j++) TOTRE[j] = "0";
		for(int j=1;j<TOTRAC.length;j++) TOTRAC[j] = "0";
		for(int j=1;j<TOTRBE.length;j++) TOTRBE[j] = "0";
		for(int j=1;j<TOTRRE.length;j++) TOTRRE[j] = "0";
		for(int j=1;j<TOTRCO.length;j++) TOTRCO[j] = "0";
		for(int j=1;j<TOTRCOD04.length;j++) TOTRCOD04[j] = "0";
		//
		TOTR[0] = "總計";
		TOTRW[0] = "應稅金額";
		TOTRA[0] = "基本薪項合計";
		TOTRB[0] = "應加項合計";
		TOTRC[0] = "應減項合計";
		TOTRD[0] = "代扣項合計";
		TOTRE[0] = "獎金項合計";
		TOTRAC[0] = "固定薪(含稅)合計";
		TOTRBE[0] = "非固定薪(含稅)合計";
		TOTRRE[0] = "公司新制退休金提撥金額";
		TOTRCO[0] = "薪資發放總人數";
		TOTRCOD04[0] = "代扣所得稅人數";

		PTOTRA[0] = "扣繳薪資總額";
		PTOTRB[0] = "應扣繳稅額";  //D04
		PTOTRC[0] = "免扣繳人數";
		PTOTRD[0] = "免扣繳薪資總額";
		/*20111013 若薪資單實領金額=0,又有一筆暫收金額
		String allSQL = "select a.EMPID,a.SALNO,a.SALSCOPE,a.SALAMT,b.WORKTYPE,c.DEP_KIND,d.TAXYN "
			+ " from SALARYD a,HRUSER b,HRUSER_DEPT_BAS c,SALITEM d,SALARY e "
			+ " where a.EMPID=e.EMPID and a.SALYM=e.SALYM and a.COUNT=e.COUNT "
			+ " and a.EMPID=b.EMPID and e.DEPT_NO=c.DEP_NO and a.SALNO=d.SALNO "
			+ " and isnull(d.METHOD,'0')!='4' and d.salno not in ('B01' ) "
			+ " and a.CPNYID = '"+convert.ToSql(CPNYID.trim())+"' and a.SALYM='"+convert.ToSql(YYMM)+"' and e.PAYMENT !='102143608977930458145' "
		*/
		String allSQL = "select a.EMPID,a.SALNO,a.SALSCOPE,a.SALAMT,b.WORKTYPE,c.DEP_KIND,d.TAXYN,e.PAYMENT "
			+ " from SALARYD a,HRUSER b,HRUSER_DEPT_BAS c,SALITEM d,SALARY e "
			+ " where a.EMPID=e.EMPID and a.SALYM=e.SALYM and a.COUNT=e.COUNT "
			+ " and a.EMPID=b.EMPID and e.DEPT_NO=c.DEP_NO and a.SALNO=d.SALNO "
			+ " and isnull(d.METHOD,'0')!='4' and d.salno not in ('B01' ) "
			+ " and a.CPNYID = '"+convert.ToSql(CPNYID.trim())+"' and a.SALYM='"+convert.ToSql(YYMM)+"'  "
			+ "union all "
			+ " select a.empid,'B01',d.salscope,a.amt,b.worktype,c.dep_kind,d.taxyn,'0' "
			+ " from y_wdeptime a,HRUSER b, hruser_dept_bas c, SALITEM d "
			+ " where a.empid = b.empid and a.wdept = c.dep_no and a.note = d.salno "
			+ " and isnull(d.METHOD,'0')!='4' and c.CPNYID = '"+convert.ToSql(CPNYID.trim())+"' and a.SALYM='"+convert.ToSql(YYMM)+"'";
		
		//String SALARYD[][] = t.queryFromPool("select a.EMPID,a.SALNO,a.SALSCOPE,a.SALAMT,b.WORKTYPE,c.DEP_KIND,d.TAXYN from SALARYD a,HRUSER b,HRUSER_DEPT_BAS c,SALITEM d,SALARY e where a.EMPID=e.EMPID and a.SALYM=e.SALYM and a.COUNT=e.COUNT and a.EMPID=b.EMPID and e.DEPT_NO=c.DEP_NO and a.SALNO=d.SALNO and isnull(d.METHOD,'0')!='4' and a.CPNYID = '"+convert.ToSql(CPNYID.trim())+"' and a.SALYM='"+convert.ToSql(YYMM)+"'");
		String SALARYD[][] = t.queryFromPool(allSQL);
		String SALRETIRE[][] = t.queryFromPool("select a.EMPID,a.COMPPAY1,c.DEP_KIND,b.WORKTYPE,c.DEP_KIND from SALRETIRE a,HRUSER b,HRUSER_DEPT_BAS c where a.EMPID=b.EMPID and a.DEPT_NO=c.DEP_NO and a.CPNYID = '"+convert.ToSql(CPNYID.trim())+"' and a.SALYM='"+convert.ToSql(YYMM)+"'");
		for(int i=0;i<SALRETIRE.length;i++){
			String WT=SALRETIRE[i][3].trim();
			String DK=SALRETIRE[i][2].trim();
			int F = 0;
			try{
				F = Integer.parseInt((String)POSTS.get(DK.trim()+"<PP>"+WT.trim()));
			}catch(Exception e){}
			if(F==0) continue;
			String amt=SALRETIRE[i][1].trim();
			TOTRRE[F] = operation.floatAdd(TOTRRE[F],amt,0);
			TOTRRE[TOTRRE.length-1] = operation.floatAdd(TOTRRE[TOTRRE.length-1],amt,0);
		}
		Hashtable VD04 = new Hashtable();
		for(int i=0;i<SALARYD.length;i++){
			String EMP = SALARYD[i][0].trim();
			String SAL = SALARYD[i][1].trim();
			String amt=decrypt(hrkey,SALARYD[i][3]);
			String payment = decrypt(hrkey,SALARYD[i][7]);
			if(SAL.trim().equals("D04") && operation.compareTo(amt,"0")>0){
				VD04.put(EMP.trim(),"1");
			}
		}
		Vector VV1 = new Vector();
		Vector VV2 = new Vector();
		for(int i=0;i<SALARYD.length;i++){
			String EMP = SALARYD[i][0].trim();
			String SAL = SALARYD[i][1].trim(); //薪項
			String amt=decrypt(hrkey,SALARYD[i][3]);
			String payment = decrypt(hrkey,SALARYD[i][7]);
			String WT=SALARYD[i][4].trim();
			String SALSCOPE=SALARYD[i][2].trim(); 
			String DK=SALARYD[i][5].trim();              //製銷管研
			String TAXYN = SALARYD[i][6].trim();
			String VSD04 = (String)VD04.get(EMP.trim());
			int F = 0;
			try{
				F = Integer.parseInt((String)POSTS.get(DK.trim()+"<PP>"+WT.trim()));
			}catch(Exception e){}
			if(F==0) continue;
			String R[] = (String [])RETY.get(SAL.trim());
			if(R==null){
				R = new String [(DEP_KIND.length*2)+2];
				for(int j=1;j<R.length;j++) R[j] = "0";
				R[0] = SAL;
			}
			if(SAL.trim().equals("D04") && operation.compareTo(amt,"0")>0){
				if(!VV2.contains(EMP.trim())){
					VV2.addElement(EMP.trim());
					TOTRCOD04[F] = operation.floatAdd(TOTRCOD04[F],"1",0);
					TOTRCOD04[TOTRCOD04.length-1] = operation.floatAdd(TOTRCOD04[TOTRCOD04.length-1],"1",0);
					PTOTRC[F] = operation.sub(PTOTRC[F],"1",0);
					PTOTRC[PTOTRC.length-1] = operation.sub(PTOTRC[PTOTRC.length-1],"1",0);
					//應扣繳稅額(D04)
					System.out.println("EMP:::"+EMP+"===D04金額="+amt);
					PTOTRB[F] = operation.floatAdd(PTOTRB[F],amt,0);
					PTOTRB[PTOTRB.length-1] = operation.floatAdd(PTOTRB[PTOTRB.length-1],amt,0);	
				}
			}
		if(operation.compareTo(amt,"0")>0){ //add 20111013 金額不為零才要加
				if(!VV1.contains(EMP.trim())){
					VV1.addElement(EMP.trim());
					TOTRCO[F] = operation.floatAdd(TOTRCO[F],"1",0);
					TOTRCO[TOTRCO.length-1] = operation.floatAdd(TOTRCO[TOTRCO.length-1],"1",0);
					PTOTRC[F] = operation.floatAdd(PTOTRC[F],"1",0);
					PTOTRC[PTOTRC.length-1] = operation.floatAdd(PTOTRC[PTOTRC.length-1],"1",0);
				}
		 }
			R[F] = operation.floatAdd(R[F],amt,0);
			R[R.length-1] = operation.floatAdd(R[R.length-1],amt,0);
			if(SALSCOPE.trim().equals("A") || SALSCOPE.trim().equals("B") || SALSCOPE.trim().equals("E")){
				if(SALSCOPE.trim().equals("A")){
					TOTRA[F] = operation.floatAdd(TOTRA[F],amt,0);
					TOTRA[TOTRA.length-1] = operation.floatAdd(TOTRA[TOTRA.length-1],amt,0);
					if(TAXYN.trim().equals("Y")){
						TOTRAC[F] = operation.floatAdd(TOTRAC[F],amt,0);
						TOTRAC[TOTRAC.length-1] = operation.floatAdd(TOTRAC[TOTRAC.length-1],amt,0);
					}
					if(VSD04!=null){
						System.out.println("AEMP:::"+EMP+"===SAL:::"+SAL +"金額="+amt);
						//PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
						//PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
						if(TAXYN.trim().equals("Y")){
								PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
								PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
						//if(SAL.trim().equals("D04")  && operation.compareTo(amt,"0")>0){
							//PTOTRB[F] = operation.floatAdd(PTOTRB[F],amt,0);
							//PTOTRB[PTOTRB.length-1] = operation.floatAdd(PTOTRB[PTOTRB.length-1],amt,0);							
						}
					}else{
						PTOTRD[F] = operation.floatAdd(PTOTRD[F],amt,0);
						PTOTRD[PTOTRD.length-1] = operation.floatAdd(PTOTRD[PTOTRD.length-1],amt,0);							
						
					}
				}
				else if(SALSCOPE.trim().equals("B")){
					TOTRB[F] = operation.floatAdd(TOTRB[F],amt,0);
					TOTRB[TOTRB.length-1] = operation.floatAdd(TOTRB[TOTRB.length-1],amt,0);
					if(TAXYN.trim().equals("Y")){
						TOTRBE[F] = operation.floatAdd(TOTRBE[F],amt,0);
						TOTRBE[TOTRBE.length-1] = operation.floatAdd(TOTRBE[TOTRBE.length-1],amt,0);
					}
					if(VSD04!=null){
							System.out.println("BEMP:::"+EMP+"===SAL:::"+SAL +"金額="+amt);
						//PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
						//PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
						if(TAXYN.trim().equals("Y")){
						//if(SAL.trim().equals("D04") && operation.compareTo(amt,"0")>0){
							PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
							PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
							//PTOTRB[F] = operation.floatAdd(PTOTRB[F],amt,0);
							//PTOTRB[PTOTRB.length-1] = operation.floatAdd(PTOTRB[PTOTRB.length-1],amt,0);
						}
					}
					else{
						PTOTRD[F] = operation.floatAdd(PTOTRD[F],amt,0);
						PTOTRD[PTOTRD.length-1] = operation.floatAdd(PTOTRD[PTOTRD.length-1],amt,0);
				
					}
				}
				else if(SALSCOPE.trim().equals("E")){
					TOTRE[F] = operation.floatAdd(TOTRE[F],amt,0);
					TOTRE[TOTRE.length-1] = operation.floatAdd(TOTRE[TOTRE.length-1],amt,0);
					if(TAXYN.trim().equals("Y")){
						TOTRBE[F] = operation.floatAdd(TOTRBE[F],amt,0);
						TOTRBE[TOTRBE.length-1] = operation.floatAdd(TOTRBE[TOTRBE.length-1],amt,0);
					}
					if(VSD04!=null){
						System.out.println("EEMP:::"+EMP+"===SAL:::"+SAL +"金額="+amt);
						//PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
						//PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
						if(TAXYN.trim().equals("Y")){
								PTOTRA[F] = operation.floatAdd(PTOTRA[F],amt,0);
								PTOTRA[PTOTRA.length-1] = operation.floatAdd(PTOTRA[PTOTRA.length-1],amt,0);
						//if(SAL.trim().equals("D04") && operation.compareTo(amt,"0")>0){
							//PTOTRB[F] = operation.floatAdd(PTOTRB[F],amt,0);
							//PTOTRB[PTOTRB.length-1] = operation.floatAdd(PTOTRB[PTOTRB.length-1],amt,0);
						}
					}
					else{
						PTOTRD[F] = operation.floatAdd(PTOTRD[F],amt,0);
						PTOTRD[PTOTRD.length-1] = operation.floatAdd(PTOTRD[PTOTRD.length-1],amt,0);						
					
					}
				}
				TOTR[F] = operation.floatAdd(TOTR[F],amt,0);
				TOTR[TOTR.length-1] = operation.floatAdd(TOTR[TOTR.length-1],amt,0);
				if(TAXYN.trim().equals("Y")){
					TOTRW[F] = operation.floatAdd(TOTRW[F],amt,0);
					TOTRW[TOTRW.length-1] = operation.floatAdd(TOTRW[TOTRW.length-1],amt,0);
					
				}
			
			}
			else{
				if(SALSCOPE.trim().equals("C")){
					TOTRC[F] = operation.floatAdd(TOTRC[F],amt,0);
					TOTRC[TOTRC.length-1] = operation.floatAdd(TOTRC[TOTRC.length-1],amt,0);
					if(TAXYN.trim().equals("Y")){
						TOTRAC[F] = operation.floatSubtract(TOTRAC[F],amt,0);
						TOTRAC[TOTRAC.length-1] = operation.floatSubtract(TOTRAC[TOTRAC.length-1],amt,0);
					}
					if(VSD04!=null){
						if(TAXYN.trim().equals("Y")){
								PTOTRA[F] = operation.floatSubtract(PTOTRA[F],amt,0);
								PTOTRA[PTOTRA.length-1] = operation.floatSubtract(PTOTRA[PTOTRA.length-1],amt,0);
						//if(SAL.trim().equals("D04") && operation.compareTo(amt,"0")>0){
							//PTOTRB[F] = operation.floatSubtract(PTOTRB[F],amt,0);
							//PTOTRB[PTOTRB.length-1] = operation.floatSubtract(PTOTRB[PTOTRB.length-1],amt,0);
						}
					}
				}
				else if(SALSCOPE.trim().equals("D")){
					TOTRD[F] = operation.floatAdd(TOTRD[F],amt,0);
					TOTRD[TOTRD.length-1] = operation.floatAdd(TOTRD[TOTRD.length-1],amt,0);
					if(VSD04!=null){
						if(TAXYN.trim().equals("Y")){
							PTOTRA[F] = operation.floatSubtract(PTOTRA[F],amt,0);
							PTOTRA[PTOTRA.length-1] = operation.floatSubtract(PTOTRA[PTOTRA.length-1],amt,0);
						}
					}
				}
				TOTR[F] = operation.floatSubtract(TOTR[F],amt,0);
				TOTR[TOTR.length-1] = operation.floatSubtract(TOTR[TOTR.length-1],amt,0);
				if(TAXYN.trim().equals("Y")){
					
					TOTRW[F] = operation.floatSubtract(TOTRW[F],amt,0);
					TOTRW[TOTRW.length-1] = operation.floatSubtract(TOTRW[TOTRW.length-1],amt,0);
				}
			
			}							
			
				/*
				else if(SALSCOPE.trim().equals("D")){
					TOTRD[F] = operation.floatAdd(TOTRD[F],amt,0);
					TOTRD[TOTRD.length-1] = operation.floatAdd(TOTRD[TOTRD.length-1],amt,0);
				}
				TOTR[F] = operation.floatSubtract(TOTR[F],amt,0);
				TOTR[TOTR.length-1] = operation.floatSubtract(TOTR[TOTR.length-1],amt,0);				
				if(TAXYN.trim().equals("Y")){
					TOTRW[F] = operation.floatSubtract(TOTRW[F],amt,0);
					TOTRW[TOTRW.length-1] = operation.floatSubtract(TOTRW[TOTRW.length-1],amt,0);
				}
			
			}						
			*/
			RETY.put(SAL.trim(),R); //應稅以上的薪項
		}
		Vector VV = new Vector();
		for(int i=0;i<SALNO.length;i++){
			String TAXYN=SALNO[i][2].trim();
			String SALSCOPE=SALNO[i][3].trim();
			String SAL = SALNO[i][0].trim();
			String R[] = (String [])RETY.get(SAL.trim());
			if(R==null) continue;
			R[0] = SALNO[i][1].trim();
			System.out.println("R302::"+TOTRW[1]);
			VV.addElement(R);
		}
		System.out.println("R305::"+TOTRW[1]);
		PTOTRD[1] =  operation.floatSubtract(TOTRW[1],PTOTRA[1],0);
		PTOTRD[2] =  operation.floatSubtract(TOTRW[2],PTOTRA[2],0);
		PTOTRD[3] =  operation.floatSubtract(TOTRW[3],PTOTRA[3],0);
		PTOTRD[4] =  operation.floatSubtract(TOTRW[4],PTOTRA[4],0);
		PTOTRD[5] =  operation.floatSubtract(TOTRW[5],PTOTRA[5],0);
		PTOTRD[6] =  operation.floatSubtract(TOTRW[6],PTOTRA[6],0);
		PTOTRD[7] =  operation.floatSubtract(TOTRW[7],PTOTRA[7],0);
		PTOTRD[8] =  operation.floatSubtract(TOTRW[8],PTOTRA[8],0);
		PTOTRD[9] =  operation.floatSubtract(TOTRW[9],PTOTRA[9],0);
		VV.addElement(TOTRW);
		VV.addElement(TOTRA);
		VV.addElement(TOTRB);
		VV.addElement(TOTRC);
		VV.addElement(TOTRD);
		VV.addElement(TOTRE);
		VV.addElement(TOTRAC);
		VV.addElement(TOTRBE);
		VV.addElement(TOTR);
		VV.addElement(TOTRRE);
		VV.addElement(TOTRCO);
		VV.addElement(TOTRCOD04);
		VV.addElement(PTOTRA);	
		VV.addElement(PTOTRB);
		VV.addElement(PTOTRC);		
		VV.addElement(PTOTRD);
		String header1[]=(String[])v1.toArray(new String[0]);
		setTableHeader("table2",header1);
		String[][] mm=(String[][])VV.toArray(new String[0][0]);
		setTableData("table2",mm);
		for (String[] a:mm){
			
		}
		/*JTable tbl = getTable("table2");
		TableColumnModel tcm = tbl.getColumnModel();
		TableColumn column;
		for(int j=0; j<tcm.getColumnCount(); j++) {
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			column = tcm.getColumn(j);
			if(j!=0){
				renderer.setHorizontalAlignment(SwingConstants.RIGHT);  //置右
			}
			column.setCellRenderer(renderer);
		}*/
		setValue("name1",CPNYID.trim());
		setValue("name2",SYYMM.trim().substring(0,SYYMM.trim().length()-2)+"/"+SYYMM.trim().substring(SYYMM.trim().length()-2,SYYMM.trim().length()));
		message("");
		return value;
	}
	public String getInformation(){
		return "---------------查詢按鈕程式.preProcess()----------------";
	}
}
