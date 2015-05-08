package products;

import java.util.ArrayList;

import static  ru.aplana.tools.Common.generateRqUID;

/**
 * Generating xml for potreb bez obespecheniya
 * 
 * @author Maksim Stepanov
 *
 */
public class PotrebBez {

	private String xml = "";
	
	private ArrayList<String> data = new ArrayList<String>(2);
	
	
	public PotrebBez(int subproduct, String operation) {

		String RQUID = generateRqUID();
		
		int dayWait = 5+ (int) (Math.random() * ((10 - 5) +1)); // [5,10]
		
		this.data.add(RQUID);
		
		this.data.add(operation);
		
		
		this.xml = new String("<tns:productInfoMessageRetail xsi:schemaLocation=\"tns productInfoMessageRetail.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tns=\"tns\">"+
				"<tns:CONSUMER_NEW_LINE_LOAN_WITHOUT_COLLATERAL>"+
				"<tns:UID>"+RQUID+"</tns:UID>"+
				"<tns:PARAMS_BUSINESS>"+
					"<tns:SEGMENT_BUSINESS_1>"+
						"<tns:PRODUCT_TYPE>1</tns:PRODUCT_TYPE>"+
						"<tns:UNIQUEID_PRODUCT>10</tns:UNIQUEID_PRODUCT>"+
						"<tns:PRODUCT_NAME>Новая Линейка. Потребительский кредит без обеспечения</tns:PRODUCT_NAME>"+
						"<tns:UNIQUEID_SUBPRODUCT>"+subproduct+"</tns:UNIQUEID_SUBPRODUCT>"+
						"<tns:SUBPRODUCT_NAME>СЕВ_Потребительский кредит без обеспечения</tns:SUBPRODUCT_NAME>"+
						"<tns:CURRENCY>RUB</tns:CURRENCY>"+
						"<tns:INCLUDED_BRANCH>99</tns:INCLUDED_BRANCH>"+
						"<tns:EXCLUDED_BRANCH/>"+
						"<tns:ID_USLOVIY_KRED>34</tns:ID_USLOVIY_KRED>"+
						"<tns:KOD_NORM_DOC_PO_KRED_FIZLIC>330</tns:KOD_NORM_DOC_PO_KRED_FIZLIC>"+
						"<tns:TYP_PROC_STAVKI_OSN_DOLG>2</tns:TYP_PROC_STAVKI_OSN_DOLG>"+
						"<tns:TYP_PROC_STAVKI_PO_NEUSTOYKE>9</tns:TYP_PROC_STAVKI_PO_NEUSTOYKE>"+
						"<tns:TYPP_ROC_STAVKI_NEUST_PO_PROC>9</tns:TYPP_ROC_STAVKI_NEUST_PO_PROC>"+
						"<tns:PENALTY>0.5</tns:PENALTY>"+
						"<tns:NEUSTOYKA_PO_PROCENTAM>0.5</tns:NEUSTOYKA_PO_PROCENTAM>"+
						"<tns:GUARANTORS>0</tns:GUARANTORS>"+
						"<tns:CO_APPLICANT>0</tns:CO_APPLICANT>"+
						"<tns:KOD_TYP_FORM_DOG>012241153/1</tns:KOD_TYP_FORM_DOG>"+
						"<tns:federal_company>N</tns:federal_company>"+
						"<tns:COMMISSION>0.0</tns:COMMISSION>"+
						"<tns:MIN_COMISS_OBSL_SSUD>0.0</tns:MIN_COMISS_OBSL_SSUD>"+
						"<tns:MAX_COMISS_OBSL_SSUD>0.0</tns:MAX_COMISS_OBSL_SSUD>"+
						"<tns:CURRENCY_COMISS>RUB</tns:CURRENCY_COMISS>"+
					"</tns:SEGMENT_BUSINESS_1>"+
					"<tns:SEGMENT_BUSINESS_2>"+
						"<tns:NAIMENOVANIE_VIDA_KREDITA>Потребительский кредит без обеспечения</tns:NAIMENOVANIE_VIDA_KREDITA>"+
						"<tns:ANNUITY_FLAG>1</tns:ANNUITY_FLAG>"+
						"<tns:DIFF_FLAG>0</tns:DIFF_FLAG>"+
					"</tns:SEGMENT_BUSINESS_2>"+
					"<tns:SEGMENT_BUSINESS_3>"+
						"<tns:Offer>Y</tns:Offer>"+
					"</tns:SEGMENT_BUSINESS_3>"+
					"<tns:SEGMENT_BUSINESS_5>"+
						"<tns:NAIMEN_NORM_DOC_PO_KRED_FIZ_LIC>Регламент №1863</tns:NAIMEN_NORM_DOC_PO_KRED_FIZ_LIC>"+
					"</tns:SEGMENT_BUSINESS_5>"+
				      "<tns:SEGMENT_BUSINESS_14>"+
				        "<tns:Auto_loan_employees>Y</tns:Auto_loan_employees>"+
				        "<tns:Auto_loan_salary_proj_participant>Y</tns:Auto_loan_salary_proj_participant>"+
				        "<tns:Auto_loan_pensioner>Y</tns:Auto_loan_pensioner>"+
				        "<tns:Auto_loan_akkr_comp_empl>Y</tns:Auto_loan_akkr_comp_empl>"+
				        "<tns:Auto_loan_street>Y</tns:Auto_loan_street>"+
				        "<tns:DSA_Auto_loan_employees>Y</tns:DSA_Auto_loan_employees>"+
				        "<tns:DSA_Auto_loan_salary_proj_participant>Y</tns:DSA_Auto_loan_salary_proj_participant>"+
				        "<tns:DSA_Auto_loan_pensioner>Y</tns:DSA_Auto_loan_pensioner>"+
				        "<tns:DSA_Auto_loan_akkr_comp_empl>Y</tns:DSA_Auto_loan_akkr_comp_empl>"+
				        "<tns:DSA_Auto_loan_street>Y</tns:DSA_Auto_loan_street>"+
				      "</tns:SEGMENT_BUSINESS_14>"+
				"</tns:PARAMS_BUSINESS>"+
				"<tns:PARAMS_BUSINESS_TB TB_CODE=\"0\">"+
					"<tns:SEGMENT_BUSINESS_1_TB>"+
						"<tns:MIN_AMOUNT>45000.0</tns:MIN_AMOUNT>"+
						"<tns:MAX_AMOUNT>60000.0</tns:MAX_AMOUNT>"+
					"</tns:SEGMENT_BUSINESS_1_TB>"+
					"<tns:SEGMENT_BUSINESS_2_TB>"+
						"<tns:SUPLEMENTARY_CARD>0</tns:SUPLEMENTARY_CARD>"+
						"<tns:IssuanceCreditCard_MA>N</tns:IssuanceCreditCard_MA>"+
						"<tns:Params_IssuanceCreditCard_MA/>"+
						"<tns:IssuanceCreditCard_CBR>N</tns:IssuanceCreditCard_CBR>"+
						"<tns:Params_IssuanceCreditCard_CBR/>"+
						"<tns:IssuanceOfaNewDepositAccount>N</tns:IssuanceOfaNewDepositAccount>"+
						"<tns:IssuanceOfanExistingDepositAccount>N</tns:IssuanceOfanExistingDepositAccount>"+
						"<tns:IssuanceOfaNewDebitCard>N</tns:IssuanceOfaNewDebitCard>"+
						"<tns:IssuanceOfanExistingDebitCard>N</tns:IssuanceOfanExistingDebitCard>"+
						"<tns:IssuanceOfaNewCurrentAccount>N</tns:IssuanceOfaNewCurrentAccount>"+
						"<tns:IssuanceOfanExistingCurrentAccount>N</tns:IssuanceOfanExistingCurrentAccount>"+
					"</tns:SEGMENT_BUSINESS_2_TB>"+
					"<tns:SEGMENT_BUSINESS_6_TB>"+
						"<tns:MAX_AMOUNT_D>50000.0</tns:MAX_AMOUNT_D>"+
					"</tns:SEGMENT_BUSINESS_6_TB>"+
					"<tns:SEGMENT_BUSINESS_11_TB>"+
						"<tns:MAX_AMOUNT_A1>50000.0</tns:MAX_AMOUNT_A1>"+
						"<tns:MAX_AMOUNT_A>50000.0</tns:MAX_AMOUNT_A>"+
						"<tns:MAX_AMOUNT_B>50000.0</tns:MAX_AMOUNT_B>"+
						"<tns:MAX_AMOUNT_C>50000.0</tns:MAX_AMOUNT_C>"+
						"<tns:MAX_AMOUNT_C1>50000.0</tns:MAX_AMOUNT_C1>"+
						"<tns:MAX_AMOUNT_D1>50000.0</tns:MAX_AMOUNT_D1>"+
						"<tns:MAX_AMOUNT_D2>50000.0</tns:MAX_AMOUNT_D2>"+
						"<tns:Max_Amount_E>50000.0</tns:Max_Amount_E>"+
					"</tns:SEGMENT_BUSINESS_11_TB>"+
				"</tns:PARAMS_BUSINESS_TB>"+
				"<tns:PARAMS_RISK>"+
					"<tns:SEGMENT_RISK_1>"+
						"<tns:MAX_REVISION_NUMBER>7</tns:MAX_REVISION_NUMBER>"+
						"<tns:APPROVAL_VALIDITY>30</tns:APPROVAL_VALIDITY>"+
						"<tns:DECLINE_SEARCH>60</tns:DECLINE_SEARCH>"+
						"<tns:UNDWRT2_LIMIT>2000000</tns:UNDWRT2_LIMIT>"+
						"<tns:UNDWRT3_LIMIT>750000</tns:UNDWRT3_LIMIT>"+
						"<tns:ROUNDING>1000</tns:ROUNDING>"+
						"<tns:KOLVO_DAYS_DO_VYDACHI>0</tns:KOLVO_DAYS_DO_VYDACHI>"+
						"<tns:HOMOGEN_PORTF_NO>12400</tns:HOMOGEN_PORTF_NO>"+
						"<tns:HOMOGEN_PORTF_NAME>Неотложные нужды и прочее</tns:HOMOGEN_PORTF_NAME>"+
						"<tns:HOMOGEN_SUBPORTF_NO>12400</tns:HOMOGEN_SUBPORTF_NO>"+
						"<tns:HOMOGEN_SUBPORTF_NAME>Неотложные нужды и прочее</tns:HOMOGEN_SUBPORTF_NAME>"+
						"<tns:CURRENT_RESERV_RATE>1.5</tns:CURRENT_RESERV_RATE>"+
						"<tns:DAYS_WAIT>"+dayWait+"</tns:DAYS_WAIT>"+
					"</tns:SEGMENT_RISK_1>"+
					"<tns:SEGMENT_RISK_4>"+
						"<tns:SPECIAL_APP>N</tns:SPECIAL_APP>"+
					"</tns:SEGMENT_RISK_4>"+
					"<tns:SEGMENT_RISK_5>"+
						"<tns:CSKO_level_of_competence>0</tns:CSKO_level_of_competence>"+
					"</tns:SEGMENT_RISK_5>"+
				"</tns:PARAMS_RISK>"+
				"<tns:PARAMS_RISK_TB TB_CODE=\"0\">"+
					"<tns:SEGMENT_RISK_2_TB>"+
						"<tns:FLAG_RBP>Y</tns:FLAG_RBP>"+
					"</tns:SEGMENT_RISK_2_TB>"+
					"<tns:SEGMENT_RISK_3_TB>"+
						"<tns:NPL_LZPK_GRA>3000000.0</tns:NPL_LZPK_GRA>"+
						"<tns:NPL_LZPK_GRA1>3000000.0</tns:NPL_LZPK_GRA1>"+
						"<tns:NPL_LZPK_GRB>3000000.0</tns:NPL_LZPK_GRB>"+
						"<tns:NPL_LZPK_GRC>3000000.0</tns:NPL_LZPK_GRC>"+
						"<tns:NPL_LZPK_GRC1>3000000.0</tns:NPL_LZPK_GRC1>"+
						"<tns:NPL_LZPK_GRD1>3000000.0</tns:NPL_LZPK_GRD1>"+
						"<tns:NPL_LZPK_GRD2>3000000.0</tns:NPL_LZPK_GRD2>"+
						"<tns:NPL_LZPK_GRE>3000000.0</tns:NPL_LZPK_GRE>"+
						"<tns:NPL_LZP_GRA>1500000.0</tns:NPL_LZP_GRA>"+
						"<tns:NPL_LZP_GRA1>1500000.0</tns:NPL_LZP_GRA1>"+
						"<tns:NPL_LZP_GRB>1500000.0</tns:NPL_LZP_GRB>"+
						"<tns:NPL_LZP_GRC>1500000.0</tns:NPL_LZP_GRC>"+
						"<tns:NPL_LZP_GRC1>1500000.0</tns:NPL_LZP_GRC1>"+
						"<tns:NPL_LZP_GRD1>1500000.0</tns:NPL_LZP_GRD1>"+
						"<tns:NPL_LZP_GRD2>1500000.0</tns:NPL_LZP_GRD2>"+
						"<tns:NPL_LZP_GRE>1500000.0</tns:NPL_LZP_GRE>"+
					"</tns:SEGMENT_RISK_3_TB>"+
					"<tns:SEGMENT_RISK_8_TB>"+
						"<tns:NPL_LZPK_GRD>3000000.0</tns:NPL_LZPK_GRD>"+
						"<tns:NPL_LZP_GRD>1500000.0</tns:NPL_LZP_GRD>"+
					"</tns:SEGMENT_RISK_8_TB>"+
				"</tns:PARAMS_RISK_TB>"+
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
					"<tns:SEGMENT_IT_2>"+
						"<tns:SPOOBK_Timeout>300</tns:SPOOBK_Timeout>"+
						"<tns:SPOOBK_Retry_Num>3</tns:SPOOBK_Retry_Num>"+
					"</tns:SEGMENT_IT_2>"+
					"<tns:SEGMENT_IT_3>"+
						"<tns:MBKI_TIMEOUT_GRD>300</tns:MBKI_TIMEOUT_GRD>"+
						"<tns:MBKI_RETRY_NUM_GRD>3</tns:MBKI_RETRY_NUM_GRD>"+
						"<tns:BRS_TIMEOUT_GRD>300</tns:BRS_TIMEOUT_GRD>"+
						"<tns:BRS_RETRY_NUM_GRD>3</tns:BRS_RETRY_NUM_GRD>"+
						"<tns:COD_TIMEOUT_GRD>300</tns:COD_TIMEOUT_GRD>"+
						"<tns:COD_RETRY_NUM_GRD>3</tns:COD_RETRY_NUM_GRD>"+
						"<tns:ASSD_TIMEOUT_GRD>300</tns:ASSD_TIMEOUT_GRD>"+
						"<tns:ASSD_RETRY_NUM_GRD>3</tns:ASSD_RETRY_NUM_GRD>"+
						"<tns:BKI_GP_TIMEOUT_GRD>300</tns:BKI_GP_TIMEOUT_GRD>"+
						"<tns:BKI_GP_RETRY_NUM_GRD>3</tns:BKI_GP_RETRY_NUM_GRD>"+
						"<tns:NBKI_TIMEOUT_GRD>300</tns:NBKI_TIMEOUT_GRD>"+
						"<tns:NBKI_RETRY_NUM_GRD>3</tns:NBKI_RETRY_NUM_GRD>"+
						"<tns:BKI_EI_TIMEOUT_GRD>300</tns:BKI_EI_TIMEOUT_GRD>"+
						"<tns:BKI_EI_RETRY_NUM_GRD>3</tns:BKI_EI_RETRY_NUM_GRD>"+
						"<tns:PF_TIMEOUT_GRD>300</tns:PF_TIMEOUT_GRD>"+
						"<tns:PF_RETRY_NUM_GRD>3</tns:PF_RETRY_NUM_GRD>"+
						"<tns:FMS_TIMEOUT_GRD>300</tns:FMS_TIMEOUT_GRD>"+
						"<tns:FMS_RETRY_NUM_GROUP_D>3</tns:FMS_RETRY_NUM_GROUP_D>"+
					"</tns:SEGMENT_IT_3>"+
					"<tns:SEGMENT_IT_4>"+
						"<tns:MBKI_TIMEOUT_GRA>300</tns:MBKI_TIMEOUT_GRA>"+
						"<tns:MBKI_RETRY_NUM_GRA>3</tns:MBKI_RETRY_NUM_GRA>"+
						"<tns:MBKI_TIMEOUT_GRB>300</tns:MBKI_TIMEOUT_GRB>"+
						"<tns:MBKI_RETRY_NUM_GRB>3</tns:MBKI_RETRY_NUM_GRB>"+
						"<tns:MBKI_TIMEOUT_GRC>300</tns:MBKI_TIMEOUT_GRC>"+
						"<tns:MBKI_RETRY_NUM_GRC>3</tns:MBKI_RETRY_NUM_GRC>"+
						"<tns:MBKI_TIMEOUT_GRE>300</tns:MBKI_TIMEOUT_GRE>"+
						"<tns:MBKI_RETRY_NUM_GRE>3</tns:MBKI_RETRY_NUM_GRE>"+
						"<tns:BRS_TIMEOUT_GRA>300</tns:BRS_TIMEOUT_GRA>"+
						"<tns:BRS_RETRY_NUM_GRA>3</tns:BRS_RETRY_NUM_GRA>"+
						"<tns:BRS_TIMEOUT_GRB>300</tns:BRS_TIMEOUT_GRB>"+
						"<tns:BRS_RETRY_NUM_GRB>3</tns:BRS_RETRY_NUM_GRB>"+
						"<tns:BRS_TIMEOUT_GRC>300</tns:BRS_TIMEOUT_GRC>"+
						"<tns:BRS_RETRY_NUM_GRC>3</tns:BRS_RETRY_NUM_GRC>"+
						"<tns:BRS_TIMEOUT_GRE>300</tns:BRS_TIMEOUT_GRE>"+
						"<tns:BRS_RETRY_NUM_GRE>3</tns:BRS_RETRY_NUM_GRE>"+
						"<tns:COD_TIMEOUT_GRA>300</tns:COD_TIMEOUT_GRA>"+
						"<tns:COD_RETRY_NUM_GRA>3</tns:COD_RETRY_NUM_GRA>"+
						"<tns:COD_TIMEOUT_GRB>300</tns:COD_TIMEOUT_GRB>"+
						"<tns:COD_RETRY_NUM_GRB>3</tns:COD_RETRY_NUM_GRB>"+
						"<tns:COD_TIMEOUT_GRC>300</tns:COD_TIMEOUT_GRC>"+
						"<tns:COD_RETRY_NUM_GRC>3</tns:COD_RETRY_NUM_GRC>"+
						"<tns:COD_TIMEOUT_GRE>300</tns:COD_TIMEOUT_GRE>"+
						"<tns:COD_RETRY_NUM_GRE>3</tns:COD_RETRY_NUM_GRE>"+
						"<tns:ASSD_TIMEOUT_GRA>300</tns:ASSD_TIMEOUT_GRA>"+
						"<tns:ASSD_RETRY_NUM_GRA>3</tns:ASSD_RETRY_NUM_GRA>"+
						"<tns:ASSD_TIMEOUT_GRB>300</tns:ASSD_TIMEOUT_GRB>"+
						"<tns:ASSD_RETRY_NUM_GRB>3</tns:ASSD_RETRY_NUM_GRB>"+
						"<tns:ASSD_TIMEOUT_GRC>300</tns:ASSD_TIMEOUT_GRC>"+
						"<tns:ASSD_RETRY_NUM_GRC>3</tns:ASSD_RETRY_NUM_GRC>"+
						"<tns:ASSD_TIMEOUT_GRE>300</tns:ASSD_TIMEOUT_GRE>"+
						"<tns:ASSD_RETRY_NUM_GRE>3</tns:ASSD_RETRY_NUM_GRE>"+
						"<tns:BKI_GP_TIMEOUT_GRA>300</tns:BKI_GP_TIMEOUT_GRA>"+
						"<tns:BKI_GP_RETRY_NUM_GRA>3</tns:BKI_GP_RETRY_NUM_GRA>"+
						"<tns:BKI_GP_TIMEOUT_GRB>300</tns:BKI_GP_TIMEOUT_GRB>"+
						"<tns:BKI_GP_RETRY_NUM_GRB>3</tns:BKI_GP_RETRY_NUM_GRB>"+
						"<tns:BKI_GP_TIMEOUT_GRC>300</tns:BKI_GP_TIMEOUT_GRC>"+
						"<tns:BKI_GP_RETRY_NUM_GRC>3</tns:BKI_GP_RETRY_NUM_GRC>"+
						"<tns:BKI_GP_TIMEOUT_GRE>300</tns:BKI_GP_TIMEOUT_GRE>"+
						"<tns:BKI_GP_RETRY_NUM_GRE>3</tns:BKI_GP_RETRY_NUM_GRE>"+
						"<tns:NBKI_TIMEOUT_GRA>300</tns:NBKI_TIMEOUT_GRA>"+
						"<tns:NBKI_RETRY_NUM_GRA>3</tns:NBKI_RETRY_NUM_GRA>"+
						"<tns:NBKI_TIMEOUT_GRB>300</tns:NBKI_TIMEOUT_GRB>"+
						"<tns:NBKI_RETRY_NUM_GRB>3</tns:NBKI_RETRY_NUM_GRB>"+
						"<tns:NBKI_TIMEOUT_GRC>300</tns:NBKI_TIMEOUT_GRC>"+
						"<tns:NBKI_RETRY_NUM_GRC>3</tns:NBKI_RETRY_NUM_GRC>"+
						"<tns:NBKI_TIMEOUT_GRE>300</tns:NBKI_TIMEOUT_GRE>"+
						"<tns:NBKI_RETRY_NUM_GRE>3</tns:NBKI_RETRY_NUM_GRE>"+
						"<tns:BKI_EI_TIMEOUT_GRA>300</tns:BKI_EI_TIMEOUT_GRA>"+
						"<tns:BKI_EI_RETRY_NUM_GRA>3</tns:BKI_EI_RETRY_NUM_GRA>"+
						"<tns:BKI_EI_TIMEOUT_GRB>300</tns:BKI_EI_TIMEOUT_GRB>"+
						"<tns:BKI_EI_RETRY_NUM_GRB>3</tns:BKI_EI_RETRY_NUM_GRB>"+
						"<tns:BKI_EI_TIMEOUT_GRC>300</tns:BKI_EI_TIMEOUT_GRC>"+
						"<tns:BKI_EI_RETRY_NUM_GRC>3</tns:BKI_EI_RETRY_NUM_GRC>"+
						"<tns:BKI_EI_TIMEOUT_GRE>300</tns:BKI_EI_TIMEOUT_GRE>"+
						"<tns:BKI_EI_RETRY_NUM_GRE>3</tns:BKI_EI_RETRY_NUM_GRE>"+
						"<tns:PF_TIMEOUT_GRA>300</tns:PF_TIMEOUT_GRA>"+
						"<tns:PF_RETRY_NUM_GRA>3</tns:PF_RETRY_NUM_GRA>"+
						"<tns:PF_TIMEOUT_GRB>300</tns:PF_TIMEOUT_GRB>"+
						"<tns:PF_RETRY_NUM_GRB>3</tns:PF_RETRY_NUM_GRB>"+
						"<tns:PF_TIMEOUT_GRC>300</tns:PF_TIMEOUT_GRC>"+
						"<tns:PF_RETRY_NUM_GRC>3</tns:PF_RETRY_NUM_GRC>"+
						"<tns:PF_TIMEOUT_GRE>300</tns:PF_TIMEOUT_GRE>"+
						"<tns:PF_RETRY_NUM_GRE>3</tns:PF_RETRY_NUM_GRE>"+
						"<tns:FMS_TIMEOUT_GRA>300</tns:FMS_TIMEOUT_GRA>"+
						"<tns:FMS_RETRY_NUM_GROUP_A>3</tns:FMS_RETRY_NUM_GROUP_A>"+
						"<tns:FMS_TIMEOUT_GRB>300</tns:FMS_TIMEOUT_GRB>"+
						"<tns:FMS_RETRY_NUM_GROUP_B>3</tns:FMS_RETRY_NUM_GROUP_B>"+
						"<tns:FMS_TIMEOUT_GRC>300</tns:FMS_TIMEOUT_GRC>"+
						"<tns:FMS_RETRY_NUM_GROUP_C>3</tns:FMS_RETRY_NUM_GROUP_C>"+
						"<tns:FMS_TIMEOUT_GRE>300</tns:FMS_TIMEOUT_GRE>"+
						"<tns:FMS_RETRY_NUM_GROUP_E>3</tns:FMS_RETRY_NUM_GROUP_E>"+
					"</tns:SEGMENT_IT_4>"+
				"</tns:PARAMS_IT>"+
				"<tns:PARAMS_SYS_TB TB_CODE=\"0\">"+
					"<tns:SEGMENT_SYS_1_TB>"+
						"<tns:STOP_LIST>Y</tns:STOP_LIST>"+
						"<tns:HUNTER>Y</tns:HUNTER>"+
						"<tns:NATIONALHUNTER>N</tns:NATIONALHUNTER>"+
						"<tns:FPSEQ>N</tns:FPSEQ>"+
						"<tns:BRS_EGRUL>N</tns:BRS_EGRUL>"+
						"<tns:AS_SD_GZS>Y</tns:AS_SD_GZS>"+
					"</tns:SEGMENT_SYS_1_TB>"+
					"<tns:SEGMENT_SYS_2_TB>"+
						"<tns:SPOOBK>N</tns:SPOOBK>"+
					"</tns:SEGMENT_SYS_2_TB>"+
					"<tns:SEGMENT_SYS_4_TB>"+
						"<tns:MBKI_GRD>N</tns:MBKI_GRD>"+
						"<tns:BRS_GRD>Y</tns:BRS_GRD>"+
						"<tns:COD_GRD>Y</tns:COD_GRD>"+
						"<tns:AS_SD_GRD>Y</tns:AS_SD_GRD>"+
						"<tns:CB_GLPAY_GRD>Y</tns:CB_GLPAY_GRD>"+
						"<tns:CB_NBKI_GRD>N</tns:CB_NBKI_GRD>"+
						"<tns:CB_EXPINT_GRD>Y</tns:CB_EXPINT_GRD>"+
						"<tns:PFR_GRD>Y</tns:PFR_GRD>"+
						"<tns:FMS_GRD>N</tns:FMS_GRD>"+
					"</tns:SEGMENT_SYS_4_TB>"+
					"<tns:SEGMENT_SYS_5_TB>"+
						"<tns:MBKI_GRA>N</tns:MBKI_GRA>"+
						"<tns:MBKI_GRB>N</tns:MBKI_GRB>"+
						"<tns:MBKI_GRC>N</tns:MBKI_GRC>"+
						"<tns:MBKI_GRE>N</tns:MBKI_GRE>"+
						"<tns:BRS_GRA>Y</tns:BRS_GRA>"+
						"<tns:BRS_GRB>Y</tns:BRS_GRB>"+
						"<tns:BRS_GRC>Y</tns:BRS_GRC>"+
						"<tns:BRS_GRE>Y</tns:BRS_GRE>"+
						"<tns:COD_GRA>Y</tns:COD_GRA>"+
						"<tns:COD_GRB>Y</tns:COD_GRB>"+
						"<tns:COD_GRC>Y</tns:COD_GRC>"+
						"<tns:COD_GRE>Y</tns:COD_GRE>"+
						"<tns:AS_SD_GRA>Y</tns:AS_SD_GRA>"+
						"<tns:AS_SD_GRB>Y</tns:AS_SD_GRB>"+
						"<tns:AS_SD_GRC>Y</tns:AS_SD_GRC>"+
						"<tns:AS_SD_GRE>Y</tns:AS_SD_GRE>"+
						"<tns:CB_GLPAY_GRA>Y</tns:CB_GLPAY_GRA>"+
						"<tns:CB_GLPAY_GRB>Y</tns:CB_GLPAY_GRB>"+
						"<tns:CB_GLPAY_GRC>Y</tns:CB_GLPAY_GRC>"+
						"<tns:CB_GLPAY_GRE>Y</tns:CB_GLPAY_GRE>"+
						"<tns:CB_NBKI_GRA>N</tns:CB_NBKI_GRA>"+
						"<tns:CB_NBKI_GRB>N</tns:CB_NBKI_GRB>"+
						"<tns:CB_NBKI_GRC>N</tns:CB_NBKI_GRC>"+
						"<tns:CB_NBKI_GRE>N</tns:CB_NBKI_GRE>"+
						"<tns:CB_EXPINT_GRA>Y</tns:CB_EXPINT_GRA>"+
						"<tns:CB_EXPINT_GRB>Y</tns:CB_EXPINT_GRB>"+
						"<tns:CB_EXPINT_GRC>Y</tns:CB_EXPINT_GRC>"+
						"<tns:CB_EXPINT_GRE>Y</tns:CB_EXPINT_GRE>"+
						"<tns:PFR_GRA>Y</tns:PFR_GRA>"+
						"<tns:PFR_GRB>Y</tns:PFR_GRB>"+
						"<tns:PFR_GRC>Y</tns:PFR_GRC>"+
						"<tns:PFR_GRE>Y</tns:PFR_GRE>"+
						"<tns:FMS_GRA>N</tns:FMS_GRA>"+
						"<tns:FMS_GRB>N</tns:FMS_GRB>"+
						"<tns:FMS_GRC>N</tns:FMS_GRC>"+
						"<tns:FMS_GRE>N</tns:FMS_GRE>"+
					"</tns:SEGMENT_SYS_5_TB>"+
				"</tns:PARAMS_SYS_TB>"+
				"<tns:PARAMS_SERVICE>"+
					"<tns:SEGMENT_SERVICE_1>"+
						"<tns:MIN_INTRATE>18.6</tns:MIN_INTRATE>"+
						"<tns:MAX_INTRATE>20.9</tns:MAX_INTRATE>"+
					"</tns:SEGMENT_SERVICE_1>"+
				"</tns:PARAMS_SERVICE>"+
				"<tns:PARAMS_TARIFF>"+
					"<tns:SEGMENT_TARIFF_1>"+
						"<tns:SUBPRODUCT_VERSION>1</tns:SUBPRODUCT_VERSION>"+
						"<tns:VALID_FROM>21042014</tns:VALID_FROM>"+
						"<tns:VALID_TO>01042099</tns:VALID_TO>"+
						"<tns:ACT_SCHEMA_CODE>0</tns:ACT_SCHEMA_CODE>"+
						"<tns:ACT_SCHEMA_NAME>0</tns:ACT_SCHEMA_NAME>"+
					"</tns:SEGMENT_TARIFF_1>"+
					"<tns:SEGMENT_TARIFF_2>"+
						"<tns:MIN_TERM>3</tns:MIN_TERM>"+
						"<tns:MAX_TERM>60</tns:MAX_TERM>"+
					"</tns:SEGMENT_TARIFF_2>"+
					"<tns:SEGMENT_TARIFF_3>"+
						"<tns:CREDIT_RANGE_1_MONTHS>12</tns:CREDIT_RANGE_1_MONTHS>"+
						"<tns:CREDIT_RANGE_2_MONTHS>36</tns:CREDIT_RANGE_2_MONTHS>"+
					"</tns:SEGMENT_TARIFF_3>"+
					"<tns:SEGMENT_TARIFF_4>"+
						"<tns:PL_GRD_BASERATE_R0>21.0</tns:PL_GRD_BASERATE_R0>"+
						"<tns:PL_GRD_BASERATE_R1>21.0</tns:PL_GRD_BASERATE_R1>"+
						"<tns:PL_GRD_BASERATE_R2>21.0</tns:PL_GRD_BASERATE_R2>"+
						"<tns:PL_GRD_GKH_R0>20.5</tns:PL_GRD_GKH_R0>"+
						"<tns:PL_GRD_GKH_R1>20.5</tns:PL_GRD_GKH_R1>"+
						"<tns:PL_GRD_GKH_R2>20.5</tns:PL_GRD_GKH_R2>"+
					"</tns:SEGMENT_TARIFF_4>"+
					"<tns:SEGMENT_TARIFF_5>"+
						"<tns:PL_GRA_BASERATE_R0>16.8</tns:PL_GRA_BASERATE_R0>"+
						"<tns:PL_GRA_BASERATE_R1>16.8</tns:PL_GRA_BASERATE_R1>"+
						"<tns:PL_GRA_BASERATE_R2>16.8</tns:PL_GRA_BASERATE_R2>"+
						"<tns:PL_GRA1_BASERATE_R0>16.8</tns:PL_GRA1_BASERATE_R0>"+
						"<tns:PL_GRA1_BASERATE_R1>16.8</tns:PL_GRA1_BASERATE_R1>"+
						"<tns:PL_GRA1_BASERATE_R2>16.8</tns:PL_GRA1_BASERATE_R2>"+
						"<tns:PL_GRB_BASERATE_R0>18.9</tns:PL_GRB_BASERATE_R0>"+
						"<tns:PL_GRB_BASERATE_R1>18.9</tns:PL_GRB_BASERATE_R1>"+
						"<tns:PL_GRB_BASERATE_R2>18.9</tns:PL_GRB_BASERATE_R2>"+
						"<tns:PL_GRC_BASERATE_R0>19.95</tns:PL_GRC_BASERATE_R0>"+
						"<tns:PL_GRC_BASERATE_R1>19.95</tns:PL_GRC_BASERATE_R1>"+
						"<tns:PL_GRC_BASERATE_R2>19.95</tns:PL_GRC_BASERATE_R2>"+
						"<tns:PL_GRC1_BASERATE_R0>18.9</tns:PL_GRC1_BASERATE_R0>"+
						"<tns:PL_GRC1_BASERATE_R1>18.9</tns:PL_GRC1_BASERATE_R1>"+
						"<tns:PL_GRC1_BASERATE_R2>18.9</tns:PL_GRC1_BASERATE_R2>"+
						"<tns:PL_GRD1_BASERATE_R0>21.15</tns:PL_GRD1_BASERATE_R0>"+
						"<tns:PL_GRD1_BASERATE_R1>21.15</tns:PL_GRD1_BASERATE_R1>"+
						"<tns:PL_GRD1_BASERATE_R2>21.15</tns:PL_GRD1_BASERATE_R2>"+
						"<tns:PL_GRD2_BASERATE_R0>21.4</tns:PL_GRD2_BASERATE_R0>"+
						"<tns:PL_GRD2_BASERATE_R1>21.4</tns:PL_GRD2_BASERATE_R1>"+
						"<tns:PL_GRD2_BASERATE_R2>21.4</tns:PL_GRD2_BASERATE_R2>"+
						"<tns:PL_GRE_BASERATE_R0>21.4</tns:PL_GRE_BASERATE_R0>"+
						"<tns:PL_GRE_BASERATE_R1>21.4</tns:PL_GRE_BASERATE_R1>"+
						"<tns:PL_GRE_BASERATE_R2>21.4</tns:PL_GRE_BASERATE_R2>"+
						"<tns:PL_GRA_GKH_R0>16.8</tns:PL_GRA_GKH_R0>"+
						"<tns:PL_GRA_GKH_R1>16.8</tns:PL_GRA_GKH_R1>"+
						"<tns:PL_GRA_GKH_R2>16.8</tns:PL_GRA_GKH_R2>"+
						"<tns:PL_GRA1_GKH_R0>16.8</tns:PL_GRA1_GKH_R0>"+
						"<tns:PL_GRA1_GKH_R1>16.8</tns:PL_GRA1_GKH_R1>"+
						"<tns:PL_GRA1_GKH_R2>16.8</tns:PL_GRA1_GKH_R2>"+
						"<tns:PL_GRB_GKH_R0>18.45</tns:PL_GRB_GKH_R0>"+
						"<tns:PL_GRB_GKH_R1>18.45</tns:PL_GRB_GKH_R1>"+
						"<tns:PL_GRB_GKH_R2>18.45</tns:PL_GRB_GKH_R2>"+
						"<tns:PL_GRC_GKH_R0>19.5</tns:PL_GRC_GKH_R0>"+
						"<tns:PL_GRC_GKH_R1>19.5</tns:PL_GRC_GKH_R1>"+
						"<tns:PL_GRC_GKH_R2>19.5</tns:PL_GRC_GKH_R2>"+
						"<tns:PL_GRC1_GKH_R0>18.45</tns:PL_GRC1_GKH_R0>"+
						"<tns:PL_GRC1_GKH_R1>18.45</tns:PL_GRC1_GKH_R1>"+
						"<tns:PL_GRC1_GKH_R2>18.45</tns:PL_GRC1_GKH_R2>"+
						"<tns:PL_GRD1_GKH_R0>20.65</tns:PL_GRD1_GKH_R0>"+
						"<tns:PL_GRD1_GKH_R1>20.65</tns:PL_GRD1_GKH_R1>"+
						"<tns:PL_GRD1_GKH_R2>20.65</tns:PL_GRD1_GKH_R2>"+
						"<tns:PL_GRD2_GKH_R0>20.9</tns:PL_GRD2_GKH_R0>"+
						"<tns:PL_GRD2_GKH_R1>20.9</tns:PL_GRD2_GKH_R1>"+
						"<tns:PL_GRD2_GKH_R2>20.9</tns:PL_GRD2_GKH_R2>"+
						"<tns:PL_GRE_GKH_R0>21.4</tns:PL_GRE_GKH_R0>"+
						"<tns:PL_GRE_GKH_R1>21.4</tns:PL_GRE_GKH_R1>"+
						"<tns:PL_GRE_GKH_R2>21.4</tns:PL_GRE_GKH_R2>"+
					"</tns:SEGMENT_TARIFF_5>"+
				"</tns:PARAMS_TARIFF>"+
			"</tns:CONSUMER_NEW_LINE_LOAN_WITHOUT_COLLATERAL>"+
		"</tns:productInfoMessageRetail>");
		
		
		
	}
	
	public String getXml() {
		
		return this.xml;
	}
	
	public ArrayList<String> getdata() {
		
		return this.data;
	}

}
