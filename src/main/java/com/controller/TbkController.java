package com.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgItemCouponGetRequest;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.response.TbkDgItemCouponGetResponse;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import com.taobao.api.response.TbkTpwdCreateResponse;
import com.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* @author liuyg
* @version 创建时间：2018年12月11日 下午6:28:07
* @ClassName 类名称：
* @Description 类描述：
*/
/**
 * @author liuyg
 *
 */
@Controller
public class TbkController {
	
	@Value("${wyUrl}")
	private String wyUrl;
	@Value("${wyVekey}")
	private String wyVekey;
	@Value("${tbUrl}")
	private String tbUrl;

	public void test(String[] args) throws Exception {
		/*String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "25358870";
		String secret = "81ee50c9881f5737f36eb5c35521234a";
		String result =  test4(url, appkey, secret);
		JSONObject json = JSONObject.parseObject(result);
		String result_list = JSONObject.parseObject(json.get("tbk_dg_item_coupon_get_response").toString()).get("results").toString();
		JSONObject json2 = JSONObject.parseObject(result_list);
		JSONArray jsonli = JSONArray.parseArray(json2.getString("tbk_coupon").toString());
		for (Object object : jsonli) {
			if( object.toString().indexOf("555709593338") > -1 ) {
				JSONObject sp = JSONObject.parseObject(object.toString());
				System.out.println(getTkl(url, appkey, secret, sp.get("coupon_click_url").toString()));
			}
		}*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long afterDate = new Date().getTime() - 1200000;
		String start_time = sdf.format(afterDate);
		while(true) {
			Robot  r   =   new   Robot(); 
			 r.delay(   10000   );  
		}
	}

	/**
	* @Description:   通用物料搜索API（导购）
	* @Param: [url, appkey, secret, name, adzoneId]
	* @return: java.lang.String
	* @Author: Liuyg
	* @Date: 2018/12/27
	*/
	public String CouponsApiTwo(String appkey,String secret,String name,Long pid) throws ApiException  {
		TaobaoClient client = new DefaultTaobaoClient(tbUrl, appkey, secret);
		TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
		req.setQ(name);
		req.setAdzoneId(pid);
		req.setMaterialId(6707L);
		req.setPlatform(2L);
		TbkDgMaterialOptionalResponse rsp = client.execute(req);
		return rsp.getBody();
	}

	/**
	* @Description:  好券清单API【导购】
	* @Param: [url, appkey, secret, name, adzoneId]
	* @return: java.lang.String
	* @Author: Liuyg
	* @Date: 2018/12/27
	*/
	public String CouponsApiOne(String appkey,String secret,String name,Long pid) throws ApiException  {
		TaobaoClient client = new DefaultTaobaoClient(tbUrl, appkey, secret);
		TbkDgItemCouponGetRequest req = new TbkDgItemCouponGetRequest();
		req.setAdzoneId(pid);
		req.setQ(name);
		req.setPlatform(2L);
		TbkDgItemCouponGetResponse rsp = client.execute(req);
		return rsp.getBody();
	}
	/**
	* @Description:  获取淘口令
	* @Param: [url, appkey, secret, spurl]
	* @return: java.lang.String
	* @Author: Liuyg
	* @Date: 2018/12/27
	*/
	public String getTkl(String appkey,String secret,String spurl,String pict_url,String shop_title) throws ApiException {
		TaobaoClient client = new DefaultTaobaoClient(tbUrl, appkey, secret);
		TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
		req.setText(shop_title+"88888");
		req.setUrl(spurl);
		req.setExt("{}");
		req.setLogo(pict_url);
		TbkTpwdCreateResponse rsp = client.execute(req);
		String result = rsp.getBody();
		JSONObject json = JSONObject.parseObject(result);
		json = JSONObject.parseObject(JSONObject.parseObject(json.get("tbk_tpwd_create_response").toString()).get("data").toString());
		return json.get("model").toString();
	}
	/** 
	* @Description: 获取订单信息
	* @Param: [start_time] 
	* @return: java.lang.String 
	* @Author: Liuyg
	* @Date: 2018/12/27 
	*/ 
	public String getDd(String start_time) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("vekey", wyVekey);
		map.put("start_time", start_time);
		String result = HttpUtil.get(wyUrl, map);
		return result;
	}

	public Map<String,String> tklToTBUrl(String url) {
		Map<String,String> map = new HashMap<>();
		String result = null;
		String title = null;
		String id = null;
		String ecurl = null;
		try {
			ecurl = result = HttpUtil.get(url,null);
			title = result.substring(result.indexOf("\"title\":\""));
			title = title.replace("\"title\":\"","");
			title = title.substring(0,title.indexOf("\""));
			map.put("title",title);
			result = result.substring(result.indexOf("var url = '"));
			result = result.substring(0,result.indexOf("';"));
			try {
				id = result.substring(result.indexOf("&id="));
				id = id.replace("&id=","");
				id = id.substring(0,id.indexOf("&"));
				map.put("id",id);
			}catch (Exception e){
				result = ecurl ;
				result = HttpUtil.get(result.substring(result.indexOf("var url = '")+"var url = '".length(),result.indexOf("';    \t//短地址有问题时跳转的地址")),null);
				try {
					result = HttpUtil.get(url,null);
					title = result.substring(result.indexOf("\"title\":\""));
					title = title.replace("\"title\":\"","");
					title = title.substring(0,title.indexOf("\""));
					map.put("title",title);
					result = result.substring(result.indexOf("var url = '"));
					result = result.substring(0,result.indexOf("';"));
					id = result.substring(result.indexOf("https://a.m.taobao.com/i"));
					id = id.replace("https://a.m.taobao.com/i","");
					id = id.substring(0,id.indexOf(".htm"));
					map.put("id",id);
				}catch (Exception es){
					System.out.println(es);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	* @Description:  根据淘口令获取优惠卷并转成淘口令
	* @Param: [tkl,appkey,secret]
	* @return: java.lang.String
	* @Author: Liuyg
	* @Date: 2018/12/27
	*/
	public Map<String,String>  TklToTkl(String tkl,String appkey,String secret,Long tbpid,String pid,String tbsession){
		String url = null;
		String result = null;
		url = tkl.substring(tkl.indexOf("http"));
		url = url.substring(0,url.indexOf(" "));
		Map<String,String> map = tklToTBUrl(url);
		try {
			result = CouponsApiTwo(appkey,secret,map.get("title"),tbpid);
		} catch (ApiException e) {
			try {
				result = CouponsApiOne(appkey,secret,map.get("title"),tbpid);
				result = CouponsApiOne(appkey,secret,map.get("title"),tbpid);
			} catch (ApiException e1) {
				return null;
			}
		}
		JSONObject json = JSONObject.parseObject(result);
		String result_list = JSONObject.parseObject(json.get("tbk_dg_material_optional_response").toString()).get("result_list").toString();
		JSONObject json2 = JSONObject.parseObject(result_list);
		JSONArray jsonli = JSONArray.parseArray(json2.getString("map_data"));
		for (Object object : jsonli) {
			if( object.toString().indexOf(map.get("id")) > -1 ) {
				JSONObject sp = JSONObject.parseObject(object.toString());
				try {
					String sps = null;
					String pict_url = sp.get("pict_url").toString();//商品主图
					map.put("picurl",pict_url);
					String shop_title = sp.get("shop_title").toString();//店铺名称
					//commission_rate佣金比率
					//coupon_info优惠券面额
					//zk_final_price商品折扣价格
					map.put("commission_rate",sp.get("commission_rate").toString());
					map.put("zk_final_price",sp.get("zk_final_price").toString());
					map.put("coupon_info",sp.get("coupon_info").toString());
					//title	商品标题
					if( Integer.valueOf(sp.get("coupon_remain_count").toString()) > 0 ){
						//sp.get("coupon_share_url").toString() //券二合一页面链接
						sps = "https://uland.taobao.com/coupon/edetail?activityId="+sp.get("coupon_id").toString()+"&itemId="+map.get("id")+"&pid="+pid;
						Map<String,String> gysendjson = new HashMap<>();
						gysendjson.put("adzone_id",pid.substring(pid.lastIndexOf("_")+1));
						gysendjson.put("site_id",pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).substring(0,pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).indexOf("_")));
						gysendjson.put("session",tbsession);
						gysendjson.put("item_id",map.get("id"));
						String par = "";
						for (String key:gysendjson.keySet()) {
							par += "&"+key+"="+gysendjson.get(key);
						}
						String gysp = "";
						try {
							gysp = HttpUtil.post("http://gateway.kouss.com/tbpub/privilegeGet",par,null);
							if(!StringUtils.isEmpty(gysp)){
								JSONObject gyjsonsp = JSONObject.parseObject(gysp);
								System.out.println(gyjsonsp.toString());
								// System.out.println(JSONObject.parseObject(JSONObject.parseObject(json.getString("result")).getString("data")).getString("coupon_click_url"));
								sps = JSONObject.parseObject(JSONObject.parseObject(gyjsonsp.getString("result")).getString("data")).getString("coupon_click_url")+"activityId="+sp.get("coupon_id").toString();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						// JSONObject json = JSONObject.parseObject(result);
						// System.out.println(JSONObject.parseObject(JSONObject.parseObject(json.getString("result")).getString("data")).getString("coupon_click_url"));
//						System.out.println(sp.get("coupon_share_url").toString());
//						sps = sp.get("url").toString() + sp.get("coupon_id").toString();
					}else{
						sps = "https://uland.taobao.com/coupon/edetail?itemId="+map.get("id")+"&pid="+pid;
						Map<String,String> gysendjson = new HashMap<>();
						gysendjson.put("adzone_id",pid.substring(pid.lastIndexOf("_")+1));
						gysendjson.put("site_id",pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).substring(0,pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).indexOf("_")));
						gysendjson.put("session",tbsession);
						gysendjson.put("item_id",map.get("id"));
						String gysp = "";
						String par = "";
						for (String key:gysendjson.keySet()) {
							par += "&"+key+"="+gysendjson.get(key);
						}
						try {
							gysp = HttpUtil.post("http://gateway.kouss.com/tbpub/privilegeGet",par,null);
							if(!StringUtils.isEmpty(gysp)){
								JSONObject gyjsonsp = JSONObject.parseObject(gysp);
								System.out.println(gyjsonsp.toString());
								// System.out.println(JSONObject.parseObject(JSONObject.parseObject(json.getString("result")).getString("data")).getString("coupon_click_url"));
								sps = JSONObject.parseObject(JSONObject.parseObject(gyjsonsp.getString("result")).getString("data")).getString("coupon_click_url");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					map.put("ulandurl",sps);
					String ulandtkl =  getTkl( appkey, secret, sps,pict_url,shop_title);
					map.put("pict_url",pict_url);
					map.put("ulandtkl",ulandtkl);
					return map;
				} catch (ApiException e) {
					return null;
				}
			}
		}
		return null;

	}
	public static void CouponsApiDtk(String id){
		String url = "http://api.dataoke.com/index.php?r=port/index&appkey=b215aab96a&v=xxx&id=xxx";
		//HttpUtil.get()
	}
	public static void main(String[] args) {
		// String tkl = "【chic羊羔绒外套冬2018新款短款宽松,这款短款的外套，很显瘦高挑哦！】，https://m.tb.cn/h.3qoZWjW?sm=13f6a8 点击链接，再选择浏览器咑閞；或復·制这段描述￥WsFTbLi3axR￥后咑閞淘♂寳♀";
		// String url = null;
		// String result = null;
		// url = tkl.substring(tkl.indexOf("http"));
		// url = url.substring(0,url.indexOf(" "));
		// System.out.println(tklToTBUrl(url));
		/*Date date1 = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = new Date();
		try {
			System.out.println(date1.getTime()-sdf.parse(sdf.format(date2)).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		/*String pid = "mm_123_456_789";
		JSONObject gysendjson = new JSONObject();
		gysendjson.put("adzone_id",pid.substring(pid.lastIndexOf("_")+1));
		gysendjson.put("site_id",pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).substring(0,pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).indexOf("_")));
		System.out.println(gysendjson.toString());*/
		// JSONObject json = JSONObject.parseObject(result);
		// System.out.println(JSONObject.parseObject(JSONObject.parseObject(json.getString("result")).getString("data")).getString("coupon_click_url"));
		String pid = "mm_55094238_214850042_73283650135";
		String sps = "";
		Map<String,String> gysendjson = new HashMap<>();
		gysendjson.put("adzone_id",pid.substring(pid.lastIndexOf("_")+1));
		gysendjson.put("site_id",pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).substring(0,pid.substring(pid.indexOf("_")+1).substring(pid.substring(pid.indexOf("_")+1).indexOf("_")+1).indexOf("_")));
		gysendjson.put("session","70000100c4144973c1cbd9eb05e8a2984207befc4a4e5f8d971ca17e42a1fbe1fe4b3d31756431517");
		gysendjson.put("item_id","580544246233");
		String par = "";
		for (String key:gysendjson.keySet()) {
			par += "&"+key+"="+gysendjson.get(key);
		}
		System.out.println(par);
		String gysp = "";
		try {
			gysp = HttpUtil.post("http://gateway.kouss.com/tbpub/privilegeGet",par,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!StringUtils.isEmpty(gysp)){
			JSONObject gyjsonsp = JSONObject.parseObject(gysp);
			System.out.println(gyjsonsp.toString());
			// System.out.println(JSONObject.parseObject(JSONObject.parseObject(json.getString("result")).getString("data")).getString("coupon_click_url"));
			sps = JSONObject.parseObject(JSONObject.parseObject(gyjsonsp.getString("result")).getString("data")).getString("coupon_click_url");
		}
		System.out.println(sps);
	}
}
