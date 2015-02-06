package answers;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.aplana.tools.Common.generateNumber;
import ru.aplana.tools.RandomCreditCardNumberGenerator;

/**
 * Create request to TSM 
 * 
 * @author Maksim Stepanov
 *
 */
public class ReqToETSM {
	
	private String response = null;
	
	// mod 3 must go to return credit inspector.
	private static AtomicInteger countCalls = new AtomicInteger(0);
	
	public ReqToETSM(ArrayList<String> data) {
		
		
		String incoming = null;
		
		String incomingFamily = null;
		
		String flat = null;
		
		String vehicle = null;
		
		if ((countCalls.getAndIncrement()) % 3 == 0) {
			
			incoming = new String("1");
			
			incomingFamily = new String("1");
			
			flat = new String("false");
			
			vehicle = new String("false");
			
		} else {
			
			incoming = new String("150000");
			
			incomingFamily = new String("150000");
			
			flat = new String("true");
			
			vehicle = new String("true");
			
		}
		
		// generating ID and serial
		String id = generateNumber(6);
		
		String serial = generateNumber(4);
		
		String city = null;
		
		String street= null;
		
		String ufms= null;

		String sberFull = null;
		
		String mother = null;
		
		String dep = null;
		
		String pos = null;
		
		String autoNum = null;
		
		String firstname = null;
		
		String lastname = null;
		
		String middlename = null;
		
		String birthday = null;
		
		String dateId = null;
		
		String cardNum = null;
		
		String sbflag = null;
		
		String unit = null;
		
		String channel = null;
		
		city = new String("Москва");
		 
		street = new String("Ленина");
		 
		ufms = new String("УФМС Москвы");
		 
		sberFull = new String("ЗАО Сбербанк");
		 
		mother = new String("Мама");
		 
		dep = new String("УВИСАС");
		 
		pos = new String("Инженер");
		 
		autoNum = new String("а345вв");
			 
		firstname = new String(data.get(7));
		
		lastname = new String(data.get(6));
			
		sbflag = new String(data.get(12));
			
		middlename = new String(data.get(8));

		birthday = new String(data.get(9));
		
		dateId = new String(data.get(10));
		
		unit = new String(data.get(13));
		
		channel = new String(data.get(14));
		
		cardNum = RandomCreditCardNumberGenerator.generateVisaCardNumber();
			 
		this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
		"<ChargeLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplicationTSM.xsd\">"+
		  "<MessageData>"+
		    "<RqUID>"+data.get(0)+"</RqUID>"+
		    "<RqTm>"+data.get(1)+"</RqTm>"+
		    "<FromAbonent>"+data.get(3)+"</FromAbonent>"+
		  "</MessageData>"+
		  "<Application>"+
		    "<SrcObjID>"+data.get(2)+"</SrcObjID>"+
		    "<Unit>"+unit+"</Unit>"+
		    "<CompleteAppFlag>true</CompleteAppFlag>"+
		    "<Channel>"+channel+"</Channel>"+
		    "<ChannelCBRegAApprove>2</ChannelCBRegAApprove>"+
		    "<ChannelPFRRegAApprove>2</ChannelPFRRegAApprove>"+
		    "<Product>"+
	            "<Type>1</Type>"+
	            "<Code>" + data.get(4) + "</Code>"+
	            "<SubProductCode>" + data.get(5) + "</SubProductCode>"+
	            "<Amount>430000</Amount>"+
	            "<Period>24</Period>"+
	            "<Currency>RUB</Currency>"+
	            "<PaymentType>Annuity</PaymentType>"+
	            "<InterestRate>17</InterestRate>"+
		    "</Product>"+
		    "<Applicant>"+
		      "<Type>MainDebitor</Type>"+
		      "<PersonInfo>"+
		        "<PersonName>"+
		          	"<LastName>"+ lastname +"</LastName>"+
	      			"<FirstName>"+ firstname +"</FirstName>"+
	    			"<MiddleName>"+ middlename +"</MiddleName>"+
		        "</PersonName>"+
		        "<NameChangedFlag>false</NameChangedFlag>"+
		        "<Sex>0</Sex>"+
		        "<Birthday>"+birthday+"</Birthday>"+
		        "<BirthPlace>"+city+"</BirthPlace>"+
		        "<Education>"+
		          "<Status>5</Status>"+
		          "<UnfinishedCourse>0</UnfinishedCourse>"+
		        "</Education>"+
		        "<Contact>"+
		        	"<EmailAddr>EmailAddr@dddd.ru</EmailAddr>"+
			          "<PhoneList>"+
			             "<Phone>"+
		                    "<Type>1</Type>"+
		                    "<CountryPrefix>7</CountryPrefix>"+
		                    "<Prefix>916</Prefix>"+
		                    "<Number>3777441</Number>"+
		                "</Phone>"+
		                "<Phone>"+
		               		"<Type>2</Type>"+
		               		"<CountryPrefix>7</CountryPrefix>"+
		               		"<Prefix>495</Prefix>"+
		               		"<Number>2659898</Number>"+
		               	"</Phone>"+			
		                "<Phone>"+
		               		"<Type>3</Type>"+
		               		"<CountryPrefix>7</CountryPrefix>"+
		               		"<Prefix>495</Prefix>"+
		               		"<Number>2659898</Number>"+
		               	"</Phone>"+
		                "<Phone>"+
		               		"<Type>4</Type>"+
		               		"<CountryPrefix>7</CountryPrefix>"+
		               		"<Prefix>495</Prefix>"+
		               		"<Number>2645698</Number>"+
		               	"</Phone>"+
			          "</PhoneList>"+
		          "<AddressList>"+
		            "<ResidenceEqualFlag>true</ResidenceEqualFlag>"+
	                "<CityResidencePeriod>10</CityResidencePeriod>"+
	                "<ActResidencePeriod>10</ActResidencePeriod>"+
	                "<ResidenceRight>0</ResidenceRight>"+
	                "<Address>"+
                    "<ManualInputFlag>false</ManualInputFlag>"+
                    "<AddrType>1</AddrType>"+
                    "<PostalCode>143350</PostalCode>"+
                    "<RegionCode>0077</RegionCode>"+
                    "<Area></Area>"+
                    "<CityType>301</CityType>"+
                    "<City>"+city+"</City>"+
                    "<SettlementType>495</SettlementType>"+
                    "<Settlement>"+city+"</Settlement>"+
                    "<StreetType>518</StreetType>"+
                    "<Street>"+street+"</Street>"+
                    "<HouseNum>1</HouseNum>"+
                    "<HouseExt>1</HouseExt>"+
                    "<Unit></Unit>"+
                    "<UnitNum>23</UnitNum>"+
                    "</Address>"+
		            "<Address>"+
                    "<ManualInputFlag>false</ManualInputFlag>"+
                    "<AddrType>2</AddrType>"+
                    "<PostalCode>143350</PostalCode>"+
                    "<RegionCode>0077</RegionCode>"+
                    "<Area></Area>"+
                    "<CityType>301</CityType>"+
                    "<City>"+city+"</City>"+
                    "<SettlementType>495</SettlementType>"+
                    "<Settlement>"+city+"</Settlement>"+
                    "<StreetType>518</StreetType>"+
                    "<Street>"+street+"</Street>"+
                    "<HouseNum>1</HouseNum>"+
                    "<HouseExt>1</HouseExt>"+
                    "<Unit></Unit>"+
                    "<UnitNum>23</UnitNum>"+
	            "</Address>"+    
		          "</AddressList>"+
		        "</Contact>"+
		        "<Citizenship>RUSSIA</Citizenship>"+
				"<IdentityCard>"+
					"<IdType>21</IdType>"+
		            "<IdSeries>" + serial + "</IdSeries>"+
		            "<IdNum>" + id + "</IdNum>"+
		            "<IssuedBy>"+ufms+"</IssuedBy>"+
		            "<IssuedCode>770-079</IssuedCode>"+
		            "<IssueDt>"+dateId+"</IssueDt>"+
		            "<PrevIDInfoFlag>false</PrevIDInfoFlag>"+
		         "</IdentityCard>"+
		        "<ExtPassportExFlag>true</ExtPassportExFlag>"+
		      "</PersonInfo>"+
		      "<MaritalCondition>"+
		        "<Status>0</Status>"+
		        "<MarriageContractFlag>false</MarriageContractFlag>"+
		        "<ChildrenFlag>false</ChildrenFlag>"+
		      "</MaritalCondition>"+
		      "<RelativeList>"+
			     "<Relative>"+
		            "<Type>1</Type>"+
		            "<PersonName>"+
		              "<LastName>"+mother+"</LastName>"+
		             "<FirstName>"+mother+"</FirstName>"+
		             "<MiddleName>"+mother+"</MiddleName>"+
		            "</PersonName>"+
		            "<Birthday>1968-05-04</Birthday>"+
		           "<DependentFlag>false</DependentFlag>"+
		          "<SBCreditFlag>0</SBCreditFlag>"+
		         "<SBEmployeeFlag>false</SBEmployeeFlag>"+
		        "</Relative>"+
	        "</RelativeList>"+
		      "<EmploymentHistory>"+
		        "<Status>2</Status>"+
		        "<OrgInfo>"+
		          "<OrgCode>001</OrgCode>"+
		        "</OrgInfo>"+
		        "<OrgInfoExt>"+
		            "<FullName>"+sberFull+"</FullName>"+
		           "<TaxId>2448352136</TaxId>"+
		          "<IndustSector>"+
		            "<Code>1</Code>"+
		          "</IndustSector>"+
		          "<NumEmployeesCode>5</NumEmployeesCode>"+
		        "</OrgInfoExt>"+
		       "<SBEmployeeFlag>"+sbflag+"</SBEmployeeFlag>"+
			    "<SBEmployee>"+
			      "<RegionId>99</RegionId>"+
			      "<FullName>"+dep+"</FullName>"+
			      "<JobType>1</JobType>"+
			    "</SBEmployee>"+
			    "<EmployeeInfo>"+
			      "<JobType>1</JobType>"+
			      "<JobTitle>"+pos+"</JobTitle>"+
			      "<ExperienceCode>5</ExperienceCode>"+
			      "<WorkPlacesNum>0</WorkPlacesNum>"+
			    "</EmployeeInfo>"+
		      "</EmploymentHistory>"+
		      "<Income>"+
		        "<BasicIncome6M>"+incoming+"</BasicIncome6M>"+
		        "<AddIncome6M>0.0</AddIncome6M>"+
		        "<FamilyIncome6M>"+incomingFamily+"</FamilyIncome6M>"+
		        "<Expenses6M>0.0</Expenses6M>"+
		      "</Income>"+
			    "<RealEstateFlag>"+flat+"</RealEstateFlag>"+
			    "<RealEstateList>" +
			    	"<RealEstate>"+
	                    "<Type>2</Type>" +
	                    "<Address>"+street+"</Address>" +
	                    "<PurchaseYear>2005</PurchaseYear>" +
	                    "<Area>100.00</Area>" +
	                    "<Units>1</Units>" +
	                    "<Cost>100000.00</Cost>" +
            		"</RealEstate>" +
			    "</RealEstateList>" +
			    "<VehicleFlag>"+vehicle+"</VehicleFlag>"+
	            "<VehicleList>"+
	                "<Vehicle>"+
	                    "<Type>1</Type>"+
	                    "<RegNumber>"+autoNum+"</RegNumber>"+
	                    "<PurchaseYear>2013</PurchaseYear>"+
	                    "<BrandName>Audi</BrandName>"+
	                    "<AgeInYears>1</AgeInYears>"+
	                    "<Cost>50000.00</Cost>"+
	                "</Vehicle>"+
           		"</VehicleList>"+
		      "<LoanFlag>false</LoanFlag>"+
           	  "<AddData>"+
		        "<LoanIssue>"+
		          "<Type>3</Type>"+
		          "<AcctId></AcctId>"+
			      "<CardNum>"+cardNum+"</CardNum>"+
		        "</LoanIssue>"+
			       "<InsuranceFlag>false</InsuranceFlag>"+
			       "<CBReqApprovalFlag>true</CBReqApprovalFlag>"+
			       "<CBSendApprovalFlag>true</CBSendApprovalFlag>"+
			       "<CBCode>CBCode0</CBCode>"+
			       "<PFRReqApprovalFlag>true</PFRReqApprovalFlag>"+
			       "<InsuranseNumber>123-123-123 12</InsuranseNumber>"+
			      "<CCardGetApprovalFlag>false</CCardGetApprovalFlag>"+
			      "<SpecialAttentionFlag>false</SpecialAttentionFlag>"+
			       "<SBSalaryCardFlag>true</SBSalaryCardFlag>"+
	                "<SalaryCardList>"+
                    "<SalaryCard>"+
                        "<Type>0</Type>"+
                        "<CardNum>"+cardNum+"</CardNum>"+
                    "</SalaryCard>"+
                "</SalaryCardList>"+
		        "<SBSalaryDepFlag>false</SBSalaryDepFlag>"+
		        "<SBShareHolderFlag>false</SBShareHolderFlag>"+
		        "<PersonVerify>" +
			        "<VerifyQuestionInfo>" +
			        	"<VerifyQuestionNumber>1</VerifyQuestionNumber>"+
			        	"<VerifyQuestionText>test</VerifyQuestionText>"+
			        	"<VerifyQuestionAnswer>test</VerifyQuestionAnswer>"+
			        "</VerifyQuestionInfo>"+
		        "</PersonVerify>"+
		        "<SigningDate>"+dateId+"</SigningDate>"+
		      "</AddData>"+
		    "</Applicant>"+
		  "</Application>"+
		"</ChargeLoanApplicationRq>";
		
	}
	
	public String getRq() {
		
		return this.response;
	}
	

}
