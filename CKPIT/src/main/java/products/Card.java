package products;

import java.util.ArrayList;

import static ru.aplana.tools.Common.generateRqUID;

/**
 * Generating xml for bank cards product
 * 
 * @author Maksim Stepanov
 *
 */
public class Card {

	private String xml = null;
	
	private ArrayList<String> data = new ArrayList<String>(2);
	
	
	public Card(int subproduct, String operation) {

		String RQUID = generateRqUID();
		
		int dayWait = 5+ (int) (Math.random() * ((10 - 5) +1)); // [5,10]
		
		this.data.add(RQUID);
		
		this.data.add(operation);
		
		
		this.xml = new String("<tns:productInfoMessageRetail xsi:schemaLocation=\"tns productInfoMessageRetail.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"tns\">"+
				"<tns:CARD>"+
				"<tns:UID>"+RQUID+"</tns:UID>"+
				"<tns:PARAMS_BUSINESS>"+
					"<tns:SEGMENT_BUSINESS_1>"+
						"<tns:PRODUCT_TYPE>3</tns:PRODUCT_TYPE>"+
						"<tns:UNIQUEID_PRODUCT>8</tns:UNIQUEID_PRODUCT>"+
						"<tns:PRODUCT_NAME>Кредитная карта по двум документам</tns:PRODUCT_NAME>"+
						"<tns:UNIQUEID_SUBPRODUCT>"+subproduct+"</tns:UNIQUEID_SUBPRODUCT>"+
						"<tns:SUBPRODUCT_NAME>2104-1 По двум Рюмшин</tns:SUBPRODUCT_NAME>"+
						"<tns:CURRENCY>RUB</tns:CURRENCY>"+
						"<tns:INCLUDED_BRANCH>40</tns:INCLUDED_BRANCH>"+
						"<tns:EXCLUDED_BRANCH/>"+
						"<tns:ID_USLOVIY_KRED>31</tns:ID_USLOVIY_KRED>"+
						"<tns:KOD_NORM_DOC_PO_KRED_FIZLIC>32</tns:KOD_NORM_DOC_PO_KRED_FIZLIC>"+
						"<tns:TYP_PROC_STAVKI_OSN_DOLG>2</tns:TYP_PROC_STAVKI_OSN_DOLG>"+
						"<tns:TYP_PROC_STAVKI_PO_NEUSTOYKE>2</tns:TYP_PROC_STAVKI_PO_NEUSTOYKE>"+
						"<tns:TYPP_ROC_STAVKI_NEUST_PO_PROC>2</tns:TYPP_ROC_STAVKI_NEUST_PO_PROC>"+
						"<tns:PENALTY>12.0</tns:PENALTY>"+
						"<tns:NEUSTOYKA_PO_PROCENTAM>11.0</tns:NEUSTOYKA_PO_PROCENTAM>"+
						"<tns:GUARANTORS>2</tns:GUARANTORS>"+
						"<tns:CO_APPLICANT>2</tns:CO_APPLICANT>"+
						"<tns:KOD_TYP_FORM_DOG>33</tns:KOD_TYP_FORM_DOG>"+
						"<tns:federal_company>Y</tns:federal_company>"+
						"<tns:COMMISSION>3.0</tns:COMMISSION>"+
						"<tns:MIN_COMISS_OBSL_SSUD>80.0</tns:MIN_COMISS_OBSL_SSUD>"+
						"<tns:MAX_COMISS_OBSL_SSUD>90.0</tns:MAX_COMISS_OBSL_SSUD>"+
						"<tns:CURRENCY_COMISS>RUB</tns:CURRENCY_COMISS>"+
					"</tns:SEGMENT_BUSINESS_1>"+
					"<tns:SEGMENT_BUSINESS_8>"+
						"<tns:KOD_VIDA_OBESP>34</tns:KOD_VIDA_OBESP>"+
						"<tns:KOD_TIP_FORM_DOG_OBESP>35</tns:KOD_TIP_FORM_DOG_OBESP>"+
					"</tns:SEGMENT_BUSINESS_8>"+
					"<tns:SEGMENT_BUSINESS_61>"+
						"<tns:PROGRAM_NO_MASK>345</tns:PROGRAM_NO_MASK>"+
						"<tns:PROGRAM_NO_REQUIRED>Y</tns:PROGRAM_NO_REQUIRED>"+
						"<tns:CARD_PROGRAM_NAME>333</tns:CARD_PROGRAM_NAME>"+
						"<tns:CARD_PROGRAM_CODE>33</tns:CARD_PROGRAM_CODE>"+
						"<tns:CARD_TYPE_1>06</tns:CARD_TYPE_1>"+
						"<tns:CARD_TYPE_2>06</tns:CARD_TYPE_2>"+
						"<tns:CARD_TYPE_3>06</tns:CARD_TYPE_3>"+
						"<tns:CARD_TYPE_4>06</tns:CARD_TYPE_4>"+
						"<tns:CARD_TYPE_5>06</tns:CARD_TYPE_5>"+
						"<tns:CARD_TYPE_6>06</tns:CARD_TYPE_6>"+
						"<tns:CARD_TYPE_7>06</tns:CARD_TYPE_7>"+
						"<tns:CARD_TYPE_8>06</tns:CARD_TYPE_8>"+
						"<tns:CARD_TYPE_9>06</tns:CARD_TYPE_9>"+
						"<tns:CARD_TYPE_10>06</tns:CARD_TYPE_10>"+
						"<tns:CARD_MUST_SET_PROGRAM_NUMBER>Y</tns:CARD_MUST_SET_PROGRAM_NUMBER>"+
					"</tns:SEGMENT_BUSINESS_61>"+
				"</tns:PARAMS_BUSINESS>"+
				"<tns:PARAMS_BUSINESS_TB TB_CODE=\"0\">"+
					"<tns:SEGMENT_BUSINESS_1_TB>"+
						"<tns:MIN_AMOUNT>2000.0</tns:MIN_AMOUNT>"+
						"<tns:MAX_AMOUNT>20000.0</tns:MAX_AMOUNT>"+
					"</tns:SEGMENT_BUSINESS_1_TB>"+
				"</tns:PARAMS_BUSINESS_TB>"+
				"<tns:PARAMS_RISK>"+
					"<tns:SEGMENT_RISK_1>"+
						"<tns:DopProductKodForSM>35</tns:DopProductKodForSM>"+
						"<tns:MAX_REVISION_NUMBER>5</tns:MAX_REVISION_NUMBER>"+
						"<tns:APPROVAL_VALIDITY>15</tns:APPROVAL_VALIDITY>"+
						"<tns:DECLINE_SEARCH>12</tns:DECLINE_SEARCH>"+
						"<tns:UNDWRT2_LIMIT>200000</tns:UNDWRT2_LIMIT>"+
						"<tns:UNDWRT3_LIMIT>50000</tns:UNDWRT3_LIMIT>"+
						"<tns:ROUNDING>100</tns:ROUNDING>"+
						"<tns:KOLVO_DAYS_DO_VYDACHI>4</tns:KOLVO_DAYS_DO_VYDACHI>"+
						"<tns:HOMOGEN_PORTF_NO>37</tns:HOMOGEN_PORTF_NO>"+
						"<tns:HOMOGEN_PORTF_NAME>38</tns:HOMOGEN_PORTF_NAME>"+
						"<tns:HOMOGEN_SUBPORTF_NO>39</tns:HOMOGEN_SUBPORTF_NO>"+
						"<tns:HOMOGEN_SUBPORTF_NAME>40</tns:HOMOGEN_SUBPORTF_NAME>"+
						"<tns:CURRENT_RESERV_RATE>3.0</tns:CURRENT_RESERV_RATE>"+
						"<tns:DAYS_WAIT>"+dayWait+"</tns:DAYS_WAIT>"+
					"</tns:SEGMENT_RISK_1>"+
					"<tns:SEGMENT_RISK_4>"+
						"<tns:SPECIAL_APP>N</tns:SPECIAL_APP>"+
					"</tns:SEGMENT_RISK_4>"+
					"<tns:SEGMENT_RISK_7>"+
						"<tns:LPZ>300000.0</tns:LPZ>"+
					"</tns:SEGMENT_RISK_7>"+
				"</tns:PARAMS_RISK>"+
				"<tns:PARAMS_RISK_TB TB_CODE=\"0\"/>"+
				"<tns:PARAMS_IT>"+
					"<tns:SEGMENT_IT_1>"+
						"<tns:STOP_LIST_TIMEOUT>300</tns:STOP_LIST_TIMEOUT>"+
						"<tns:STOP_LIST_RETRY_NUM>3</tns:STOP_LIST_RETRY_NUM>"+
						"<tns:HUNTER_TIMEOUT>300</tns:HUNTER_TIMEOUT>"+
						"<tns:HUNTER_RETRY_NUM>3</tns:HUNTER_RETRY_NUM>"+
						"<tns:NATIONALHUNTERTIMEOUT>300</tns:NATIONALHUNTERTIMEOUT>"+
						"<tns:NATIONALHUNTERRETRYNUM>3</tns:NATIONALHUNTERRETRYNUM>"+
						"<tns:FPSEQ_TIMEOUT>300</tns:FPSEQ_TIMEOUT>"+
						"<tns:FPSEQ_RETRY_NUM>3</tns:FPSEQ_RETRY_NUM>"+
						"<tns:ASSD_GSZ_TIMEOUT>300</tns:ASSD_GSZ_TIMEOUT>"+
						"<tns:ASSD_GSZ_RETRY_NUM>3</tns:ASSD_GSZ_RETRY_NUM>"+
						"<tns:BACK_OFFICE_TIMEOUT>300</tns:BACK_OFFICE_TIMEOUT>"+
						"<tns:BACK_OFFICE_RETRY_NUM>3</tns:BACK_OFFICE_RETRY_NUM>"+
					"</tns:SEGMENT_IT_1>"+
					"<tns:SEGMENT_IT_61>"+
						"<tns:MBKI_TIMEOUT>300</tns:MBKI_TIMEOUT>"+
						"<tns:MBKI_RETRY_NUM>3</tns:MBKI_RETRY_NUM>"+
						"<tns:BRS_TIMEOUT>300</tns:BRS_TIMEOUT>"+
						"<tns:BRS_RETRY_NUM>3</tns:BRS_RETRY_NUM>"+
						"<tns:COD_TIMEOUT>300</tns:COD_TIMEOUT>"+
						"<tns:COD_RETRY_NUM>3</tns:COD_RETRY_NUM>"+
						"<tns:ASSD_TIMEOUT>300</tns:ASSD_TIMEOUT>"+
						"<tns:ASSD_RETRY_NUM>3</tns:ASSD_RETRY_NUM>"+
						"<tns:BKI_GP_TIMEOUT>300</tns:BKI_GP_TIMEOUT>"+
						"<tns:BKI_GP_RETRY_NUM>3</tns:BKI_GP_RETRY_NUM>"+
						"<tns:NBKI_TIMEOUT>300</tns:NBKI_TIMEOUT>"+
						"<tns:NBKI_RETRY_NUM>3</tns:NBKI_RETRY_NUM>"+
						"<tns:BKI_EI_TIMEOUT>300</tns:BKI_EI_TIMEOUT>"+
						"<tns:BKI_EI_RETRY_NUM>3</tns:BKI_EI_RETRY_NUM>"+
						"<tns:PF_TIMEOUT>300</tns:PF_TIMEOUT>"+
						"<tns:PF_RETRY_NUM>3</tns:PF_RETRY_NUM>"+
						"<tns:FMS_Timeout>300</tns:FMS_Timeout>"+
						"<tns:FMS_Retry_Num>3</tns:FMS_Retry_Num>"+
					"</tns:SEGMENT_IT_61>"+
				"</tns:PARAMS_IT>"+
				"<tns:PARAMS_SYS_TB TB_CODE=\"0\">"+
					"<tns:SEGMENT_SYS_1_TB>"+
						"<tns:STOP_LIST>Y</tns:STOP_LIST>"+
						"<tns:HUNTER>Y</tns:HUNTER>"+
						"<tns:NATIONALHUNTER>Y</tns:NATIONALHUNTER>"+
						"<tns:FPSEQ>N</tns:FPSEQ>"+
						"<tns:BRS_EGRUL>N</tns:BRS_EGRUL>"+
						"<tns:AS_SD_GZS>N</tns:AS_SD_GZS>"+
					"</tns:SEGMENT_SYS_1_TB>"+
					"<tns:SEGMENT_SYS_6_TB>"+
						"<tns:BRS>N</tns:BRS>"+
						"<tns:AS_SD>N</tns:AS_SD>"+
						"<tns:MBKI>Y</tns:MBKI>"+
						"<tns:COD>N</tns:COD>"+
						"<tns:CB_GLPAY>N</tns:CB_GLPAY>"+
						"<tns:CB_EXPINT>N</tns:CB_EXPINT>"+
						"<tns:CB_NBKI>Y</tns:CB_NBKI>"+
						"<tns:PFR>N</tns:PFR>"+
						"<tns:FMS>N</tns:FMS>"+
					"</tns:SEGMENT_SYS_6_TB>"+
				"</tns:PARAMS_SYS_TB>"+
				"<tns:PARAMS_SERVICE>"+
					"<tns:SEGMENT_SERVICE_1>"+
						"<tns:MIN_INTRATE>10.0</tns:MIN_INTRATE>"+
						"<tns:MAX_INTRATE>17.0</tns:MAX_INTRATE>"+
					"</tns:SEGMENT_SERVICE_1>"+
				"</tns:PARAMS_SERVICE>"+
				"<tns:PARAMS_TARIFF>"+
					"<tns:SEGMENT_TARIFF_1>"+
						"<tns:SUBPRODUCT_VERSION>1</tns:SUBPRODUCT_VERSION>"+
						"<tns:VALID_FROM>21042014</tns:VALID_FROM>"+
						"<tns:VALID_TO>30042015</tns:VALID_TO>"+
						"<tns:ACT_SCHEMA_CODE>8</tns:ACT_SCHEMA_CODE>"+
						"<tns:ACT_SCHEMA_NAME>777</tns:ACT_SCHEMA_NAME>"+
					"</tns:SEGMENT_TARIFF_1>"+
				"</tns:PARAMS_TARIFF>"+
			"</tns:CARD>"+
		"</tns:productInfoMessageRetail>");
		
		
		
	}
	
	public String getXml() {
		
		return this.xml;
	}
	
	public ArrayList<String> getdata() {
		
		return this.data;
	}

}
