package team.hmhlyh.web.sms;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import team.hmhlyh.service.ShortMessageService;
import team.hmhlyh.service.impl.ShortMessageServiceImpl;

public class SendMessageJob implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ShortMessageService sms = new ShortMessageServiceImpl();
		//测试
		System.out.println("-----------执行到了Job------------");
		
		List<Map<String, Object>> list = sms.forSendSM();
		String telephone = null;
		String name = null;
		String startTime = null;
		String courseName = null;
		int location = 0;
		for (int i = 0; i < list.size(); i++) {
			//测试
			System.out.println("---------执行了循环----------");
			
			Map<String, Object> map = list.get(i);
			telephone = (String) map.get("telephone");
			name = (String) map.get("name");
			startTime = map.get("startTime").toString();
			courseName = (String) map.get("courseName");
			location = (Integer)map.get("location");
			//测试
			System.out.println("telephone:"+telephone+"name:"+name+"courseName"+courseName+"time:"+startTime+"location"+location);
			//通过多线程控制，每次循环开启一个线程去进行发送短信任务，线程一开始就进行下一次循环，而不是一直等一次短信发送完再下一次循环
			new Thread(new SendMessageThread(telephone, name, startTime, courseName, location)).start();
		}
		//测试
		System.out.println("--------循环后代码-------");
		
	}

}

class SendMessageThread implements Runnable {

	private String telephone;
	private String name;
	private String time;
	private String courseName;
	private int location;
	
	public SendMessageThread(String telephone, String name, String time, String courseName, int location) {
		this.telephone = telephone;
		this.name = name;
		this.time = time; 
		this.courseName = courseName;
		this.location = location;
	}
	
	@Override
	public void run() {
		try {
		 //发短信
        SendSmsResponse response = SendMessageDriver.sendSms(telephone, name, time, courseName,location);
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());

        try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //查明细
        if(response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = SendMessageDriver.querySendDetails(response.getBizId(),telephone);
            System.out.println("短信明细查询接口返回数据----------------");
            System.out.println("Code=" + querySendDetailsResponse.getCode());
            System.out.println("Message=" + querySendDetailsResponse.getMessage());
            int i = 0;
            for(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs())
            {
                System.out.println("SmsSendDetailDTO["+i+"]:");
                System.out.println("Content=" + smsSendDetailDTO.getContent());
                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
            }
            System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
            System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
        }
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}
	
}
